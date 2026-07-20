package ru.anton.springtest.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anton.springtest.dto.UserCreateDto;
import ru.anton.springtest.dto.UserEnrichmentClientResponseDto;
import ru.anton.springtest.dto.UserResponseDto;
import ru.anton.springtest.dto.UserUpdateDto;
import ru.anton.springtest.exception.EnrichmentServiceUnavailableException;
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.mapper.UserMapper;
import ru.anton.springtest.model.Order;
import ru.anton.springtest.model.SagaTask;
import ru.anton.springtest.model.User;
import ru.anton.springtest.repository.SagaTaskRepository;
import ru.anton.springtest.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static ru.anton.springtest.util.UserTestFixtures.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService — юнит-тесты")
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    SagaTaskRepository sagaTaskRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    EnrichmentServiceAdapter enrichmentServiceAdapter;

    @InjectMocks
    UserService userService;

    UUID userId;
    User user;
    UserResponseDto responseDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = newUser(DEFAULT_USERNAME);
        user.setId(userId);
        user.setIsDeleted(false);

        responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setUsername(DEFAULT_USERNAME);
    }

    @Test
    @DisplayName("создаёт пользователя, синхронно обогащает при доступном enrichment-сервисе")
    void createUser_enrichmentAvailable_appliesEnrichmentSynchronously() {
        UserCreateDto dto = userCreateDto(DEFAULT_USERNAME);
        User mappedUser = newUser(DEFAULT_USERNAME);

        UserEnrichmentClientResponseDto enrichment = new UserEnrichmentClientResponseDto();
        enrichment.setDiscountCardNumber(DEFAULT_DISCOUNT_CARD);
        enrichment.setBalance(DEFAULT_BALANCE);

        UserResponseDto enrichedResponse = new UserResponseDto();
        enrichedResponse.setId(userId);
        enrichedResponse.setUsername(DEFAULT_USERNAME);
        enrichedResponse.setDiscountCardNumber(DEFAULT_DISCOUNT_CARD);
        enrichedResponse.setBalance(DEFAULT_BALANCE);

        given(userMapper.toEntity(dto)).willReturn(mappedUser);
        given(userRepository.save(mappedUser)).willReturn(user);
        given(enrichmentServiceAdapter.createEnrichment(any())).willReturn(enrichment);
        given(userMapper.toResponseDto(user, enrichment)).willReturn(enrichedResponse);

        UserResponseDto result = userService.createUser(dto);

        assertThat(result.getDiscountCardNumber()).isEqualTo(DEFAULT_DISCOUNT_CARD);
        assertThat(result.getBalance()).isEqualByComparingTo(DEFAULT_BALANCE);
        verify(sagaTaskRepository, never()).save(any());
    }

    @Test
    @DisplayName("создаёт пользователя, сохраняет и запускает сагу обогащения")
    void createUser_savesUserAndStartsEnrichmentSaga() {
        UserCreateDto dto = userCreateDto(DEFAULT_USERNAME);
        User mappedUser = newUser(DEFAULT_USERNAME);
        SagaTask sagaTask = new SagaTask();

        given(userMapper.toEntity(dto)).willReturn(mappedUser);
        given(userRepository.save(mappedUser)).willReturn(user);
        given(enrichmentServiceAdapter.createEnrichment(any()))
                .willThrow(new EnrichmentServiceUnavailableException("unavailable", null));
        given(userMapper.toSagaTask(user, dto)).willReturn(sagaTask);
        given(userMapper.toResponseDto(user)).willReturn(responseDto);

        UserResponseDto result = userService.createUser(dto);

        assertThat(result.getId()).isEqualTo(userId);
        verify(userRepository).save(mappedUser);
        verify(sagaTaskRepository).save(sagaTask);
        verify(enrichmentServiceAdapter).createEnrichment(any());
    }

    @Test
    @DisplayName("получение пользователя — всегда обогащает данными enrichment-service")
    void getUserById_alwaysAppliesEnrichmentToResponse() {
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        UserEnrichmentClientResponseDto enrichment = new UserEnrichmentClientResponseDto();
        enrichment.setDiscountCardNumber(DEFAULT_DISCOUNT_CARD);
        enrichment.setBalance(DEFAULT_BALANCE);
        given(enrichmentServiceAdapter.getEnrichment(userId)).willReturn(enrichment);

        UserResponseDto enrichedResponse = new UserResponseDto();
        enrichedResponse.setId(userId);
        enrichedResponse.setUsername(DEFAULT_USERNAME);
        enrichedResponse.setDiscountCardNumber(DEFAULT_DISCOUNT_CARD);
        enrichedResponse.setBalance(DEFAULT_BALANCE);
        given(userMapper.toResponseDto(user, enrichment)).willReturn(enrichedResponse);

        UserResponseDto result = userService.getUserById(userId);

        assertThat(result.getDiscountCardNumber()).isEqualTo(DEFAULT_DISCOUNT_CARD);
        assertThat(result.getBalance()).isEqualByComparingTo(DEFAULT_BALANCE);
        verify(enrichmentServiceAdapter).getEnrichment(userId);
    }

    @Test
    @DisplayName("несуществующий пользователь — EntityNotFoundException")
    void getUserById_userNotFound_throwsException() {
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(userId.toString());
    }

    @Test
    @DisplayName("удалённый (soft delete) пользователь — EntityNotFoundException")
    void getUserById_userIsDeleted_throwsException() {
        user.setIsDeleted(true);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("soft delete пользователя и каскадно всех его заказов")
    void deleteUser_softDeletesUserAndCascadesOrders() {
        Order order1 = newOrder(DEFAULT_ORDER_DESCRIPTION, user);
        Order order2 = newOrder("Another order", user);
        user.setOrders(List.of(order1, order2));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        userService.deleteUser(userId);

        assertThat(user.getIsDeleted()).isTrue();
        assertThat(order1.getIsDeleted()).isTrue();
        assertThat(order2.getIsDeleted()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("несуществующий пользователь — EntityNotFoundException, save не вызывается")
    void deleteUser_userNotFound_throwsExceptionAndDoesNotSave() {
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(EntityNotFoundException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("обновляет поля пользователя через маппер и сохраняет")
    void updateUser_updatesFieldsAndReturnsDto() {
        UserUpdateDto updateDto = userUpdateDto(UPDATED_USERNAME);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);

        UserResponseDto updatedResponse = new UserResponseDto();
        updatedResponse.setId(userId);
        updatedResponse.setUsername(UPDATED_USERNAME);
        given(userMapper.toResponseDto(user)).willReturn(updatedResponse);

        UserResponseDto result = userService.updateUser(userId, updateDto);

        verify(userMapper).updateEntity(updateDto, user);
        assertThat(result.getUsername()).isEqualTo(UPDATED_USERNAME);
    }

    @Test
    @DisplayName("несуществующий пользователь — EntityNotFoundException")
    void updateUser_userNotFound_throwsException() {
        UserUpdateDto updateDto = userUpdateDto(UPDATED_USERNAME);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(userId, updateDto));
    }
}
