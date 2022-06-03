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

import com.amtsybex.fieldreach.xmlloader.audit.dao.MilestoneValuesDao;
import com.amtsybex.fieldreach.xmlloader.audit.model.MilestoneValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO to support interaction with the milestone_values table in the XML loader
 * audit database.
 */
public class MilestoneValuesDaoImpl extends GenericDAO implements
		MilestoneValuesDao {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory
			.getLogger(MilestoneValuesDaoImpl.class.getName());

	/*-------------------------------------------
	 - Interface implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.audit.dao.MilestoneValuesDao#find()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MilestoneValues> find() {
		
		if(log.isDebugEnabled())
			log.debug(">>> find");

		List<MilestoneValues> milestoneValues;

		Session session = super.getSession();

		Query query = session.createQuery("FROM MilestoneValues");

		milestoneValues = (List<MilestoneValues>) query.list();

		if(log.isDebugEnabled())
			log.debug("<<< find");

		return milestoneValues;
	}

}
