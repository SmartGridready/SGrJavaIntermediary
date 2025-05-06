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

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * DTO for device.
 */
@Schema(description = "Represents device configuration.")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDto
{
    @Schema(description = "The device name")
    private String name;

    @Schema(description = "The EI-XML name")
    private String eiXmlName;

    @Schema(description = "The device configuration values")
    private List<ConfigurationValue> configurationValues;
}
