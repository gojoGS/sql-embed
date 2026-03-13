package io.github.gojogs.sqlembed.core;

import io.github.gojogs.sqlembed.exception.SqlInjectionException;
import io.github.gojogs.sqlembed.exception.SqlResourceNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

final class ClasspathSqlResourceLoader {
    String loadSql(Class<?> targetClass, String fieldName, String sqlPath, Charset charset) {
        ClassLoader effectiveClassLoader = resolveResourceClassLoader(targetClass, fieldName, sqlPath);
        return loadSql(effectiveClassLoader, targetClass, fieldName, sqlPath, charset);
    }

    String loadSql(ClassLoader classLoader, Class<?> targetClass, String fieldName, String sqlPath, Charset charset) {
        InputStream inputStream = getResourceAsStream(classLoader, sqlPath);
        if (inputStream == null) {
            throw new SqlResourceNotFoundException(targetClass.getName(), fieldName, sqlPath);
        }

        try (InputStream resource = inputStream) {
            byte[] bytes = resource.readAllBytes();
            return new String(bytes, charset);
        } catch (IOException ex) {
            throw new SqlInjectionException(
                "Failed SQL injection for field '"
                    + fieldName
                    + "' in '"
                    + targetClass.getName()
                    + "': failed to read classpath resource '"
                    + sqlPath
                    + "'",
                ex
            );
        }
    }

    ClassLoader resolveResourceClassLoader(Class<?> targetClass, String fieldName, String sqlPath) {
        ClassLoader targetClassLoader = targetClass.getClassLoader();
        if (resourceExists(targetClassLoader, sqlPath)) {
            return targetClassLoader;
        }

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != targetClassLoader && resourceExists(contextClassLoader, sqlPath)) {
            return contextClassLoader;
        }

        throw new SqlResourceNotFoundException(targetClass.getName(), fieldName, sqlPath);
    }

    private boolean resourceExists(ClassLoader classLoader, String sqlPath) {
        try (InputStream stream = getResourceAsStream(classLoader, sqlPath)) {
            return stream != null;
        } catch (IOException ex) {
            return false;
        }
    }

    private InputStream getResourceAsStream(ClassLoader classLoader, String sqlPath) {
        if (classLoader == null) {
            return ClassLoader.getSystemResourceAsStream(sqlPath);
        }
        return classLoader.getResourceAsStream(sqlPath);
    }
}
