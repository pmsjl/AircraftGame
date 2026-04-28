package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.enemy.ShieldEnemy;

public class ShieldEnemyFactory implements EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        return new ShieldEnemy(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.SHIELD_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                0,
                8,   // speedY 较慢，体现"重型"
                70   // 真血量
        );
    }
}
