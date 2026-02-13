package dev.mock.mini.controller;

import dev.mock.mini.service.MockExecutionService;
import dev.mock.mini.service.MockRuleService;
import dev.voldpix.loomera.RequestContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockExecutionController {

    private final MockRuleService mockRuleService;
    private final MockExecutionService mockExecutionService;

    public MockExecutionController(MockRuleService mockRuleService, MockExecutionService mockExecutionService) {
        this.mockRuleService = mockRuleService;
        this.mockExecutionService = mockExecutionService;
    }

    public void execute(RequestContext ctx) {
        var method = ctx.getMethod();
        var wildcardPath = ctx.getWildcardPath();
        log.info("REQ: {}-{}", method, wildcardPath);
        var result = mockExecutionService.executeRule(method, wildcardPath);
        ctx.setStatus(200).json(result);
    }
}
