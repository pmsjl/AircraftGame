package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.enemy.DodgeEnemy;

public class DodgeEnemyFactory implements EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        return new DodgeEnemy(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.DODGE_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                0,
                9,
                60
        );
    }
}
