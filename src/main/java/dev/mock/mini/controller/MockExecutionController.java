package dev.mock.mini.controller;

import dev.mock.mini.common.util.StringUtil;
import dev.mock.mini.service.MockExecutionService;
import dev.mock.mini.service.MockRuleService;
import dev.voldpix.loomera.context.RequestContext;
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
        var method = ctx.method();
        var wildcardPath = ctx.wildcardPath();
        log.info("REQ: {}-{}", method, wildcardPath);
        var result = mockExecutionService.executeRule(method, wildcardPath);
        ctx.status(200).json(result);
    }
}
