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
