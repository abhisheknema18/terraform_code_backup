/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	28/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.dao.impl;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.amtsybex.fieldreach.xmlloader.audit.dao.StatusValuesDao;
import com.amtsybex.fieldreach.xmlloader.audit.model.StatusValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO to support interaction with the status_values table in the XML loader
 * audit database.
 */
public class StatusValuesDaoImpl extends GenericDAO implements StatusValuesDao {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory
			.getLogger(StatusValuesDaoImpl.class.getName());

	/*-------------------------------------------
	 - Interface implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.dao.StatusValuesDao#find()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StatusValues> find() {
		
		if(log.isDebugEnabled())
			log.debug(">>> find");

		List<StatusValues> statusValues;

		Session session = super.getSession();

		Query query = session.createQuery("FROM StatusValues");

		statusValues = (List<StatusValues>) query.list();

		if(log.isDebugEnabled())
			log.debug("<<< find");

		return statusValues;
	}

}
