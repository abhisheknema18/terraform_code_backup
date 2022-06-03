/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Removal of /v1 namespace from web service end points 
 * and code re-factoring.
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amtsybex.fieldreach.services.endpoint.rest.RestTimeController;

@Controller
public class RestTimeControllerImpl implements RestTimeController {

	private static Logger log = Logger
			.getLogger(RestTimeControllerImpl.class.getName());

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestTimeController#serverTime
	 * ()
	 */
	@Override
	@RequestMapping(value = "/time", method = RequestMethod.GET)
	@ResponseBody
	public String serverTime() {

		if (log.isDebugEnabled())
			log.debug(">>> serverTime");

		String retval = org.apache.commons.httpclient.util.DateUtil
				.formatDate(new Date());

		if (log.isDebugEnabled())
			log.debug("<<< serverTime time=" + retval);

		return retval;
	}

}
