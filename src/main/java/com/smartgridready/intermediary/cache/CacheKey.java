package com.smartgridready.intermediary.cache;

import java.util.Objects;

public class CacheKey {

    private final String functionalProfileName;
    private final String dataPointName;
    
    public CacheKey(String functionalProfileName, String dataPointName) {
        this.functionalProfileName = functionalProfileName;
        this.dataPointName = dataPointName;
    }

    public static CacheKey of(String functionalProfileName, String dataPointName) {
        return new CacheKey(functionalProfileName, dataPointName);
    }

    public String getFunctionalProfileName() {
        return functionalProfileName;
    }

    public String getDataPointName() {
        return dataPointName;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", functionalProfileName, dataPointName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionalProfileName, dataPointName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (!(other instanceof CacheKey)) {
            return false;
        }
        return Objects.equals(this.functionalProfileName, ((CacheKey)other).functionalProfileName) &&
                Objects.equals(this.dataPointName, ((CacheKey)other).dataPointName);
    }
}
