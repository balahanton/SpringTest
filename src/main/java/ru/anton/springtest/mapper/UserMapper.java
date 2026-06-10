package ru.anton.springtest.mapper;

import org.mapstruct.*;
import ru.anton.springtest.dto.*;
import ru.anton.springtest.model.Order;
import ru.anton.springtest.model.User;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ACCESSOR_ONLY
)
public interface UserMapper {

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "orders", source = "orders")
    })
    UserResponseDto toResponseDto(User user);

    List<UserResponseDto> toResponseDtoList(List<User> users);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "orders", source = "orders"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    User toEntity(UserCreateDto dto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "orders", source = "orders"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    void updateEntity(UserUpdateDto dto, @MappingTarget User user);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "description", source = "description")
    })
    OrderResponseDto toOrderResponseDto(Order order);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "description", source = "description"),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    Order toOrderEntity(OrderCreateDto dto);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "description", source = "description"),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    Order toOrderEntityFromUpdate(OrderUpdateDto dto);

    @AfterMapping
    default void linkOrders(@MappingTarget User user) {
        if (user.getOrders() != null) {
            for (Order order : user.getOrders()) {
                order.setUser(user);
            }
        }
    }

}
