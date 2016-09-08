package demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.concurrent.TimeUnit;


public class Test {
    private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
            "applicationContext.xml");

    private static TaskExecutor taskExec = (TaskExecutor) context.getBean("taskExec");
    private static MessageChannel inChannel = (MessageChannel) context.getBean("channel-in");

    public static void main(String[] args) throws Exception {
        Runnable r = new Runnable() {

            public void run() {
                System.out.println("Saying hello from thread : " + Thread.currentThread().getId());
                Message<String> message = MessageBuilder.withPayload("").build();
                inChannel.send(message);
            }
        };

        for (int k = 0; k < 10; ++k) {
            taskExec.execute(r);
        }

        TimeUnit.SECONDS.sleep(10);
    }
}