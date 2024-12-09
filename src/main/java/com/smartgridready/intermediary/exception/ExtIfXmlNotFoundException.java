/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.exception;

/**
 * Exception for EI-XML not found.
 */
public class ExtIfXmlNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public ExtIfXmlNotFoundException( String name )
    {
        super( "Could not find externalInterfaceXml: " + name );
    }
    
    public ExtIfXmlNotFoundException( Throwable cause )
    {
        super( cause );
    }
}
