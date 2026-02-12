package dev.mock.mini;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// for native image configs generation: src/main/resources/META-INF/native-image
public class EndpointTestRunner {

    private static final String SERVER_URL = "http://localhost:9001";

    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            call(client, "GET", SERVER_URL + "/health", null);
            var createResult = call(client, "POST", SERVER_URL + "/mock-rules", """
                    {
                      "method": "GET",
                      "path": "/api/path",
                      "statusCode": "200",
                      "body": "{\\"browsers\\":{\\"firefox\\":{\\"name\\":\\"Firefox\\",\\"pref_url\\":\\"about:config\\"}}}"
                    }
                    """);
            var createdId = extractId(createResult);
            var updateResult = call(client, "PUT", SERVER_URL + "/mock-rules/" + createdId, """
                    {
                      "method": "POST",
                      "path": "/api/path",
                      "statusCode": "200",
                      "body": "{}"
                    }
                    """);
            var updatedId = extractId(updateResult);
            System.out.println("IDs match: " + updatedId.equals(createdId));

            call(client, "GET", SERVER_URL + "/mock-rules", null);
            call(client, "DELETE", SERVER_URL + "/mock-rules/" + updatedId, null);
        }
    }

    private static String call(HttpClient client, String method, String url, String body) throws IOException, InterruptedException {
        var builder = HttpRequest.newBuilder().uri(URI.create(url));
        switch (method) {
            case "POST" -> builder.POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json");
            case "PUT" -> builder.PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json");
            case "DELETE" -> builder.DELETE();
            case null, default -> builder.GET();
        }

        var response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        System.out.println(method + " " + url + " -> " + response.statusCode());
        return response.body();
    }

    private static String extractId(String json) {
        var obj = gson.fromJson(json, JsonObject.class);
        return obj.get("id").getAsString();
    }
}
