package edu.hitsz.enemy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.observer.Explosive;
import edu.hitsz.strategy.NullShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * 自爆机
 * 不射击、低血量、向下冲锋速度极快
 * 死亡时（被击毁或撞机）触发爆炸，向 8 个方向喷射敌弹
 */
public class KamikazeEnemy extends AbstractAircraft implements Explosive {

    private static final int EXPLOSION_DIRECTIONS = 8;
    private static final int EXPLOSION_BULLET_SPEED = 8;
    private static final int EXPLOSION_BULLET_POWER = 20;

    public KamikazeEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.score = 20;
        this.shootNum = 0;
        this.power = 0;
        this.direction = 1;
        this.shootStrategy = new NullShootStrategy();
        this.originalSpeedX = speedX;
        this.originalSpeedY = speedY;
    }

    @Override
    public void forward() {
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
        return shootStrategy.Shoot(this, 0);
    }

    /**
     * 8 方向爆炸弹幕，由 Game 统一注入到 enemyBullets
     */
    @Override
    public List<BaseBullet> explode() {
        List<BaseBullet> bullets = new LinkedList<>();
        for (int i = 0; i < EXPLOSION_DIRECTIONS; i++) {
            double angle = 2 * Math.PI * i / EXPLOSION_DIRECTIONS;
            int bsx = (int) Math.round(EXPLOSION_BULLET_SPEED * Math.cos(angle));
            int bsy = (int) Math.round(EXPLOSION_BULLET_SPEED * Math.sin(angle));
            bullets.add(new EnemyBullet(locationX, locationY, bsx, bsy, EXPLOSION_BULLET_POWER));
        }
        System.out.println("自爆机引爆！喷射 " + EXPLOSION_DIRECTIONS + " 方向弹幕！");
        return bullets;
    }

    @Override
    public void updateOnUnfreeze() {
        this.speedX = this.originalSpeedX;
        this.speedY = this.originalSpeedY;
    }

    @Override
    public int updateOnBomb() {
        // 炸弹也算击毁，由 Game 触发 explode()
        this.vanish();
        return this.score;
    }

    @Override
<<<<<<< HEAD
    public int updateOnFreeze() {
        this.speedX = 0;
        this.speedY = 0;
        // 自爆机归普通机类，永久静止直至离场或死亡
        return -1;
=======
    public boolean updateOnFreeze() {
        this.speedX = 0;
        this.speedY = 0;
        return true;
>>>>>>> 1aaca805a7a8a470b7b73812a01b7a44b78a1d49
    }
}
