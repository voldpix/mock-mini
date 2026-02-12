package dev.mock.mini.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil {

    public static String removeTrailingSlash(String requestPath) {
        if (Objects.isNull(requestPath)) return "";
        if (requestPath.length() > 1 && requestPath.endsWith("/")) {
            return requestPath.substring(0, requestPath.length() - 1);
        }
        return requestPath;
    }

    public static String extractMockRulePath(String requestPath) {
        if (Objects.isNull(requestPath)) return "/";

        var prefix = "/m";
        if (requestPath.equals(prefix)) {
            return "/";
        }

        if (requestPath.startsWith(prefix + "/")) {
            return requestPath.substring(prefix.length());
        }
        return requestPath;
    }

    public static int countWildcards(String path) {
        if (Objects.isNull(path)) return 0;
        return Math.toIntExact(path.chars().filter(ch -> ch == '*').count());
    }
}
