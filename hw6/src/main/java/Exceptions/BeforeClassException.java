package Exceptions;

/**
 * This exception tells if method with @BeforeClass annotation went wrong.
 */
public class BeforeClassException extends Exception {
    public BeforeClassException(String message) {
        super(message);
    }
}
