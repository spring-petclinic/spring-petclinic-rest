package org.springframework.samples.petclinic.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service to monitor cache performance and provide insights
 */
@Service
public class CacheMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(CacheMonitoringService.class);
    private final CacheManager cacheManager;

    public CacheMonitoringService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Log cache statistics every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void logCacheStatistics() {
        Map<String, CacheStatistics> stats = getCacheStatistics();

        if (stats.isEmpty()) {
            return;
        }

        logger.info("=== Cache Performance Statistics ===");
        stats.forEach((cacheName, stat) -> {
            logger.info("Cache: {} | Hits: {} | Misses: {} | Hit Rate: {:.2f}% | Evictions: {} | Size: {}",
                cacheName,
                stat.hitCount,
                stat.missCount,
                stat.hitRate * 100,
                stat.evictionCount,
                stat.estimatedSize);
        });
        logger.info("=====================================");
    }

    /**
     * Get comprehensive cache statistics
     */
    public Map<String, CacheStatistics> getCacheStatistics() {
        Map<String, CacheStatistics> statistics = new HashMap<>();

        cacheManager.getCacheNames().forEach(cacheName -> {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache instanceof CaffeineCache) {
                CaffeineCache caffeineCache = (CaffeineCache) cache;
                Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                CacheStats stats = nativeCache.stats();

                statistics.put(cacheName, new CacheStatistics(
                    stats.hitCount(),
                    stats.missCount(),
                    stats.hitRate(),
                    stats.evictionCount(),
                    nativeCache.estimatedSize()
                ));
            }
        });

        return statistics;
    }

    /**
     * Clear all caches (useful for testing or manual cache refresh)
     */
    public void clearAllCaches() {
        logger.info("Clearing all caches");
        cacheManager.getCacheNames().forEach(cacheName -> {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                logger.debug("Cleared cache: {}", cacheName);
            }
        });
    }

    /**
     * Clear specific cache
     */
    public void clearCache(String cacheName) {
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            logger.info("Cleared cache: {}", cacheName);
        } else {
            logger.warn("Cache not found: {}", cacheName);
        }
    }

    /**
     * Get cache size for a specific cache
     */
    public long getCacheSize(String cacheName) {
        org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
        if (cache instanceof CaffeineCache) {
            CaffeineCache caffeineCache = (CaffeineCache) cache;
            return caffeineCache.getNativeCache().estimatedSize();
        }
        return -1;
    }

    /**
     * Data class to hold cache statistics
     */
    public static class CacheStatistics {
        public final long hitCount;
        public final long missCount;
        public final double hitRate;
        public final long evictionCount;
        public final long estimatedSize;

        public CacheStatistics(long hitCount, long missCount, double hitRate, long evictionCount, long estimatedSize) {
            this.hitCount = hitCount;
            this.missCount = missCount;
            this.hitRate = hitRate;
            this.evictionCount = evictionCount;
            this.estimatedSize = estimatedSize;
        }
    }
}
