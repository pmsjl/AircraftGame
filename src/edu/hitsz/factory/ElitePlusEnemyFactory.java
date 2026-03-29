package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.enemy.EliteEnemy;
import edu.hitsz.enemy.ElitePlusEnemy;

public class ElitePlusEnemyFactory implements EnemyFactory{
    @Override
    public AbstractAircraft createEnemy() {
        return new ElitePlusEnemy(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                10,
                10, // speedY
                55 //
        );
    }
}
