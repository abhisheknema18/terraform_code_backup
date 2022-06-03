/**
 * Author:  T Murray
 * Date:    22/05/2014
 * Project: FDE026
 * 
 * Copyright AMT-Sybex 2014
 */
package com.amtsybex.fieldreach.services.endpoint.common;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.InProgressDownloads;

/**
 * Interface to allow common operations around file transfers to be performed.
 * These operations are not specific to any type of web service implementation
 * such as SOAP or REST.
 */
public interface FileTransferController {


	
	/**
	 * Method to remove records from the InProgressUpload with a start date that is 
	 * older than the number of days supplied. Associate drecords will also be
	 * deleted from the UploadParts table.
	 * 
	 * @param maxDays
	 * The maximum number of days an upload should be retained for 
	 * in the inProgressUploads table.
	 * 
	 * @throws Exception
	 * General exceptions may occur when accessing the database. These are not handled to 
	 * allow the spring framework to manage transactions.
	 */
	public void uploadMaintenance(int maxDays) throws Exception;
	
	/**
	 * Method to find a record in the InProgressDownloads table with an id
	 * matching that supplied.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param id
	 *            id of the record to search for.
	 * 
	 * @return Record found in the InProgressDownloads table with the id
	 *         matching that supplied. Null is returned if no record is found.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public InProgressDownloads findDownload(String frInstance, String id)
			throws FRInstanceException;

	/**
	 * Method to insert a record into the InProgressDownloads table in the
	 * Fieldreach database using the InProgressDownloads entity supplied.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param entity
	 *            InProgressDownloads to be inserted into the database.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public void startDownload(String frInstance, InProgressDownloads entity)
			throws FRInstanceException;


	/**
	 * Method to remove records from the InProgressDownloads with a start date
	 * that is older than the number of days supplied.
	 * 
	 * @param maxDays
	 *            The maximum number of days a download should be retained for
	 *            in the InProgressDownloads table.
	 * 
	 * @throws Exception
	 *             General exceptions may occur when accessing the database.
	 *             These are not handled to allow the spring framework to manage
	 *             transactions.
	 */
	public void downloadMaintenance(int maxDays) throws Exception;

	/**
	 * Method to check if any records exist in the InProgressDownloads table
	 * with an id matching that supplied.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param id
	 *            id of the record to check exists in the database.
	 * 
	 * @return true is returned if at least one record exists with the id
	 *         supplied. false is returned if no records exist with an id
	 *         matching that supplied.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public boolean isExistingDownload(String frInstance, String id)
			throws FRInstanceException;

	/**
	 * Set the location of the tempory upload directory. This is to prevent
	 * circular references being created between Filetransfer and
	 * FileUpload controllers in Spring conifguration.
	 * 
	 * @param tempDir
	 */
	public void setUploadTempDir(String tempDir);

	boolean isExistingUpload(String frInstance, String id) throws FRInstanceException;
}
