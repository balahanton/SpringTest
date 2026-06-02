package ru.anton.springtest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anton.springtest.dto.UserCreateDto;
import ru.anton.springtest.dto.UserResponseDto;
import ru.anton.springtest.dto.UserUpdateDto;
import ru.anton.springtest.excteption.EntityNotFoundException;
import ru.anton.springtest.mapper.UserMapper;
import ru.anton.springtest.model.User;
import ru.anton.springtest.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto createUser(UserCreateDto dto) {

        User user = userMapper.toEntity(dto);
        User savedUser = userRepository.save(user);

        log.info("Пользователь успешно создан с ID: {}", savedUser.getId());
        return userMapper.toResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));

        return userMapper.toResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers(Pageable pageable) {

        Page<User> userPage = userRepository.findAll(pageable);

        return userMapper.toResponseDtoList(userPage.getContent());
    }

    @Transactional
    public UserResponseDto updateUser(UUID id, UserUpdateDto dto) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found for update"));

        existingUser.setUsername(dto.getUsername());

        if (dto.getOrders() == null || dto.getOrders().isEmpty()) {
            existingUser.getOrders().clear();
        } else {
            existingUser.getOrders().removeIf(existingOrder ->
                    dto.getOrders().stream()
                            .filter(dtoOrder -> dtoOrder.getId() != null)
                            .noneMatch(dtoOrder -> dtoOrder.getId().equals(existingOrder.getId()))
            );

            userMapper.updateEntity(dto, existingUser);
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("Данные пользователя с ID {} успешно обновлены", id);
        return userMapper.toResponseDto(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found for deletion"));

        userRepository.delete(user);

        log.info("Пользователь с ID {} и его заказы переведены в статус удаленных", id);
    }
}
