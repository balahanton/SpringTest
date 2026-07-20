package ru.anton.springtest.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.anton.springtest.AbstractIntegrationTest;
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.model.Warehouse;
import ru.anton.springtest.repository.WarehouseRepository;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.anton.springtest.util.WarehouseTestFixtures.DEFAULT_WAREHOUSE_NAME;
import static ru.anton.springtest.util.WarehouseTestFixtures.newWarehouse;

@DisplayName("WarehouseService — тесты Redis-кэша")
class WarehouseServiceCacheTest extends AbstractIntegrationTest {

    @Autowired
    WarehouseService warehouseService;

    @MockitoSpyBean
    WarehouseRepository warehouseRepository;

    UUID warehouseId;

    @BeforeEach
    void setUp() {
        Warehouse warehouse = newWarehouse(DEFAULT_WAREHOUSE_NAME);
        warehouseId = warehouseRepository.save(warehouse).getId();
    }

    @Test
    @DisplayName("после deleteWarehouseProducts кэш инвалидируется — следующий getWarehouseById снова идёт в БД")
    void deleteWarehouseProducts_evictsCache_nextGetGoesToDatabase() {
        warehouseService.getWarehouseById(warehouseId);

        warehouseService.deleteWarehouseProducts(warehouseId, null);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> warehouseService.getWarehouseById(warehouseId));

        verify(warehouseRepository, times(3)).findById(warehouseId);
    }

}