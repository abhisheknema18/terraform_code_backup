/**
 * Author:  T Murray
 * Date:    17/01/2012
 * Project: FDE016
 * 
 * Author:  T Goodwin
 * Date:    10/07/2012
 * Project: FDE018
 * Description : Add means to extract application identifier from request headers.
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Removal of /v1 namespace from web service end points 
 * and code refactoring.
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.services.endpoint.rest.BaseController;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.request.UploadInitiate;
import com.amtsybex.fieldreach.services.utils.Utils;

/**
 * Base class to handle all HTTP status errors thrown by all
 * classes contained in this package. the aim of this class is
 * to promote code reuse.
 */
public class BaseControllerImpl implements BaseController {

	private static final Logger log = LoggerFactory.getLogger(BaseControllerImpl.class.getName());

	@Autowired
	protected XStreamMarshaller restApiMarshaller;

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.BaseController#
	 * handleFRInstanceException(java.lang.Exception,
	 * javax.servlet.http.HttpServletRequest, java.io.Writer)
	 */
	@Override
	@ExceptionHandler(FRInstanceException.class)
	@ResponseBody
	public void handleFRInstanceException(final Exception e,
			final HttpServletRequest request, Writer writer) {

		log.debug(">>> handleFRInstanceException e=XXX");

		try {

			writer.write(String
					.format("<CallResponse>"
							+ "<success>false</success>"
							+ "<error>"
							+ "	<errorCode>"
							+ Utils.FR_INSTANCE_EXCEPTION
							+ "</errorCode>"
							+ "	<errorDescription>java.class:%s, message:%s</errorDescription>"
							+ "</error>" + "</CallResponse>", e.getClass(),
							e.getMessage()));
		} catch (IOException e1) {

			log.error("Error writing exception details for exception type: "
					+ Utils.GENERAL_EXCEPTION);
			log.error(e1.toString());
		}

		log.debug("<<< handleFRInstanceException");
	}

	/*-------------------------------------------
	 - Protected Methods
	 --------------------------------------------*/

	// FDE026 06/06/2014 TM

	// New function to validate initiate upload request bodies. This is designed
	// to promote code reuse amongst end points that leverage the multi part
	// upload mechanism.

	/**
	 * Validate the supplied UploadInitiate object supplied.
	 * 
	 * @param uploadInitiate
	 * UploadInitiate object representing the body of an initiate upload request.
	 * 
	 * @throws BadRequestException
	 * Filename is empty, or totalSizeBytes <= 0.
	 */
	protected void validateUploadInitiateBody(UploadInitiate uploadInitiate)
			throws BadRequestException {

		if (log.isDebugEnabled()) {

			log.debug(">>> validateUploadInitiateBody uploadInitiate=XXX");

			log.debug("Upload Initiate totalSize="
					+ uploadInitiate.getTotalSizeBytes() + " Mime="
					+ uploadInitiate.getMimeType() + " fileName="
					+ uploadInitiate.getFileName() + " identifier="
					+ uploadInitiate.getIdentifier());
		}

		// Verify required elements exists in the parsed XML input
		if (uploadInitiate.getFileName() == null
				|| uploadInitiate.getFileName().trim().equals("")) {

			throw new BadRequestException("Initiate file name not set");
		}

		if (uploadInitiate.getTotalSizeBytes() <= 0)
			throw new BadRequestException("Initiate total size <= 0");
		
		log.debug("<<< validateUploadInitiateBody");
	}

	// New function to validate upload part request bodies. This is designed to
	// promote code reuse amongst end points that leverage the multi part
	// upload mechanism.

	/**
	 * Validates the supplied UploadPart object.
	 * 
	 * @param uploadPart
	 * UploadPart headers representing the body of an initiate part request. 
	 * 
	 * @throws BadRequestException
	 *             Upload Identifier has not been supplied or is blank. Upload
	 *             part number is not supplied or is <= 0. Checksum has not been
	 *             supplied or is blank. Upload data length is <= 0. Upload part
	 */
	protected void validateUploadPartHeader(String partIdentifier, BigInteger partNumber,Long partLength, String partChecksum)
			throws BadRequestException {

		if (log.isDebugEnabled()) {

			log.debug(">>> validateUploadPartBody uploadPart=XXX");

			log.debug("Upload part identifier=" + partIdentifier
					+ " Upload part checksum=" + partChecksum
					+ " Upload part partNumber=" + partNumber
					+ " Upload part dataLength=" + partLength);

		}

		// Verify that required information is present
		if (partIdentifier == null
				|| partIdentifier.trim().equals("")) {

			throw new BadRequestException("Upload Part Identifier not passed");
		}

		if (partNumber == null
				|| partNumber.intValue() <= 0) {

			throw new BadRequestException("Upload Part number not valid");
		}

		if (partChecksum == null
				|| partChecksum.trim().equals("")) {

			throw new BadRequestException("Upload Part Checksum not valid");
		}

		if (partLength <= 0)
			throw new BadRequestException("Upload Part Length invalid");

		log.debug("<<< validateUploadPartBody");
	}

	// End FDE026
	
	// FDE034 TM 18/08/2015
	
	/**
	 * Method to Unmarshall the object supplied and convert it to a string.
	 * 
	 * @param responseObject
	 * Object to be unmarshalled and converted to a string
	 * 
	 * @return
	 * String represnetation of the unmarshalled responseObject supplied.
	 * 
	 */
	protected String buildResponseString(Object responseObject) {
		
		log.debug(">>> buildResponseString responseObject=XXX");
		
		String response = "";
		StringWriter ioStream = new StringWriter();

		try {

			restApiMarshaller.marshal(responseObject, new StreamResult(ioStream));

			response = ioStream.getBuffer().toString();
			
			ioStream.close();
			
		} catch (XmlMappingException e) {

			log.error("Mapping Exception in buildResponseString: " + e.getMessage());

		} catch (IOException e) {

			log.error("IOException in buildResponseString: " + e.getMessage());
		}
		
		log.debug("<<< buildResponseString");
		
		return response;
	}
	
	public SystemUsers getSystemUserFromUserPrincipal() throws FRInstanceException {

		if (log.isDebugEnabled())
		log.debug(">>> getSystemUserFromUserPrincipal");

		SystemUsers retVal = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		retVal =  (SystemUsers) authentication.getPrincipal();

		if (log.isDebugEnabled())
		log.debug("<<< getSystemUserFromUserPrincipal");

		return retVal;
	}

}
