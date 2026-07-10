package ru.anton.springtest.config;

import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EnrichmentRequestIdInterceptor implements ClientHttpRequestInterceptor {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String REQUEST_ID_MDC_KEY = "requestId";

    @Override
    public @NonNull ClientHttpResponse intercept(@NonNull HttpRequest request, byte @NonNull [] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        String requestId = MDC.get(REQUEST_ID_MDC_KEY);
        if (requestId != null && !requestId.isBlank()) {
            request.getHeaders().set(REQUEST_ID_HEADER, requestId);
        }
        return execution.execute(request, body);
    }
}
