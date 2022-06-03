/**
 * Author:  T Goodwin
 * Date:    29/05/2012
 * Project: FDE018
 * 
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.endpoint.common;

import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;

public interface ReferenceOnlyFileController {
	
	/**
	 * Retrieve source for given reference only file
	 * @param referenceOnlyFileName
	 * @param applicationIdentifier
	 * @return contents of reference file if it exists.
	 * @throws ResourceTypeNotFoundException
	 * @throws ResourceNotFoundException
	 */
	public String getReferenceOnlyFileContents(String referenceOnlyFileName, String applicationIdentifier)
			throws ResourceTypeNotFoundException, ResourceNotFoundException;
	
	/**
	 * Determine if a reference only file exists
	 * @param referenceOnlyFileFileName
	 * @param applicationIdentifier
	 * @return true if file exists, else false
	 */
	public boolean referenceOnlyFileExists(String referenceOnlyFileName, String applicationIdentifier);
	
	
	/**
	 * Determine check (md5) of reference only file
	 * @param referenceOnlyFileName
	 * @param applicationIdentifier
	 * @return md5 checksum, null if file does not exist
	 */
	public String referenceOnlyFileChecksum(String referenceOnlyFileName, String applicationIdentifier);
	
}
