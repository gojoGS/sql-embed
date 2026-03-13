package io.github.gojogs.sqlembed.exception;

import java.lang.reflect.Field;

/**
 * Indicates that the specified SQL classpath resource path is invalid during SQL injection.
 * This exception is thrown when an annotated field in a target class references an invalid SQL path.
 */
public final class SqlInvalidPathException extends SqlInjectionException {
    /**
     * Constructs a new {@code SqlInvalidPathException}.
     * Thrown to indicate that the provided SQL path is invalid for a specific field in the target class.
     *
     * @param targetClass the class containing the annotated field
     * @param field the field for which the SQL path is invalid
     * @param sqlPath the defective SQL path that caused the exception
     */
    public SqlInvalidPathException(Class<?> targetClass, Field field, String sqlPath) {
        super(
                "Failed SQL injection for field '"
                        + field.getName()
                        + "' in '"
                        + targetClass.getName()
                        + "': invalid SQL path '"
                        + sqlPath
                        + "'"
        );
    }
}
