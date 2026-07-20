package ru.anton.springtest.exception;

import java.io.Serial;

public class EnrichmentServiceUnavailableException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EnrichmentServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
