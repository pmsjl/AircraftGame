package edu.hitsz.factory;

import edu.hitsz.prop.*;

/**
 * 道具简单工厂
 */
public class PropFactory {

    /**
     * 生产道具的静态方法
     * @param type 道具类型：Blood, Bomb, Fire, SuperFire, Freeze
     * @param x 产生位置X
     * @param y 产生位置Y
     */
    public static AbstractProp createProp(String type, int x, int y) {
        // 道具统一下落速度设为 5
        int speedX = 5;
        int speedY = 5;

        switch (type) {
            case "Blood":
                return new BloodProp(x, y, speedX, speedY);
            case "Bomb":
                return new BombProp(x, y, speedX, speedY);
            case "Fire":
                return new BulletProp(x, y, speedX, speedY);
            case "SuperFire":
                return new BulletPlusProp(x, y, speedX, speedY);
            case "Freeze":
                return new FreezeProp(x, y, speedX, speedY);
            default:
                return null;
        }
    }
}