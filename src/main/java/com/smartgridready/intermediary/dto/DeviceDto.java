/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.smartgridready.intermediary.entity.ConfigurationValue;


/**
 * DTO for device.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDto
{
    private String name;
    private String eiXmlName;
    private List<ConfigurationValue> configurationValues;
    private String status;
}
