package com.smartgridready.intermediary.helper;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

import com.smartgridready.communicator.common.api.dto.ConfigurationValue;
import com.smartgridready.communicator.common.api.values.DataType;
import com.smartgridready.communicator.common.api.values.DataTypeInfo;
import com.smartgridready.communicator.common.api.values.EnumValue;
import com.smartgridready.communicator.common.api.values.Value;
import com.smartgridready.ns.v0.ConfigurationListElement;
import com.smartgridready.ns.v0.DeviceFrame;
import com.smartgridready.ns.v0.Language;
import com.smartgridready.ns.v0.VersionNumber;

public class EidHelper {
    
    // hide constructor
    private EidHelper() {}

    public static String getVersionString(VersionNumber v) {
        return String.format("%d.%d.%d", v.getPrimaryVersionNumber(), v.getSecondaryVersionNumber(), v.getSubReleaseVersionNumber());
    }

    public static List<ConfigurationValue> getConfigurationParameters(DeviceFrame deviceFrame) {
        List<ConfigurationListElement> list = (deviceFrame.getConfigurationList() != null)
            ? deviceFrame.getConfigurationList().getConfigurationListElement()
            : Collections.emptyList();
        return list.stream().map(EidHelper::mapToConfigurationValue).collect(Collectors.toList());
    }

    private static ConfigurationValue mapToConfigurationValue(ConfigurationListElement configurationListElement) {
        EnumMap<Language, String> descriptions = new EnumMap<>(Language.class);
        configurationListElement.getConfigurationDescription().forEach((description) -> {
            descriptions.put(description.getLanguage(), description.getTextElement());
        });
        Value v = configurationListElement.getDefaultValue() != null ? (configurationListElement.getDataType().getEnum() != null ? EnumValue.of(configurationListElement.getDefaultValue()) : Value.fromString(configurationListElement.getDataType(), configurationListElement.getDefaultValue())) : null;
        return new ConfigurationValue(configurationListElement.getName(), (Value)v, (DataTypeInfo)DataType.getDataTypeInfo(configurationListElement.getDataType()).orElse((DataTypeInfo)null), descriptions);
   }
}
