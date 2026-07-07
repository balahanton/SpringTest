package ru.anton.springtest.util;

import ru.anton.springtest.dto.UserCreateDto;
import ru.anton.springtest.dto.UserUpdateDto;
import ru.anton.springtest.model.Order;
import ru.anton.springtest.model.User;

import java.math.BigDecimal;

public final class UserTestFixtures {

    public static final String DEFAULT_USERNAME = "IVAN1";
    public static final String UPDATED_USERNAME = "IVAN2_UPDATED";
    public static final String DEFAULT_DISCOUNT_CARD = "1111";
    public static final BigDecimal DEFAULT_BALANCE = BigDecimal.valueOf(150.50);
    public static final String DEFAULT_ORDER_DESCRIPTION = "TEST DESCRIPTION";

    private UserTestFixtures() {
    }

    public static User newUser(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }

    public static Order newOrder(String description, User user) {
        Order order = new Order();
        order.setDescription(description);
        order.setUser(user);
        return order;
    }

    public static UserCreateDto userCreateDto(String username) {
        return userCreateDtoWithCard(username, DEFAULT_DISCOUNT_CARD, DEFAULT_BALANCE);
    }

    public static UserCreateDto userCreateDtoWithCard(String username, String cardNumber, BigDecimal balance) {
        UserCreateDto dto = new UserCreateDto();
        dto.setUsername(username);
        dto.setDiscountCardNumber(cardNumber);
        dto.setBalance(balance);
        return dto;
    }

    public static UserUpdateDto userUpdateDto(String username) {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setUsername(username);
        return dto;
    }
}
