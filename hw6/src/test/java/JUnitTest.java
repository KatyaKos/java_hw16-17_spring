import Exceptions.*;
import TestingClasses.*;
import org.junit.*;

import java.util.List;

import static org.junit.Assert.*;

public class JUnitTest {
    private TestRunner testRunner;

    @Before
    public void setUp() throws Exception {
        testRunner = new TestRunner();
    }

    @Test
    public void simpleTest() throws Exception {
        testRunner.testClass(SimpleTestClass.class);
        testRunner.getReports().forEach(r -> System.out.println(r.getInfo()));
    }

    @Test
    public void beforeAndAfterTest() throws Exception {
        testRunner.testClass(BeforeAndAfterTestClass.class);
    }

    @Test
    public void exceptionsTest() throws Exception {
        testRunner.testClass(ExceptionsThrownTestClass.class);
        List<TestReporter> reports = testRunner.getReports();
        assertEquals(2, reports.size());
        assertTrue(reports.stream().anyMatch(e -> e.getResult().equals("passed: ") && e.getTestName().equals("testPassed")));
        assertTrue(reports.stream().anyMatch(e -> e.getResult().startsWith("failed: ") && e.getTestName().equals("testFailed")));
    }

    @Test(expected = IllegalUsageException.class)
    public void incorrectUsage() throws Exception {
        testRunner.testClass(IllegalUsageTestClass.class);
    }

    @Test
    public void testIgnored() throws Exception {
        testRunner.testClass(IgnoredTestClass.class);
        assertTrue(testRunner.getReports().stream().allMatch(e -> e.getTestName().equals("method") && e.getResult().startsWith("ignored: ")));
    }
}
