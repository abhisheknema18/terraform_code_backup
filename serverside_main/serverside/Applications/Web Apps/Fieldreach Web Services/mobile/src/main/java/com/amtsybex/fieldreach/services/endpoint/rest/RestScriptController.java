/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2015
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Removal of /v1 namespace from web service end points 
 * and code re-factoring.
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.amtsybex.fieldreach.services.messages.response.GetScriptResponse;
import com.amtsybex.fieldreach.services.messages.response.ScriptListResponse;
import com.amtsybex.fieldreach.services.messages.response.ScriptQuestionDefinitionResponse;

public interface RestScriptController {

	/**
	 * Retrieve source for Script within Fieldreach
	 * 
	 * @param scriptId
	 *            ScriptId of the script to get the source of.
	 * 
	 * @return GetScriptResponse with base64 source or error message if script
	 *         not found
	 * 
	 * @throws Exception
	 *             if invalid Authorization token
	 */
	ResponseEntity<GetScriptResponse> getScriptSource(HttpHeaders httpHeaders, Integer scriptId) throws Exception;

	
	/* FDE034 CM 07/09/2015 */
	/**
	 * Retrieve script question definition response
	 * 
	 * @param httpHeaders
	 *            http Headers.
	 * 
	 * @param scriptId
	 *            ScriptID of the script of the Question definition to be
	 *            retrieved belongs to.
	 * 
	 * @param sequenceNumber
	 *            Sequence number of the question definition to be retrieved.
	 * 
	 * @return ScriptQuestionDefinitionResponse message
	 * 
	 * @
	 *             If invalid authorization token is passed.
	 *
	 */
	ResponseEntity<ScriptQuestionDefinitionResponse> getScriptQuestionDefinition(HttpHeaders httpHeaders, String returnId,
																				 int scriptId, int sequenceNumber);
	
	/**
	 * Retrieve a list of scripts available to the user referenced by the
	 * request. Usercode will be extracted from the authorisation header. The
	 * web service will then check for a script profile and will return all
	 * scripts in that users script profile. If no profile is found, all online
	 * scripts are returned.
	 * 
	 * @param httpHeaders
	 *            http Headers
	 * 
	 * @return A ScriptListResponse message.
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	ResponseEntity<ScriptListResponse> getScriptList(HttpHeaders httpHeaders) ;

}
