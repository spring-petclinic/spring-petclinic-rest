/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.samples.petclinic.cache.HybridCacheManager;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private static final List<String> CACHE_NAMES = Arrays.asList(
        "vets", "vetById",
        "owners", "ownerById", "ownersByLastName",
        "pets", "petById",
        "petTypes", "petTypeById",
        "specialties", "specialtyById", "specialtiesByNameIn",
        "visits", "visitById", "visitsByPetId"
    );

    @Autowired
    private CacheProperties cacheProperties;

    @Bean(name = "l1CacheManager")
    public CacheManager l1CacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats());
        cacheManager.setCacheNames(CACHE_NAMES);
        return cacheManager;
    }

    @Bean(name = "l2CacheManager")
    @ConditionalOnProperty(name = "petclinic.cache.hybrid.enabled", havingValue = "true")
    public CacheManager l2CacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(cacheConfig)
            .transactionAware()
            .build();
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "petclinic.cache.hybrid.enabled", havingValue = "true")
    public CacheManager hybridCacheManager(CacheManager l1CacheManager, CacheManager l2CacheManager) {
        return new HybridCacheManager(l1CacheManager, l2CacheManager);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "petclinic.cache.hybrid.enabled", havingValue = "false", matchIfMissing = true)
    public CacheManager simpleCacheManager(CacheManager l1CacheManager) {
        return l1CacheManager;
    }
}