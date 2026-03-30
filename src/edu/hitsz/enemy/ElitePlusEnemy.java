package edu.hitsz.enemy;


import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.factory.PropFactory;
import edu.hitsz.observer.BombObserver;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.NormalShootStrategy;
import edu.hitsz.strategy.ShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * 精锐敌机
 */
public class ElitePlusEnemy extends AbstractAircraft implements BombObserver {
    public ElitePlusEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.score = 25;
        this.shootNum = 1;
        this.power = 20;
        this.direction = 2;
        this.shootStrategy = new NormalShootStrategy();
        this.originalSpeedX = speedX;
        this.originalSpeedY = speedY;

    }

    @Override
    public BaseBullet createBullet(int x, int y, int speedX, int speedY, int power) {
        return new EnemyBullet(x, y, speedX, speedY, power);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.Shoot(this, 8);
    }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> res = new LinkedList<>();
        double num = Math.random();
        if (num < 0.1) {
            // 用工厂造一个加血道具，位置就在当前精英机爆炸的地方
            AbstractProp blood = PropFactory.createProp("Blood", this.getLocationX(), this.getLocationY());
            if (blood != null) {
                res.add(blood);
                System.out.println("精锐机掉落了加血道具！");
            }
        } else if (num > 0.9) {
            AbstractProp bulletplus = PropFactory.createProp("SuperFire", this.getLocationX(), this.getLocationY());
            if (bulletplus != null) {
                res.add(bulletplus);
                System.out.println("精锐机掉落了超级弹药道具！");
            }
        }
        return res;
    }

    @Override
    public int updateOnBomb() {
        this.vanish();
        return this.score;
    }

    @Override
    public void updateOnUnfreeze() {
        this.speedX = this.originalSpeedX;
        this.speedY = this.originalSpeedY;

    }

    @Override
    public boolean updateOnFreeze() {
        this.speedY = 0;
        this.speedX = 0;
        return true;
    }
}
