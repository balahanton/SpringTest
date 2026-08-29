package ru.anton.springtest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anton.springtest.model.OutboxEvent;
import ru.anton.springtest.repository.OutboxEventRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("OutboxEventService — юнит-тесты")
class OutboxEventServiceTest {

    @Mock
    OutboxEventRepository outboxEventRepository;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    OutboxEventService outboxEventService;

    UUID aggregateId;

    @BeforeEach
    void setUp() {
        aggregateId = UUID.randomUUID();
    }

    @Test
    @DisplayName("save сериализует payload и сохраняет outbox-событие в статусе по умолчанию")
    void save_serializesPayloadAndPersistsEvent() {
        record Payload(String address) {
        }
        given(objectMapper.writeValueAsString(any())).willReturn("{\"address\":\"Test\"}");

        outboxEventService.save("DeliveryCreated", aggregateId, new Payload("Test"));

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());

        OutboxEvent saved = captor.getValue();
        assertThat(saved.getEventType()).isEqualTo("DeliveryCreated");
        assertThat(saved.getAggregateId()).isEqualTo(aggregateId);
        assertThat(saved.getPayload()).isEqualTo("{\"address\":\"Test\"}");
    }

    @Test
    @DisplayName("save оборачивает ошибку сериализации в IllegalStateException и не сохраняет событие")
    void save_serializationFails_throwsIllegalStateException() {
        given(objectMapper.writeValueAsString(any())).willThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> outboxEventService.save("DeliveryCreated", aggregateId, new Object()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DeliveryCreated")
                .hasMessageContaining(aggregateId.toString())
                .hasCauseInstanceOf(RuntimeException.class);

        verify(outboxEventRepository, org.mockito.Mockito.never()).save(any());
    }
}
