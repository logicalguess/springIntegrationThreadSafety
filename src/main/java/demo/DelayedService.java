package demo;

import org.springframework.integration.annotation.ServiceActivator;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DelayedService {
    private AtomicInteger counter = new AtomicInteger(0);

    @ServiceActivator
    public void justDelay(Object obj) throws Exception {
        System.out.println("Delays in thread " + this + " - " + Thread.currentThread().getId());
        counter.incrementAndGet();
        TimeUnit.SECONDS.sleep(new Random().nextInt(10));
        System.out.println("Ends Delaying in thread " + Thread.currentThread().getId() + ", value = " + counter);
    }
}