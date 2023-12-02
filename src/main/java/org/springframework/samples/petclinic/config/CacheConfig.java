package org.springframework.samples.petclinic.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {
    @Value("${cache.expiration.minutes}")
    private int cacheExpirationMinutes;

    @Value("${cache.maximum.size}")
    private int cacheMaximumSize;
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .expireAfterAccess(cacheExpirationMinutes, TimeUnit.MINUTES)
                .maximumSize(cacheMaximumSize)
        );
        return cacheManager;
    }
}
