package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractAircraft;

/**
 * 敌机工厂接口
 */
public interface EnemyFactory {
    /**
     * 创建敌机对象
     * @return 生产出的敌机
     */
    AbstractAircraft createEnemy();
}