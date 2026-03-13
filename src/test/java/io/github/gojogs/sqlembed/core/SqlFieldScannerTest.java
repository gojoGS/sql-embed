package io.github.gojogs.sqlembed.core;

import io.github.gojogs.sqlembed.annotation.SqlInject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SqlFieldScanner class.
 *
 * <p>The SqlFieldScanner class is responsible for scanning all fields in a class hierarchy
 * and collecting those annotated with the @SqlInject annotation.
 *
 * <p>Each test verifies a specific behavior of the scanAnnotatedFields method.
 */
class SqlFieldScannerTest {

    @Test
    void scanAnnotatedFields_ReturnsAnnotatedFieldsInClass() {
        // Setup
        SqlFieldScanner scanner = new SqlFieldScanner();

        // Execute
        List<Field> annotatedFields = scanner.scanAnnotatedFields(AnnotatedClass.class);

        // Verify
        assertEquals(2, annotatedFields.size());
        assertTrue(containsFieldWithName(annotatedFields, "sqlQuery1"));
        assertTrue(containsFieldWithName(annotatedFields, "sqlQuery2"));
    }

    @Test
    void scanAnnotatedFields_IncludesFieldsFromSuperClass() {
        // Setup
        SqlFieldScanner scanner = new SqlFieldScanner();

        // Execute
        List<Field> annotatedFields = scanner.scanAnnotatedFields(ChildClass.class);

        // Verify
        assertEquals(2, annotatedFields.size());
        assertTrue(containsFieldWithName(annotatedFields, "parentSqlQuery"));
        assertTrue(containsFieldWithName(annotatedFields, "childSqlQuery"));
    }

    @Test
    void scanAnnotatedFields_ExcludesFieldsFromObjectClass() {
        // Setup
        SqlFieldScanner scanner = new SqlFieldScanner();

        // Execute
        List<Field> annotatedFields = scanner.scanAnnotatedFields(Object.class);

        // Verify
        assertTrue(annotatedFields.isEmpty(), "Fields from Object class should not be included.");
    }

    @Test
    void scanAnnotatedFields_IgnoresFieldsWithoutAnnotation() {
        // Setup
        SqlFieldScanner scanner = new SqlFieldScanner();

        // Execute
        List<Field> annotatedFields = scanner.scanAnnotatedFields(ClassWithMixedFields.class);

        // Verify
        assertEquals(1, annotatedFields.size());
        assertTrue(containsFieldWithName(annotatedFields, "annotatedField"));
    }

    @Test
    void scanAnnotatedFields_DoesNotFailOnEmptyClass() {
        // Setup
        SqlFieldScanner scanner = new SqlFieldScanner();

        // Execute
        List<Field> annotatedFields = scanner.scanAnnotatedFields(EmptyClass.class);

        // Verify
        assertNotNull(annotatedFields);
        assertTrue(annotatedFields.isEmpty());
    }

    @Test
    void scanAnnotatedFields_DoesNotFailOnNull() {
        // Setup
        SqlFieldScanner scanner = new SqlFieldScanner();

        // Execute
        List<Field> annotatedFields = scanner.scanAnnotatedFields(null);

        // Verify
        assertNotNull(annotatedFields);
        assertTrue(annotatedFields.isEmpty());
    }

    // Helper methods and test classes
    private boolean containsFieldWithName(List<Field> fields, String fieldName) {
        return fields.stream().anyMatch(field -> field.getName().equals(fieldName));
    }

    private static class AnnotatedClass {
        @SqlInject("sql/query1.sql")
        private String sqlQuery1;

        @SqlInject("sql/query2.sql")
        private String sqlQuery2;

        private String nonAnnotatedField;
    }

    private static class ParentClass {
        @SqlInject("sql/parent_query.sql")
        protected String parentSqlQuery;
    }

    private static class ChildClass extends ParentClass {
        @SqlInject("sql/child_query.sql")
        private String childSqlQuery;
    }

    private static class ClassWithMixedFields {
        @SqlInject("sql/annotated.sql")
        private String annotatedField;

        private int nonAnnotatedField1;

        private boolean nonAnnotatedField2;
    }

    private static class EmptyClass {
        // No fields
    }
}