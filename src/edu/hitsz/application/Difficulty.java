package edu.hitsz.application;

/**
 * 游戏难度枚举
 * 统一定义游戏的三种难度及其对应的数值倍率
 */
public enum Difficulty {
    EASY("简单", 1.0),
    NORMAL("普通", 1.5),
    HARD("困难", 2.0);

    private final String displayName;
    private final double scoreMultiplier;

    Difficulty(String displayName, double scoreMultiplier) {
        this.displayName = displayName;
        this.scoreMultiplier = scoreMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 获取难度对应的分数倍率（用于控制敌机生成速度或算分）
     */
    public double getScoreMultiplier() {
        return scoreMultiplier;
    }

    /**
     * 根据字符串名称获取对应的枚举对象（以后读取本地存档文件时会非常有用）
     */
    public static Difficulty fromString(String name) {
        for (Difficulty d : values()) {
            if (d.name().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return NORMAL; // 默认返回普通难度，防止报错
    }
}