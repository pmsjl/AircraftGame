package edu.hitsz.enemy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.factory.PropFactory;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.StraightShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * 闪避机
 * 被命中后立刻向左或向右快速横向位移一段时间，然后恢复原速
 */
public class DodgeEnemy extends AbstractAircraft {

    private static final int DODGE_DURATION = 5;   // 闪避帧数
    private static final int DODGE_SPEED    = 15;   // 闪避横向速度

    private int dodgeFrames = 0;

    public DodgeEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.score = 25;
        this.shootNum = 1;
        this.power = 15;
        this.direction = 1;
        this.shootStrategy = new StraightShootStrategy();
        this.originalSpeedX = speedX;
        this.originalSpeedY = speedY;
    }

    @Override
    public void decreaseHp(int decrease) {
        super.decreaseHp(decrease);
        // 中弹存活时触发一次闪避
        if (!notValid() && dodgeFrames == 0) {
            this.speedX = (Math.random() < 0.5 ? -1 : 1) * DODGE_SPEED;
            this.dodgeFrames = DODGE_DURATION;
        }
    }

    @Override
    public void forward() {
        if (dodgeFrames > 0) {
            dodgeFrames--;
            if (dodgeFrames == 0) {
                this.speedX = this.originalSpeedX;
            }
        }
        super.forward();
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
        return shootStrategy.Shoot(this, 5);
    }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> res = new LinkedList<>();
        double num = Math.random();
        AbstractProp prop = null;
        String propName = "";
        if (num < 0.10) {
            prop = PropFactory.createProp("Blood", this.getLocationX(), this.getLocationY());
            propName = "加血道具";
        } else if (num < 0.20) {
            prop = PropFactory.createProp("Fire", this.getLocationX(), this.getLocationY());
            propName = "火力道具";
        } else if (num < 0.30) {
            prop = PropFactory.createProp("SuperFire", this.getLocationX(), this.getLocationY());
            propName = "超级火力道具";
        }
        if (prop != null) {
            res.add(prop);
            System.out.println("闪避机坠毁，掉落了：" + propName + "！");
        }
        return res;
    }

    @Override
    public void updateOnUnfreeze() {
        this.speedX = this.originalSpeedX;
        this.speedY = this.originalSpeedY;
    }

    @Override
    public int updateOnBomb() {
        this.vanish();
        return this.score;
    }

    @Override
    public int updateOnFreeze() {
        this.speedX = 0;
        this.speedY = 0;
        this.dodgeFrames = 0;
        // 闪避机归普通机类，永久静止直至离场或死亡
        return -1;
    }
}
