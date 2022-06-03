/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	31/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface PickupFiles {

	/**
	 * Get the map of valid script result files held by the class.
	 * 
	 * @return Map where the value is a list of File objects whose name follows
	 *         the script result naming convention. The Key of the map is the
	 *         name of the sub directory the files were found in. Key will be
	 *         null where the name of the directory the files were found is the
	 *         configured pickup folder.
	 */
	public Map<String, List<File>> getValidFiles();

	/**
	 * Get the map of invalid script result files held by the class.
	 * 
	 * @return Map where the value is a list of File objects whose name does not
	 *         follow the script result naming convention. The Key of the map is
	 *         the name of the sub directory the files were found in. Key will
	 *         be null where the name of the directory the files were found is
	 *         the configured pickup folder.
	 */
	public Map<String, List<File>> getInvalidFiles();

}
