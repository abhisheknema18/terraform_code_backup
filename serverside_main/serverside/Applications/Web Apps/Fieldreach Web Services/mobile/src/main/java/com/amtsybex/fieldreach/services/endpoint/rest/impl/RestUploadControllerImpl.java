/**
 * Author:  T Murray
 * Date:    24/02/2016
 * Project: FDE037
 * 
 * Copyright AMT-Sybex 2016
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse.APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse.APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.UploadPartResponse.APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.UploadPartResponse.APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_JSON;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.services.endpoint.rest.RestUploadController;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.request.UploadInitiate;
import com.amtsybex.fieldreach.services.messages.request.UploadPart;
import com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse;
import com.amtsybex.fieldreach.services.messages.response.UploadPartResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.upload.UploadController;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api(tags = "File Upload")
public class RestUploadControllerImpl extends BaseControllerImpl implements RestUploadController {

	private static final Logger log = LoggerFactory.getLogger(RestUploadControllerImpl.class.getName());
	
	@Autowired
	private UploadController fileUploadController;

	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestUploadController#
	 * initiateUpload(javax.servlet.http.HttpServletRequest, java.lang.String,
	 * java.lang.String)
	 */
	@PostMapping(value = "/files/{filename:.*}/multipart",
	        consumes = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_JSON, APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_XML},
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_JSON, APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_XML})
	@ApiOperation(value = "Initiate File Upload", 
			notes = "Clients must initiate a multipart upload, indicating the resultant file name and total size. " +
					"The web service returns a unique identifier which the client must use to reference subsequent calls to upload the data. " +
					"The client is also returned the maximum size of any chunk to be uploaded in bytes." +
					"\n" +
					"The web service may impose a limit on the size of file uploads (via the app.properties parameter file.upload.maxUploadSize), " +
					"so the initiation process may fail if the client indicates a size greater than that limit. " +
					"The web service imposes a maximum chunk size limit in bytes for each part uploaded. " +
					"If the web service determines that this file has already been partially uploaded, " +
					"then it will indicate which part the client should start uploading from")
	@ResponseBody
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "OK - Possible error codes include FRInstanceException, EXCEPTION(General Exception)"),	
		@ApiResponse(code = 400, message = "Bad Request - Malformed or missing XML in the Request body."),
})
	public ResponseEntity<UploadInitiateResponse> initiateUpload(@RequestHeader HttpHeaders httpHeaders, @PathVariable("filename") String fileName,
																 @RequestBody(required = true) UploadInitiate uploadInitiate) throws BadRequestException {

		if (log.isDebugEnabled())
			log.debug(">>> initiateUpload fileName=" + Common.CRLFEscapeString(fileName));

		UploadInitiateResponse uploadInitiateResponse = new UploadInitiateResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		uploadInitiateResponse.setError(errorMessage);

		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers
			Utils.debugHeaders(log, httpHeaders);
			if (log.isDebugEnabled())
				log.debug("Body :" + uploadInitiate.toString());

			// Validate initiate message. BadRequestException will be thrown if
			// validation fails.
			this.validateUploadInitiateBody(uploadInitiate);

			// Initiate the upload and get a response
			uploadInitiateResponse = fileUploadController.initiate(applicationIdentifier,
					uploadInitiate.getIdentifier(), uploadInitiate.getFileName(), uploadInitiate.getTotalSizeBytes());

		} catch (BadRequestException e) {

			// In case of BadRequestException failure.. rethrow for handler to
			// set.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception ex) {

			log.error("Exception in /files/{filename}/multipart: " + ex.getMessage() + Utils.getStackTrace(ex));

			uploadInitiateResponse.getError().setErrorCode(Utils.GENERAL_EXCEPTION);
			uploadInitiateResponse.getError().setErrorDescription(ex.getMessage());
		} finally {

			if (errorMessage.getErrorCode() != null)
				uploadInitiateResponse.setSuccess(false);

		}

		if (log.isDebugEnabled())
			log.debug("<<< initiateUpload response=" + uploadInitiateResponse.toString());

		return ResponseEntity.ok(uploadInitiateResponse);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestUploadController#
	 * uploadPart(javax.servlet.http.HttpServletRequest, java.lang.String,
	 * java.lang.String)
	 */
	@PutMapping(value = "/files/{filename:.*}/multipart/{identifier}",
	        consumes = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_JSON, APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_XML},
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_JSON, APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_XML})
	@ApiOperation(value = "Upload File Part", 
			notes = "After initiating a file upload the client must upload the file in parts sequentially. " +
					"As the client uploads the file parts, the web service checks that the file is not exceeding its size or the maximum allowed size. " +
					"Partially uploaded files will be stored on disk in a temporary directory and moved to the required directory when complete. " +
					"The client must pass an MD5 checksum with each part upload so that the web service can verify the content." +
					"\n" +
					"The upload service allows for the restarting of an upload file through the initiate call. " +
					"If the web service determines that the file being uploaded has already been partially uploaded, " +
					"then the client will be informed which part of the file is required next")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include FRInstanceException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - Malformed or missing XML in the Request body."),
	})
	public ResponseEntity<UploadPartResponse> uploadPart(@RequestHeader HttpHeaders httpHeaders, @PathVariable("identifier") String identifier,
														 @RequestBody(required = true) UploadPart uploadPart) throws BadRequestException {

		if (log.isDebugEnabled())
			log.debug(">>> uploadPart identifer=" + Common.CRLFEscapeString(identifier));

		UploadPartResponse uploadPartResponse = new UploadPartResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		uploadPartResponse.setError(errorMessage);

		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers
			Utils.debugHeaders(log, httpHeaders);
			
			// Validate upload part. BadRequestException will be thrown if
			// validation fails.
			this.validateUploadPartBody(uploadPart);

			// Call to process the upload part, returning an UploadPartResponse
			// object.
			uploadPartResponse = fileUploadController.receivePart(applicationIdentifier,
					uploadPart.getIdentifier(), uploadPart.getPartNumber().intValue(), uploadPart.getDataLength(),
					uploadPart.getChecksum(), uploadPart.getData());

		} catch (BadRequestException e) {

			// In case of BadRequestException failure.. rethrow for handler to set.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception ex) {

			log.error("Exception in /files/{filename}/multipart/{identifier}: " + ex.getMessage());
			uploadPartResponse.getError().setErrorCode(Utils.GENERAL_EXCEPTION);
			uploadPartResponse.getError().setErrorDescription(ex.getMessage());
			
		} finally {

			if (errorMessage.getErrorCode() != null)
				uploadPartResponse.setSuccess(false);

		}

		if (log.isDebugEnabled())
			log.debug("<<< uploadPart response=" + uploadPartResponse.toString());

		return ResponseEntity.ok(uploadPartResponse);
	}

}
