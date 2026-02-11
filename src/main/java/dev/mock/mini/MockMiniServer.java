package dev.mock.mini;

import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JsonMapper;
import io.javalin.plugin.bundled.CorsPluginConfig;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Objects;

@Slf4j
public class MockMiniServer {

    private Javalin app;

    public MockMiniServer() {
        buildServer();
    }

    public void start() {
        if (Objects.isNull(app)) {
            throw new IllegalStateException("MockMiniServer.class has not been initialized");
        }

        int port = Constants.PORT;
        this.app.start(port);

        log.info("Mock Mini Server started on port {}, version: {}", port, Constants.APP_VERSION);
        log.info("Dashboard UI: http://localhost:{}", port);
        log.info("Execution path: http://localhost:{}/m", port);
    }

    private void buildServer() {
        this.app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.useVirtualThreads = true;

            config.jsonMapper(gsonMapper());

            config.staticFiles.add("/static", Location.CLASSPATH);
            config.bundledPlugins.enableCors(cors -> cors.addRule(CorsPluginConfig.CorsRule::anyHost));
        });

        setupRoutes();
    }

    private void setupRoutes() {
        app.get("/health", ctx -> ctx.result("OK"));

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
}
