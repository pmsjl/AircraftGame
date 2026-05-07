package edu.hitsz.bullet;

import edu.hitsz.application.Main;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.observer.FreezeObserver;

/**
 * 子弹基类
 * @author hitsz
 */
public abstract class BaseBullet extends AbstractFlyingObject implements FreezeObserver {

    private int power = 0;

    // 冻结期间用于暂存原始速度，解冻后恢复
    protected int frozenSpeedX;
    protected int frozenSpeedY;

    public BaseBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY);
        this.power = power;
    }

    @Override
    public void forward() {
        super.forward();

        // 判定 x 轴出界
        if (locationX <= 0 || locationX >= Main.WINDOW_WIDTH) {
            vanish();
        }

        // 判定 y 轴出界
        if (speedY > 0 && locationY >= Main.WINDOW_HEIGHT ) {
            // 向下飞行出界
            vanish();
        }else if (locationY <= 0){
            // 向上飞行出界
            vanish();
        }
    }

    public int getPower() {
        return power;
    }

    /**
     * 默认不参与冰冻（英雄子弹会落到这条路径）。
     * 敌机子弹通过重写返回正帧数加入冰冻。
     */
    @Override
    public int updateOnFreeze() {
        return 0;
    }

    /**
     * 解冻：恢复速度（仅当 updateOnFreeze 返回 >0 时由 Game 调用）。
     */
    public void updateOnUnfreeze() {
        this.speedX = this.frozenSpeedX;
        this.speedY = this.frozenSpeedY;
    }
}
