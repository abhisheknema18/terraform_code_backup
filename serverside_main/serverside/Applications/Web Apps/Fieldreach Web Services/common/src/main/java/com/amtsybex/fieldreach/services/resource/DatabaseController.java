/**
 * Author:  T Murray
 * Date:    22/03/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2015
 * 
 * Amended:
 * FDE029 TM 19/01/2015
 * Changes to asset database download process and code re-factoring for
 * work bank database download functionality.
 * 
 */
package com.amtsybex.fieldreach.services.resource;

import java.io.IOException;

import com.amtsybex.fieldreach.services.resource.common.DatabaseDetails;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;

public interface DatabaseController {

	/**
	 * Get the location that database files are stored on the server for the
	 * supplied database type. This is configured using the 'fileType.assetDb'
	 * property in app.properties. Occurrences of $DBCLASS will be substituted
	 * with the dbClass parameter and occurrences of $DBAREA will be substituted
	 * with the dbArea parameter.
	 * 
	 * @param dbType
	 *            The type of database to get the location of.
	 * 
	 * @param dbClass
	 *            Value to replace occurrences of $DBCLASS or $WBCLASS with in
	 *            the 'fileType.assetDb' property.
	 * 
	 * @param dbArea
	 *            Value to replace occurrence of $DBAREA with in the
	 *            'fileType.assetDb' property.
	 * 
	 * @param applicationIdentifier
	 *            Application identifier supplied in original request headers.
	 * 
	 * @return The location on the file system database files are located for
	 *         the database type supplied.
	 * 
	 * @throws ResourceTypeNotFoundException
	 *             Error resolving the database type.
	 * 
	 * @throws ResourceNotFoundException
	 *             Error resolving the database location.
	 */
	public String getDbLocation(String dbType, String dbClass, String dbArea,
			String applicationIdentifier) throws ResourceTypeNotFoundException,
			ResourceNotFoundException;

	/**
	 * Retrieve a DatabaseDetails object populated with information about the
	 * latest database available for the specified database type.
	 * 
	 * @param dbType
	 *            The type of database to get the details of.
	 * 
	 * @param dbClass
	 *            dbClass of the database.
	 * 
	 * @param dbArea
	 *            dbArea of the database.
	 * 
	 * @param applicationIdentifier
	 *            Application identifier supplied in original request headers.
	 * 
	 * @return A DatabaseDetails object populated with information about the
	 *         latest database available for the specified database type..
	 * 
	 * @throws IOException
	 *             Exception thrown is there is an error reading from the
	 *             database file when trying to determine the checksum or the
	 *             file size in bytes.
	 * 
	 * @throws ResourceTypeNotFoundException
	 *             Error resolving the database type.
	 * 
	 * @throws ResourceNotFoundException
	 *             Error resolving the database location or not database could
	 *             be found
	 */
	public DatabaseDetails getDbDetails(String dbType, String dbClass,
			String dbArea, String applicationIdentifier)
			throws ResourceTypeNotFoundException, ResourceNotFoundException,
			IOException;

}