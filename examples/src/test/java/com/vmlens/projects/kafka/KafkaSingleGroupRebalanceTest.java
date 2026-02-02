package com.vmlens.projects.kafka;

import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.*;

import static com.vmlens.api.Runner.runParallel;

@Testcontainers
public class KafkaSingleGroupRebalanceTest {

    private static final String TOPIC = "rebalance-topic";

    @Container
    static KafkaContainer kafka =
            new KafkaContainer(
                    DockerImageName.parse("confluentinc/cp-kafka:7.5.1")
            );

    private Producer<String, String> producer;

    @BeforeEach
    void setUp() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafka.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());

        producer = new KafkaProducer<>(props);
    }

    @AfterEach
    void tearDown() {
        producer.close();
    }

    private KafkaConsumer<String, String> consumer(String clientId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-1");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        return new KafkaConsumer<>(props);
    }

    @Test
    void noOffsetProcessedConcurrentlyDuringRebalance() throws Exception {
        Runnable consumerLogic = () -> {
            try (KafkaConsumer<String, String> consumer =
                         consumer(UUID.randomUUID().toString())) {
                consumer.subscribe(List.of(TOPIC));
                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofNanos(1L));
                consumer.commitSync();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        for (int i = 0; i < 10; i++) {
            producer.send(new ProducerRecord<>(TOPIC, "k", "msg-" + i));
        }
        producer.flush();

        try (AllInterleavings allInterleavings =
                     new AllInterleavingsBuilder().withRemoveCycleThreshold(5)
                             .withMaximumIterations(1).build("kafka-rebalance")) {
            while (allInterleavings.hasNext()) {
                runParallel(
                        consumerLogic,
                        consumerLogic
                );
            }
        }

    }
}
