package com.smartgridready.intermediary.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.smartgridready.intermediary.exception.DeviceNotFoundException;
import com.smartgridready.intermediary.exception.DeviceOperationFailedException;
import com.smartgridready.intermediary.exception.ExtIfXmlNotFoundException;


/**
 * Exception handler for the runtime exceptions thrown by the service.
 */
@ControllerAdvice
class ErrorNotFoundAdvice
{
    private static final Logger LOG = LoggerFactory.getLogger( ErrorNotFoundAdvice.class );

    @ResponseBody
    @ExceptionHandler({ ExtIfXmlNotFoundException.class,
                        DeviceNotFoundException.class,
                        DeviceOperationFailedException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String exceptionHandler( RuntimeException ex )
    {
        LOG.debug( "Handling exception '{}' with message '{}'",
                   ex.getClass().getSimpleName(),
                   ex.getMessage() );
        return ex.getMessage();
    }
}
