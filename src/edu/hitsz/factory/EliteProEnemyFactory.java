package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.enemy.EliteEnemy;
import edu.hitsz.enemy.EliteProEnemy;

public class EliteProEnemyFactory implements EnemyFactory{
    @Override
    public AbstractAircraft createEnemy() {
        return new EliteProEnemy(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                12,
                5, // speedY
                65//
        );
    }
}