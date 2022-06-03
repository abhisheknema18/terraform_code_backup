/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	18/12/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.audit.dao.impl;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.amtsybex.fieldreach.extract.audit.dao.RedeliveryDao;
import com.amtsybex.fieldreach.extract.audit.model.Redelivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO to support interaction with the redelivery table in the Extract Adapter
 * audit database.
 */
public class RedeliveryDaoImpl extends GenericDAO implements RedeliveryDao {

	/*-------------------------------------------
	 - Member Variables 
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory.getLogger(RedeliveryDaoImpl.class
			.getName());

	/*-------------------------------------------
	 - Interface implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.audit.dao.RedeliveryDao#find()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Redelivery> find() {

		if (log.isDebugEnabled())
			log.debug(">>> find");

		List<Redelivery> redeliveries;

		Session session = super.getSession();

		Query query = session.createQuery("FROM Redelivery");

		redeliveries = (List<Redelivery>) query.list();

		if (log.isDebugEnabled())
			log.debug("<<< find");

		return redeliveries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.audit.dao.RedeliveryDao#find(int)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Redelivery> findAttemptsLessThan(int maxAttempts) {

		if (log.isDebugEnabled())
			log.debug(">>> findAttemptsLessThan maxAttempts=" + maxAttempts);

		List<Redelivery> redeliveries;

		Session session = super.getSession();

		Query query = session
				.createQuery("FROM Redelivery WHERE attempts <:p_ma");
		query.setParameter("p_ma", maxAttempts);

		redeliveries = (List<Redelivery>) query.list();

		if (log.isDebugEnabled())
			log.debug("<<< findAttemptsLessThan");

		return redeliveries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.audit.dao.RedeliveryDao#
	 * findAttemptsGreaterThan(int)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Redelivery> findAttemptsGreaterThan(int maxAttempts) {

		if (log.isDebugEnabled())
			log.debug(">>> findAttemptsGreaterThan maxAttempts=" + maxAttempts);

		List<Redelivery> redeliveries;

		Session session = super.getSession();

		Query query = session
				.createQuery("FROM Redelivery WHERE attempts >=:p_ma");
		query.setParameter("p_ma", maxAttempts);

		redeliveries = (List<Redelivery>) query.list();

		if (log.isDebugEnabled())
			log.debug("<<< findAttemptsGreaterThan");

		return redeliveries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.dao.RedeliveryDao#deleteById(java
	 * .util.List)
	 */
	@Override
	public void deleteById(List<String> ids) {

		if (log.isDebugEnabled())
			log.debug(">>> deleteById ids=xxx");

		Session session = super.getSession();

		Query q = session
				.createQuery("DELETE FROM Redelivery WHERE id IN (:p_ids)");
		q.setParameterList("p_ids", ids);

		q.executeUpdate();

		if (log.isDebugEnabled())
			log.debug("<<< deleteById");
	}

}
