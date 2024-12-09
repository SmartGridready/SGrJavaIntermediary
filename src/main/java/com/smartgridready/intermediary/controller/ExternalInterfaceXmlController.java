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
import org.springframework.web.bind.annotation.RestController;

import com.smartgridready.intermediary.entity.ExternalInterfaceXml;
import com.smartgridready.intermediary.service.IntermediaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "External-Interface-XML Controller",
     description = "API to manage the XML files of the external interfaces (EI-XML).")
class ExternalInterfaceXmlController
{
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
    @PostMapping("/eiXml/{name}")
    ExternalInterfaceXml save( 
            @PathVariable("name")
            @Parameter(description = "The EI-XML name")
            String name )
    {
        return intermediaryService.saveEiXml( name );
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
