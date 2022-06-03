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

public interface BaseController {

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
