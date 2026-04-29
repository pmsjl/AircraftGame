package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.enemy.BossEnemy;
import edu.hitsz.enemy.EliteEnemy;

public class BossEnemyFactory implements EnemyFactory{
    @Override
    public AbstractAircraft createEnemy() {
        return new BossEnemy(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                10,
                0, // speedY
                5000
        );
    }
}
