package edu.hitsz.dao;

import edu.hitsz.record.PlayerRecord;
import edu.hitsz.application.Difficulty;
import java.util.List;

public interface RecordDao {
    // 根据游戏难度，获取对应的排行榜记录
    List<PlayerRecord> getAllRecords(Difficulty difficulty);

    // 添加一条新记录
    void addRecord(PlayerRecord record);

    // 删除一条记录（这里使用记录时间作为唯一标识来删除）
    void deleteRecord(String recordTime);
}