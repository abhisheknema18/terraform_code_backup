/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	29/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.amtsybex.fieldreach.xmlloader.audit.dao.AuditMilestonesDao;
import com.amtsybex.fieldreach.xmlloader.audit.model.AuditMilestones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO to support interaction with the audit_milestones table in the XML loader
 * audit database.
 */
public class AuditMilestonesDaoImpl extends GenericDAO implements
		AuditMilestonesDao {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory
			.getLogger(AuditMilestonesDaoImpl.class.getName());

	/*-------------------------------------------
	 - Interface implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.audit.dao.AuditMilestonesDao#find()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AuditMilestones> find() {

		if(log.isDebugEnabled())
			log.debug(">>> find");

		List<AuditMilestones> auditMilestones;

		Session session = super.getSession();

		Query q = session.createQuery("FROM AuditMilestones");

		auditMilestones = (List<AuditMilestones>) q.list();

		if(log.isDebugEnabled())
			log.debug("<<< find");

		return auditMilestones;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.dao.AuditMilestonesDao#
	 * getMaintenanceDeletionCandidates(java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getMaintenanceDeletionCandidates(Date date) {

		if(log.isDebugEnabled())
			log.debug(">>> getMaintenanceDeletionCandidates date=xxx");

		Session session = super.getSession();

		List<Integer> ids;

		Query q = session
				.createQuery("SELECT id FROM AuditMilestones WHERE milestoneDate<:p_md");
		q.setParameter("p_md", date);

		ids = (List<Integer>) q.list();

		if(log.isDebugEnabled())
			log.debug("<<< getMaintenanceDeletionCandidates");

		return ids;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.audit.dao.AuditMilestonesDao#deleteById
	 * (java.util.List)
	 */
	@Override
	public void deleteById(List<Integer> ids) {
		
		if(log.isDebugEnabled())
			log.debug(">>> deleteById ids=xxx");

		Session session = super.getSession();

		Query q = session
				.createQuery("DELETE FROM AuditMilestones WHERE id IN (:p_ids)");
		q.setParameterList("p_ids", ids);

		q.executeUpdate();
		
		if(log.isDebugEnabled())
			log.debug("<<< deleteById");
	}

}
