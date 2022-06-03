/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	07/08/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.exception.ConfigException;
import com.amtsybex.fieldreach.xmlloader.exception.scriptresult.LoadScriptResultException;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.ResultSet;

/**
 * Interface for loading script result files into the Fieldreach database.
 */
public interface Load {

	// FDP1165 TM 24/11/2015 - Modified method signature.
	/**
	 * Parse the specified script result XML file and load it into the
	 * Fieldreach database.
	 * 
	 * Assumption is made that the file has already been validated prior to
	 * loading via the ScriptResultValidation class.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to load the script result into.
	 * 
	 * @param srXml
	 *            Parsed script result file to be loaded.
	 * 
	 * @param fileUri
	 *            URL ofthe script result file.
	 *            
	 * @throws FRInstanceException
	 *             Specified Fieldreach instance has not been configured.
	 */
	public void loadScriptResult(String frInstance, ResultSet srXml, String fileUri)
			throws FRInstanceException, LoadScriptResultException;

	/**
	 * Set a flag indicating if script results should be saved to the Fieldreach 
	 * database whenever a script result is loaded.
	 * 
	 * @param dbImport
	 * True if script results should be saved t the database, otherwise false;
	 */
	public void setScriptResultDbImport(boolean dbImport);
	
	// FDE037 TM 29/02/2016
	
	/**
	 * Set the directory that the binary data of a GEOMOETRY question response will be stored.
	 * 
	 * @param dir
	 * The directory to store the GEOMETRY binary data.
	 * 
	 * @throws ConfigException
	 * The directory does not exist.
	 */
	public void setGeometryDataDir(String dir) throws ConfigException;
	
	// End FDE037
}