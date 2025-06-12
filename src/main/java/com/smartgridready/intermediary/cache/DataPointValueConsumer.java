package com.smartgridready.intermediary.cache;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartgridready.communicator.common.api.values.Value;

import io.vavr.control.Either;

public class DataPointValueConsumer implements Consumer<Either<Throwable, Value>> {

    private static final Logger LOG = LoggerFactory.getLogger(DataPointValueConsumer.class);

    private final CacheKey key;
    private final DataPointValueCache cache;

    public DataPointValueConsumer(CacheKey key, DataPointValueCache cache) {
        this.key = key;
        this.cache = cache;
    }

    @Override
    public void accept(Either<Throwable, Value> data) {
        if (data.isRight()) {
            cache.put(key, data.get());
        } else {
            LOG.error("Received error from {}: {}", key, data.getLeft().getMessage());
        }
    }

    public static DataPointValueConsumer of(CacheKey key, DataPointValueCache cache) {
        return new DataPointValueConsumer(key, cache);
    }

    public static DataPointValueConsumer of(String functionalProfileName, String dataPointName, DataPointValueCache cache) {
        return of(CacheKey.of(functionalProfileName, dataPointName), cache);
    }
}
