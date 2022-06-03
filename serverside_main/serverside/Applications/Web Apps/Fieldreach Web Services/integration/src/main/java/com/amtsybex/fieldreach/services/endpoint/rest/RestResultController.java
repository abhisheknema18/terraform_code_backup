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

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.request.ScriptResultNote;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.GetScriptResultResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.NextResultStatusResponse;
import com.amtsybex.fieldreach.services.messages.response.ScriptResultsSearchResponse;

public interface RestResultController {
	
	//
	// FDE020 TM 23/01/2013

	//
	// FDP998 TM 20/09/2013
	// Modify method signature to include extra parameters

	//
	// FDP1009 TM 17/12/2013
	// Modified method signature.

	// FDE034 TM 18/08/2014
	// Modified method signature

	/**
	 * Retrieve a list of all script results completed using the parameters
	 * supplied.
	 * 
	 * @param request
	 *            servlet request.
	 * 
	 * @param equipNo
	 *            the equipno of the asset the script result was completed
	 *            against
	 * 
	 * @param fromDate
	 *            Return script results where the completed date is >= supplied
	 *            value.
	 * 
	 * @param toDate
	 *            Return script results where the completed date is <= supplied
	 *            value.
	 * 
	 * @param scriptCode
	 *            Return script results where the script code matches any of
	 *            those supplied.
	 * 
	 * @param resultStatus
	 *            Return script results where the result status matches any of
	 *            those supplied.
	 * 
	 * @param userCode
	 *            Return script results where the complete user matches any of
	 *            those supplied.
	 * 
	 * @param workgroupCode
	 *            Return script results where the workgroup matches any of those
	 *            supplied.
	 * 
	 * @param altEquipRef
	 *            Return script results where the altEquipRef matches that
	 *            supplied.
	 * 
	 * @param workOrderNo
	 *            Return script results where the workorderno matches that
	 *            supplied. Must appear along with no other parameters.
	 * 
	 * @param resAssocReturnId
	 *            Get the resassoccode of the returnid supplied in this
	 *            parameter. Then use the reassoccode to return script results
	 *            where the resassoccode matches that resolved. Must appear
	 *            along with no other parameters.
	 * 
	 * @param viewId
	 *            Id of personal view to use to perform a search. Must appear
	 *            along with no other parameters.
	 * 
	 * @return AssetScriptResultsResponse message containing details of
	 *         completed script results using the parameters supplied.
	 * 
	 * @
	 *             If invalid authorization token is passed.
	 * 
	 * @throws BadRequestException
	 *             No search parameters are passed.
	 */
	ResponseEntity<ScriptResultsSearchResponse> searchScriptResults(HttpServletRequest request, String appCode, String deviceId, String equipNo, Integer fromDate, Integer toDate,
																	String scriptCode, String resultStatus, String userCode, String workgroupCode, String altEquipRef,
																	String workOrderNo, Integer resAssocReturnId, Integer viewId)
					throws BadRequestException;

	// End FDP1009

	// End FDP998

	// FDE034 TM 03/09/2015
	// Added new parameter.
	/**
	 * Retrieve a script result from the server. The script result should be in
	 * 'Extract Result' format produced by the Fieldreach Extract Engine.
	 * 
	 * @param httpHeaders
	 *            http Headers.
	 * 
	 * @param returnid
	 *            The returnId of the script result to retrieve from the server.
	 * 
	 * @param extractBinary
	 *            Boolean flag to indicate if binary response data should be
	 *            extracted. Response information such as seqno and resorderno
	 *            will still be extracted, but the actually binary response data
	 *            will not.
	 * 
	 *            True means the binary data will be extracted, false means it
	 *            will not.
	 * 
	 * @param fullScript
	 *            Boolean flag indicating if the full script result should be
	 *            extracted. If this is true unanswered questions will also be
	 *            extracted, false means they will not.
	 * 
	 * @param editFlag
	 *            Boolean flag to indicate if the response should contain a flag
	 *            indicating if the user can edit the result.
	 * 
	 * @return GetScriptResultResponse message containing the script result data
	 *         encoded in base64.
	 * 
	 * @
	 *             If invalid authorization token is passed.
	 * 
	 * @throws BadRequestException
	 *             If returnid passed is not a valid number.
	 */
	ResponseEntity<GetScriptResultResponse> getScriptResult(HttpHeaders httpHeaders, String appCode, String deviceId, int returnid, boolean extractBinary, boolean fullScript,
															boolean editFlag)
			throws BadRequestException;

	/**
	 * Get the next status definition for a particular status of a script result.
	 * 
	 * @param httpHeaders
	 *            http Headers.
	 * 
	 * @param returnid
	 *            The returnId of the script result to retrieve the next status definition of.
	 * 
	 * @param status
	 *            status value to get the next status definition of.
	 * 
	 * @return NextResultStatusResponse message.
	 * 
	 * @
	 *             If invalid authorization token is passed.
	 * 
	 * @throws BadRequestException
	 *             If returnid is not a valid number.
	 */
	ResponseEntity<NextResultStatusResponse> getNextResultWorkflowStatus(HttpHeaders httpHeaders, int returnid, String status)
			throws BadRequestException;
	
	/**
	 * Set the status of a particular status of a script result.
	 * 
	 * @param httpHeaders
	 *            http Headers.
	 * 
	 * @param returnid
	 *            The returnId of the script result to update the status of.
	 * 
	 * @param status
	 *            The status to set the script result to.
	 * 
	 * @return CallResponse message.
	 * 
	 * @
	 *             If invalid authorization token is passed.
	 * 
	 * @throws BadRequestException
	 *             If returnid is not a valid number.
	 */
	ResponseEntity<CallResponse> updateScriptResultStatus(HttpHeaders httpHeaders, int returnid, String userCode, String status)
			throws BadRequestException;
	
	// End FDE034

	/**
	 * Retrieve binary script response data for a specific binary question from
	 * a specific script result.
	 * 
	 * @param httpHeaders
	 *            http Headers.
	 * 
	 * @param returnid
	 *            The returnId of the script result to retrieve the binary
	 *            response from.
	 * 
	 * @param resorderno
	 *            resorderno of the binary response to extract.
	 * 
	 * @return GetBinaryScriptResponse message.
	 * 
	 * @
	 *             If invalid authorization token is passed.
	 * 
	 * @throws BadRequestException
	 *             If returnid or resorderno passed are not valid numbers.
	 */
	ResponseEntity<InitiateDownloadResponse> getBinaryScriptResponse(HttpHeaders httpHeaders, String appCode, String deviceId, int returnid, int resorderno)
			throws BadRequestException;

	/**
	 * Download the specified part of the file referenced by the download
	 * identifier supplied.
	 * 
	 * @param httpHeaders
	 *            http Headers
	 * 
	 * @param returnid
	 *            Returnid of the script result the binary response being
	 *            downloaded belongs to.
	 * 
	 * @param resorderno
	 *            ResOrderNo of the binary response being downloaded.
	 * 
	 * @param identifier
	 *            identifier of download to retrieve a part for.
	 * 
	 * @param partNo
	 *            The part of the file to download.
	 * 
	 * @return DownloadPartResponse message containing data for the requested
	 *         part.
	 * 
	 * @
	 *             If invalid authorization token is passed.
	 *
	 */
	ResponseEntity<DownloadPartResponse> downloadBinaryScriptResponsePart(HttpHeaders httpHeaders, int returnid, int resorderno,
																		  String identifier, int partNo);

	// End FDE020
	
	//FDE044 - MC - Move fieldreachint to fieldreachws

	/**
	 * Create a result note against the script result specified.
	 * 
	 * @param httpHeaders
	 *            http Headers.
	 * 
	 * @param returnid
	 *            The returnId of the script result to create a note against.
	 * 
	 * @param body
	 *            ScriptResultNote message
	 * 
	 * @return CallResponse message.
	 * 
	 * @throws BadRequestException
	 *             If returnid is not a valid number or request body could not
	 *             be parsed.
	 *
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	ResponseEntity<CallResponse> createResultNote(HttpHeaders httpHeaders, int returnid, String userCode, ScriptResultNote body) throws BadRequestException;


}
