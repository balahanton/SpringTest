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
        try {
            OutboxEvent event = new OutboxEvent();
            event.setEventType(eventType);
            event.setAggregateId(aggregateId);
            event.setPayload(objectMapper.writeValueAsString(payload));
            outboxEventRepository.save(event);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Не удалось сериализовать outbox-событие типа " + eventType + " для aggregateId " + aggregateId, ex);
        }
    }
}
