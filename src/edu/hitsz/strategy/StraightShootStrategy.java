package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;

public class StraightShootStrategy implements ShootStrategy{
    @Override
    public List<BaseBullet> Shoot(AbstractAircraft aircraft,int speedSet) {
        List<BaseBullet> res = new LinkedList<>();
        //因为把原本的射击方法提出来，所以对应参数都要重新获取
        int direction=aircraft.getDirection();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction*2;
        int power=aircraft.getPower();
        int shootNum=aircraft.getShootNum();
        int speedX = 0;
        int speedY = aircraft.getSpeedY() + direction*speedSet;
        BaseBullet bullet;
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            bullet = aircraft.createBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }
}
