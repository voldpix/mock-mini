package dev.mock.mini;

import com.google.gson.GsonBuilder;
import dev.mock.mini.common.exception.BadRequestException;
import dev.mock.mini.controller.MockExecutionController;
import dev.mock.mini.controller.MockRuleController;
import dev.mock.mini.repository.MockRuleRepository;
import dev.mock.mini.service.MockExecutionService;
import dev.mock.mini.service.MockRuleService;
import io.javalin.Javalin;
import io.javalin.http.HandlerType;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JsonMapper;
import io.javalin.plugin.bundled.CorsPluginConfig;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;

@Slf4j
public class MockMiniServer {

    private Javalin app;

    private MockRuleService mockRuleService;
    private MockExecutionService mockExecutionService;

    private MockRuleController mockRuleController;
    private MockExecutionController mockExecutionController;

    public MockMiniServer() {
        configureServer();
    }

    public void start() {
        if (Objects.isNull(app)) {
            throw new IllegalStateException("MockMiniServer.class has not been initialized");
        }

        Database.initialize();
        initializeInstances();
        registerShutdownHook();

        var port = Constants.PORT;
        this.app.start(port);

        log.info("Mock Mini Server started on port {}, version: {}", port, Constants.APP_VERSION);
        log.info("Dashboard UI: http://localhost:{}", port);
        log.info("Execution path: http://localhost:{}/m", port);
    }

    private void initializeInstances() {
        var mockRuleRepository = new MockRuleRepository();

        this.mockRuleService = new MockRuleService(mockRuleRepository);
        this.mockExecutionService = new MockExecutionService(mockRuleService);

        this.mockRuleController = new MockRuleController(mockRuleService);
        this.mockExecutionController = new MockExecutionController(mockRuleService, mockExecutionService);
    }

    private void configureServer() {
        this.app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.useVirtualThreads = true;

            config.jsonMapper(gsonMapper());

            config.staticFiles.add("/static", Location.CLASSPATH);
            config.bundledPlugins.enableCors(cors -> cors.addRule(CorsPluginConfig.CorsRule::anyHost));
        });

        setupExceptions();
        setupRoutes();
    }

    private void setupExceptions() {
        var errorMap = new HashMap<String, Object>();
        app.exception(BadRequestException.class, (e, ctx) -> {
            errorMap.put("error", e.getMessage());
            ctx.status(400).json(errorMap);
        });
    }

    private void setupRoutes() {
        app.get("/health", ctx -> ctx.result("OK"));

        app.post("/rules", ctx -> mockRuleController.createMockRule(ctx));
        app.put("/rules/{id}", ctx -> mockRuleController.updateMockRule(ctx));
        app.get("/rules", ctx -> mockRuleController.getMockRules(ctx));
        app.delete("/rules/{id}", ctx -> mockRuleController.deleteMockRule(ctx));

        setupMockExecutionRoutes();
    }

    // workaround
    private void setupMockExecutionRoutes() {
        String[] paths = {"/m", "/m/*"};
        HandlerType[] methods = {
                HandlerType.GET,
                HandlerType.POST,
                HandlerType.PUT,
                HandlerType.PATCH,
                HandlerType.DELETE
        };

        for (var path : paths) {
            for (var method : methods) {
                app.addHttpHandler(method, path, ctx -> mockExecutionController.execute(ctx));
            }
        }
    }

    private JsonMapper gsonMapper() {
        var gson = new GsonBuilder().create();
        return new JsonMapper() {
            @NotNull
            @Override
            public String toJsonString(@NotNull Object obj, @NotNull Type type) {
                return gson.toJson(obj);
            }

            @NotNull
            @Override
            public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                return gson.fromJson(json, targetType);
            }
        };
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown signal received. Stopping server...");
            if (Objects.nonNull(app)) {
                app.stop();
                log.info("Javalin server stopped");
            }

            Database.close();
            log.info("Graceful shutdown completed");
        }, "shutdown-hook"));
        log.info("Shutdown hook registered");
    }
}
