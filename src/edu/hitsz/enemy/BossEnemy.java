package edu.hitsz.enemy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.factory.PropFactory;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.RingShootStrategy;
import edu.hitsz.strategy.StraightShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * 王牌敌机
 */

public class BossEnemy extends AbstractAircraft {

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {

        super(locationX, locationY, speedX, speedY, hp);
        this.score = 200;
        this.shootNum = 20;
        this.power = 30;
        this.direction = 1;
        this.shootStrategy = new RingShootStrategy();
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
    public List<AbstractProp> dropProps() {

        List<AbstractProp> res = new LinkedList<>();
        Integer count = 3;
        do {
            count--;
            double num = Math.random();
            AbstractProp prop = null;
            String propName = "";
            if (num < 0.5) {
                prop = PropFactory.createProp("Blood", this.getLocationX(), this.getLocationY());
                propName = "加血道具";

            } else if (num < 0.7) {
                prop = PropFactory.createProp("Fire", this.getLocationX(), this.getLocationY());
                propName = "火力道具";

            } else if (num < 0.9) {
                prop = PropFactory.createProp("SuperFire", this.getLocationX(), this.getLocationY());
                propName = "超级火力道具";

            } else if (num < 0.95) {
                prop = PropFactory.createProp("Bomb", this.getLocationX(), this.getLocationY());
                propName = "炸弹道具";

            } else {
                prop = PropFactory.createProp("Freeze", this.getLocationX(), this.getLocationY());
                propName = "冰冻道具";

            }
            // 如果真的掉落了东西，把它装进列表里返回
            if (prop != null) {
                res.add(prop);
                System.out.println("王牌机坠毁，掉落了：" + propName + "！");
            }
        } while (count > 0);

        return res;
    }

    @Override
    public void updateOnUnfreeze() {

    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.Shoot(this, 12);
    }

    @Override
    public int updateOnBomb() {
        return 0;
    }

    @Override
    public int updateOnFreeze() {
        // Boss 完全免疫冰冻
        return 0;
    }
}
