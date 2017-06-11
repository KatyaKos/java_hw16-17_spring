package TestingClasses;

import Annotations.*;

import static java.lang.Thread.sleep;

public class SimpleTestClass {
    @Before
    public void before() {
        System.out.println("before test\n");
    }

    @After
    public void after() {
        System.out.println("after test\n");
    }

    @BeforeClass
    public void beforeClass() {
        System.out.println("before class\n");
    }

    @AfterClass
    public void afterClass() {
        System.out.println("after class\n");
    }

    @Test
    public void testHello() throws Exception {
        sleep(500);
        System.out.println("Hello!\n");
    }

    @Test
    public void testHi() throws Exception {
        sleep(200);
        System.out.println("Hi!\n");
    }
}
