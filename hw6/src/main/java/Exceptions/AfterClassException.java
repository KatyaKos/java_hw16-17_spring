package Exceptions;

/**
 * This exception tells if method with @AfterClass annotation went wrong.
 */
public class AfterClassException extends Exception {
    public AfterClassException(String message) {
        super(message);
    }
}
