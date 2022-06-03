/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	05/01/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.audit.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;

import com.amtsybex.fieldreach.extract.audit.model.ExtractAudit;

/**
 * Interface to support interaction with the extract_audit table in the Extract
 * Adapter audit database.
 */
public interface ExtractAuditDao {

	/**
	 * Insert or update a record in the extract_audit table of the embedded
	 * database.
	 * 
	 * @param audit
	 */
	public void save(ExtractAudit audit);

	/**
	 * Insert or update a record in the extract_audit table of the embedded
	 * database.
	 * 
	 * @param audit
	 * 
	 * @param xmlData
	 *            Data to create a CLOB to store the XML data in the filecontent
	 *            column.
	 */
	public void save(ExtractAudit audit, String xmlData);

	/**
	 * Determine if a record exists in the extract_audit table with an id
	 * matching that supplied.
	 * 
	 * @param id
	 * 
	 * @return true if a record with a matching id exists otherwise false.
	 */
	public boolean idExists(String id);

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
	public ExtractAudit find(String id) throws HibernateException;

	/**
	 * Get list of ids of records from the extract_audit table that have a
	 * extractdate < the supplied date.
	 */
	public List<String> getMaintenanceDeletionCandidates(Date date);

	/**
	 * Deletes records from the extract_audit table with returnIds matching
	 * those supplied. Associated records will be deleted from extract_errors
	 * and redelivery tables.
	 * 
	 * @param ids
	 *            List of returnIds to delete from the extract_audit table.
	 */
	public void deleteById(List<String> ids);

}
