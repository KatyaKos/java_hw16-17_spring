package Exceptions;

/**
 * This exception tells if method with @Before annotation went wrong.
 */
public class BeforeException extends Exception {
    public BeforeException(String message) {
        super(message);
    }
}
