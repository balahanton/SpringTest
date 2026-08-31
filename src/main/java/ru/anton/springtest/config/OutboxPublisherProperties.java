package ru.anton.springtest.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "outbox.publisher")
public class OutboxPublisherProperties {

    @Positive
    private int batchSize = 50;

    @NotBlank
    private String defaultTopic = "user.created";

    private Map<String, String> topicMapping = Map.of();

    @Positive
    private long staleTimeoutMs = 120_000;

    @Positive
    private int maxAttempts = 5;

    @Positive
    private long retryBaseDelayMs = 2000;
}
