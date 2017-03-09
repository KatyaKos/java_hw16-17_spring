import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Class that gives user ability to create three Lazy implementations:
 * for one thread, for multiple threads and lock-free.
 */
public class LazyFactory {

    /**
     * Creates implementation for one thread.
     * @param supplier evaluation t obe done
     * @param <T> type of evaluation result
     * @return Lazy object for one thread
     */
    public static <T> Lazy<T> createLazySimple(@NotNull Supplier<T> supplier) {
        return new LazySimple<>(supplier);
    }

    /**
     * Creates implementation for multiple threads.
     * @param supplier evaluation to be done
     * @param <T> type of evaluation result
     * @return Lazy object for multiple threads
     */
    public static <T> Lazy<T> createLazyConcurrent(@NotNull Supplier<T> supplier) {
        return new LazyConcurrent<>(supplier);
    }

    /**
     * Creates implementation for lock-free.
     * @param supplier evaluation to be done
     * @param <T> type of evaluation result
     * @return Lazy object for lock-free
     */
    public static <T> Lazy<T> createLazyLockFree(@NotNull Supplier<T> supplier) {
        return new LazyLockFree<>(supplier);
    }
}
