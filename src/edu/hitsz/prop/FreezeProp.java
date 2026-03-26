package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;

/**
 * 冰冻道具
 */
public class FreezeProp extends AbstractProp {
    public FreezeProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public int effect(HeroAircraft heroAircraft) {
        return 3;
    }
}
