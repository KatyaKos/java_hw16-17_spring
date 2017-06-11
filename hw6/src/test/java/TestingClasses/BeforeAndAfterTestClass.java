package TestingClasses;

import Annotations.*;

public class BeforeAndAfterTestClass {
    private static boolean flag = false;
    private static boolean classFlag = false;

    public BeforeAndAfterTestClass() {}

    @Before
    public void setFlagTrue() {
        if (flag) throw new RuntimeException("oops");
        flag = true;
    }

    @After
    public void setFlagFalse() {
        if (!flag) throw new RuntimeException("oops");
        flag = false;
    }

    @BeforeClass
    public void setClassFlagTrue() {
        if (classFlag) throw new RuntimeException("oops");
        classFlag = true;
    }

    @AfterClass
    public void setClassFlagFalse() {
        if (!classFlag) throw new RuntimeException("oops");
        classFlag = false;
    }

    @Test
    public void test() {}
}
