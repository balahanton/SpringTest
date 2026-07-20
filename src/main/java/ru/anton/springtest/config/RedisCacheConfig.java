package ru.anton.springtest.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.anton.springtest.dto.DeliveryResponseDto;
import ru.anton.springtest.dto.UserResponseDto;
import ru.anton.springtest.dto.WarehouseResponseDto;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisCacheConfig {

    public static final String USERS_CACHE = "users";
    public static final String USERS_PAGE_CACHE = "usersPage";
    public static final String WAREHOUSES_CACHE = "warehouses";
    public static final String WAREHOUSES_PAGE_CACHE = "warehousesPage";
    public static final String DELIVERIES_CACHE = "deliveries";
    public static final String DELIVERIES_PAGE_CACHE = "deliveriesPage";

    private static final Duration DEFAULT_TTL = Duration.ofHours(1);
    private static final Duration PAGE_CACHE_TTL = Duration.ofMinutes(2);

    private final ObjectMapper objectMapper;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        RedisSerializer<Object> defaultSerializer = new JacksonJsonRedisSerializer<>(objectMapper, Object.class);

        RedisCacheConfiguration baseConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(DEFAULT_TTL)
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "springtest:cache:" + cacheName + "::")
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(defaultSerializer));

        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                USERS_CACHE, baseConfig.serializeValuesWith(valueSerializer(UserResponseDto.class)),
                USERS_PAGE_CACHE, baseConfig.entryTtl(PAGE_CACHE_TTL).serializeValuesWith(listValueSerializer(UserResponseDto.class)),
                WAREHOUSES_CACHE, baseConfig.serializeValuesWith(valueSerializer(WarehouseResponseDto.class)),
                WAREHOUSES_PAGE_CACHE, baseConfig.entryTtl(PAGE_CACHE_TTL).serializeValuesWith(listValueSerializer(WarehouseResponseDto.class)),
                DELIVERIES_CACHE, baseConfig.serializeValuesWith(valueSerializer(DeliveryResponseDto.class)),
                DELIVERIES_PAGE_CACHE, baseConfig.entryTtl(PAGE_CACHE_TTL).serializeValuesWith(listValueSerializer(DeliveryResponseDto.class))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(baseConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @SuppressWarnings("unchecked")
    private <T> RedisSerializationContext.SerializationPair<Object> valueSerializer(Class<T> type) {
        RedisSerializer<Object> serializer = (RedisSerializer<Object>) new JacksonJsonRedisSerializer<>(objectMapper, type);
        return RedisSerializationContext.SerializationPair.fromSerializer(serializer);
    }

    private RedisSerializationContext.SerializationPair<Object> listValueSerializer(Class<?> elementType) {
        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
        RedisSerializer<Object> serializer = new JacksonJsonRedisSerializer<>(objectMapper, listType);
        return RedisSerializationContext.SerializationPair.fromSerializer(serializer);
    }
}