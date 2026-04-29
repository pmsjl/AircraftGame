package edu.hitsz.application;

import edu.hitsz.dao.RecordDao;
import edu.hitsz.enemy.BossEnemy;

/**
 * 困难难度。
 * <p>正常生成 Boss，每次出场 HP 累乘提升；难度递进同时调整射击周期，让交火节奏越来越紧张。
 */
public class HardGame extends Game {

    // 每次递进的乘数（中等幅度，与普通一致）
    private static final double SPAWN_DECAY = 0.92;
    private static final double SPEED_GROWTH = 1.10;
    private static final double HP_GROWTH = 1.15;
    // 困难独有：英雄射击周期变短（射速更快），敌机射击周期也变短（敌机更凶）
    private static final double HERO_SHOOT_DECAY = 1.0 / 1.05; // 周期 ×0.95
    private static final double ENEMY_SHOOT_DECAY = 0.92;

    // Boss 每次出场 HP 累乘倍率
    private static final double BOSS_HP_GROWTH = 1.30;
    private int bossSpawnCount = 0;

    public HardGame(RecordDao recordDao) {
        super(Difficulty.HARD, recordDao);
    }

    @Override
    protected void onBossSpawn(BossEnemy boss) {
        bossSpawnCount++;
        if (bossSpawnCount > 1) {
            double factor = Math.pow(BOSS_HP_GROWTH, bossSpawnCount - 1);
            boss.scaleHp(factor);
            System.out.printf("[困难加成] 第 %d 次 Boss — HP 倍率 ×%.2f%n", bossSpawnCount, factor);
        }
    }

    @Override
    protected void onProgressTick() {
        this.enemySpawnCycle = Math.max(2.0, this.enemySpawnCycle * SPAWN_DECAY);
        this.enemySpeedMultiplier *= SPEED_GROWTH;
        this.enemyHpMultiplier *= HP_GROWTH;
        this.heroShootCycle = Math.max(1.0, this.heroShootCycle * HERO_SHOOT_DECAY);
        this.enemyShootCycle = Math.max(2.0, this.enemyShootCycle * ENEMY_SHOOT_DECAY);
        System.out.printf(
                "[难度递进] 第 %d 次升级 — 生成=%.2f, 速度系数=%.2f, 血量系数=%.2f, 英雄射周期=%.2f, 敌机射周期=%.2f%n",
                getProgressLevel(), enemySpawnCycle, enemySpeedMultiplier,
                enemyHpMultiplier, heroShootCycle, enemyShootCycle);
    }
}
