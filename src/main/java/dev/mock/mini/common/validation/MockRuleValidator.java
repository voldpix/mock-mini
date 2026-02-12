package dev.mock.mini.common.validation;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dev.mock.mini.Constants;
import dev.mock.mini.common.dto.MockRuleDto;
import dev.mock.mini.common.exception.BadRequestException;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import static dev.mock.mini.Constants.*;

public class MockRuleValidator {

    private final Gson gson = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();

    public void validateMockRule(MockRuleDto dto) {
        validateMethod(dto.getMethod());
        validatePath(dto.getPath());
        validateHeaders(dto.getHeaders());
        validateBody(dto.getBody());
        validateStatusCode(dto.getStatusCode());
        validateDelay(dto.getDelay());
    }

    private void validatePath(String path) {
        if (Objects.isNull(path) || path.isBlank()) {
            throw new BadRequestException("Path cannot be empty");
        }

        if (!path.startsWith("/")) {
            throw new BadRequestException("Path must start with /");
        }

        if (path.length() > MAX_PATH_LENGTH) {
            throw new BadRequestException("Path exceeds maximum length of " + MAX_PATH_LENGTH);
        }

        if (!VALID_PATH_PATTERN.matcher(path).matches()) {
            throw new BadRequestException("Path contains invalid characters. Allowed: a-z, A-Z, 0-9, /, _, -, *");
        }

        long wildcardCount = path.chars().filter(ch -> ch == '*').count();
        if (wildcardCount > MAX_WILDCARDS) {
            throw new BadRequestException("Path cannot have more than " + MAX_WILDCARDS + " wildcards");
        }

        if (path.contains("**")) {
            throw new BadRequestException("Adjacent wildcards (**) not allowed");
        }
    }

    private void validateMethod(String method) {
        if (Objects.isNull(method) || method.isBlank()) {
            throw new BadRequestException("HTTP method cannot be empty");
        }

        if (!VALID_HTTP_METHODS.contains(method)) {
            throw new BadRequestException("Invalid HTTP method: " + method);
        }
    }

    private void validateBody(String body) {
        if (Objects.isNull(body) || body.isBlank()) {
            return;
        }

        try {
            JsonParser.parseString(body);
        } catch (Exception e) {
            throw new BadRequestException("Body must be valid JSON string");
        }

        if (body.length() > MAX_BODY_LENGTH) {
            throw new BadRequestException("Body too large (max " +  (MAX_BODY_LENGTH / 1000) + "KB)");
        }
    }

    private void validateHeaders(String headers) {
        if (Objects.isNull(headers) || headers.isBlank()) {
            return;
        }

        try {
            var element = JsonParser.parseString(headers);
            if (!element.isJsonObject()) {
                throw new BadRequestException("Headers must be a JSON object");
            }

            var jsonObject = element.getAsJsonObject();
            if (jsonObject.size() > MAX_HEADERS_SIZE) {
                throw new BadRequestException("Too many headers (max " + MAX_HEADERS_SIZE + " allowed)");
            }
            for (var entry : jsonObject.entrySet()) {
                var key = entry.getKey();
                var valueElement = entry.getValue();

                if (!valueElement.isJsonPrimitive() || !valueElement.getAsJsonPrimitive().isString()) {
                    throw new BadRequestException("Header values must be strings");
                }

                var value = valueElement.getAsString();
                if (key.length() > MAX_HEADER_KEY_LENGTH || value.length() > MAX_HEADER_VALUE_LENGTH) {
                    throw new BadRequestException("Header key or value too long");
                }
            }
        } catch (Exception e) {
            throw new BadRequestException("Headers must be valid JSON string", e);
        }
    }

    private void validateStatusCode(Integer statusCode) {
        if (Objects.isNull(statusCode)) {
            throw new BadRequestException("Status code cannot be empty");
        }
        if (statusCode < 100 || statusCode > 599) {
            throw new BadRequestException("Invalid HTTP status code: " + statusCode);
        }
    }

    private void validateDelay(Integer delay) {
        if (Objects.isNull(delay)) return;

        if (delay < 0 || delay > Constants.MAX_ALLOWED_DELAY) {
            throw new IllegalArgumentException("Delay must be in the range 0..." + Constants.MAX_ALLOWED_DELAY);
        }
    }
}
