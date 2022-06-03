/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Removal of /v1 namespace from web service end points 
 * and code re-factoring.
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import com.amtsybex.fieldreach.services.exception.AuthenticationException;
import com.amtsybex.fieldreach.services.messages.response.ConfigListResponse;
import com.amtsybex.fieldreach.services.messages.response.GetConfigResponse;


import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;


public interface RestConfigController {

	/**
	 * Retrieve source for Config file within Fieldreach
	 * 
	 * @param httpHeaders
	 *            HttpHeaders
	 * 
	 * @param configFileName
	 * 
	 * @return GetConfigResponse with base64 source or error message if script
	 *         not found
	 * 
	 * @throws Exception
	 *             if invalid Authorization token.
	 */
	ResponseEntity<GetConfigResponse> getConfigFile(HttpHeaders httpHeaders,
													String configFileName) throws Exception;

	/**
	 * Retrieve a list of config files available to the user referenced by the
	 * request. Usercode will be extracted from the authorisation header. The
	 * web service will then check for a all config files avialble ot that user
	 * and return them in a list.
	 * 
	 * @param httpHeaders
	 *            HttpHeaders
	 * 
	 * @return A ConfigListResponse message
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	ResponseEntity<ConfigListResponse> getConfigList( HttpHeaders httpHeaders) throws AuthenticationException
			;

}
