package ch.smartgridready.intermediary;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class ErrorNotFoundAdvice {

	@ResponseBody
	@ExceptionHandler({ExtIfXmlNotFoundException.class, DeviceNotFoundException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String extIfNotFoundHandler(RuntimeException ex) {
		return ex.getMessage();
	}
}
