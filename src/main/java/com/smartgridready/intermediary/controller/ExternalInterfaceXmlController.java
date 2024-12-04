package com.smartgridready.intermediary.controller;

import java.io.IOException;
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
     description = "Allows the management of the external-interface-xml files (EI-XML)")
class ExternalInterfaceXmlController
{
    private final IntermediaryService intermediaryService;

    ExternalInterfaceXmlController( IntermediaryService intermediaryService )
    {
        this.intermediaryService = intermediaryService;
    }

    @Operation(description = "Used to add new EI-XML file")
    @PostMapping("/eiXml/{fileName}")
    @ApiResponse(description = "The EI-XML file name")
    ExternalInterfaceXml save( 
            @PathVariable("fileName")
            @Parameter(description = "The filename of the EI-XML")
            String fileName ) 
                    throws IOException, InterruptedException
    {
        return intermediaryService.saveEiXml( fileName );
    }

    @Operation(description = "Finds an EI-XML by name.")
    @ApiResponse(description = "The EI-XML file name and content")
    @GetMapping("/eiXml/{name}")
    ExternalInterfaceXml externalInterfaceXml( 
            @PathVariable("name")
            @Parameter(description = "The name of the EI-XML file")
            String fileName )
    {
        return intermediaryService.getEiXml( fileName );
    }

    @Operation(description = "Deletes an EI-XML")
    @DeleteMapping("/eiXml/{name}")
    void delete( 
            @PathVariable("name")
            @Parameter(description = "The EI-XML name")
            String name )
    {
        intermediaryService.deleteEiXml( name );
    }

    @Operation(description = "Get a list of all EI-XML")
    @ApiResponse(description = "A list of all EI-XMLs with their names and content")
    @GetMapping("/eiXml")
    List<ExternalInterfaceXml> getAll()
    {
        return intermediaryService.getAllEiXml();
    }
}
