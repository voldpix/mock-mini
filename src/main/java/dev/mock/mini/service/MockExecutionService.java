package dev.mock.mini.service;

import dev.mock.mini.common.dto.MockRuleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class MockExecutionService {

    private final MockRuleService mockRuleService;

    public Object executeRule(String method, String path) {
        var rule = findMatchingRule(method, path);

        return rule;
    }

    private MockRuleDto findMatchingRule(String method, String path) {
        var rules = mockRuleService.findAll();
        if (rules.isEmpty()) {
            return null;
        }

        return rules.stream()
                .filter(r -> r.getMethod().equals(method))
                .filter(r -> r.getPath().equals(path))
                .min((r1, r2) -> {
                    if (!Objects.equals(r1.getWildcardCount(), r2.getWildcardCount())) {
                        return Integer.compare(r1.getWildcardCount(), r2.getWildcardCount());
                    }
                    return Integer.compare(r2.getPathLength(), r1.getPathLength());
                }).orElse(null);
    }
}
