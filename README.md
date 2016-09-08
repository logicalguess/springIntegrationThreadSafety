        <task:executor id="taskExec" pool-size="50" keep-alive="120"/>

        <bean id="service" class="demo.DelayedService"/>

        <int:channel id="channel-in">
            <int:queue capacity="10"/>
        </int:channel>

        <int:channel id="channel-out">
            <int:queue capacity="10"/>
        </int:channel>

        <int:service-activator ref="service" input-channel="channel-in" output-channel="channel-out">
            <int:poller fixed-delay="100" time-unit="MILLISECONDS" task-executor="taskExec"></int:poller>
        </int:service-activator>


#
    public class DelayedService {
        private AtomicInteger counter = new AtomicInteger(0);

        @ServiceActivator
        public Integer apply(Object obj) throws Exception {
            System.out.println("Delays in thread " + this + " - " + Thread.currentThread().getId());
            Integer incremented = counter.incrementAndGet();
            TimeUnit.SECONDS.sleep(new Random().nextInt(10));
            System.out.println("Ends Delaying in thread " + Thread.currentThread().getId() + ", value = " + counter);
            return incremented;
        }
    }

#
    public class Test {
        private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "applicationContext.xml");

        private static TaskExecutor taskExec = (TaskExecutor) context.getBean("taskExec");
        private static MessageChannel inChannel = (MessageChannel) context.getBean("channel-in");
        private static QueueChannel outChannel = (QueueChannel) context.getBean("channel-out");


        public static void main(String[] args) throws Exception {
            Runnable r = new Runnable() {

                public void run() {
                    System.out.println("SENDING MESSAGE FROM THREAD : " + Thread.currentThread().getId());
                    Message<String> message = MessageBuilder.withPayload("").build();
                    inChannel.send(message);
                }
            };

            for (int k = 0; k < 10; ++k) {
                taskExec.execute(r);
            }

            for (int k = 0; k < 10; ++k) {
                System.out.println("GOT MESSAGE: " + outChannel.receive().getPayload());
            }

        }

    }
#
    SENDING MESSAGE FROM THREAD : 14
    SENDING MESSAGE FROM THREAD : 15
    SENDING MESSAGE FROM THREAD : 16
    SENDING MESSAGE FROM THREAD : 17
    SENDING MESSAGE FROM THREAD : 18
    SENDING MESSAGE FROM THREAD : 19
    SENDING MESSAGE FROM THREAD : 20
    SENDING MESSAGE FROM THREAD : 21
    SENDING MESSAGE FROM THREAD : 22
    Delays in thread demo.DelayedService@2209a59d - 12
    SENDING MESSAGE FROM THREAD : 23
    Delays in thread demo.DelayedService@2209a59d - 24
    Delays in thread demo.DelayedService@2209a59d - 26
    Delays in thread demo.DelayedService@2209a59d - 28
    Delays in thread demo.DelayedService@2209a59d - 30
    Delays in thread demo.DelayedService@2209a59d - 32
    Delays in thread demo.DelayedService@2209a59d - 34
    Delays in thread demo.DelayedService@2209a59d - 36
    Delays in thread demo.DelayedService@2209a59d - 38
    Delays in thread demo.DelayedService@2209a59d - 40
    Ends Delaying in thread 24, value = 10
    GOT MESSAGE: 2
    Ends Delaying in thread 26, value = 10
    GOT MESSAGE: 3
    Ends Delaying in thread 32, value = 10
    GOT MESSAGE: 6
    Ends Delaying in thread 30, value = 10
    GOT MESSAGE: 5
    Ends Delaying in thread 34, value = 10
    GOT MESSAGE: 7
    Ends Delaying in thread 28, value = 10
    GOT MESSAGE: 4
    Ends Delaying in thread 38, value = 10
    GOT MESSAGE: 9
    Ends Delaying in thread 40, value = 10
    GOT MESSAGE: 10
    Ends Delaying in thread 12, value = 10
    GOT MESSAGE: 1
    Ends Delaying in thread 36, value = 10
    GOT MESSAGE: 8
