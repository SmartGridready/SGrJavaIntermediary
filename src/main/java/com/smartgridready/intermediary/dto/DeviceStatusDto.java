/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.dto;

import java.util.List;

import com.smartgridready.intermediary.entity.ConfigurationValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO for device.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatusDto extends DeviceDto
{
    private String status;
    
    public DeviceStatusDto( String name, String eiXmlName, List<ConfigurationValue> configurationValues, String status )
    {
        super( name, eiXmlName, configurationValues );
        this.status = status;
    }
}
