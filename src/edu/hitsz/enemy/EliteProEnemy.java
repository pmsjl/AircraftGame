package edu.hitsz.enemy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.factory.PropFactory;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.ScatterShootStrategy;
import edu.hitsz.strategy.StraightShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * 王牌敌机
 */


public class EliteProEnemy extends AbstractAircraft {
    public EliteProEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.score = 30;
        this.shootNum = 3;
        this.power = 25;
        this.direction = 2;
        this.shootStrategy = new ScatterShootStrategy();
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
    public List<AbstractProp> dropProps() {

        List<AbstractProp> res = new LinkedList<>();
        double num = Math.random();
        AbstractProp prop = null;
        String propName="";
        if (num < 0.1) {
            prop = PropFactory.createProp("Blood", this.getLocationX(), this.getLocationY());
            propName = "加血道具";

        } else if (num < 0.2) {
            prop = PropFactory.createProp("Fire", this.getLocationX(), this.getLocationY());
            propName = "火力道具";

        } else if (num < 0.3) {
            prop = PropFactory.createProp("SuperFire", this.getLocationX(), this.getLocationY());
            propName = "超级火力道具";

        } else if (num < 0.35) {
            prop = PropFactory.createProp("Bomb", this.getLocationX(), this.getLocationY());
            propName = "炸弹道具";

        } else if (num < 0.4) {
            prop = PropFactory.createProp("Freeze", this.getLocationX(), this.getLocationY());
            propName = "冰冻道具";

        } else {

        }
        // 如果真的掉落了东西，把它装进列表里返回
        if (prop != null) {
            res.add(prop);
            System.out.println("王牌机坠毁，掉落了：" + propName + "！");
        }
        return res;
    }


    @Override
    public void updateOnUnfreeze() {

    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.Shoot(this, 5);
    }

    @Override
    public int updateOnBomb() {
        this.decreaseHp(35);
        if (!this.isValid) {
            return this.score;
        }
        return 0;

    }

    @Override
    public boolean updateOnFreeze() {
        this.speedY =(int)(speedY*0.5);
        this.speedX =(int)(speedX*0.5);

        return false;
    }
}
