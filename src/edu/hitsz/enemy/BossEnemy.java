package edu.hitsz.enemy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.factory.PropFactory;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.NormalShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * 王牌敌机
 */

public class BossEnemy extends AbstractAircraft
{

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {

        super(locationX, locationY, speedX, speedY, hp);
        this.score = 200;
        this.shootNum = 2;
        this.power = 30;
        this.direction = 1;
        this.shootStrategy = new NormalShootStrategy();
    }

    @Override
    public BaseBullet createBullet(int x, int y, int speedX, int speedY, int power) {
        return new EnemyBullet(x,y,speedX,speedY,power);
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
    public List<AbstractProp> dropProps() {

        List<AbstractProp> res = new LinkedList<>();
        double num = Math.random();
        if (num < 0.2) {
            // 用工厂造一个加血道具，位置就在当前精英机爆炸的地方
            AbstractProp blood = PropFactory.createProp("Blood", this.getLocationX(), this.getLocationY());
            if (blood != null) {
                res.add(blood);
                System.out.println("boss机掉落了加血道具！");
            }
        } else if (num > 0.8) {
            AbstractProp bullet = PropFactory.createProp("SuperFire", this.getLocationX(), this.getLocationY());
            if (bullet != null) {
                res.add(bullet);
                System.out.println("boss机掉落了超级弹药道具！");
            }
        }
        return res;
    }

    @Override
    public void updateOnUnfreeze() {

    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.Shoot(this,20);
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
