package ru.anton.springtest.support;

import ru.anton.springtest.dto.ProductCreateDto;
import ru.anton.springtest.dto.ProductUpdateDto;
import ru.anton.springtest.dto.WarehouseCreateDto;
import ru.anton.springtest.dto.WarehouseUpdateDto;
import ru.anton.springtest.model.Product;
import ru.anton.springtest.model.Warehouse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class WarehouseTestFixtures {

    public static final String DEFAULT_WAREHOUSE_NAME = "Test1 Warehouse";
    public static final String UPDATED_WAREHOUSE_NAME = "Test1 Warehouse Updated";
    public static final String DEFAULT_PRODUCT_TITLE = "Test product";
    public static final String UPDATED_PRODUCT_TITLE = "Updated product";
    public static final BigDecimal DEFAULT_PRODUCT_PRICE = BigDecimal.valueOf(99.99);

    private WarehouseTestFixtures() {
    }

    public static Warehouse newWarehouse(String name) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(name);
        return warehouse;
    }

    public static Product newProduct(String title, BigDecimal price) {
        Product product = new Product();
        product.setTitle(title);
        product.setPrice(price);
        return product;
    }

    public static WarehouseCreateDto warehouseCreateDto(String name) {
        WarehouseCreateDto dto = new WarehouseCreateDto();
        dto.setName(name);
        return dto;
    }

    public static WarehouseCreateDto warehouseCreateDtoWithProducts(String name, ProductCreateDto... products) {
        WarehouseCreateDto dto = warehouseCreateDto(name);
        dto.setProducts(List.of(products));
        return dto;
    }

    public static ProductCreateDto productCreateDto(String title, BigDecimal price) {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setTitle(title);
        dto.setPrice(price);
        return dto;
    }

    public static WarehouseUpdateDto warehouseUpdateDto(String name) {
        WarehouseUpdateDto dto = new WarehouseUpdateDto();
        dto.setName(name);
        return dto;
    }

    public static WarehouseUpdateDto warehouseUpdateDtoWithProducts(String name, ProductUpdateDto... products) {
        WarehouseUpdateDto dto = warehouseUpdateDto(name);
        dto.setProducts(List.of(products));
        return dto;
    }

    public static ProductUpdateDto productUpdateDto(UUID id, String title, BigDecimal price) {
        ProductUpdateDto dto = new ProductUpdateDto();
        dto.setId(id);
        dto.setTitle(title);
        dto.setPrice(price);
        return dto;
    }
}
