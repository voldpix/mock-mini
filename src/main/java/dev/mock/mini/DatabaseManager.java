package dev.mock.mini;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseManager {

    private static DB db;

    public static void initialize() {
        var dataDir = new File("data");
        if (!dataDir.exists()) {
            var result = dataDir.mkdirs();
            log.info("Data directory {} created: {}", dataDir, result);
        }

        db = DBMaker.fileDB(Constants.DB_PATH)
                .fileMmapEnableIfSupported()
                .transactionEnable()
                .closeOnJvmShutdown()
                .make();
        log.info("Database initialized successfully");
    }

    public static DB getDB() {
        return db;
    }

    public static void close() {
        if (Objects.nonNull(db) && !db.isClosed()) {
            db.close();
            log.info("Connection to database closed.");
        }
    }
}
