package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse.APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse.APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.GetDeltaScriptResultResponse.APPLICATION_VND_FIELDSMART_DELTA_SCRIPT_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.GetDeltaScriptResultResponse.APPLICATION_VND_FIELDSMART_DELTA_SCRIPT_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse.APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse.APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.ResultsHistoryDatabaseResponse.APPLICATION_VND_FIELDSMART_HISTORY_DB_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.ResultsHistoryDatabaseResponse.APPLICATION_VND_FIELDSMART_HISTORY_DB_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.ResultsHistoryDeltaResponse.APPLICATION_VND_FIELDSMART_HISTORY_DELTA_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.ResultsHistoryDeltaResponse.APPLICATION_VND_FIELDSMART_HISTORY_DELTA_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_JSON;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DB_AREA_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DB_CLASS_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DELTA_DATE_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DELTA_TIME_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_FILE_NAME_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_IDENTIFIER_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_PART_DESCRIPTION;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.ResultHistoryExtract;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.services.download.FileDownloadController;
import com.amtsybex.fieldreach.services.endpoint.rest.RestResultHistoryController;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.GetDeltaScriptResultResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.ResultsHistoryDatabaseResponse;
import com.amtsybex.fieldreach.services.messages.response.ResultsHistoryDeltaResponse;
import com.amtsybex.fieldreach.services.messages.types.DeltaResultList;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.FullDataBaseMessage;
import com.amtsybex.fieldreach.services.resource.DatabaseController;
import com.amtsybex.fieldreach.services.resource.common.DatabaseDetails;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * FDE043
 * @author CroninM
 *
 */
@Controller
@RequestMapping("/result/history")
@Api(tags = "Result History Database/Delta")
public class RestResultHistoryControllerImpl extends BaseControllerImpl implements RestResultHistoryController {

	private static final Logger log = LoggerFactory.getLogger(RestResultHistoryControllerImpl.class.getName());
	
	@Autowired
	private DatabaseController databaseController;

	@Autowired
	private FileDownloadController fileDownloadController;
	
	@Autowired
	private ScriptResultsService scriptResultsService;

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestResultHistoryController
	 * #getDatabaseFileDetails(javax.servlet.http.HttpServletRequest)
	 */
	@Deprecated
	@GetMapping(value = "/database",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_HISTORY_DB_1_JSON, APPLICATION_VND_FIELDSMART_HISTORY_DB_1_XML})
	@ApiOperation(value = "Retrieval of Latest Historic Results Database Information", 
			notes = "The web service supports the retrieval of a historic results database from the server. " +
					"Retrieving the historic results database is a two-step process. " +
					"The device must first retrieve details of the latest database version based on database class. " +
					"The second step is to then retrieve the database using this information if a new database is required")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include ResultHistoryDbNotFoundException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - If dbClass parameter is not supplied")

	})
	public ResponseEntity<ResultsHistoryDatabaseResponse> getDatabaseFileDetails(@RequestHeader HttpHeaders httpHeaders,
				 @ApiParam(value = DB_CLASS_DESCRIPTION, required = true) @RequestParam(name = Utils.DBCLASS_PARAM, required = false) String dbClass,
				 @ApiParam(value = DB_AREA_DESCRIPTION) @RequestParam(name = Utils.DBAREA_PARAM, required = false) String dbArea)
			throws BadRequestException {

		log.debug(">>> getDatabaseFileDetails");

		// Initialise response object.
		ResultsHistoryDatabaseResponse rhDBResp = new ResultsHistoryDatabaseResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		rhDBResp.setError(errorMessage);

		try {

			Utils.debugHeaders(log, httpHeaders);

			// Extract application identifier from request headers.
			String applicationIdentifier = Utils
					.extractApplicationIdentifier(httpHeaders);
			
			// Check that dbClass parameter has been supplied.
			if (dbClass == null)
				throw new BadRequestException(
						"dbClass paramater must be supplied.");

			if (dbClass.trim().equals(""))
				throw new BadRequestException("dbClass supplied is empty");
			
			// Retrieve the database details
			DatabaseDetails dbDetails = databaseController.getDbDetails(
					Utils.RESULTHISTORYDB_TYPE, dbClass, dbArea, applicationIdentifier);

			if (dbDetails != null) {

				// Set the response message properties
				FullDataBaseMessage full = new FullDataBaseMessage();
				full.setChecksum(dbDetails.getChecksum());
				full.setName(dbDetails.getName());
				full.setSizeBytes(dbDetails.getSize());

				rhDBResp.setFull(full);

				if (log.isDebugEnabled())
					log.debug("ResultHistory DB selected " + dbDetails.getName());

			} else {

				log.error("ResultHistory DB not found.");

				errorMessage.setErrorCode(Utils.RESULTHISTORY_DB_NOT_FOUND);
				errorMessage
						.setErrorDescription("No ResultHistory database information was retrieved");
			}

		} catch (BadRequestException e) {

			// Rethrow for handler to set.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);			
			

		} catch (ResourceTypeNotFoundException | ResourceNotFoundException ex) {

			log.error("Error in /database/resultshistory: " + ex.getMessage());

			errorMessage.setErrorCode(Utils.RESULTHISTORY_DB_NOT_FOUND);
			errorMessage.setErrorDescription(ex.getMessage());

		} catch (Exception ex) {

			log.error("Error in /database/resultshistory: " + ex.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(ex.getMessage());

		} finally {
			
			rhDBResp.setSuccess(errorMessage.getErrorCode() == null);
	
		}

		log.debug("<<< getDatabaseFileDetails");

		return ResponseEntity.ok(rhDBResp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestResultHistoryController
	 * #initiateDbDownload(javax.servlet.http.HttpServletRequest,
	 * java.lang.String)
	 */
	@Override
	@Deprecated
	@GetMapping(value = "/database/{filename:.*}/multipart",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_JSON, APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_XML})
	@ApiOperation(value = "Initiate Historic Results Database Download", 
			notes = "Multipart file download initiate call to begin the download and retrieve the download identifier")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include FileNotFoundException , MaximumFileSizeExceededException, IOException, DatabaseException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - The dbClass parameter is not supplied."),
	})
	public ResponseEntity<InitiateDownloadResponse> initiateDbDownload(@RequestHeader HttpHeaders httpHeaders,
			     @ApiParam(value = DB_CLASS_DESCRIPTION, required = true) @RequestParam(name = Utils.DBCLASS_PARAM, required = false) String dbClass,
			     @ApiParam(value = DB_AREA_DESCRIPTION) @RequestParam(name = Utils.DBAREA_PARAM, required = false) String dbArea,
			@PathVariable("filename") String fileName)
			throws BadRequestException {

		if (log.isDebugEnabled())
			log.debug(">>> initiateDbDownload fileName=" + Common.CRLFEscapeString(fileName));

		// Initialise Response object.
		InitiateDownloadResponse objResponse = new InitiateDownloadResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);

		try {

			Utils.debugHeaders(log, httpHeaders);

			// Extract application identifier from request headers.
			String applicationIdentifier = Utils
					.extractApplicationIdentifier(httpHeaders);
			
			// Check that dbClass parameter has been supplied.
			if (dbClass == null)
				throw new BadRequestException(
						"dbClass paramater must be supplied.");

			if (dbClass.trim().equals(""))
				throw new BadRequestException("dbClass supplied is empty");
			
			
			// Find location the database file is stored.
			String rhDbLoc = databaseController.getDbLocation(
					Utils.RESULTHISTORYDB_TYPE, dbClass, dbArea, applicationIdentifier);

			rhDbLoc = rhDbLoc + File.separator + fileName;

			if (log.isDebugEnabled())
				log.debug("ResultHistory database location:" + Common.CRLFEscapeString(rhDbLoc));

			// Initiate the download.
			objResponse = this.fileDownloadController
					.initiateFileSystemDownload(applicationIdentifier,
							rhDbLoc);

		} catch (BadRequestException e) {

			// Rethrow for handler to set.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (ResourceTypeNotFoundException e) {

			log.error("ResultHistory database directory could not be found: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (ResourceNotFoundException e) {

			log.error("ResultHistory database could not be found: " + e.getMessage());

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("Unexpected error occured in /database/resulthistory/{filename}: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		}

		log.debug("<<< initiateDbDownload");

		return ResponseEntity.ok(objResponse);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestResultHistoryController
	 * #downloadDbPart(javax.servlet.http.HttpServletRequest, java.lang.String,
	 * java.lang.String, int)
	 */
	@Deprecated
	@GetMapping(value = "/database/{filename:.*}/multipart/{identifier}/{part}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON, APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML})
	@ApiOperation(value = "Download Historic Results Database Part", notes = "Multipart file download call to retrieve file part")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include, InvalidDownloadIdentifierException, PartNumberException, FileNotFoundException, IOException, EXCEPTION(General Exception) ")
	})
	public ResponseEntity<DownloadPartResponse> downloadDbPart(@RequestHeader HttpHeaders httpHeaders,
				   @ApiParam(value = DOWNLOAD_FILE_NAME_DESCRIPTION) @PathVariable("filename") String fileName,
				   @ApiParam(value = DOWNLOAD_IDENTIFIER_DESCRIPTION) @PathVariable("identifier") String identifier,
				   @ApiParam(value = DOWNLOAD_PART_DESCRIPTION) @PathVariable("part") int partNo)  {

		if (log.isDebugEnabled()) {

			log.debug(">>> downloadDbPart fileName=" + Common.CRLFEscapeString(fileName)
					+ " identifier=" + Common.CRLFEscapeString(identifier) + " partNo=" + partNo);
		}
		
		// Initialise response object
		DownloadPartResponse objResponse = new DownloadPartResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);
		
		String applicationIdentifier = Utils
				.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers.
			Utils.debugHeaders(log, httpHeaders);

			// Get the part data.
			objResponse = this.fileDownloadController.downloadPart(
					applicationIdentifier, identifier, partNo);

		} catch (Exception e) {

			log.error("/database/resulthistory/{filename}/multipart/{identifier}/{partno}: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
			
		}

		log.debug("<<< downloadDbPart");

		return ResponseEntity.ok(objResponse);
	}

	@Deprecated
	@GetMapping(value = "/delta/search",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_HISTORY_DELTA_1_JSON, APPLICATION_VND_FIELDSMART_HISTORY_DELTA_1_XML})
	@ApiOperation(value = "Results History Delta Results Query", 
			notes = "The web services allow client devices to request a list of Result History Delta script results " +
					"that have been completed using information passed in parameters. " +
					"A query is built using the values of the parameters supplied to search the ResultsHistoryExtract " +
					"table in the Fieldreach database for records that meet the specified criteria")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - possible error codes include , FRInstanceException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - No Parameters are supplied for the request."),
	})
	public ResponseEntity<ResultsHistoryDeltaResponse> getDeltaResultsList(@RequestHeader HttpHeaders httpHeaders,
				   @ApiParam(value = DELTA_DATE_DESCRIPTION, required = true) @RequestParam(value = "deltaSearchDate", required = false) Integer deltaSearchDate,
				   @ApiParam(value = DELTA_TIME_DESCRIPTION, required = true) @RequestParam(value = "deltaSearchTime", required = false) String deltaSearchTime,
				   @ApiParam(value = DB_CLASS_DESCRIPTION, required = true) @RequestParam(value = "dbClass", required = false) String dbClass,
				   @ApiParam(value = DB_AREA_DESCRIPTION) @RequestParam(value = "dbArea", required = false) String dbArea) throws BadRequestException {

		Utils.debugHeaders(log, httpHeaders);
		
		ResultsHistoryDeltaResponse rhResponse = new ResultsHistoryDeltaResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		rhResponse.setError(errorMessage);
		
		List<Integer>results = null;

		try {

			// Extract application identifier from request headers.
			String applicationIdentifier = Utils
					.extractApplicationIdentifier(httpHeaders);
			
			// Check that dbClass parameter has been supplied.
			if (dbClass == null)
				throw new BadRequestException(
						"dbClass paramater must be supplied.");

			if (dbClass.trim().equals(""))
				throw new BadRequestException("dbClass supplied is empty");
			
			if(deltaSearchDate == null || String.valueOf(deltaSearchDate).length() != 8)
				throw new BadRequestException("deltaSearchDate has not been supplied or is invalid.");
			
			if(deltaSearchTime == null || deltaSearchTime.length() != 6)
				throw new BadRequestException("deltaSearchTime has not been supplied or is invalid.");
			
			results = scriptResultsService.getResultHistoryExtractIds(applicationIdentifier, deltaSearchDate, deltaSearchTime, dbArea, dbClass);
			
			
		} catch (BadRequestException e) {
			// Rethrow for handler to set.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);
			
		} catch (FRInstanceException e) {
			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (Exception e) {
			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		}finally {

			// Determine if the request was a success
			rhResponse.setSuccess(errorMessage.getErrorCode() == null);

			// Set the list of work issued.
			if (rhResponse.isSuccess())
				rhResponse.setDeltaResultList(new DeltaResultList(results));
				//rhResponse.setDeltaResultList(new DeltaResultList(results));
		}

		if (log.isDebugEnabled())
			log.debug("<<< workIssued");

		return ResponseEntity.ok(rhResponse);
	}

	@Deprecated
	@GetMapping(value = "/delta/{id}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_DELTA_SCRIPT_1_JSON, APPLICATION_VND_FIELDSMART_DELTA_SCRIPT_1_XML})
	@ApiOperation(value = "Results History Delta Results Download", 
			notes = "The web services allow client devices to retrieve specific script result files from the server " +
					"by specifying the returnid of the script result file")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include ScriptResultNotFoundException,ScriptResultExtractionException, FRInstanceException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - Specified returnid is not a valid number."),
			
	})
	public ResponseEntity<GetDeltaScriptResultResponse> getDeltaResult(@RequestHeader HttpHeaders httpHeaders, @PathVariable("id") int returnId)  {


		Utils.debugHeaders(log, httpHeaders);

		GetDeltaScriptResultResponse rhResponse = new GetDeltaScriptResultResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		rhResponse.setError(errorMessage);
		
		ResultHistoryExtract result = null;
		
		String scriptResultSource = null;
		String checksum = null;
		String fileName = null;

		try {

			// Extract application identifier from request headers.
			String applicationIdentifier = Utils
					.extractApplicationIdentifier(httpHeaders);

			//get result extract for file return id
			result = scriptResultsService.getResultHistoryExtract(applicationIdentifier, returnId);

			if (result == null) {

				log.error("Delta Result entry " + returnId + " not found");
				throw new ResourceNotFoundException();
			}
			
			//get file from extract
			File file = new File(result.getFileName());

			if (file.exists()) {

				//add file details to response
				fileName = file.getName();
				
				byte[] byteData = Common.getBytesFromFile(file);

				scriptResultSource = Common.encodeBase64(byteData);
				
				checksum = Common.generateMD5Checksum(byteData);

			}else {
				log.error("Delta Result file " + returnId + " not found");
				throw new ResourceNotFoundException();
			}
			
		} catch (ResourceNotFoundException e) {

			log.error("Delta Result file not found " + returnId);

			errorMessage.setErrorCode(Utils.SCRIPT_RESULT_NOTFOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (FileNotFoundException e) {
			
			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.SCRIPT_RESULT_EXTRACTION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (FRInstanceException | IOException e) {
			
			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (Exception e) {
			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} finally {

			// Determine if the request was a success
			rhResponse.setSuccess(errorMessage.getErrorCode() == null);

			// Set the list of work issued.
			if (rhResponse.isSuccess()) {
				rhResponse.setChecksum(checksum);
				rhResponse.setFileName(fileName);
				rhResponse.setScriptResultSource(scriptResultSource);
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< workIssued");

		return ResponseEntity.ok(rhResponse);
		
	}
	
}
