package ru.anton.springtest.exception;

import java.io.Serial;

public class EnrichmentServiceClientException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EnrichmentServiceClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
