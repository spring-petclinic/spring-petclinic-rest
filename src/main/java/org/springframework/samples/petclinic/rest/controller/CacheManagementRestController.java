package org.springframework.samples.petclinic.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.cache.CacheMonitoringService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for cache monitoring and management
 */
@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api/cache")
public class CacheManagementRestController {

    private final CacheMonitoringService cacheMonitoringService;

    public CacheManagementRestController(CacheMonitoringService cacheMonitoringService) {
        this.cacheMonitoringService = cacheMonitoringService;
    }

    /**
     * Get cache statistics for all caches
     */
    @GetMapping(value = "/stats")
    public ResponseEntity<Map<String, CacheMonitoringService.CacheStatistics>> getCacheStatistics() {
        Map<String, CacheMonitoringService.CacheStatistics> statistics = cacheMonitoringService.getCacheStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    /**
     * Get size of a specific cache
     */
    @GetMapping(value = "/size/{cacheName}")
    public ResponseEntity<Long> getCacheSize(@PathVariable("cacheName") String cacheName) {
        long size = cacheMonitoringService.getCacheSize(cacheName);
        if (size >= 0) {
            return new ResponseEntity<>(size, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Clear all caches
     */
    @DeleteMapping(value = "/clear")
    public ResponseEntity<Void> clearAllCaches() {
        cacheMonitoringService.clearAllCaches();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Clear specific cache
     */
    @DeleteMapping(value = "/clear/{cacheName}")
    public ResponseEntity<Void> clearCache(@PathVariable("cacheName") String cacheName) {
        cacheMonitoringService.clearCache(cacheName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
