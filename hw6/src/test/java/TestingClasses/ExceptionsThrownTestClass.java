package TestingClasses;

import Annotations.*;

import java.util.MissingResourceException;

public class ExceptionsThrownTestClass {
    @Test(expected = NullPointerException.class)
    public void testPassed() {
        throw new NullPointerException();
    }

    @Test(expected = MissingResourceException.class)
    public void testFailed() {
        throw new NullPointerException();
    }
}
