/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.controller;

import com.smartgridready.intermediary.dto.ErrorResponseDto;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.smartgridready.intermediary.exception.DeviceNotFoundException;
import com.smartgridready.intermediary.exception.DeviceOperationFailedException;
import com.smartgridready.intermediary.exception.ExtIfXmlNotFoundException;


/**
 * Exception handler for the runtime exceptions thrown by the service.
 */
@ControllerAdvice
class ErrorAdvice
{
    private static final String ERROR_MSG = "Handling exception '{}' with message '{}'";

    private static final Logger LOG = LoggerFactory.getLogger( ErrorAdvice.class );

    @ExceptionHandler({ ExtIfXmlNotFoundException.class,
                        DeviceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<ErrorResponseDto> exceptionHandler(RuntimeException ex )
    {
        return errorResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler({
            UnsupportedOperationException.class,
            IllegalArgumentException.class,
            IOException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ErrorResponseDto> invalidRequestHandler( RuntimeException ex )
    {
        return errorResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler({ DeviceOperationFailedException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ResponseEntity<ErrorResponseDto> runTimeExceptionHandler( RuntimeException ex )
    {
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private static ResponseEntity<ErrorResponseDto> errorResponse(HttpStatus httpStatus, RuntimeException ex) {
        LOG.debug( ERROR_MSG,
                ex.getClass().getSimpleName(),
                ex.getMessage() );
        return  ResponseEntity
                .status(httpStatus)
                .body(new ErrorResponseDto(ex.getClass().getSimpleName(), ex.getMessage()));
    }
}
