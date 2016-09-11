#
        <task:scheduler id="taskExec" pool-size="50"/>

        <bean id="simulator" class="demo.DelaySimulator">
            <constructor-arg ref="taskExec"/>
        </bean>

        <bean id="service" class="demo.DelayedService">
            <constructor-arg ref="simulator"/>
        </bean>

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

#
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
