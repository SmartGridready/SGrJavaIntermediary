package com.smartgridready.intermediary.controller;

import java.util.Map;
import java.util.Properties;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartgridready.communicator.common.api.values.BooleanValue;
import com.smartgridready.communicator.common.api.values.Float64Value;
import com.smartgridready.communicator.common.api.values.StringValue;
import com.smartgridready.communicator.common.api.values.Value;
import com.smartgridready.intermediary.dto.ValueDto;
import com.smartgridready.intermediary.service.IntermediaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "Device Communications Controller",
     description = "API to write/read values to/from a device data point.")
public class CommunicationsController
{
    private final IntermediaryService intermediaryService;

    CommunicationsController( IntermediaryService intermediaryService )
    {
        this.intermediaryService = intermediaryService;
    }

    @Operation(description = "Read a value from a device data point.")
    @ApiResponse(description = "The value read from the device.")
    @GetMapping("/value/{device}/{functionalProfile}/{dataPoint}")
    public String getVal( 
            @PathVariable("device")
            @Parameter(description = "The device name")
            String device,
            @PathVariable("functionalProfile")
            @Parameter(description = "The functional profile name")
            String functionalProfile,
            @PathVariable("dataPoint")
            @Parameter(description = "The data point name")
            String dataPoint,
            @RequestParam(required = false)
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

        return intermediaryService.getVal( device, functionalProfile, dataPoint, properties ).getString();
    }

    @Operation(description = "Write a value to the device data point.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The value to be written to the device, see ValueDto.")
    @PostMapping("/value/{device}/{functionalProfile}/{dataPoint}")
    public void setVal( 
            @PathVariable("device")
            @Parameter(description = "The device name")
            String device,
            @PathVariable("functionalProfile")
            @Parameter(description = "The functional profile name")
            String functionalProfile,
            @PathVariable("dataPoint")
            @Parameter(description = "The data point name")
            String dataPoint,
            @RequestBody
            @Parameter(description = "The value must be a Number, a Boolean or a String (which could contain JSON).")
            ValueDto value )
    {
        Value sgrValue;

        if ( ( value == null ) || ( value.getValue() == null ) )
        {
            throw new IllegalArgumentException( "need parameter value" );
        }
        
        if ( value.getValue() instanceof Number )
        { 
            // convert the number to the biggest number, SGr knows: let the concrete implementation convert this again
            sgrValue = Float64Value.of( ( Double ) value.getValue() );
        }
        else
        if ( value.getValue() instanceof Boolean b )
        {
            sgrValue = BooleanValue.of( b );
        }
        else
        if ( value.getValue() instanceof String s )
        {
            sgrValue = StringValue.of( s );
        }
        else
        {
            throw new IllegalArgumentException( "parameter value has invalid type: "
                                                + value.getValue().getClass().getSimpleName() );
        }

        intermediaryService.setVal( device, functionalProfile, dataPoint, sgrValue );
    }
}
