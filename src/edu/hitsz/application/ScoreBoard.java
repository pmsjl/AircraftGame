package edu.hitsz.application;

import edu.hitsz.dao.RecordDao;
import edu.hitsz.dao.RecordDaoImpl;
import edu.hitsz.record.PlayerRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 排行榜面板
 */
public class ScoreBoard extends JPanel {

    private final Difficulty difficulty;
    private final RecordDao recordDao;
    private DefaultTableModel tableModel;
    private JTable scoreTable;

    public ScoreBoard(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.recordDao = new RecordDaoImpl();

        // 使用 BorderLayout (东南西北中布局)
        this.setLayout(new BorderLayout());

        // 1. 顶部：标题
        JLabel titleLabel = new JLabel("排行榜 - " + difficulty.getDisplayName() + "模式", SwingConstants.CENTER);
        titleLabel.setFont(new Font("黑体", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        this.add(titleLabel, BorderLayout.NORTH);

        // 2. 中部：带滚动条的表格
        initTable();
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        this.add(scrollPane, BorderLayout.CENTER);

        // 3. 底部：操作按钮
        JPanel bottomPanel = new JPanel();
        JButton deleteBtn = new JButton("删除选中记录");
        JButton backBtn = new JButton("返回主菜单");

        // 删除按钮逻辑
        deleteBtn.addActionListener(e -> {
            int selectedRow = scoreTable.getSelectedRow();
            if (selectedRow != -1) {
                // 弹窗二次确认
                int result = JOptionPane.showConfirmDialog(this, "确定要删除选中的记录吗？", "确认删除", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    // 获取选中的那一行，第 4 列（索引为 3，即记录时间）的数据作为删除凭证
                    String recordTime = (String) tableModel.getValueAt(selectedRow, 3);
                    recordDao.deleteRecord(recordTime); // 从文件彻底删除
                    updateTableData(); // 刷新表格显示
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先在表格中点击选中一条记录！");
            }
        });

        // 返回主菜单逻辑
        backBtn.addActionListener(e -> {
            // 呼叫总指挥，切回开始菜单！
            Main.cardLayout.show(Main.cardPanel, "start");
        });

        bottomPanel.add(deleteBtn);
        bottomPanel.add(backBtn);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 初始化表格列名和基本属性
     */
    private void initTable() {
        // 定义表头
        String[] columnNames = {"名次", "玩家名", "得分", "记录时间"};
        // 初始化数据模型（不允许玩家直接双击修改单元格）
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scoreTable = new JTable(tableModel);
        scoreTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 只能单选

        // 填入数据
        updateTableData();
    }

    /**
     * 从 DAO 获取最新数据并刷新表格
     */
    private void updateTableData() {
        // 先清空旧数据
        tableModel.setRowCount(0);

        // 利用 DAO 模式查询当前难度的所有数据
        List<PlayerRecord> records = recordDao.getAllRecords(difficulty);

        // 逐行填入表格
        int rank = 1;
        for (PlayerRecord record : records) {
            Object[] rowData = {
                    rank++,                      // 名次
                    record.getPlayerName(),      // 玩家名
                    record.getScore(),           // 得分
                    record.getRecordTime()       // 记录时间
            };
            tableModel.addRow(rowData);
        }
    }
}