package com.smartgridready.intermediary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.smartgridready.intermediary.entity.ConfigurationValue;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDto {
    private String name;
    private String eiXmlName;
    private List<ConfigurationValue> configurationValues;
    private String status;
}
