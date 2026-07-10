package ru.anton.springtest.service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.anton.springtest.client.EnrichmentServiceClient;
import ru.anton.springtest.dto.UserEnrichmentClientDto;
import ru.anton.springtest.dto.UserEnrichmentCreateClientDto;
import ru.anton.springtest.exception.EnrichmentServiceUnavailableException;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrichmentServiceAdapter {

    private static final String RESILIENCE4J_INSTANCE = "enrichmentService";

    private final EnrichmentServiceClient enrichmentServiceClient;

    @Retry(name = RESILIENCE4J_INSTANCE, fallbackMethod = "createEnrichmentFallback")
    @EnrichmentResilience
    public UserEnrichmentClientDto createEnrichment(UserEnrichmentCreateClientDto request) {
        return enrichmentServiceClient.createEnrichment(request);
    }

    @Retry(name = RESILIENCE4J_INSTANCE, fallbackMethod = "getEnrichmentFallback")
    @EnrichmentResilience
    public UserEnrichmentClientDto getEnrichment(UUID userId) {
        return enrichmentServiceClient.getEnrichmentByUserId(userId);
    }

    public UserEnrichmentClientDto createEnrichmentFallback(UserEnrichmentCreateClientDto request, Exception ex) {
        return handleFallback(request.getUserId(), "создать", ex);
    }

    public UserEnrichmentClientDto getEnrichmentFallback(UUID userId, Exception ex) {
        return handleFallback(userId, "получить", ex);
    }

    private UserEnrichmentClientDto handleFallback(UUID userId, String action, Exception ex) {
        log.warn("Enrichment-service недоступен при попытке {} данные обогащения для userId: {}. Причина: {}",
                action, userId, ex.getMessage());
        throw new EnrichmentServiceUnavailableException(
                "Не удалось " + action + " данные обогащения для userId: " + userId, ex);
    }

    public void deleteEnrichment(UUID userId) {
        enrichmentServiceClient.deleteEnrichmentByUserId(userId);
    }
}
