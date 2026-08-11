package ru.anton.springtest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.anton.springtest.config.RedisCacheConfig;
import ru.anton.springtest.dto.*;
import ru.anton.springtest.exception.EnrichmentServiceUnavailableException;
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.mapper.UserMapper;
import ru.anton.springtest.model.Order;
import ru.anton.springtest.model.SagaTask;
import ru.anton.springtest.model.User;
import ru.anton.springtest.repository.SagaTaskRepository;
import ru.anton.springtest.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String USER_CREATED_EVENT_TYPE = "UserCreated";

    private final UserRepository userRepository;
    private final SagaTaskRepository sagaTaskRepository;
    private final UserMapper userMapper;
    private final EnrichmentServiceAdapter enrichmentServiceAdapter;
    private final OutboxEventService outboxEventService;

    @Transactional
    public UserResponseDto createUser(UserCreateDto dto) {
        User savedUser = userRepository.save(userMapper.toEntity(dto));

        UserEnrichmentClientRequestDto enrichmentRequest = userMapper.toEnrichmentRequest(savedUser, dto);
        outboxEventService.save(USER_CREATED_EVENT_TYPE, savedUser.getId(), enrichmentRequest);
        UserResponseDto responseDto;
        try {
            UserEnrichmentClientResponseDto enrichment = enrichmentServiceAdapter.createEnrichment(enrichmentRequest);
            responseDto = userMapper.toResponseDto(savedUser, enrichment);
        } catch (EnrichmentServiceUnavailableException ex) {
            SagaTask task = userMapper.toSagaTask(savedUser, dto);
            sagaTaskRepository.save(task);
            responseDto = userMapper.toResponseDto(savedUser);
            log.warn("Enrichment-сервис недоступен, для пользователя {} создана сага", savedUser.getId());
        }

        return responseDto;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = RedisCacheConfig.USERS_CACHE, key = "#id")
    public UserResponseDto getUserById(UUID id) {

        User user = findUserOrThrow(id);
        UserEnrichmentClientResponseDto enrichment = enrichmentServiceAdapter.getEnrichment(id);
        return userMapper.toResponseDto(user, enrichment);
    }

    @Transactional
    @Cacheable(cacheNames = RedisCacheConfig.USERS_PAGE_CACHE, key = "#pageable")
    public List<UserResponseDto> getAllUsers(Pageable pageable) {

        Page<User> userPage = userRepository.findWithLockByIsDeletedFalse(pageable);

        return userMapper.toResponseDtoList(userPage.getContent());
    }

    @Transactional
    @CacheEvict(cacheNames = RedisCacheConfig.USERS_CACHE, key = "#id")
    public UserResponseDto updateUser(UUID id, UserUpdateDto dto) {

        User existingUser = findUserOrThrow(id);

        userMapper.updateEntity(dto, existingUser);

        User updatedUser = userRepository.save(existingUser);
        log.info("Успешно обновлен пользователь с ID: {}", id);
        return userMapper.toResponseDto(updatedUser);
    }

    @Transactional
    @CacheEvict(cacheNames = RedisCacheConfig.USERS_CACHE, key = "#id")
    public void deleteUser(UUID id) {

        User user = findUserOrThrow(id);

        user.setIsDeleted(true);

        if (user.getOrders() != null) {
            for (Order order : user.getOrders()) {
                order.setIsDeleted(true);
            }
        }

        userRepository.save(user);

        log.info("Пользователь с ID {} и его заказы переведены в статус удаленных", id);
    }

    @CacheEvict(cacheNames = RedisCacheConfig.USERS_CACHE, key = "#id")
    public void evictUserCache(UUID id) {
        log.debug("Кэш пользователя {} инвалидирован", id);
    }

    private User findUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .filter(user -> !user.getIsDeleted())
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден или был удален", id);
                    return new EntityNotFoundException("User with ID " + id + " not found");
                });
    }
}