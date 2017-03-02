import com.sun.istack.internal.NotNull;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

/**
 * Lazy lock-free implementation for multiple threads.
 * @param <T> type of evaluation result
 */
public class LazyLockFree<T> extends AbstractLazy<T> {

    /**
     * Evaluation result.
     */
    private volatile T result = (T) none;
    /**
     * Atomic updater for result.
     */
    private static final AtomicReferenceFieldUpdater<LazyLockFree, Object> updater = AtomicReferenceFieldUpdater.newUpdater(LazyLockFree.class, Object.class, "result");

    public LazyLockFree(@NotNull Supplier<T> supplier) {
        super(supplier);
    }

    /**
     * Starts evaluation.
     * It may be called multiple times (unlike LazySimple and LazyConcurrent).
     * @return evaluation result
     */
    public T get() {
        if (result == none) {
            Supplier<T> local = supplier;
            if (local != null) {
                if (updater.compareAndSet(this, none, local.get())) {
                    supplier = null;
                }
            }
        }
        return result;
    }
}
