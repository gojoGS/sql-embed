package dev.zbartha.sqlembed.exception;

/**
 * Indicates that an annotated field is not of type {@link String}.
 */
public final class SqlFieldTypeMismatchException extends SqlInjectionException {
    /**
     * Creates a field type mismatch exception.
     *
     * @param className target class name
     * @param fieldName annotated field name
     * @param sqlPath normalized SQL classpath resource path
     * @param actualType actual field type name
     */
    public SqlFieldTypeMismatchException(String className, String fieldName, String sqlPath, String actualType) {
        super(
            "Failed SQL injection for field '"
                + fieldName
                + "' in '"
                + className
                + "': classpath resource '"
                + sqlPath
                + "' requires field type 'java.lang.String' but was '"
                + actualType
                + "'"
        );
    }
}
