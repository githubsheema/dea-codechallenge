package com.mycompany.ipv4manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class IPv4ConflictException extends RuntimeException {
	public IPv4ConflictException(String message) {
		super(message);
	}
}
