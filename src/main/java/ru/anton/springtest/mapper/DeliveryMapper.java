package ru.anton.springtest.mapper;

import org.mapstruct.*;
import ru.anton.springtest.dto.DeliveryCreateDto;
import ru.anton.springtest.dto.DeliveryResponseDto;
import ru.anton.springtest.model.Delivery;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DeliveryMapper {

    DeliveryResponseDto toResponseDto(Delivery delivery);

    List<DeliveryResponseDto> toResponseDtoList(List<Delivery> deliveries);

    Delivery toEntity(DeliveryCreateDto dto);

    @AfterMapping
    default void linkDetails(@MappingTarget Delivery delivery) {
        if (delivery.getDetails() != null) {
            delivery.getDetails().setDelivery(delivery);
        }
    }
}
