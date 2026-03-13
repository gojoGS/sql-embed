package dev.zbartha.sqlembed.core;

import dev.zbartha.sqlembed.exception.SqlInjectionException;
import dev.zbartha.sqlembed.exception.SqlResourceNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

final class ClasspathSqlResourceLoader {
    String loadSql(Class<?> targetClass, String fieldName, String sqlPath, Charset charset) {
        InputStream inputStream = openResourceStream(targetClass, sqlPath);
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

    private InputStream openResourceStream(Class<?> targetClass, String sqlPath) {
        ClassLoader targetClassLoader = targetClass.getClassLoader();
        InputStream fromTargetClassLoader = getResourceAsStream(targetClassLoader, sqlPath);
        if (fromTargetClassLoader != null) {
            return fromTargetClassLoader;
        }

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader == targetClassLoader) {
            return null;
        }

        return getResourceAsStream(contextClassLoader, sqlPath);
    }

    private InputStream getResourceAsStream(ClassLoader classLoader, String sqlPath) {
        if (classLoader == null) {
            return ClassLoader.getSystemResourceAsStream(sqlPath);
        }
        return classLoader.getResourceAsStream(sqlPath);
    }
}
