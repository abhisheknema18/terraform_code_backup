/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	30/07/2014
 * 
 * Modified by T Murray
 * FDP1165
 * 20/11/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult;

import java.util.List;
import java.util.Map;

/**
 * Interface for picking up script result files from a directory.
 */
public interface Pickup {

	/**
	 * Get a list of valid and invalid script result files from the specified
	 * directory,
	 * 
	 * @param dir
	 *            Directory to examine.
	 * 
	 * @return ScriptResultPickupFiles object populated with lists of valid and
	 *         invalid script result files in the directory specified.
	 */
	public PickupFiles getScriptResultPickupFiles(String dir);

	/**
	 * Get the number of files that were picked up during the last pickup cycle.
	 * This total includes invalid and valid files picked up.
	 * 
	 * @return The number of files picked up from the pickup directory during
	 *         the last pickup cycle.
	 */
	public int getLastPickupCount();

	/**
	 * Get a list of files with the .tmp extension in the specified directory.
	 * 
	 * @param dir
	 *            Directory to examine.
	 * 
	 * @return Map where the value is a list of URIs to files in the specified
	 *         pickup directory that have a .tmp extension. The Key of the map
	 *         is the name of the sub directory the files were found in. Key
	 *         will be null where the name of the directory the files were found
	 *         is the configured pickup folder.
	 */
	public Map<String, List<String>> getPickupTempFiles(String dir);

	/**
	 * Indicates if the specified filename conforms to the script result file
	 * naming convention.
	 * 
	 * @param filename
	 *            File name to check.
	 * 
	 * @return true if the given file name is a valid script result file name,
	 *         otherwise false.
	 */
	public boolean isValidScriptResultFilename(String filename);
}
