package com.smartgridready.intermediary.exception;

/**
 * Exception for device not found.
 */
public class DeviceNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public DeviceNotFoundException( String deviceName )
    {
        super( "Could not find device '" + deviceName + "'" );
    }

}
