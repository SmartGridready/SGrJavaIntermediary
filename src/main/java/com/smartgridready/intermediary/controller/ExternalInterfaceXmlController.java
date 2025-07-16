/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.smartgridready.intermediary.dto.DeviceStatusDto;
import com.smartgridready.intermediary.dto.EidInfoDto;
import com.smartgridready.intermediary.entity.ExternalInterfaceXml;
import com.smartgridready.intermediary.service.IntermediaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;


@SuppressWarnings("unused")
@RestController
@Tag(
    name = "External-Interface-XML Controller",
    description = "API to manage the XML files of the external interfaces (EI-XML)."
)
public class ExternalInterfaceXmlController
{
    private static final String SGR_GITHUB_EI_XML_BASE_URI =
            "https://raw.githubusercontent.com/SmartGridready/SGrSpecifications/refs/heads/master/XMLInstances/ExtInterfaces/";

    private static final String SGR_LIBRARY_EI_XML_BASE_URI = "https://library.smartgridready.ch/prodx/";

    private final IntermediaryService intermediaryService;

    public ExternalInterfaceXmlController( IntermediaryService intermediaryService )
    {
        this.intermediaryService = intermediaryService;
    }

    @Operation(
        summary = "Get all EI-XMLs",
        description = "Get a list of all EI-XMLs."
    )
    @ApiResponse(
        description = "A list of all EI-XMLs with their names and content",
        responseCode = "200",
        content = @Content(
            array = @ArraySchema(schema = @Schema(implementation = ExternalInterfaceXml.class))
        )
    )
    @GetMapping(path = "/eiXml", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExternalInterfaceXml>> getAllExternalInterfacesXml()
    {
        return ResponseEntity.ok(
            intermediaryService.getAllEiXml()
        );
    }

    @Operation(
        summary = "Add EI-XML from Github",
        description = "Add a EI-XML hosted on GitHub.",
        parameters = {
            @Parameter(name = "eiXmlName", in = ParameterIn.QUERY, description = "The name of the EI-XML", required = true)
        }
    )
    @ApiResponse(
        description = "The EI-XML name and its content",
        responseCode = "200",
        content = @Content(
            schema = @Schema(implementation = ExternalInterfaceXml.class)
        )
    )
    @PostMapping(path = "/eiXml/sgr-github", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExternalInterfaceXml> loadFromGithub(
            @RequestParam("eiXmlName")
            String eiXmlName)
    {
        return ResponseEntity.ok(
            intermediaryService.loadEiXmlFromUri( eiXmlName, SGR_GITHUB_EI_XML_BASE_URI + eiXmlName )
        );
    }

    @Operation(
        summary = "Add EI-XML from library",
        description = "Add an EI-XML from the official SGr EI-XML library",
        parameters = {
            @Parameter(name = "eiXmlName", in = ParameterIn.QUERY, description = "The name of the EI-XML", required = true)
        }
    )
    @ApiResponse(
        description = "The EI-XML name and its content",
        responseCode = "200",
        content = @Content(
            schema = @Schema(implementation = ExternalInterfaceXml.class)
        )
    )
    @PostMapping(path = "/eiXml/sgr-library", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExternalInterfaceXml> loadFromSgrLibrary(
            @RequestParam("eiXmlName")
            String eiXmlName)
    {
        return ResponseEntity.ok(
            intermediaryService.loadEiXmlFromUri( eiXmlName, SGR_LIBRARY_EI_XML_BASE_URI + eiXmlName)
        );
    }

    @Operation(
        summary = "Add EI-XML from URL",
        description = "Add an EI-XML from a Web resource URL",
        parameters = {
            @Parameter(name = "eiXmlName", in = ParameterIn.QUERY, description = "The name of the EI-XML", required = true),
            @Parameter(name = "uri", in = ParameterIn.QUERY, description = "The URL of the Web resource", required = true),
        }
    )
    @ApiResponse(
        description = "The EI-XML name and its content",
        responseCode = "200",
        content = @Content(
            schema = @Schema(implementation = ExternalInterfaceXml.class)
        )
    )
    @PostMapping(path = "/eiXml/web-resource", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExternalInterfaceXml> loadFromWebResource(
            @RequestParam("eiXmlName")
            String eiXmlName,
            @RequestParam("uri")
            String uri)

    {
        return ResponseEntity.ok(
            intermediaryService.loadEiXmlFromUri(eiXmlName, uri)
        );
    }

    @Operation(
        summary = "Add EI-XML from file",    
        description = "Add an EI-XML from a local file",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "the EI-XML file",
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        )
    )
    @ApiResponse(
        description = "The EI-XML name and its content",
        responseCode = "200",
        content = @Content(
            schema = @Schema(implementation = ExternalInterfaceXml.class)
        )
    )
    @PostMapping(path = "/eiXml/local-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExternalInterfaceXml> loadFromLocalFile(
            @RequestPart("file")
            MultipartFile file) throws IOException
    {
        return ResponseEntity.ok(
            intermediaryService.loadEiXmlFromLocalFile(file.getOriginalFilename(), file.getInputStream())
        );
    }

    @Operation(
        summary = "Get an EI-XML",
        description = "Get an EI-XML by name.",
        parameters = {
            @Parameter(name = "name", in = ParameterIn.PATH, description = "The EI-XML name", required = true)
        }
    )
    @ApiResponse(
        description = "The EI-XML name and its content",
        responseCode = "200",
        content = @Content(
            schema = @Schema(implementation = ExternalInterfaceXml.class)
        )
    )
    @GetMapping(path = "/eiXml/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExternalInterfaceXml> getExternalInterfaceXml( 
            @PathVariable("name")
            String fileName )
    {
        return ResponseEntity.ok(
            intermediaryService.getEiXml( fileName )
        );
    }

    @Operation(
        summary = "Delete an EI-XML",
        description = "Deletes an EI-XML by name",
        parameters = {
            @Parameter(name = "name", in = ParameterIn.PATH, description = "The EI-XML name", required = true)
        }
    )
    @ApiResponse(
        description = "Empty response",
        responseCode = "204"
    )
    @DeleteMapping(path = "/eiXml/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteExternalInterfaceXml( 
            @PathVariable("name")
            String name )
    {
        intermediaryService.deleteEiXml( name );

        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get EI-XML info",
        description = "Get an EI-XML by name and returns its configuration info.",
        parameters = {
            @Parameter(name = "name", in = ParameterIn.PATH, description = "The EI-XML name", required = true)
        }
    )
    @ApiResponse(
        description = "The EI-XML info",
        responseCode = "200",
        content = @Content(
            schema = @Schema(implementation = EidInfoDto.class)
        )
    )
    @GetMapping(path = "/eiXml/info/{name}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EidInfoDto> getExternalInterfaceXmlInfo( 
            @PathVariable("name")
            String fileName )
    {
        return ResponseEntity.ok(
            intermediaryService.getEiXmlInfo( fileName )
        );
    }
}
