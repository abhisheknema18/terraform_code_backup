/**
 * Author:  T Murray
 * Date:    17/05/2016
 * Project: FDP1218
 * 
 * Copyright AMT-Sybex 2016
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.ScriptDefinitionNotFoundException;
import com.amtsybex.fieldreach.extract.core.ExtractEngine;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.endpoint.rest.RestScriptController;
import com.amtsybex.fieldreach.services.messages.response.GetScriptReportResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

@Controller
public class RestScriptControllerImpl extends BaseControllerImpl implements RestScriptController {

	private static Logger log = Logger.getLogger(RestScriptControllerImpl.class.getName());
	
	@Autowired
	private ExtractEngine fieldreachExtractEngine;

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/
	
	// FDP1242 TM 25/08/2016 - Moved endpoint from mobile web services.
	
	/*
	 * FDP1183 CM 08/02/2016 (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestScriptController#
	 * getScriptReport(javax.servlet.http.HttpServletRequest,
	 * int)
	 */
	@RequestMapping(value = "/script/{id}/exceldefreport", method = RequestMethod.GET)
	@ResponseBody
	@Override
	public String getScriptReport(HttpServletRequest request,
			@PathVariable("id") int scriptId)
			throws BadRequestException {
		
		if (log.isDebugEnabled())
			log.debug(">>> getScriptReport scriptId=" + scriptId);
		
		
		// Initialise response
		GetScriptReportResponse responseObject = new GetScriptReportResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		responseObject.setError(errorMessage);
		String response = null;
		
		String applicationIdentifier = Utils.extractApplicationIdentifier(request);
		
		try{
			
			Utils.debugHeaders(log, request);

			
			// Use the Fieldreach Extract Engine to extract the script result
			
			String scriptReport = Common.encodeBase64(fieldreachExtractEngine.getScriptReport(applicationIdentifier, scriptId));
			
	  	    responseObject.setScriptReportSource(scriptReport);

			// Decode the Base64 data and create a checksum
	  	    responseObject
					.setChecksum(Common.generateMD5Checksum(Common.decodeBase64(scriptReport)));


		} catch (ScriptDefinitionNotFoundException e) {

			log.error(e.getMessage());
			errorMessage.setErrorCode(Utils.SCRIPT_DEF_NOT_FOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception ex) {

			log.error("Exception in getScriptReport " + ex.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(ex.getMessage());

		} finally {

			responseObject.setSuccess(errorMessage.getErrorCode() == null);
			response = super.buildResponseString(responseObject);
		}

		
		log.debug("<<< getScriptReport");
		
		return response;

		
	}
	//FDP1183 CM End
}
