/**
 * Author:  T Goodwin
 * Date:    29/05/2012
 * Project: FDE018
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import com.amtsybex.fieldreach.services.messages.response.GetScriptReferenceResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public interface RestReferenceOnlyFileController {

	/**
	 * Retrieve source for Reference only files within Fieldreach
	 * 
	 * @param referenceOnlyFileName
	 * Name of the reference only file to be retrieved.
	 * 
	 * @return GetScriptReferenceResponse with base64 source or error message if reference only file
	 *         not found
	 * @throws Exception
	 *             if invalid Authorization token
	 */
	ResponseEntity<GetScriptReferenceResponse> getReferenceOnlyFile(HttpHeaders httpHeaders, String referenceOnlyFileName)
			throws Exception;

	
}
