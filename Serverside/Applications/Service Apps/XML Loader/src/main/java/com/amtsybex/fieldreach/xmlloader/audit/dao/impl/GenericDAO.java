/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	28/07/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.dao.impl;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all other classes in the com.amtsybex.fieldreach.backend.model
 * package. This provides each subclass a facility to set a SessionFactory and
 * fetch Session objects from this SessionFactory.
 */
abstract class GenericDAO {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory.getLogger(GenericDAO.class
			.getName());

	protected SessionFactory sessionFactory;

	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	/**
	 * Returns a Session object which can be used to connect to Hibernate and
	 * perform various Hibernate tasks. The method will try to return a current
	 * session, and if this is not possible, a new Session will be opened and
	 * returned.
	 * 
	 * @return A Session object to allow Hibernate tasks to be performed.
	 */
	public Session getSession() {

		if(log.isDebugEnabled())
			log.debug(">>> getSession");

		Session session = null;

		try {

			session = sessionFactory.getCurrentSession();

		} catch (HibernateException e) {

			try {

				session = sessionFactory.openSession();

			} catch (HibernateException ex) {

				e.printStackTrace();
			}
		}

		if(log.isDebugEnabled())
			log.debug("<<< getSession");

		return session;
	}

	/**
	 * Set the SessionFactory object for this class.
	 * 
	 * @param sf
	 *            Instance of a SessionFactory class.
	 */
	public void setSessionFactory(SessionFactory sf) {

		if(log.isDebugEnabled())
			log.debug(">>> setSessionFactory sf=XXX");

		this.sessionFactory = sf;

		if(log.isDebugEnabled())
			log.debug("<<< setSessionFactory");
	}

}