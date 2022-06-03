/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	07/08/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult.impl;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.ScriptVersions;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.xmlloader.exception.scriptresult.ValidationException;
import com.amtsybex.fieldreach.xmlloader.scriptresult.Validation;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Parser;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.ResultSet;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.ScriptResults;
import com.amtsybex.fieldreach.xmlloader.utils.XmlLoaderUtils;

/**
 * Class to perform the tasks associated with validating a script result file.
 */
public class ValidationImpl implements Validation {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory
			.getLogger(ValidationImpl.class.getName());

	private ScriptResultsService scriptResultsService;

	private ScriptService scriptService;

	private Schema scriptResultSchema;
	
	private Parser parser;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public ValidationImpl(
			ScriptResultsService scriptResultsService,
			ScriptService scriptService) throws SAXException {

		if (log.isDebugEnabled())
			log.debug(">>> ScriptResultValidationTaskImpl");

		this.scriptResultsService = scriptResultsService;
		this.scriptService = scriptService;

		this.scriptResultSchema = XmlLoaderUtils.getScriptResultSchema();

		this.parser = new Parser();
		
		if (log.isDebugEnabled())
			log.debug("<<< ScriptResultValidationTaskImpl");
	}

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	// FDP1165 TM 25/11/2015
	// Modified method so it parses and validates the XML. 
	// This is to prevent multiple parses on the same file.
	/*
	 * (non-Javadoc)
	 * @see com.amtsybex.fieldreach.xmlloader.scriptresult.Validation#parseAndValidateXml(java.lang.String)
	 */
	@Override
	public ResultSet parseAndValidateXml(String fileUri) throws ValidationException, UnsupportedEncodingException {

		if (log.isDebugEnabled())
			log.debug(">>> parseAndValidateXml fileUri=" + fileUri);

		ResultSet srXml = null;
	
		try {
			
			srXml= this.parser.parseJAXB(fileUri, scriptResultSchema);
			
		} catch (JAXBException e) {

			log.error("Error parsing/validating Script Result XML :\n"
					+ e.getMessage());

			throw new ValidationException(e);
		}
		
		if (log.isDebugEnabled())
			log.debug("<<< parseAndValidateXml");
		
		return srXml;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.scriptresult.standalone.
	 * ScriptResultValidation#alreadyLoaded(java.lang.String, java.lang.String)
	 */
	@Override
	public void previouslyLoaded(String frInstance, String fileName)
			throws FRInstanceException, ValidationException {

		if (log.isDebugEnabled())
			log.debug(">>> validateXml fileName=" + fileName);

		boolean loaded = false;

		try {

			loaded = scriptResultsService.resultPreviouslyLoaded(frInstance,
					fileName);

		} catch (Exception e) {

			String msg = "Error determining if result has been previously loaded:\n"
					+ e.getMessage();

			log.error(msg);
			throw new ValidationException(msg);
		}

		if (loaded)
			throw new ValidationException("Script result previously loaded: "
					+ fileName);

		if (log.isDebugEnabled())
			log.debug("<<< validateXml previouslyLoaded=" + loaded);
	}

	// FDP1165 TM 24/11/2015
	// Modfied method signature to accept parsed XML rather than URL to XML file.
	/*
	 * (non-Javadoc)
	 * @see com.amtsybex.fieldreach.xmlloader.scriptresult.Validation#validateScriptVersion(java.lang.String, com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.ResultSet)
	 */
	@Override
	public void validateScriptVersion(String frInstance, ResultSet srXml)
			throws FRInstanceException, ValidationException {

		if (log.isDebugEnabled())
			log.debug(">>> validateScriptVersion frInstance=" + frInstance + "srXml=XXX");

		// Get ScriptResults element from script result file.
		ScriptResults resultsElement = srXml.getScriptResults();

		// Extract required information
		ScriptVersions scriptVersion = null;
		int scriptCodeId = Integer.parseInt(resultsElement.getScriptCodeId());
		int versionNo = Integer.parseInt(resultsElement.getVersionNo());
		int scriptId = Integer.parseInt(resultsElement.getScriptId());
		int itemCount = Integer.parseInt(resultsElement.getItemCount());

		// Search for corresponding script version in Fieldreach database.
		List<ScriptVersions> versions = this.scriptService
				.findScriptVersions(frInstance, scriptCodeId);

		// Check to see if any versions exist for scriptCodeId
		if (versions == null)
			throw new ValidationException(
					"No corresponding script versions found in Fieldreach database");

		// Find matching version number
		for (ScriptVersions version : versions) {

			if (versionNo == version.getVersionNumber()) {
				scriptVersion = version;
				break;
			}
		}

		if (scriptVersion == null)
			throw new ValidationException(
					"Script version not found in Fieldreach database");

		// Ensure scriptIds match.
		if (scriptVersion.getId() != scriptId)
			throw new ValidationException("Version ScriptIds do not match");

		// Check file is not in development
		if (scriptVersion.getOnlineStatus() == 0
				&& scriptVersion.getOnlineDate() == null)
			throw new ValidationException("Script version in development");

		// Check item count.
		if (itemCount != scriptVersion.getItemCount())
			throw new ValidationException(
					"Version item counts do not match");

		if (log.isDebugEnabled())
			log.debug("<<< validateScriptVersion");
	}

}
