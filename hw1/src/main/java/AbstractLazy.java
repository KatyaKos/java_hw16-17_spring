import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Abstract class for Lazy implementations.
 * @param <T> type of evaluation result
 */
abstract class AbstractLazy<T> implements Lazy<T> {

    /**
     * Object that shows emptiness instead of null (because Supplier may return null).
     */
    protected static Object none = new Object();
    /**
     * Evaluation.
     */
    protected Supplier<T> supplier;

    protected AbstractLazy(@NotNull Supplier<T> supplier) {
        this.supplier = supplier;
    }

}
