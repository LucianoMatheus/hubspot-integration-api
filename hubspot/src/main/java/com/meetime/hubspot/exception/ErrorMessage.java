package com.meetime.hubspot.exception;

import java.time.LocalDateTime;

public class ErrorMessage {
	
	private String message;
	private int status;
	private LocalDateTime date;
	
	public ErrorMessage(String message, int status) {
		this.message = message;
		this.status = status;
		this.date = LocalDateTime.now();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	
		

}
