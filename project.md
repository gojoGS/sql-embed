# SQL Inject Library - Project Description

## Overview

Build a lightweight Java library that injects SQL text from classpath `.sql` files into annotated `String` fields.

Target API:

```java
@SqlInject("book/insert.sql")
private String insertBookSql;
```

Expected behavior: when injection runs, the field value becomes the exact file contents from classpath resource `book/insert.sql` (usually from `src/main/resources/book/insert.sql`).

## Goals

- Externalize SQL from Java constants into `.sql` files.
- Keep public API minimal and annotation-driven.
- Support plain Java projects first, then optional Spring Boot integration.
- Fail clearly on missing resources or invalid field usage.
- Be thread-safe and efficient with caching.

## Non-goals (MVP)

- SQL parsing/validation.
- Query execution abstraction.
- ORM functionality.
- Advanced templating/dynamic SQL.

## Public API

### Annotation

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SqlInject {
    String value(); // classpath-relative path, e.g. "book/insert.sql"
}
```

### Core entrypoint

```java
public final class SqlInjector {
    public static void inject(Object target);
    public static void inject(Object target, SqlLoaderOptions options);
}
```

### Options (recommended)

```java
public final class SqlLoaderOptions {
    private boolean failFast = true;
    private boolean cacheEnabled = true;
    private Charset charset = StandardCharsets.UTF_8;
    private boolean normalizeLineEndings = false;
    private boolean trimTrailingWhitespace = false;
}
```

## Runtime rules

### Resource resolution

1. Resolve `@SqlInject("book/insert.sql")` from classpath root.
2. Use class loader of target class first.
3. Fallback to thread context class loader.
4. Normalize path (`\\` -> `/`, strip leading `/`, reject blank).

### Field validation

- Supported: instance `String` fields (any visibility).
- Unsupported (throw): `static`, `final`, non-`String` annotated fields.
- Scan class hierarchy (include superclasses, skip `Object`).

### Default failure behavior

- `failFast=true` (recommended): throw exception immediately.
- Error should include class, field, and SQL path.

## Project structure

Suggested multi-module layout:

```text
sql-loader/
|- sql-loader-core/
|  `- src/main/java/...
`- sql-loader-spring-boot-starter/
   `- src/main/java/...
```

### `sql-loader-core` package map

- `annotation/SqlInject`
- `core/SqlInjector`
- `core/SqlFieldScanner`
- `core/SqlFieldInjector`
- `core/ClasspathSqlResourceLoader`
- `core/SqlTextCache`
- `config/SqlLoaderOptions`
- `exception/SqlInjectionException`
- `exception/SqlResourceNotFoundException`
- `exception/SqlFieldTypeMismatchException`
- `exception/SqlFieldNotInjectableException`

### `sql-loader-spring-boot-starter` package map

- `autoconfigure/SqlInjectAutoConfiguration`
- `autoconfigure/SqlInjectBeanPostProcessor`
- `autoconfigure/SqlInjectProperties`
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

## Core algorithm

```text
inject(target, options):
  fields = scan all @SqlInject fields in class hierarchy
  for field in fields:
    validate field type and modifiers
    path = normalize(annotation.value)
    sql = load from classpath (or cache)
    sql = optional post-process(options)
    set field accessible and assign sql
```

## Caching strategy

- Use `ConcurrentHashMap`.
- Key should include classloader identity + normalized path + charset.
- Cache loaded SQL text, not streams.
- Optional: cache scanned field metadata per class for faster repeated injections.

## Exception model

All exceptions should be actionable and include context.

Examples:

- `SqlResourceNotFoundException`: classpath resource missing.
- `SqlFieldTypeMismatchException`: annotated field is not `String`.
- `SqlFieldNotInjectableException`: field is `static`/`final`.
- `SqlInjectionException`: reflection access failure or wrapper exception.

Example message:

`Failed SQL injection for field 'insertBookSql' in 'com.acme.BookRepository': classpath resource 'book/insert.sql' not found`

## Spring Boot integration details

Use a `BeanPostProcessor` to inject SQL on bean initialization.

Proposed config keys:

- `sql.inject.fail-fast=true`
- `sql.inject.cache-enabled=true`
- `sql.inject.charset=UTF-8`
- `sql.inject.normalize-line-endings=false`
- `sql.inject.trim-trailing-whitespace=false`

Boot 3 registration file:

`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

## Testing plan

### Unit tests (core)

1. Injects private field from existing `.sql` resource.
2. Missing resource throws `SqlResourceNotFoundException`.
3. Non-`String` annotated field throws type mismatch exception.
4. `final` and `static` annotated fields are rejected.
5. Inherited annotated fields are injected.
6. Cache path is hit on repeated calls.
7. Charset handling works for UTF-8 special chars.
8. Optional post-processing flags behave as configured.

### Integration tests (Spring Boot)

1. Bean gets injected automatically at startup.
2. Missing SQL fails context startup when `fail-fast=true`.
3. Startup continues when `fail-fast=false` (if supported).
4. Property overrides are respected.

### Test resources

- `src/test/resources/book/insert.sql`
- `src/test/resources/book/find_by_id.sql`
- Missing-path scenarios

## Implementation phases

1. **MVP Core**: annotation + loader + reflection injector + tests.
2. **Hardening**: cache + richer diagnostics + hierarchy scanning.
3. **Spring Starter**: auto-config + BPP + integration tests.
4. **Docs/Release**: README, examples, versioning, publishing.
5. **Optional Features**: placeholders, dev reload, resolver SPI.

## Recommended defaults

- Fail fast: `true`
- Cache enabled: `true`
- Charset: `UTF-8`
- Preserve raw content (no trimming/line normalization by default)

These defaults maximize correctness and make failures obvious.

## Notes for implementation agent

- Keep the first release strict and predictable; avoid silent fallback behavior.
- Preserve SQL text exactly unless option flags request transformation.
- Avoid heavy dependencies in `sql-loader-core`.
- Ensure all public errors include class + field + resource path.
- Document one plain Java example and one Spring Boot example.
- Add a small compatibility matrix (`Java version`, `Spring Boot version`) in README.
