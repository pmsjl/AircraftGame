package edu.hitsz.enemy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.factory.PropFactory;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.NormalShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * 精英敌机
 */

public class EliteEnemy extends AbstractAircraft {

    // 每次射击发射子弹数量
    private int shootNum = 1;
    // 子弹威力
    private int power = 15;
    // 子弹射击方向 (向上发射：-1，向下发射：1)
    private int direction = 2;

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.score = 20;
        this.shootNum = 1;
        this.power = 15;
        this.direction = 1;
        this.shootStrategy = new NormalShootStrategy();
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
    public BaseBullet createBullet(int x, int y, int speedX, int speedY, int power) {
        return new EnemyBullet(x, y, speedX, speedY, power);
    }

    @Override
    public List<BaseBullet> shoot() {

        return shootStrategy.Shoot(this, 10);
    }


    @Override
    public List<AbstractProp> dropProps() {

        List<AbstractProp> res = new LinkedList<>();
        double num = Math.random();
        if (num < 0.3) {
            // 用工厂造一个加血道具，位置就在当前精英机爆炸的地方
            AbstractProp blood = PropFactory.createProp("Blood", this.getLocationX(), this.getLocationY());
            if (blood != null) {
                res.add(blood);
                System.out.println("精英机掉落了加血道具！");
            }
        } else if (num > 0.6) {
            AbstractProp bullet = PropFactory.createProp("Fire", this.getLocationX(), this.getLocationY());
            if (bullet != null) {
                res.add(bullet);
                System.out.println("精英机掉落了弹药道具！");
            }
        } else {
            AbstractProp bulletplus = PropFactory.createProp("SuperFire", this.getLocationX(), this.getLocationY());
            if (bulletplus != null) {
                res.add(bulletplus);
                System.out.println("精英机掉落了超级弹药道具！");
            }
        }
        return res;
    }
}
