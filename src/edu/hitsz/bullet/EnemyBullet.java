package edu.hitsz.bullet;

/**
 * 敌机子弹
 * @Author hitsz
 */
public class EnemyBullet extends BaseBullet {

    public EnemyBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    @Override
    public int updateOnFreeze() {
        // lab6 要求：敌机子弹冻结 5 秒（25 FPS × 5 = 125 帧）后恢复
        this.frozenSpeedX = this.speedX;
        this.frozenSpeedY = this.speedY;
        this.speedX = 0;
        this.speedY = 0;
        return 125;
    }
}
