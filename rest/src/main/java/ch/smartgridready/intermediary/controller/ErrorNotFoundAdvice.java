package ch.smartgridready.intermediary.controller;

import ch.smartgridready.intermediary.exception.DeviceNotFoundException;
import ch.smartgridready.intermediary.exception.DeviceOperationFailedException;
import ch.smartgridready.intermediary.exception.ExtIfXmlNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class ErrorNotFoundAdvice {

	@ResponseBody
	@ExceptionHandler({ExtIfXmlNotFoundException.class, DeviceNotFoundException.class, DeviceOperationFailedException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String exceptionHandler(RuntimeException ex) {
		return ex.getMessage();
	}
}
