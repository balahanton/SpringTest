package ru.anton.springtest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.anton.springtest.AbstractIntegrationTest;
import ru.anton.springtest.dto.ProductCreateDto;
import ru.anton.springtest.dto.ProductUpdateDto;
import ru.anton.springtest.dto.WarehouseCreateDto;
import ru.anton.springtest.dto.WarehouseUpdateDto;
import ru.anton.springtest.model.Warehouse;
import ru.anton.springtest.repository.WarehouseRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.anton.springtest.util.WarehouseTestFixtures.*;

@DisplayName("WarehouseController — интеграционные тесты")
class WarehouseControllerTest extends AbstractIntegrationTest {

    private static final String WAREHOUSES_URL = "/api/v1/warehouses";
    private static final String WAREHOUSE_BY_ID_URL = "/api/v1/warehouses/{id}";
    private static final String INVALID_FIELDS_PATH = "$.invalid_fields";

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Test
    @DisplayName("POST /api/v1/warehouses с валидным телом — 201 и тело с id")
    void createWarehouse_valid_returns201WithId() throws Exception {
        WarehouseCreateDto dto = warehouseCreateDto(DEFAULT_WAREHOUSE_NAME);

        mockMvc.perform(postJson(WAREHOUSES_URL, dto))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(DEFAULT_WAREHOUSE_NAME));
    }

    @Test
    @DisplayName("POST /api/v1/warehouses с продуктами — 201, продукты сохранены вместе со складом")
    void createWarehouse_withProducts_returns201WithProducts() throws Exception {
        ProductCreateDto productDto = productCreateDto(DEFAULT_PRODUCT_TITLE, DEFAULT_PRODUCT_PRICE);
        WarehouseCreateDto dto = warehouseCreateDtoWithProducts(DEFAULT_WAREHOUSE_NAME, productDto);

        mockMvc.perform(postJson(WAREHOUSES_URL, dto))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.products", hasSize(1)))
                .andExpect(jsonPath("$.products[0].title").value(DEFAULT_PRODUCT_TITLE));
    }

    @Test
    @DisplayName("POST /api/v1/warehouses с именем короче 5 символов — 400")
    void createWarehouse_nameTooShort_returns400() throws Exception {
        WarehouseCreateDto dto = warehouseCreateDto("abc");

        mockMvc.perform(postJson(WAREHOUSES_URL, dto))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(INVALID_FIELDS_PATH + ".name").exists());
    }

    @Test
    @DisplayName("GET /api/v1/warehouses — 200 и массив складов")
    void getAllWarehouses_returns200WithArray() throws Exception {
        warehouseRepository.save(newWarehouse(DEFAULT_WAREHOUSE_NAME));
        warehouseRepository.save(newWarehouse("Second warehouse"));

        mockMvc.perform(get(WAREHOUSES_URL).param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/warehouses/{id} для существующего склада — 200")
    void getWarehouseById_existing_returns200() throws Exception {
        Warehouse saved = warehouseRepository.save(newWarehouse(DEFAULT_WAREHOUSE_NAME));

        mockMvc.perform(get(WAREHOUSE_BY_ID_URL, saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()));
    }

    @Test
    @DisplayName("GET /api/v1/warehouses/{id} для несуществующего склада — 404")
    void getWarehouseById_notFound_returns404() throws Exception {
        mockMvc.perform(get(WAREHOUSE_BY_ID_URL, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/warehouses/{id} без продуктов — 200, имя обновлено")
    void updateWarehouse_withoutProducts_returns200() throws Exception {
        Warehouse saved = warehouseRepository.save(newWarehouse(DEFAULT_WAREHOUSE_NAME));
        WarehouseUpdateDto dto = warehouseUpdateDto(UPDATED_WAREHOUSE_NAME);

        mockMvc.perform(putJson(WAREHOUSE_BY_ID_URL, dto, saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(UPDATED_WAREHOUSE_NAME));
    }

    @Test
    @DisplayName("PUT /api/v1/warehouses/{id} с обновлением существующего продукта — 200, поля продукта обновлены")
    void updateWarehouse_withExistingProduct_updatesProductFields() throws Exception {
        Warehouse warehouseWithProduct = newWarehouse(DEFAULT_WAREHOUSE_NAME);
        warehouseWithProduct.setProducts(List.of(newProduct(DEFAULT_PRODUCT_TITLE, DEFAULT_PRODUCT_PRICE)));
        Warehouse saved = warehouseRepository.save(warehouseWithProduct);
        UUID productId = saved.getProducts().getFirst().getId();

        ProductUpdateDto productDto = productUpdateDto(productId, UPDATED_PRODUCT_TITLE, BigDecimal.valueOf(50));
        WarehouseUpdateDto dto = warehouseUpdateDtoWithProducts(UPDATED_WAREHOUSE_NAME, productDto);

        mockMvc.perform(putJson(WAREHOUSE_BY_ID_URL, dto, saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].title").value(UPDATED_PRODUCT_TITLE));
    }

    @Test
    @DisplayName("PUT /api/v1/warehouses/{id} с несуществующим id продукта — 404")
    void updateWarehouse_unknownProductId_returns404() throws Exception {
        Warehouse saved = warehouseRepository.save(newWarehouse(DEFAULT_WAREHOUSE_NAME));
        ProductUpdateDto productDto = productUpdateDto(UUID.randomUUID(), UPDATED_PRODUCT_TITLE, BigDecimal.TEN);
        WarehouseUpdateDto dto = warehouseUpdateDtoWithProducts(UPDATED_WAREHOUSE_NAME, productDto);

        mockMvc.perform(putJson(WAREHOUSE_BY_ID_URL, dto, saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/warehouses/{id} для несуществующего склада — 404")
    void updateWarehouse_warehouseNotFound_returns404() throws Exception {
        WarehouseUpdateDto dto = warehouseUpdateDto(UPDATED_WAREHOUSE_NAME);

        mockMvc.perform(putJson(WAREHOUSE_BY_ID_URL, dto, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/warehouses/{id} без productIds — 204")
    void deleteWarehouse_withoutProductIds_returns204() throws Exception {
        Warehouse saved = warehouseRepository.save(newWarehouse(DEFAULT_WAREHOUSE_NAME));

        mockMvc.perform(delete(WAREHOUSE_BY_ID_URL, saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/warehouses/{id}?productIds=... — 204, указанные продукты тоже помечены удалёнными")
    void deleteWarehouse_withProductIds_returns204() throws Exception {
        Warehouse warehouseWithProduct = newWarehouse(DEFAULT_WAREHOUSE_NAME);
        warehouseWithProduct.setProducts(List.of(newProduct(DEFAULT_PRODUCT_TITLE, DEFAULT_PRODUCT_PRICE)));
        Warehouse saved = warehouseRepository.save(warehouseWithProduct);
        UUID productId = saved.getProducts().getFirst().getId();

        mockMvc.perform(delete(WAREHOUSE_BY_ID_URL, saved.getId())
                        .param("productIds", productId.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/warehouses/{id} для несуществующего склада — 404")
    void deleteWarehouse_notFound_returns404() throws Exception {
        mockMvc.perform(delete(WAREHOUSE_BY_ID_URL, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}