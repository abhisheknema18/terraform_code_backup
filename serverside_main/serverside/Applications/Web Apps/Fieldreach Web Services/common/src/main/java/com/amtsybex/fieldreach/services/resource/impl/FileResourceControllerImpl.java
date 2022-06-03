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
 * Description : Add in listing and support for application identifiers.
 * 
 */
package com.amtsybex.fieldreach.services.resource.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import com.amtsybex.fieldreach.services.resource.FileResourceController;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.utils.impl.Common;

public class FileResourceControllerImpl implements FileResourceController 
{

	static Logger log = LoggerFactory.getLogger(FileResourceControllerImpl.class
			.getName());

	private Map<String, String> fileTypeMapping;

	
	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/
	
	public FileResourceControllerImpl()
	{
		
	}
	
	
	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/
	
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
	@Override
	public String getURI(String fileName, String fileType,
			String applicationIdentifier) throws ResourceTypeNotFoundException,
			ResourceNotFoundException 
	{
		if (log.isDebugEnabled())
			log.debug(">>> getURI filename=" + Common.CRLFEscapeString(fileName) + " filetype=" + Common.CRLFEscapeString(fileType)
				+ " applicationIdentifier=" + Common.CRLFEscapeString(applicationIdentifier));

		String retval = null;
		
		
		// FDE019 TM 04/09/2012
		// Use new getResourceRootDirectory to get the directory
		String resourceDirectory = this.getFileTypeDir(fileType,
				applicationIdentifier);
		//End FDE019

		
		// Check file exists
		retval = StringUtils.cleanPath(resourceDirectory + "/" + fileName);
		
		if (log.isDebugEnabled())
			log.debug("File path=[" + Common.CRLFEscapeString(retval) + "]");

		Resource resFile = new FileSystemResource(retval);

		if (!resFile.exists()) 
		{
			log.debug("File not found [" + resFile.getFilename() + "]");
			throw new ResourceNotFoundException("Resource Not Found: " + 
					retval);
		}

		if (log.isDebugEnabled())
			log.debug("<<< getURI retval=" + Common.CRLFEscapeString(retval));
		
		return retval;
	}



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
	@Override
	public String getURI(String fileName, String filetype, String subDir,
			String applicationIdentifier) throws ResourceTypeNotFoundException,
			ResourceNotFoundException 
	{
		if (log.isDebugEnabled())
			log.debug(">>> getURI filename=" + Common.CRLFEscapeString(fileName) + " filetype=" + Common.CRLFEscapeString(filetype)
				+ " subDir= " + Common.CRLFEscapeString(subDir) + " applicationIdentifier=" + Common.CRLFEscapeString(applicationIdentifier));

		String retval = null;
		
		
		// Use new getResourceRootDirectory to get the directory
		String resourceDirectory = this.getFileTypeDir(filetype,
				applicationIdentifier);

		
		//Append subDir onto the root directory
		resourceDirectory += "/" + subDir;
		
		
		// Check file exists
		retval = StringUtils.cleanPath(resourceDirectory + "/" + fileName);
		
		if (log.isDebugEnabled())
			log.debug("File path=[" + Common.CRLFEscapeString(retval) + "]");

		Resource resFile = new FileSystemResource(retval);

		if (!resFile.exists()) 
		{
			log.debug("File not found [" + resFile.getFilename() + "]");
			throw new ResourceNotFoundException("Resource Not Found: " + 
					retval);
		}

		if (log.isDebugEnabled())
			log.debug("<<< getURI retval=" + Common.CRLFEscapeString(retval));
		
		return retval;
	}
	
	
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
	 */
	@Override
	public String getBase64Content(String fileName, String filetype,
			String applicationIdentifier) throws ResourceTypeNotFoundException,
			ResourceNotFoundException {
		
		if (log.isDebugEnabled())
			log.debug(">>> getBase64Content filename=" + Common.CRLFEscapeString(fileName) + " filetype="
				+ Common.CRLFEscapeString(filetype) + " applicationIdentifier=" + Common.CRLFEscapeString(applicationIdentifier));

		String retval = null;
		String uri = getURI(fileName, filetype, applicationIdentifier);

		retval = this.getBase64Content(uri);

		log.debug("<<< getBase64Content ");
		return retval;
	}

	
	/**
	 * FDE019 TM 05/09/2012
	 * 
	 * Retrieve the contents of the file specified in the fileName parameter and return
	 * with base 64 encoding.
	 * This also takes into account any sub directory structure that may be in place 
	 * within the root directory specified by the file type.
	 * 
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
	@Override
	public String getBase64Content(String fileName, String filetype, String subDir, 
			String applicationIdentifier) throws ResourceTypeNotFoundException,
			ResourceNotFoundException 
	{
		
		if (log.isDebugEnabled())
			log.debug(">>> getBase64Content filename=" + Common.CRLFEscapeString(fileName) + " filetype="
				+ Common.CRLFEscapeString(filetype) + " applicationIdentifier=" + Common.CRLFEscapeString(applicationIdentifier));

		String retval = null;
		String uri = getURI(fileName, filetype, subDir, applicationIdentifier);

		retval = this.getBase64Content(uri);

		log.debug("<<< getBase64Content");
		
		return retval;
	}
	
	
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
	@Override
	public String getMd5Checksum(String fileName, String filetype,
			String applicationIdentifier) throws ResourceTypeNotFoundException,
			ResourceNotFoundException {
		
		if (log.isDebugEnabled())
			log.debug(">>> getMd5Checksum filename=" + Common.CRLFEscapeString(fileName) + " filetype="
				+ Common.CRLFEscapeString(filetype) + " applicationIdentifier=" + Common.CRLFEscapeString(applicationIdentifier));

		String retval = null;
		String uri = getURI(fileName, filetype, applicationIdentifier);

		Resource resFile = new FileSystemResource(uri);

		if (!resFile.exists()) {
			throw new ResourceNotFoundException("Resource Not Found: " + uri);
		}

		try {

			byte[] byteData = Common.getBytesFromFile(resFile.getFile());

			// Determine the checksum
			retval = Common.generateMD5Checksum(byteData);

		} catch (IOException ioe) {
			log.error("IOException readin resource file " + fileName);
			throw new ResourceNotFoundException("Resource Not Found: " + 
					ioe.getMessage());
		}

		log.debug("<<< getMd5Checksum ");
		return retval;
	}

	
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
	@Override
	public List<String> getListing(String filetype, String applicationIdentifier)
			throws ResourceTypeNotFoundException 
	{
		
		if (log.isDebugEnabled())
			log.debug(">>> getListing type=" + Common.CRLFEscapeString(filetype) + " applicationIdentifier="
				+ Common.CRLFEscapeString(applicationIdentifier));

		List<String> lstFiles = new ArrayList<String>();

		
		//FDE018 Moved code to the resolveAppSpecificFileType function
		// if a valid application identifier has been supplied the use this to create 
		// a working file type.
		String wrkFileType = resolveAppSpecificFileType(applicationIdentifier, filetype);
		//End FDE018
		
		
		if (!fileTypeMapping.containsKey(wrkFileType)) {
			throw new ResourceTypeNotFoundException("Resource Type Not Found: " + 
					wrkFileType);
		}

		String resourceDirectory = fileTypeMapping.get(wrkFileType);
		log.debug("Resource directory =" + resourceDirectory);

		
		//
		// Find files in directory
		// Then omit directories .. and anything we cant read.
		File dir = new File(resourceDirectory);

		String[] directoryFiles = dir.list();

		if (directoryFiles != null) {
			for (int i = 0; i < directoryFiles.length; i++) {
				File objFile = new File(
						StringUtils.cleanPath(resourceDirectory) + "/"
								+ directoryFiles[i]);
				if (objFile != null && !objFile.isDirectory()
						&& objFile.canRead()) {
					lstFiles.add(directoryFiles[i]);
					log.debug("Adding File :" + directoryFiles[i]);
				}
			}
		}

		log.debug("<<< getListing count=" + lstFiles.size());
		return lstFiles;
	}


	/**
	 * FDE019 TM 05/09/2012
	 * 
	 * Get the directory for a specified file type.
	 * 
	 * @param filetype
	 * The file type you want to get a listing of all files for.
	 * 
	 * @return 
	 * list of filenames in directory identified by file type.
	 * 
	 * @return
	 * Absolute path of the directory for the specified file type.
	 * 
	 */
	public String getFileTypeDir(String fileType, String applicationIdentifier) 
			throws ResourceTypeNotFoundException
	{
		
		if (log.isDebugEnabled())
			log.debug(">>> getFileTypeDir filetype=" + Common.CRLFEscapeString(fileType)
				+ " applicationIdentifier=" + Common.CRLFEscapeString(applicationIdentifier));

		String retval = null;
		String wrkFileType = resolveAppSpecificFileType(applicationIdentifier, fileType);


		// We use the file type to determine the resource directory.
		// First check we have a mapping for that file Type
		if (!fileTypeMapping.containsKey(wrkFileType)) 
		{
			throw new ResourceTypeNotFoundException("Resource Type Not Found: " + 
					wrkFileType);
		}

		
		retval = fileTypeMapping.get(wrkFileType);
		
		log.debug("<<< getFileTypeDir return value=" + retval);
		
		return retval;
	}
	
	
	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/
	
	//FDE018 PVC#1 TM 30/07/2012
	/**
	 * Function will append applicationIdentifier to the end of the filetype supplied
	 * seperated by a '.' This is to allow separation of resources for different applications.
	 * 
	 * @param applicationIdentifier 
	 * Unique application identifier.
	 * 
	 * @param filetype 
	 * The standard non application specific file type.
	 * 
	 * @return 
	 * A new file type identifier that is specific to the application specified in the
	 * applicationIdentifier passed in.
	 */
	private String resolveAppSpecificFileType(String applicationIdentifier, String filetype)
	{
		
		if (log.isDebugEnabled())
			log.debug(">>> resolveAppSpecificFileType applicationIdentifier=" + Common.CRLFEscapeString(applicationIdentifier) 
				+ " filetype=" + Common.CRLFEscapeString(filetype));
		
		String wrkFileType = filetype;
		
		if (applicationIdentifier != null && !applicationIdentifier.isEmpty())
		{
			String[] words = applicationIdentifier.split(" ");
			if (words.length > 0)
			{
				String candidate = filetype + "." + words[0].toLowerCase();
				
				if (log.isDebugEnabled())
					log.debug("Candidate app identifier :" + Common.CRLFEscapeString(candidate));
					
				// Only active if defined in mapping
				if (fileTypeMapping.containsKey(candidate ))
					wrkFileType = candidate;
			}
		}
		
		
		log.debug("<<< resolveAppSpecificFileType");
		
		return wrkFileType;
	}
	//End FDE018
	
	
	/**
	 * FDE019 TM 05/09/2012
	 * 
	 * Retrieves the content of the resource specified by the URI passed in encoded
	 * in base 64. This function is designed to promoted code reuse.
	 * 
	 * @param uri
	 * URI of the resource to encode in base 64.
	 * 
	 * @return
	 * base 64 encoded representation of the resource specified by the URI passed in.
	 * 
	 * @throws ResourceNotFoundException
	 */
	private String getBase64Content(String uri) 
			throws ResourceNotFoundException
	{
		
		if (log.isDebugEnabled())
			log.debug(">>> getBase64Content uri=" + Common.CRLFEscapeString(uri));
		
		String retval = null;
		
		
		Resource resFile = new FileSystemResource(uri);

		if (!resFile.exists()) 
			throw new ResourceNotFoundException("Resource Not Found: " + uri);
		

		try 
		{
			byte[] byteData = Common.getBytesFromFile(resFile.getFile());

			// Encode data..
			retval = Common.encodeBase64(byteData);
		} 
		catch (IOException ioe) 
		{
			log.error("IOException reading resource file " + Common.CRLFEscapeString(uri));
			throw new ResourceNotFoundException(ioe.getMessage());
		}
		
		log.debug("<<< getBase64Content");
		
		return retval;
	}
		
	
	/*-------------------------------------------
	 - Spring injection Methods
	 --------------------------------------------*/
	
	public Map<String, String> getFileTypeMapping() 
	{
		return fileTypeMapping;
	}

	public void setFileTypeMapping(Map<String, String> fileTypeMapping) 
	{
		this.fileTypeMapping = fileTypeMapping;
	}
}
