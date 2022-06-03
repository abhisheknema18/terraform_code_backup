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

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.amtsybex.fieldreach.services.endpoint.rest.BaseController;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.resource.exception.NotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;

/**
 * Base class to handle all HTTP status errors thrown by all
 * classes contained in this package. the aim of this class is
 * to promote code reuse.
 */
public class BaseControllerImpl implements BaseController {

	private static Logger log = Logger.getLogger(BaseControllerImpl.class.getName());

	@Autowired
	protected XStreamMarshaller restApiMarshaller;
		
	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.BaseController#
	 * handleBadRequestException
	 * (com.amtsybex.fieldreach.services.authentication.
	 * exception.BadRequestException, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String handleBadRequestException(BadRequestException e,
			HttpServletRequest request) {

		if (log.isDebugEnabled()) {
			
			log.debug(">>> handleBadRequestException message=" + e.getMessage());

			log.debug("<<< handleBadRequestException");
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.BaseController#handleException
	 * (java.lang.Exception, javax.servlet.http.HttpServletRequest,
	 * java.io.Writer)
	 */
	@Override
	@ExceptionHandler(Throwable.class)
	@ResponseBody
	public void handleException(final Exception e,
			final HttpServletRequest request, Writer writer) {

		log.debug(">>> handleException e=XXX request=XXX writer=XXX");

		try {

			writer.write(String
					.format("<CallResponse>"
							+ "<success>false</success>"
							+ "<error>"
							+ "	<errorCode>"
							+ Utils.GENERAL_EXCEPTION
							+ "</errorCode>"
							+ "	<errorDescription>java.class:%s, message:%s</errorDescription>"
							+ "</error>" + "</CallResponse>", e.getClass(),
							e.getMessage()));

		} catch (IOException e1) {

			log.error("Error writing exception details for exception type: "
					+ Utils.GENERAL_EXCEPTION);

			log.error(e1.toString());
		}

		log.debug("<<< handleException");
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.BaseController#
	 * handleNotFoundException
	 * (com.amtsybex.fieldreach.services.resource.exception.NotFoundException,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String handleNotFoundException(NotFoundException e,
			HttpServletRequest request) {

		if (log.isDebugEnabled()) {
			
			log.debug(">>> handleNotFoundException message=" + e.getMessage());

			log.debug("<<< handleNotFoundException");
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.BaseController#
	 * handleTypeMismatchException
	 * (org.springframework.beans.TypeMismatchException,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@ExceptionHandler(TypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String handleTypeMismatchException(TypeMismatchException e,
			HttpServletRequest request) {

		if (log.isDebugEnabled()) {
			
			log.debug(">>> handleBadRequestException message=" + e.getMessage());

			log.debug("<<< handleBadRequestException");
		}

		return null;
	}

	/*-------------------------------------------
	 - Protected Methods
	 --------------------------------------------*/
	
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
	
}
