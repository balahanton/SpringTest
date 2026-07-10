package ru.anton.springtest.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.anton.springtest.dto.UserCreateDto;
import ru.anton.springtest.mapper.UserMapper;
import ru.anton.springtest.model.SagaTask;
import ru.anton.springtest.model.User;
import ru.anton.springtest.repository.SagaTaskRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrichmentSagaService {

    private final SagaTaskRepository sagaTaskRepository;
    private final UserMapper userMapper;

    public void startEnrichmentSaga(User user, UserCreateDto dto) {
        SagaTask task = userMapper.toSagaTask(user, dto);
        sagaTaskRepository.save(task);
    }
}
