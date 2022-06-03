/**
 * Author:  T Murray
 * Date:    19/03/2012
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
package com.amtsybex.fieldreach.services.resource.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.services.resource.DatabaseController;
import com.amtsybex.fieldreach.services.resource.common.DatabaseDetails;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

public class DatabaseControllerImpl implements DatabaseController {

	private static Logger log = LoggerFactory.getLogger(DatabaseControllerImpl.class
			.getName());

	private Map<String, String> dbTypes;

	//FDE034 TM 12/08/2015
	private static final String ASSET_DB_PREFIX = "EQUIPMENT_";
	private static final String WORKBANK_DB_PREFIX = "WORKBANK_";
	private static final String RESULTHISTORY_DB_PREFIX = "HISTORICRESULTS_";
	private static final String DB_EXT  = ".ZIP";
	
	private final FilenameFilter assetDBFilter = this.createFilenameFilter(ASSET_DB_PREFIX, DB_EXT);
	private final FilenameFilter workbankDBFilter = this.createFilenameFilter(WORKBANK_DB_PREFIX, DB_EXT);
	private final FilenameFilter resulthistoryDBFilter = this.createFilenameFilter(RESULTHISTORY_DB_PREFIX, DB_EXT);
	
	//End FDE034
	
	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public DatabaseControllerImpl() {

		super();
	}

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.resource.DatabaseController#getDbLocation
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getDbLocation(String dbType, String dbClass, String dbArea,
			String applicationIdentifier) throws ResourceTypeNotFoundException,
			ResourceNotFoundException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getDbLocation dbType=" + Common.CRLFEscapeString(dbType) + " dbClass="
					+ Common.CRLFEscapeString(dbClass) + " dbArea=" + Common.CRLFEscapeString(dbArea) + " applicationIdentifier="
					+ Common.CRLFEscapeString(applicationIdentifier));
		}

		// DB Class is required so throw exception if it is not supplied.
		if (dbClass == null || dbClass.trim().equals("")) {

			throw new ResourceTypeNotFoundException(
					"Database class value not supplied.");
		}

		String retVal = null;

		// Determine which property we need to use to get the database location.

		if (applicationIdentifier != null
				&& !applicationIdentifier.trim().equals("")) {

			String candidate = "";

			String[] words = applicationIdentifier.split(" ");

			if (words.length > 0)
				candidate = words[0].toLowerCase();

			// Look for context specific property first then look
			// for default if none exists.
			if (dbTypes.containsKey(dbType + "." + candidate)) {

				retVal = dbTypes.get(dbType + "." + candidate);

			} else if (dbTypes.containsKey(dbType)) {

				retVal = dbTypes.get(dbType);
			}

		} else {

			if (dbTypes.containsKey(dbType))
				retVal = dbTypes.get(dbType);
		}

		// Could not find database property in configuration so throw exception.
		if (retVal == null) {

			throw new ResourceTypeNotFoundException(
					"Database type could not be found in configuration:"
							+ dbType);
		}

		// Perform substitution of values if any place holders present in
		// database location configuration.
		retVal = retVal.replace(Utils.DBCLASS_PLACEHOLDER, dbClass);

		retVal = retVal.replace(Utils.WBCLASS_PLACEHOLDER, dbClass);
		
		if (dbArea != null && !dbArea.trim().equals(""))
			retVal = retVal.replace(Utils.DBAREA_PLACEHOLDER, dbArea);

		// Check directory exists on file system
		File dir = new File(retVal);

		if (!dir.isDirectory()) {

			throw new ResourceNotFoundException("Resource not found: " + retVal);
		}

		if (log.isDebugEnabled())
			log.debug("<<< getDbLocation retVal=" + Common.CRLFEscapeString(retVal));

		return retVal;
	}

	// FDE034 TM 12/08/2015
	// Modified function so that latest database is determined by using lastmodifieddate rather
	// than the production date.
	// Also removed dependency on filename format. The only check will be to see if the file is
	// a .zip and if it is prefixed with EQUIPMENT or WORKBANK.
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.resource.DatabaseController#getDbDetails
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public DatabaseDetails getDbDetails(String dbType, String dbClass,
			String dbArea, String applicationIdentifier)
			throws ResourceTypeNotFoundException, ResourceNotFoundException,
			IOException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getDbDetails dbType=" + Common.CRLFEscapeString(dbType) + " dbClass="
					+ Common.CRLFEscapeString(dbClass) + " dbArea=" + Common.CRLFEscapeString(dbArea) + " applicationIdentifier="
					+ Common.CRLFEscapeString(applicationIdentifier));
		}

		// Iterate over each of the files contained in the directory
		// specified looking for the latest asset hierarchy database.

		String dbFileName = null;
		long dbDate = -1;

		File dir = new File(this.getDbLocation(dbType, dbClass, dbArea,
				applicationIdentifier));

		//Only include .zip files prefixed with EQUIPMENT or WORKBANK
		File[] files;
		
		if(dbType.equalsIgnoreCase(Utils.ASSETDB_TYPE))
			files = dir.listFiles(assetDBFilter);
		else if(dbType.equalsIgnoreCase(Utils.WORKBANKDB_TYPE))
			files = dir.listFiles(workbankDBFilter);
		else if(dbType.equalsIgnoreCase(Utils.RESULTHISTORYDB_TYPE))
			files = dir.listFiles(resulthistoryDBFilter);
		else
			throw new ResourceTypeNotFoundException("Invalid database type found: " + dbType);
				
		for (File file : files) {
			
			long tempDate = file.lastModified();

			if (tempDate > dbDate) {

				dbFileName = file.getName();
				dbDate = tempDate;
			}
		}

		// Now create a DatabaseDetails object containing the information
		// about the latest database.

		DatabaseDetails latestDB = null;

		// If the dbDate is -1 that means that date information could not
		// be extracted from any of the files found. This means that no
		// databases could be found so a ResourceTypeNotFoundException should
		// be thrown.
		if (dbDate != -1) {

			try {

				latestDB = resolveDBDetails(dir.getAbsolutePath(), dbFileName);

			} catch (FileNotFoundException e) {

				log.error("Unable to find database file "
						+ Common.CRLFEscapeString(dir.getAbsolutePath()) + "/" + Common.CRLFEscapeString(dbFileName));

				throw new ResourceNotFoundException(e.toString());
			}

		} else {

			log.error("No databases in the directory: " + Common.CRLFEscapeString(dir.getAbsolutePath()));

			throw new ResourceTypeNotFoundException(
					"No databases in the directory: " + dir.getAbsolutePath());
		}

		log.debug("<<< getDbDetails");

		return latestDB;
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	// FDE034 TM 11/08/2015
	// Removed methods getDateFromFileName and  getDBClassFromFileName 
	// as they are no longer required.
	// End FDE034

	/**
	 * A method to create a DatabaseDetails object based on the data passed in.
	 * The method will look for the database file passed in as a parameter in
	 * the directory specified. A checksum will be calculated and the size of
	 * the file is also calculated.
	 * 
	 * @param holdingDir
	 *            The directory to look in for the database specified by the
	 *            'name' parameter.
	 * 
	 * @param name
	 *            The name of the database to return the information on.
	 * 
	 * @return DatabaseDetails object populated with the information relating to
	 *         the database name that is passed in.
	 * 
	 * @throws FileNotFoundException
	 *             Exception thrown if the database file specified can not be
	 *             found in the directory specified.
	 * 
	 * @throws IOException
	 *             Exception thrown is there is an error reading from the asset
	 *             hierarchy database file when trying to determine the checksum
	 *             or the file size in bytes.
	 */
	private DatabaseDetails resolveDBDetails(String holdingDir, String name)
			throws IOException, FileNotFoundException {

		if (log.isDebugEnabled())
			log.debug(">>> resolveDBDetails");

		DatabaseDetails latestDB = null;

		if (name != null) {

			// Get the data of the asset DB
			File assetDBFile = new File(holdingDir + "/" + name);

			byte[] assetDBData = Common.getBytesFromFile(assetDBFile);

			// Determine the checksum and the size of the asset DB
			String dbChecksum = Common.generateMD5Checksum(assetDBData);

			int dbSizeInBytes = assetDBData.length;

			if (log.isDebugEnabled()) {
				
				log.debug("Checksum = " + dbChecksum);
				log.debug("size in bytes = " + dbSizeInBytes);
			}

			latestDB = new DatabaseDetails(name, (long) dbSizeInBytes, dbChecksum);
		}

		if (log.isDebugEnabled())
			log.debug("<<< resolveDBDetails");

		return latestDB;
	}
	
	
	// FDE034 TM 12/08/2015
	/**
	 * Create a FilenameFilter using the parameters supplied.
	 * 
	 * @param filterStart
	 *            Filter to be applied to the start of the filename.
	 * 
	 * @param filterEnd
	 *            Filter to be applied to the end of the filename.
	 * 
	 * @return FilenameFilter object created using the values supplied.
	 */
	private FilenameFilter createFilenameFilter(
			final String filterStart, final String filterEnd) {

		if(log.isDebugEnabled()) {
			
			log.debug(">>> createFilenameFilter filterStart=" + filterStart
					+ " filterEnd=" + filterEnd);
		}

		FilenameFilter pickupFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				String uppercaseName = name.toUpperCase();
				
				if (uppercaseName.startsWith(filterStart)
						&& uppercaseName.endsWith(filterEnd)) {

					return true;

				} else {

					return false;
				}
			}
		};

		log.debug("<<< createFilenameFilter");

		return pickupFilter;
	}
	// End FDE034
	

	/*-------------------------------------------
	 - Spring Injection Methods
	 --------------------------------------------*/

	public Map<String, String> getDbTypes() {

		return dbTypes;
	}

	public void setDbTypes(Map<String, String> dbTypes) {

		this.dbTypes = dbTypes;
	}

}