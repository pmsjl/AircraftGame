package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;

public class NullShootStrategy implements ShootStrategy{
    @Override
    public List<BaseBullet> Shoot(AbstractAircraft aircraft,int speedSet) {
        return new LinkedList<>();
    }
}
