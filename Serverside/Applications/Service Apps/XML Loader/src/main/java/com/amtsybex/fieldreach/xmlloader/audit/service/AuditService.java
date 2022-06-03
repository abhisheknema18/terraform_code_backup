/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	28/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.service;

import java.util.List;

import org.hibernate.HibernateException;

import com.amtsybex.fieldreach.xmlloader.audit.message.AuditMessage;
import com.amtsybex.fieldreach.xmlloader.audit.model.AuditFiles;
import com.amtsybex.fieldreach.xmlloader.audit.model.AuditMilestones;
import com.amtsybex.fieldreach.xmlloader.audit.model.FileErrors;
import com.amtsybex.fieldreach.xmlloader.audit.model.MilestoneValues;
import com.amtsybex.fieldreach.xmlloader.audit.model.StatusValues;

/**
 * Interface to support interaction with the XML loader local audit database.
 */
public interface AuditService {

	/**
	 * Get all of the records from the audit_files table in the audit database.
	 * 
	 * @return List of AuditFiles objects, where each object represents a record
	 *         in the audit_files table.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public List<AuditFiles> findAuditFiles() throws HibernateException;

	/**
	 * Get records from the audit_files table in the audit database where the id
	 * matches that supplied.
	 * 
	 * @param id
	 *            id of the record to find.
	 * 
	 * @return AuditFiles object where each object represents a record in the
	 *         audit_files table where the id matches that supplied.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public AuditFiles findAuditFile(String id) throws HibernateException;

	/**
	 * Get all of the records from the audit_milestones table in the audit
	 * database.
	 * 
	 * @return List of AuditMilestones objects, where each object represents a
	 *         record in the audit_milestones table.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public List<AuditMilestones> findAuditMilestones()
			throws HibernateException;

	/**
	 * Get all of the records from the status_values table in the audit
	 * database.
	 * 
	 * @return List of StatusValues objects, where each object represents a
	 *         record in the status_values table.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database
	 */
	public List<StatusValues> findStatusValues() throws HibernateException;

	/**
	 * Get all of the records from the milestone_values table in the audit
	 * database.
	 * 
	 * @return List of MilestoneValues objects, where each object represents a
	 *         record in the milestone_values table.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public List<MilestoneValues> findMilestoneValues()
			throws HibernateException;

	/**
	 * Get all of the records from the file_errors table in the audit database.
	 * 
	 * @return List of FileError objects, where each object represents a record
	 *         in the file_errors table.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public List<FileErrors> findFileErrors() throws HibernateException;

	/**
	 * Perform maintenance tasks on the XML loader audit database.
	 * 
	 * Maintenance tasks include: 1) Delete all records that are older than than
	 * the value of the maxRecordAge parameter.
	 * 
	 * @param maxRecordAge
	 *            Delete records from audit_milestones table where milestonedate
	 *            < maxRecordAge parameter. Associated records will be deleted
	 *            from audit_files and file_errors.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public void performAuditDbMaintenance(int maxRecordAge)
			throws HibernateException;

	/**
	 * Generates a unique id for an audit record.
	 * 
	 * @return
	 */
	public String generateId();

	/**
	 * Create records in the audit_files and audit_milestone tables indicating
	 * that a file has been picked up successfully.
	 * 
	 * @param msg
	 *            ScriptResultMessage object populated with the information
	 *            required to create records in the audit_files and
	 *            audit_milestone tables.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public void logPickupSuccess(AuditMessage msg)
			throws HibernateException;

	/**
	 * Create records in the audit_files, audit_milestone and file_errors tables
	 * indicating that a file has been picked up unsuccessfully.
	 * 
	 * @param msg
	 *            ScriptResultMessage object populated with the information
	 *            required to create records in the audit_files, audit_milestone
	 *            and file_errorstables.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public void logPickupError(AuditMessage msg)
			throws HibernateException;

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
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public boolean isDuplicateFile(String filename) throws HibernateException;

	//FDP1165 TM 25/11/2015 - Modified Method signature
	/**
	 * Update the milestone record in audit_milestones using the data supplied
	 * to indicate that the file has been validated successfully.
	 * 
	 * @param msg
	 *            ScriptResultMessage object populated with the information
	 *            required audit_milestones table.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public void logValidationSuccess(AuditMessage msg)
			throws HibernateException;

	/**
	 * Update records in the audit_files, audit_milestone and file_errors tables
	 * indicating that a file has been failed validation.
	 * 
	 * @param msg
	 *            ScriptResultMessage object populated with the information
	 *            required to create records in the audit_files, audit_milestone
	 *            and file_errorstables.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public void logValidationError(AuditMessage msg)
			throws HibernateException;

	/**
	 * Update the milestone record in audit_milestones using the data supplied
	 * to indicate that the file has been loaded successfully.
	 * 
	 * @param msg
	 *            ScriptResultMessage object populated with the information
	 *            required audit_milestones table.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public void logLoadSuccess(AuditMessage msg)
			throws HibernateException;

	/**
	 * Update records in the audit_files, audit_milestone and file_errors tables
	 * indicating that a file has been not been loaded.
	 * 
	 * @param msg
	 *            ScriptResultMessage object populated with the information
	 *            required to create records in the audit_files, audit_milestone
	 *            and file_errorstables.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public void logLoadError(AuditMessage msg)
			throws HibernateException;

	/**
	 * Update records in the audit_files and audit_milestone tables to indicate
	 * that the file has been archived.
	 * 
	 * @param msg
	 *            ScriptResultMessage object populated with the information
	 *            required audit_files and audit_milestone tables.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public void logDispatchArchive(AuditMessage msg)
			throws HibernateException;

	/**
	 * Update records in the audit_files and audit_milestone tables indicating
	 * that a file has been dispatched to the error directory.
	 * 
	 * @param msg
	 *            ScriptResultMessage object populated with the information
	 *            required to create records in the audit_files and
	 *            audit_milestone tables.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public void logDispatchError(AuditMessage msg)
			throws HibernateException;

	/**
	 * Update records in the audit_files, audit_milestone and file_errors tables
	 * indicating that an error occurring during the dispatch process.
	 * 
	 * @param msg
	 *            ScriptResultMessage object populated with the information
	 *            required to create records in the audit_files, audit_milestone
	 *            and file_errorstables.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public void logDispatcherError(AuditMessage msg)
			throws HibernateException;

	/**
	 * Returns a list of AuditFiles records that have not completed processing.
	 * 
	 * @return List of AuditFiles records where the value of the dispatched
	 *         column is false;
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public List<AuditFiles> getInProgressFiles() throws HibernateException;

}
