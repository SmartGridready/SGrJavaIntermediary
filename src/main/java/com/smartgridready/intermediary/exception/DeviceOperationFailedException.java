package com.smartgridready.intermediary.exception;

/**
 * Exception for device operation failed.
 */
public class DeviceOperationFailedException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public DeviceOperationFailedException( String deviceName )
    {
        super( "Read/write failed for device '" + deviceName + "'." );
    }

    public DeviceOperationFailedException( String deviceName, Throwable cause )
    {
        super( "Read/write failed for device '" + deviceName
               + "'. Error: " + cause.getMessage() );
    }

}
