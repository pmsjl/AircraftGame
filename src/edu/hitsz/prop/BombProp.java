package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;

/**
 * 炸弹道具
 */
public class BombProp extends AbstractProp{
    public BombProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public int effect(HeroAircraft heroAircraft) {
        System.out.println("炸弹引爆");
        return 4;
    }
}
