package ru.anton.springtest.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Method;

@Configuration
public class CacheKeyGeneratorConfig {

    public static final String PAGEABLE_KEY_GENERATOR = "pageableKeyGenerator";

    @Bean(PAGEABLE_KEY_GENERATOR)
    public KeyGenerator pageableKeyGenerator() {
        return (Object target, Method method, Object... params) -> {
            StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(target.getClass().getSimpleName()).append(":").append(method.getName());
            for (Object param : params) {
                if (param instanceof Pageable pageable) {
                    keyBuilder.append(":page=").append(pageable.getPageNumber());
                    keyBuilder.append(":size=").append(pageable.getPageSize());
                    Sort sort = pageable.getSort();
                    if (sort.isSorted()) {
                        keyBuilder.append(":sort=");
                        sort.forEach(order -> keyBuilder.append(order.getProperty())
                                .append(",")
                                .append(order.getDirection())
                                .append(";"));
                    }
                } else if (param != null) {
                    keyBuilder.append(":").append(param);
                }
            }
            return keyBuilder.toString();
        };
    }
}
