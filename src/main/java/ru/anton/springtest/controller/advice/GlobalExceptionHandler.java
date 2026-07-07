package ru.anton.springtest.controller.advice;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.anton.springtest.exception.EnrichmentServiceUnavailableException;
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.exception.SagaExecutionException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String RESOURCE_NOT_FOUND = "Ресурс не найден: {}";
    private static final String PROBLEM_RESOURCE_NOT_FOUND_TITLE = "Resource Not Found";
    private static final String VALIDATION_FAILED = "Ошибка валидации входных данных: {}";
    private static final String PROBLEM_VALIDATION_FAILED_TITLE = "Validation Failed";
    private static final String PROBLEM_VALIDATION_FAILED_DETAIL = "Указаны некорректные параметры или заполнены не все обязательные поля";
    private static final String PROBLEM_VALIDATION_ERRORS_KEY = "invalid_fields";
    private static final String CONSTRAINT_VIOLATION_FAILED = "Нарушение ограничений (Constraint Violation): {}";
    private static final String ENRICHMENT_SERVICE_UNAVAILABLE = "Enrichment-service недоступен: {}";
    private static final String PROBLEM_ENRICHMENT_UNAVAILABLE_TITLE = "Enrichment Service Unavailable";
    private static final String SAGA_EXECUTION_FAILED = "Ошибка выполнения распределенной транзакции (Сага): {}";
    private static final String PROBLEM_SAGA_FAILED_TITLE = "Saga Execution Failed";

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        log.error(RESOURCE_NOT_FOUND, ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle(PROBLEM_RESOURCE_NOT_FOUND_TITLE);
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn(VALIDATION_FAILED, ex.getBindingResult().getErrorCount());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return buildValidationProblemDetail(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn(CONSTRAINT_VIOLATION_FAILED, ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });

        return buildValidationProblemDetail(errors);
    }

    private ProblemDetail buildValidationProblemDetail(Map<String, String> errors) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                PROBLEM_VALIDATION_FAILED_DETAIL
        );
        problemDetail.setTitle(PROBLEM_VALIDATION_FAILED_TITLE);
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty(PROBLEM_VALIDATION_ERRORS_KEY, errors);

        return problemDetail;
    }

    @ExceptionHandler(EnrichmentServiceUnavailableException.class)
    public ProblemDetail handleEnrichmentServiceUnavailable(EnrichmentServiceUnavailableException ex) {
        log.error(ENRICHMENT_SERVICE_UNAVAILABLE, ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problemDetail.setTitle(PROBLEM_ENRICHMENT_UNAVAILABLE_TITLE);
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(SagaExecutionException.class)
    public ProblemDetail handleSagaExecutionException(SagaExecutionException ex) {
        log.error(SAGA_EXECUTION_FAILED, ex.getMessage(), ex.getCause());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage()
        );
        problemDetail.setTitle(PROBLEM_SAGA_FAILED_TITLE);
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}
