package ru.anton.springtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;
import ru.anton.springtest.client.EnrichmentServiceClient;

@Configuration(proxyBeanMethods = false)
@ImportHttpServices(group = "enrichment", types = EnrichmentServiceClient.class)
public class EnrichmentClientConfig {

    @Bean
    public RestClientHttpServiceGroupConfigurer enrichmentGroupConfigurer(
            @Value("${enrichment-service.url}") String enrichmentServiceUrl,
            EnrichmentRequestIdInterceptor requestIdInterceptor) {
        return groups -> groups.filterByName("enrichment")
                .forEachClient((group, builder) -> builder
                        .baseUrl(enrichmentServiceUrl)
                        .requestInterceptor(requestIdInterceptor));
    }
}
