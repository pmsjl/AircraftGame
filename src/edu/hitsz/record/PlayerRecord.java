package edu.hitsz.record;

import edu.hitsz.application.Difficulty; // 记得导入你的难度枚举类
import java.io.Serializable;

/**
 * 玩家得分记录实体类
 */
public class PlayerRecord implements Serializable, Comparable<PlayerRecord> {
    // 序列化版本号，防止修改类结构后读取报错
    private static final long serialVersionUID = 1L;

    private String playerName;
    private int score;
    private Difficulty difficulty; // 记录打出这个分数时的游戏难度
    private String recordTime;     // 记录时间

    public PlayerRecord(String playerName, int score, Difficulty difficulty, String recordTime) {
        this.playerName = playerName;
        this.score = score;
        this.difficulty = difficulty;
        this.recordTime = recordTime;
    }

    // --- Getter 方法 ---
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public Difficulty getDifficulty() { return difficulty; }
    public String getRecordTime() { return recordTime; }

    /**
     * 核心规则：按分数从高到低降序排列
     */
    @Override
    public int compareTo(PlayerRecord other) {
        return Integer.compare(other.score, this.score);
    }
}