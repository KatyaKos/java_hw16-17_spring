import Annotations.*;
import Exceptions.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs all tests in the class (does not forget about annotations).
 */
public class TestRunner {
    private Object instance = null;
    private List<TestReporter> reports = new ArrayList<>();

    private List<Method> methodsList = new ArrayList<>();
    private List<Method> beforeClassMethods = new ArrayList<>();
    private List<Method> afterClassMethods = new ArrayList<>();
    private List<Method> beforeMethods = new ArrayList<>();
    private List<Method> afterMethods = new ArrayList<>();

    /**
     * Returns reports with results of tests.
     * @return reports list
     */
    public List<TestReporter> getReports() {
        return reports;
    }

    /**
     * Tests the class.
     * @param clazz class to test
     * @throws AfterException if something went wrong in @After method
     * @throws BeforeException if something went wrong in @Before method
     * @throws BeforeClassException if something went wrong in @BeforeClass method
     * @throws AfterClassException if something went wrong in @AfterClass method
     * @throws IllegalUsageException if annotations' usage is not correct
     */
    public void testClass(@NotNull Class clazz) throws AfterException, BeforeException, BeforeClassException, AfterClassException, IllegalUsageException {
        preparation(clazz);
        runBeforeClassTests();
        runTests();
        runAfterClassTests();
    }

    /**
     * Sorts methods in the class for lists with different annotations.
     * @param clazz class with methods
     * @throws IllegalUsageException if annotations' usage is not correct
     */
    private void preparation(@NotNull Class clazz) throws IllegalUsageException {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class) && (method.isAnnotationPresent(Before.class) || method.isAnnotationPresent(After.class) || method.isAnnotationPresent(BeforeClass.class) || method.isAnnotationPresent(AfterClass.class))) {
                throw new IllegalUsageException("incorrect annotation usage in " + method.getName());
            }
            if (method.isAnnotationPresent(After.class)){
                afterMethods.add(method);
            }
            if (method.isAnnotationPresent(Before.class)){
                beforeMethods.add(method);
            }
            if (method.isAnnotationPresent(AfterClass.class)){
                afterClassMethods.add(method);
            }
            if (method.isAnnotationPresent(BeforeClass.class)){
                beforeClassMethods.add(method);
            }
            if (method.isAnnotationPresent(Test.class)) {
                String ignore = method.getAnnotation(Test.class).ignore();
                if (!ignore.equals("")) {
                    reports.add(new TestReporter(method.getName(), "ignored: " + ignore));
                } else {
                    methodsList.add(method);
                }
            }
        }
        try {
            instance = clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs tests in the class.
     */
    private void runTests() throws AfterException, BeforeException {
        for (Method method : methodsList) {
            runBeforeTests();
            long start = System.currentTimeMillis();
            Throwable exception = null;
            try {
                method.invoke(instance);
            } catch (Exception e) {
                exception = e.getCause();
            }
            long stop = System.currentTimeMillis();
            Class expected = method.getAnnotation(Test.class).expected();
            if ((expected == Test.None.class && exception == null) || (exception != null && expected != Test.None.class && expected.equals(exception.getClass()))) {
                reports.add(new TestReporter(method.getName(), "passed: ", stop - start));
            } else {
                reports.add(new TestReporter(method.getName(), "failed: " + exception.getMessage()));
            }
            runAfterTests();
        }
    }

    private void runAfterTests() throws AfterException {
        for (Method method : afterMethods)
            try {
                method.invoke(instance);
            } catch (Exception e) {
                throw new AfterException(constructFailureMessage(method, e));
            }
    }

    private void runBeforeTests() throws BeforeException {
        for (Method method : beforeMethods)
            try {
                method.invoke(instance);
            } catch (Exception e) {
                throw new BeforeException(constructFailureMessage(method, e));
            }
    }

    private void runBeforeClassTests() throws BeforeClassException {
        for (Method method : beforeClassMethods)
            try {
                method.invoke(instance);
            } catch (Exception e) {
                throw new BeforeClassException(constructFailureMessage(method, e));
            }
    }

    private void runAfterClassTests() throws AfterClassException {
        for (Method method : afterClassMethods)
            try {
                method.invoke(instance);
            } catch (Exception e) {
                throw new AfterClassException(constructFailureMessage(method, e));
            }
    }

    private String constructFailureMessage(Method method, Exception e) {
        return method.getName() + " failed: " + e.getMessage();
    }
}
