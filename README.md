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
        public String apply(Object obj) throws Exception {
            Integer incremented = counter.incrementAndGet();
            TimeUnit.SECONDS.sleep(new Random().nextInt(10));
            return String.format("incremented=%s, final=%s", incremented, counter);
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
                    Message<String> message = MessageBuilder.withPayload("").build();
                    inChannel.send(message);
                }
            };

            for (int k = 0; k < 10; ++k) {
                taskExec.execute(r);
            }

            for (int k = 0; k < 10; ++k) {
                System.out.println("RECEIVED MESSAGE: " + outChannel.receive().getPayload());
            }
            System.exit(0);
        }
    }
#
    RECEIVED MESSAGE: incremented=2, final=2
    RECEIVED MESSAGE: incremented=10, final=10
    RECEIVED MESSAGE: incremented=4, final=10
    RECEIVED MESSAGE: incremented=3, final=10
    RECEIVED MESSAGE: incremented=7, final=10
    RECEIVED MESSAGE: incremented=9, final=10
    RECEIVED MESSAGE: incremented=1, final=10
    RECEIVED MESSAGE: incremented=6, final=10
    RECEIVED MESSAGE: incremented=8, final=10
    RECEIVED MESSAGE: incremented=5, final=10
