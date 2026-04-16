package edu.hitsz.record;

import edu.hitsz.application.Difficulty; // 记得导入你的难度枚举类
import java.io.Serializable;
import java.util.UUID;

/**
 * 玩家得分记录实体类
 */
public class PlayerRecord implements Serializable, Comparable<PlayerRecord> {
    // 序列化版本号，防止修改类结构后读取报错
    private static final long serialVersionUID = 1L;

    private String recordId;
    private String playerName;
    private int score;
    private Difficulty difficulty; // 记录打出这个分数时的游戏难度
    private String recordTime; // 记录时间

    public PlayerRecord(String playerName, int score, Difficulty difficulty, String recordTime) {
        this(UUID.randomUUID().toString(), playerName, score, difficulty, recordTime);
    }

    public PlayerRecord(String recordId, String playerName, int score, Difficulty difficulty, String recordTime) {
        this.recordId = recordId;
        this.playerName = playerName;
        this.score = score;
        this.difficulty = difficulty;
        this.recordTime = recordTime;
        ensureRecordId();
    }

    // --- Getter 方法 ---
    public String getRecordId() {
        return recordId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public String getRecordTime() {
        return recordTime;
    }

    /**
     * 为旧版本反序列化出的记录补齐主键，保证删除操作有稳定标识。
     *
     * @return true 表示本次补齐了新主键
     */
    public boolean ensureRecordId() {
        if (recordId == null || recordId.trim().isEmpty()) {
            recordId = UUID.randomUUID().toString();
            return true;
        }
        return false;
    }

    /**
     * 核心规则：按分数从高到低降序排列
     */
    @Override
    public int compareTo(PlayerRecord other) {
        int scoreCompare = Integer.compare(other.score, this.score);
        if (scoreCompare != 0) {
            return scoreCompare;
        }
        return this.recordId.compareTo(other.recordId);
    }
}