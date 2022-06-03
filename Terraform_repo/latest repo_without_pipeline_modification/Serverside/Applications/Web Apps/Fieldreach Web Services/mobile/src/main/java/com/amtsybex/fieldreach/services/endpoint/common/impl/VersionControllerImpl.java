/**
 * Author:  T Murray
 * Date:    05/11/2013
 * Project: FDP997
 * 
 * Copyright AMT-Sybex 2015
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Code Re-factoring
 */
package com.amtsybex.fieldreach.services.endpoint.common.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.services.endpoint.common.VersionController;

public class VersionControllerImpl implements VersionController {

	private static Logger log = LoggerFactory.getLogger(VersionControllerImpl.class
			.getName());

	private String versionNo;

	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.VersionController#getVersion
	 * ()
	 */
	@Override
	public String getVersion() {
		
		if (log.isDebugEnabled()) {
			
			log.debug(">>> getVersion");
			
			log.debug("<<< getVersion retval = " + versionNo);
		}
		
		return versionNo;
	}

	/*-------------------------------------------
	 - Spring Injection Methods
	 --------------------------------------------*/

	public void setVersionNo(String versionNo) {

		if (log.isDebugEnabled())
			log.debug(">>> setVersionNo");

		this.versionNo = versionNo;

		if (log.isDebugEnabled())
			log.debug("<<< setVersionNo");
	}

}
