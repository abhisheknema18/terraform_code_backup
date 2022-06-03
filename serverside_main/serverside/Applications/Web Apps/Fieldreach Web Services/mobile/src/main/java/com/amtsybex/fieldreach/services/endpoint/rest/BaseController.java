/**
 * Author:  T Murray
 * Date:    17/00/2012
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
package com.amtsybex.fieldreach.services.endpoint.rest;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.amtsybex.fieldreach.services.resource.exception.NotFoundException;

public interface BaseController {


	/**
	 * Catch errors when trying to find files that do not exist on the
	 * web service. In such cases the HTTP status response code is set to 404 Not
	 * Found.
	 * 
	 * @param e
	 * Original exception thrown elsewhere.
	 * 
	 * @param request
	 * HttpServletRequest the exception originated from
	 * 
	 * @return null
	 */
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNotFoundException(NotFoundException e,
			HttpServletRequest request);

	/**
	 * If an un-handled FRInstance extraction is thrown handle by returning a
	 * call response object with appropriate exception.
	 * 
	 * Catch and handle FRInstanceException not handled elsewhere. A CallResponse
	 * message is returned with success=false and the error code and description
	 * reflect the exception caught.
	 * 
	 * @param e
	 * Original exception thrown elsewhere.
	 * 
	 * @param request
	 * HttpServletRequest the exception originated from
	 * 
	 * @param writer
	 * Writer object to write response to.
	 */
	public void handleFRInstanceException(final Exception e,
			final HttpServletRequest request, Writer writer);


	
}
