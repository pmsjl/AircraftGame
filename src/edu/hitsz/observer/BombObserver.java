package edu.hitsz.observer;



//观察者模式
public interface BombObserver {
    // 听到炸弹爆炸时的反应，返回产生的分数
    public int updateOnBomb();
}