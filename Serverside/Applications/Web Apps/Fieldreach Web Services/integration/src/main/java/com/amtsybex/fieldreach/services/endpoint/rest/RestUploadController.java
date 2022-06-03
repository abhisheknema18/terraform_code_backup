package com.amtsybex.fieldreach.services.endpoint.rest;

import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.request.UploadInitiate;
import com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse;
import com.amtsybex.fieldreach.services.messages.response.UploadPartResponse;

public interface RestUploadController {
	
	
	/**
	 * Initiate the upload of a file.
	 * 
	 * @param httpHeaders
	 *            http Headers
	 * 
	 * @param fileName
	 *            Name of the support file to be uploaded.
	 * 
	 * @param uploadInitiate
	 *            UploadInitiate message with details of the file to be
	 *            uploaded.
	 * 
	 * @return UploadInitiateResponseMessage populated with details of the
	 *         upload.
	 * 
	 * 
	 *             If invalid Authorization token is passed in the request.
	 * 
	 * @throws BadRequestException
	 *             If no request body is supplied, if the request body could not
	 *             be parsed or if the file to be uploaded is <=0 bytes in size.
	 */
	public ResponseEntity<UploadInitiateResponse> initiateUploadInt(@RequestHeader HttpHeaders httpHeaders, @PathVariable("filename") String fileName,
			 @RequestBody UploadInitiate uploadInitiate) throws BadRequestException;
	
	
	/**
	 * Upload the specified part of the file referenced by the upload identifier
	 * supplied.
	 * 
	 * @param httpHeaders
	 *            http Headers
	 *            
	 * @param String partNumber
	 * 
	 * @param String partLength
	 * 
	 * @param String partChecksum
	 *            
	 * @param identifier
	 *            Identifier for the current upload part.
	 * 
	 * @param uploadPart
	 *            UploadPart message containing the part number, checksum and
	 *            data.
	 * 
	 * @return UploadPartResponse message indicating success or failure reason
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	ResponseEntity<UploadPartResponse> uploadPartOctet(HttpHeaders httpHeaders, BigInteger partNumber, Long partLength,
			String partChecksum, HttpServletRequest request, String identifier) throws BadRequestException;

}
