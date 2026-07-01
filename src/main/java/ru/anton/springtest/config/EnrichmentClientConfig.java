package ru.anton.springtest.config;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ru.anton.springtest.client.EnrichmentServiceClient;

@Configuration
public class EnrichmentClientConfig {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_ID_MDC_KEY = "requestId";

    @Value("${enrichment-service.url}")
    private String enrichmentServiceUrl;

    @Bean
    public EnrichmentServiceClient enrichmentServiceClient() {
        RestClient.Builder builder = RestClient.builder();
        RestClient restClient = builder
                .baseUrl(enrichmentServiceUrl)
                .requestInterceptor((request, body, execution) -> {
                    String requestId = MDC.get(REQUEST_ID_MDC_KEY);
                    if (requestId != null && !requestId.isBlank()) {
                        request.getHeaders().set(REQUEST_ID_HEADER, requestId);
                    }
                    return execution.execute(request, body);
                })
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(EnrichmentServiceClient.class);
    }
}
