/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.controller;

import java.util.Map;
import java.util.Properties;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartgridready.intermediary.dto.DeviceInfoDto;
import com.smartgridready.intermediary.exception.DeviceOperationFailedException;
import com.smartgridready.intermediary.service.IntermediaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(
    name = "Device Communications Controller",
    description = "API to write/read values to/from a device data point and for introspection."
)
public class CommunicationsController
{
    private final IntermediaryService intermediaryService;

    public CommunicationsController( IntermediaryService intermediaryService )
    {
        this.intermediaryService = intermediaryService;
    }

    @Operation(
        summary = "Get device information.",
        description = "Get the device information for introspection.",
        parameters = {
            @Parameter(name = "device", in = ParameterIn.PATH, description = "The device name", required = true)
        }
    )
    @ApiResponse(
        description = "The device information.",
        responseCode = "200",
        content = @Content(
            schema = @Schema(implementation = DeviceInfoDto.class)
        )
    )
    @GetMapping(path = "/info/{device}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceInfoDto> getInfo( 
            @PathVariable("device")
            String device )
    {
        return ResponseEntity.ok(
            intermediaryService.getDeviceInfo( device )
        );
    }

    @Operation(
        summary = "Read a data point value.",
        description = "Read a value from a device data point.",
        parameters = {
            @Parameter(name = "device", in = ParameterIn.PATH, description = "The device name", required = true),
            @Parameter(name = "functionalProfile", in = ParameterIn.PATH, description = "The functional profile name", required = true),
            @Parameter(name = "dataPoint", in = ParameterIn.PATH, description = "The data point name", required = true),
            @Parameter(name = "queryParams", in = ParameterIn.QUERY, description = "The request-specific query parameters", explode = Explode.TRUE)
        }
    )
    @ApiResponse(
        description = "The value read from the device.",
        responseCode = "200",
        content = @Content(
            schema = @Schema(implementation = JsonNode.class)
        )
    )
    @GetMapping(path = "/value/{device}/{functionalProfile}/{dataPoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> getDataPointValue( 
            @PathVariable("device")
            String device,
            @PathVariable("functionalProfile")
            String functionalProfile,
            @PathVariable("dataPoint")
            String dataPoint,
            @RequestParam(defaultValue = "{}")
            Map<String, String> queryParams )
    {
        var properties = new Properties();

        // Spring does replace + with space by default. This leads to problems when using
        // RFC 3339 timestamps like 2024-01-01T00:00:00+02:00 in URL parameters.
        // To use encode a space in the URL use %20.
        if ( queryParams != null )
        {
            queryParams.forEach( ( key, val ) -> properties.put( key, val.replace( ' ', '+' ) ) );
        }

        JsonNode value = intermediaryService.getVal( device, functionalProfile, dataPoint, properties );
        if (value == null) {
            throw new DeviceOperationFailedException("Data point value was null");
        }

        return ResponseEntity.ok(value);
    }

    @Operation(
        summary = "Write a data point value.",
        description = "Write a value to the device data point.",
        parameters = {
            @Parameter(name = "device", in = ParameterIn.PATH, description = "The device name", required = true),
            @Parameter(name = "functionalProfile", in = ParameterIn.PATH, description = "The functional profile name", required = true),
            @Parameter(name = "dataPoint", in = ParameterIn.PATH, description = "The data point name", required = true)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The value must be a JSON value, which can be a simple value such as a string or a number, or an object or array.",
            required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = JsonNode.class))
        )
    )
    @ApiResponse(
        description = "Empty response",
        responseCode = "204"
    )
    @PostMapping(path = "/value/{device}/{functionalProfile}/{dataPoint}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> setDataPointValue( 
            @PathVariable("device")
            String device,
            @PathVariable("functionalProfile")
            String functionalProfile,
            @PathVariable("dataPoint")
            String dataPoint,
            @RequestBody
            JsonNode value )
    {
        if ( value == null )
        {
            throw new IllegalArgumentException( "need parameter value" );
        }

        intermediaryService.setVal( device, functionalProfile, dataPoint, value );

        return ResponseEntity.noContent().build();
    }
}
