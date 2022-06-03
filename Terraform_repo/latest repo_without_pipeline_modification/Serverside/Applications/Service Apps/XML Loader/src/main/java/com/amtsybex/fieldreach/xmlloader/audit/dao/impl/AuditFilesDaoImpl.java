/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	29/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.dao.impl;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.amtsybex.fieldreach.xmlloader.audit.dao.AuditFilesDao;
import com.amtsybex.fieldreach.xmlloader.audit.model.AuditFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO to support interaction with the audit_files table in the XML loader audit
 * database.
 */
public class AuditFilesDaoImpl extends GenericDAO implements AuditFilesDao {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory.getLogger(AuditFilesDaoImpl.class
			.getName());

	/*-------------------------------------------
	 - Interface implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.dao.AuditFilesDao#find()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditFiles> find() {
		
		if(log.isDebugEnabled())
			log.debug(">>> find");

		List<AuditFiles> auditFiles;

		Session session = super.getSession();

		Query query = session.createQuery("FROM AuditFiles");

		auditFiles = query.list();

		if(log.isDebugEnabled())
			log.debug("<<< find");

		return auditFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.audit.dao.AuditFilesDao#find(java.lang
	 * .String)
	 */
	@Override
	public AuditFiles find(String id) {

		if(log.isDebugEnabled())
			log.debug(">>> find id=" + id);

		AuditFiles auditFile;

		Session session = super.getSession();

		Query query = session.createQuery("FROM AuditFiles WHERE id = :p_id");
		query.setParameter("p_id", id);

		auditFile = (AuditFiles) query.uniqueResult();

		log.debug("<<< find");

		return auditFile;
	}

	/*
	 * (non-Javadoc)
	 * @see com.amtsybex.fieldreach.xmlloader.audit.dao.AuditFilesDao#findInProgress()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditFiles> findInProgress() {
		
		if(log.isDebugEnabled())
			log.debug(">>> findInProgress");
		
		List<AuditFiles> auditFiles;

		Session session = super.getSession();

		Query query = session.createQuery("FROM AuditFiles WHERE dispatched = false");
		
		auditFiles = (List<AuditFiles>) query.list();
		
		if(log.isDebugEnabled())
			log.debug("<<< findInProgress");
		
		return auditFiles;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.audit.dao.AuditFilesDao#deleteById(
	 * java.util.List)
	 */
	@Override
	public void deleteById(List<Integer> ids) {

		if(log.isDebugEnabled())
			log.debug(">>> deleteById ids=xxx");

		Session session = super.getSession();

		Query q = session
				.createQuery("DELETE FROM AuditFiles WHERE id IN (:p_ids)");
		q.setParameterList("p_ids", ids);

		q.executeUpdate();
		
		if(log.isDebugEnabled())
			log.debug("<<< deleteById");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.audit.dao.AuditFilesDao#idExists(java
	 * .lang.String)
	 */
	@Override
	public boolean idExists(String id) {
		
		if(log.isDebugEnabled())
			log.debug(">>> idExists id=" + id);

		boolean result = false;

		Session session = super.getSession();

		AuditFiles entity = session.get(AuditFiles.class, id);

		if (entity != null)
			result = true;

		if(log.isDebugEnabled())
			log.debug("<<< idExists retval=" + result);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.audit.dao.AuditFilesDao#save(com.amtsybex
	 * .fieldreach.xmlloader.audit.model.AuditFiles)
	 */
	@Override
	public void save(AuditFiles entity) {

		if(log.isDebugEnabled())
			log.debug(">>> save entity=XXX");

		Session session = super.getSession();

		session.save(entity);

		if(log.isDebugEnabled())
			log.debug("<<< save");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.audit.dao.AuditFilesDao#isDuplicate
	 * (java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean isDuplicate(String filename) {

		if(log.isDebugEnabled())
			log.debug(">>> isDuplicate filename=" + filename);

		boolean isDuplicate = false;

		Session session = super.getSession();

		Query q = session
				.createQuery("FROM AuditFiles WHERE filename = :p_filename");
		q.setParameter("p_filename", filename);

		List<AuditFiles> records = (List<AuditFiles>) q.list();

		if (!records.isEmpty())
			isDuplicate = true;

		if(log.isDebugEnabled())
			log.debug("<<< isDuplicate retval=" + isDuplicate);

		return isDuplicate;
	}

}
