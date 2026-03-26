package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;

/**
 * 超级火力道具
 *
 */
public class BulletPlusProp extends AbstractProp {
    public BulletPlusProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public int effect(HeroAircraft heroAircraft) {
        int num = heroAircraft.getShootNum();
        heroAircraft.setShootNum(num * 2);
        return 2;
    }
}
