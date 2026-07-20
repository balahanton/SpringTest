package ru.anton.springtest.util;

import ru.anton.springtest.dto.DeliveryCreateDto;
import ru.anton.springtest.dto.DeliveryDetailsCreateDto;
import ru.anton.springtest.dto.DeliveryUpdateDto;
import ru.anton.springtest.model.Delivery;
import ru.anton.springtest.model.DeliveryDetails;

public final class DeliveryTestFixtures {

    public static final String DEFAULT_ADDRESS = "TEST ADDRESS 1";
    public static final String UPDATED_ADDRESS = "TEST ADDRESS 2";
    public static final String DEFAULT_STATUS = "CREATED";
    public static final String UPDATED_STATUS = "IN_TRANSIT";
    public static final String DEFAULT_COURIER_NAME = "IVAN";
    public static final String DEFAULT_DELIVERY_NOTES = "TEST DELIVERY NOTES";

    private DeliveryTestFixtures() {
    }

    public static Delivery newDelivery(String address, String status) {
        Delivery delivery = new Delivery();
        delivery.setAddress(address);
        delivery.setStatus(status);
        return delivery;
    }

    public static DeliveryDetails newDeliveryDetails(String courierName, String notes, Delivery delivery) {
        DeliveryDetails details = new DeliveryDetails();
        details.setCourierName(courierName);
        details.setDeliveryNotes(notes);
        details.setDelivery(delivery);
        return details;
    }

    public static DeliveryCreateDto deliveryCreateDto(String address, String status) {
        DeliveryCreateDto dto = new DeliveryCreateDto();
        dto.setAddress(address);
        dto.setStatus(status);
        return dto;
    }

    public static DeliveryCreateDto deliveryCreateDtoWithDetails(String address, String status,
                                                                 DeliveryDetailsCreateDto details) {
        DeliveryCreateDto dto = deliveryCreateDto(address, status);
        dto.setDetails(details);
        return dto;
    }

    public static DeliveryDetailsCreateDto deliveryDetailsCreateDto(String courierName, String notes) {
        DeliveryDetailsCreateDto dto = new DeliveryDetailsCreateDto();
        dto.setCourierName(courierName);
        dto.setDeliveryNotes(notes);
        return dto;
    }

    public static DeliveryUpdateDto deliveryUpdateDto(String address, String status) {
        DeliveryUpdateDto dto = new DeliveryUpdateDto();
        dto.setAddress(address);
        dto.setStatus(status);
        return dto;
    }
}
