package dev.mock.mini;

import com.google.gson.GsonBuilder;
import dev.mock.mini.common.exception.BadRequestException;
import dev.mock.mini.common.exception.MockMiniException;
import dev.mock.mini.controller.MockExecutionController;
import dev.mock.mini.controller.MockRuleController;
import dev.mock.mini.repository.MockRuleRepository;
import dev.mock.mini.service.MockExecutionService;
import dev.mock.mini.service.MockRuleService;
import dev.voldpix.loomera.JsonProvider;
import dev.voldpix.loomera.Loomera;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class MockMiniServer {

    private Loomera app;

    private MockRuleController mockRuleController;
    private MockExecutionController mockExecutionController;

    public MockMiniServer() {
        configureServer();
    }

    public void start() {
        if (Objects.isNull(app)) {
            throw new IllegalStateException("MockMiniServer.class has not been initialized");
        }

        DatabaseManager.initialize();
        initializeInstances();
        registerShutdownHook();

        var port = Constants.PORT;
        this.app.port(port);
        this.app.start();

        log.info("Mock Mini Server started on port {}, version: {}", port, Constants.APP_VERSION);
        log.info("Dashboard UI: http://localhost:{}", port);
        log.info("Execution path: http://localhost:{}/m", port);
    }

    private void initializeInstances() {
        var mockRuleRepository = new MockRuleRepository();

        var mockRuleService = new MockRuleService(mockRuleRepository);
        var mockExecutionService = new MockExecutionService(mockRuleService);

        this.mockRuleController = new MockRuleController(mockRuleService);
        this.mockExecutionController = new MockExecutionController(mockRuleService, mockExecutionService);
    }

    private void configureServer() {
        this.app = new Loomera();
        this.app.setJsonProvider(gsonMapper());

        setupRoutes();
        setupExceptions();
    }

    private void setupRoutes() {
        app.get("/health", ctx -> ctx.result("OK"));
        app.post("/rules", ctx -> mockRuleController.createMockRule(ctx));
        app.put("/rules", ctx -> mockRuleController.updateMockRule(ctx));
        app.delete("/rules", ctx -> mockRuleController.deleteMockRule(ctx));
        app.get("/rules", ctx -> mockRuleController.getMockRules(ctx));

        String[] paths = {"/m", "/m/*"};
        for (var path : paths) {
            app.get(path, ctx -> mockExecutionController.execute(ctx));
            app.post(path, ctx -> mockExecutionController.execute(ctx));
            app.put(path, ctx -> mockExecutionController.execute(ctx));
            app.patch(path, ctx -> mockExecutionController.execute(ctx));
            app.delete(path, ctx -> mockExecutionController.execute(ctx));
        }
    }

    private void setupExceptions() {
        record ErrorResponse(String error){}

        app.exception(BadRequestException.class, (ctx, e) -> {
            ctx.setStatus(400).json(new ErrorResponse(e.getMessage()));
        });

        app.exception(MockMiniException.class, (ctx, e) -> {
            ctx.setStatus(500).json(new ErrorResponse(e.getMessage()));
        });

        app.exception(Exception.class, (ctx, e) -> {
            ctx.setStatus(500).json(new ErrorResponse(e.getMessage()));
        });
    }

    private JsonProvider gsonMapper() {
        var gson = new GsonBuilder().create();
        return new JsonProvider() {

            @Override
            public String toJson(Object o) {
                return gson.toJson(o);
            }

            @Override
            public <T> T fromJson(String s, Class<T> aClass) {
                return gson.fromJson(s, aClass);
            }
        };
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown signal received. Stopping server...");
            if (Objects.nonNull(app)) {
                app.stop();
                log.info("Server stopped");
            }

            DatabaseManager.close();
            log.info("Graceful shutdown completed");
        }, "shutdown-hook"));
        log.info("Shutdown hook registered");
    }
}
