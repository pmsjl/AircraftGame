package edu.hitsz.strategy;


import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 环射策略：360度全方位发射
 */
public class RingShootStrategy implements ShootStrategy {

    @Override
    public List<BaseBullet> Shoot(AbstractAircraft aircraft, int speedSet) {
        List<BaseBullet> res = new LinkedList<>();

        // 环射通常是从飞机的正中心发射，所以直接获取飞机坐标
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY();
        int power = aircraft.getPower();
        int shootNum = aircraft.getShootNum();

        // 如果子弹数为 0，直接返回空
        if (shootNum <= 0) {
            return res;
        }

        // 以传入的 speedSet 作为子弹向外扩散的基准速度
        double baseSpeed = speedSet;

        for (int i = 0; i < shootNum; i++) {

            // 1. 计算当前这颗子弹的发射角度 (弧度制)
            // 公式：(2 * PI / 总子弹数) * 第i颗
            double angle = (2 * Math.PI / shootNum) * i;

            // 2. 利用三角函数计算 X 和 Y 方向的分速度
            // Math.cos 和 Math.sin 接收的都是弧度值
            int speedX = (int) (baseSpeed*2 * Math.cos(angle));
            int speedY = (int) (baseSpeed*2 * Math.sin(angle));

            // 3. 制造子弹
            // 注意这里就不需要 direction 参数了，因为子弹是朝四面八方飞的
            BaseBullet bullet = aircraft.createBullet(x, y, speedX, speedY, power);
            res.add(bullet);
        }

        return res;
    }
}