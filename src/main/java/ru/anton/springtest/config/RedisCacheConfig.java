package ru.anton.springtest.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    public static final String USERS_CACHE = "users";
    public static final String USERS_PAGE_CACHE = "usersPage";
    public static final String WAREHOUSES_CACHE = "warehouses";
    public static final String WAREHOUSES_PAGE_CACHE = "warehousesPage";
    public static final String DELIVERIES_CACHE = "deliveries";
    public static final String DELIVERIES_PAGE_CACHE = "deliveriesPage";

    private static final Duration DEFAULT_TTL = Duration.ofHours(1);

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        BasicPolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("ru.anton.springtest")
                .allowIfSubType("java.util")
                .build();

        GenericJacksonJsonRedisSerializer jsonSerializer = GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(typeValidator)
                .enableSpringCacheNullValueSupport()
                .build();

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(DEFAULT_TTL)
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "springtest:cache:" + cacheName + "::")
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        Map<String, RedisCacheConfiguration> perCacheConfig = new HashMap<>();
        perCacheConfig.put(USERS_CACHE, defaultConfig.entryTtl(DEFAULT_TTL));
        perCacheConfig.put(USERS_PAGE_CACHE, defaultConfig.entryTtl(DEFAULT_TTL));
        perCacheConfig.put(WAREHOUSES_CACHE, defaultConfig.entryTtl(DEFAULT_TTL));
        perCacheConfig.put(WAREHOUSES_PAGE_CACHE, defaultConfig.entryTtl(DEFAULT_TTL));
        perCacheConfig.put(DELIVERIES_CACHE, defaultConfig.entryTtl(DEFAULT_TTL));
        perCacheConfig.put(DELIVERIES_PAGE_CACHE, defaultConfig.entryTtl(DEFAULT_TTL));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(perCacheConfig)
                .build();
    }
}