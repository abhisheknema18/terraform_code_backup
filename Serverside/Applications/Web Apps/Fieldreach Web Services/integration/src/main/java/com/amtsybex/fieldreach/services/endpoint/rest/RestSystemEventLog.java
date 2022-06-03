package com.amtsybex.fieldreach.services.endpoint.rest;

import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.request.SystemEvent;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 * FDE044 - MC - Alow the logging of system exceptions to the system event log table
 * @author CroninM
 *
 */
public interface RestSystemEventLog {

	/**
	 * 
	 * @param httpHeaders
	 * @param systemEvent
	 * @return
	 * @throws BadRequestException
	 */
	ResponseEntity<CallResponse> addSystemEventLog(HttpHeaders httpHeaders, SystemEvent systemEvent) throws BadRequestException;
	
}
