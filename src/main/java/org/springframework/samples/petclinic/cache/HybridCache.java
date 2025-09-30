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
package org.springframework.samples.petclinic.cache;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * Hybrid cache implementation combining L1 (local) and L2 (distributed) caches.
 * Implements read-through and write-through patterns.
 */
public class HybridCache implements Cache {

    private final String name;
    private final Cache l1Cache;
    private final Cache l2Cache;

    public HybridCache(String name, Cache l1Cache, Cache l2Cache) {
        this.name = name;
        this.l1Cache = l1Cache;
        this.l2Cache = l2Cache;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        // Try L1 first
        ValueWrapper value = l1Cache.get(key);
        if (value != null) {
            return value;
        }

        // Fallback to L2
        value = l2Cache.get(key);
        if (value != null) {
            // Populate L1 with value from L2
            l1Cache.put(key, value.get());
        }
        return value;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        // Try L1 first
        T value = l1Cache.get(key, type);
        if (value != null) {
            return value;
        }

        // Fallback to L2
        value = l2Cache.get(key, type);
        if (value != null) {
            // Populate L1 with value from L2
            l1Cache.put(key, value);
        }
        return value;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        // Try L1 first
        T value = l1Cache.get(key, (Class<T>) null);
        if (value != null) {
            return value;
        }

        // Try L2
        value = l2Cache.get(key, (Class<T>) null);
        if (value != null) {
            // Populate L1
            l1Cache.put(key, value);
            return value;
        }

        // Load value if not in either cache
        try {
            value = valueLoader.call();
            if (value != null) {
                put(key, value);
            }
            return value;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        // Write-through: write to both caches
        l1Cache.put(key, value);
        l2Cache.put(key, value);
    }

    @Override
    public void evict(Object key) {
        // Evict from both caches
        l1Cache.evict(key);
        l2Cache.evict(key);
    }

    @Override
    public void clear() {
        // Clear both caches
        l1Cache.clear();
        l2Cache.clear();
    }
}