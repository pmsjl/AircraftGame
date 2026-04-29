package edu.hitsz.observer;

/**
 * 冰冻观察者接口
 * <p>
 * 返回值语义：
 * <ul>
 *   <li>{@code > 0}：进入冻结，持续指定帧数后由 Game 调用 {@code updateOnUnfreeze()} 恢复</li>
 *   <li>{@code 0}：免疫冰冻（如 Boss、英雄机、英雄子弹）</li>
 *   <li>{@code -1}：永久冻结（如普通敌机），不会被自动解冻</li>
 * </ul>
 */
public interface FreezeObserver {
    int updateOnFreeze();
}
