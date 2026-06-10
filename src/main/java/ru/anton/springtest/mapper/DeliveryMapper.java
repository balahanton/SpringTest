package ru.anton.springtest.mapper;

import org.mapstruct.*;
import ru.anton.springtest.dto.*;
import ru.anton.springtest.model.Delivery;
import ru.anton.springtest.model.DeliveryDetails;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DeliveryMapper {

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "address", source = "address"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "details.id", source = "details.id"),
            @Mapping(target = "details.courierName", source = "details.courierName"),
            @Mapping(target = "details.deliveryNotes", source = "details.deliveryNotes")
    })
    DeliveryResponseDto toResponseDto(Delivery delivery);

    List<DeliveryResponseDto> toResponseDtoList(List<Delivery> deliveries);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "address", source = "address"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "details.id", ignore = true),
            @Mapping(target = "details.courierName", source = "details.courierName"),
            @Mapping(target = "details.deliveryNotes", source = "details.deliveryNotes"),
            @Mapping(target = "details.delivery", ignore = true),
            @Mapping(target = "details.createdAt", ignore = true),
            @Mapping(target = "details.updatedAt", ignore = true),
            @Mapping(target = "details.isDeleted", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    Delivery toEntity(DeliveryCreateDto dto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "address", source = "address"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "details.id", ignore = true),
            @Mapping(target = "details.courierName", source = "details.courierName"),
            @Mapping(target = "details.deliveryNotes", source = "details.deliveryNotes"),
            @Mapping(target = "details.delivery", ignore = true),
            @Mapping(target = "details.createdAt", ignore = true),
            @Mapping(target = "details.updatedAt", ignore = true),
            @Mapping(target = "details.isDeleted", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    void updateEntity(DeliveryUpdateDto dto, @MappingTarget Delivery delivery);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "courierName", source = "courierName"),
            @Mapping(target = "deliveryNotes", source = "deliveryNotes")
    })
    DeliveryDetailsResponseDto toDetailsResponseDto(DeliveryDetails details);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "courierName", source = "courierName"),
            @Mapping(target = "deliveryNotes", source = "deliveryNotes"),
            @Mapping(target = "delivery", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    DeliveryDetails toDetailsEntity(DeliveryDetailsCreateDto dto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "courierName", source = "courierName"),
            @Mapping(target = "deliveryNotes", source = "deliveryNotes"),
            @Mapping(target = "delivery", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    void updateDetailsEntity(DeliveryDetailsUpdateDto dto, @MappingTarget DeliveryDetails details);

    @AfterMapping
    default void linkDetails(@MappingTarget Delivery delivery) {
        if (delivery.getDetails() != null) {
            delivery.getDetails().setDelivery(delivery);
        }
    }
}
