package io.github.gojogs.sqlembed.core;

import io.github.gojogs.sqlembed.exception.SqlInjectionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlPathNormalizerTest {

    /**
     * Tests the normalize method of the SqlPathNormalizer class.
     * This method is responsible for normalizing file paths
     * by replacing backslashes with forward slashes, removing leading slashes,
     * and ensuring that the resulting path is not empty.
     * <p>
     * The method throws an SqlInjectionException for invalid paths.
     */

    @Test
    void normalize_NullPath_ThrowsSqlInjectionException() {
        SqlPathNormalizer normalizer = new SqlPathNormalizer();

        SqlInjectionException exception = assertThrows(
                SqlInjectionException.class,
                () -> normalizer.normalize(null, TestClass.class, "testField")
        );

        assertTrue(exception.getMessage().contains("testField"));
        assertTrue(exception.getMessage().contains(TestClass.class.getName()));
        assertTrue(exception.getMessage().contains("null"));
    }

    @Test
    void normalize_ConvertsBackslashesToForwardSlashes() {
        SqlPathNormalizer normalizer = new SqlPathNormalizer();
        String rawPath = "folder\\subfolder\\file.sql";

        String normalizedPath = normalizer.normalize(rawPath, TestClass.class, "testField");

        assertEquals("folder/subfolder/file.sql", normalizedPath);
    }

    @Test
    void normalize_RemovesLeadingSlashes() {
        SqlPathNormalizer normalizer = new SqlPathNormalizer();
        String rawPath = "/folder/subfolder/file.sql";

        String normalizedPath = normalizer.normalize(rawPath, TestClass.class, "testField");

        assertEquals("folder/subfolder/file.sql", normalizedPath);
    }

    @Test
    void normalize_EmptyAfterTrimming_ThrowsSqlInjectionException() {
        SqlPathNormalizer normalizer = new SqlPathNormalizer();
        String rawPath = "    ";

        SqlInjectionException exception = assertThrows(
                SqlInjectionException.class,
                () -> normalizer.normalize(rawPath, TestClass.class, "testField")
        );

        assertTrue(exception.getMessage().contains("testField"));
        assertTrue(exception.getMessage().contains(TestClass.class.getName()));
        assertTrue(exception.getMessage().contains("    "));
    }

    @Test
    void normalize_ValidPath_ReturnsNormalizedPath() {
        SqlPathNormalizer normalizer = new SqlPathNormalizer();
        String rawPath = "folder/subfolder/file.sql";

        String normalizedPath = normalizer.normalize(rawPath, TestClass.class, "testField");

        assertEquals("folder/subfolder/file.sql", normalizedPath);
    }

    private static class TestClass {
    }
}