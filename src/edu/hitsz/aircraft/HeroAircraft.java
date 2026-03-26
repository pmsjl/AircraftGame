package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.strategy.NormalShootStrategy;

import java.util.List;

/**
 * 英雄飞机，游戏玩家操控 (单例模式)
 *
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {


    private static volatile HeroAircraft instance;


    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 父类已有对应值无需重新定义直接赋值即可
        this.shootNum = 3;
        this.direction = -1;
        this.power = 10;
        this.setShootStrategy(new NormalShootStrategy());
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
                            1000
                    );
                }
            }
        }
        return instance;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */
    public List<BaseBullet> shoot() {
        return shootStrategy.Shoot(this,10);
    }

    @Override
    public void updateOnUnfreeze() {

    }


    @Override
    public int updateOnBomb() {
        return 0;
    }

    @Override
    public boolean updateOnFreeze() {
        return false;
    }
}