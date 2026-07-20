package ru.anton.springtest.exception;

import java.io.Serial;

public class SagaExecutionException extends  RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public SagaExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
