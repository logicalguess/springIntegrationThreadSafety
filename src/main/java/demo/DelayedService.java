package demo;

import org.springframework.integration.annotation.ServiceActivator;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class DelayedService implements Function<String, String> {
    private AtomicInteger counter = new AtomicInteger(0);

    Random rand = new Random();

    @ServiceActivator
    public String apply(String in) {
        Integer incremented = counter.incrementAndGet();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {countDownLatch.countDown();}, rand.nextInt(10), TimeUnit.SECONDS);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            //
        }
        executor.shutdown();
        return String.format("incremented=%s, final=%s", incremented, counter);
    }
}