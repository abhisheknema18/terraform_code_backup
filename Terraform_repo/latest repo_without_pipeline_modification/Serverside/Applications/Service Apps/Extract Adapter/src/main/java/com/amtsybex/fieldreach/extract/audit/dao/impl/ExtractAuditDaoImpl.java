/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	05/01/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.audit.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.amtsybex.fieldreach.extract.audit.dao.ExtractAuditDao;
import com.amtsybex.fieldreach.extract.audit.model.ExtractAudit;
import com.amtsybex.fieldreach.extract.audit.model.Redelivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO to support interaction with the extract_audit table in the Extract
 * Adapter audit database.
 */
public class ExtractAuditDaoImpl extends GenericDAO implements ExtractAuditDao {

	/*-------------------------------------------
	 - Member Variables 
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory
			.getLogger(ExtractAuditDaoImpl.class.getName());

	/*-------------------------------------------
	 - Interface implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.dao.ExtractAuditDao#save(com.amtsybex
	 * .fieldreach.extract.audit.model.ExtractAudit)
	 */
	@Override
	public void save(ExtractAudit audit) {

		if (log.isDebugEnabled())
			log.debug(">>> save audit=XXX");

		Session session = super.getSession();

		session.saveOrUpdate(audit);

		if (log.isDebugEnabled())
			log.debug("<<< save");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.dao.ExtractAuditDao#save(com.amtsybex
	 * .fieldreach.extract.audit.model.ExtractAudit, java.lang.String)
	 */
	@Override
	public void save(ExtractAudit audit, String xmlData) {

		if (log.isDebugEnabled())
			log.debug(">>> save audit=XXX xmlData=XXX");

		Session session = super.getSession();

		Redelivery reDel = audit.getRedelivery();
		
		reDel.setMessage(xmlData);

		session.saveOrUpdate(audit);
		
		if (log.isDebugEnabled())
			log.debug("<<< save");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.dao.ExtractAuditDao#idExists(java
	 * .lang.String)
	 */
	@Override
	public boolean idExists(String id) {

		if (log.isDebugEnabled())
			log.debug(">>> idExists id=" + id);

		boolean result = false;

		Session session = super.getSession();

		ExtractAudit entity = (ExtractAudit) session
				.get(ExtractAudit.class, id);

		if (entity != null)
			result = true;

		if (log.isDebugEnabled())
			log.debug("<<< idExists retval=" + result);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.dao.ExtractAuditDao#find(java.lang
	 * .String)
	 */
	@Override
	public ExtractAudit find(String id) {

		if (log.isDebugEnabled())
			log.debug(">>> find id=" + id);

		Session session = super.getSession();

		ExtractAudit entity = (ExtractAudit) session
				.get(ExtractAudit.class, id);

		if (log.isDebugEnabled())
			log.debug("<<< find");

		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.audit.dao.ExtractAuditDao#
	 * getMaintenanceDeletionCandidates(java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getMaintenanceDeletionCandidates(Date date) {

		if (log.isDebugEnabled())
			log.debug(">>> getMaintenanceDeletionCandidates date=xxx");

		Session session = super.getSession();

		List<String> ids;

		Query q = session
				.createQuery("SELECT id FROM ExtractAudit WHERE auditDate<:p_md");
		q.setParameter("p_md", date);

		ids = (List<String>) q.list();

		if (log.isDebugEnabled())
			log.debug("<<< getMaintenanceDeletionCandidates");

		return ids;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.dao.ExtractAuditDao#deleteById(
	 * java.util.List)
	 */
	@Override
	public void deleteById(List<String> ids) {

		if (log.isDebugEnabled())
			log.debug(">>> deleteById ids=xxx");

		Session session = super.getSession();

		Query q = session
				.createQuery("DELETE FROM ExtractAudit WHERE id IN (:p_ids)");
		q.setParameterList("p_ids", ids);

		q.executeUpdate();

		if (log.isDebugEnabled())
			log.debug("<<< deleteById");
	}

}
