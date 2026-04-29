package edu.hitsz.application;

import edu.hitsz.dao.RecordDao;

/**
 * 普通难度。
 * <p>正常生成 Boss，Boss HP 不变；启用难度递进：每 30 秒敌机生成更快、新生敌机移速更高、血量更厚。
 */
public class NormalGame extends Game {

    // 每次递进的乘数（中等幅度）
    private static final double SPAWN_DECAY = 0.92;
    private static final double SPEED_GROWTH = 1.10;
    private static final double HP_GROWTH = 1.15;

    public NormalGame(RecordDao recordDao) {
        super(Difficulty.NORMAL, recordDao);
    }

    @Override
    protected void onProgressTick() {
        this.enemySpawnCycle = Math.max(2.0, this.enemySpawnCycle * SPAWN_DECAY);
        this.enemySpeedMultiplier *= SPEED_GROWTH;
        this.enemyHpMultiplier *= HP_GROWTH;
        System.out.printf(
                "[难度递进] 第 %d 次升级 — 敌机生成周期=%.2f, 新生速度系数=%.2f, 新生血量系数=%.2f%n",
                getProgressLevel(), enemySpawnCycle, enemySpeedMultiplier, enemyHpMultiplier);
    }
}
