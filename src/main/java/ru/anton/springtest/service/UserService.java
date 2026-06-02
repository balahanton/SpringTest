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
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.mapper.UserMapper;
import ru.anton.springtest.model.User;
import ru.anton.springtest.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto createUser(UserCreateDto dto) {
        log.info("Начало метода создания пользователя: {}", dto.getUsername());

        User user = userMapper.toEntity(dto);
        User savedUser = userRepository.save(user);

        log.info("Пользователь успешно создан с ID: {}", savedUser.getId());
        return userMapper.toResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID id) {
        log.info("Запрос на получение пользователя по ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден в базе данных", id);
                    return new EntityNotFoundException("Пользователь с ID " + id + " не найден");
                });

        return userMapper.toResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers(Pageable pageable) {
        log.info("Запрос списка пользователей. Страница: {}, Размер: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<User> userPage = userRepository.findAll(pageable);

        return userMapper.toResponseDtoList(userPage.getContent());
    }

    @Transactional
    public UserResponseDto updateUser(UUID id, UserUpdateDto dto) {
        log.info("Начало обновления пользователя с ID: {}. Новое имя: {}", id, dto.getUsername());

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден для обновления", id);
                    return new EntityNotFoundException("Пользователь с ID " + id + " не найден");
                });

        existingUser.setUsername(dto.getUsername());

        if (dto.getOrders() == null || dto.getOrders().isEmpty()) {
            log.info("Список заказов пуст. Очистка заказов для пользователя ID: {}", id);
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
        log.info("Запрос на удаление пользователя с ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден для удаления", id);
                    return new EntityNotFoundException("Пользователь с ID " + id + " не найден");
                });

        userRepository.delete(user);

        log.info("Пользователь с ID {} и его заказы переведены в статус удаленных", id);
    }
}