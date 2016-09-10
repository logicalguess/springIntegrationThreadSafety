package demo;

import org.springframework.integration.annotation.ServiceActivator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class DelayedService implements Function<String, String> {

    private AtomicInteger counter = new AtomicInteger(0);
    private DelaySimulator simulator;

    public DelayedService(DelaySimulator simulator) {
        this.simulator = simulator;
    }

    @ServiceActivator
    public String apply(String in) {
        Integer incremented = counter.incrementAndGet();
        simulator.simulateLongRunningTransaction();
        return String.format("incremented=%s, final=%s", incremented, counter);
    }
}