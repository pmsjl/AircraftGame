package edu.hitsz.enemy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 王牌敌机
 */


public class EliteProEnemy extends AbstractAircraft {
    public EliteProEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }
    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }
    @Override
    public BaseBullet createBullet(int x, int y, int speedX, int speedY, int power) {
        return new EnemyBullet(x,y,speedX,speedY,power);
    }

    @Override
    public List<BaseBullet> shoot() {
        return new LinkedList<>();
    }
}
