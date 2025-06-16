package com.smartgridready.intermediary.helper;

import java.util.Map;
import java.util.EnumMap;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.smartgridready.communicator.common.api.values.*;


/**
 * Converts between SGr device values and JSON nodes.
 */
public class SGrValueConverter {

    private static Map<DataType, Function<Value, Value>> DATA_TYPE_DEVICE_MAP = new EnumMap<>(DataType.class);
    private static Map<DataType, Function<Value, JsonNode>> DATA_TYPE_INTERMEDIARY_MAP = new EnumMap<>(DataType.class);

    static {
        // conversion from intermediary --> device value
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

        // conversion from device --> intermediary value
        DATA_TYPE_INTERMEDIARY_MAP.put(DataType.STRING, SGrValueConverter::getStringAsJson);
        DATA_TYPE_INTERMEDIARY_MAP.put(DataType.ENUM, SGrValueConverter::getStringAsJson);
    }

    // hide constructor
    private SGrValueConverter() {}

    /**
     * Gets an Intermediary output value from an SGr device value.
     * @param dataType the data type to convert to
     * @param deviceValue the SGr device value
     * @return a JSON node
     */
    public static JsonNode getIntermediaryValue(DataType dataType, Value deviceValue) {
        if (dataType == null || deviceValue == null) {
            throw new IllegalArgumentException("Data type and value must not be null");
        }

        final Function<Value, JsonNode> converter = DATA_TYPE_INTERMEDIARY_MAP.getOrDefault(dataType, Value::getJson);
        return converter.apply(deviceValue);
    }

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

        final Function<Value, Value> converter = DATA_TYPE_DEVICE_MAP.getOrDefault(dataType, SGrValueConverter::getJson);
        return converter.apply(JsonValue.of(value));
    }

    private static Value getFloat32(Value value) {
        return Float32Value.of(value.getFloat32());
    }

    private static Value getFloat64(Value value) {
        return Float64Value.of(value.getFloat64());
    }

    private static Value getInt8(Value value) {
        return Int8Value.of(value.getInt8());
    }

    private static Value getInt8U(Value value) {
        return Int8UValue.of(value.getInt8U());
    }

    private static Value getInt16(Value value) {
        return Int16Value.of(value.getInt16());
    }

    private static Value getInt16U(Value value) {
        return Int16UValue.of(value.getInt16U());
    }

    private static Value getInt32(Value value) {
        return Int32Value.of(value.getInt32());
    }

    private static Value getInt32U(Value value) {
        return Int32UValue.of(value.getInt32U());
    }

    private static Value getInt64(Value value) {
        return Int64Value.of(value.getInt64());
    }

    private static Value getInt64U(Value value) {
        return Int64UValue.of(value.getInt64U());
    }

    private static Value getBoolean(Value value) {
        return BooleanValue.of(value.getBoolean());
    }

    private static Value getEnum(Value value) {
        return EnumValue.of(value.getEnum().getLiteral());
    }

    private static Value getBitmap(Value value) {
        return BitmapValue.of(value.getBitmap());
    }

    private static Value getString(Value value) {
        return StringValue.of(value.getString());
    }

    private static Value getDateTime(Value value) {
        return DateTimeValue.of(value.getDateTime());
    }

    private static Value getJson(Value value) {
        return JsonValue.of(value.getJson());
    }

    private static JsonNode getStringAsJson(Value value) {
        return TextNode.valueOf(value.getString());
    }
}
