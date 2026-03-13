package io.github.gojogs.sqlembed.exception;

/**
 * Indicates that an annotated field is not injectable due to invalid modifiers.
 */
public final class SqlFieldNotInjectableException extends SqlInjectionException {
    /**
     * Creates a non-injectable field exception.
     *
     * @param className target class name
     * @param fieldName annotated field name
     * @param sqlPath normalized SQL classpath resource path
     * @param reason human-readable reason
     */
    public SqlFieldNotInjectableException(String className, String fieldName, String sqlPath, String reason) {
        super(
            "Failed SQL injection for field '"
                + fieldName
                + "' in '"
                + className
                + "': classpath resource '"
                + sqlPath
                + "' cannot be injected because "
                + reason
        );
    }
}
