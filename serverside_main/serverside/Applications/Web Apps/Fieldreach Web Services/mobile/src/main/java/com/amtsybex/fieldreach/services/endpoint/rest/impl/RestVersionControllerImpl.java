/**
 * Author:  T Murray
 * Date:    05/11/2013
 * Project: FDP997
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Removal of /v1 namespace from web service end points 
 * and code re-factoring.
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import com.amtsybex.fieldreach.services.endpoint.rest.RestVersionController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(tags = "Info")
public class RestVersionControllerImpl implements RestVersionController {

	private static final Logger log = LoggerFactory.getLogger(RestVersionControllerImpl.class
			.getName());

	@Value("${info.app.version?:'DEV'}") 
	String version;
	
	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestVersionController#
	 * versionNo()
	 */
	@Override
	@Deprecated
	@GetMapping(value = "/version")
	@ApiOperation(value = "Instance Version", notes = "This web service will return version of the FieldSmart instance")
	@ResponseBody
	public ResponseEntity<String> versionNo() {

		if (log.isDebugEnabled())
			log.debug(">>> << versionNo retval=" + this.version);

		String retval = "";
		
		if(this.version != null) {
			retval = this.version;
		}

		if (log.isDebugEnabled())
			log.debug("<<< versionNo retval=" + retval);
		
		return ResponseEntity.ok(retval);
	}

}