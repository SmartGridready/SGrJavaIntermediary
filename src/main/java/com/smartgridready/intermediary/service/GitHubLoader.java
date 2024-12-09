/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartgridready.driver.api.http.HttpStatus;
import com.smartgridready.intermediary.exception.ExtIfXmlNotFoundException;

/**
 * Responsible to load resources from GitHub.
 */
public class GitHubLoader
{
    private static final Logger LOG = LoggerFactory.getLogger( GitHubLoader.class );
    
    private static final String EI_XML_BASE_URI =
            "https://raw.githubusercontent.com/SmartGridready/SGrSpecifications/refs/heads/master/XMLInstances/ExtInterfaces/";

    /**
     * Loads the given EI-XML from GitHub.
     * 
     * @param eiXmlname
     *        name of EI-XML
     * @return contents of EI-XML
     * @throws ExtIfXmlNotFoundException
     *         if the EI-XML cannot be loaded from GitHub
     */
    public String loadExternalInterface( String eiXmlname )
    {
        final var uri = EI_XML_BASE_URI + eiXmlname;
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder().uri( URI.create( uri ) ).build();
        
        LOG.info( "Loading EI-XML file for file name '{}' from URI '{}'", eiXmlname, uri );
        HttpResponse<String> response;

        try
        {
            response = client.send( request, HttpResponse.BodyHandlers.ofString() );
        }
        catch ( Exception e )
        {
            throw new ExtIfXmlNotFoundException( e );
        }
        
        if ( response.statusCode() != HttpStatus.OK )
        {
            throw new ExtIfXmlNotFoundException( "GitHub answered with status code != OK, message is '"
                                                 + response.body()
                                                 + "'" );
        }
        
        return response.body();
    }
}
