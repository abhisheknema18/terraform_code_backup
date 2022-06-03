/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	28/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.dao;

import java.util.List;

import com.amtsybex.fieldreach.xmlloader.audit.model.StatusValues;

/**
 * Interface to support interaction with the status_values table in the XML
 * loader audit database.
 */
public interface StatusValuesDao {

	/**
	 * Get all records from the status_values table.
	 * 
	 * @return List of StatusValues objects.
	 */
	public List<StatusValues> find();

}
