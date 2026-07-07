package ru.anton.springtest.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.anton.springtest.dto.UserEnrichmentClientDto;
import ru.anton.springtest.dto.UserEnrichmentCreateClientDto;
import ru.anton.springtest.exception.EnrichmentServiceUnavailableException;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrichmentServiceAdapter {

    private static final String RESILIENCE4J_INSTANCE = "enrichmentService";

    private final EnrichmentServiceClient enrichmentServiceClient;

    @Retry(name = RESILIENCE4J_INSTANCE, fallbackMethod = "createEnrichmentFallback")
    @CircuitBreaker(name = RESILIENCE4J_INSTANCE)
    @RateLimiter(name = RESILIENCE4J_INSTANCE)
    public UserEnrichmentClientDto createEnrichment(UUID userId, String discountCardNumber, BigDecimal balance) {
        UserEnrichmentCreateClientDto request = UserEnrichmentCreateClientDto.builder()
                .userId(userId)
                .discountCardNumber(discountCardNumber)
                .balance(balance)
                .build();
        return enrichmentServiceClient.createEnrichment(request);
    }

    @Retry(name = RESILIENCE4J_INSTANCE, fallbackMethod = "getEnrichmentFallback")
    @CircuitBreaker(name = RESILIENCE4J_INSTANCE)
    @RateLimiter(name = RESILIENCE4J_INSTANCE)
    public UserEnrichmentClientDto getEnrichment(UUID userId) {
        return enrichmentServiceClient.getEnrichmentByUserId(userId);
    }

    public UserEnrichmentClientDto createEnrichmentFallback(UUID userId, String discountCardNumber,
                                                            BigDecimal balance, Exception ex) {
        log.warn("Enrichment-service недоступен при создании обогащения для userId: {}. Причина: {}",
                userId, ex.getMessage());
        throw new EnrichmentServiceUnavailableException(
                "Не удалось создать данные обогащения для userId: " + userId, ex);
    }

    public UserEnrichmentClientDto getEnrichmentFallback(UUID userId, Exception ex) {
        log.warn("Enrichment-service недоступен при получении обогащения для userId: {}. Причина: {}",
                userId, ex.getMessage());
        throw new EnrichmentServiceUnavailableException(
                "Не удалось получить данные обогащения для userId: " + userId, ex);
    }

    public void deleteEnrichment(UUID userId) {
        enrichmentServiceClient.deleteEnrichmentByUserId(userId);
    }
}
