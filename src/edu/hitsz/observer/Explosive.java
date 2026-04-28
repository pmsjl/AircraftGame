package edu.hitsz.observer;

import edu.hitsz.bullet.BaseBullet;

import java.util.List;

/**
 * 可爆炸接口：实现该接口的飞机在死亡时会触发自爆，向外抛出一组子弹
 */
public interface Explosive {
    /**
     * 触发自爆，返回向四周扩散的子弹列表
     */
    List<BaseBullet> explode();
}
