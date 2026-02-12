package dev.mock.mini.common.util;

import dev.mock.mini.Constants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdGenerator {

    public static String generateId() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, Constants.MAX_ID_LENGTH);
    }
}
