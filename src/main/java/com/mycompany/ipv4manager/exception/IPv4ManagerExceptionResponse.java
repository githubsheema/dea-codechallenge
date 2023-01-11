package com.mycompany.ipv4manager.exception;

import java.util.Date;

/*
 * A Generic Exception allows user
 * to expect a standard Fault response
 * 
 */
public class IPv4ManagerExceptionResponse {
	private Date timestamp;
	private String message;
	private String details;

	public IPv4ManagerExceptionResponse(Date timestamp, String message, String details) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getMessage() {
		return message;
	}

	public String getDetails() {
		return details;
	}

}