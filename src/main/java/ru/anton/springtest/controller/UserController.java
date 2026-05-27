package ru.anton.springtest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.anton.springtest.api.UsersApi;
import ru.anton.springtest.dto.UserRequest;
import ru.anton.springtest.dto.UserResponse;
import ru.anton.springtest.model.User;
import ru.anton.springtest.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponse> createUser(UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());

        User saved = userService.create(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(saved.getId());
        userResponse.setUsername(saved.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @Override
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userResponseList = userService.findAll().stream()
                .map(user -> {
                    UserResponse userResponse = new UserResponse();
                    userResponse.setId(user.getId());
                    userResponse.setUsername(user.getUsername());
                    return userResponse;
                })
                .toList();
        return ResponseEntity.ok(userResponseList);
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(UUID uuid) {
        User user = userService.findById(uuid);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());

        return ResponseEntity.ok(userResponse);
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(UUID uuid, UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());

        User updated = userService.update(uuid, user);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(updated.getId());
        userResponse.setUsername(updated.getUsername());
        return ResponseEntity.ok(userResponse);
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID uuid) {
        userService.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }
}
