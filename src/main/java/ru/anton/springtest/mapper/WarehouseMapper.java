package ru.anton.springtest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.anton.springtest.dto.WarehouseCreateDto;
import ru.anton.springtest.dto.WarehouseResponseDto;
import ru.anton.springtest.model.Warehouse;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WarehouseMapper {

    WarehouseResponseDto toResponseDto(Warehouse warehouse);

    List<WarehouseResponseDto> toResponseDtoList(List<Warehouse> warehouses);

    Warehouse toEntity(WarehouseCreateDto dto);

}
