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

    DeliveryResponseDto toResponseDto(Delivery delivery);

    List<DeliveryResponseDto> toResponseDtoList(List<Delivery> deliveries);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    Delivery toEntity(DeliveryCreateDto dto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    void updateEntity(DeliveryUpdateDto dto, @MappingTarget Delivery delivery);

    @Mapping(target = "deliveryId", source = "id")
    DeliveryCreatedEventPayloadDto toDeliveryCreatedEventPayloadDto(Delivery delivery);

    default DeliveryDetailsResponseDto toDetailsResponseDto(DeliveryDetails details) {
        if (details == null) {
            return null;
        }
        DeliveryDetailsResponseDto dto = new DeliveryDetailsResponseDto();
        dto.setId(details.getId());
        dto.setCourierName(details.getCourierName());
        dto.setDeliveryNotes(details.getDeliveryNotes());
        return dto;
    }

    default DeliveryDetails toDetailsEntity(DeliveryDetailsCreateDto dto) {
        if (dto == null) {
            return null;
        }
        DeliveryDetails details = new DeliveryDetails();
        details.setCourierName(dto.getCourierName());
        details.setDeliveryNotes(dto.getDeliveryNotes());
        return details;
    }

    default void updateDetailsEntity(DeliveryDetailsUpdateDto dto, @MappingTarget DeliveryDetails details) {
        if (dto == null || details == null) {
            return;
        }
        details.setCourierName(dto.getCourierName());
        details.setDeliveryNotes(dto.getDeliveryNotes());
    }

    @AfterMapping
    default void linkDetails(@MappingTarget Delivery delivery) {
        if (delivery.getDetails() != null) {
            delivery.getDetails().setDelivery(delivery);
        }
    }
}
