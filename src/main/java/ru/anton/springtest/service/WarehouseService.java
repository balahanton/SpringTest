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
import ru.anton.springtest.excteption.EntityNotFoundException;
import ru.anton.springtest.mapper.WarehouseMapper;
import ru.anton.springtest.model.Product;
import ru.anton.springtest.model.Warehouse;
import ru.anton.springtest.repository.ProductRepository;
import ru.anton.springtest.repository.WarehouseRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final WarehouseMapper warehouseMapper;

    @Transactional
    public WarehouseResponseDto createWarehouse(WarehouseCreateDto dto) {
        log.info("Начало метода создания склада: {}", dto.getName());

        Warehouse warehouse = warehouseMapper.toEntity(dto);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        log.info("Склад успешно создан с ID: {}", savedWarehouse.getId());
        return warehouseMapper.toResponseDto(savedWarehouse);
    }

    @Transactional(readOnly = true)
    public WarehouseResponseDto getWarehouseById(UUID id) {
        log.info("Запрос склада по ID: {}", id);

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Склад с ID {} не найден", id);
                    return new EntityNotFoundException("Склад с ID " + id + " не найден");
                });

        return warehouseMapper.toResponseDto(warehouse);
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponseDto> getAllWarehouses(Pageable pageable) {
        log.info("Запрос списка складов. Страница: {}, Размер: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Warehouse> warehousePage = warehouseRepository.findAll(pageable);
        return warehouseMapper.toResponseDtoList(warehousePage.getContent());
    }


    @Transactional
    public WarehouseResponseDto updateWarehouse(UUID id, WarehouseUpdateDto dto) {
        log.info("Начало метода обновления склада с ID: {}. Новое имя: {}", id, dto.getName());

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Склад с ID {} не найден для обновления", id);
                    return new EntityNotFoundException("Склад с ID " + id + " не найден");
                });

        warehouse.setName(dto.getName());

        if (dto.getProducts() == null || dto.getProducts().isEmpty()) {
            log.info("Список товаров пуст. Очистка связей для склада ID: {}", id);
            warehouse.getProducts().clear();
        } else {
            warehouse.getProducts().removeIf(existingProduct ->
                    dto.getProducts().stream()
                            .filter(pDto -> pDto.getId() != null)
                            .noneMatch(pDto -> pDto.getId().equals(existingProduct.getId()))
            );

            for (ProductUpdateDto pDto : dto.getProducts()) {
                if (pDto.getId() != null) {
                    Product existingProduct = productRepository.findById(pDto.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Продукт не найден"));
                    existingProduct.setTitle(pDto.getTitle());
                    existingProduct.setPrice(pDto.getPrice());

                    if (!warehouse.getProducts().contains(existingProduct)) {
                        warehouse.getProducts().add(existingProduct);
                    }
                } else {
                    Product newProduct = new Product();
                    newProduct.setTitle(pDto.getTitle());
                    newProduct.setPrice(pDto.getPrice());

                    warehouse.getProducts().add(newProduct);
                }
            }
        }

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        log.info("Склад с ID {} успешно обновлен", id);
        return warehouseMapper.toResponseDto(updatedWarehouse);
    }

    @Transactional
    public void deleteWarehouse(UUID id) {
        log.info("Запрос на удаление склада с ID: {}", id);

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Склад с ID " + id + " не найден"));

        warehouseRepository.delete(warehouse);

        log.info("Склад с ID {} и его связи с товарами переведены в статус удаленных", id);
    }
}
