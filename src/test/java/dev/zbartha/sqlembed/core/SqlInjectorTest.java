package dev.zbartha.sqlembed.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.zbartha.sqlembed.annotation.SqlInject;
import dev.zbartha.sqlembed.config.SqlLoaderOptions;
import dev.zbartha.sqlembed.exception.SqlFieldNotInjectableException;
import dev.zbartha.sqlembed.exception.SqlFieldTypeMismatchException;
import dev.zbartha.sqlembed.exception.SqlResourceNotFoundException;
import org.junit.jupiter.api.Test;

class SqlInjectorTest {

    @Test
    void injectsPrivateStringFieldFromClasspathResource() {
        BookRepository target = new BookRepository();

        SqlInjector.inject(target);

        assertEquals("INSERT INTO books(id, title) VALUES (:id, :title);\n", target.insertBookSql);
    }

    @Test
    void missingResourceThrowsContextualException() {
        MissingResourceTarget target = new MissingResourceTarget();

        SqlResourceNotFoundException ex = assertThrows(
            SqlResourceNotFoundException.class,
            () -> SqlInjector.inject(target)
        );

        assertTrue(ex.getMessage().contains(MissingResourceTarget.class.getName()));
        assertTrue(ex.getMessage().contains("missingSql"));
        assertTrue(ex.getMessage().contains("sql/missing.sql"));
    }

    @Test
    void rejectsNonStringAnnotatedField() {
        NonStringFieldTarget target = new NonStringFieldTarget();

        SqlFieldTypeMismatchException ex = assertThrows(
            SqlFieldTypeMismatchException.class,
            () -> SqlInjector.inject(target)
        );

        assertTrue(ex.getMessage().contains(NonStringFieldTarget.class.getName()));
        assertTrue(ex.getMessage().contains("sqlAsInt"));
        assertTrue(ex.getMessage().contains("sql/book/insert.sql"));
    }

    @Test
    void rejectsStaticAnnotatedField() {
        SqlFieldNotInjectableException ex = assertThrows(
            SqlFieldNotInjectableException.class,
            () -> SqlInjector.inject(new StaticFieldTarget())
        );

        assertTrue(ex.getMessage().contains(StaticFieldTarget.class.getName()));
        assertTrue(ex.getMessage().contains("STATIC_SQL"));
        assertTrue(ex.getMessage().contains("sql/book/insert.sql"));
    }

    @Test
    void rejectsFinalAnnotatedField() {
        SqlFieldNotInjectableException ex = assertThrows(
            SqlFieldNotInjectableException.class,
            () -> SqlInjector.inject(new FinalFieldTarget())
        );

        assertTrue(ex.getMessage().contains(FinalFieldTarget.class.getName()));
        assertTrue(ex.getMessage().contains("finalSql"));
        assertTrue(ex.getMessage().contains("sql/book/insert.sql"));
    }

    @Test
    void injectsInheritedAnnotatedField() {
        ChildRepository target = new ChildRepository();

        SqlInjector.inject(target);

        assertEquals("SELECT id, title FROM books WHERE id = :id;\n", target.findByIdSql);
    }

    @Test
    void normalizesBackslashAndLeadingSlashInSqlPath() {
        PathNormalizationTarget target = new PathNormalizationTarget();

        SqlInjector.inject(target);

        assertEquals("SELECT id, title FROM books WHERE id = :id;\n", target.findByIdSql);
    }

    @Test
    void cachesLoadedSqlTextWhenEnabled() {
        BookRepository first = new BookRepository();
        BookRepository second = new BookRepository();

        SqlInjector.inject(first);
        SqlInjector.inject(second);

        assertSame(first.insertBookSql, second.insertBookSql);
    }

    @Test
    void doesNotReuseSqlTextWhenCacheDisabled() {
        BookRepository first = new BookRepository();
        BookRepository second = new BookRepository();
        SqlLoaderOptions options = SqlLoaderOptions.defaults().withCacheEnabled(false);

        SqlInjector.inject(first, options);
        SqlInjector.inject(second, options);

        assertNotSame(first.insertBookSql, second.insertBookSql);
        assertEquals(first.insertBookSql, second.insertBookSql);
    }

    @Test
    void trimsTrailingWhitespaceWhenOptionEnabled() {
        TrailingWhitespaceTarget target = new TrailingWhitespaceTarget();
        SqlLoaderOptions options = SqlLoaderOptions.defaults().withTrimTrailingWhitespace(true);

        SqlInjector.inject(target, options);

        assertEquals("SELECT id FROM books;\n", target.sql);
    }

    private static final class BookRepository {
        @SqlInject("sql/book/insert.sql")
        private String insertBookSql;
    }

    private static final class MissingResourceTarget {
        @SqlInject("sql/missing.sql")
        private String missingSql;
    }

    private static final class NonStringFieldTarget {
        @SqlInject("sql/book/insert.sql")
        private int sqlAsInt;
    }

    private static final class StaticFieldTarget {
        @SqlInject("sql/book/insert.sql")
        private static String STATIC_SQL;
    }

    private static final class FinalFieldTarget {
        @SqlInject("sql/book/insert.sql")
        private final String finalSql = "";
    }

    private static class ParentRepository {
        @SqlInject("sql/book/find_by_id.sql")
        String findByIdSql;
    }

    private static final class ChildRepository extends ParentRepository {
    }

    private static final class PathNormalizationTarget {
        @SqlInject("\\sql\\book\\find_by_id.sql")
        private String findByIdSql;
    }

    private static final class TrailingWhitespaceTarget {
        @SqlInject("sql/book/trailing_whitespace.sql")
        private String sql;
    }
}
