package ru.anton.springtest.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import ru.anton.springtest.model.User;
import ru.anton.springtest.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.save(user);
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User update(UUID id, @NonNull User user) {
        User existing = findById(id);
        existing.setUsername(user.getUsername());
        return userRepository.save(existing);
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

}
