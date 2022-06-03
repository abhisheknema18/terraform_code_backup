/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	29/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.dao;

import java.util.List;

import com.amtsybex.fieldreach.xmlloader.audit.model.MilestoneValues;

/**
 * Interface to support interaction with the milestone_values table in the XML
 * loader audit database.
 */
public interface MilestoneValuesDao {

	/**
	 * Get all records from the milestone_values table.
	 * 
	 * @return List of MilestoneValues objects.
	 */
	public List<MilestoneValues> find();

}
