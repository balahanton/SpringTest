package ru.anton.springtest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.anton.springtest.AbstractIntegrationTest;
import ru.anton.springtest.client.EnrichmentServiceAdapter;
import ru.anton.springtest.dto.UserCreateDto;
import ru.anton.springtest.dto.UserUpdateDto;
import ru.anton.springtest.model.User;
import ru.anton.springtest.repository.UserRepository;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.anton.springtest.support.UserTestFixtures.*;

@DisplayName("UserController — интеграционные тесты")
public class UserControllerTest extends AbstractIntegrationTest {

    private static final String USERS_URL = "/api/v1/users";
    private static final String USER_BY_ID_URL = "/api/v1/users/{id}";
    private static final String INVALID_FIELDS_PATH = "$.invalid_fields";

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private EnrichmentServiceAdapter enrichmentServiceAdapter;

    @Test
    @DisplayName("POST /api/v1/users с валидным телом — 201 и тело с id")
    void createUser_valid_returns201WithId() throws Exception {
        UserCreateDto dto = userCreateDto(DEFAULT_USERNAME);

        mockMvc.perform(postJson(USERS_URL, dto))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME));
    }

    @Test
    @DisplayName("POST /api/v1/users без username — 400 и invalid_fields")
    void createUser_missingUsername_returns400() throws Exception {
        UserCreateDto dto = new UserCreateDto();

        mockMvc.perform(postJson(USERS_URL, dto))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(INVALID_FIELDS_PATH + ".username").exists());
    }

    @Test
    @DisplayName("POST /api/v1/users с username в 1 символ — 400 (нарушение Size 2-50)")
    void createUser_usernameTooShort_returns400() throws Exception {
        UserCreateDto dto = userCreateDto("a");

        mockMvc.perform(postJson(USERS_URL, dto))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(INVALID_FIELDS_PATH + ".username").exists());
    }

    @Test
    @DisplayName("GET /api/v1/users — 200 и массив пользователей")
    void getAllUsers_returns200WithArray() throws Exception {
        userRepository.save(newUser(DEFAULT_USERNAME));
        userRepository.save(newUser("second_user"));

        mockMvc.perform(get(USERS_URL).param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} для существующего пользователя — 200")
    void getUserById_existing_returns200() throws Exception {
        User saved = userRepository.save(newUser(DEFAULT_USERNAME));

        mockMvc.perform(get(USER_BY_ID_URL, saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} для несуществующего пользователя — 404 и ProblemDetail")
    void getUserById_notFound_returns404() throws Exception {
        mockMvc.perform(get(USER_BY_ID_URL, UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"));
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} для несуществующего пользователя — 404")
    void updateUser_notFound_returns404() throws Exception {
        UserUpdateDto dto = userUpdateDto(UPDATED_USERNAME);

        mockMvc.perform(putJson(USER_BY_ID_URL, dto, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} — 204")
    void deleteUser_returns204() throws Exception {
        User saved = userRepository.save(newUser(DEFAULT_USERNAME));

        mockMvc.perform(delete(USER_BY_ID_URL, saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} для несуществующего пользователя — 404")
    void deleteUser_notFound_returns404() throws Exception {
        mockMvc.perform(delete(USER_BY_ID_URL, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
