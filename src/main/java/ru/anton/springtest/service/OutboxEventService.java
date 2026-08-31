package ru.anton.springtest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.anton.springtest.model.OutboxEvent;
import ru.anton.springtest.repository.OutboxEventRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void save(String eventType, UUID aggregateId, Object payload) {
        String serializedPayload;
        try {
            serializedPayload = objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Не удалось сериализовать outbox-событие типа " + eventType + " для aggregateId " + aggregateId, ex);
        }

        OutboxEvent event = new OutboxEvent();
        event.setEventType(eventType);
        event.setAggregateId(aggregateId);
        event.setPayload(serializedPayload);
        outboxEventRepository.save(event);
    }
}
