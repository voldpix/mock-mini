package dev.mock.mini.common.mapper;

import dev.mock.mini.common.dto.MockRuleDto;
import dev.mock.mini.repository.model.MockRule;

public class MockRuleMapper {

    @SuppressWarnings("java:S4144")
    public MockRule toEntity(MockRuleDto dto) {
        var mockRule = new MockRule();
        mockRule.setId(dto.getId());
        mockRule.setMethod(dto.getMethod());
        mockRule.setPath(dto.getPath());
        mockRule.setHeaders(dto.getHeaders());
        mockRule.setBody(dto.getBody());
        mockRule.setStatusCode(dto.getStatusCode());
        mockRule.setDelay(dto.getDelay());
        mockRule.setCreated(dto.getCreated());
        return mockRule;
    }

    @SuppressWarnings("java:S4144")
    public MockRuleDto toDto(MockRule mockRule) {
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
