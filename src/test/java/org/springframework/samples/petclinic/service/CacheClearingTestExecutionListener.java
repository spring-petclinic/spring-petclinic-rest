package org.springframework.samples.petclinic.service;

import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * Test execution listener that clears all caches before each test method
 * to ensure test isolation and prevent cached data from affecting test results.
 */
public class CacheClearingTestExecutionListener implements TestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        final CacheManager cacheManager;
        try {
            cacheManager = testContext.getApplicationContext().getBean(CacheManager.class);
        } catch (Exception e) {
            // Cache manager not available, skip cache clearing
            return;
        }

        if (cacheManager != null) {
            // Clear all caches before each test
            cacheManager.getCacheNames().forEach(cacheName -> {
                org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                }
            });
        }
    }
}