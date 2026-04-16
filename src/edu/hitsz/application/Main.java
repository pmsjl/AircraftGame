package edu.hitsz.application;

import edu.hitsz.dao.RecordDao;
import edu.hitsz.dao.RecordDaoImpl;

import javax.swing.*;
import java.awt.*;

/**
 * 程序入口
 * 
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;
    private static final RecordDao RECORD_DAO = new RecordDaoImpl();

    // 【全局卡片布局】方便随时随地切换界面
    public static final CardLayout cardLayout = new CardLayout();
    public static final JPanel cardPanel = new JPanel(cardLayout);

    public static RecordDao getRecordDao() {
        return RECORD_DAO;
    }

    public static void main(String[] args) {
        System.out.println("Hello Aircraft War");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("Aircraft War");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        StartMenu startMenu = new StartMenu();
        cardPanel.add(startMenu, "start"); // 给开始菜单起个代号叫 "start"

        // 默认显示 start 卡片
        cardLayout.show(cardPanel, "start");

        frame.add(cardPanel);
        frame.setVisible(true);
    }
}