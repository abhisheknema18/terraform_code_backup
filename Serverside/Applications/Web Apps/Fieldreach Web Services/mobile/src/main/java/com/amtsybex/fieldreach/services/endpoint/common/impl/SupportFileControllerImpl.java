/**
 * Author:  T Goodwin
 * Date:    01/07/2012
 * Project: FDE018
 * 
 * Copyright AMT-Sybex 2015
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Code Re-factoring
 * 
 */
package com.amtsybex.fieldreach.services.endpoint.common.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.services.endpoint.common.SupportFileController;
import com.amtsybex.fieldreach.services.resource.FileResourceController;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

public class SupportFileControllerImpl implements SupportFileController {

	private static Logger log = LoggerFactory.getLogger(SupportFileControllerImpl.class.getName());

	private FileResourceController fileResourceController;

	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.SupportFileController
	 * #getSupportFileContents(java.lang.String, java.lang.String)
	 */
	@Override
	public String getSupportFileContents(String supportFileName,
			String applicationIdentifier) throws ResourceTypeNotFoundException,
			ResourceNotFoundException {

		if (log.isDebugEnabled()) {
			
			log.debug(">>> getSupportFileContents supportFileName="
					+ supportFileName + " applicationIdentifier="
					+ applicationIdentifier);
		}

		String retval = null;

		retval = getFileResourceController().getBase64Content(supportFileName,
				Utils.SUPPORT_FILE_TYPE, applicationIdentifier);

		if (log.isDebugEnabled())
			log.debug("<<< getSupportFileContents");

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.SupportFileController
	 * #supportFileExists(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean supportFileExists(String supportFileName,
			String applicationIdentifier) {

		if (log.isDebugEnabled()) {
			
			log.debug(">>> supportFileExists supportFileName=" + supportFileName
					+ " applicationIdentifier=" + applicationIdentifier);
		}

		boolean retval = true;

		try {

			getFileResourceController().getURI(supportFileName,
					Utils.SUPPORT_FILE_TYPE, applicationIdentifier);
			
		} catch (ResourceTypeNotFoundException e) {

			retval = false;
			
		} catch (ResourceNotFoundException e) {

			retval = false;
		}

		if (log.isDebugEnabled())
			log.debug("<<< supportFileExists retval=" + retval);

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.SupportFileController
	 * #supportFileChecksum(java.lang.String, java.lang.String)
	 */
	@Override
	public String supportFileChecksum(String supportFileName,
			String applicationIdentifier) {

		if (log.isDebugEnabled()) {
			
			log.debug(">>> supportFileChecksum supportFileFileName="
					+ Common.CRLFEscapeString(supportFileName) + " applicationIdentifier="
					+ Common.CRLFEscapeString(applicationIdentifier));
		}

		String retval = null;

		try {

			retval = getFileResourceController().getMd5Checksum(
					supportFileName, Utils.SUPPORT_FILE_TYPE,
					applicationIdentifier);
			
		} catch (ResourceTypeNotFoundException e) {

			log.error("Resource not found " + supportFileName);
			
		} catch (ResourceNotFoundException e) {

			log.error("(Resource type not found " + Utils.SUPPORT_FILE_TYPE);
		}

		if (log.isDebugEnabled())
			log.debug("<<< supportFileChecksum retval=" + retval);

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.SupportFileController
	 * #supportFileListing(java.lang.String)
	 */
	@Override
	public List<String> supportFileListing(String applicationIdentifier)
			throws ResourceTypeNotFoundException {

		if (log.isDebugEnabled()) {
			
			log.debug(">>> supportFileListing applicationIdentifier="
					+ Common.CRLFEscapeString(applicationIdentifier));
	
			log.debug("<<< supportFileListing");
		}

		return getFileResourceController().getListing(Utils.SUPPORT_FILE_TYPE,
				applicationIdentifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.SupportFileController
	 * #getSupportFileDir(java.lang.String)
	 */
	public String getSupportFileDir(String applicationIdentifier)
			throws ResourceTypeNotFoundException {

		if (log.isDebugEnabled()) {
			
			log.debug(">>> getSupportFileDir applicationIdentifier="
					+ Common.CRLFEscapeString(applicationIdentifier));
		}

		String dir = this.fileResourceController.getFileTypeDir(
				Utils.SUPPORT_FILE_TYPE, applicationIdentifier);

		if (log.isDebugEnabled())
			log.debug("<<< getSupportFileDir");

		return dir;
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
