package dev.mock.mini.common.dto;

import dev.mock.mini.Constants;
import dev.mock.mini.common.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MockRuleDto implements Serializable {
    private String id;
    private String method;
    private String path;
    private String headers;
    private String body;
    private int statusCode;
    private int delay;
    private Timestamp created;

    // pre-baked fields
    private transient Pattern compiledPattern;
    private transient Integer wildcardCount;
    private transient Integer pathLength;

    public void compilePattern() {
        if (Objects.isNull(this.path)) return;

        var escaped = escapePath(this.path);
        this.compiledPattern = Pattern.compile("^" + escaped + "$");
        this.wildcardCount = StringUtil.countWildcards(this.path);
        this.pathLength = this.path.length();
    }

    public boolean matches(String requestPath) {
        if (Objects.isNull(requestPath)) return false;
        if (Objects.isNull(this.compiledPattern)) {
            throw new IllegalStateException("Pattern not compiled. Call compilePattern() first.");
        }
        return this.compiledPattern.matcher(requestPath).matches();
    }

    private String escapePath(String requestPath) {
        var builder = new StringBuilder();
        int start = 0;

        for (int i = 0; i < requestPath.length(); i++) {
            if (requestPath.charAt(i) == '*') {
                if (i > start) {
                    builder.append(Pattern.quote(requestPath.substring(start, i)));
                }
                builder.append("[^/]+");
                start = i + 1;
            }
        }

        if (start < requestPath.length()) {
            builder.append(Pattern.quote(requestPath.substring(start)));
        }
        return builder.toString();
    }
}
