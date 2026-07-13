package ru.anton.springtest.service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import ru.anton.springtest.client.EnrichmentServiceClient;
import ru.anton.springtest.dto.UserEnrichmentClientRequestDto;
import ru.anton.springtest.dto.UserEnrichmentClientResponseDto;
import ru.anton.springtest.exception.EnrichmentServiceUnavailableException;
import ru.anton.springtest.exception.EntityNotFoundException;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrichmentServiceAdapter {

    private static final String RESILIENCE4J_INSTANCE = "enrichmentService";

    private final EnrichmentServiceClient enrichmentServiceClient;

    @Retry(name = RESILIENCE4J_INSTANCE, fallbackMethod = "createEnrichmentFallback")
    @EnrichmentResilience
    public UserEnrichmentClientResponseDto createEnrichment(UserEnrichmentClientRequestDto request) {
        return enrichmentServiceClient.createEnrichment(request);
    }

    @Retry(name = RESILIENCE4J_INSTANCE, fallbackMethod = "getEnrichmentFallback")
    @EnrichmentResilience
    public UserEnrichmentClientResponseDto getEnrichment(UUID userId) {
        try {
            return enrichmentServiceClient.getEnrichmentByUserId(userId);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new EntityNotFoundException("Данные для обогащения не найдены");
        }
    }

    public UserEnrichmentClientResponseDto createEnrichmentFallback(UserEnrichmentClientRequestDto request, Exception ex) {
        return handleFallback(request.getUserId(), "создать", ex);
    }

    public UserEnrichmentClientResponseDto getEnrichmentFallback(UUID userId, Exception ex) {
        return handleFallback(userId, "получить", ex);
    }

    private UserEnrichmentClientResponseDto handleFallback(UUID userId, String action, Exception ex) {
        log.warn("Enrichment-service недоступен при попытке {} данные обогащения для userId: {}. Причина: {}",
                action, userId, ex.getMessage());
        throw new EnrichmentServiceUnavailableException(
                "Не удалось " + action + " данные обогащения для userId: " + userId, ex);
    }

    public void deleteEnrichment(UUID userId) {
        enrichmentServiceClient.deleteEnrichmentByUserId(userId);
    }
}
