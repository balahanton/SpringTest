package ru.anton.springtest.mapper;

import org.mapstruct.*;
import ru.anton.springtest.dto.ProductUpdateDto;
import ru.anton.springtest.dto.WarehouseCreateDto;
import ru.anton.springtest.dto.WarehouseResponseDto;
import ru.anton.springtest.dto.WarehouseUpdateDto;
import ru.anton.springtest.model.Product;
import ru.anton.springtest.model.Warehouse;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WarehouseMapper {

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "products", source = "products")
    })
    WarehouseResponseDto toResponseDto(Warehouse warehouse);

    List<WarehouseResponseDto> toResponseDtoList(List<Warehouse> warehouses);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "products", source = "products"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    Warehouse toEntity(WarehouseCreateDto dto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "products", source = "products"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    void updateEntity(WarehouseUpdateDto dto, @MappingTarget Warehouse warehouse);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "title", source = "title"),
            @Mapping(target = "price", source = "price"),
            @Mapping(target = "warehouses", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    void updateProductEntity(ProductUpdateDto dto, @MappingTarget Product product);

}
