package demo;

import org.springframework.integration.annotation.ServiceActivator;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ServiceAggregator implements Function<Collection<String>, String> {

    @ServiceActivator
    public String apply(Collection<String> in) {
        return String.format("collection=%s", in);
    }
}