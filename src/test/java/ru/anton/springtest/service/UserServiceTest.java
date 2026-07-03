package ru.anton.springtest.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anton.springtest.client.EnrichmentServiceAdapter;
import ru.anton.springtest.client.dto.UserEnrichmentClientDto;
import ru.anton.springtest.dto.UserCreateDto;
import ru.anton.springtest.dto.UserResponseDto;
import ru.anton.springtest.dto.UserUpdateDto;
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.mapper.UserMapper;
import ru.anton.springtest.model.Order;
import ru.anton.springtest.model.User;
import ru.anton.springtest.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static ru.anton.springtest.support.UserTestFixtures.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService — юнит-тесты")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private EnrichmentServiceAdapter enrichmentServiceAdapter;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User user;
    private UserResponseDto responseDto;

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

    @Nested
    @DisplayName("createUser")
    class CreateUser {

        @Test
        @DisplayName("если discountCardNumber не передан — enrichment-service не вызывается")
        void createUser_withoutDiscountCard_doesNotCallEnrichment() {
            UserCreateDto dto = userCreateDto(DEFAULT_USERNAME);
            given(userMapper.toEntity(dto)).willReturn(user);
            given(userRepository.save(user)).willReturn(user);
            given(userMapper.toResponseDto(user)).willReturn(responseDto);

            UserResponseDto result = userService.createUser(dto);

            assertThat(result.getId()).isEqualTo(userId);
            verify(enrichmentServiceAdapter, never()).createEnrichment(any(), any(), any());
        }

        @Test
        @DisplayName("если discountCardNumber передан — вызывается enrichment-service, его данные применяются к ответу")
        void createUser_withDiscountCard_callsEnrichmentAndAppliesResult() {
            UserCreateDto dto = userCreateDtoWithCard(DEFAULT_USERNAME, DEFAULT_DISCOUNT_CARD, DEFAULT_BALANCE);
            given(userMapper.toEntity(dto)).willReturn(user);
            given(userRepository.save(user)).willReturn(user);
            given(userMapper.toResponseDto(user)).willReturn(responseDto);

            UserEnrichmentClientDto enrichment = new UserEnrichmentClientDto();
            enrichment.setDiscountCardNumber(DEFAULT_DISCOUNT_CARD);
            enrichment.setBalance(DEFAULT_BALANCE);
            given(enrichmentServiceAdapter.createEnrichment(userId, DEFAULT_DISCOUNT_CARD, DEFAULT_BALANCE))
                    .willReturn(enrichment);

            UserResponseDto result = userService.createUser(dto);

            assertThat(result.getDiscountCardNumber()).isEqualTo(DEFAULT_DISCOUNT_CARD);
            assertThat(result.getBalance()).isEqualByComparingTo(DEFAULT_BALANCE);
            verify(enrichmentServiceAdapter).createEnrichment(userId, DEFAULT_DISCOUNT_CARD, DEFAULT_BALANCE);
        }

        @Test
        @DisplayName("если карта передана без баланса — используется BigDecimal.ZERO")
        void createUser_cardWithoutBalance_usesZeroBalance() {
            UserCreateDto dto = userCreateDtoWithCard(DEFAULT_USERNAME, DEFAULT_DISCOUNT_CARD, null);
            given(userMapper.toEntity(dto)).willReturn(user);
            given(userRepository.save(user)).willReturn(user);
            given(userMapper.toResponseDto(user)).willReturn(responseDto);
            given(enrichmentServiceAdapter.createEnrichment(eq(userId), eq(DEFAULT_DISCOUNT_CARD), eq(BigDecimal.ZERO)))
                    .willReturn(null);

            userService.createUser(dto);

            verify(enrichmentServiceAdapter).createEnrichment(userId, DEFAULT_DISCOUNT_CARD, BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("getUserById")
    class GetUserById {

        @Test
        @DisplayName("применяет данные enrichment-service к ответу")
        void getUserById_appliesEnrichmentToResponse() {
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(userMapper.toResponseDto(user)).willReturn(responseDto);

            UserEnrichmentClientDto enrichment = new UserEnrichmentClientDto();
            enrichment.setDiscountCardNumber(DEFAULT_DISCOUNT_CARD);
            enrichment.setBalance(DEFAULT_BALANCE);
            given(enrichmentServiceAdapter.getEnrichment(userId)).willReturn(enrichment);

            UserResponseDto result = userService.getUserById(userId);

            assertThat(result.getDiscountCardNumber()).isEqualTo(DEFAULT_DISCOUNT_CARD);
            assertThat(result.getBalance()).isEqualByComparingTo(DEFAULT_BALANCE);
        }

        @Test
        @DisplayName("если enrichment-service недоступен (fallback вернул null) — поля карты остаются null")
        void getUserById_enrichmentReturnsNull_cardFieldsStayNull() {
            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(userMapper.toResponseDto(user)).willReturn(responseDto);
            given(enrichmentServiceAdapter.getEnrichment(userId)).willReturn(null);

            UserResponseDto result = userService.getUserById(userId);

            assertThat(result.getDiscountCardNumber()).isNull();
            assertThat(result.getBalance()).isNull();
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
    }

    @Nested
    @DisplayName("deleteUser")
    class DeleteUser {

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
    }

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

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
}
