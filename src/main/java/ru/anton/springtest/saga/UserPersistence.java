package ru.anton.springtest.saga;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.anton.springtest.dto.UserCreateDto;
import ru.anton.springtest.mapper.UserMapper;
import ru.anton.springtest.model.User;
import ru.anton.springtest.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class UserPersistence {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    User save(UserCreateDto dto) {
        User user = userMapper.toEntity(dto);
        return userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
}
