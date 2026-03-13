package io.github.gojogs.sqlembed.exception;

/**
 * Indicates that an annotated SQL classpath resource could not be found.
 */
public final class SqlResourceNotFoundException extends SqlInjectionException {
    /**
     * Creates a missing resource exception.
     *
     * @param className target class name
     * @param fieldName annotated field name
     * @param sqlPath normalized SQL classpath resource path
     */
    public SqlResourceNotFoundException(String className, String fieldName, String sqlPath) {
        super(
            "Failed SQL injection for field '"
                + fieldName
                + "' in '"
                + className
                + "': classpath resource '"
                + sqlPath
                + "' not found"
        );
    }
}
