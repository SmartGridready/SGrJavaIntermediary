package com.smartgridready.intermediary.cache;

import java.util.HashMap;
import java.util.Map;

import com.smartgridready.communicator.common.api.values.Value;

public class DataPointValueCache
{
    private final Map<CacheKey, CacheValue> cache;

    public DataPointValueCache() {
        this.cache = new HashMap<>();
    }

    public void clear() {
        cache.clear();
    }

    public CacheValue get(CacheKey key) {
        return cache.get(key);
    }

    public CacheValue get(String functionalProfileName, String dataPointName) {
        return get(CacheKey.of(functionalProfileName, dataPointName));
    }

    public CacheValue put(CacheKey key, CacheValue value) {
        return cache.put(key, value);
    }

    public CacheValue put(CacheKey key, Value value) {
        return put(key, CacheValue.of(value));
    }

    public CacheValue put(String functionalProfileName, String dataPointName, Value value) {
        return put(CacheKey.of(functionalProfileName, dataPointName), CacheValue.of(value));
    }

    public static DataPointValueCache of() {
        return new DataPointValueCache();
    }
}
