package Exceptions;

/**
 * This exception tells if annotations' usage is not correct.
 */
public class IllegalUsageException extends Exception {
    public IllegalUsageException(String msg) {
        super(msg);
    }
}