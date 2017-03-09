import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Lazy implementation for multiple threads.
 * @param <T> type of evaluation result
 */
public class LazyConcurrent<T> extends AbstractLazy<T> {

    /**
     * Evaluation result.
     */
    private volatile T result = (T) none;

    public LazyConcurrent(@NotNull Supplier<T> supplier) {
        super(supplier);
    }

    /**
     * Starts evaluation.
     * Evaluation will be done only for first call.
     * For all following calls it returns the same object as for the first call.
     * @return evaluation result
     */
    public T get() {
        if (result == none) {
            synchronized (this) {
                if (result == none) {
                    result = supplier.get();
                    supplier = null;
                }
            }
        }
        return result;
    }
}
