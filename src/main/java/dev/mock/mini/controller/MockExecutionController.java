package dev.mock.mini.controller;

import dev.mock.mini.common.util.StringUtil;
import dev.mock.mini.service.MockExecutionService;
import dev.mock.mini.service.MockRuleService;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockExecutionController {

    private final MockRuleService mockRuleService;
    private final MockExecutionService mockExecutionService;

    public MockExecutionController(MockRuleService mockRuleService, MockExecutionService mockExecutionService) {
        this.mockRuleService = mockRuleService;
        this.mockExecutionService = mockExecutionService;
    }

    public void execute(Context ctx) {
        var method = ctx.method();
        var mockPath = StringUtil.extractMockRulePath(ctx.path());
        var result = mockExecutionService.executeRule(method.name(), mockPath);
        ctx.status(200).json(result);
    }
}
