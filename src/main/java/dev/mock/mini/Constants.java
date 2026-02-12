package dev.mock.mini;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final String APP_VERSION = "0.0.1";

    public static final int PORT = Env.getInt("PORT", 9001);

    public static final String DB_PATH = "data/mock-mini.db";

    // app
    public static final int MAX_ID_LENGTH = 20;

    // validations
    public static final Pattern VALID_PATH_PATTERN = Pattern.compile("^/[a-zA-Z0-9/_\\-*]+$");
    public static final Set<String> VALID_HTTP_METHODS = Set.of("GET", "POST", "PUT", "DELETE", "PATCH");
    public static final int MAX_PATH_LENGTH = 250;
    public static final int MAX_QUERY_STRING_LENGTH = 250;
    public static final int MAX_BODY_LENGTH = 5_000;
    public static final int MAX_WILDCARDS = 3;
    public static final int MAX_HEADERS_SIZE = 5;
    public static final int MAX_HEADER_KEY_LENGTH = Env.getInt("MBD_VALIDATION_MOCK_MAX_HEADER_KEY_LENGTH", 100);
    public static final int MAX_HEADER_VALUE_LENGTH = Env.getInt("MBD_VALIDATION_MOCK_MAX_HEADER_VALUE_LENGTH", 500);
    public static final int MAX_ALLOWED_DELAY = Env.getInt("MBD_VALIDATION_MOCK_MAX_ALLOWED_DELAY", 30_000);

}
