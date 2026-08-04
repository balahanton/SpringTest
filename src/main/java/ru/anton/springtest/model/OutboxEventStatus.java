package ru.anton.springtest.model;

public enum OutboxEventStatus {
    NEW, PROCESSING, SENT, FAILED
}
