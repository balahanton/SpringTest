package ru.anton.springtest.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CircuitBreaker(name = "enrichmentService")
@RateLimiter(name = "enrichmentService")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnrichmentResilience {
}
