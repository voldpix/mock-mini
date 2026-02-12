package dev.mock.mini.service;

import dev.mock.mini.common.dto.IdResponse;
import dev.mock.mini.common.dto.MockRuleDto;
import dev.mock.mini.common.exception.BadRequestException;
import dev.mock.mini.common.mapper.MockRuleMapper;
import dev.mock.mini.common.util.IdGenerator;
import dev.mock.mini.common.validation.MockRuleValidator;
import dev.mock.mini.repository.MockRuleRepository;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MockRuleService {

    private final MockRuleRepository mockRuleRepository;
    private final MockRuleMapper mockRuleMapper;
    private final MockRuleValidator mockRuleValidator;

    public MockRuleService(MockRuleRepository mockRuleRepository) {
        this.mockRuleRepository = mockRuleRepository;
        this.mockRuleMapper = new MockRuleMapper();
        this.mockRuleValidator = new MockRuleValidator();
        log.info("MockRuleService initialized");
    }

    public IdResponse createMockRule(MockRuleDto mockRuleDto) {
        mockRuleValidator.validateMockRule(mockRuleDto);

        var mockRule = mockRuleMapper.toEntity(mockRuleDto);
        var mockRuleId = IdGenerator.generateId();
        mockRule.setId(mockRuleId);
        mockRule.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        mockRuleRepository.createRule(mockRule);

        log.info("Mock rule successfully created: {}", mockRule.getId());
        return new IdResponse(mockRuleId);
    }

    public IdResponse updateMockRule(String mockRuleId, MockRuleDto mockRuleDto) {
        if (Objects.isNull(mockRuleId) || mockRuleId.isBlank()) {
            throw new BadRequestException("id is null or blank");
        }

        mockRuleValidator.validateMockRule(mockRuleDto);

        // todo: later use cache
        findAll().stream()
                .filter(Objects::nonNull)
                .filter(m -> m.getId().equals(mockRuleId)).findFirst()
                .orElseThrow(() -> new BadRequestException("Unable to find mock rule with id: " + mockRuleId));

        var mockRule = mockRuleMapper.toEntity(mockRuleDto);
        mockRuleRepository.updateRule(mockRuleId, mockRule);
        log.info("Mock rule successfully updated: {}", mockRuleId);
        return new IdResponse(mockRuleId);
    }

    public List<MockRuleDto> findAll() {
        return mockRuleRepository.findAll()
                .stream()
                .map(mockRuleMapper::toDto).toList();
    }

    public void deleteMockRule(String mockRuleId) {
        if (Objects.isNull(mockRuleId) || mockRuleId.isBlank()) {
            throw new BadRequestException("id is null or blank");
        }

        mockRuleRepository.deleteMockRule(mockRuleId);
        log.info("Mock rule successfully deleted: {}", mockRuleId);
    }
}
