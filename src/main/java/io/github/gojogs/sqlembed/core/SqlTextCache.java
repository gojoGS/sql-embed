package io.github.gojogs.sqlembed.core;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

final class SqlTextCache {
    private final ConcurrentMap<CacheKey, String> sqlTextByResource = new ConcurrentHashMap<>();

    String getOrLoad(
        ClassLoader classLoader,
        String sqlPath,
        Charset charset,
        boolean cacheEnabled,
        Supplier<String> sqlLoader
    ) {
        if (!cacheEnabled) {
            return sqlLoader.get();
        }

        CacheKey key = new CacheKey(classLoader, sqlPath, charset);
        return sqlTextByResource.computeIfAbsent(key, ignored -> sqlLoader.get());
    }

    String getIfPresent(ClassLoader classLoader, String sqlPath, Charset charset) {
        CacheKey key = new CacheKey(classLoader, sqlPath, charset);
        return sqlTextByResource.get(key);
    }

    private static final class CacheKey {
        private final ClassLoader classLoader;
        private final String sqlPath;
        private final Charset charset;

        private CacheKey(ClassLoader classLoader, String sqlPath, Charset charset) {
            this.classLoader = classLoader;
            this.sqlPath = sqlPath;
            this.charset = charset;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof CacheKey other)) {
                return false;
            }
            return classLoader == other.classLoader
                && Objects.equals(sqlPath, other.sqlPath)
                && Objects.equals(charset, other.charset);
        }

        @Override
        public int hashCode() {
            return Objects.hash(System.identityHashCode(classLoader), sqlPath, charset);
        }
    }
}
