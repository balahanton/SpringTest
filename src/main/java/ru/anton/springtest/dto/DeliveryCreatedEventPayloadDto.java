package ru.anton.springtest.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DeliveryCreatedEventPayloadDto {
    private UUID deliveryId;
    private String address;
    private String status;
}
