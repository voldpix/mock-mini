package dev.mock.mini;

import com.google.gson.GsonBuilder;
import dev.mock.mini.common.exception.MockMiniException;
import dev.mock.mini.controller.MockExecutionController;
import dev.mock.mini.controller.MockRuleController;
import dev.mock.mini.repository.MockRuleRepository;
import dev.mock.mini.service.MockExecutionService;
import dev.mock.mini.service.MockRuleService;
import dev.voldpix.loomera.Loomera;
import dev.voldpix.loomera.json.JsonProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
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
        try {
            this.app.start();
        } catch (IOException e) {
            throw new MockMiniException("Exception starting Mock Mini", e);
        }

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
        this.app.jsonProvider(gsonMapper());

        setupRoutes();
    }

    private void setupRoutes() {
        app.at("/health").get().handle(ctx -> ctx.result("OK"));
        app.at("/rules").post().handle(ctx -> mockRuleController.createMockRule(ctx));
        app.at("/rules/:id").put().handle(ctx -> mockRuleController.updateMockRule(ctx));
        app.at("/rules/:id").delete().handle(ctx -> mockRuleController.deleteMockRule(ctx));
        app.at("/rules").get().handle(ctx -> mockRuleController.getMockRules(ctx));

        String[] paths = {"/m", "/m/*"};
        for (var path : paths) {
            app.at(path).get().handle(ctx -> mockExecutionController.execute(ctx));
            app.at(path).post().handle(ctx -> mockExecutionController.execute(ctx));
            app.at(path).put().handle(ctx -> mockExecutionController.execute(ctx));
            app.at(path).patch().handle(ctx -> mockExecutionController.execute(ctx));
            app.at(path).delete().handle(ctx -> mockExecutionController.execute(ctx));
        }
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

            @Override
            public <T> T fromJson(String s, Type type) {
                return gson.fromJson(s, type);
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
