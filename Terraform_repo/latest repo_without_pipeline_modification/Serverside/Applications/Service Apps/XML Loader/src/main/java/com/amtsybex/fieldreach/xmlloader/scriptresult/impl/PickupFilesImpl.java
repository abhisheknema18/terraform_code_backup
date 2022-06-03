/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	31/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.amtsybex.fieldreach.xmlloader.scriptresult.PickupFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supporting class to perform the task of picking up script result files from
 * the file system.
 */
public class PickupFilesImpl implements PickupFiles {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory
			.getLogger(PickupFilesImpl.class.getName());

	private Map<String, List<File>> validFiles;
	private Map<String, List<File>> invalidFiles;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	/**
	 * Create an instance of the class with the supplied Maps of valid and
	 * invalid script result files.
	 * 
	 * @param validFiles
	 *            Map where the value is a list of File objects whose name
	 *            follows the script result naming convention. The Key of the
	 *            map is the name of the sub directory the files were found in.
	 *            Key will be null where the name of the directory the files
	 *            were found is the configured pickup folder.
	 * 
	 * @param invalidFiles
	 *            Map where the value is a list of File objects whose name does
	 *            not follow the script result naming convention. The Key of the
	 *            map is the name of the sub directory the files were found in.
	 *            Key will be null where the name of the directory the files
	 *            were found is the configured pickup folder.
	 */
	public PickupFilesImpl(Map<String, List<File>> validFiles,
			Map<String, List<File>> invalidFiles) {

		if (log.isDebugEnabled())
			log.debug(">>> ScriptResultPickupFiles validFiles=xxx invalidFiles=xxx");

		this.validFiles = validFiles;
		this.invalidFiles = invalidFiles;

		if (log.isDebugEnabled())
			log.debug("<<< ScriptResultPickupFiles");
	}

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.scriptresult.standalone.
	 * ScriptResultPickupFiles#getValidFiles()
	 */
	@Override
	public Map<String, List<File>> getValidFiles() {

		if (log.isDebugEnabled()) {

			log.debug(">>> getValidFiles");
			log.debug("<<< getValidFiles");
		}

		return this.validFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.scriptresult.standalone.
	 * ScriptResultPickupFiles#getInvalidFiles()
	 */
	@Override
	public Map<String, List<File>> getInvalidFiles() {

		if (log.isDebugEnabled()) {

			log.debug(">>> getInvalidFiles");
			log.debug("<<< getInvalidFiles");
		}

		return this.invalidFiles;
	}

}
