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

import com.amtsybex.fieldreach.xmlloader.audit.dao.FileErrorsDao;
import com.amtsybex.fieldreach.xmlloader.audit.model.FileErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO to support interaction with the file_error table in the XML loader audit
 * database.
 */
public class FileErrorsDaoImpl extends GenericDAO implements FileErrorsDao {

	/*-------------------------------------------
	 - Member Variables 
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory.getLogger(FileErrorsDaoImpl.class
			.getName());

	/*-------------------------------------------
	 - Interface implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.audit.dao.FileErrorDao#find()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FileErrors> find() {

		if(log.isDebugEnabled())
			log.debug(">>> find");

		List<FileErrors> fileErrors;

		Session session = super.getSession();

		Query query = session.createQuery("FROM FileErrors");

		fileErrors = (List<FileErrors>) query.list();

		if(log.isDebugEnabled())
			log.debug("<<< find");

		return fileErrors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.audit.dao.FileErrorsDao#deleteById(
	 * java.util.List)
	 */
	@Override
	public void deleteById(List<Integer> ids) {
		
		if(log.isDebugEnabled())	
			log.debug(">>> deleteById ids=xxx");

		Session session = super.getSession();

		Query q = session
				.createQuery("DELETE FROM FileErrors WHERE id IN (:p_ids)");
		q.setParameterList("p_ids", ids);

		q.executeUpdate();

		if(log.isDebugEnabled())
			log.debug("<<< deleteById");
	}
}
