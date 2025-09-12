package org.springframework.samples.petclinic.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration for PetClinic REST API
 * Uses Caffeine as the cache provider with appropriate TTL and size limits
 */
@Configuration
@EnableCaching
public class CacheConfig {
    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    public static final String VETS_CACHE = "vets";
    public static final String OWNERS_CACHE = "owners";
    public static final String PETS_CACHE = "pets";
    public static final String VISITS_CACHE = "visits";
    public static final String SPECIALTIES_CACHE = "specialties";
    public static final String PET_TYPES_CACHE = "petTypes";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        cacheManager.setCacheNames(java.util.Arrays.asList("vets", "owners", "pets", "petTypes", "specialties", "visits"));

        logger.info("Initialized Caffeine cache manager with caches: vets, owners, pets, petTypes, specialties, visits");
        return cacheManager;
    }

    @Bean
    public Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .recordStats()
                .removalListener((key, value, cause) ->
                    logger.debug("Cache entry removed - Key: {}, Cause: {}", key, cause));
    }

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                logger.error("Cache get error - Cache: {}, Key: {}", cache.getName(), key, exception);
                super.handleCacheGetError(exception, cache, key);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
                logger.error("Cache put error - Cache: {}, Key: {}", cache.getName(), key, exception);
                super.handleCachePutError(exception, cache, key, value);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                logger.error("Cache evict error - Cache: {}, Key: {}", cache.getName(), key, exception);
                super.handleCacheEvictError(exception, cache, key);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
                logger.error("Cache clear error - Cache: {}", cache.getName(), exception);
                super.handleCacheClearError(exception, cache);
            }
        };
    }
}
