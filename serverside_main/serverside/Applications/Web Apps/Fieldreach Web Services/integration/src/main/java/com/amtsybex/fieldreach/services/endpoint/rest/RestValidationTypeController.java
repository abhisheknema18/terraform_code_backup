/**
 * Author:  T Goodwin
 * Date:    29/05/2012
 * Project: FDE018
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import com.amtsybex.fieldreach.services.messages.response.ValidationTypeResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public interface RestValidationTypeController {

	/**
	 * Request the validation type list associated with the validation types requested
	 * 
	 * @param validationTypeRequestList
	 *            - contains validation type request Message with list of validation types
	 *              to retrieve
	 * @return ValidationTypeResponse Message
	 * @throws Exception
	 *             if invalid Authorization token
	 */
	ResponseEntity<ValidationTypeResponse> getValidationTypeList(HttpHeaders httpHeaders,
                                                                 String validationTypeRequestList) throws Exception;


}
