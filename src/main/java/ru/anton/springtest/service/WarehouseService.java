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
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Transactional
    public List<WarehouseResponseDto> getAllWarehouses(Pageable pageable) {

        Page<Warehouse> warehousePage = warehouseRepository.findWithLockByIsDeletedFalse(pageable);
        return warehouseMapper.toResponseDtoList(warehousePage.getContent());
    }


    @Transactional
    public WarehouseResponseDto updateWarehouse(UUID id, WarehouseUpdateDto dto) {

        Warehouse warehouse = findWarehouseOrThrow(id);

        if (dto.getProducts() != null) {
            List<UUID> productIdsToUpdate = warehouseMapper.extractProductIdsToUpdate(dto.getProducts());

            List<Product> existingProductsFromDb = productService.findAllByIds(productIdsToUpdate);

            Map<UUID, Product> productMap = existingProductsFromDb.stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));

            for (ProductUpdateDto pDto : dto.getProducts()) {
                if (pDto.getId() != null) {
                    Product dbProduct = productMap.get(pDto.getId());
                    if (dbProduct != null) {
                        warehouseMapper.updateProductFromDto(pDto, dbProduct);
                    }
                } else {
                    Product newProduct = warehouseMapper.toProductEntityFromUpdate(pDto);
                    warehouse.getProducts().add(newProduct);
                }
            }
        }

        warehouseMapper.updateEntity(dto, warehouse);

        log.info("Успешно обновлен склад с ID: {}", id);
        return warehouseMapper.toResponseDto(warehouse);
    }

    @Transactional
    public void deleteWarehouseProducts(UUID id, List<UUID> productIdsToDelete) {

        Warehouse warehouse = findWarehouseOrThrow(id);

        warehouse.setIsDeleted(true);

        if (productIdsToDelete != null) {

            List<Product> productsToDelete = warehouse.getProducts().stream()
                    .filter(product -> productIdsToDelete.contains(product.getId()))
                    .toList();

            warehouse.getProducts().removeAll(productsToDelete);

            productsToDelete.forEach(product -> product.setIsDeleted(true));
            productService.saveAll(productsToDelete);

            log.info("Связанные товары в количестве {} помечены как удаленные", productsToDelete.size());
        }

        warehouseRepository.save(warehouse);

        log.info("Склад с ID {} успешно переведен в статус удаленного", id);
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
