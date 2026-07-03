package ru.anton.springtest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anton.springtest.dto.ProductUpdateDto;
import ru.anton.springtest.dto.WarehouseResponseDto;
import ru.anton.springtest.dto.WarehouseUpdateDto;
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.mapper.WarehouseMapper;
import ru.anton.springtest.model.Product;
import ru.anton.springtest.model.Warehouse;
import ru.anton.springtest.repository.WarehouseRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static ru.anton.springtest.support.WarehouseTestFixtures.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WarehouseService — юнит-тесты")
public class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductService productService;

    @Mock
    private WarehouseMapper warehouseMapper;

    @InjectMocks
    private WarehouseService warehouseService;

    private UUID warehouseId;
    private Warehouse warehouse;
    private WarehouseResponseDto responseDto;

    @BeforeEach
    void setUp() {
        warehouseId = UUID.randomUUID();
        warehouse = newWarehouse(DEFAULT_WAREHOUSE_NAME);
        warehouse.setId(warehouseId);
        warehouse.setIsDeleted(false);

        responseDto = new WarehouseResponseDto();
        responseDto.setId(warehouseId);
        responseDto.setName(DEFAULT_WAREHOUSE_NAME);
    }

    @Nested
    @DisplayName("createWarehouse")
    class CreateWarehouse {

        @Test
        @DisplayName("создаёт склад через маппер и репозиторий")
        void createWarehouse_savesAndReturnsDto() {
            var dto = warehouseCreateDto(DEFAULT_WAREHOUSE_NAME);
            given(warehouseMapper.toEntity(dto)).willReturn(warehouse);
            given(warehouseRepository.save(warehouse)).willReturn(warehouse);
            given(warehouseMapper.toResponseDto(warehouse)).willReturn(responseDto);

            WarehouseResponseDto result = warehouseService.createWarehouse(dto);

            assertThat(result.getId()).isEqualTo(warehouseId);
            verify(warehouseRepository).save(warehouse);
        }
    }

    @Nested
    @DisplayName("getWarehouseById")
    class GetWarehouseById {

        @Test
        @DisplayName("возвращает DTO для существующего склада")
        void getWarehouseById_found_returnsDto() {
            given(warehouseRepository.findById(warehouseId)).willReturn(Optional.of(warehouse));
            given(warehouseMapper.toResponseDto(warehouse)).willReturn(responseDto);

            WarehouseResponseDto result = warehouseService.getWarehouseById(warehouseId);

            assertThat(result.getId()).isEqualTo(warehouseId);
        }

        @Test
        @DisplayName("несуществующий склад — EntityNotFoundException")
        void getWarehouseById_notFound_throws() {
            given(warehouseRepository.findById(warehouseId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> warehouseService.getWarehouseById(warehouseId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("удалённый склад — EntityNotFoundException")
        void getWarehouseById_deleted_throws() {
            warehouse.setIsDeleted(true);
            given(warehouseRepository.findById(warehouseId)).willReturn(Optional.of(warehouse));

            assertThatThrownBy(() -> warehouseService.getWarehouseById(warehouseId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateWarehouse")
    class UpdateWarehouse {

        @Test
        @DisplayName("без списка продуктов — ProductService не вызывается, обновляются только собственные поля склада")
        void updateWarehouse_withoutProducts_doesNotTouchProducts() {
            WarehouseUpdateDto dto = warehouseUpdateDto(UPDATED_WAREHOUSE_NAME);
            given(warehouseRepository.findById(warehouseId)).willReturn(Optional.of(warehouse));
            given(warehouseMapper.toResponseDto(warehouse)).willReturn(responseDto);

            warehouseService.updateWarehouse(warehouseId, dto);

            verify(productService, never()).findAllByIds(any());
            verify(warehouseMapper).updateEntity(dto, warehouse);
        }

        @Test
        @DisplayName("со списком продуктов — все id найдены, продукты обновляются на месте")
        void updateWarehouse_withProducts_updatesFoundProducts() {
            UUID productId = UUID.randomUUID();
            ProductUpdateDto productDto = productUpdateDto(productId, "New title", BigDecimal.TEN);
            WarehouseUpdateDto dto = warehouseUpdateDtoWithProducts(UPDATED_WAREHOUSE_NAME, productDto);

            Product product = newProduct("Old title", BigDecimal.ONE);
            product.setId(productId);

            given(warehouseRepository.findById(warehouseId)).willReturn(Optional.of(warehouse));
            given(warehouseMapper.extractProductIdsToUpdate(dto.getProducts())).willReturn(List.of(productId));
            given(productService.findAllByIds(List.of(productId))).willReturn(List.of(product));
            given(warehouseMapper.toResponseDto(warehouse)).willReturn(responseDto);

            warehouseService.updateWarehouse(warehouseId, dto);

            verify(warehouseMapper).updateProductsFromDtos(
                    eq(dto.getProducts()),
                    argThat(map -> map.get(productId) == product));
            verify(warehouseMapper).updateEntity(dto, warehouse);
        }

        @Test
        @DisplayName("id продукта не найден в БД — EntityNotFoundException, изменения склада не применяются")
        void updateWarehouse_productNotFound_throwsAndDoesNotUpdate() {
            UUID missingId = UUID.randomUUID();
            ProductUpdateDto productDto = productUpdateDto(missingId, "New title", BigDecimal.TEN);
            WarehouseUpdateDto dto = warehouseUpdateDtoWithProducts(UPDATED_WAREHOUSE_NAME, productDto);

            given(warehouseRepository.findById(warehouseId)).willReturn(Optional.of(warehouse));
            given(warehouseMapper.extractProductIdsToUpdate(dto.getProducts())).willReturn(List.of(missingId));
            given(productService.findAllByIds(List.of(missingId))).willReturn(List.of());

            assertThatThrownBy(() -> warehouseService.updateWarehouse(warehouseId, dto))
                    .isInstanceOf(EntityNotFoundException.class);

            verify(warehouseMapper, never()).updateEntity(any(), any());
        }

        @Test
        @DisplayName("несуществующий склад — EntityNotFoundException")
        void updateWarehouse_warehouseNotFound_throws() {
            WarehouseUpdateDto dto = warehouseUpdateDto(UPDATED_WAREHOUSE_NAME);
            given(warehouseRepository.findById(warehouseId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> warehouseService.updateWarehouse(warehouseId, dto))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteWarehouseProducts")
    class DeleteWarehouseProducts {

        @Test
        @DisplayName("без productIds — soft delete только самого склада")
        void deleteWarehouseProducts_withoutProductIds_softDeletesWarehouseOnly() {
            given(warehouseRepository.findById(warehouseId)).willReturn(Optional.of(warehouse));

            warehouseService.deleteWarehouseProducts(warehouseId, null);

            assertThat(warehouse.getIsDeleted()).isTrue();
            verify(warehouseRepository).save(warehouse);
        }

        @Test
        @DisplayName("с productIds — soft delete склада и выбранных продуктов, остальные не трогаем")
        void deleteWarehouseProducts_withProductIds_softDeletesSelectedProductsOnly() {
            Product toDelete = newProduct("Delete me", BigDecimal.ONE);
            UUID toDeleteId = UUID.randomUUID();
            toDelete.setId(toDeleteId);

            Product toKeep = newProduct("Keep me", BigDecimal.ONE);
            toKeep.setId(UUID.randomUUID());

            warehouse.setProducts(List.of(toDelete, toKeep));
            given(warehouseRepository.findById(warehouseId)).willReturn(Optional.of(warehouse));

            warehouseService.deleteWarehouseProducts(warehouseId, List.of(toDeleteId));

            assertThat(warehouse.getIsDeleted()).isTrue();
            assertThat(toDelete.getIsDeleted()).isTrue();
            assertThat(toKeep.getIsDeleted()).isFalse();
        }

        @Test
        @DisplayName("несуществующий склад — EntityNotFoundException, save не вызывается")
        void deleteWarehouseProducts_notFound_throwsAndDoesNotSave() {
            given(warehouseRepository.findById(warehouseId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> warehouseService.deleteWarehouseProducts(warehouseId, null))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(warehouseRepository, never()).save(any());
        }
    }
}
