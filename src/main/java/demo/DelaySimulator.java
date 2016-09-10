package demo;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DelaySimulator {
    private ScheduledExecutorService executor;
    private Random rand = new Random();

    public DelaySimulator(ThreadPoolTaskScheduler scheduler) {
        this.executor = scheduler.getScheduledExecutor();
    }

    public void simulateLongRunningTransaction() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        executor.schedule(() -> {countDownLatch.countDown();}, rand.nextInt(10), TimeUnit.SECONDS);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            //
        }
    }
}
