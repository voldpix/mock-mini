package dev.mock.mini.common.exception;

public class MockMiniException extends RuntimeException {

    public MockMiniException(String message) {
        super(message);
    }

    public MockMiniException(String message, Throwable cause) {
        super(message, cause);
    }
}
