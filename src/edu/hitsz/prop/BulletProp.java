package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;

/**
 * 火力道具
 */
public class BulletProp extends AbstractProp {
    public BulletProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void effect(HeroAircraft heroAircraft) {
        int num = heroAircraft.getShootNum();
        heroAircraft.setShootNum(num + 2);
    }
}
