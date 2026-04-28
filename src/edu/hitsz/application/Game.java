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
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public class Game extends JPanel {

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

    // 敌机生成周期
    protected double enemySpawnCycle;
    private int enemySpawnCounter = 0;

    // 英雄机和敌机射击周期
    protected double heroShootCycle = 3;
    protected double enemyShootCycle;
    private int heroShootCounter = 0;
    private int enemyShootCounter = 0;
    // 当前玩家分数
    private int score = 0;

    // 道具生效周期

    // 冰冻生效时长控制
    protected double freezeCycle = 50;
    private boolean freezeIsActive = false;
    private int freezeCounter = 0;

    // 游戏结束标志
    private boolean gameOverFlag = false;

    // 【新增】存放当前屏幕上所有道具的列表
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
    // 下一次产生 Boss 的分数阈值（比如 500 分出第一个，1000 分出第二个）
    private int bossThreshold = 500;

    private boolean bossActive = false;

    private final Difficulty difficulty;
    private final RecordDao recordDao;

    public Game(Difficulty difficulty, RecordDao recordDao) {
        this.difficulty = difficulty;
        this.recordDao = Objects.requireNonNull(recordDao, "recordDao");

        // 【核心】根据难度，动态调整游戏参数
        // 例如：难度越高，乘数越大，周期越短，敌机出得越快！
        // 简单(1.0) = 8帧出一个; 普通(1.5) ≈ 5帧出一个; 困难(2.0) = 4帧出一个
        this.enemySpawnCycle = 12 / difficulty.getScoreMultiplier();
        this.enemyShootCycle = 18 / difficulty.getScoreMultiplier();
        // 你也可以在这里调整 bossThreshold 比如困难模式 300分就出 Boss

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
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {

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
                // 道具时效判断
                updatePropAction();
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

    // 道具是否生效的判断，只有接触到道具后才会开始计时，时间到达后就会进行归位
    private void updatePropAction() {
        if (freezeIsActive) {
            freezeCounter++;
            if (freezeCounter >= freezeCycle) {
                freezeCounter = 0;
                for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                    enemyAircraft.updateOnUnfreeze();
                }

            }
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
            // 将工厂生产好的敌机加入队列
            if (newEnemy != null) {
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
            // TODO 敌机射击
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
        // TODO 敌机子弹攻击英雄机
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
                        // TODO 获得分数，产生道具补给
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

        // Todo: 我方获得道具，道具生效
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
        // 对于boss机的出现与否进行判断
        if (score >= bossThreshold && !bossActive) {

            // 标志位置为 true，说明 Boss 降临了，锁住！
            bossActive = true;

            // 产生 Boss 机（假设你已经写好了 BossEnemyFactory）
            enemyAircrafts.add(bossEnemyFactory.createEnemy());
            new MusicThread("src/videos/bgm_boss.wav", false).start();
            System.out.println("警告！分数达到 " + bossThreshold + "，Boss 机降临！");

            // 把下一次触发 Boss 的阈值提高

            bossThreshold += 3000;
        }

    }

    private void FreezeUpdate() {

        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            if (enemyAircraft.updateOnFreeze()) {
                freezeIsActive = true;
                freezeCounter = 0;
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
        enemyBullets.clear();

    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 删除无效的道具
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);

        // Todo: 删除无效道具
        props.removeIf(AbstractFlyingObject::notValid);
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

            // 2. 检查玩家是否输入了名字（如果点了取消，playerName 会是 null）
            if (playerName != null && !playerName.trim().isEmpty()) {
                // 【DAO 接入】获取当前时间的格式化字符串
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

    ;

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

        // Todo: 绘制道具
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
