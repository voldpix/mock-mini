package dev.mock.mini.controller;

import dev.mock.mini.common.dto.MockRuleDto;
import dev.mock.mini.common.exception.BadRequestException;
import dev.mock.mini.common.validation.MockRuleValidator;
import dev.mock.mini.service.MockRuleService;
import dev.voldpix.loomera.context.RequestContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockRuleController {

    private final MockRuleService mockRuleService;
    private final MockRuleValidator mockRuleValidator;

    public MockRuleController(MockRuleService mockRuleService) {
        this.mockRuleService = mockRuleService;
        this.mockRuleValidator = new MockRuleValidator();
    }

    public void getMockRules(RequestContext ctx) {
        var mockRules = mockRuleService.findAll();
        ctx.json(mockRules);
    }

    public void createMockRule(RequestContext ctx) {
        var mockRuleDto = ctx.bodyAsClass(MockRuleDto.class);
        mockRuleValidator.validateMockRule(mockRuleDto);

        var result = mockRuleService.createMockRule(mockRuleDto);
        ctx.status(201).json(result);
    }

    public void updateMockRule(RequestContext ctx) {
        var mockRuleId = ctx.param("id");
        if (mockRuleId.isBlank()) {
            throw new BadRequestException("Mock rule id is required");
        }

        var mockRuleDto = ctx.bodyAsClass(MockRuleDto.class);
        mockRuleValidator.validateMockRule(mockRuleDto);

        var result = mockRuleService.updateMockRule(mockRuleId, mockRuleDto);
        ctx.status(200).json(result);
    }

    public void deleteMockRule(RequestContext ctx) {
        var mockRuleId = ctx.param("id");
        if (mockRuleId.isBlank()) {
            throw new BadRequestException("id is null or blank");
        }

        mockRuleService.deleteMockRule(mockRuleId);
        ctx.status(204);
    }
}
