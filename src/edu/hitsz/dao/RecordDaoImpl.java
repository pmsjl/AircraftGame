package edu.hitsz.dao;

import edu.hitsz.record.PlayerRecord;
import edu.hitsz.application.Difficulty;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RecordDaoImpl implements RecordDao {
    // 内存中的排行榜大名单
    private List<PlayerRecord> records = new ArrayList<>();
    // 本地存档文件的路径
    private static final String FILE_PATH = "src/record.dat";

    public RecordDaoImpl() {
        // 管理员一上任（类被实例化），立刻从硬盘读取历史数据
        loadFromFile();
    }

    /***
     * 获取指定难度的数据记录
     * @param difficulty
     * @return
     */
    @Override
    public List<PlayerRecord> getAllRecords(Difficulty difficulty) {
        return records.stream()
                .filter(r -> r.getDifficulty() == difficulty)
                .collect(Collectors.toList());
    }

    @Override
    public void addRecord(PlayerRecord record) {
        records.add(record);
        Collections.sort(records); // 添加后立刻按分数从高到低排好序
        saveToFile();              // 立刻存入硬盘！
    }

    @Override
    public void deleteRecord(String recordTime) {
        // 删掉时间匹配的那条记录
        records.removeIf(r -> r.getRecordTime().equals(recordTime));
        saveToFile();              // 立刻存入硬盘！
    }

    // ============================================
    // 内部私有方法：处理繁琐的文件 I/O 读写
    // ============================================
    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return; // 第一次玩，没有存档文件，直接返回
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // 从硬盘把一整个 List 反序列化读出来
            records = (List<PlayerRecord>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            // 把排好序的 List 序列化写进硬盘
            oos.writeObject(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}