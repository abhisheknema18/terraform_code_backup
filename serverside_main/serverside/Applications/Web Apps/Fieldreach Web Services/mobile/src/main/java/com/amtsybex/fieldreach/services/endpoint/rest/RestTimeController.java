/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import org.springframework.http.ResponseEntity;

public interface RestTimeController {

	
	/**
	 * Get server time
	 * @return server time in format
	 * "EEE, dd MMM yyyy HH:mm:ss ZZZ"
	 */
	ResponseEntity<String> serverTime();
	
}
