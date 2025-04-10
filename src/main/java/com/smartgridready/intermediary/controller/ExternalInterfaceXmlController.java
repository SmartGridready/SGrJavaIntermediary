/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartgridready.intermediary.entity.ExternalInterfaceXml;
import com.smartgridready.intermediary.service.IntermediaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;


@SuppressWarnings("unused")
@RestController
@Tag(name = "External-Interface-XML Controller",
     description = "API to manage the XML files of the external interfaces (EI-XML).")
public class ExternalInterfaceXmlController
{
    private static final String SGR_GITHUB_EI_XML_BASE_URI =
            "https://raw.githubusercontent.com/SmartGridready/SGrSpecifications/refs/heads/master/XMLInstances/ExtInterfaces/";

    private static final String SGR_LIBRARY_EI_XML_BASE_URI = "https://library.smartgridready.ch/prodx/";


    private final IntermediaryService intermediaryService;

    ExternalInterfaceXmlController( IntermediaryService intermediaryService )
    {
        this.intermediaryService = intermediaryService;
    }

    @Operation(description = "Get a list of all EI-XMLs.")
    @ApiResponse(description = "A list of all EI-XMLs with their names and content")
    @GetMapping("/eiXml")
    List<ExternalInterfaceXml> getAll()
    {
        return intermediaryService.getAllEiXml();
    }


    @Operation(description = "Add a EI-XML hosted on GitHub.")
    @ApiResponse(description = "The EI-XML name and its content")
    @PostMapping("/eiXml/sgr-github")
    ExternalInterfaceXml loadFromGithub(
            @Parameter(description = "The name of the EI-XML") @RequestParam String eiXmlName)
    {
        return intermediaryService.loadEiXmlFromUri( eiXmlName, SGR_GITHUB_EI_XML_BASE_URI + eiXmlName );
    }

    @Operation(description = "Add an EI-XML from the official SGr EI-XML library")
    @ApiResponse(description = "The EI-XML name and its content")
    @PostMapping("/eiXml/sgr-library")
    ExternalInterfaceXml loadFromSgrLibrary(
            @Parameter(description = "The name of the EI-XML") @RequestParam String eiXmlName)
    {
        return intermediaryService.loadEiXmlFromUri( eiXmlName, SGR_LIBRARY_EI_XML_BASE_URI + eiXmlName);
    }

    @Operation(description = "Add an EI-XML from a Web resource URL")
    @ApiResponse(description = "The EI-XML name and its content")
    @PostMapping("/eiXml/web-resource")
    ExternalInterfaceXml loadFromWebResource(
            @Parameter(description = "The EI-XML name") @RequestParam String eiXmlName,
            @Parameter(description = "The URL of the Web resource.") @RequestParam String uri)

    {
       return intermediaryService.loadEiXmlFromUri(eiXmlName, uri);
    }

    @Operation(description = "Add an EI-XML from a local file")
    @ApiResponse(description = "The EI-XML name and its content")
    @PostMapping(path = "/eiXml/local-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ExternalInterfaceXml loadFromLocalFile(
            @RequestParam("file") MultipartFile file) throws IOException
    {
       return intermediaryService.loadEiXmlFromLocalFile(file.getOriginalFilename(), file.getInputStream());
    }

    @Operation(description = "Get an EI-XML by name.")
    @ApiResponse(description = "The EI-XML name and its content")
    @GetMapping("/eiXml/{name}")
    ExternalInterfaceXml externalInterfaceXml( 
            @PathVariable("name")
            @Parameter(description = "The EI-XML name")
            String fileName )
    {
        return intermediaryService.getEiXml( fileName );
    }

    @Operation(description = "Deletes an EI-XML by name")
    @DeleteMapping("/eiXml/{name}")
    void delete( 
            @PathVariable("name")
            @Parameter(description = "The EI-XML name")
            String name )
    {
        intermediaryService.deleteEiXml( name );
    }
}
