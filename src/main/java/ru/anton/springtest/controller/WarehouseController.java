package ru.anton.springtest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.anton.springtest.api.WarehouseControllerApi;
import ru.anton.springtest.dto.WarehouseCreateDto;
import ru.anton.springtest.dto.WarehouseResponseDto;
import ru.anton.springtest.dto.WarehouseUpdateDto;
import ru.anton.springtest.service.WarehouseService;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
public class WarehouseController implements WarehouseControllerApi {

    private final WarehouseService warehouseService;

    @Override
    public WarehouseResponseDto createWarehouse(WarehouseCreateDto warehouseCreateDto) {
        return warehouseService.createWarehouse(warehouseCreateDto);
    }

    @Override
    public List<WarehouseResponseDto> getAllWarehouses(Integer page, Integer size) {
        return warehouseService.getAllWarehouses(PageRequest.of(page, size));
    }

    @Override
    public WarehouseResponseDto getWarehouseById(UUID id) {
        return warehouseService.getWarehouseById(id);
    }

    @Override
    public WarehouseResponseDto updateWarehouse(UUID id, WarehouseUpdateDto warehouseUpdateDto) {
        return warehouseService.updateWarehouse(id, warehouseUpdateDto);
    }

    @Override
    public void deleteWarehouse(UUID id, List<UUID> productIds) {
        warehouseService.deleteWarehouseProducts(id, productIds);
    }
}
