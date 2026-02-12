package dev.mock.mini;

import java.time.ZoneId;
import java.util.TimeZone;

public class MockMiniApp {

    public static void main( String[] args ) {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));
        new MockMiniServer().start();
    }
}
