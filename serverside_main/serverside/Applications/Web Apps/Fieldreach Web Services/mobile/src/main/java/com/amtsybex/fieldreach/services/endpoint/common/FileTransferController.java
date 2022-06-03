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
import com.amtsybex.fieldreach.backend.model.InProgressUploads;

/**
 * Interface to allow common operations around file transfers to be performed.
 * These operations are not specific to any type of web service implementation
 * such as SOAP or REST.
 */
public interface FileTransferController {

	/**
	 * Method to find a record in the InProgressUploads table with an id
	 * matching that supplied.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param id
	 *            id of the record to search for.
	 * 
	 * @return Record found in the InProgressUploads table with the id matching
	 *         that supplied. Null is returned if no record is found.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public InProgressUploads findUpload(String frInstance, String id)
			throws FRInstanceException;

	/**
	 * Method to insert a record into the InProgressUploads table in the
	 * Fieldreach database using the InProgressUploads entity supplied.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param entity
	 *            InProgressUploads record to be inserted into the database.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public void startUpload(String frInstance, InProgressUploads entity)
			throws FRInstanceException;

	/**
	 * Method to remove records into the InProgressUploads table in the
	 * Fieldreach database with an id that matches that supplied.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param id
	 *            id of the record to be removed from the database.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public void cancelUpload(String frInstance, String id)
			throws FRInstanceException;

	/**
	 * Method to update a record into the InProgressUploads table in the
	 * Fieldreach database using the InProgressUploads entity supplied.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param entity
	 *            InProgressUploads record to be updated in the database.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public void updateUpload(String frInstance, InProgressUploads entity)
			throws FRInstanceException;

	/**
	 * Method to check if any records exist in the InProgressUploads table with
	 * an id matching that supplied.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param id
	 *            id of the upload to check for the existance of in the
	 *            database.
	 * 
	 * @return true is returned if at least one record exists with the id
	 *         supplied. false is returned if no records exist with an id
	 *         matching that supplied.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found.
	 *             An exception occurred accessing the Fieldreach instance.
	 */
	public boolean isExistingUpload(String frInstance, String id)
			throws FRInstanceException;

	void setUploadTempDir(String tempDir);
	
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

}
