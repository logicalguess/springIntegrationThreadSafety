        <task:executor id="taskExec" pool-size="50" keep-alive="120" />

    	<bean id="service" class="demo.DelayedService"/>

        <int:channel id="channel-in">
            <int:queue capacity="10"/>
        </int:channel>

    	<int:service-activator ref="service" input-channel="channel-in">
            <int:poller fixed-delay="100" time-unit="MILLISECONDS" task-executor="taskExec"></int:poller>
        </int:service-activator>

#
    public class DelayedService {
        private AtomicInteger counter = new AtomicInteger(0);

        @ServiceActivator
        public void apply(Object obj) throws Exception {
            System.out.println("Delays in thread " + this + " - " + Thread.currentThread().getId());
            counter.incrementAndGet();
            TimeUnit.SECONDS.sleep(new Random().nextInt(10));
            System.out.println("Ends Delaying in thread " + Thread.currentThread().getId() + ", value = " + counter);
        }
    }

#
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
#
    Saying hello from thread : 14
    Saying hello from thread : 15
    Saying hello from thread : 16
    Saying hello from thread : 17
    Saying hello from thread : 18
    Saying hello from thread : 19
    Saying hello from thread : 20
    Saying hello from thread : 21
    Saying hello from thread : 22
    Saying hello from thread : 23
    Delays in thread demo.DelayedService@4119dc89 - 12
    Delays in thread demo.DelayedService@4119dc89 - 24
    Delays in thread demo.DelayedService@4119dc89 - 26
    Delays in thread demo.DelayedService@4119dc89 - 28
    Delays in thread demo.DelayedService@4119dc89 - 30
    Delays in thread demo.DelayedService@4119dc89 - 32
    Delays in thread demo.DelayedService@4119dc89 - 34
    Delays in thread demo.DelayedService@4119dc89 - 36
    Delays in thread demo.DelayedService@4119dc89 - 38
    Ends Delaying in thread 38, value = 9
    Delays in thread demo.DelayedService@4119dc89 - 38
    Ends Delaying in thread 26, value = 10
    Ends Delaying in thread 32, value = 10
    Ends Delaying in thread 34, value = 10
    Ends Delaying in thread 36, value = 10
    Ends Delaying in thread 24, value = 10
    Ends Delaying in thread 28, value = 10
    Ends Delaying in thread 12, value = 10
    Ends Delaying in thread 38, value = 10
    Ends Delaying in thread 30, value = 10
