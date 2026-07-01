package ru.anton.springtest.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import ru.anton.springtest.client.dto.UserEnrichmentClientDto;
import ru.anton.springtest.client.dto.UserEnrichmentCreateClientDto;

import java.util.UUID;

@HttpExchange("/api/v1/enrich")
public interface EnrichmentServiceClient {

    @PostExchange
    UserEnrichmentClientDto createEnrichment(@RequestBody UserEnrichmentCreateClientDto dto);

    @GetExchange("/{userId}")
    UserEnrichmentClientDto getEnrichmentByUserId(@PathVariable UUID userId);
}
