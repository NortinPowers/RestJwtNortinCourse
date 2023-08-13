package by.nortin.restjwtproject.utils;

import static by.nortin.restjwtproject.utils.ObjectUtils.getIgnoredProperties;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class ObjectUtilsTest {

    private TestObject testObject;
    private TestIncludeObject testIncludeObject;

    @Test
    void test_getIgnoredProperties_notIncludeFields() {
        testIncludeObject = new TestIncludeObject(1L, "testName");
        testObject = new TestObject(2L, "name", 18, true, testIncludeObject);

        assertArrayEquals(new String[] {}, getIgnoredProperties(testObject));
    }

    @Test
    void test_getIgnoredProperties_notIncludeFields_semiEmptyTestIncludeObject() {
        testIncludeObject = new TestIncludeObject(1L, null);
        testObject = new TestObject(2L, "name", 18, true, testIncludeObject);

        assertArrayEquals(new String[] {}, getIgnoredProperties(testObject));
    }

    @Test
    void test_getIgnoredProperties_includeFieldsTestIncludeObject() {
        testIncludeObject = null;
        testObject = new TestObject(2L, "name", 18, true, testIncludeObject);

        assertArrayEquals(new String[] {"testIncludeObject"}, getIgnoredProperties(testObject));
    }

    @Test
    void test_getIgnoredProperties_includeFieldsNameAge() {
        testIncludeObject = new TestIncludeObject(1L, "testName");
        testObject = new TestObject(2L, null, null, true, testIncludeObject);

        assertArrayEquals(new String[] {"name", "age"}, getIgnoredProperties(testObject));
    }

    @Test
    void test_getIgnoredProperties_includeFieldsIdTrust() {
        testIncludeObject = new TestIncludeObject(1L, "testName");
        testObject = new TestObject(null, "name", 18, null, testIncludeObject);

        assertArrayEquals(new String[] {"id", "trust"}, getIgnoredProperties(testObject));
    }

    record TestObject(Long id, String name, Integer age, Boolean trust, TestIncludeObject testIncludeObject) {
    }

    record TestIncludeObject(Long testId, String testName) {
    }
}
