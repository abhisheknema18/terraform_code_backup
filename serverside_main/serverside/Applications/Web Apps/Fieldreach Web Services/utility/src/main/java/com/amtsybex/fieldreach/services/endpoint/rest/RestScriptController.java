/**
 * Author:  T Murray
 * Date:    17/05/2016
 * Project: FDP1218
 * 
 * Copyright AMT-Sybex 2016
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import javax.servlet.http.HttpServletRequest;

import com.amtsybex.fieldreach.services.exception.BadRequestException;

public interface RestScriptController {

	// FDP1242 TM 25/08/2016 - Moved method from mobile web service
	// FDP1183 CM 08/02/2016
	/**
	 * Retrieve the script as a base64 encoded spreadsheet
	 * 
	 * @param request
	 * 				servlet request.
	 * 
	 * @param scriptId
	 * 				ScriptId of the script to be retrieved
	 * 
	 * @return GetScriptReportResponse message
	 *  
	 * @throws BadRequestException
	 * 				Specified id is not a valid number.    
	 */
	public String getScriptReport(HttpServletRequest request,
			int scriptId)
			throws BadRequestException;
	// FDP1183 CM End
}
