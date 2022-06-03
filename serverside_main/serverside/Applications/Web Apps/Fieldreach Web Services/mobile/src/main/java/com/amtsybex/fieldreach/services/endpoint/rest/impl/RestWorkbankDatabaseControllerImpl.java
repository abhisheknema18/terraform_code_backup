/**
 * Author:  T Murray
 * Date:    20/01/2015
 * Project: FDE029
 * 
 * Copyright AMT-Sybex 2015
 * 
 * mended:
 * FDE034 TM 18/08/2015
 * Refactored code that unmarhsals responses and converts them to a string.
 * Moved this to a re-usable method in the super class.
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse.APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse.APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse.APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse.APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.WorkBankDatabaseResponse.APPLICATION_VND_FIELDSMART_WORKBANK_DB_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.WorkBankDatabaseResponse.APPLICATION_VND_FIELDSMART_WORKBANK_DB_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_JSON;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DB_AREA_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_FILE_NAME_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_IDENTIFIER_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_PART_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.WB_CLASS_DESCRIPTION;

import java.io.File;

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

import com.amtsybex.fieldreach.services.download.FileDownloadController;
import com.amtsybex.fieldreach.services.endpoint.rest.RestAssetDatabaseController;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.response.DatabaseResponse;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.WorkBankDatabaseResponse;
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

@Controller
@RequestMapping("/workbank")
@Api(tags = "Work Bank Database")
public class RestWorkbankDatabaseControllerImpl extends BaseControllerImpl
		implements RestAssetDatabaseController {

	private static final Logger log = LoggerFactory.getLogger(RestWorkbankDatabaseControllerImpl.class.getName());
	
	@Autowired
	private DatabaseController databaseController;

	@Autowired
	private FileDownloadController fileDownloadController;

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestAssetDatabaseController
	 * #getDatabaseFileDetails(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@Deprecated
	@GetMapping(value = "/database",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_WORKBANK_DB_1_JSON, APPLICATION_VND_FIELDSMART_WORKBANK_DB_1_XML})
	@ApiOperation(value = "Retrieval of Latest Work Bank Database Information", 
			notes = "The web service supports the retrieval of a work bank database from the server. " +
					"The process of retrieving the work bank database is a two-step process. " +
					"The device must first retrieve details of the latest database version based on the work bank class. " +
					"The second step is to then retrieve the database using this information if a new database is required")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include WorkBankDbNotFoundException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - The wbClass parameter is not supplied or is empty."),
	})
	public ResponseEntity<DatabaseResponse> getDatabaseFileDetails(@RequestHeader HttpHeaders httpHeaders,
				   @ApiParam(value = WB_CLASS_DESCRIPTION, required = true) @RequestParam(name = Utils.WBCLASS_PARAM, required = false) String wbClass,
				   @ApiParam(value = DB_AREA_DESCRIPTION) @RequestParam(name = Utils.DBAREA_PARAM, required = false) String dbArea)
			throws BadRequestException {

		log.debug(">>> getDatabaseFileDetails");

		// Initialise response object.
		WorkBankDatabaseResponse workbankDBResp = new WorkBankDatabaseResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		workbankDBResp.setError(errorMessage);

		try {

			Utils.debugHeaders(log, httpHeaders);

			// Extract application identifier from request headers.
			String applicationIdentifier = Utils
					.extractApplicationIdentifier(httpHeaders);

			// Check that dbClass parameter has been supplied.
			if (wbClass == null)
				throw new BadRequestException(
						"wbClass paramater must be supplied.");

			if ((wbClass.trim().equals("")))
				throw new BadRequestException("wbClass supplied is empty");

			// Retrieve the database details
			DatabaseDetails dbDetails = databaseController.getDbDetails(
					Utils.WORKBANKDB_TYPE, wbClass, dbArea,
					applicationIdentifier);

			if (dbDetails != null) {

				// Set the response message properties
				FullDataBaseMessage full = new FullDataBaseMessage();
				full.setChecksum(dbDetails.getChecksum());
				full.setName(dbDetails.getName());
				full.setSizeBytes(dbDetails.getSize());

				workbankDBResp.setFull(full);

				if (log.isDebugEnabled())
					log.debug("Workbank DB selected " + dbDetails.getName());

			} else {

				log.error("Workbank DB not found.");

				errorMessage.setErrorCode(Utils.WORKBANK_DB_NOT_FOUND);
				errorMessage
						.setErrorDescription("No workbank database information was retrieved");
			}

		} catch (BadRequestException e) {

			// Rethrow for handler to set.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (ResourceTypeNotFoundException | ResourceNotFoundException ex) {

			log.error("Error in /database/workbank: " + ex.getMessage());

			errorMessage.setErrorCode(Utils.WORKBANK_DB_NOT_FOUND);
			errorMessage.setErrorDescription(ex.getMessage());

		} catch (Exception ex) {

			log.error("Error in /database/workbank: " + ex.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(ex.getMessage());

		} finally {
			
			workbankDBResp.setSuccess(errorMessage.getErrorCode() == null);
		}
		
		log.debug("<<< getDatabaseFileDetails");

		return ResponseEntity.ok(workbankDBResp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestAssetDatabaseController
	 * #initiateDbDownload(javax.servlet.http.HttpServletRequest,
	 * java.lang.String)
	 */
	@Override
	@Deprecated
	@GetMapping(value = "/database/{filename:.*}/multipart",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_JSON, APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_XML})
	@ApiOperation(value = "Initiate Work Bank Database Download",
			notes = "Multipart file download initiate call to begin the download and retrieve the download identifier")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include FileNotFoundException, MaximumFileSizeExceededException, IOException, DatabaseException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - The wbClass parameter is not supplied or is empty.")
	})
	public ResponseEntity<InitiateDownloadResponse> initiateDbDownload(@RequestHeader HttpHeaders httpHeaders, 
				   @ApiParam(value = DOWNLOAD_FILE_NAME_DESCRIPTION) @PathVariable("filename") String fileName, 
				   @ApiParam(value = WB_CLASS_DESCRIPTION, required = true) @RequestParam(name = Utils.WBCLASS_PARAM, required = false) String wbClass, 
				   @ApiParam(value = DB_AREA_DESCRIPTION) @RequestParam(name = Utils.DBAREA_PARAM, required = false) String dbArea)
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
			if (wbClass == null)
				throw new BadRequestException(
						"wbClass paramater must be supplied.");

			if ((wbClass.trim().equals("")))
				throw new BadRequestException("wbClass supplied is empty");

			// Find location the database file is stored.
			String workbankDbLoc = databaseController.getDbLocation(
					Utils.WORKBANKDB_TYPE, wbClass, dbArea,
					applicationIdentifier);

			workbankDbLoc = workbankDbLoc + File.separator + fileName;

			if (log.isDebugEnabled())
				log.debug("Workbank database location:" + Common.CRLFEscapeString(workbankDbLoc));

			// Initiate the download.
			objResponse = this.fileDownloadController
					.initiateFileSystemDownload(applicationIdentifier,
							workbankDbLoc);

		} catch (BadRequestException e) {

			// Rethrow for handler to set.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (ResourceTypeNotFoundException e) {

			log.error("Workbank database directory could not be found: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (ResourceNotFoundException e) {

			log.error("Workbank database could not be found: " + e.getMessage());

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("Unexpected error occured in /database/workbank/{filename}: "
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
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestAssetDatabaseController
	 * #downloadDbPart(javax.servlet.http.HttpServletRequest, java.lang.String,
	 * java.lang.String, int)
	 */
	@Override
	@Deprecated
	@GetMapping(value = "/database/{filename:.*}/multipart/{identifier}/{part}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON, APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML})
	@ApiOperation(value = "Download Work Bank Database Part", notes = "Multipart file download call to retrieve file part")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include InvalidDownloadIdentifierException, PartNumberException, FileNotFoundException, IOException, EXCEPTION(General Exception)"),
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

			log.error("/database/workbank/{filename}/multipart/{identifier}/{partno}: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		}
		
		log.debug("<<< downloadDbPart");

		return ResponseEntity.ok(objResponse);
	}

}
