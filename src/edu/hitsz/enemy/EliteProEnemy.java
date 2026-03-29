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


public class EliteProEnemy extends AbstractAircraft {
    public EliteProEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.score = 30;
        this.shootNum = 1;
        this.power = 25;
        this.direction = 3;
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
    public List<AbstractProp> dropProps() {
        List<AbstractProp> res = new LinkedList<>();
        double num = Math.random();
        if (num < 0.1) {
            // 用工厂造一个加血道具，位置就在当前精英机爆炸的地方
            AbstractProp blood = PropFactory.createProp("Blood", this.getLocationX(), this.getLocationY());
            if (blood != null) {
                res.add(blood);
                System.out.println("王牌机掉落了加血道具！");
            }
        } else if (num > 0.9) {
            AbstractProp bulletplus = PropFactory.createProp("SuperFire", this.getLocationX(), this.getLocationY());
            if (bulletplus != null) {
                res.add(bulletplus);
                System.out.println("王牌机掉落了超级弹药道具！");
            }
        } else if(num>0.4&&num<0.45){
            AbstractProp bomb = PropFactory.createProp("Bomb", this.getLocationX(), this.getLocationY());
            if (bomb != null) {
                res.add(bomb);
                System.out.println("王牌机掉落了炸弹道具！");
            }
        }
        return res;
    }

    @Override
    public void updateOnUnfreeze() {

    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.Shoot(this, 18);
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
