package io.github.gojogs.sqlembed.core;

import io.github.gojogs.sqlembed.exception.SqlInvalidPathException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class SqlPathNormalizerTest {

    /**
     * Tests the normalize method in SqlPathNormalizer.
     * <p>
     * The normalize method is responsible for:
     * - Replacing backslashes with forward slashes in the given raw path.
     * - Stripping leading forward slashes.
     * - Throwing an exception if the raw path is null or results in an empty/blank normalized path.
     */

    @Test
    void normalizeReplacesBackslashesAndStripsLeadingSlashes() throws NoSuchFieldException {
        String rawPath = "\\path\\to\\file.sql";
        Class<?> targetClass = DummyClass.class;
        Field targetField = DummyClass.class.getDeclaredField("dummyField");
        SqlPathNormalizer normalizer = new SqlPathNormalizer();

        String normalized = normalizer.normalize(rawPath, targetClass, targetField);

        assertEquals("path/to/file.sql", normalized);
    }

    @Test
    void normalizeThrowsExceptionForNullPath() throws NoSuchFieldException {
        String rawPath = null;
        Class<?> targetClass = DummyClass.class;
        Field targetField = DummyClass.class.getDeclaredField("dummyField");
        SqlPathNormalizer normalizer = new SqlPathNormalizer();

        SqlInvalidPathException exception = assertThrows(SqlInvalidPathException.class, () ->
                normalizer.normalize(rawPath, targetClass, targetField)
        );

        assertTrue(exception.getMessage().contains("null"));
        assertTrue(exception.getMessage().contains("DummyClass"));
        assertTrue(exception.getMessage().contains("dummyField"));
    }

    @Test
    void normalizeThrowsExceptionForEmptyPath() throws NoSuchFieldException {
        String rawPath = "   ";
        Class<?> targetClass = DummyClass.class;
        Field targetField = DummyClass.class.getDeclaredField("dummyField");
        SqlPathNormalizer normalizer = new SqlPathNormalizer();

        SqlInvalidPathException exception = assertThrows(SqlInvalidPathException.class, () ->
                normalizer.normalize(rawPath, targetClass, targetField)
        );

        assertTrue(exception.getMessage().contains("   "));
        assertTrue(exception.getMessage().contains("DummyClass"));
        assertTrue(exception.getMessage().contains("dummyField"));
    }

    @Test
    void normalizeHandlesMultipleLeadingSlashes() throws NoSuchFieldException {
        String rawPath = "/////path/to/file.sql";
        Class<?> targetClass = DummyClass.class;
        Field targetField = DummyClass.class.getDeclaredField("dummyField");
        SqlPathNormalizer normalizer = new SqlPathNormalizer();

        String normalized = normalizer.normalize(rawPath, targetClass, targetField);

        assertEquals("path/to/file.sql", normalized);
    }

    @Test
    void normalizeHandlesPathsWithNoModificationsNeeded() throws NoSuchFieldException {
        String rawPath = "path/with/no/changes.sql";
        Class<?> targetClass = DummyClass.class;
        Field targetField = DummyClass.class.getDeclaredField("dummyField");
        SqlPathNormalizer normalizer = new SqlPathNormalizer();

        String normalized = normalizer.normalize(rawPath, targetClass, targetField);

        assertEquals("path/with/no/changes.sql", normalized);
    }

    private static class DummyClass {
        private String dummyField;
    }
}