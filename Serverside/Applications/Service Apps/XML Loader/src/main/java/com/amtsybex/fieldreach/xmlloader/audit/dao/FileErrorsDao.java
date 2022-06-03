/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	29/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.dao;

import java.util.List;

import com.amtsybex.fieldreach.xmlloader.audit.model.FileErrors;

/**
 * Interface to support interaction with the file_errors table in the XML loader
 * audit database.
 */
public interface FileErrorsDao {

	/**
	 * Get all records from the file_error table.
	 * 
	 * @return List of FileError objects.
	 */
	public List<FileErrors> find();

	/**
	 * Deletes records from the file_errors table with fileIds matching those
	 * supplied.
	 * 
	 * @param ids
	 *            List of fileIds to delete from the file_errors table.
	 */
	public void deleteById(List<Integer> ids);

}
