package ru.anton.springtest.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.anton.springtest.client.dto.UserEnrichmentClientDto;
import ru.anton.springtest.client.dto.UserEnrichmentCreateClientDto;

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
        log.info("Отправляем запрос на создание обогащения в enrichment-service для userId: {}", userId);
        return enrichmentServiceClient.createEnrichment(request);
    }

    @Retry(name = RESILIENCE4J_INSTANCE, fallbackMethod = "getEnrichmentFallback")
    @CircuitBreaker(name = RESILIENCE4J_INSTANCE)
    @RateLimiter(name = RESILIENCE4J_INSTANCE)
    public UserEnrichmentClientDto getEnrichment(UUID userId) {
        log.info("Запрашиваем обогащение из enrichment-service для userId: {}", userId);
        return enrichmentServiceClient.getEnrichmentByUserId(userId);
    }

    public UserEnrichmentClientDto createEnrichmentFallback(UUID userId, String discountCardNumber,
                                                            BigDecimal balance, Exception ex) {
        log.warn("Enrichment-service недоступен при создании обогащения для userId: {}. Причина: {}",
                userId, ex.getMessage());
        return null;
    }

    public UserEnrichmentClientDto getEnrichmentFallback(UUID userId, Exception ex) {
        log.warn("Enrichment-service недоступен при получении обогащения для userId: {}. Причина: {}",
                userId, ex.getMessage());
        return null;
    }
}
