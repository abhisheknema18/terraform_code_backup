/**
 * Author:  T Goodwin
 * Date:    29/05/2012
 * Project: FDE018
 * 
 * Copyright AMT-Sybex 2012
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 */
package com.amtsybex.fieldreach.services.endpoint.common;

import java.util.List;

import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;

public interface SupportFileController {

	/**
	 * Retrieve source for given support file.
	 * 
	 * @param supportFileName
	 * name of the support file ot retirve the contents of
	 * 
	 * @param applicationIdentifier
	 * application identifier supplied with the original request.
	 * 
	 * @return 
	 * Base64 encoded contents of the support file.
	 * 
	 * @throws ResourceTypeNotFoundException
	 * Support file directory could not be determined.
	 * 
	 * @throws ResourceNotFoundException
	 * Support file could not be found.
	 */
	public String getSupportFileContents(String supportFileName, String applicationIdentifier)
			throws ResourceTypeNotFoundException, ResourceNotFoundException;
	
	/**
	 * Determine if a support file exists
	 * 
	 * @param supportFileFileName
	 * Name of the support file to check the existance of.
	 * 
	 * @param applicationIdentifier
	 * aplication identifier supplied with the original request.
	 * 
	 * @return
	 * True if the support file exists, false if it does not.
	 */
	public boolean supportFileExists(String supportFileName, String applicationIdentifier);
	
	
	/**
	 * Determine check (md5) of supportfile
	 * 
	 * @param supportFileName
	 * name of the support file to generate the checksum for.
	 * 
	 * @param applicationIdentifier
	 * application identifier of supplied with the original request.
	 * 
	 * @return 
	 * md5 checksum, null if support file does not exist.
	 */
	public String supportFileChecksum(String supportFileName, String applicationIdentifier);
	
	
	/**
	 * Retrieve list of all supporting files.
	 * 
	 * @param applicationIdentifier
	 * application identiifer supplied with the original request.
	 * 
	 * @return 
	 * list of file in support directory
	 * Only normal files which can be read are returned. Directories are omitted.
	 */
	public List<String> supportFileListing(String applicationIdentifier) throws ResourceTypeNotFoundException;
	
	
	// FDE026 TM 30/05/2014 
	
	/**
	 * Get the name of the directory in which support files are stored.
	 * 
	 * @param applicationIdentifier
	 * application identiifer supplied with the original request
	 * 
	 * @return
	 * Full path of the directory that support files are stored in
	 * 
	 * @throws ResourceTypeNotFoundException
	 * Support file directory could not be determined.
	 */
	public String getSupportFileDir(String applicationIdentifier) 
			throws ResourceTypeNotFoundException;
	
	//End FDE026
}
