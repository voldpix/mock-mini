package dev.mock.mini.repository;

import dev.mock.mini.Database;
import dev.mock.mini.repository.model.MockRule;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MockRuleRepository {

    public void createRule(MockRule mockRule) {
        var sql = """
                INSERT INTO mock_rules
                (id, method, path, headers, body, status_code, delay, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """;

        try (var conn = Database.getConnection(); var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mockRule.getId());
            stmt.setString(2, mockRule.getMethod());
            stmt.setString(3, mockRule.getPath());
            stmt.setString(4, mockRule.getHeaders());
            stmt.setString(5, mockRule.getBody());
            stmt.setInt(6, mockRule.getStatusCode());
            stmt.setInt(7, mockRule.getDelay());
            stmt.setTimestamp(8, mockRule.getCreated());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to create mock rule", e);
        }
    }

    public void updateRule(String mockRuleId, MockRule mockRule) {
        var sql = """
            UPDATE mock_rules
            SET method = ?,
                path = ?,
                headers = ?,
                body = ?,
                status_code = ?,
                delay = ?
            WHERE id = ?
            """;

        try (var conn = Database.getConnection(); var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mockRule.getMethod());
            stmt.setString(2, mockRule.getPath());
            stmt.setString(3, mockRule.getHeaders());
            stmt.setString(4, mockRule.getBody());
            stmt.setInt(5, mockRule.getStatusCode());
            stmt.setInt(6, mockRule.getDelay());
            stmt.setString(7, mockRuleId);

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                log.info("No mock rule found with id={}", mockRule.getId());
            }

        } catch (SQLException e) {
            log.error("Failed to update mock rule with id={}", mockRule.getId(), e);
        }
    }

    public MockRule findById(String mockRuleId) {
        var sql = """
            SELECT id, method, path, headers, body, status_code, delay, created_at
            FROM mock_rules
            WHERE id = ?
            """;

        try (var conn = Database.getConnection(); var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mockRuleId);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Failed to find mock rule with id={}", mockRuleId, e);
        }

        return null;
    }

    public List<MockRule> findAll() {
        var sql = """
                SELECT id, method, path, headers, body, status_code, delay, created_at FROM mock_rules ORDER BY created_at DESC;
        """;

        var mockRules = new ArrayList<MockRule>();
        try (var conn = Database.getConnection(); var stmt = conn.prepareStatement(sql)) {
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) mockRules.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to find mock rules", e);
        }
        return mockRules;
    }

    public void deleteMockRule(String mockRuleId) {
        var sql = """
                DELETE FROM mock_rules WHERE id = ?;
        """;

        try (var conn = Database.getConnection(); var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mockRuleId);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                log.warn("No mock rule found with id={}", mockRuleId);
            }
        } catch (SQLException e) {
            log.error("Failed to delete mock rule with id={}", mockRuleId, e);
        }
    }

    private MockRule mapRow(ResultSet rs) {
        var mockRule = new MockRule();
        try {
            mockRule.setId(rs.getString("id"));
            mockRule.setMethod(rs.getString("method"));
            mockRule.setPath(rs.getString("path"));
            mockRule.setHeaders(rs.getString("headers"));
            mockRule.setBody(rs.getString("body"));
            mockRule.setStatusCode(rs.getInt("status_code"));
            mockRule.setDelay(rs.getInt("delay"));
            mockRule.setCreated(rs.getTimestamp("created_at"));
        } catch (SQLException e) {
            log.error("Failed to map MockRule row", e);
        }
        return mockRule;
    }
}
