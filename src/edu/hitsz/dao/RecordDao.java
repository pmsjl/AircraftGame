package edu.hitsz.dao;

import edu.hitsz.record.PlayerRecord;
import edu.hitsz.application.Difficulty;
import java.util.List;

/**
 * DAO 接口：对排行榜记录提供统一的数据访问能力。
 * 调用方只依赖接口，不关心底层是文件、数据库还是其他存储介质。
 */
public interface RecordDao {
    // 根据游戏难度，获取对应的排行榜记录
    List<PlayerRecord> getAllRecords(Difficulty difficulty);

    // 添加一条新记录
    void addRecord(PlayerRecord record);

    // 按稳定主键删除一条记录
    void deleteRecordById(String recordId);
}