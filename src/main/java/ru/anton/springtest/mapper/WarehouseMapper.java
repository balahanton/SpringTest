package ru.anton.springtest.mapper;

import org.mapstruct.*;
import ru.anton.springtest.dto.*;
import ru.anton.springtest.model.Product;
import ru.anton.springtest.model.Warehouse;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ACCESSOR_ONLY
)
public interface WarehouseMapper {

    WarehouseResponseDto toResponseDto(Warehouse warehouse);

    List<WarehouseResponseDto> toResponseDtoList(List<Warehouse> warehouses);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    Warehouse toEntity(WarehouseCreateDto dto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "products", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    void updateEntity(WarehouseUpdateDto dto, @MappingTarget Warehouse warehouse);

    default void updateProductFromDto(ProductUpdateDto dto, @MappingTarget Product product) {
        if (dto == null || product == null) {
            return;
        }
        product.setTitle(dto.getTitle());
        product.setPrice(dto.getPrice());
    }

    default Product toProductEntityFromUpdate(ProductUpdateDto dto) {
        if (dto == null) {
            return null;
        }
        Product product = new Product();
        product.setTitle(dto.getTitle());
        product.setPrice(dto.getPrice());
        return product;
    }

    default ProductResponseDto toProductResponseDto(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setPrice(product.getPrice());
        return dto;
    }

    default Product toProductEntity(ProductCreateDto dto) {
        if (dto == null) {
            return null;
        }
        Product product = new Product();
        product.setTitle(dto.getTitle());
        product.setPrice(dto.getPrice());
        return product;
    }
}
