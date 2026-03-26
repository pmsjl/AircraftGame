package edu.hitsz.enemy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 王牌敌机
 */

public class BossEnemy extends AbstractAircraft
{

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {

        super(locationX, locationY, speedX, speedY, hp);
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
