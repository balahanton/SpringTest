package ru.anton.springtest.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.anton.springtest.client.EnrichmentServiceAdapter;
import ru.anton.springtest.dto.UserCreateDto;
import ru.anton.springtest.dto.UserEnrichmentClientDto;
import ru.anton.springtest.dto.UserResponseDto;
import ru.anton.springtest.exception.SagaExecutionException;
import ru.anton.springtest.mapper.UserMapper;
import ru.anton.springtest.model.User;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateUserSagaOrchestrator {

    private final UserPersistence userPersistence;
    private final EnrichmentServiceAdapter enrichmentServiceAdapter;
    private final UserMapper userMapper;

    public UserResponseDto execute(UserCreateDto dto) {
        User savedUser = userPersistence.save(dto);
        log.info("Сага: Шаг 1 успешен. ID: {}", savedUser.getId());

        UserEnrichmentClientDto enrichment;
        try {
            enrichment = enrichmentServiceAdapter.createEnrichment(
                    savedUser.getId(), dto.getDiscountCardNumber(), dto.getBalance());
            log.info("Сага: Шаг 2 успешен.");
        } catch (Exception ex) {
            log.error("Сага: Шаг 2 ошибка.");

            rollbackEverything(savedUser.getId());

            throw new SagaExecutionException("Не удалось завершить регистрацию пользователя", ex);
        }

        UserResponseDto responseDto = userMapper.toResponseDto(savedUser);
        responseDto.setDiscountCardNumber(enrichment.getDiscountCardNumber());
        responseDto.setBalance(enrichment.getBalance());
        return responseDto;
    }

    private void rollbackEverything(UUID userId) {
        try {
            enrichmentServiceAdapter.deleteEnrichment(userId);
            log.info("Откат Шага 2 успешен для userId: {}", userId);
        } catch (Exception externalEx) {
            log.warn("Не удалось зачистить данные во внешнем сервисе для userId: {}. Причина: {}",
                    userId, externalEx.getMessage());
        }

        try {
            userPersistence.deleteById(userId);
            log.info("Компенсация успешна. Пользователь {} удален.", userId);
        } catch (Exception compensationEx) {
            log.error("Не удалось удалить пользователя {} при компенсации!", userId, compensationEx);
        }
    }
}
