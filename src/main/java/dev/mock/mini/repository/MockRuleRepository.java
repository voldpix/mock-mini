package dev.mock.mini.repository;

import dev.mock.mini.DatabaseManager;
import dev.mock.mini.repository.model.MockRule;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.mapdb.DB;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class MockRuleRepository {

    private final DB db;
    private final ConcurrentMap<String, MockRule> storage;

    public MockRuleRepository() {
        this.db = DatabaseManager.getDB();
        this.storage = db.hashMap("mock_rules", Serializer.STRING, mockRuleSerializer())
                .createOrOpen();
    }

    public List<MockRule> getMockRules() {
        return new ArrayList<>(storage.values());
    }

    public void upsertRule(MockRule mockRule) {
        storage.put(mockRule.getId(), mockRule);
        db.commit();
    }

    public Optional<MockRule> findById(String mockRuleId) {
        return Optional.ofNullable(storage.get(mockRuleId));
    }

    public void deleteRule(String mockRuleId) {
        storage.remove(mockRuleId);
        db.commit();
    }

    private Serializer<MockRule> mockRuleSerializer() {
        return new Serializer<>() {
            @Override
            public void serialize(@NonNull DataOutput2 out, @NonNull MockRule mockRule) throws IOException {
                out.writeUTF(nullSafe(mockRule.getId()));
                out.writeUTF(nullSafe(mockRule.getMethod()));
                out.writeUTF(nullSafe(mockRule.getPath()));
                out.writeUTF(nullSafe(mockRule.getHeaders()));
                out.writeUTF(nullSafe(mockRule.getBody()));
                out.writeInt(nullSafeInt(mockRule.getStatusCode()));
                out.writeInt(nullSafeInt(mockRule.getDelay()));
                out.writeLong(mockRule.getCreated().getTime());
            }

            @Override
            public MockRule deserialize(@NonNull DataInput2 in, int available) throws IOException {
                return new MockRule(
                        in.readUTF(),
                        in.readUTF(),
                        in.readUTF(),
                        in.readUTF(),
                        in.readUTF(),
                        in.readInt(),
                        in.readInt(),
                        new Timestamp(in.readLong())
                );
            }

            private String nullSafe(String s) {
                return Objects.isNull(s) ? "" : s;
            }

            private int nullSafeInt(Integer i) {
                return Objects.isNull(i) ? 0 : i;
            }
        };
    }
}
