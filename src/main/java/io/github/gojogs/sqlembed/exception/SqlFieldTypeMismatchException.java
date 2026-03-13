package io.github.gojogs.sqlembed.exception;

import java.lang.reflect.Field;

/**
 * Indicates that an annotated field is not of type {@link String}.
 */
public final class SqlFieldTypeMismatchException extends SqlInjectionException {
    /**
     * Creates a field type mismatch exception.
     *
     * @param targetClass target class
     * @param field       annotated field
     * @param sqlPath     normalized SQL classpath resource path
     */
    public SqlFieldTypeMismatchException(Class<?> targetClass, Field field, String sqlPath) {
        super(
            "Failed SQL injection for field '"
                + field.getName()
                + "' in '"
                + targetClass.getName()
                + "': classpath resource '"
                + sqlPath
                + "' requires field type 'java.lang.String' but was '"
                + field.getType().getName()
                + "'"
        );
    }
}
