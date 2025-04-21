package com.meetime.hubspot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HubspotExceptionHandler {
	
	 // Generic exception handle
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGenericException(Exception e) {
    	
    	ErrorMessage error = new ErrorMessage(
    			"Unexpected error: " + e.getMessage(),
				 HttpStatus.INTERNAL_SERVER_ERROR.value()
		 );
    	
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Exception handling for HubSpot authorization error
    @ExceptionHandler(HubspotAuthorizationException.class)
    public ResponseEntity<ErrorMessage> handleHubspotAuthorizationException(HubspotAuthorizationException e) {
    	
    	ErrorMessage error = new ErrorMessage(
				 "Error connecting to HubSpot to generate URL: " + e.getMessage(),
				 HttpStatus.BAD_REQUEST.value()
		 );
    	
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    // Exception handling for the token getting callback process
    @ExceptionHandler(HubspotCallbackProcessException.class)
    public ResponseEntity<ErrorMessage> handleHubspotCallbackProcessException(HubspotCallbackProcessException e) {
    	
    	ErrorMessage error = new ErrorMessage(
				 "Error connecting to HubSpot to callback process: " + e.getMessage(),
				 HttpStatus.BAD_REQUEST.value()
		 );
    	
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    // Exception handling for contact creation
    @ExceptionHandler(HubspotContactException.class)
    public ResponseEntity<ErrorMessage> handleHubspotContactException(HubspotContactException e) {
    	
    	ErrorMessage error = new ErrorMessage(
				 "Error creating contact: " + e.getMessage(),
				 HttpStatus.UNPROCESSABLE_ENTITY.value()
		 );
    	
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    // Exception handling for receiving Webhooks
    @ExceptionHandler(HubspotWebhookException.class)
    public ResponseEntity<ErrorMessage> handleHubspotContactException(HubspotWebhookException e) {
    	
    	ErrorMessage error = new ErrorMessage(
				 "Error Webhook process: " + e.getMessage(),
				 HttpStatus.BAD_REQUEST.value()
		 );
    	
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
