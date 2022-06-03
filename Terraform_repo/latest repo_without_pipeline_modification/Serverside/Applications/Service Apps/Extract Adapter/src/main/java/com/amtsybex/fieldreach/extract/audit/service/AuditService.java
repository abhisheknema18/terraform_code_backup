/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	18/12/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.audit.service;

import java.util.List;

import org.hibernate.HibernateException;

import com.amtsybex.fieldreach.extract.audit.model.ExtractAudit;
import com.amtsybex.fieldreach.extract.audit.model.Redelivery;

/**
 * Interface to support interaction with the Extract Adapter local audit
 * database.
 */
public interface AuditService {

	/**
	 * Set the status a script result will be set to if an error occurs during
	 * the extraction process.
	 * 
	 * @param errorStatus
	 */
	public void setErrorStatus(String errorStatus);
	
	/**
	 * Generates a unique id for an audit record.
	 * 
	 * @return
	 */
	public String generateId();

	/**
	 * Find record in the extract_audit table of the embedded database with an
	 * id matching that supplied.
	 * 
	 * @param id
	 *            Id of the record to search for.
	 * 
	 * @return ExtractAudit object with an id matching that supplied. Null is
	 *         returned if no record is found.
	 */
	public ExtractAudit findExtractAudit(String id) throws HibernateException;

	/**
	 * Create a new record in the extract_audit table of the embedded database
	 * signifying that a result has been identified as an extraction candidate.
	 * 
	 * @param returnId
	 *            ReturnId of the script result that is an extraction candidate.
	 * 
	 * @param instance
	 *            Fieldreach instance the result is located. Null indicates the
	 *            default instance.
	 * 
	 * @return
	 * 
	 *         Returns the unique Id generated when updated the extract_audit
	 *         table.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database
	 */
	public String logExtractionCandidate(int returnId, String instance)
			throws HibernateException;

	/**
	 * Update the record in the extract_audit table of the embedded database and
	 * also create a record in the audit_errors tables to log an extraction
	 * error.
	 * 
	 * @param id
	 *            Unique id of the audit record to be updated.
	 * 
	 * @param errorDetail
	 *            The reason that extraction of the script result failed.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database
	 * 
	 */
	public void logExtractionError(String id, String errorDetail)
			throws HibernateException;

	/**
	 * Update the record in the extract_audit table of the embedded database and
	 * also create a record in the redelivery table to allow re-delivery to be
	 * attempted.
	 * 
	 * @param id
	 *            Unique id of the audit record to be updated.
	 * 
	 * @param content
	 *            The content of the extracted XML message.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database
	 */
	public void logDispatchException(String id, String content)
			throws HibernateException;

	/**
	 * Update the audit record in the embedded database to indicate that an
	 * error occurred during attempted redelivery of the extracted XML message
	 * to its destination, and update the redelivery table.
	 * 
	 * @param auditId
	 *            id of the record in the audit database to update.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public void logRedeliveryException(String id) throws HibernateException;

	/**
	 * Update the record in the extract_audit table of the embedded database to
	 * indicate that the extract process has completed. Clear down any entries
	 * the redelivery and audit_errors tables.
	 * 
	 * @param id
	 *            Unique id of the audit record to be updated.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database
	 */
	public void logDispatchSuccess(String id) throws HibernateException;

	/**
	 * Get all records from the redelivery table where the attempts column is <
	 * the supplied value.
	 * 
	 * @param maxAttempts
	 * 
	 * @return List of Redelivery objects.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database
	 */
	public List<Redelivery> findRedeliveryCandidates(int maxAttempts)
			throws HibernateException;

	/**
	 * Delete all records from the redelivery table where the attempts column is
	 * >= the supplied value. Also update the audit_files table to indicate
	 * extraction failed and create an entry in the audit_errors table
	 * indicating redelivery failed.
	 * 
	 * @param maxAttempts
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database
	 */
	public void cancelExpiredRedeliveries(int maxAttempts)
			throws HibernateException;
	
	/**
	 * Perform maintenance tasks on the extract adapter audit database.
	 * 
	 * Maintenance tasks include: 1) Delete all records that are older than than
	 * the value of the maxRecordAge parameter.
	 * 
	 * @param maxRecordAge
	 *            Delete records from extract_audit table where extractdate
	 *            < maxRecordAge parameter. Associated records will be deleted
	 *            from extract_errors and redelivery tables.
	 * 
	 * @throws HibernateException
	 *             An error occurs when accessing the audit database.
	 */
	public void performAuditDbMaintenance(int maxRecordAge)
			throws HibernateException;
}
