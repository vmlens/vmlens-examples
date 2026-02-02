package com.vmlens.projects.pulsar;

import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import org.apache.pulsar.client.api.*;

import java.util.concurrent.TimeUnit;

import static com.vmlens.api.Runner.runParallel;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PulsarTest {

    private static final DockerImageName PULSAR_IMAGE =
            DockerImageName.parse("apachepulsar/pulsar:3.2.2");

    private GenericContainer<?> pulsar;
    private PulsarClient client;

    @BeforeAll
    void startPulsar() throws Exception {
        pulsar =
                new GenericContainer<>(PULSAR_IMAGE)
                        .withExposedPorts(6650, 8080)
                        .withCommand("bin/pulsar standalone");

        pulsar.start();

        String serviceUrl =
                "pulsar://" + pulsar.getHost() + ":" + pulsar.getMappedPort(6650);

        client =
                PulsarClient.builder()
                        .serviceUrl(serviceUrl)
                        .build();
    }

    @AfterAll
    void stopPulsar() throws Exception {
        if (client != null) {
            client.close();
        }
        if (pulsar != null) {
            pulsar.stop();
        }
    }

    @Test
    void shouldProduceAndConsumeMessage() throws Exception {
        String topic = "persistent://public/default/test-topic";

        Producer<String> producer =
                client.newProducer(Schema.STRING)
                        .topic(topic)
                        .create();

        Consumer<String> consumer =
                client.newConsumer(Schema.STRING)
                        .topic(topic)
                        .subscriptionName("test-subscription")
                        .subscriptionType(SubscriptionType.Exclusive)
                        .subscribe();

        try(AllInterleavings allInterleavings =
                    new AllInterleavingsBuilder()
                            .build("pulsarConsumer")) {
            while(allInterleavings.hasNext()) {
                producer.sendAsync("hello pulsar");
                producer.sendAsync("hello pulsar");
                runParallel(() -> {
                            Message<String> msg = null;
                            try {
                                msg = consumer.receive(5, TimeUnit.MICROSECONDS);
                            } catch (PulsarClientException e) {
                                throw new RuntimeException(e);
                            }
                            if(msg != null) {
                                try {
                                    consumer.acknowledge(msg);
                                } catch (PulsarClientException e) {
                                    throw new RuntimeException(e);
                                }
                            }} ,
                        () -> {
                            Message<String> msg = null;
                            try {
                                msg = consumer.receive(5, TimeUnit.MICROSECONDS);
                            } catch (PulsarClientException e) {
                                throw new RuntimeException(e);
                            }
                            if(msg != null) {
                                try {
                                    consumer.acknowledge(msg);
                                } catch (PulsarClientException e) {
                                    throw new RuntimeException(e);
                                }
                            }}
            );
            }

        }



        producer.close();
        consumer.close();
    }
}

