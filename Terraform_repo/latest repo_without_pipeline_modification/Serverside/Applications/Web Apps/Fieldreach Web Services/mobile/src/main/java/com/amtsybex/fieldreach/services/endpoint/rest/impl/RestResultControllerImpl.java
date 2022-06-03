/**
 * Author:  T Goodwin & T Murray
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Author T Goodwin
 * Date : 10/07/2012
 * Project : FDE018
 * Description : Add application identifier to script retrieval
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
 * Amended:
 * FDE034 TM 18/08/2015
 * Refactored code that unmarhsals responses and converts them to a string.
 * Moved this to a re-usable method in the super class.
 * 
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse.APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse.APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.GetScriptResultResponse.APPLICATION_VND_FIELDSMART_SCRIPT_RESULT_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.GetScriptResultResponse.APPLICATION_VND_FIELDSMART_SCRIPT_RESULT_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse.APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse.APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.NextResultStatusResponse.APPLICATION_VND_FIELDSMART_NEXT_STATUS_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.NextResultStatusResponse.APPLICATION_VND_FIELDSMART_NEXT_STATUS_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.ScriptResultsSearchResponse.APPLICATION_VND_FIELDSMART_SCRIPT_RESULT_SEARCH_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.ScriptResultsSearchResponse.APPLICATION_VND_FIELDSMART_SCRIPT_RESULT_SEARCH_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.ALT_EQUIP_REF_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_JSON;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_IDENTIFIER_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_PART_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.EQUIP_NO_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.EXTRACT_BINARY_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.EXTRACT_EDIT_PERMISSION_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.FROM_DATE_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.FULL_SCRIPT_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.RESULT_STATUS_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.RES_ASSOC_CODE_RETURN_ID_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.RES_ORDER_NO_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.SCRIPT_CODE_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.TO_DATE_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.USER_CODES_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.VIEW_ID_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.WORKGROUP_CODES_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.WORK_ORDER_NO_DESCRIPTION;
import static com.amtsybex.fieldreach.services.messages.request.ScriptResultNote.APPLICATION_VND_FIELDSMART_SCRIPTRESULTNOTE_1_JSON;
import static com.amtsybex.fieldreach.services.messages.request.ScriptResultNote.APPLICATION_VND_FIELDSMART_SCRIPTRESULTNOTE_1_XML;
import static com.amtsybex.fieldreach.services.messages.request.UpdateScriptResult.APPLICATION_VND_FIELDSMART_UPDATESCRIPTRESULT_1_JSON;
import static com.amtsybex.fieldreach.services.messages.request.UpdateScriptResult.APPLICATION_VND_FIELDSMART_UPDATESCRIPTRESULT_1_XML;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.MaxResultsExceededException;
import com.amtsybex.fieldreach.backend.exception.StatusUpdateException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.Item;
import com.amtsybex.fieldreach.backend.model.NextStatusDef;
import com.amtsybex.fieldreach.backend.model.ResultNotes;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.Script;
import com.amtsybex.fieldreach.backend.model.ScriptStatusDef;
import com.amtsybex.fieldreach.backend.model.ScriptVersions;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.model.pk.ResultNotesId;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.extract.core.ExtractEngine;
import com.amtsybex.fieldreach.extract.processor.result.exception.DBAccessException;
import com.amtsybex.fieldreach.extract.processor.result.exception.ResultNotFoundException;
import com.amtsybex.fieldreach.services.download.FileDownloadController;
import com.amtsybex.fieldreach.services.endpoint.common.ActivityLogController;
import com.amtsybex.fieldreach.services.endpoint.common.ActivityLogController.ACTIVITYTYPE;
import com.amtsybex.fieldreach.services.endpoint.common.ScriptController;
import com.amtsybex.fieldreach.services.endpoint.rest.RestResultController;
import com.amtsybex.fieldreach.services.exception.AuthenticationException;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.exception.NoAccessException;
import com.amtsybex.fieldreach.services.exception.ResponseUpdateNotSupportedException;
import com.amtsybex.fieldreach.services.exception.ResultNoteCreationException;
import com.amtsybex.fieldreach.services.exception.ResultStatusNotFoundException;
import com.amtsybex.fieldreach.services.exception.ScriptItemNotFoundException;
import com.amtsybex.fieldreach.services.exception.ScriptResultNotFoundException;
import com.amtsybex.fieldreach.services.messages.request.ScriptResultNote;
import com.amtsybex.fieldreach.services.messages.request.UpdateScriptResult;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.GetScriptResultResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.NextResultStatusResponse;
import com.amtsybex.fieldreach.services.messages.response.ScriptResultsSearchResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.ScriptResult;
import com.amtsybex.fieldreach.services.messages.types.Status;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api(tags = "Script/Results")
public class RestResultControllerImpl extends BaseControllerImpl implements RestResultController {

	private static final Logger log = LoggerFactory.getLogger(RestResultControllerImpl.class.getName());

	@Autowired
	private ScriptController scriptController;

	// FDE020 TM 24/01/2013
	@Autowired
	private ExtractEngine fieldreachExtractEngine;

	// FDE020 TM 28/02/2013
	@Autowired
	private UserService userService;

	// FDE020 TM 10/04/2013
	@Autowired
	private FileDownloadController fileDownloadController;

	@Autowired
	private ScriptService scriptService;
	
	@Autowired
	private ScriptResultsService scriptResultsService;
	
	//FDE044 - MC - field activity log
	@Autowired
	private ActivityLogController activityLogController;
	
	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	// FDE020 TM 23/01/2013

	// FDP998 TM 20/09/2013
	// Modify method signature to include extra parameters

	//
	// FDP1009 TM 17/12/2013
	// Modified method signature and implementation.

	// FDE034 TM 18/08/2014
	// Modified method signature

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestScriptController#
	 * searchScriptResults(javax.servlet.http.HttpServletRequest,
	 * java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Deprecated
	@GetMapping(value = "/result/search",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_SCRIPT_RESULT_SEARCH_1_JSON, APPLICATION_VND_FIELDSMART_SCRIPT_RESULT_SEARCH_1_XML})
	@ApiOperation(value = "Script Result Search",
			notes = "The web services allow client devices to request a list of script results that have been completed " +
					"using information passed in parameters. " +
					"A query is built using the values of the parameters supplied to search the ReturnedScripts table " +
					"in the Fieldreach database for records that meet the specified criteria")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include MaxResultsExceededException, FRInstanceException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - No Parameters or wrong parameter type are supplied for the request")
	})
	public ResponseEntity<ScriptResultsSearchResponse> searchScriptResults(HttpServletRequest request,
			   @RequestHeader(value=Utils.APPCODE_HEADER, required = false) String appCode,
			   @RequestHeader(value=Utils.DEVICEID_HEADER, required = false) String deviceId,
			   @ApiParam(value = EQUIP_NO_DESCRIPTION) @RequestParam(value = "equipNo", required = false) String equipNo,
			   @ApiParam(value = FROM_DATE_DESCRIPTION) @RequestParam(value = "fromDate", required = false) Integer fromDate,
			   @ApiParam(value = TO_DATE_DESCRIPTION) @RequestParam(value = "toDate", required = false) Integer toDate,
			   @ApiParam(value = SCRIPT_CODE_DESCRIPTION) @RequestParam(value = "scriptCode", required = false) String scriptCode,
			   @ApiParam(value = RESULT_STATUS_DESCRIPTION) @RequestParam(value = "resultStatus", required = false) String resultStatus,
			   @ApiParam(value = USER_CODES_DESCRIPTION) @RequestParam(value = "userCode", required = false) String userCode,
			   @ApiParam(value = WORKGROUP_CODES_DESCRIPTION) @RequestParam(value = "workgroupCode", required = false) String workgroupCode,
			   @ApiParam(value = ALT_EQUIP_REF_DESCRIPTION) @RequestParam(value = "altEquipRef", required = false) String altEquipRef,
			   @ApiParam(value = WORK_ORDER_NO_DESCRIPTION) @RequestParam(value = "workOrderNo", required = false) String workOrderNo,
			   @ApiParam(value = RES_ASSOC_CODE_RETURN_ID_DESCRIPTION) @RequestParam(value = "resAssocCodeReturnId", required = false) Integer resAssocReturnId,
			   @ApiParam(value = VIEW_ID_DESCRIPTION) @RequestParam(value = "viewId", required = false) Integer viewId)
					throws BadRequestException, AuthenticationException {

		if (log.isDebugEnabled())
			log.debug(">>> searchScriptResults equipno=" + Common.CRLFEscapeString(equipNo));

		// Initialise response
		ScriptResultsSearchResponse objResponse = new ScriptResultsSearchResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);

		try {

			String applicationIdentifier = Utils.extractApplicationIdentifier(request);
			
			String activityAdditionalText = "equipNo=" + equipNo + ", fromDate=" + fromDate + ", toDate=" + toDate + ", scriptCode=" + scriptCode + ", resultStatus=" + resultStatus + ", userCode=" + userCode + ", workgroupCode=" + workgroupCode + ", altEquipRef=" + altEquipRef + ", viewID=" + viewId + ", workOrderNo=" + workOrderNo + ", resAssocCodeReturnId=" + resAssocReturnId;
			activityLogController.recordActivityLog(applicationIdentifier, ACTIVITYTYPE.RESULT_SEARCH, getUserDetailsFromUserPrincipal(applicationIdentifier).getId().getUserCode(), deviceId, appCode, activityAdditionalText, null);

			// Parameter validation

			// FDE034 TM 18/08/2015
			// Refactored code: Moved request parameter validation code to method.
			//FDE044 - MC move this in here and then catch and rethrow the exception below.
			// Validate the search parameters supplied with the request
			validateSearchParams(request);
			// End FDE034
			
			// check that toDate is > than fromDate if both are supplied
			if ((fromDate != null && toDate != null) && fromDate > toDate) {

				log.error("Supplied 'fromDate' is greater than the supplied 'toDate'");

				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Supplied 'fromDate' is greater than the supplied 'toDate'");
			}

			// FDE034 TM 18/08/2015
			// Refactored code: created a generic method to process parameters
			// that accept multiple values.

			List<String> scriptCodes = convertParamToList(scriptCode);
			List<String> resultStatusList = convertParamToList(resultStatus);
			List<String> userCodes = convertParamToList(userCode);
			List<String> workgroupCodes = convertParamToList(workgroupCode);
			
			// Debug headers
			Utils.debugHeaders(log, request);


			// Fetch List of results completed against the specified asset
			// within the resolved time frame.
			
			// FDP1152 TM 06/11/2015
			
			HPCUsers hpcUsr = getUserDetailsFromUserPrincipal(applicationIdentifier);
			
			String systemUserCode = null;

			try {
				
				SystemUsers sysUser = userService.getSystemUser(applicationIdentifier, hpcUsr.getHpcUsersAuth().getSubject(), hpcUsr.getHpcUsersAuth().getIssuer());
				if(sysUser != null) {
					systemUserCode = sysUser.getId();
				}
				
			} catch (Exception e) {
				
				// User not found, so carry on.
			}
			
			List<ReturnedScripts> assetResults = this.scriptController.getScriptResults(applicationIdentifier, systemUserCode, 
					equipNo, fromDate, toDate, scriptCodes, resultStatusList, userCodes, workgroupCodes, altEquipRef,
					workOrderNo, resAssocReturnId, viewId);

			// End FDP1152
			
			if (assetResults != null) {

				// Loop through the results and build the response
				List<ScriptResult> resultList = new ArrayList<ScriptResult>();

				for (ReturnedScripts result : assetResults) {

					// FDP987 TM 13/09/2013
					// Check to see if the script code of the result should be
					// omitted from the Asset History search results.
					if (!scriptController.getOmitFromAHSearchList().contains(result.getScriptCode())) {

						ScriptResult objResult = new ScriptResult();

						objResult.setReturnId(Integer.toString(result.getId()));
						objResult.setScriptCode(result.getScriptCode());
						
						if(result.getDeviceId() != null)
							objResult.setDeviceId(Common.SanitizeXmlString(result.getDeviceId())); //Bug5195 TM 16/02/2016
						
						objResult.setWorkorderNo(result.getWorkOrderNo());
						objResult.setWorkorderDesc(result.getWorkOrderDesc());
						objResult.setEquipNo(result.getEquipNo());
						objResult.setEquipDesc(result.getEquipDesc());
						objResult.setAltEquipRef(result.getAltEquipRef());
						objResult.setSummaryDesc(result.getSummaryDesc());

						if (result.getCompleteDate() != null) {

							objResult.setCompleteDate(Integer.toString(result.getCompleteDate()));
						}

						if (result.getCompleteTime() != null) {

							objResult.setCompleteTime(Integer.toString(result.getCompleteTime()));
						}

						objResult.setCompleteUser(result.getCompleteUser());

						HPCUsers objUser = userService.findHPCUser(applicationIdentifier, result.getCompleteUser(),
								result.getWorkGroup());

						if (objUser != null) {

							objResult.setCompleteUserName(objUser.getUserName());
						}

						objResult.setCompleteCode(result.getCompleteCode());
						objResult.setWorkgroupCode(result.getWorkGroup());
						objResult.setResultStatus(result.getResultStatus());

						if (result.getTotalWeightScore() != null) {

							objResult.setTotalWeightScore(Integer.toString(result.getTotalWeightScore()));
						}

						objResult.setResAssocCode(result.getResAssocCode());

						// Set script desc
						Script objScript = this.scriptController.getScriptByScriptId(applicationIdentifier,
								result.getScriptId());

						if (objScript != null)
							objResult.setScriptDesc(objScript.getScriptDesc());

						// set workgroup desc
						HPCWorkGroups objwg = userService.findHPCWorkGroup(applicationIdentifier,
								result.getWorkGroup());

						if (objwg != null) {

							objResult.setWorkgroupDesc(objwg.getWorkgroupDesc());
						}

						// Add results to list of results to be returned.
						resultList.add(objResult);
					}
					// End FDP987
				}

				// Set the list of results in the response
				if (resultList.size() > 0)
					objResponse.setScriptResultList(resultList);

				objResponse.setResultsetCount(resultList.size());

			}

		} 
		catch(AuthenticationException ex) {
		    log.warn(ex.getMessage());
		    throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), ex);
		}
		catch (BadRequestException e) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);
		}
		catch (MaxResultsExceededException e) {

			log.error("/script/result/search " + e.getMessage());

			errorMessage.setErrorCode(Utils.MAX_RESULTS_EXCEEDED_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("/script/result/search " + e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} finally {
			
			objResponse.setSuccess(errorMessage.getErrorCode() == null);
			
		}

		log.debug("<<< searchScriptResults");

		return ResponseEntity.ok(objResponse);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestScriptController#
	 * getScriptResult(javax.servlet.http.HttpServletRequest, int, boolean)
	 */
	@Deprecated
	@GetMapping(value = "/result/{id}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_SCRIPT_RESULT_1_JSON, APPLICATION_VND_FIELDSMART_SCRIPT_RESULT_1_XML})
	@ApiOperation(value = "Script Result Retrieval",
			notes = "The web services allows clients to retrieve specific script result set data from the server. " +
					"It will also be possible to indicate whether binary response data should be returned with the script results or not. " +
					"Information on the question such as sequence number and resorderno will still be returned; however the " +
					"base64 encoded response data will be omitted if requested")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include AuthorizationException, ScriptResultNotFoundException, ScriptResultExtractException, FRInstanceException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - Supplied values are not of expected types" )

	})
	public ResponseEntity<GetScriptResultResponse> getScriptResult(@RequestHeader HttpHeaders httpHeaders, 
			   @RequestHeader(value=Utils.APPCODE_HEADER, required = false) String appCode,
			   @RequestHeader(value=Utils.DEVICEID_HEADER, required = false) String deviceId,
			   @PathVariable("id") int returnid,
			   @ApiParam(value = EXTRACT_BINARY_DESCRIPTION) @RequestParam(value = "extractBinary", required = false) boolean extractBinary,
			   @ApiParam(value = FULL_SCRIPT_DESCRIPTION) @RequestParam(value = "fullScript", required = false) boolean fullScript,
			   @ApiParam(value = EXTRACT_EDIT_PERMISSION_DESCRIPTION) @RequestParam(value = "extractEditPermission", required = false) boolean editFlag) throws AuthenticationException {

		if (log.isDebugEnabled())
			log.debug(">>> getScriptResult returnid=" + returnid);

		// Initialise response
		GetScriptResultResponse objResponse = new GetScriptResultResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);

		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers
			Utils.debugHeaders(log, httpHeaders);

			activityLogController.recordActivityLog(applicationIdentifier, ACTIVITYTYPE.GET_RESULT, getUserDetailsFromUserPrincipal(applicationIdentifier).getId().getUserCode(), deviceId, appCode, String.valueOf(returnid), null);

			// FDE034 TM 04/09/2015
			// Indicate if the user is able to edit the result.
			boolean canEdit = false;
			//FDE044
			
			if(editFlag) {				
				
				// Get the system user linked to the mobile user.
				HPCUsers hpcusr = getUserDetailsFromUserPrincipal(applicationIdentifier);
				SystemUsers systemUser = null;
				
				try {
					
					systemUser = userService.getSystemUser(applicationIdentifier, hpcusr.getHpcUsersAuth().getSubject(), hpcusr.getHpcUsersAuth().getIssuer());

					if(systemUser != null) {

						if (editFlag) {
							canEdit = scriptResultsService.canEditResult(applicationIdentifier, systemUser, returnid);
						}

					}
					
				}catch(Exception e) {
					
					//do nothing. mobile user not linked to system user
					canEdit = false;
				}
				
			}
			
			
			
			//END FDE044
			

			// Use the Fieldreach Extract Engine to extract the script result
			String scriptResult = fieldreachExtractEngine.extractScriptResultAll(applicationIdentifier, returnid, "",
					extractBinary, fullScript, false, false); // FDP1235 TM 25/07/2016

			// Bug 2261 TM 04/09/2013

			byte[] resultBytes;

			try {

				resultBytes = scriptResult.getBytes(Common.UTF8_ENCODING);

			} catch (UnsupportedEncodingException e) {

				resultBytes = scriptResult.getBytes();
			}

			// generate checksum for the script result
			objResponse.setChecksum(Common.generateMD5Checksum(resultBytes));

			// Convert data to base64
			objResponse.setScriptResultSource(Common.encodeBase64(resultBytes));

			// End Bug

			// Only set the edit flag in the response if a parameter has been
			// passed asking for it.
			if (editFlag)
				objResponse.setEdit(canEdit);

			// End FDE034

		} catch(UserNotFoundException | NoAccessException e) {
			
			log.error(e.getMessage());
			
			errorMessage.setErrorCode(Utils.AUTHORIZATION_EXCEPTION);
			errorMessage.setErrorDescription("Access to result is not Authorized");
			
		}
		catch(AuthenticationException ex) {
            log.warn(ex.getMessage());
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), ex);
        }  catch (ResultNotFoundException e) {

			log.error("/script/result/{id} " + e.getMessage());

			errorMessage.setErrorCode(Utils.SCRIPT_RESULT_NOTFOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch (DBAccessException e) {

			log.error("/script/result/{id} " + e.getMessage());

			errorMessage.setErrorCode(Utils.SCRIPT_RESULT_EXTRACT_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("/script/result/{id} " + e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} finally {

			objResponse.setSuccess(errorMessage.getErrorCode() == null);

		}

		log.debug("<<< getScriptResult");

		return ResponseEntity.ok(objResponse);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestScriptController#
	 * getBinaryScriptResponse(javax.servlet.http.HttpServletRequest, int, int)
	 */
	@Deprecated
	@GetMapping(value = "/result/{id}/binaryresponse/{res}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_JSON, APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_XML})
	@ApiOperation(value = "Initiate Script Binary Response Download",
			notes = "When downloading a complete script result the default option is to omitt binary response data for any binary question types. " +
					"This endpoint provides clients with a means to retrieve the binary response data for binary questions. " +
					"This web service implements the web services multi part download mechanism")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - FileNotFoundException, MaximumFileSizeExceededException, IOException, FRInstanceException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - No Parameters or Parameters not of expected type are supplied for the request")
	})
	public ResponseEntity<InitiateDownloadResponse> getBinaryScriptResponse(@RequestHeader HttpHeaders httpHeaders, 
			    @RequestHeader(value=Utils.APPCODE_HEADER, required = false) String appCode,
			    @RequestHeader(value=Utils.DEVICEID_HEADER, required = false) String deviceId,
			    @PathVariable("id") int returnid,
				@ApiParam(value = RES_ORDER_NO_DESCRIPTION) @PathVariable("res") int resorderno) throws AuthenticationException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getBinaryScriptResponse returnid=" + returnid + " resorderno=" + resorderno);
		}

		InitiateDownloadResponse objResponse = new InitiateDownloadResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);

		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers
			Utils.debugHeaders(log, httpHeaders);

			activityLogController.recordActivityLog(applicationIdentifier, ACTIVITYTYPE.GET_BINARY, getUserDetailsFromUserPrincipal(applicationIdentifier).getId().getUserCode(), deviceId, appCode, String.valueOf(returnid) + ", " + String.valueOf(resorderno), null);

			// initialise the download
			objResponse = this.fileDownloadController.initiateScriptResultsBlbDownload(applicationIdentifier, returnid,
					resorderno);

		} 
		catch(AuthenticationException ex) {
            log.warn(ex.getMessage());
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), ex);
        } 
		catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("/script/result/{id}/binaryresponse/{res} " + e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		}

		log.debug("<<< getBinaryScriptResponse");

		return ResponseEntity.ok(objResponse);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestScriptController#
	 * downloadBinaryScriptResponsePart(javax.servlet.http.HttpServletRequest,
	 * int, int, java.lang.String, int)
	 */
	@Deprecated
	@GetMapping(value = "/result/{id}/binaryresponse/{res}/multipart/{identifier}/{part}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON, APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML})
	@ApiOperation(value = "Download Script Binary Response Part",
			notes = "This web service downloads an individual part of a binary response.")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Ok,InvalidDownloadIdentifierException, PartNumberException, IOException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - Specified parameters are not valid numbers."),
	})
	public ResponseEntity<DownloadPartResponse> downloadBinaryScriptResponsePart(@RequestHeader HttpHeaders httpHeaders, @PathVariable("id") int returnid,
				@ApiParam(value = RES_ORDER_NO_DESCRIPTION) @PathVariable("res") int resorderno, 
				@ApiParam(value = DOWNLOAD_IDENTIFIER_DESCRIPTION) @PathVariable("identifier") String identifier,
				@ApiParam(value = DOWNLOAD_PART_DESCRIPTION) @PathVariable("part") int partNo) {

		if (log.isDebugEnabled()) {

			log.debug(">>> downloadBinaryScriptResponsePart returnid=" + returnid + " resorderno=" + resorderno
					+ " identifier=" + Common.CRLFEscapeString(identifier) + " partNo=" + partNo);
		}

		// Initialise response object
		DownloadPartResponse objResponse = new DownloadPartResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);

		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers
			Utils.debugHeaders(log, httpHeaders);

			//activityLogController.recordActivityLog(applicationIdentifier, ACTIVITYTYPE.GET_BINARY_PART, getUserCodeFromUserPrincipal(applicationIdentifier), request.getHeader("x-fws-deviceid"), Utils.extractAppCode(request), String.valueOf(returnid) + ", " + String.valueOf(resorderno), null);
			
			// Get the part data.
			objResponse = this.fileDownloadController.downloadPart(applicationIdentifier, identifier, partNo);

		} catch (Exception e) {

			log.error("/script/result/{id}/binaryresponse/{res}/multipart/{identifier}/{part} " + e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} 

		log.debug("<<< downloadBinaryScriptResponsePart");

		return ResponseEntity.ok(objResponse);
	}

	// End FDE020
	
	// FDE034 TM 04/09/2015

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestScriptController#
	 * getNextResultWorkflowStatus(javax.servlet.http.HttpServletRequest, int,
	 * java.lang.String)
	 */
	@Override
	@Deprecated
	@GetMapping(value = "/result/{id}/status/{status}/next",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_NEXT_STATUS_1_JSON, APPLICATION_VND_FIELDSMART_NEXT_STATUS_1_XML})
	@ApiOperation(value = "Get Script Result Next Workflow Status",
			notes = "A FieldSmart script supports a workflow implemented as a set of linked statuses through which a script " +
					"result can progress through its lifecycle. " +
					"This endpoint allows clients to request a list of possible workflow status values that the result can be set to")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - ScriptResultNotFoundException, ResultStatusNotFoundException, FRInstanceException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - The return id parameter is not a valid number."),
	})
	public ResponseEntity<NextResultStatusResponse> getNextResultWorkflowStatus(@RequestHeader HttpHeaders httpHeaders, @PathVariable("id") int returnid,
																				@PathVariable("status") String status) {

		if (log.isDebugEnabled())
			log.debug(">>> getNextResultWorkflowStatus returnid=" + returnid + " status=" + Common.CRLFEscapeString(status));

		// Initialise response
		NextResultStatusResponse resp = new NextResultStatusResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		resp.setError(errorMessage);

		// Get application identifier
		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers
			Utils.debugHeaders(log, httpHeaders);

			//activityLogController.recordActivityLog(applicationIdentifier, ACTIVITYTYPE.GET_NEXT_RESULT_WORKFLOW_STATUS, getUserCodeFromUserPrincipal(applicationIdentifier), request.getHeader("x-fws-deviceid"), Utils.extractAppCode(request), String.valueOf(returnid) + ", " + status, null);

			// Search for the result first.
			ReturnedScripts res = this.scriptResultsService.getReturnedScript(applicationIdentifier, returnid);

			if (res == null)
				throw new ScriptResultNotFoundException("Script result not found. returnId:" + returnid);

			// Then find the script version that generated the result to
			// determine scriptCodeId.
			ScriptVersions version = this.scriptService.findScriptVersion(applicationIdentifier, res.getScriptId());

			List<NextStatusDef> nextDef = scriptResultsService.getNextStatusDefByScriptCodeID(applicationIdentifier,
					version.getScriptCodeId());

			if (nextDef.isEmpty())
				throw new ResultStatusNotFoundException(
						"unable to find next status definition for scriptCodeId:" + version.getScriptCodeId());

			List<ScriptStatusDef> scriptStatusDef = 
					scriptResultsService.getScriptStatusDefByScriptCodeID(applicationIdentifier, version.getScriptCodeId());
			
			if (scriptStatusDef.isEmpty())
				throw new ResultStatusNotFoundException(
						"unable to find script status definition for scriptCodeId:" + version.getScriptCodeId());
			
			List<Status> nextStatusDef = new ArrayList<Status>();

			for (NextStatusDef def : nextDef) {

				if (def.getId().getStatusValue().equalsIgnoreCase(status)) {

					Status stat = new Status();

					stat.setStatus(def.getNextStatusValue());
					stat.setOrderNo(def.getId().getStatusOrderNo().toString());
					stat.setSystem("0");
					
					// Check to see if status is a system status
					for (ScriptStatusDef ssd : scriptStatusDef) {

						if (ssd.getStatusValue().equals(def.getNextStatusValue())) {

							if (Integer.toString(ssd.getSysStatusFlag()).equals("1"))
								stat.setSystem("1");

							break;
						}
					}

					nextStatusDef.add(stat);
				}
			}

			if (nextStatusDef.isEmpty())
				throw new ResultStatusNotFoundException(
						"Unable to find next status definition " + "for status: " + status);

			resp.setResultStatusList(nextStatusDef);

		} catch (ScriptResultNotFoundException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.SCRIPT_RESULT_NOTFOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (ResultStatusNotFoundException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.RESULT_STATUS_NOTFOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} finally {

			resp.setSuccess(errorMessage.getErrorCode() == null);

		}

		log.debug("<<< getNextResultWorkflowStatus");

		return ResponseEntity.ok(resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestScriptController#
	 * updateScriptResultStatus(javax.servlet.http.HttpServletRequest, int,
	 * java.lang.String)
	 */
	@Override
	@Deprecated
	@PutMapping(value = "/result/{id}/status/{status}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CALL_1_JSON, APPLICATION_VND_FIELDSMART_CALL_1_XML})
	@ApiOperation(value = "Update Script Result Status",
			notes = "A FieldSmart script supports a workflow implemented as a set of linked statuses through which a script " +
					"result can progress through its lifecycle. " +
					"This web service allows clients to change the current workflow status of a script result set")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - StatusUpdateException, FRInstanceException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - The return id parameter is not a valid number."),
			
	})
	public ResponseEntity<CallResponse> updateScriptResultStatus(@RequestHeader HttpHeaders httpHeaders, @PathVariable("id") int returnid,
																 @RequestParam(name = "userCode", required = false) String userCode,
																 @PathVariable("status") String status) {

		if (log.isDebugEnabled())
			log.debug(">>> updateScriptResultStatus returnid=" + returnid + " status=" + Common.CRLFEscapeString(status));

		// Initialise response
		CallResponse resp = new CallResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		resp.setError(errorMessage);

		// Get application identifier
		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers
			Utils.debugHeaders(log, httpHeaders);

			//activityLogController.recordActivityLog(applicationIdentifier, ACTIVITYTYPE.UPDATE_RESULT_WORKFLOW_STATUS, getUserCodeFromUserPrincipal(applicationIdentifier), request.getHeader("x-fws-deviceid"), Utils.extractAppCode(request), String.valueOf(returnid) + "," + status, null);

			if (status == null || status.equals(""))
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Status is null or blank");
			// Search for the result first.
			this.scriptResultsService.updateResultStatus(applicationIdentifier, returnid, status, userCode);
			
			// End FDP1188

		} catch (StatusUpdateException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.RESULT_STATUS_UPDATE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} finally {

			resp.setSuccess(errorMessage.getErrorCode() == null);
			
		}

		log.debug("<<< updateScriptResultStatus");

		return ResponseEntity.ok(resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestScriptController#
	 * updateScriptResultResponse(javax.servlet.http.HttpServletRequest, int,
	 * int, java.lang.String)
	 */
	@Deprecated
	@PutMapping(value = "/result/{id}/response/{resorderno}",
	        consumes = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_UPDATESCRIPTRESULT_1_JSON, APPLICATION_VND_FIELDSMART_UPDATESCRIPTRESULT_1_XML},
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CALL_1_JSON, APPLICATION_VND_FIELDSMART_CALL_1_XML})
	@ApiOperation(value = "Update Question Response",
			notes = "This endpoint is responsible for updating responses for script questions")
	@ResponseBody
	@Override
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - ScriptResultNotFoundException, ResponseUpdateNotSupportedException, FRInstanceException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - The body of the request is missing or invalid parameters.")
	})
	public ResponseEntity<CallResponse> updateScriptResultResponse(@RequestHeader HttpHeaders httpHeaders, @PathVariable("id") int returnid,
																   @PathVariable("resorderno") int resOrderNo, 
																   @RequestBody UpdateScriptResult updateScriptResult)
					throws BadRequestException {

		log.debug(">>> updateScriptResultResponse returnid=" + returnid + " resOrderNo=" + resOrderNo);

		// Initialise response
		CallResponse resp = new CallResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		resp.setError(errorMessage);

		// Get application identifier
		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers
			Utils.debugHeaders(log, httpHeaders);

			//activityLogController.recordActivityLog(applicationIdentifier, ACTIVITYTYPE.UPDATE_RESULT_RESPONSE, getUserCodeFromUserPrincipal(applicationIdentifier), request.getHeader("x-fws-deviceid"), Utils.extractAppCode(request), String.valueOf(returnid) + ", " +  String.valueOf(resOrderNo), null);

			// Body passed?
			if (updateScriptResult == null)
				throw new BadRequestException("Empty request body in /script/result/{id}/response/{resorderno}");
			
			scriptController.updateResultResponse(applicationIdentifier, returnid, resOrderNo, updateScriptResult);

		} catch (BadRequestException e) {

			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (ScriptResultNotFoundException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.SCRIPT_RESULT_NOTFOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (ResponseUpdateNotSupportedException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.RESPONSE_UPDATE_NOT_SUPPORTED_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (ScriptItemNotFoundException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.SCRIPT_ITEM_NOT_FOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (XmlMappingException e) {

			log.error("XmlMappingException in /script/result/{id}/response/{resorderno} " + e.getMessage());

			// If we are unable to parse the XML it is invalid and a 400
			// BadRequest needs to be returned.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (Exception e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} finally {

			resp.setSuccess(errorMessage.getErrorCode() == null);

		}

		log.debug("<<< updateScriptResultResponse");

		return ResponseEntity.ok(resp);
	}

	// End FDE034
	
	//FDE044 - MC - move from fieldreachint
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestScriptController#
	 * createResultNote(javax.servlet.http.HttpServletRequest, int,
	 * java.lang.String)
	 */
	@Override
	@Deprecated
	@PostMapping(value = "/result/{id}/note",
	        consumes = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_SCRIPTRESULTNOTE_1_JSON, APPLICATION_VND_FIELDSMART_SCRIPTRESULTNOTE_1_XML},
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CALL_1_JSON, APPLICATION_VND_FIELDSMART_CALL_1_XML})
	@ApiOperation(value = "Create a script result note", notes = "This web service creates a script result note")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include ScriptResultNotFoundException, ResultNoteCreationException"),
			@ApiResponse(code = 400, message = "Bad Request - Invalid request parameters")
	})
	public ResponseEntity<CallResponse> createResultNote(@RequestHeader HttpHeaders httpHeaders, @PathVariable("id") int returnid,
														 @RequestParam(name = "userCode", required = false) String userCode,
														 @RequestBody ScriptResultNote requestBody) throws BadRequestException {

		if (log.isDebugEnabled())
			log.debug(">>> createResultNote returnid=" + returnid + " body=" + requestBody.toString());

		// Initialise response
		CallResponse resp = new CallResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		resp.setError(errorMessage);

		// Get application identifier
		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers
			Utils.debugHeaders(log, httpHeaders);
			
			//activityLogController.recordActivityLog(applicationIdentifier, ACTIVITYTYPE.CREATE_RESULT_NOTE, getUserCodeFromUserPrincipal(applicationIdentifier), request.getHeader("x-fws-deviceid"), Utils.extractAppCode(request), String.valueOf(returnid), null);

			// Body passed?
			if (requestBody == null)
				throw new BadRequestException("Empty request body supplied");
			
			// Validate the request body.
			if (requestBody.getNoteText() == null || 
					(requestBody.getNoteText() != null && requestBody.getNoteText().trim().equals("")))
				throw new BadRequestException("NoteText has not been supplied.");

			if (requestBody.getSequenceNumber() > 0 && requestBody.getResOrderNo() <= 0)
				throw new BadRequestException("SequenceNumber and ResOrderNo must both be set");

			if (requestBody.getResOrderNo() > 0 && requestBody.getSequenceNumber() <= 0)
				throw new BadRequestException("SequenceNumber and ResOrderNo must both be set");

			// Now check to see if the script result specified exists in the
			// database
			ReturnedScripts returnedScript = this.scriptResultsService.getReturnedScript(applicationIdentifier, returnid);

			if (returnedScript == null)
				throw new ScriptResultNotFoundException("Script Result could not be found. ReturnId= " + returnid);
			
			// Check that the sequence number supplied exists
			if(requestBody.getSequenceNumber() > 0) {
				Item item = this.scriptService.findScriptItem(applicationIdentifier, returnedScript.getScriptId(),
						requestBody.getSequenceNumber());
	
				if (item == null)
					throw new ResultNoteCreationException("Sequence number '" + requestBody.getSequenceNumber()
							+ "' not found for scriptid '" + returnedScript.getScriptId() + "'");
			}
			
			// Create Result Note
			ResultNotes note = new ResultNotes();
			
			ResultNotesId id = new ResultNotesId();
			id.setReturnId(returnid);
			id.setSequenceNumber(requestBody.getSequenceNumber());
			id.setResOrderNo(requestBody.getSequenceNumber());

			if (userCode != null)
				id.setCreateUser(userCode);
			else
				id.setCreateUser(Utils.SYSTEM_USERCODE);
			
			id.setCreateDate(Common.generateFieldreachDBDate());
			id.setCreateTime(Common.generateFieldreachDBTime());
			
			note.setId(id);
			note.setNoteText(requestBody.getNoteText());
			
			this.scriptResultsService.createResultNote(applicationIdentifier, note);
			
		} catch (BadRequestException e) {

			// Handle in base class.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (XmlMappingException e) {

			log.error(e.getMessage());

			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (ScriptResultNotFoundException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.SCRIPT_RESULT_NOTFOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch(ResultNoteCreationException e) {
			
			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.RESULT_NOTE_CREATION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} finally {

			resp.setSuccess(errorMessage.getErrorCode() == null);

		}

		log.debug("<<< createResultNote");

		return ResponseEntity.ok(resp);
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Verify the parameters supplied for script result search
	 * 
	 * @param request
	 * 
	 * @return
	 */
	private void validateSearchParams(HttpServletRequest request) throws BadRequestException {

		log.debug(">>> validateSearchParams request=XXX");

		// At least one valid search parameter must be supplied.
		if (request.getParameterMap().size() == 0) {

			String msg = "No search parameters supplied.";

			log.error(msg);
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, msg);
		}

		// Specify valid parameters
		List<String> allowedParams = new ArrayList<String>();

		allowedParams.add("equipNo");
		allowedParams.add("fromDate");
		allowedParams.add("toDate");
		allowedParams.add("scriptCode");
		allowedParams.add("resultStatus");
		allowedParams.add("userCode");
		allowedParams.add("workgroupCode");
		allowedParams.add("altEquipRef");
		allowedParams.add("workOrderNo");
		allowedParams.add("resAssocCodeReturnId");
		allowedParams.add("viewId");

		// Iterate over parameters supplied and check that at least one
		// valid parameter has been suppled.
		boolean validParams = false;

		Map<String, String[]> requestParams = (Map<String, String[]>) request.getParameterMap();

		for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {

			String key = entry.getKey();

			if (allowedParams.contains(key)) {

				validParams = true;
				break;
			}
		}

		if (!validParams) {

			String msg = "No valid search parameters supplied.";

			log.error(msg);
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, msg);
		}

		// Specify parameters that can only appear alone.
		List<String> mutuallyExclusiveParams = new ArrayList<String>();

		mutuallyExclusiveParams.add("workOrderNo");
		mutuallyExclusiveParams.add("resAssocCodeReturnId");
		mutuallyExclusiveParams.add("viewId");

		// Check to see if any mutually exclusive parameters have been passed,
		// and if so ensure they are the only parameter
		for (String meParam : mutuallyExclusiveParams) {

			if (requestParams.size() > 1 && requestParams.containsKey(meParam)) {

				String msg = "Mutually exclusive parameter must appear alone: " + meParam;

				log.error(msg);
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, msg);
			}
		}

		log.debug("<<< validateSearchParams");
	}

	/**
	 * Take a string value which is a comma delimited list, and return a List of
	 * strings.
	 * 
	 * @param param
	 *            Comma delimited list of values.
	 * 
	 * @return A List of strings split by the comma delimiter.
	 */
	private List<String> convertParamToList(String param) {

		if (log.isDebugEnabled())
			log.debug(">>> convertParamToList param=" + Common.CRLFEscapeString(param));

		List<String> paramList = null;
		String[] temp;

		if (param != null) {

			temp = param.split(",");

			if (temp.length > 0)
				paramList = new ArrayList<String>();

			for (int i = 0; i < temp.length; i++)
				paramList.add(temp[i].trim());
		}

		log.debug("<<< convertParamToList");

		return paramList;
	}

	// End FDE034
	
}
