/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	28/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.dao;

import java.util.Date;
import java.util.List;

import com.amtsybex.fieldreach.xmlloader.audit.model.AuditMilestones;

/**
 * Interface to support interaction with the audit_milestones table in the XML
 * loader audit database.
 */
public interface AuditMilestonesDao {

	/**
	 * Get all records from the audit_milestones table.
	 * 
	 * @return List of AuditMilestones objects.
	 */
	public List<AuditMilestones> find();

	/**
	 * Get list of ids of records from the audit_milestones table that have a
	 * milestonedate < the supplied date.
	 */
	public List<Integer> getMaintenanceDeletionCandidates(Date date);

	/**
	 * Deletes records from the audit_milestones table with fileIds matching
	 * those supplied.
	 * 
	 * @param ids
	 *            List of fileIds to delete from the audit_milestones table.
	 */
	public void deleteById(List<Integer> ids);

}
