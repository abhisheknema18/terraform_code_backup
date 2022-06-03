/**
 * Author:  T Goodwin
 * Date:    01/07/2012
 * Project: FDE018
 * 
 * Copyright AMT-Sybex 2012
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Code Re-factoring
 */
package com.amtsybex.fieldreach.services.endpoint.common.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.services.endpoint.common.ReferenceOnlyFileController;
import com.amtsybex.fieldreach.services.resource.FileResourceController;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

public class ReferenceOnlyFileControllerImpl implements
		ReferenceOnlyFileController {

	private static Logger log = LoggerFactory.getLogger(ReferenceOnlyFileControllerImpl.class.getName());

	private FileResourceController fileResourceController;

	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.ReferenceOnlyFileController
	 * #getReferenceOnlyFileContents(java.lang.String, java.lang.String)
	 */
	@Override
	public String getReferenceOnlyFileContents(String referenceOnlyFileName,
			String applicationIdentifier) throws ResourceTypeNotFoundException,
			ResourceNotFoundException {

		if(log.isDebugEnabled()) {
			
			log.debug(">>> getReferenceOnlyFileContents referenceOnlyFileName="
					+ Common.CRLFEscapeString(referenceOnlyFileName) + " applicationIdentifier="
					+ Common.CRLFEscapeString(applicationIdentifier));
		}
		
		String retval = null;

		retval = getFileResourceController().getBase64Content(
				referenceOnlyFileName, Utils.REFERENCEONLY_FILE_TYPE,
				applicationIdentifier);
		
		if(log.isDebugEnabled())
			log.debug("<<< getReferenceOnlyFileContents");

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.ReferenceOnlyFileController
	 * #referenceOnlyFileExists(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean referenceOnlyFileExists(String referenceOnlyFileName,
			String applicationIdentifier) {

		if(log.isDebugEnabled()) {
			
			log.debug(">>> referenceOnlyFileExists referenceOnlyFileName="
					+ Common.CRLFEscapeString(referenceOnlyFileName) + " applicationIdentifier="
					+ Common.CRLFEscapeString(applicationIdentifier));
		}

		boolean retval = true;

		try {

			getFileResourceController().getURI(referenceOnlyFileName,
					Utils.REFERENCEONLY_FILE_TYPE, applicationIdentifier);

		} catch (ResourceTypeNotFoundException e) {

			retval = false;

		} catch (ResourceNotFoundException e) {

			retval = false;
		}

		if(log.isDebugEnabled())
			log.debug("<<< referenceOnlyFileExists retval=" + retval);

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.ReferenceOnlyFileController
	 * #referenceOnlyFileChecksum(java.lang.String, java.lang.String)
	 */
	@Override
	public String referenceOnlyFileChecksum(String referenceOnlyFileName,
			String applicationIdentifier) {

		if(log.isDebugEnabled()) {
			
			log.debug(">>> referenceOnlyFileChecksum referenceOnlyFileFileName="
					+ Common.CRLFEscapeString(referenceOnlyFileName) + " applicationIdentifier="
					+ Common.CRLFEscapeString(applicationIdentifier));
		}

		String retval = null;

		try {

			retval = getFileResourceController().getMd5Checksum(
					referenceOnlyFileName, Utils.REFERENCEONLY_FILE_TYPE,
					applicationIdentifier);

		} catch (ResourceTypeNotFoundException e) {

			log.error("Resource not found " + referenceOnlyFileName);

		} catch (ResourceNotFoundException e) {

			log.error("(Resource type not found "
					+ Utils.REFERENCEONLY_FILE_TYPE);
		}

		if(log.isDebugEnabled())
			log.debug("<<< referenceOnlyFileChecksum retval=" + retval);

		return retval;
	}

	/*-------------------------------------------
	 - Spring Injection Methods
	 --------------------------------------------*/

	public FileResourceController getFileResourceController() {

		return fileResourceController;
	}

	public void setFileResourceController(
			FileResourceController fileResourceController) {

		this.fileResourceController = fileResourceController;
	}

}
