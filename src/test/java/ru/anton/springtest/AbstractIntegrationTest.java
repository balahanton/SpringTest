package ru.anton.springtest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public abstract class AbstractIntegrationTest {

    private static final String SCHEMA = "spring_test";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired(required = false)
    private ObjectMapper springObjectMapper;

    @Autowired
    protected StringRedisTemplate redisTemplate;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    private final ObjectMapper localMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void cleanDatabase() {
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT tablename FROM pg_tables WHERE schemaname = ? AND tablename <> 'flyway_schema_history'",
                String.class,
                SCHEMA);

        if (tables.isEmpty()) {
            return;
        }

        String tableList = tables.stream()
                .map(table -> SCHEMA + "." + table)
                .collect(Collectors.joining(", "));
        jdbcTemplate.execute("TRUNCATE TABLE " + tableList + " RESTART IDENTITY CASCADE");
    }

    @AfterEach
    void clearRedis() {
        var connectionFactory = redisTemplate.getConnectionFactory();

        if (connectionFactory == null) {
            return;
        }

        try (RedisConnection connection = connectionFactory.getConnection()) {
            connection.serverCommands().flushAll();
        }
    }

    protected MockHttpServletRequestBuilder postJson(String url, Object body) {
        return post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body));
    }

    protected MockHttpServletRequestBuilder putJson(String url, Object body, Object... uriVars) {
        return put(url, uriVars)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body));
    }

    protected String toJson(Object object) {
        try {
            ObjectMapper mapper = (springObjectMapper != null) ? springObjectMapper : localMapper;
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации в JSON", e);
        }
    }
}
