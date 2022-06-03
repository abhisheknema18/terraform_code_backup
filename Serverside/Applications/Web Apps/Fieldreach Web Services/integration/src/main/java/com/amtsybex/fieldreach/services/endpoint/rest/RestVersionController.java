/**
 * Author:  T Murray
 * Date:    05/11/2013
 * Project: FDP997
 * 
 * Copyright AMT-Sybex 2013
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import org.springframework.http.ResponseEntity;

public interface RestVersionController {

	
	/**
	 * Get the version number of the web services.
	 * 
	 * @return
	 */
	ResponseEntity<String> versionNo();
	
}
