Integrating embedded Kafka in a Cucumber test setup for a Spring application involves the following steps:

1. Add Dependencies
Add the required dependencies to your pom.xml or build.gradle:

Maven:

xml
Copy
Edit
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-spring</artifactId>
    <version>your-cucumber-version</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>your-cucumber-version</version>
    <scope>test</scope>
</dependency>
Gradle:

gradle
Copy
Edit
testImplementation 'org.springframework.kafka:spring-kafka-test'
testImplementation 'io.cucumber:cucumber-spring:your-cucumber-version'
testImplementation 'io.cucumber:cucumber-java:your-cucumber-version'
2. Enable Embedded Kafka
Use the @EmbeddedKafka annotation from the spring-kafka-test library. Create a configuration class for your Kafka tests:

java
Copy
Edit
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

@TestConfiguration
@EmbeddedKafka(partitions = 1, topics = {"test-topic"})
public class KafkaTestConfig {

    @Bean
    public EmbeddedKafkaBroker embeddedKafkaBroker() {
        return new EmbeddedKafkaBroker(1, true, "test-topic");
    }
}
3. Configure Kafka Properties
In your test configuration (application.yml or application.properties), ensure that the application uses the embedded Kafka:

yaml
Copy
Edit
spring:
  kafka:
    bootstrap-servers: ${spring.embedded.kafka.brokers}
4. Cucumber Glue Code
Set up Cucumber glue code to produce and consume Kafka messages during tests:

Producer:

java
Copy
Edit
@Autowired
private KafkaTemplate<String, String> kafkaTemplate;

public void sendMessage(String topic, String message) {
    kafkaTemplate.send(topic, message);
}
Consumer:

java
Copy
Edit
@KafkaListener(topics = "test-topic", groupId = "test-group")
public void listen(String message) {
    // Logic to process the message
}
5. Step Definitions
Write step definitions to interact with Kafka:

java
Copy
Edit
import static org.assertj.core.api.Assertions.assertThat;

public class KafkaStepDefinitions {

    @Autowired
    private KafkaTestConfig kafkaTestConfig;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @When("a message {string} is sent to topic {string}")
    public void aMessageIsSentToTopic(String message, String topic) {
        kafkaTemplate.send(topic, message);
    }

    @Then("the message {string} should be consumed")
    public void theMessageShouldBeConsumed(String expectedMessage) {
        // Implement logic to verify the message was consumed
        // For example, using a ConsumerRecord or asserting state
    }
}
6. Cucumber Test Runner
Create a test runner to execute the Cucumber tests:

java
Copy
Edit
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest
public class CucumberSpringConfiguration {
}
7. Feature File
Define the feature file to test Kafka functionality:

gherkin
Copy
Edit
Feature: Kafka integration

  Scenario: Sending and receiving messages
    When a message "Hello Kafka" is sent to topic "test-topic"
    Then the message "Hello Kafka" should be consumed
8. Run the Tests
Execute the Cucumber tests to validate your Kafka integration.
