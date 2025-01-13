

Get Outlook for Android
From: Black Star <harishmathur377@gmail.com>
Sent: Monday, January 13, 2025 10:11:21 PM
To: brijesh_tiwari@hotmail.com <brijesh_tiwari@hotmail.com>
Subject: Kafka
 
Creating multiple Kafka consumers dynamically in a Spring Boot application involves using a configuration-driven approach. You can create and register multiple consumers for the same consumer group based on application configurations.


---

Step-by-Step Implementation

1. Update Your Configuration

Add a property to your application.yml or application.properties file to specify the number of consumers to create.


kafka:
  bootstrap-servers: localhost:9092
  topic: your-topic-name
  consumer-group: your-consumer-group
  consumers: 10




---

2. Define a Kafka Consumer Factory

Use ConcurrentKafkaListenerContainerFactory to create a Kafka consumer container dynamically.


@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer-group}")
    private String consumerGroup;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}




---

3. Create Consumers Dynamically

Use a @Bean or an initializer method to create multiple consumer containers based on the configuration.


@Component
public class KafkaConsumerInitializer {

    private final KafkaMessageListenerContainer<String, String>[] consumers;
    private final KafkaConsumerConfig kafkaConsumerConfig;
    private final KafkaListenerEndpointRegistry registry;

    @Value("${kafka.topic}")
    private String topic;

    @Value("${kafka.consumers}")
    private int consumerCount;

    public KafkaConsumerInitializer(
            KafkaConsumerConfig kafkaConsumerConfig,
            KafkaListenerEndpointRegistry registry) {
        this.kafkaConsumerConfig = kafkaConsumerConfig;
        this.registry = registry;
        this.consumers = new KafkaMessageListenerContainer[consumerCount];
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeConsumers() {
        for (int i = 0; i < consumerCount; i++) {
            String containerId = "consumer-" + i;
            consumers[i] = createContainer(containerId);
            consumers[i].start();
        }
    }

    private KafkaMessageListenerContainer<String, String> createContainer(String containerId) {
        ContainerProperties containerProps = new ContainerProperties(topic);
        containerProps.setGroupId(containerId); // Optional: If you want different group IDs for debugging
        containerProps.setMessageListener((MessageListener<String, String>) record -> {
            System.out.println("Received message: " + record.value() + " by " + containerId);
        });

        return new KafkaMessageListenerContainer<>(kafkaConsumerConfig.consumerFactory(), containerProps);
    }
}




---

4. Add a KafkaListener for Each Consumer (Optional)

Alternatively, use @KafkaListener annotations to create consumers dynamically with unique id values.


@KafkaListener(
        topics = "${kafka.topic}",
        groupId = "${kafka.consumer-group}",
        containerFactory = "kafkaListenerContainerFactory"
)
public void listen(String message) {
    System.out.println("Received Message: " + message);
}




---

5. Verify Consumers in the Kafka Group

When the application starts, you can check the consumer group using the following Kafka CLI command:


kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group your-consumer-group

This should show 10 consumers (or the configured number) assigned to the group.




---

Benefits of This Approach

1. Dynamic Scaling:

The number of consumers can be changed easily by updating the configuration.



2. Centralized Management:

All consumers are part of the same group and are managed together.



3. Load Balancing:

Kafka will automatically assign partitions among the consumers in the group.



