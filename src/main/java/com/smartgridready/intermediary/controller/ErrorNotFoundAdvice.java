package com.smartgridready.intermediary.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.smartgridready.intermediary.exception.DeviceNotFoundException;
import com.smartgridready.intermediary.exception.DeviceOperationFailedException;
import com.smartgridready.intermediary.exception.ExtIfXmlNotFoundException;

@SuppressWarnings("unused")
@ControllerAdvice
class ErrorNotFoundAdvice {

	@ResponseBody
	@ExceptionHandler({ExtIfXmlNotFoundException.class, DeviceNotFoundException.class, DeviceOperationFailedException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String exceptionHandler(RuntimeException ex) {
		return ex.getMessage();
	}
}
