/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2012
 * 
 * Modified T Goodwin
 * Date 29/05/2012
 * Project FDE018
 * Description : Add listing call and support for application identifiers
 * 
 */
package com.amtsybex.fieldreach.services.resource;

import java.util.List;

import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;

public interface FileResourceController 
{

	/**
	 * Get resource URI for fileName/fileType pairing. This method examines the spring 
	 * configuration and uses the for the 'file type' and the application identifier 
	 * to resolve the location of the file on the file system.
	 * 
	 * @param fileName
	 * The name of the file to resolve the URI of.
	 * 
	 * @param fileType
	 * The 'type' of the file to resolve the URI of. IE a script file, config file etc
	 * 
	 * @param applicationIdentifier
	 * The identifier of the client that has made a request to the web service.
	 * This is used to resolve the location of the file.
	 * 
	 * @return URI for filename/type combination, otherwise appropriate exception.
	 *         
	 * @throws ResourceTypeNotFoundException
	 * @throws ResourceNotFoundException
	 */
	public String getURI(String fileName, String fileType, String applicationIdentifier)
			throws ResourceTypeNotFoundException, ResourceNotFoundException;

	
	/**
	 * FDE019 TM 05/09/2012
	 * 
	 * Get resource URI for fileName/fileType pairing. This method examines the spring 
	 * configuration and uses the for the 'file type' and the application identifier 
	 * to resolve the location of the file on the file system.
	 * This also takes into account any sub directory structure that may be in place 
	 * within the root directory specified by the file type.
	 * 
	 * @param fileName
	 * The name of the file to resolve the URI of.
	 * 
	 * @param fileType
	 * The 'type' of the file to resolve the URI of. IE a script file, config file etc
	 * 
	 * @param subDir
	 * Sub directory contained within the 'file type' directory.
	 * 
	 * @param applicationIdentifier
	 * The identifier of the client that has made a request to the web service.
	 * This is used to resolve the location of the file.
	 * 
	 * @return URI for filename/type combination, otherwise appropriate exception.
	 *         
	 * @throws ResourceTypeNotFoundException
	 * @throws ResourceNotFoundException
	 */
	public String getURI(String fileName, String filetype, String subDir, 
			String applicationIdentifier)
			throws ResourceTypeNotFoundException, ResourceNotFoundException;
	
	
	/**
	 * Retrieve the contents of the file specified in the fileName parameter and return
	 * with base 64 encoding.
	 * 
	 * @param fileName
	 * The name of the file to retrieve base64 data from.
	 * 
	 * @param fileType
	 * The 'type' of the file being retrieved. IE a script file, config file etc
	 * This is used to resolve the location of the file.
	 * 
	 * @param applicationIdentifier
	 * The identifier of the client that has made a request to the web service.
	 * This is used to resolve the location of the file.
	 *  
	 * @return
	 * Base 64 encoded content of the file specified.
	 * 
	 * @throws ResourceTypeNotFoundException
	 * @throws ResourceNotFoundException
	 * 
	 */
	public String getBase64Content(String fileName, String filetype, String applicationIdentifier)
			throws ResourceTypeNotFoundException, ResourceNotFoundException;

	
	/**
	 * FDE019 TM 05/09/2012
	 * 
	 * Retrieve the contents of the file specified in the fileName parameter and return
	 * with base 64 encoding.
	 * This also takes into account any sub directory structure that may be in place 
	 * within the root directory specified by the file type.
	 * 
	 * @param fileName
	 * The name of the file to retrieve base64 data from.
	 * 
	 * @param fileType
	 * The 'type' of the file being retrieved. IE a script file, config file etc
	 * This is used to resolve the location of the file.
	 * 
	 * @param subDir
	 * Sub directory contained within the 'file type' directory.
	 * 
	 * @param applicationIdentifier
	 * The identifier of the client that has made a request to the web service.
	 * This is used to resolve the location of the file.
	 *  
	 * @return
	 * Base 64 encoded content of the file specified.
	 * 
	 * @throws ResourceTypeNotFoundException
	 * @throws ResourceNotFoundException
	 * 
	 */
	public String getBase64Content(String fileName, String filetype, String subDir,
			String applicationIdentifier)
			throws ResourceTypeNotFoundException, ResourceNotFoundException;
	
	/**
	 * Read the contents of the file specified and generate an MD5 checksum.
	 * The file content is NOT encoded in base 64 when the checksum is generated.
	 * 
	 * @param fileName
	 * The name of the file to retrieve base64 data from.
	 * 
	 * @param fileType
	 * The 'type' of the file being retrieved. IE a script file, config file etc
	 * This is used to resolve the location of the file.
	 * 
	 * @param applicationIdentifier
	 * The identifier of the client that has made a request to the web service.
	 * This is used to resolve the location of the file.
	 * 
	 * @return
	 * MD5 checksum for the contents of the file specified.
	 * 
	 * @throws ResourceTypeNotFoundException
	 * @throws ResourceNotFoundException
	 */
	public String getMd5Checksum(String fileName, String filetype, String applicationIdentifier)
			throws ResourceTypeNotFoundException, ResourceNotFoundException;

	
	/**
	 * Get listing of files contained in the directory for a specified file type.
	 * 
	 * @param filetype
	 * The file type you want to get a listing of all files for.
	 * 
	 * @return 
	 * list of filenames in directory identified by filetype.
	 * 
	 * @throws ResourceTypeNotFoundException
	 */
	public List<String> getListing(String filetype, String applicationIdentifier)
			throws ResourceTypeNotFoundException;
	
	
	
	/**
	 * FDE019 TM 05/09/2012
	 * 
	 * Get the directory for a specified filetype.
	 * 
	 * @param filetype
	 * The file type you want to get a listing of all files for.
	 * 
	 * @return 
	 * list of filenames in directory identified by filetype.
	 * 
	 * @return
	 * Absolute path of the directory for the specified file type.
	 * 
	 */
	public String getFileTypeDir(String fileType, String applicationIdentifier) 
			throws ResourceTypeNotFoundException;
}
