package TestingClasses;

import Annotations.*;

public class IgnoredTestClass {
    @Test(ignore = "ignore this")
    public void method() {}
}
