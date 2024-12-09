/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
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
        super( "Read/write failed for device '" + deviceName + "'.", cause );
    }

}
