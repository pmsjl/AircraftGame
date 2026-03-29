package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;

/**
 * 游戏开始菜单面板
 */
public class StartMenu extends JPanel {

    public StartMenu() {
        // 使用网格布局把按钮竖着排列
        this.setLayout(new GridLayout(3, 1, 0, 50));
        this.setBorder(BorderFactory.createEmptyBorder(200, 150, 200, 150));

        // 1. 创建三个难度按钮
        JButton easyBtn = new JButton("简单模式");
        JButton normalBtn = new JButton("普通模式");
        JButton hardBtn = new JButton("困难模式");

        // 2. 给按钮绑定点击事件（Lambda 表达式简写）
        // 点击按钮后，去调用下方的 startGame 方法，并传入对应的枚举难度！
        easyBtn.addActionListener(e -> startGame(Difficulty.EASY));
        normalBtn.addActionListener(e -> startGame(Difficulty.NORMAL));
        hardBtn.addActionListener(e -> startGame(Difficulty.HARD));

        // 3. 将按钮加到面板上
        this.add(easyBtn);
        this.add(normalBtn);
        this.add(hardBtn);
    }

    /**
     * 点击难度按钮后触发的核心逻辑
     */
    private void startGame(Difficulty difficulty) {
        System.out.println("玩家选择了难度: " + difficulty.getDisplayName());

        // 1. 在这里才真正创建 Game 实例，并把难度喂给它！
        Game game = new Game(difficulty);

        // 2. 把刚创建好的游戏画面，塞进 Main 的卡片底板里，代号叫 "game"
        Main.cardPanel.add(game, "game");

        // 3. 呼叫发牌员 Main，把画面瞬间切到 "game" 这张卡片！
        Main.cardLayout.show(Main.cardPanel, "game");

        // 4. 通知 Game 的定时器开始运转，游戏正式开始！
        game.action();

        // （小细节：让游戏面板获取焦点，否则键盘事件可能失灵）
        game.requestFocusInWindow();
    }
}