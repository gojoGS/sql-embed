package io.github.gojogs.sqlembed.core;

import io.github.gojogs.sqlembed.exception.SqlFieldNotInjectableException;
import io.github.gojogs.sqlembed.exception.SqlFieldTypeMismatchException;
import io.github.gojogs.sqlembed.exception.SqlInjectionException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

final class SqlFieldInjector {
    void validateInjectableField(Field field, Class<?> targetClass, String sqlPath) {
        if (Modifier.isStatic(field.getModifiers())) {
            throw new SqlFieldNotInjectableException(targetClass, field, sqlPath, "the field is static");
        }

        if (Modifier.isFinal(field.getModifiers())) {
            throw new SqlFieldNotInjectableException(targetClass, field, sqlPath, "the field is final");
        }

        if (field.getType() != String.class) {
            throw new SqlFieldTypeMismatchException(targetClass, field, sqlPath);
        }
    }

    void injectSqlText(Object target, Field field, String sqlText, String sqlPath, Class<?> targetClass) {
        try {
            field.setAccessible(true);
            field.set(target, sqlText);
        } catch (IllegalAccessException | RuntimeException ex) {
            throw new SqlInjectionException(
                "Failed SQL injection for field '"
                    + field.getName()
                    + "' in '"
                    + targetClass.getName()
                    + "': failed assigning SQL text from classpath resource '"
                    + sqlPath
                    + "'",
                ex
            );
        }
    }
}
