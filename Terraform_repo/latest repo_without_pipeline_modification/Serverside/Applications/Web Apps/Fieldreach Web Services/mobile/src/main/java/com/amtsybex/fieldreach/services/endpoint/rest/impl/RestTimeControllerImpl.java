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

import com.amtsybex.fieldreach.services.endpoint.rest.RestTimeController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@Api(tags = "Info")
public class RestTimeControllerImpl implements RestTimeController {

	private static final Logger log = LoggerFactory.getLogger(RestTimeControllerImpl.class.getName());

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
	@Deprecated
	@GetMapping(value = "/time")
	@ApiOperation(value = "Server Time", notes = "Services returning time of the server")
	@ResponseBody
	public ResponseEntity<String> serverTime() {

		if (log.isDebugEnabled())
			log.debug(">>> serverTime");

		String retval = org.apache.http.client.utils.DateUtils.formatDate(new Date());
		
		if (log.isDebugEnabled())
			log.debug("<<< serverTime time=" + retval);

		return ResponseEntity.ok(retval);
	}

}
