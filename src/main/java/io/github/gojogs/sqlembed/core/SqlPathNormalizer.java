package io.github.gojogs.sqlembed.core;

import io.github.gojogs.sqlembed.exception.SqlInjectionException;

final class SqlPathNormalizer {
    String normalize(String rawPath, Class<?> targetClass, String fieldName) {
        if (rawPath == null) {
            throw invalidPathException(targetClass, fieldName, "null");
        }

        String normalizedPath = rawPath.replace('\\', '/');
        while (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }

        if (normalizedPath.trim().isEmpty()) {
            throw invalidPathException(targetClass, fieldName, rawPath);
        }

        return normalizedPath;
    }

    private SqlInjectionException invalidPathException(Class<?> targetClass, String fieldName, String sqlPath) {
        return new SqlInjectionException(
            "Failed SQL injection for field '"
                + fieldName
                + "' in '"
                + targetClass.getName()
                + "': invalid SQL path '"
                + sqlPath
                + "'"
        );
    }
}
