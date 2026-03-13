package io.github.gojogs.sqlembed.exception;

import java.lang.reflect.Field;

/**
 * Indicates that an annotated field is not injectable due to invalid modifiers.
 */
public final class SqlFieldNotInjectableException extends SqlInjectionException {
    /**
     * Creates a non-injectable field exception.
     *
     * @param targetClass target class
     * @param field annotated field name
     * @param sqlPath normalized SQL classpath resource path
     * @param reason human-readable reason
     */
    public SqlFieldNotInjectableException(Class<?> targetClass, Field field, String sqlPath, String reason) {
        super(
            "Failed SQL injection for field '"
                + field.getName()
                + "' in '"
                + targetClass.getName()
                + "': classpath resource '"
                + sqlPath
                + "' cannot be injected because "
                + reason
        );
    }
}
