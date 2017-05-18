package Exceptions;

/**
 * This exception tells if method with @After annotation went wrong.
 */
public class AfterException extends Exception {
    public AfterException(String message) {
        super(message);
    }
}
