/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.smartgridready.intermediary.dto.DeviceDto;
import com.smartgridready.intermediary.service.IntermediaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "Device Controller", description = "API to manage devices.")
public class DeviceController
{
    private final IntermediaryService intermediaryService;

    public DeviceController( IntermediaryService intermediaryService )
    {
        this.intermediaryService = intermediaryService;
    }

    @Operation(description = "Get a list of all devices.")
    @ApiResponse(description = "A list with all devices with their device information.")
    @GetMapping("/device")
    List<DeviceDto> getAll()
    {
        var devices = intermediaryService.getAllDevices();
        return devices.stream()
                .map( device -> new DeviceDto( device.getName(),
                                               device.getEiXml().getName(),
                                               device.getConfigurationValues(),
                                               intermediaryService.getDeviceStatus( device.getName() ) ) )
                .toList();
    }

    @Operation(description = "Add a new or update an existing device.")
    @ApiResponse(description = "Device information of added/updated device")
    @PostMapping("/device")
    DeviceDto insertOrUpdateDevice( 
            @RequestBody 
            @Parameter(description = "Device description with device name, EI-XML name and configuration values.")
            DeviceDto deviceDto )
    {
        var device = intermediaryService.insertOrUpdateDevice( deviceDto.getName(),
                                                               deviceDto.getEiXmlName(),
                                                               deviceDto.getConfigurationValues() );
        return new DeviceDto( device.getName(),
                              device.getEiXml().getName(),
                              device.getConfigurationValues(),
                              intermediaryService.getDeviceStatus( device.getName() ) );
    }

    @Operation(description = "Get a device by name")
    @ApiResponse(description = "Device information")
    @GetMapping("/device/{name}")
    DeviceDto getDevice( 
            @PathVariable("name") 
            @Parameter(description = "The device name")
            String name )
    {
        var device = intermediaryService.getDevice( name );
        return new DeviceDto( device.getName(),
                              device.getEiXml().getName(),
                              device.getConfigurationValues(),
                              intermediaryService.getDeviceStatus( name ) );
    }

    @Operation(description = "Delete a device by name.")
    @DeleteMapping("/device/{name}")
    void deleteDevice( 
            @PathVariable("name") 
            @Parameter(description = "The device name")
            String name )
    {
        intermediaryService.deleteDevice( name );
    }
}
