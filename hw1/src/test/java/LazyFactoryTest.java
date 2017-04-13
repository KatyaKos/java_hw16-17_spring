import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * Created by KatyaKos on 02.03.2017.
 */
public class LazyFactoryTest {

    private static final int threadsNumber = 10;

    private static final Supplier<Long> supplierFact = () -> {
        long result = 1;
        for (int i = 0; i <= 10000; i++) {
            result *= i;
        }
        return result;
    };

    private static final Supplier<Object> supplierNull = () -> null;

    private <T> void basicTest(Lazy<T> lazy, Supplier<T> supplier) {
        final T result = lazy.get();
        assertEquals(supplier.get(), result);
        final T anotherResult = lazy.get();
        assertSame(result, anotherResult);
    }

    @Test
    public void basicTest() {
        LazyFactory.createLazySimple(new MySupplier<>(supplierFact, 0));
        LazyFactory.createLazyConcurrent(new MySupplier<>(supplierFact, 0));
        LazyFactory.createLazyLockFree(new MySupplier<>(supplierFact, 0));

        basicTest(LazyFactory.createLazySimple(new MySupplier<>(supplierFact, 1)), supplierFact);
        basicTest(LazyFactory.createLazyConcurrent(new MySupplier<>(supplierFact, 1)), supplierFact);
        basicTest(LazyFactory.createLazyLockFree(new MySupplier<>(supplierFact, 1)), supplierFact);
    }

    private <T> void nullTest(Lazy<T> lazy) {
        final T result = lazy.get();
        assertEquals(null, result);
        final T anotherResult = lazy.get();
        assertSame(result, anotherResult);
    }

    @Test
    public void nullTest() {
        nullTest(LazyFactory.createLazySimple(new MySupplier<>(supplierNull, 1)));
        nullTest(LazyFactory.createLazyConcurrent(new MySupplier<>(supplierNull, 1)));
        nullTest(LazyFactory.createLazyLockFree(new MySupplier<>(supplierNull, 1)));
    }

    private <T> void multithreadTest(Lazy<T> lazy, Supplier<T> supplier) throws InterruptedException {
        ArrayList<Thread> threads = new ArrayList<>();
        Runnable runnable = () -> basicTest(lazy, supplier);

        for (int i = 0; i < threadsNumber; i++) {
            Thread thread = new Thread(runnable);
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    public void multithreadTest() throws InterruptedException {
        multithreadTest(LazyFactory.createLazyConcurrent(new MySupplier<>(supplierFact, 1)), supplierFact);
        multithreadTest(LazyFactory.createLazyLockFree(supplierFact), supplierFact);
    }


    private static class MySupplier<T> implements Supplier<T> {

        final Supplier<T> supplier;
        final AtomicInteger limit;

        MySupplier(Supplier<T> supplier, int limit) {
            this.supplier = supplier;
            this.limit = new AtomicInteger(limit);
        }

        @Override
        public T get() {
            if (limit.get() == 0) {
                throw new RuntimeException();
            } else {
                limit.decrementAndGet();
            }
            return supplier.get();
        }
    }

}