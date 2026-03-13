package dev.zbartha.sqlembed.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Configuration options for SQL loading and post-processing.
 *
 * <p>Instances are mutable and intended to be configured fluently before calling
 * {@code SqlInjector.inject(target, options)}.
 */
public final class SqlLoaderOptions {
    private boolean failFast = true;
    private boolean cacheEnabled = true;
    private Charset charset = StandardCharsets.UTF_8;
    private boolean normalizeLineEndings;
    private boolean trimTrailingWhitespace;

    private SqlLoaderOptions() {
    }

    /**
     * Creates a new options instance with default values.
     *
     * <ul>
     *   <li>{@code failFast=true}</li>
     *   <li>{@code cacheEnabled=true}</li>
     *   <li>{@code charset=UTF-8}</li>
     *   <li>{@code normalizeLineEndings=false}</li>
     *   <li>{@code trimTrailingWhitespace=false}</li>
     * </ul>
     *
     * @return new options instance
     */
    public static SqlLoaderOptions defaults() {
        return new SqlLoaderOptions();
    }

    /**
     * Returns whether injection stops on the first failure.
     *
     * @return {@code true} when fail-fast is enabled
     */
    public boolean isFailFast() {
        return failFast;
    }

    /**
     * Sets fail-fast behavior.
     *
     * <p>When enabled, the first {@code SqlInjectionException} is thrown immediately. When disabled,
     * injection attempts continue and the first failure is thrown after processing all annotated fields.
     *
     * @param failFast whether fail-fast mode is enabled
     * @return this options instance
     */
    public SqlLoaderOptions withFailFast(boolean failFast) {
        this.failFast = failFast;
        return this;
    }

    /**
     * Returns whether SQL resource text caching is enabled.
     *
     * @return {@code true} when caching is enabled
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    /**
     * Enables or disables SQL resource text caching.
     *
     * @param cacheEnabled whether cache is enabled
     * @return this options instance
     */
    public SqlLoaderOptions withCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
        return this;
    }

    /**
     * Returns the charset used to decode SQL resource bytes.
     *
     * @return configured charset
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Sets the charset used to decode SQL resource bytes.
     *
     * @param charset charset to use
     * @return this options instance
     * @throws NullPointerException when {@code charset} is {@code null}
     */
    public SqlLoaderOptions withCharset(Charset charset) {
        this.charset = Objects.requireNonNull(charset, "charset must not be null");
        return this;
    }

    /**
     * Returns whether line endings are normalized to {@code \n}.
     *
     * @return {@code true} when line ending normalization is enabled
     */
    public boolean isNormalizeLineEndings() {
        return normalizeLineEndings;
    }

    /**
     * Enables or disables line ending normalization from {@code \r\n} and {@code \r} to {@code \n}.
     *
     * @param normalizeLineEndings whether line endings should be normalized
     * @return this options instance
     */
    public SqlLoaderOptions withNormalizeLineEndings(boolean normalizeLineEndings) {
        this.normalizeLineEndings = normalizeLineEndings;
        return this;
    }

    /**
     * Returns whether trailing whitespace is removed from each line.
     *
     * @return {@code true} when trailing whitespace trimming is enabled
     */
    public boolean isTrimTrailingWhitespace() {
        return trimTrailingWhitespace;
    }

    /**
     * Enables or disables trailing whitespace removal for each line.
     *
     * @param trimTrailingWhitespace whether trailing whitespace should be removed
     * @return this options instance
     */
    public SqlLoaderOptions withTrimTrailingWhitespace(boolean trimTrailingWhitespace) {
        this.trimTrailingWhitespace = trimTrailingWhitespace;
        return this;
    }
}
