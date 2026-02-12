package dev.mock.mini.cache;

import dev.mock.mini.Constants;
import dev.mock.mini.common.dto.MockRuleDto;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class MockRuleCache {

    private List<MockRuleDto> mockRules = new ArrayList<>(Constants.MAX_MOCK_RULES);

    public MockRuleCache() {
        log.info("MockRuleCache initialized");
    }

    public void addMockRules(List<MockRuleDto> mockRules) {
        this.mockRules = mockRules.stream()
                .sorted(Comparator.comparing(MockRuleDto::getCreated).reversed())
                .toList();
    }

    public List<MockRuleDto> getMockRules() {
        return mockRules.isEmpty() ? Collections.emptyList() : mockRules;
    }

    public void clear() {
        this.mockRules = new ArrayList<>(Constants.MAX_MOCK_RULES);
    }
}
