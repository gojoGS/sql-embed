package io.github.gojogs.sqlembed.core;

import io.github.gojogs.sqlembed.annotation.SqlInject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

final class SqlFieldScanner {
    List<Field> scanAnnotatedFields(Class<?> targetClass) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = targetClass;

        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(SqlInject.class)) {
                    fields.add(field);
                }
            }
            current = current.getSuperclass();
        }

        return fields;
    }
}
