package edu.hitsz.dao;

import edu.hitsz.record.PlayerRecord;
import edu.hitsz.application.Difficulty;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文件版 DAO 实现，负责排行榜记录的加载、排序、保存与删除。
 */
public class RecordDaoImpl implements RecordDao {
    // 内存中的排行榜大名单
    private List<PlayerRecord> records = new ArrayList<>();
    private static final Path DEFAULT_FILE_PATH = Paths.get("data", "record.dat");
    private static final Path LEGACY_FILE_PATH = Paths.get("src", "record.dat");
    private final Path filePath;

    public RecordDaoImpl() {
        this(DEFAULT_FILE_PATH);
    }

    public RecordDaoImpl(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath, "filePath");
        this.records = loadFromFile();
        normalizeRecords();
    }

    /***
     * 获取指定难度的数据记录
     * 
     * @param difficulty
     * @return
     */
    @Override
    public synchronized List<PlayerRecord> getAllRecords(Difficulty difficulty) {
        return records.stream()
                .filter(r -> r.getDifficulty() == difficulty)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void addRecord(PlayerRecord record) {
        Objects.requireNonNull(record, "record");
        record.ensureRecordId();
        records.add(record);
        records.sort(null); // 添加后立刻按分数从高到低排好序
        saveToFile(); // 立刻存入硬盘！
    }

    @Override
    public synchronized void deleteRecordById(String recordId) {
        boolean removed = records.removeIf(r -> Objects.equals(r.getRecordId(), recordId));
        if (removed) {
            saveToFile();
        }
    }

    // ============================================
    // 内部私有方法：处理繁琐的文件 I/O 读写
    // ============================================
    private List<PlayerRecord> loadFromFile() {
        Path readPath = resolveReadablePath();
        if (!Files.exists(readPath)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(readPath.toFile()))) {
            Object savedObject = ois.readObject();
            if (!(savedObject instanceof List<?> savedList)) {
                return new ArrayList<>();
            }
            List<PlayerRecord> loadedRecords = new ArrayList<>();
            for (Object item : savedList) {
                if (item instanceof PlayerRecord record) {
                    loadedRecords.add(record);
                }
            }
            return loadedRecords;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载排行榜失败：" + readPath.toAbsolutePath());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveToFile() {
        try {
            Path parent = filePath.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            System.err.println("创建排行榜目录失败：" + filePath.toAbsolutePath());
            e.printStackTrace();
            return;
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            // 把排好序的 List 序列化写进硬盘
            oos.writeObject(records);
        } catch (IOException e) {
            System.err.println("保存排行榜失败：" + filePath.toAbsolutePath());
            e.printStackTrace();
        }
    }

    private void normalizeRecords() {
        boolean metadataChanged = false;
        for (PlayerRecord record : records) {
            metadataChanged |= record.ensureRecordId();
        }
        records.sort(null);
        if (metadataChanged || shouldMigrateLegacyFile()) {
            saveToFile();
        }
    }

    private Path resolveReadablePath() {
        if (Files.exists(filePath)) {
            return filePath;
        }
        if (usesDefaultStore() && Files.exists(LEGACY_FILE_PATH)) {
            return LEGACY_FILE_PATH;
        }
        return filePath;
    }

    private boolean shouldMigrateLegacyFile() {
        return usesDefaultStore() && !Files.exists(filePath) && Files.exists(LEGACY_FILE_PATH) && !records.isEmpty();
    }

    private boolean usesDefaultStore() {
        return filePath.toAbsolutePath().normalize().equals(DEFAULT_FILE_PATH.toAbsolutePath().normalize());
    }
}