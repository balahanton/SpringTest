package ru.anton.springtest.mapper;

import org.mapstruct.*;
import ru.anton.springtest.dto.*;
import ru.anton.springtest.model.Order;
import ru.anton.springtest.model.SagaTask;
import ru.anton.springtest.model.User;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ACCESSOR_ONLY
)
public interface UserMapper {

    UserResponseDto toResponseDto(User user);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "discountCardNumber", source = "enrichment.discountCardNumber")
    @Mapping(target = "balance", source = "enrichment.balance")
    UserResponseDto toResponseDto(User user, UserEnrichmentClientResponseDto enrichment);

    List<UserResponseDto> toResponseDtoList(List<User> users);

    UserEnrichmentClientRequestDto toEnrichmentCreateDto(SagaTask task);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "discountCardNumber", source = "dto.discountCardNumber")
    @Mapping(target = "balance", source = "dto.balance")
    UserEnrichmentClientRequestDto toEnrichmentRequest(User user, UserCreateDto dto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true),
            @Mapping(target = "enrichmentStatus", constant = "PENDING")
    })
    User toEntity(UserCreateDto dto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true),
            @Mapping(target = "userId", source = "user.id"),
            @Mapping(target = "discountCardNumber", source = "dto.discountCardNumber"),
            @Mapping(target = "balance", source = "dto.balance"),
            @Mapping(target = "status", constant = "PENDING"),
            @Mapping(target = "attempts", constant = "0")
    })
    SagaTask toSagaTask(User user, UserCreateDto dto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    void updateEntity(UserUpdateDto dto, @MappingTarget User user);

    default OrderResponseDto toOrderResponseDto(Order order) {
        if (order == null) {
            return null;
        }
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setDescription(order.getDescription());
        return dto;
    }

    default Order toOrderEntity(OrderCreateDto dto) {
        if (dto == null) {
            return null;
        }
        Order order = new Order();
        order.setDescription(dto.getDescription());
        return order;
    }

    default Order toOrderEntityFromUpdate(OrderUpdateDto dto) {
        if (dto == null) {
            return null;
        }
        Order order = new Order();
        order.setId(dto.getId());
        order.setDescription(dto.getDescription());
        return order;
    }

    @AfterMapping
    default void linkOrders(@MappingTarget User user) {
        if (user.getOrders() != null) {
            for (Order order : user.getOrders()) {
                order.setUser(user);
            }
        }
    }
}