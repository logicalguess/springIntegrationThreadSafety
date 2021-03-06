package demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.stream.IntStream;

public class Test {
    private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
            "applicationContext.xml");

    private static TaskExecutor taskExec = (TaskExecutor) context.getBean("taskExec");
    private static MessageChannel inChannel = (MessageChannel) context.getBean("channel-in");
    private static QueueChannel outChannel = (QueueChannel) context.getBean("channel-out");


    public static void main(String[] args) throws Exception {
        int count = 10;

        IntStream.rangeClosed(1, count).forEach((i) -> taskExec.execute(() -> {
            Message<String> message = MessageBuilder.withPayload("").build();
            inChannel.send(message);
        }));

        IntStream.rangeClosed(1, count).forEach((i) -> {
                System.out.println("RECEIVED MESSAGE: " + outChannel.receive().getPayload());
        });

        System.exit(0);
    }
}