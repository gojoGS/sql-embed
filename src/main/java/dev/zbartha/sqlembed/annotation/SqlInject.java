package dev.zbartha.sqlembed.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field for SQL text injection from a classpath resource.
 *
 * <p>The annotated field must be a non-static, non-final {@link String}. The path is classpath-root
 * relative, for example {@code "book/insert.sql"}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SqlInject {
    /**
     * Classpath-relative resource path to the SQL file.
     *
     * @return SQL resource path, for example {@code "book/insert.sql"}
     */
    String value();
}
