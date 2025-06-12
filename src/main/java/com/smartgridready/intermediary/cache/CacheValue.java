package com.smartgridready.intermediary.cache;

import java.time.Instant;

import com.smartgridready.communicator.common.api.values.Value;

public class CacheValue {

    private final Value value;
    private final Instant timestamp;
    
    public CacheValue(Value value, Instant timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public static CacheValue of(Value value) {
        return new CacheValue(value, Instant.now());
    }

    public Value getValue() {
        return value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
