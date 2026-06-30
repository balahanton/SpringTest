package ru.anton.springtest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.anton.springtest.api.DeliveryControllerApi;
import ru.anton.springtest.dto.DeliveryCreateDto;
import ru.anton.springtest.dto.DeliveryResponseDto;
import ru.anton.springtest.dto.DeliveryUpdateDto;
import ru.anton.springtest.service.DeliveryService;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
public class DeliveryController implements DeliveryControllerApi {

    private final DeliveryService deliveryService;

    @Override
    public DeliveryResponseDto createDelivery(DeliveryCreateDto deliveryCreateDto) {
        return deliveryService.createDelivery(deliveryCreateDto);
    }

    @Override
    public void deleteDelivery(UUID id) {
        deliveryService.deleteDelivery(id);
    }

    @Override
    public List<DeliveryResponseDto> getAllDeliveries(Integer page, Integer size) {
        return deliveryService.getAllDeliveries(PageRequest.of(page, size));
    }

    @Override
    public DeliveryResponseDto getDeliveryById(UUID id) {
        return deliveryService.getDeliveryById(id);
    }

    @Override
    public DeliveryResponseDto updateDelivery(UUID id, DeliveryUpdateDto deliveryUpdateDto) {
        return deliveryService.updateDelivery(id, deliveryUpdateDto);
    }
}