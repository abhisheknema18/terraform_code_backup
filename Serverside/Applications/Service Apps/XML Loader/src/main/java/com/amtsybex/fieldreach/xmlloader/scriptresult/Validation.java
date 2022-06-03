/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	07/08/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult;

import java.io.UnsupportedEncodingException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.xmlloader.exception.scriptresult.ValidationException;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.ResultSet;

/**
 * Interface for performing validation tasks of script result files.
 */
public interface Validation {

	// FDP1165 25/11/2015 TM - Modified method signature
	/**
	 * Parse the file specified and then validate it against the script result
	 * schema
	 * 
	 * @param fileUri
	 *            URI to the script result file to parse and validate
	 * 
	 * @return ResultSet object representing the file specified.
	 * 
	 * @throws ValidationException
	 * @throws UnsupportedEncodingException 
	 */
	public ResultSet parseAndValidateXml(String fileUri) throws ValidationException, UnsupportedEncodingException;

	/**
	 * Check to see if the script result has previously been loaded into the
	 * Fieldreach database.
	 * 
	 * @param frInstance
	 *            Instance of fieldreach to use when checking to see if the
	 *            result has already been loaded.
	 * 
	 * @param fileName
	 *            The name of the script result file to check.
	 * 
	 * @return True if a record exists in the ReturnedScripts table with a
	 *         filename matching that supplied, otherwise false.
	 *
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found.
	 * 
	 * @throws ValidationException
	 *             Any of the validation checks fail.
	 */
	public void previouslyLoaded(String frInstance, String fileName) throws FRInstanceException, ValidationException;

	// FDP1165 TM 24/11/2015
	// Modified method signature to accept parsed XMl rather than URL to xml
	// file.
	/**
	 * Validate the script result file supplied to ensure that a corresponding
	 * script version exists in the Fieldreach database, the version is not in
	 * development and the number of items the result contains matches the
	 * number expected.
	 * 
	 * @param frInstance
	 *            Instance of fieldreach to use when validating the script
	 *            version.
	 * 
	 * @param fileName
	 * 
	 * @param srXml
	 *            Parsed script result file contained in a ResultSet object
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found.
	 * 
	 * @throws ValidationException
	 *             Any of the validation checks fail.
	 */
	public void validateScriptVersion(String frInstance, ResultSet srXml)
			throws FRInstanceException, ValidationException;

}
