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
        public void justDelay(Object obj) throws Exception {
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
