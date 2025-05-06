/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.smartgridready.intermediary.dto.DeviceDto;
import com.smartgridready.intermediary.dto.DeviceStatusDto;
import com.smartgridready.intermediary.service.IntermediaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(
    name = "Device Controller",
    description = "API to manage devices."
)
public class DeviceController
{
    private final IntermediaryService intermediaryService;

    public DeviceController( IntermediaryService intermediaryService )
    {
        this.intermediaryService = intermediaryService;
    }

    @Operation(
        summary = "Get all devices",
        description = "Get a list of all devices."
    )
    @ApiResponse(description = "A list with all devices with their device information.")
    @GetMapping(path = "/device", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DeviceStatusDto>> getAll()
    {
        var devices = intermediaryService.getAllDevices();
        return ResponseEntity.ok(
            devices
                .stream()
                .map( device -> new DeviceStatusDto( device.getName(),
                                                    device.getEiXml().getName(),
                                                    device.getConfigurationValues(),
                                                    intermediaryService
                                                            .getDeviceStatus( device.getName() ) ) )
                .toList()
        );
    }

    @Operation(
        summary = "Add or update device",
        description = "Add a new or update an existing device.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Device description with device name, EI-XML name and configuration values.",
            required = true,
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DeviceDto.class))}
        )
    )
    @ApiResponse(description = "Device information of added/updated device")
    @PostMapping(path = "/device", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceStatusDto> insertOrUpdateDevice( 
            @RequestBody 
            DeviceDto deviceDto )
    {
        var device = intermediaryService.insertOrUpdateDevice( deviceDto.getName(),
                                                               deviceDto.getEiXmlName(),
                                                               deviceDto.getConfigurationValues() );
        return ResponseEntity.ok(
            new DeviceStatusDto( device.getName(),
                                    device.getEiXml().getName(),
                                    device.getConfigurationValues(),
                                    intermediaryService.getDeviceStatus( device.getName() ))
        );
    }

    @Operation(
        summary = "Get device",
        description = "Get a device by name",
        parameters = {
            @Parameter(name = "name", in = ParameterIn.PATH, description = "The device name", required = true)
        }
    )
    @ApiResponse(description = "Device information")
    @GetMapping(path = "/device/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceStatusDto> getDevice( 
            @PathVariable("name")
            String name )
    {
        var device = intermediaryService.getDevice( name );
        return ResponseEntity.ok(
            new DeviceStatusDto( device.getName(),
                                    device.getEiXml().getName(),
                                    device.getConfigurationValues(),
                                    intermediaryService.getDeviceStatus( name ) )
        );
    }

    @Operation(
        summary = "Delete device",
        description = "Delete a device by name.",
        parameters = {
            @Parameter(name = "name", in = ParameterIn.PATH, description = "The device name", required = true)
        }
    )
    @DeleteMapping(path = "/device/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteDevice( 
            @PathVariable("name") 
            String name )
    {
        intermediaryService.deleteDevice( name );

        return ResponseEntity.noContent().build();
    }
}
