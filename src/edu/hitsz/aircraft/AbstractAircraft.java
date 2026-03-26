package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.observer.BombObserver;
import edu.hitsz.observer.FreezeObserver;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.ShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * 所有种类飞机的抽象父类
 *
 * @author hitsz
 */
public abstract class AbstractAircraft extends AbstractFlyingObject implements BombObserver, FreezeObserver {
    // 【新增】记录初始速度，用于冰冻后的恢复
    protected int originalSpeedX;
    protected int originalSpeedY;
    //最大生命值
    protected int maxHp;
    protected int hp;

    //这里这么添加就是为了完成我们的子弹设计策略模式需要使用get方法
    //抽象类多态父类也得有才能调用
    protected int shootNum = 1; // 默认发射数量
    protected int power = 10;   // 默认威力
    protected int direction = 1;// 默认方向（向下）
    protected ShootStrategy shootStrategy;

    public void setShootStrategy(ShootStrategy shootStrategy) {
        this.shootStrategy = shootStrategy;
    }

    public int getShootNum() {
        return shootNum;
    }

    public int getPower() {
        return power;
    }

    public int getDirection() {
        return direction;
    }

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
    }

    public void decreaseHp(int decrease) {
        hp -= decrease;
        if (hp <= 0) {
            hp = 0;
            vanish();
        }
    }

    public void increaseHp(int increase) {
        if (hp + increase >= maxHp) {
            hp = maxHp;
        } else {
            hp += increase;
        }
    }

    //子弹策略英雄和
    public abstract BaseBullet createBullet(int x, int y, int speedX, int speedY, int power);

    public int getHp() {
        return hp;
    }


    public void setShootNum(int shootNum) {
        this.shootNum = shootNum;
    }

    /**
     * 飞机射击方法
     *
     * @return 可射击对象需实现，返回子弹列表
     * 非可射击对象空实现，返回空列表
     */
    public abstract List<BaseBullet> shoot();


    //为了留下道具，添加新的方法

    // 飞机的基础分值，默认是 10 分
    protected int score = 10;

    // 获取这架飞机的分值
    public int getScore() {
        return this.score;
    }

    /**
     * 掉落道具的方法（默认什么都不掉落）
     * 只有需要掉落道具的飞机（比如精英机）才会重写这个方法
     */
    public List<AbstractProp> dropProps() {
        return new LinkedList<>(); // 默认返回一个空的列表
    }

    // 【观察者模式】增加解除冰冻的接头暗号
    public abstract void updateOnUnfreeze();
}


