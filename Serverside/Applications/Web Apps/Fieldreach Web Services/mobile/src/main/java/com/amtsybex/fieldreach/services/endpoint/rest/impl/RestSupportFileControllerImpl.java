/**
 * Author:  T Murray
 * Date:    29/05/2014
 * Project: FDE026
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
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse.APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse.APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse.APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse.APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.SupportFileListResponse.APPLICATION_VND_FIELDSMART_SUPPORT_FILELIST_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.SupportFileListResponse.APPLICATION_VND_FIELDSMART_SUPPORT_FILELIST_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_JSON;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_FILE_NAME_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_IDENTIFIER_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_PART_DESCRIPTION;

import java.util.ArrayList;
import java.util.List;

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

import com.amtsybex.fieldreach.services.download.FileDownloadController;
import com.amtsybex.fieldreach.services.endpoint.common.SupportFileController;
import com.amtsybex.fieldreach.services.endpoint.rest.RestSupportFileController;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.SupportFileListResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.SupportFile;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api(tags = "Support File Download")
public class RestSupportFileControllerImpl extends BaseControllerImpl implements
		RestSupportFileController {

	private static final Logger log = LoggerFactory.getLogger(RestSupportFileControllerImpl.class.getName());
	
	@Autowired
	private SupportFileController supportFileController;

	@Autowired
	private FileDownloadController fileDownloadController;



	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestSupportFileController
	 * #getSupportFileList(javax.servlet.http.HttpServletRequest)
	 */
	@Deprecated
	@GetMapping(value = "/support/resource",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_SUPPORT_FILELIST_1_JSON, APPLICATION_VND_FIELDSMART_SUPPORT_FILELIST_1_XML})
	@ApiOperation(value = "Get Support File List", 
			notes = "The web service exposes and endpoint that provides clients with a means to get a list of support " +
					"files contained within a preconfigured support file download directory")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include EXCEPTION(General Exception)"),
	})
	public ResponseEntity<SupportFileListResponse> getSupportFileList(@RequestHeader HttpHeaders httpHeaders)
			 {

		if (log.isDebugEnabled())
			log.debug(">>> getSupportFileList request=xxx");

		SupportFileListResponse supportFileListResponse = new SupportFileListResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		supportFileListResponse.setError(errorMessage);

		try {

			Utils.debugHeaders(log, httpHeaders);

			// Get list of support files from the correct support file directory
			// and then build the response object
			String appIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

			List<String> supportFiles = supportFileController
					.supportFileListing(appIdentifier);

			List<SupportFile> supportFileList = new ArrayList<SupportFile>();

			for (String fileName : supportFiles) {

				SupportFile supportFile = new SupportFile();

				supportFile.setFileName(fileName);
				supportFile.setCheckSum(supportFileController
						.supportFileChecksum(fileName, appIdentifier));

				supportFileList.add(supportFile);
			}

			supportFileListResponse.setSupportFileList(supportFileList);

		} catch (Exception e) {

			log.error("Unexpected error occured getting list of support files: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
		}
		finally {
			
			supportFileListResponse.setSuccess(errorMessage.getErrorCode() == null);
		
		}

		if (log.isDebugEnabled())
			log.debug("<<< getSupportFileList");

		return ResponseEntity.ok(supportFileListResponse);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestSupportFileController
	 * #initiateSupportFileDownload(javax.servlet.http.HttpServletRequest,
	 * java.lang.String)
	 */
	@Deprecated
	@GetMapping(value = "/support/resource/{filename:.*}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_JSON, APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_XML})
	@ApiOperation(value = "Initiate Support File Download",
			notes = "This web service provides clients with a means to download support files from a preconfigured support file download directory. " +
					"This web service implements the web services multi part download mechanism")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include  MaximumFileSizeExceededException, FileNotFoundException, DBException, EXCEPTION(General Exception)"),
	})
	public ResponseEntity<InitiateDownloadResponse> initiateSupportFileDownload(@RequestHeader HttpHeaders httpHeaders,
						@ApiParam(value = DOWNLOAD_FILE_NAME_DESCRIPTION) @PathVariable("filename") String fileName)
			 {

		if (log.isDebugEnabled())
			log.debug(">>> initiateSupportFileDownload fileName=" + Common.CRLFEscapeString(fileName));

		InitiateDownloadResponse objResponse = new InitiateDownloadResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);
		
		String applicationIdentifier = Utils
				.extractApplicationIdentifier(httpHeaders);

		try {

			Utils.debugHeaders(log, httpHeaders);

			// Determine full path of support file ot be downloaded and then
			// initiate the download.
			String appIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

			String supportFileDir = this.supportFileController
					.getSupportFileDir(appIdentifier);
			String supportFile = supportFileDir + "/" + fileName;

			objResponse = this.fileDownloadController
					.initiateFileSystemDownload(applicationIdentifier,
							supportFile);

		} catch (ResourceTypeNotFoundException e) {

			log.error("Support file directory could not be found: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("Unexpected error occured in /support/resource/{filename}: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
		}
		
		if (log.isDebugEnabled())
			log.debug("<<< initiateSupportFileDownload");

		return ResponseEntity.ok(objResponse);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestSupportFileController
	 * #downloadSupportFilePart(javax.servlet.http.HttpServletRequest,
	 * java.lang.String, java.lang.String, int)
	 */
	@Deprecated
	@GetMapping(value = "/support/resource/{filename:.*}/multipart/{identifier}/{part}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON, APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML})
	@ApiOperation(value = "Download Support File Part",
			notes = "This web service provides clients with a means to download support files from a preconfigured support file download directory. " +
					"This web service implements the web services multi part download mechanism")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include InvalidDownloadIdentifierException, PartNumberException, IOException, EXCEPTION(General Exception)")
	})
	public ResponseEntity<DownloadPartResponse> downloadSupportFilePart(@RequestHeader HttpHeaders httpHeaders,
						@ApiParam(value = DOWNLOAD_FILE_NAME_DESCRIPTION) @PathVariable("filename") String fileName,
						@ApiParam(value = DOWNLOAD_IDENTIFIER_DESCRIPTION) @PathVariable("identifier") String identifier,
						@ApiParam(value = DOWNLOAD_PART_DESCRIPTION) @PathVariable("part") int partNo)  {

		if (log.isDebugEnabled()) {

			log.debug(">>> downloadSupportFilePart fileName=" + Common.CRLFEscapeString(fileName)
					+ " identifier=" + Common.CRLFEscapeString(identifier) + " part=" + partNo);
		}

		DownloadPartResponse objResponse = new DownloadPartResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);
		
		String applicationIdentifier = Utils
				.extractApplicationIdentifier(httpHeaders);

		try {

			Utils.debugHeaders(log, httpHeaders);

			objResponse = this.fileDownloadController.downloadPart(
					applicationIdentifier, identifier, partNo);

		} catch (Exception e) {

			log.error("/support/resource/{filename}/multipart/{identifier}/{part}: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		}
	
		if (log.isDebugEnabled())
			log.debug("<<< downloadSupportFilePart");

		return ResponseEntity.ok(objResponse);
	}
	
}
