package ru.anton.springtest.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.anton.springtest.AbstractIntegrationTest;
import ru.anton.springtest.dto.UserEnrichmentClientResponseDto;
import ru.anton.springtest.exception.EntityNotFoundException;
import ru.anton.springtest.model.User;
import ru.anton.springtest.repository.UserRepository;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static ru.anton.springtest.util.UserTestFixtures.*;

@DisplayName("UserService — тесты Redis-кэша")
public class UserServiceCacheTest extends AbstractIntegrationTest {

    @Autowired
    private UserService userService;

    @MockitoSpyBean
    private UserRepository userRepository;

    @MockitoBean
    private EnrichmentServiceAdapter enrichmentServiceAdapter;

    private UUID userId;

    @BeforeEach
    void setUp() {
        given(enrichmentServiceAdapter.getEnrichment(any())).willReturn(null);

        User user = newUser(DEFAULT_USERNAME);
        userId = userRepository.save(user).getId();
    }

    @Test
    @DisplayName("после deleteUser кэш инвалидируется — следующий getUserById снова идёт в БД")
    void deleteUser_evictsCache_nextGetGoesToDatabase() {
        UserEnrichmentClientResponseDto enrichment = new UserEnrichmentClientResponseDto();
        enrichment.setUserId(userId);
        enrichment.setDiscountCardNumber(DEFAULT_DISCOUNT_CARD);
        enrichment.setBalance(DEFAULT_BALANCE);
        when(enrichmentServiceAdapter.getEnrichment(userId)).thenReturn(enrichment);

        userService.getUserById(userId);

        userService.deleteUser(userId);

        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));

        verify(userRepository, times(3)).findById(userId);
    }

}
