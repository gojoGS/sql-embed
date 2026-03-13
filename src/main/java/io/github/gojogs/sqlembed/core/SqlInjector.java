package io.github.gojogs.sqlembed.core;

import io.github.gojogs.sqlembed.annotation.SqlInject;
import io.github.gojogs.sqlembed.config.SqlLoaderOptions;
import io.github.gojogs.sqlembed.exception.SqlInjectionException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Injects SQL text from classpath resources into fields annotated with {@link SqlInject}.
 *
 * <p>Supported fields are instance {@link String} fields of any visibility. Annotated fields in the
 * full class hierarchy are scanned (excluding {@link Object}).
 */
public final class SqlInjector {
    private static final Pattern TRAILING_WHITESPACE_PATTERN = Pattern.compile("(?m)[ \\t]+$");

    private static final SqlFieldScanner FIELD_SCANNER = new SqlFieldScanner();
    private static final SqlFieldInjector FIELD_INJECTOR = new SqlFieldInjector();
    private static final SqlPathNormalizer PATH_NORMALIZER = new SqlPathNormalizer();
    private static final ClasspathSqlResourceLoader RESOURCE_LOADER = new ClasspathSqlResourceLoader();
    private static final SqlTextCache SQL_TEXT_CACHE = new SqlTextCache();

    private SqlInjector() {
    }

    /**
     * Creates and initializes an instance of the specified type using the provided supplier.
     * The created instance is processed to inject required dependencies or perform actions
     * as defined by the {@code inject} method.
     *
     * @param <T> the type of the object to be created
     * @param supplier the supplier responsible for providing the instance of type {@code T};
     *                 must not be {@code null}
     * @return the initialized instance of type {@code T}
     * @throws NullPointerException if the {@code supplier} is {@code null}
     * @throws IllegalArgumentException if the injected instance violates injection invariants
     * @throws SqlInjectionException if the injection process fails due to validation, loading,
     *                                or assignment issues
     */
    public static <T> T create(Supplier<T> supplier) {
        T target = supplier.get();
        inject(target);
        return target;
    }

    /**
     * Injects SQL using {@link SqlLoaderOptions#defaults()}.
     *
     * @param target object instance containing {@link SqlInject} fields
     * @throws IllegalArgumentException when {@code target} is {@code null}
     * @throws SqlInjectionException when validation, loading, or assignment fails
     */
    public static void inject(Object target) {
        inject(target, SqlLoaderOptions.defaults());
    }

    /**
     * Injects SQL into all fields annotated with {@link SqlInject} on the target class hierarchy.
     *
     * <p>For each annotated field, injection performs field validation, SQL path normalization, classpath
     * loading (target class loader first, then thread context class loader), optional caching, and optional
     * text post-processing based on the provided options.
     *
     * <p>If {@code options} is {@code null}, defaults are used.
     *
     * @param target object instance containing {@link SqlInject} fields
     * @param options loading and post-processing options; {@code null} uses defaults
     * @throws IllegalArgumentException when {@code target} is {@code null}
     * @throws SqlInjectionException when validation, loading, or assignment fails
     */
    public static void inject(Object target, SqlLoaderOptions options) {
        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }

        SqlLoaderOptions loaderOptions = options == null ? SqlLoaderOptions.defaults() : options;
        SqlInjectionException firstException = null;
        Class<?> targetClass = target.getClass();

        for (Field field : FIELD_SCANNER.scanAnnotatedFields(targetClass)) {
            SqlInject annotation = field.getAnnotation(SqlInject.class);
            try {
                String normalizedPath = PATH_NORMALIZER.normalize(annotation.value(), targetClass, field.getName());
                FIELD_INJECTOR.validateInjectableField(field, targetClass, normalizedPath);

                String sqlText = loaderOptions.isCacheEnabled()
                    ? getCachedSqlText(targetClass, normalizedPath, loaderOptions.getCharset())
                    : null;
                if (sqlText == null) {
                    ClassLoader resourceClassLoader = RESOURCE_LOADER.resolveResourceClassLoader(
                        targetClass,
                        field.getName(),
                        normalizedPath
                    );
                    sqlText = SQL_TEXT_CACHE.getOrLoad(
                        resourceClassLoader,
                        normalizedPath,
                        loaderOptions.getCharset(),
                        loaderOptions.isCacheEnabled(),
                        () -> RESOURCE_LOADER.loadSql(
                            resourceClassLoader,
                            targetClass,
                            field.getName(),
                            normalizedPath,
                            loaderOptions.getCharset()
                        )
                    );
                }

                String processedSqlText = applyPostProcessing(sqlText, loaderOptions);
                FIELD_INJECTOR.injectSqlText(target, field, processedSqlText, normalizedPath, targetClass);
            } catch (SqlInjectionException ex) {
                if (loaderOptions.isFailFast()) {
                    throw ex;
                }
                if (firstException == null) {
                    firstException = ex;
                }
            }
        }

        if (firstException != null) {
            throw firstException;
        }
    }

    private static String getCachedSqlText(Class<?> targetClass, String normalizedPath, Charset charset) {
        ClassLoader targetClassLoader = targetClass.getClassLoader();
        String cachedSqlText = SQL_TEXT_CACHE.getIfPresent(targetClassLoader, normalizedPath, charset);
        if (cachedSqlText != null) {
            return cachedSqlText;
        }

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader == targetClassLoader) {
            return null;
        }

        return SQL_TEXT_CACHE.getIfPresent(contextClassLoader, normalizedPath, charset);
    }

    private static String applyPostProcessing(String sqlText, SqlLoaderOptions options) {
        String result = sqlText;

        if (options.isNormalizeLineEndings()) {
            result = result.replace("\r\n", "\n").replace("\r", "\n");
        }

        if (options.isTrimTrailingWhitespace()) {
            result = TRAILING_WHITESPACE_PATTERN.matcher(result).replaceAll("");
        }

        return result;
    }
}
