package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.RingShootStrategy;
import edu.hitsz.strategy.StraightShootStrategy;
import edu.hitsz.utils.MusicThread;

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
        int buffVersion = heroAircraft.applyBuff(2);
        if (buffVersion == -1) {
            System.out.println("当前存在更高级道具，获得的低级道具不生效");
            return 0;
        }
        System.out.println("吃到超级火力道具，火力增强！");
        // 1. 生效：增加子弹数量

        heroAircraft.setShootNum(heroAircraft.getShootNum() * 2);
        heroAircraft.setShootStrategy(new RingShootStrategy());
        new MusicThread("src/videos/get_supply.wav", false).start();

        // 【核心修改】：开启一个专门的后台线程来控制火力恢复
        new Thread(() -> {
            try {
                // 2. 维持时长：线程休眠
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 3. 恢复原状：把刚才加上的子弹减回去
            heroAircraft.clearBuff(buffVersion);

        }).start();

        return 0;
    }
}
