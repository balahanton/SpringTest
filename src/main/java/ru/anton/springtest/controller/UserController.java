package ru.anton.springtest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;
import ru.anton.springtest.api.UserControllerApi;
import ru.anton.springtest.dto.UserCreateDto;
import ru.anton.springtest.dto.UserResponseDto;
import ru.anton.springtest.dto.UserUpdateDto;
import ru.anton.springtest.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerApi {

    private final UserService userService;

    @Override
    public UserResponseDto createUser(UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }

    @Override
    public List<UserResponseDto> getAllUsers(Integer page, Integer size) {
        int pageNumber = (page != null) ? page : 0;
        int pageSize = (size != null) ? size : 10;

        return userService.getAllUsers(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public UserResponseDto getUserById(UUID id) {
        return userService.getUserById(id);
    }

    @Override
    public UserResponseDto updateUser(UUID id, UserUpdateDto userUpdateDto) {
        return userService.updateUser(id, userUpdateDto);
    }

    @Override
    public void deleteUser(UUID id) {
        userService.deleteUser(id);
    }
}