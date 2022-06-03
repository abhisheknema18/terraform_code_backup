/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	18/12/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.audit.dao;

import java.util.List;

import com.amtsybex.fieldreach.extract.audit.model.ExtractErrors;


/**
 * Interface to support interaction with the extract_errors table in the 
 * Extract Adapter audit database.
 */
public interface ExtractErrorsDao {

	/**
	 * Get all records from the extract_errors table.
	 * 
	 * @return List of ExtractErrors objects.
	 */
	public List<ExtractErrors> find();

	/**
	 * Deletes records from the extract_errors table with returnIds matching those
	 * supplied.
	 * 
	 * @param ids
	 *            List of returnIds to delete from the extract_errors table.
	 */
	public void deleteById(List<String> ids);

}
