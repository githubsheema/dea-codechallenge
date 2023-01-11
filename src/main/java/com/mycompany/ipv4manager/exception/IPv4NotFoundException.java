package com.mycompany.ipv4manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class IPv4NotFoundException extends RuntimeException {
	public IPv4NotFoundException(String message) {
		super(message);
	}
}
