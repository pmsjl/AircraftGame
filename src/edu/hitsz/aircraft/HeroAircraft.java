package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.strategy.RingShootStrategy;
import edu.hitsz.strategy.ShootStrategy;
import edu.hitsz.strategy.StraightShootStrategy;

import java.util.List;

/**
 * 英雄飞机，游戏玩家操控 (单例模式)
 *
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    // 【新增】Buff 状态管理
    private int currentBuffPriority = 0; // 当前Buff优先级：0=无，1=普通火力，2=超级火力
    private int buffVersion = 0; // 当前Buff的版本号，用于道具叠加后的延时处理

    private static volatile HeroAircraft instance;

    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 父类已有对应值无需重新定义直接赋值即可
        this.shootNum = 3;
        this.originalShootNum = 3;
        this.direction = -3;
        this.power = 10;
        this.setShootStrategy(new StraightShootStrategy());
    }

    @Override
    public BaseBullet createBullet(int x, int y, int speedX, int speedY, int power) {
        return new HeroBullet(x, y, speedX, speedY, power);
    }

    public static HeroAircraft getInstance() {
        if (instance == null) {
            // 同步锁，确保多线程下只有一个线程能进入创建逻辑
            synchronized (HeroAircraft.class) {
                if (instance == null) {
                    // 在这里统一初始化英雄机的默认出生位置和血量
                    instance = new HeroAircraft(
                            Main.WINDOW_WIDTH / 2,
                            Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                            0,
                            0,
                            10000);
                }
            }
        }
        return instance;
    }

    /**
     * 每次开始新游戏时重置英雄机状态。
     */
    public synchronized void resetForNewGame() {
        this.hp = this.maxHp;
        this.isValid = true;
        this.currentBuffPriority = 0;
        this.buffVersion = 0;
        this.shootNum = this.getOriginalShootNum();
        this.setShootStrategy(new StraightShootStrategy());
        this.setLocation(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight());
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /**
     * 通过射击产生子弹
     * 
     * @return 射击出的子弹List
     */
    public List<BaseBullet> shoot() {
        return shootStrategy.Shoot(this, 10);
    }

    @Override
    public void updateOnUnfreeze() {

    }

    @Override
    public int updateOnBomb() {
        return 0;
    }

    @Override
    public int updateOnFreeze() {
        // 英雄机不参与冰冻
        return 0;
    }

    /**
     * 【核心逻辑】：申请使用 Buff
     * 
     * @param priority 申请的Buff优先级
     * @return 成功发放的“版本号令牌”。如果返回 -1，说明申请被拒绝（被高阶覆盖了）
     */
    public synchronized int applyBuff(int priority) {
        // 【实现：高阶覆盖】只有当新吃的道具优先级 >= 当前优先级时，才允许生效！
        if (priority >= currentBuffPriority) {
            this.currentBuffPriority = priority;
            this.buffVersion++; // 版本号 +1，颁发新令牌！
            return this.buffVersion;
        }
        // 如果身上有超级火力(2)，吃了普通火力(1)，直接拒绝！
        return -1;
    }

    /**
     * 【核心逻辑】：尝试清除 Buff
     * 
     * @param version 线程持有的“版本号令牌”
     */
    public synchronized void clearBuff(int version) {
        // 【实现：延长时间/防错乱】
        // 只有当线程手里的令牌，和飞机当前的最新版本号对得上时，才允许恢复原始状态！
        if (this.buffVersion == version) {
            this.currentBuffPriority = 0; // 优先级归零
            this.shootNum = this.getOriginalShootNum(); // 恢复基础子弹数
            this.setShootStrategy(new StraightShootStrategy());

            System.out.println("道具时间到，恢复基础状态。");
        } else {
            // 如果对不上，说明玩家在期间吃了新道具，这个老线程只能乖乖“默默死亡”，不准改子弹！
            System.out.println("老线程苏醒，但发现已有新Buff，默默退出。");
        }
    }
}