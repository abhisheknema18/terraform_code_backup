/**
 * Author:  Rajesh Shivaramakrishnan
 * Date:    02/12/2022
 * 
 * Copyright AMT-Sybex 2014
 */
package com.amtsybex.fieldreach.services.endpoint.common;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.InProgressUploadsInt;

public interface FileTransferController {

	
	/**
	 * Method to check if any records exist in the InProgressUploadsInt table with
	 * an id matching that supplied.
	 * 
	 * @param FRInstance
	 *            Name of the FieldSmart instance to be used.
	 * 
	 * @param id
	 *            id of the upload to check for the existence of in the
	 *            database.
	 * 
	 * @return true is returned if at least one record exists with the id
	 *         supplied. false is returned if no records exist with an id
	 *         matching that supplied.
	 * 
	 * @throws FRInstanceException
	 *             The FieldSmart instance supplied could not be found.
	 *             An exception occurred accessing the FieldSmart instance.
	 */
	public boolean isExistingUpload(String frInstance, String id)
			throws FRInstanceException;

	
	/**
	 * Method to find a record in the InProgressUploadsInt table with an id
	 * matching that supplied.
	 * 
	 * @param FRInstance
	 *            Name of the FieldSmart instance to be used.
	 * 
	 * @param id
	 *            id of the record to search for.
	 * 
	 * @return Record found in the InProgressUploadsInt table with the id matching
	 *         that supplied. Null is returned if no record is found.
	 * 
	 * @throws FRInstanceException
	 *             The FieldSmart instance supplied could not be found. An
	 *             exception occurred accessing the FieldSmart instance.
	 */
	public InProgressUploadsInt findUpload(String frInstance, String id)
			throws FRInstanceException;


	/**
	 * Method to insert a record into the InProgressUploadsInt table in the
	 * FieldSmart database using the InProgressUploadsInt entity supplied.
	 * 
	 * @param FRInstance
	 *            Name of the FieldSmart instance to be used.
	 * 
	 * @param entity
	 *            InProgressUploadsInt record to be inserted into the database.
	 * 
	 * @throws FRInstanceException
	 *             The FieldSmart instance supplied could not be found. An
	 *             exception occurred accessing the FieldSmart instance.
	 */
	public void startUpload(String frInstance, InProgressUploadsInt upload) throws FRInstanceException;


	/**
	 * Method to update a record into the InProgressUploadsInt table in the
	 * FieldSmart database using the InProgressUploadsInt entity supplied.
	 * 
	 * @param FRInstance
	 *            Name of the FieldSmart instance to be used.
	 * 
	 * @param entity
	 *            InProgressUploadsInt record to be updated in the database.
	 * 
	 * @throws FRInstanceException
	 *             The FieldSmart instance supplied could not be found. An
	 *             exception occurred accessing the FieldSmarts instance.
	 */
	public void updateUpload(String frInstance, InProgressUploadsInt entity) throws FRInstanceException;

	/**
	 * Method to remove records into the InProgressUploadsInt table in the
	 * FieldSmart database with an id that matches that supplied.
	 * 
	 * @param FRInstance
	 *            Name of the FieldSmart instance to be used.
	 * 
	 * @param id
	 *            id of the record to be removed from the database.
	 * 
	 * @throws FRInstanceException
	 *             The FieldSmart instance supplied could not be found. An
	 *             exception occurred accessing the FieldSmart instance.
	 */
	void cancelUpload(String frInstance, String id) throws FRInstanceException;
	
	
}
