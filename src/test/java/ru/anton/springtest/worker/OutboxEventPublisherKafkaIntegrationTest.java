package ru.anton.springtest.worker;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.kafka.KafkaContainer;
import ru.anton.springtest.AbstractIntegrationTest;
import ru.anton.springtest.model.OutboxEvent;
import ru.anton.springtest.model.OutboxEventStatus;
import ru.anton.springtest.repository.OutboxEventRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OutboxEventPublisher — взаимодействие с реальной Kafka")
@Import(OutboxEventPublisherKafkaIntegrationTest.KafkaTestcontainersConfiguration.class)
class OutboxEventPublisherKafkaIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    OutboxEventRepository outboxEventRepository;

    @Autowired
    OutboxEventPublisher outboxEventPublisher;

    @Autowired
    KafkaContainer kafkaContainer;

    @Test
    @DisplayName("паблишер отправляет outbox-событие в реальный топик Kafka с заголовком eventId и помечает его SENT")
    void publishPendingEvents_sendsRealMessageAndMarksSent() {
        OutboxEvent event = new OutboxEvent();
        event.setEventType("DeliveryCreated");
        event.setAggregateId(UUID.randomUUID());
        event.setPayload("{\"address\":\"Test\"}");
        event = outboxEventRepository.save(event);

        try (KafkaConsumer<String, String> consumer = createConsumer()) {
            consumer.subscribe(List.of("delivery.created"));

            outboxEventPublisher.publishPendingEvents();

            ConsumerRecord<String, String> record = pollSingleRecord(consumer);

            assertThat(record.key()).isEqualTo(event.getAggregateId().toString());
            assertThat(record.value()).contains("Test");
            assertThat(new String(record.headers().lastHeader("eventId").value())).isEqualTo(event.getId().toString());
        }

        OutboxEvent reloaded = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OutboxEventStatus.SENT);
        assertThat(reloaded.getClaimToken()).isNull();
    }

    private ConsumerRecord<String, String> pollSingleRecord(KafkaConsumer<String, String> consumer) {
        List<ConsumerRecord<String, String>> received = new ArrayList<>();
        long deadline = System.currentTimeMillis() + 15_000;
        while (received.isEmpty() && System.currentTimeMillis() < deadline) {
            consumer.poll(Duration.ofMillis(500)).forEach(received::add);
        }
        assertThat(received).hasSize(1);
        return received.getFirst();
    }

    private KafkaConsumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "outbox-publisher-test-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return new KafkaConsumer<>(props);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class KafkaTestcontainersConfiguration {

        @Bean
        @ServiceConnection
        KafkaContainer kafkaContainer() {
            return new KafkaContainer("apache/kafka:3.8.0");
        }
    }
}
