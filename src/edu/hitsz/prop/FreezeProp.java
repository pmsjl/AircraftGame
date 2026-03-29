package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.utils.MusicThread;

/**
 * 冰冻道具
 */
public class FreezeProp extends AbstractProp {
    public FreezeProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public int effect(HeroAircraft heroAircraft) {
        new MusicThread("src/videos/get_supply.wav", false).start();
        return 3;
    }
}
