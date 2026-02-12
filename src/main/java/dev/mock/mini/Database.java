package dev.mock.mini;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.mock.mini.common.exception.MockMiniException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Database {

    private static HikariDataSource dataSource;

    public static void initialize() {
        var dataDir = new File("data");
        if (!dataDir.exists()) {
            var result = dataDir.mkdirs();
            log.info("Data directory {} created: {}", dataDir, result);
        }

        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + Constants.DB_PATH);
        config.setDriverClassName("org.sqlite.JDBC");

        config.setMaximumPoolSize(1);
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("SqlitePool");

        dataSource = new HikariDataSource(config);

        try (var conn = getConnection()) {
            initSchema(conn);
        } catch (SQLException e) {
            throw new MockMiniException("Failed to initialize database", e);
        }
        log.info("Database initialized successfully.");
    }

    private static void initSchema(Connection conn) throws SQLException {
        try {
            var schemaSql = loadResource();
            var statements = schemaSql.split(";");

            try (var stmt = conn.createStatement()) {
                for (var sql : statements) {
                    if (!sql.trim().isEmpty()) {
                        stmt.execute(sql.trim());
                    }
                }
            }
            log.info("Database schema initialized from schema.sql");
        } catch (Exception e) {
            throw new MockMiniException("Could not initialize database schema", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        if (Objects.nonNull(dataSource) && !dataSource.isClosed()) {
            log.info("Closing connection to database.");
            dataSource.close();
            log.info("Connection to database closed.");
        }
    }

    private static String loadResource() {
        var path = "schema.sql";
        try (var is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (Objects.isNull(is)) {
                throw new MockMiniException("Resource not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new MockMiniException("Failed to read resource: " + path, e);
        }
    }
}
