package edu.hitsz.application;

import edu.hitsz.dao.RecordDao;

/**
 * 简单难度。
 * <p>不生成 Boss，不启用难度递进，初始周期保持 Difficulty.EASY 的乘数计算结果。
 */
public class EasyGame extends Game {

    public EasyGame(RecordDao recordDao) {
        super(Difficulty.EASY, recordDao);
    }

    @Override
    protected boolean shouldSpawnBoss() {
        // 简单难度永远不出 Boss
        return false;
    }

    @Override
    protected int progressIntervalTicks() {
        // 简单难度不启用递进
        return 0;
    }
}
