package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.enemy.MobEnemy;

public class MobEnemyFactory implements EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        return new MobEnemy(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                0, // speedX
                10, // speedY
                30 // hp
        );
    }
}