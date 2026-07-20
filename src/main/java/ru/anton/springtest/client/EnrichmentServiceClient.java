package ru.anton.springtest.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import ru.anton.springtest.dto.UserEnrichmentClientResponseDto;
import ru.anton.springtest.dto.UserEnrichmentClientRequestDto;

import java.util.UUID;

@HttpExchange("/api/v1/enrich")
public interface EnrichmentServiceClient {

    @PostExchange
    UserEnrichmentClientResponseDto createEnrichment(@RequestBody UserEnrichmentClientRequestDto dto);

    @GetExchange("/{userId}")
    UserEnrichmentClientResponseDto getEnrichmentByUserId(@PathVariable UUID userId);

    @DeleteExchange("/{userId}")
    void deleteEnrichmentByUserId(@PathVariable UUID userId);
}
