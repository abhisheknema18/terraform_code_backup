/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	29/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.dao;

import java.util.List;

import com.amtsybex.fieldreach.xmlloader.audit.model.AuditFiles;

/**
 * Interface to support interaction with the audit_files table in the XML loader
 * audit database.
 */
public interface AuditFilesDao {

	/**
	 * Get all records from the audit_files table.
	 * 
	 * @return List of AuditFiles objects.
	 */
	public List<AuditFiles> find();

	/**
	 * Get record from the audit_files table with an id matching that supplied.
	 * 
	 * @param id
	 *            Id to search the audit_files table for in the audit database.
	 * 
	 * @return List of AuditFiles objects.
	 */
	public AuditFiles find(String id);

	/**
	 * Returns a list of AuditFiles records that have not completed processing.
	 * 
	 * @return List of AuditFiles records where the value of the dispatched
	 *         column is false;
	 */
	public List<AuditFiles> findInProgress();

	/**
	 * Deletes records from the audit_files table with ids matching those
	 * supplied.
	 * 
	 * @param ids
	 *            List of ids to delete from the audit_files table.
	 */
	public void deleteById(List<Integer> ids);

	/**
	 * Determine if a record exists in the audit_files table with an id matching
	 * that supplied.
	 * 
	 * @param id
	 * 
	 * @return true if a record with a matching id exists otherwise false.
	 */
	public boolean idExists(String id);

	/**
	 * Create/update a record in the audit_files table using the supplied
	 * entity. Hibernate mapping rules will ensure records will also be
	 * created/updated in the audit_milestones and file_errors tables;
	 * 
	 * @param entity
	 */
	public void save(AuditFiles entity);

	/**
	 * Determine if a record already exists in the audit_files table with a
	 * filename matching that supplied.
	 * 
	 * Script result filename is unique.
	 * 
	 * @param filename
	 *            filename to check.
	 * 
	 * @return True if a record exists with the specified filename, otherwise
	 *         false.
	 */
	public boolean isDuplicate(String filename);
}
