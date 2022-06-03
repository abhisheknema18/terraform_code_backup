/**
 * Author:  T Goodwin
 * Date:    29/05/2012
 * Project: FDE018
 * 
 * Copyright AMT-Sybex 2015
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Removal of /v1 namespace from web service end points 
 * and code re-factoring.
 * 
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.GetScriptReferenceResponse.APPLICATION_VND_FIELDSMART_SCRIPT_REF_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.GetScriptReferenceResponse.APPLICATION_VND_FIELDSMART_SCRIPT_REF_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_JSON;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_FILE_NAME_DESCRIPTION;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amtsybex.fieldreach.services.endpoint.common.ReferenceOnlyFileController;
import com.amtsybex.fieldreach.services.endpoint.rest.RestReferenceOnlyFileController;
import com.amtsybex.fieldreach.services.messages.response.GetScriptReferenceResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api(tags = "Script/Results")
public class RestReferenceOnlyFileControllerImpl extends BaseControllerImpl
		implements RestReferenceOnlyFileController {

	private static final Logger log = LoggerFactory.getLogger(RestReferenceOnlyFileControllerImpl.class.getName());

	@Autowired
	private ReferenceOnlyFileController referenceOnlyFileController;

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.
	 * RestReferenceOnlyFileController
	 * #getReferenceOnlyFile(javax.servlet.http.HttpServletRequest,
	 * java.lang.String)
	 */
	@Deprecated
	@Override
	@GetMapping(value = "/script/reference/{id:.*}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_SCRIPT_REF_1_JSON, APPLICATION_VND_FIELDSMART_SCRIPT_REF_1_XML})
	@ApiOperation(value = "Script Reference Only File Retrieval", 
			notes = "It is possible for reference only media items to be associated with script questions. " +
					"In this case the script question contains a reference to a media file but an encoded version of " +
					"the file itself is not contained within the script. " + 
					"The web service provides a means for these referenced media files to be downloaded")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include FileNotFoundException, EXCEPTION(General Exception)"),
	})
	public ResponseEntity<GetScriptReferenceResponse> getReferenceOnlyFile(@RequestHeader HttpHeaders httpHeaders,
					   @ApiParam(value = DOWNLOAD_FILE_NAME_DESCRIPTION) @PathVariable("id") String referenceOnlyFileName) {

		if (log.isDebugEnabled())
			log.debug(">>> getReferenceOnlyFile fileName="
					+ Common.CRLFEscapeString(referenceOnlyFileName));

		GetScriptReferenceResponse getReferenceOnlyFileresponse = new GetScriptReferenceResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		getReferenceOnlyFileresponse.setError(errorMessage);

		try {

			// FDE019 05/09/2012 TM
			// Moved Header Debug to Utility method
			// Debug headers
			Utils.debugHeaders(log, httpHeaders);
			// End FDE019

			getReferenceOnlyFileresponse
					.setReferenceFileSource(referenceOnlyFileController.getReferenceOnlyFileContents(
							referenceOnlyFileName,
							Utils.extractApplicationIdentifier(httpHeaders)));

		} catch (ResourceNotFoundException e) {

			log.error("Resource file not found " + Common.CRLFEscapeString(referenceOnlyFileName)
					+ " type=" + Common.CRLFEscapeString(Utils.REFERENCEONLY_FILE_TYPE));

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription("Resource File not found");

		} catch (ResourceTypeNotFoundException e) {

			log.error("Resource type not found for " + Common.CRLFEscapeString(referenceOnlyFileName)
					+ " type=" + Common.CRLFEscapeString(Utils.REFERENCEONLY_FILE_TYPE));

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			
			errorMessage.setErrorDescription("Resource File type not found");
		} catch (Exception ex) {

			log.error("Exception in get Reference only File " + ex.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(ex.getMessage());
		}
		finally {
			
			getReferenceOnlyFileresponse
				.setSuccess(errorMessage.getErrorCode() == null);
		
		}

		if (log.isDebugEnabled())
			log.debug("<<< getReferenceOnlyFile");

		return ResponseEntity.ok(getReferenceOnlyFileresponse);
	}
}
