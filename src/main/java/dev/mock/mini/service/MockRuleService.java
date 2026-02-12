package dev.mock.mini.service;

import dev.mock.mini.Constants;
import dev.mock.mini.cache.MockRuleCache;
import dev.mock.mini.common.dto.IdResponse;
import dev.mock.mini.common.dto.MockRuleDto;
import dev.mock.mini.common.exception.BadRequestException;
import dev.mock.mini.common.util.IdGenerator;
import dev.mock.mini.common.util.StringUtil;
import dev.mock.mini.common.validation.MockRuleValidator;
import dev.mock.mini.repository.MockRuleRepository;
import dev.mock.mini.repository.model.MockRule;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MockRuleService {

    private final MockRuleRepository mockRuleRepository;
    private final MockRuleCache mockRuleCache;

    public MockRuleService(MockRuleRepository mockRuleRepository) {
        this.mockRuleRepository = mockRuleRepository;
        this.mockRuleCache = new MockRuleCache();
        log.info("MockRuleService initialized");
    }

    public IdResponse createMockRule(MockRuleDto mockRuleDto) {
        var persistedRules = getAll();
        if (persistedRules.size() >= Constants.MAX_MOCK_RULES) {
            throw new BadRequestException("Reached max number of Mock Rules (max: " + Constants.MAX_MOCK_RULES + ")");
        }
        var method = mockRuleDto.getMethod().toUpperCase();
        var path = StringUtil.removeTrailingSlash(mockRuleDto.getPath().toLowerCase());
        var duplicate = persistedRules.stream()
                .filter(Objects::nonNull)
                .filter(m -> m.getMethod().equals(method))
                .filter(m -> m.getPath().equals(path))
                .findFirst()
                .orElse(null);
        if (Objects.nonNull(duplicate)) {
            throw new BadRequestException("Mock Rule already exists by method and path");
        }

        var mockRuleId = IdGenerator.generateId();
        var mockRule = new MockRule();
        mockRule.setId(mockRuleId);
        mockRule.setMethod(method);
        mockRule.setPath(path);
        mockRule.setHeaders(mockRuleDto.getHeaders());
        mockRule.setBody(mockRuleDto.getBody());
        mockRule.setStatusCode(mockRuleDto.getStatusCode());
        mockRule.setDelay(mockRuleDto.getDelay());
        mockRule.setCreated(Timestamp.valueOf(LocalDateTime.now()));

        mockRuleRepository.createRule(mockRule);
        mockRuleCache.clear();

        log.info("Mock rule successfully created: {}", mockRule.getId());
        return new IdResponse(mockRuleId);
    }

    public IdResponse updateMockRule(String mockRuleId, MockRuleDto mockRuleDto) {
        var mockRule = getAll().stream()
                .filter(Objects::nonNull)
                .filter(m -> m.getId().equals(mockRuleId)).findFirst()
                .orElseThrow(() -> new BadRequestException("Unable to find mock rule with id: " + mockRuleId));

        mockRule.setMethod(mockRuleDto.getMethod().toUpperCase());
        mockRule.setPath(StringUtil.removeTrailingSlash(mockRuleDto.getPath().toLowerCase()));
        mockRule.setHeaders(mockRuleDto.getHeaders());
        mockRule.setBody(mockRuleDto.getBody());
        mockRule.setStatusCode(mockRuleDto.getStatusCode());
        mockRule.setDelay(mockRuleDto.getDelay());
        mockRuleRepository.updateRule(mockRuleId, mockRule);
        mockRuleCache.clear();

        log.info("Mock rule successfully updated: {}", mockRuleId);
        return new IdResponse(mockRuleId);
    }

    public List<MockRuleDto> findAll() {
        var cachedList = mockRuleCache.getMockRules();
        if (Objects.nonNull(cachedList) && !cachedList.isEmpty()) {
            return cachedList;
        }

        var list = getAll().stream()
                .map(m -> {
                    var dto = toDto(m);
                    dto.compilePattern();
                    return dto;
                })
                .toList();
        mockRuleCache.addMockRules(list);
        return list;
    }

    public void deleteMockRule(String mockRuleId) {
        mockRuleRepository.deleteMockRule(mockRuleId);
        mockRuleCache.clear();
        log.info("Mock rule successfully deleted: {}", mockRuleId);
    }

    private List<MockRule> getAll() {
        return mockRuleRepository.findAll();
    }

    private MockRuleDto toDto(MockRule mockRule) {
        var mockRuleDto = new MockRuleDto();
        mockRuleDto.setId(mockRule.getId());
        mockRuleDto.setMethod(mockRule.getMethod());
        mockRuleDto.setPath(mockRule.getPath());
        mockRuleDto.setHeaders(mockRule.getHeaders());
        mockRuleDto.setBody(mockRule.getBody());
        mockRuleDto.setStatusCode(mockRule.getStatusCode());
        mockRuleDto.setDelay(mockRule.getDelay());
        mockRuleDto.setCreated(mockRule.getCreated());
        return mockRuleDto;
    }
}
