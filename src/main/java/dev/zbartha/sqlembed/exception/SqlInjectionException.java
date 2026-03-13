package dev.zbartha.sqlembed.exception;

/**
 * Base runtime exception for SQL injection failures.
 */
public class SqlInjectionException extends RuntimeException {
    /**
     * Creates an exception with a message.
     *
     * @param message failure description
     */
    public SqlInjectionException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a message and cause.
     *
     * @param message failure description
     * @param cause root cause
     */
    public SqlInjectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
