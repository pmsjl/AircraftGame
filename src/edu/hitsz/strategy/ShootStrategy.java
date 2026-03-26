package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import java.util.List;

/**
 * 射击策略模式的实现
 * 将射击的不同模式提取成不同的类
 */
public interface ShootStrategy {
    /**
     * 执行射击动作
     * @param aircraft 是谁在射击（为了获取它的坐标、速度等）
     * @return 产生的子弹列表
     */
    List<BaseBullet> Shoot(AbstractAircraft aircraft,int speedSet);
}