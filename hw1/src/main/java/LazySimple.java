import com.sun.istack.internal.NotNull;

import java.util.function.Supplier;

/**
 * Lazy implementation for one thread. No guarantees for multiple threads.
 * @param <T> type of evaluation result
 */
public class LazySimple<T> extends AbstractLazy<T> {

    /**
     * Evaluation result.
     */
    private T result = (T) none;

    public LazySimple(@NotNull Supplier<T> supplier) {
        super(supplier);
    }

    /**
     * Starts evaluation.
     * Evaluation will be done only for first call.
     * For all following calls it returns the same object as for the first call.
     * @return evaluation result
     */
    public T get() {
        if (supplier != null) {
            result = supplier.get();
            supplier = null;
        }
        return result;
    }
}
