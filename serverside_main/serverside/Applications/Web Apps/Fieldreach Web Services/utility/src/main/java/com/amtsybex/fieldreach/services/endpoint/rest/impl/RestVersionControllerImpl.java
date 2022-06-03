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

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amtsybex.fieldreach.services.endpoint.rest.RestVersionController;

@Controller
public class RestVersionControllerImpl implements RestVersionController {

	private static Logger log = Logger.getLogger(RestVersionControllerImpl.class
			.getName());

	@Autowired
	ServletContext servletContext;
	
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
	@RequestMapping(value = "/version", method = RequestMethod.GET)
	@ResponseBody
	public String versionNo() {

		if (log.isDebugEnabled())
			log.debug(">>> versionNo");

		String retval = "";
        
		try {
			
			InputStream inputStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF");
			Manifest manifest = new Manifest(inputStream);
			Attributes mainAttribs = manifest.getMainAttributes();
	        String version = mainAttribs.getValue("Implementation-Version");
	        
	        if(version != null) 
	        	retval =  version;
	        
		} catch (IOException e) {

			retval = "error";
			log.error("Exception" + e.getMessage());
			
		}

		if (log.isDebugEnabled())
			log.debug("<<< versionNo retval=" + retval);

		return retval;
	}

}