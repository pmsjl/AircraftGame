package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.dao.RecordDao;
import edu.hitsz.enemy.BossEnemy;
import edu.hitsz.enemy.MobEnemy;
import edu.hitsz.factory.*;
import edu.hitsz.observer.Explosive;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.record.PlayerRecord;
import edu.hitsz.utils.MusicThread;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.Objects;

/**
 * 游戏主面板（模板方法的骨架类）。
 *
 * <p>本类是 lab6 模板方法重构后的抽象基类：{@link #action()} 是 final 模板方法，
 * 锁定主循环步骤；不同难度通过覆盖 protected hook 决定细节：
 * <ul>
 *   <li>{@link #shouldSpawnBoss()}：是否触发 Boss（简单难度恒 false）</li>
 *   <li>{@link #onBossSpawn(BossEnemy)}：Boss 出场加成（困难按次累乘 hp）</li>
 *   <li>{@link #onProgressTick()}：难度递进周期到达时的处理</li>
 * </ul>
 *
 * @author hitsz
 */
public abstract class Game extends JPanel {

    private int backGroundTop = 0;

    // 调度器, 用于定时任务调度
    private final Timer timer;
    // 时间间隔(ms)，控制刷新频率
    private final int timeInterval = 40;

    private final HeroAircraft heroAircraft;
    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;

    // 屏幕中出现的敌机最大数量
    private final int enemyMaxNumber = 10;

    // 敌机生成周期 / 射击周期（子类可在构造或 onProgressTick 中调整）
    protected double enemySpawnCycle;
    private int enemySpawnCounter = 0;

    protected double heroShootCycle = 3;
    protected double enemyShootCycle;
    private int heroShootCounter = 0;
    private int enemyShootCounter = 0;

    // 当前玩家分数
    private int score = 0;

    // 【lab6】按对象计时的冰冻状态：value 为剩余帧数（>0 才在 map 里）
    private final Map<AbstractAircraft, Integer> enemyFreezeTicks = new IdentityHashMap<>();
    private final Map<BaseBullet, Integer> bulletFreezeTicks = new IdentityHashMap<>();

    // 难度递进计时器
    private int progressCounter = 0;
    private int progressLevel = 0;

    // 新生敌机的难度系数，由 onProgressTick 累乘修改；只影响新出生的敌机
    protected double enemyHpMultiplier = 1.0;
    protected double enemySpeedMultiplier = 1.0;

    // 游戏结束标志
    private boolean gameOverFlag = false;

    // 当前屏幕上所有道具
    private final List<AbstractProp> props;
    private final List<AbstractProp> bombDroppedProps;

    // --- 工厂实例 ---
    private final EnemyFactory mobEnemyFactory = new MobEnemyFactory();
    private final EnemyFactory eliteEnemyFactory = new EliteEnemyFactory();
    private final EnemyFactory elitePlusEnemyFactory = new ElitePlusEnemyFactory();
    private final EnemyFactory eliteProEnemyFactory = new EliteProEnemyFactory();
    private final EnemyFactory bossEnemyFactory = new BossEnemyFactory();
    // 三种特殊敌机工厂
    private final EnemyFactory shieldEnemyFactory   = new ShieldEnemyFactory();
    private final EnemyFactory dodgeEnemyFactory    = new DodgeEnemyFactory();
    private final EnemyFactory kamikazeEnemyFactory = new KamikazeEnemyFactory();

    // boss机出现的处理方式
    // 下一次产生 Boss 的分数阈值（比如 500 分出第一个，再过 3000 分出下一个）
    private int bossThreshold = 500;

    private boolean bossActive = false;

    private final Difficulty difficulty;
    private final RecordDao recordDao;

    protected Game(Difficulty difficulty, RecordDao recordDao) {
        this.difficulty = difficulty;
        this.recordDao = Objects.requireNonNull(recordDao, "recordDao");

        // 根据难度决定初始周期：难度越高，周期越短，敌机出得越快
        this.enemySpawnCycle = 12 / difficulty.getScoreMultiplier();
        this.enemyShootCycle = 18 / difficulty.getScoreMultiplier();

        heroAircraft = HeroAircraft.getInstance();
        heroAircraft.resetForNewGame();
        enemyAircrafts = new ArrayList<>();
        heroBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        props = new ArrayList<>();
        bombDroppedProps = new ArrayList<>();

        // 启动英雄机鼠标监听
        new HeroController(this, heroAircraft);

        this.timer = new Timer("game-action-timer", true);
        // 背景音乐循环播放
        new MusicThread("src/videos/bgm.wav", true).start();
    }

    /**
     * 游戏启动入口（模板方法）。
     * <p>主循环步骤被 final 锁定；难度差异通过 protected hook 表达。
     */
    public final void action() {

        // 定时任务：绘制、对象产生、碰撞判定、及结束判定
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                enemySpawnCounter++;
                if (enemySpawnCounter >= enemySpawnCycle) {
                    enemySpawnCounter = 0;
                    // 依概率产生各种敌机
                    generateEnemy();
                }

                // 飞机发射子弹
                shootAction();
                // 子弹移动
                bulletsMoveAction();
                // 飞机移动
                aircraftsMoveAction();
                // 道具移动
                propsMoveAction();
                // 撞击检测
                crashCheckAction();
                // 道具时效判断（含按对象冰冻倒计时）
                updatePropAction();
                // 难度递进
                progressTickAction();
                // 后处理
                postProcessAction();
                // 重绘界面
                repaint();
                // 游戏结束检查
                checkResultAction();
            }
        };
        // 以固定延迟时间进行执行：本次任务执行完成后，延迟 timeInterval 再执行下一次
        timer.schedule(task, 0, timeInterval);

    }

    /**
     * 道具时效：按对象推进冰冻倒计时；到 0 调 unfreeze 并移出 map。
     */
    private void updatePropAction() {
        Iterator<Map.Entry<AbstractAircraft, Integer>> ei = enemyFreezeTicks.entrySet().iterator();
        while (ei.hasNext()) {
            Map.Entry<AbstractAircraft, Integer> e = ei.next();
            int remain = e.getValue() - 1;
            if (remain <= 0) {
                e.getKey().updateOnUnfreeze();
                ei.remove();
            } else {
                e.setValue(remain);
            }
        }
        Iterator<Map.Entry<BaseBullet, Integer>> bi = bulletFreezeTicks.entrySet().iterator();
        while (bi.hasNext()) {
            Map.Entry<BaseBullet, Integer> e = bi.next();
            int remain = e.getValue() - 1;
            if (remain <= 0) {
                e.getKey().updateOnUnfreeze();
                bi.remove();
            } else {
                e.setValue(remain);
            }
        }
    }

    /**
     * 主循环每帧推进难度计时器，到达间隔后调 hook。
     */
    private void progressTickAction() {
        int interval = progressIntervalTicks();
        if (interval <= 0) {
            return;
        }
        progressCounter++;
        if (progressCounter >= interval) {
            progressCounter = 0;
            progressLevel++;
            onProgressTick();
        }
    }

    private void generateEnemy() {
        if (enemyAircrafts.size() < enemyMaxNumber) {
            double rand = Math.random();
            AbstractAircraft newEnemy = null;

            // 概率分布（共 100%）：
            // [0.00, 0.20)  EliteEnemy      20%
            // [0.20, 0.30)  ShieldEnemy     10%
            // [0.30, 0.40)  DodgeEnemy      10%
            // [0.40, 0.50)  KamikazeEnemy   10%
            // [0.50, 0.80)  EliteProEnemy   30%
            // [0.80, 1.00)  ElitePlusEnemy  20%
            if (rand < 0.20) {
                newEnemy = eliteEnemyFactory.createEnemy();
            } else if (rand < 0.30) {
                newEnemy = shieldEnemyFactory.createEnemy();
            } else if (rand < 0.40) {
                newEnemy = dodgeEnemyFactory.createEnemy();
            } else if (rand < 0.50) {
                newEnemy = kamikazeEnemyFactory.createEnemy();
            } else if (rand < 0.80) {
                newEnemy = eliteProEnemyFactory.createEnemy();
            } else {
                newEnemy = elitePlusEnemyFactory.createEnemy();
            }
            // 应用难度递进系数（仅作用于新生敌机，不影响已在场的）
            if (newEnemy != null) {
                if (enemyHpMultiplier != 1.0) {
                    newEnemy.scaleHp(enemyHpMultiplier);
                }
                if (enemySpeedMultiplier != 1.0) {
                    newEnemy.scaleSpeedY(enemySpeedMultiplier);
                }
                enemyAircrafts.add(newEnemy);
            }
        }
    }

    // ***********************
    // Action 各部分
    // ***********************

    private void shootAction() {
        heroShootCounter++;
        enemyShootCounter++;
        if (heroShootCounter >= heroShootCycle) {
            heroShootCounter = 0;
            // 英雄机射击
            heroBullets.addAll(heroAircraft.shoot());

        }
        if (enemyShootCounter >= enemyShootCycle) {
            enemyShootCounter = 0;
            // 敌机射击
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                List<BaseBullet> enemyBullet = enemyAircraft.shoot();
                enemyBullets.addAll(enemyBullet);

            }
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void propsMoveAction() {
        for (AbstractProp prop : props) {
            prop.forward();
        }

    }

    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // 敌机子弹攻击英雄机
        for (BaseBullet enemyBullet : enemyBullets) {
            if (enemyBullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(enemyBullet)) {
                // 英雄机中子弹
                // 英雄机损失一定生命值
                heroAircraft.decreaseHp(enemyBullet.getPower());
                new MusicThread("src/videos/bullet_hit.wav", false).start();
                enemyBullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    new MusicThread("src/videos/bullet_hit.wav", false).start();
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        // 获得分数，产生道具补给
                        if (enemyAircraft instanceof BossEnemy) {
                            bossActive = false;
                            System.out.println("Boss 被击毁！世界暂时和平。");
                        }
                        score += enemyAircraft.getScore();
                        props.addAll(enemyAircraft.dropProps());
                        // 自爆机引爆：向外喷射弹幕
                        if (enemyAircraft instanceof Explosive) {
                            enemyBullets.addAll(((Explosive) enemyAircraft).explode());
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    // 撞机时自爆机也要触发爆炸
                    if (enemyAircraft instanceof Explosive) {
                        enemyBullets.addAll(((Explosive) enemyAircraft).explode());
                    }
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 我方获得道具，道具生效
        for (AbstractProp prop : props) {
            if (prop.notValid()) {
                continue;
            }
            if (heroAircraft.crash(prop)) {
                int buffType = prop.effect(heroAircraft);
                if (buffType == 3) {
                    FreezeUpdate();
                } else if (buffType == 4) {
                    BombUpdate();
                }

                prop.vanish();
            }
        }
        props.addAll(bombDroppedProps);
        bombDroppedProps.clear();
        // 对于 boss 机的出现与否进行判断（hook 控制）
        if (shouldSpawnBoss() && !bossActive) {
            bossActive = true;
            BossEnemy boss = (BossEnemy) bossEnemyFactory.createEnemy();
            onBossSpawn(boss);
            enemyAircrafts.add(boss);
            new MusicThread("src/videos/bgm_boss.wav", false).start();
            System.out.println("警告！分数达到 " + bossThreshold + "，Boss 机降临！");

            // 把下一次触发 Boss 的阈值提高
            bossThreshold += 3000;
        }

    }

    /**
     * 冰冻道具触发：按每个对象自报的时长入 map（>0），免疫(0)/永久(-1) 不入。
     */
    private void FreezeUpdate() {
        for (AbstractAircraft enemy : enemyAircrafts) {
            int dur = enemy.updateOnFreeze();
            if (dur > 0) {
                enemyFreezeTicks.put(enemy, dur);
            } else if (dur < 0) {
                // 永久冻结：从 map 中移除任何残留计时（防止短期道具叠加）
                enemyFreezeTicks.remove(enemy);
            }
        }
        for (BaseBullet bullet : enemyBullets) {
            int dur = bullet.updateOnFreeze();
            if (dur > 0) {
                bulletFreezeTicks.put(bullet, dur);
            }
        }
    }

    private void BombUpdate() {
        bombDroppedProps.clear();
        new MusicThread("src/videos/bomb_explosion.wav", false).start();
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            int addScore = enemyAircraft.updateOnBomb();
            if (addScore > 0) {
                bombDroppedProps.addAll(enemyAircraft.dropProps());
            }
            score += addScore;
        }
        // lab6 要求：炸弹同时清空所有敌机子弹
        enemyBullets.clear();
        bulletFreezeTicks.clear();

    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 删除无效的道具
     * 4. 清理冰冻 map 中已无效对象的残留
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);

        // 删除无效道具
        props.removeIf(AbstractFlyingObject::notValid);

        // 同步清理冰冻 map（避免对象残留导致泄漏或 unfreeze 死亡对象）
        enemyFreezeTicks.keySet().removeIf(AbstractFlyingObject::notValid);
        bulletFreezeTicks.keySet().removeIf(AbstractFlyingObject::notValid);
    }

    /**
     * 检查游戏是否结束，若结束：关闭线程池
     */
    private void checkResultAction() {
        if (gameOverFlag) {
            return;
        }

        // 游戏结束检查英雄机是否存活
        if (heroAircraft.getHp() <= 0) {
            gameOverFlag = true;
            timer.cancel(); // 取消定时器并终止所有调度任务
            new MusicThread("src/videos/game_over.wav", false).start();
            System.out.println("Game Over!");
            String playerName = JOptionPane.showInputDialog(
                    this,
                    "游戏结束！\n你的得分为: " + score + "\n请输入玩家名字以记录成绩:",
                    "结算",
                    JOptionPane.QUESTION_MESSAGE);

            // 检查玩家是否输入了名字（如果点了取消，playerName 会是 null）
            if (playerName != null && !playerName.trim().isEmpty()) {
                // 获取当前时间的格式化字符串
                String currentTime = java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"));

                // 封装成一个实体类
                PlayerRecord newRecord = new PlayerRecord(playerName, score, this.difficulty, currentTime);

                recordDao.addRecord(newRecord);

                System.out.println("玩家记录已保存至本地文件！");
            }
            // 显现排行榜
            for (Component component : Main.cardPanel.getComponents()) {
                if (component instanceof ScoreBoard) {
                    Main.cardPanel.remove(component);
                }
            }
            ScoreBoard scoreBoard = new ScoreBoard(this.difficulty, this.recordDao);
            Main.cardPanel.add(scoreBoard, "score");
            Main.cardPanel.revalidate();
            Main.cardPanel.repaint();
            Main.cardLayout.show(Main.cardPanel, "score");
        }
    }

    // ***********************
    // 模板方法的 protected hook（默认行为，子类按难度覆盖）
    // ***********************

    /**
     * 是否触发 Boss 出场。默认按分数阈值判定；EasyGame 覆盖为永远不出。
     */
    protected boolean shouldSpawnBoss() {
        return score >= bossThreshold;
    }

    /**
     * Boss 加入战场前的额外处理。默认空；HardGame 覆盖以累乘 Boss HP。
     */
    protected void onBossSpawn(BossEnemy boss) {
        // 默认无加成
    }

    /**
     * 难度递进周期到达时调用。默认空；普通/困难覆盖以放大敌机参数。
     */
    protected void onProgressTick() {
        // 默认无递进
    }

    /**
     * 难度递进的间隔（帧）。默认 750 = 30s（25 FPS）；返回 ≤0 表示禁用递进。
     */
    protected int progressIntervalTicks() {
        return 750;
    }

    /**
     * 当前递进次数（供子类输出日志或计算累乘系数）。
     */
    protected int getProgressLevel() {
        return progressLevel;
    }

    // ***********************
    // Paint 各部分
    // ***********************

    /**
     * 重写 paint方法
     * 通过重复调用paint方法，实现游戏动画
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景,图片滚动
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);
        paintImageWithPositionRevised(g, enemyAircrafts);

        // 绘制道具
        paintImageWithPositionRevised(g, props);

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        // 绘制得分和生命值
        paintScoreAndLife(g);

    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.isEmpty()) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(Color.RED);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE: " + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE: " + this.heroAircraft.getHp(), x, y);
    }

}
