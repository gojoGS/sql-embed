# SQL Embed

SQL Embed - a small library to embed SQL scripts.

## Installation

```xml
<dependency>
  <groupId>io.github.gojogs</groupId>
  <artifactId>sql-embed</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Quick start

```java
import annotation.io.github.gojogs.sqlembed.SqlInject;

final class BookRepository {
    @SqlInject("sql/book/find_by_id.sql")
    private String findByIdSql;

    String query() {
        return findByIdSql;
    }
}

void foo() {
    BookRepository repository = new BookRepository();
    SqlInjector.inject(repository);   
}
```

## API

- `@SqlInject`
- `SqlInjector.inject(Object)`
- `SqlInjector.inject(Object, SqlLoaderOptions)`

## Build

```bash
mvn -q clean test
```
