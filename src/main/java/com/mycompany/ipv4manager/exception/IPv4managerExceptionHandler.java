package com.mycompany.ipv4manager.exception;

import java.util.Date;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;;
/**
 * 
 * @author mathi
 * An exception handler to avoid unnecessary errors as response.
 * It uses a standard IPv4managerExceptionResponse as responseEntity with approrpiate corresponding
 * Exception messages.
 * 
 * This could be optimized further.
 *
 */
@ControllerAdvice
@RestController
public class IPv4managerExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		IPv4ManagerExceptionResponse IPv4managerExceptionResponse = new IPv4ManagerExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<Object>(IPv4managerExceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(IPv4NotFoundException.class)
	public final ResponseEntity<Object> handleUserNotFoundException(IPv4NotFoundException ex, WebRequest request) {
		IPv4ManagerExceptionResponse IPv4managerExceptionResponse = new IPv4ManagerExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<Object>(IPv4managerExceptionResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(IPv4ConflictException.class)
	public final ResponseEntity<Object> handleUserNotFoundException(IPv4ConflictException ex, WebRequest request) {
		IPv4ManagerExceptionResponse IPv4managerExceptionResponse = new IPv4ManagerExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<Object>(IPv4managerExceptionResponse, HttpStatus.CONFLICT);
	}
	
	
	@ExceptionHandler(IllegalArgumentException.class)
	public final ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
		IPv4ManagerExceptionResponse IPv4managerExceptionResponse = new IPv4ManagerExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<Object>(IPv4managerExceptionResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public final ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
		IPv4ManagerExceptionResponse IPv4managerExceptionResponse = new IPv4ManagerExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<Object>(IPv4managerExceptionResponse, HttpStatus.BAD_REQUEST);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		IPv4ManagerExceptionResponse IPv4managerExceptionResponse = new IPv4ManagerExceptionResponse(new Date(), "Validation Failed",
				ex.getBindingResult().toString());
		return new ResponseEntity<Object>(IPv4managerExceptionResponse, HttpStatus.BAD_REQUEST);
	}	
	

}