package ru.anton.springtest.mapper;

import org.mapstruct.*;
import ru.anton.springtest.dto.UserCreateDto;
import ru.anton.springtest.dto.UserResponseDto;
import ru.anton.springtest.dto.UserUpdateDto;
import ru.anton.springtest.model.Order;
import ru.anton.springtest.model.User;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    UserResponseDto toResponseDto(User user);

    List<UserResponseDto> toResponseDtoList(List<User> users);

    User toEntity(UserCreateDto dto);

    void updateEntity(UserUpdateDto dto, @MappingTarget User user);

    @AfterMapping
    default void linkOrders(@MappingTarget User user) {
        if (user.getOrders() != null) {
            for (Order order : user.getOrders()) {
                order.setUser(user);
            }
        }
    }

}
