package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.utils.MusicThread;

/**
 * 加血道具
 */
public class BloodProp extends AbstractProp {

    // 默认加血量
    private int healthAmount = 30;

    public BloodProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    /**
     * 道具触发效果：给英雄机加血
     *
     * @param heroAircraft 英雄机实例
     * @return
     */
    public int effect(HeroAircraft heroAircraft) {

        heroAircraft.increaseHp(healthAmount);
        new MusicThread("src/videos/get_supply.wav", false).start();
        System.out.println("吃到加血道具，血量增加 " + healthAmount);
        return 0;
    }
}