package demo;

import org.springframework.integration.annotation.ServiceActivator;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DelayedService {
    private AtomicInteger counter = new AtomicInteger(0);

    @ServiceActivator
    public String apply(Object obj) throws Exception {
        Integer incremented = counter.incrementAndGet();
        TimeUnit.SECONDS.sleep(new Random().nextInt(10));
        return String.format("incremented=%s, final=%s", incremented, counter);
    }
}