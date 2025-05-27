package com.smartgridready.intermediary.helper;

import java.util.Map;
import java.util.EnumMap;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartgridready.communicator.common.api.values.*;


/**
 * Converts between SGr device values and JSON nodes.
 */
public class SGrValueConverter {

    private static Map<DataType, Function<JsonValue, Value>> DATA_TYPE_DEVICE_MAP = new EnumMap<>(DataType.class);

    static {
        // conversion from generic --> device value
        DATA_TYPE_DEVICE_MAP.put(DataType.STRING, SGrValueConverter::getString);
        DATA_TYPE_DEVICE_MAP.put(DataType.INT8, SGrValueConverter::getInt8);
        DATA_TYPE_DEVICE_MAP.put(DataType.INT8U, SGrValueConverter::getInt8U);
        DATA_TYPE_DEVICE_MAP.put(DataType.INT16, SGrValueConverter::getInt16);
        DATA_TYPE_DEVICE_MAP.put(DataType.INT16U, SGrValueConverter::getInt16U);
        DATA_TYPE_DEVICE_MAP.put(DataType.INT32, SGrValueConverter::getInt32);
        DATA_TYPE_DEVICE_MAP.put(DataType.INT32U, SGrValueConverter::getInt32U);
        DATA_TYPE_DEVICE_MAP.put(DataType.INT64, SGrValueConverter::getInt64);
        DATA_TYPE_DEVICE_MAP.put(DataType.INT64U, SGrValueConverter::getInt64U);
        DATA_TYPE_DEVICE_MAP.put(DataType.FLOAT32, SGrValueConverter::getFloat32);
        DATA_TYPE_DEVICE_MAP.put(DataType.FLOAT64, SGrValueConverter::getFloat64);
        DATA_TYPE_DEVICE_MAP.put(DataType.BOOLEAN, SGrValueConverter::getBoolean);
        DATA_TYPE_DEVICE_MAP.put(DataType.DATE_TIME, SGrValueConverter::getDateTime);
        DATA_TYPE_DEVICE_MAP.put(DataType.ENUM, SGrValueConverter::getEnum);
        DATA_TYPE_DEVICE_MAP.put(DataType.BITMAP, SGrValueConverter::getBitmap);
        DATA_TYPE_DEVICE_MAP.put(DataType.JSON, SGrValueConverter::getJson);
    }

    // hide constructor
    private SGrValueConverter() {}

    /**
     * Gets an SGr device value from a JSON node.
     * @param dataType the data type to convert to
     * @param value the JSON value
     * @return an SGr device Value
     */
    public static Value getDeviceValue(DataType dataType, JsonNode value) {
        if (dataType == null || value == null) {
            throw new IllegalArgumentException("Data type and value must not be null");
        }

        final Function<JsonValue, Value> converter = DATA_TYPE_DEVICE_MAP.getOrDefault(dataType, SGrValueConverter::getJson);
        return converter.apply(JsonValue.of(value));
    }

    private static Value getFloat32(JsonValue value) {
        return Float32Value.of(value.getFloat32());
    }

    private static Value getFloat64(JsonValue value) {
        return Float64Value.of(value.getFloat64());
    }

    private static Value getInt8(JsonValue value) {
        return Int8Value.of(value.getInt8());
    }

    private static Value getInt8U(JsonValue value) {
        return Int8UValue.of(value.getInt8U());
    }

    private static Value getInt16(JsonValue value) {
        return Int16Value.of(value.getInt16());
    }

    private static Value getInt16U(JsonValue value) {
        return Int16UValue.of(value.getInt16U());
    }

    private static Value getInt32(JsonValue value) {
        return Int32Value.of(value.getInt32());
    }

    private static Value getInt32U(JsonValue value) {
        return Int32UValue.of(value.getInt32U());
    }

    private static Value getInt64(JsonValue value) {
        return Int64Value.of(value.getInt64());
    }

    private static Value getInt64U(JsonValue value) {
        return Int64UValue.of(value.getInt64U());
    }

    private static Value getBoolean(JsonValue value) {
        return BooleanValue.of(value.getBoolean());
    }

    private static Value getEnum(JsonValue value) {
        return EnumValue.of(value.getEnum().getLiteral());
    }

    private static Value getBitmap(JsonValue value) {
        return BitmapValue.of(value.getBitmap());
    }

    private static Value getString(JsonValue value) {
        return StringValue.of(value.getString());
    }

    private static Value getDateTime(JsonValue value) {
        return DateTimeValue.of(value.getDateTime());
    }

    private static Value getJson(JsonValue value) {
        return value;
    }
}
