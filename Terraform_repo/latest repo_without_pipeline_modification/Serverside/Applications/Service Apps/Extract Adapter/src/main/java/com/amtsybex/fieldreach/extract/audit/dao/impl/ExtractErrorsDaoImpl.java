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

import com.amtsybex.fieldreach.extract.audit.dao.ExtractErrorsDao;
import com.amtsybex.fieldreach.extract.audit.model.ExtractErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO to support interaction with the extract_errors table in the Extract
 * Adapter audit database.
 */
public class ExtractErrorsDaoImpl extends GenericDAO implements
		ExtractErrorsDao {

	/*-------------------------------------------
	 - Member Variables 
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory
			.getLogger(ExtractErrorsDaoImpl.class.getName());

	/*-------------------------------------------
	 - Interface implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.audit.dao.ExtractErrorsDao#find()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ExtractErrors> find() {

		if (log.isDebugEnabled())
			log.debug(">>> find");

		List<ExtractErrors> fileErrors;

		Session session = super.getSession();

		Query query = session.createQuery("FROM ExtractErrors");

		fileErrors = (List<ExtractErrors>) query.list();

		if (log.isDebugEnabled())
			log.debug("<<< find");

		return fileErrors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.dao.ExtractErrorsDao#deleteById
	 * (java.util.List)
	 */
	@Override
	public void deleteById(List<String> ids) {

		if (log.isDebugEnabled())
			log.debug(">>> deleteById ids=xxx");

		Session session = super.getSession();

		Query q = session
				.createQuery("DELETE FROM ExtractErrors WHERE id IN (:p_ids)");
		q.setParameterList("p_ids", ids);

		q.executeUpdate();

		if (log.isDebugEnabled())
			log.debug("<<< deleteById");
	}
	
}
