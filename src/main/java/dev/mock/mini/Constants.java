package dev.mock.mini;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final String APP_VERSION = "0.0.1";

    public static final int PORT = Env.getInt("PORT", 9001);

    public static final String DB_PATH = "data/mock-mini.db";
}
