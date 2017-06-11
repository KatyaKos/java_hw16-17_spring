package Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods as JUnit Test methods.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {

    /**
     * Tells why the method was ignored.
     * @return a message with a reason (or empty string if was not ignored)
     */
    String ignore() default "";

    /**
     * Exception expected during the execution (None if no exception).
     * @return exception
     */
    Class<? extends Throwable> expected() default None.class;

    /**
     * Represents absence of exception.
     */
    class None extends Throwable {}
}
