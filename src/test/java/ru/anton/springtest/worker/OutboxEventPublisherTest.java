package ru.anton.springtest.worker;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.anton.springtest.config.OutboxPublisherProperties;
import ru.anton.springtest.model.OutboxEvent;
import ru.anton.springtest.model.OutboxEventStatus;
import ru.anton.springtest.repository.OutboxEventRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OutboxEventPublisher — юнит-тесты")
class OutboxEventPublisherTest {

    @Mock
    OutboxEventRepository outboxEventRepository;

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    TransactionTemplate transactionTemplate;

    OutboxPublisherProperties properties;
    OutboxEventPublisher publisher;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        properties = new OutboxPublisherProperties();
        properties.setBatchSize(50);
        properties.setDefaultTopic("default.topic");
        properties.setTopicMapping(Map.of("Mapped", "mapped.topic"));
        properties.setMaxAttempts(2);
        properties.setRetryBaseDelayMs(100);
        properties.setStaleTimeoutMs(120_000);

        publisher = new OutboxEventPublisher(outboxEventRepository, kafkaTemplate, transactionTemplate, properties);

        given(transactionTemplate.execute(any(TransactionCallback.class))).willAnswer(invocation -> {
            TransactionCallback<Object> callback = invocation.getArgument(0);
            return callback.doInTransaction(mock(TransactionStatus.class));
        });

        doAnswer(invocation -> {
            Consumer<TransactionStatus> action = invocation.getArgument(0);
            action.accept(mock(TransactionStatus.class));
            return null;
        }).when(transactionTemplate).executeWithoutResult(any(Consumer.class));
    }

    private OutboxEvent newEvent(String eventType) {
        OutboxEvent event = new OutboxEvent();
        event.setId(UUID.randomUUID());
        event.setEventType(eventType);
        event.setAggregateId(UUID.randomUUID());
        event.setPayload("{}");
        event.setAttempts(0);
        return event;
    }

    @Test
    @DisplayName("claim помечает событие PROCESSING и выдаёт ему уникальный claimToken")
    void publishPendingEvents_claimsAndAssignsClaimToken() {
        OutboxEvent event = newEvent("Mapped");
        given(outboxEventRepository.claimPendingEvents(anyInt(), any())).willReturn(List.of(event));
        given(kafkaTemplate.send(any(ProducerRecord.class)))
                .willReturn(CompletableFuture.completedFuture(mock(SendResult.class)));
        given(outboxEventRepository.markSent(any(), any(), any())).willReturn(1);

        publisher.publishPendingEvents();

        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PROCESSING);
        assertThat(event.getClaimToken()).isNotNull();
        assertThat(event.getClaimedAt()).isNotNull();
        verify(outboxEventRepository).saveAll(List.of(event));
    }

    @Test
    @DisplayName("успешная отправка помечает событие SENT с текущим claimToken")
    void publishPendingEvents_onSuccess_marksSentWithClaimToken() {
        OutboxEvent event = newEvent("Mapped");
        given(outboxEventRepository.claimPendingEvents(anyInt(), any())).willReturn(List.of(event));
        given(kafkaTemplate.send(any(ProducerRecord.class)))
                .willReturn(CompletableFuture.completedFuture(mock(SendResult.class)));
        given(outboxEventRepository.markSent(any(), any(), any())).willReturn(1);

        publisher.publishPendingEvents();

        ArgumentCaptor<UUID> claimTokenCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(outboxEventRepository).markSent(org.mockito.ArgumentMatchers.eq(event.getId()), claimTokenCaptor.capture(), any());
        assertThat(claimTokenCaptor.getValue()).isEqualTo(event.getClaimToken());
    }

    @Test
    @DisplayName("отправка в маппированный eventType использует топик из topic-mapping")
    void publishPendingEvents_usesMappedTopic() {
        OutboxEvent event = newEvent("Mapped");
        given(outboxEventRepository.claimPendingEvents(anyInt(), any())).willReturn(List.of(event));
        given(kafkaTemplate.send(any(ProducerRecord.class)))
                .willReturn(CompletableFuture.completedFuture(mock(SendResult.class)));
        given(outboxEventRepository.markSent(any(), any(), any())).willReturn(1);

        publisher.publishPendingEvents();

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(recordCaptor.capture());
        assertThat(recordCaptor.getValue().topic()).isEqualTo("mapped.topic");
    }

    @Test
    @DisplayName("немаппированный eventType уходит в default-topic")
    void publishPendingEvents_unmappedEventType_usesDefaultTopic() {
        OutboxEvent event = newEvent("Unknown");
        given(outboxEventRepository.claimPendingEvents(anyInt(), any())).willReturn(List.of(event));
        given(kafkaTemplate.send(any(ProducerRecord.class)))
                .willReturn(CompletableFuture.completedFuture(mock(SendResult.class)));
        given(outboxEventRepository.markSent(any(), any(), any())).willReturn(1);

        publisher.publishPendingEvents();

        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(recordCaptor.capture());
        assertThat(recordCaptor.getValue().topic()).isEqualTo("default.topic");
    }

    @Test
    @DisplayName("сбой отправки при attempts < maxAttempts планирует повтор через markForRetry")
    void publishPendingEvents_onFailure_schedulesRetry() {
        OutboxEvent event = newEvent("Mapped");
        given(outboxEventRepository.claimPendingEvents(anyInt(), any())).willReturn(List.of(event));
        given(kafkaTemplate.send(any(ProducerRecord.class)))
                .willReturn(CompletableFuture.failedFuture(new RuntimeException("kafka недоступна")));
        given(outboxEventRepository.markForRetry(any(), any(), anyInt(), any())).willReturn(1);

        publisher.publishPendingEvents();

        verify(outboxEventRepository).markForRetry(
                org.mockito.ArgumentMatchers.eq(event.getId()), any(), org.mockito.ArgumentMatchers.eq(1), any());
        verify(outboxEventRepository, never()).markFailed(any(), any(), anyInt());
    }

    @Test
    @DisplayName("сбой отправки при исчерпанных попытках помечает событие FAILED")
    void publishPendingEvents_onFailure_exhaustedAttempts_marksFailed() {
        OutboxEvent event = newEvent("Mapped");
        event.setAttempts(1); // maxAttempts = 2, следующая попытка станет последней
        given(outboxEventRepository.claimPendingEvents(anyInt(), any())).willReturn(List.of(event));
        given(kafkaTemplate.send(any(ProducerRecord.class)))
                .willReturn(CompletableFuture.failedFuture(new RuntimeException("kafka недоступна")));
        given(outboxEventRepository.markFailed(any(), any(), anyInt())).willReturn(1);

        publisher.publishPendingEvents();

        verify(outboxEventRepository).markFailed(
                org.mockito.ArgumentMatchers.eq(event.getId()), any(), org.mockito.ArgumentMatchers.eq(2));
        verify(outboxEventRepository, never()).markForRetry(any(), any(), anyInt(), any());
    }

    @Test
    @DisplayName("если claimToken уже устарел (0 обновлённых строк), паблишер не падает")
    void publishPendingEvents_lostClaim_doesNotThrow() {
        OutboxEvent event = newEvent("Mapped");
        given(outboxEventRepository.claimPendingEvents(anyInt(), any())).willReturn(List.of(event));
        given(kafkaTemplate.send(any(ProducerRecord.class)))
                .willReturn(CompletableFuture.completedFuture(mock(SendResult.class)));
        given(outboxEventRepository.markSent(any(), any(), any())).willReturn(0);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> publisher.publishPendingEvents());
    }
}
