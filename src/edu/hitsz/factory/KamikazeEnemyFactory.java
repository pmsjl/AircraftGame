package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.enemy.KamikazeEnemy;

public class KamikazeEnemyFactory implements EnemyFactory {
    @Override
    public AbstractAircraft createEnemy() {
        return new KamikazeEnemy(
                (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.KAMIKAZE_ENEMY_IMAGE.getWidth())),
                (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                0,
                12,  // 高速冲下
                25   // 低血量
        );
    }
}
