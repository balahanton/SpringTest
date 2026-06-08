package ru.anton.springtest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anton.springtest.dto.ProductUpdateDto;
import ru.anton.springtest.dto.WarehouseCreateDto;
import ru.anton.springtest.dto.WarehouseResponseDto;
import ru.anton.springtest.dto.WarehouseUpdateDto;
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.mapper.WarehouseMapper;
import ru.anton.springtest.model.Product;
import ru.anton.springtest.model.Warehouse;
import ru.anton.springtest.repository.WarehouseRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final ProductService productService;
    private final WarehouseMapper warehouseMapper;

    @Transactional
    public WarehouseResponseDto createWarehouse(WarehouseCreateDto dto) {

        Warehouse warehouse = warehouseMapper.toEntity(dto);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        log.info("Склад успешно создан с ID: {}", savedWarehouse.getId());
        return warehouseMapper.toResponseDto(savedWarehouse);
    }

    @Transactional(readOnly = true)
    public WarehouseResponseDto getWarehouseById(UUID id) {

        Warehouse warehouse = findWarehouseOrThrow(id);

        return warehouseMapper.toResponseDto(warehouse);
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponseDto> getAllWarehouses(Pageable pageable) {

        Page<Warehouse> warehousePage = warehouseRepository.findWithLockByIsDeletedFalse(pageable);
        return warehouseMapper.toResponseDtoList(warehousePage.getContent());
    }


    @Transactional
    public WarehouseResponseDto updateWarehouse(UUID id, WarehouseUpdateDto dto) {

        Warehouse warehouse = findWarehouseOrThrow(id);

        if (dto.getProducts() == null || dto.getProducts().isEmpty()) {
            warehouse.getProducts().clear();
        } else {
            warehouse.getProducts().removeIf(existingProduct ->
                    dto.getProducts().stream()
                            .filter(pDto -> pDto.getId() != null)
                            .noneMatch(pDto -> pDto.getId().equals(existingProduct.getId()))
            );

            for (ProductUpdateDto pDto : dto.getProducts()) {
                if (pDto.getId() != null) {
                    Product existingProduct = productService.findByIdOrThrow(pDto.getId());

                    if (!warehouse.getProducts().contains(existingProduct)) {
                        warehouse.getProducts().add(existingProduct);
                    }
                } else {
                    Product newProduct = new Product();
                    warehouse.getProducts().add(newProduct);
                }
            }
        }

        warehouseMapper.updateEntity(dto, warehouse);

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        log.info("Успешно обновлен склад с ID: {}", id);
        return warehouseMapper.toResponseDto(updatedWarehouse);
    }

    @Transactional
    public void deleteWarehouse(UUID id) {

        Warehouse warehouse = findWarehouseOrThrow(id);

        warehouse.setIsDeleted(true);

        warehouseRepository.save(warehouse);
        log.info("Склад с ID {} и его связи с товарами переведены в статус удаленных", id);
    }

    private Warehouse findWarehouseOrThrow(UUID id) {
        return warehouseRepository.findById(id)
                .filter(warehouse -> !warehouse.getIsDeleted())
                .orElseThrow(() -> {
                    log.error("Склад с ID {} не найден или был удален", id);
                    return new EntityNotFoundException("Warehouse with ID " + id + " not found");
                });
    }
}
