package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.application.Main;

/**
 * 所有道具的抽象基类
 */
public abstract class AbstractProp extends AbstractFlyingObject {
    public AbstractProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void forward() {
        super.forward();
        // 道具飞出屏幕下方则消失
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }
    public abstract void effect(HeroAircraft heroAircraft);
}