package ru.anton.springtest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anton.springtest.config.RedisCacheConfig;
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
    @CacheEvict(cacheNames = RedisCacheConfig.WAREHOUSES_PAGE_CACHE, allEntries = true)
    public WarehouseResponseDto createWarehouse(WarehouseCreateDto dto) {

        Warehouse warehouse = warehouseMapper.toEntity(dto);
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        log.info("Склад успешно создан с ID: {}", savedWarehouse.getId());
        return warehouseMapper.toResponseDto(savedWarehouse);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = RedisCacheConfig.WAREHOUSES_CACHE, key = "#id")
    public WarehouseResponseDto getWarehouseById(UUID id) {

        Warehouse warehouse = findWarehouseOrThrow(id);

        return warehouseMapper.toResponseDto(warehouse);
    }

    @Transactional
    @Cacheable(cacheNames = RedisCacheConfig.WAREHOUSES_PAGE_CACHE, key = "#pageable")
    public List<WarehouseResponseDto> getAllWarehouses(Pageable pageable) {

        Page<Warehouse> warehousePage = warehouseRepository.findWithLockByIsDeletedFalse(pageable);
        return warehouseMapper.toResponseDtoList(warehousePage.getContent());
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisCacheConfig.WAREHOUSES_CACHE, key = "#id"),
            @CacheEvict(cacheNames = RedisCacheConfig.WAREHOUSES_PAGE_CACHE, allEntries = true)
    })
    public WarehouseResponseDto updateWarehouse(UUID id, WarehouseUpdateDto dto) {

        Warehouse warehouse = findWarehouseOrThrow(id);

        if (dto.getProducts() != null) {
            List<UUID> productIdsToUpdate = warehouseMapper.extractProductIdsToUpdate(dto.getProducts());

            List<Product> existingProductsFromDb = productService.findAllByIds(productIdsToUpdate);

            Map<UUID, Product> productMap = existingProductsFromDb.stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));

            for (ProductUpdateDto pDto : dto.getProducts()) {
                if (productMap.get(pDto.getId()) == null) {
                    throw new EntityNotFoundException("Продукт с ID " + pDto.getId() + " не найден");
                }
            }

            warehouseMapper.updateProductsFromDtos(dto.getProducts(), productMap);
        }

        warehouseMapper.updateEntity(dto, warehouse);

        log.info("Успешно обновлен склад с ID: {}", id);
        return warehouseMapper.toResponseDto(warehouse);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = RedisCacheConfig.WAREHOUSES_CACHE, key = "#id"),
            @CacheEvict(cacheNames = RedisCacheConfig.WAREHOUSES_PAGE_CACHE, allEntries = true)
    })
    public void deleteWarehouseProducts(UUID id, List<UUID> productIdsToDelete) {

        Warehouse warehouse = findWarehouseOrThrow(id);

        warehouse.setIsDeleted(true);

        if (productIdsToDelete != null) {

            List<Product> productsToDelete = warehouse.getProducts().stream()
                    .filter(product -> productIdsToDelete.contains(product.getId()))
                    .toList();

            productsToDelete.forEach(product -> product.setIsDeleted(true));

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
