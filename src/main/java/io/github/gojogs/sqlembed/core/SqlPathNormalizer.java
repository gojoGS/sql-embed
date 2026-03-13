package io.github.gojogs.sqlembed.core;

import io.github.gojogs.sqlembed.exception.SqlInvalidPathException;

import java.lang.reflect.Field;

final class SqlPathNormalizer {
    String normalize(String rawPath, Class<?> targetClass, Field field) {
        if (rawPath == null) {
            throw new SqlInvalidPathException(targetClass, field, "null");
        }

        String normalizedPath = rawPath.replace('\\', '/');
        while (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }

        if (normalizedPath.trim().isEmpty()) {
            throw new SqlInvalidPathException(targetClass, field, rawPath);
        }

        return normalizedPath;
    }
}
