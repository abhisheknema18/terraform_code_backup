/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	18/12/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.amtsybex.fieldreach.exception.InstanceNotStartedException;
import com.amtsybex.fieldreach.extract.core.impl.ExtractAdapterCoreImpl;
import com.amtsybex.fieldreach.interfaces.ServiceApplication;
import com.amtsybex.fieldreach.utils.Email;

/**
 * Utility class to provide common functions and constants for use throughout
 * the entire Extract Adapter application
 */
public class FEAUtils implements ApplicationContextAware {

	private static final Logger log = LoggerFactory
			.getLogger(FEAUtils.class.getName());

	@SuppressWarnings("unused")
	private ApplicationContext ctx;
	
	/*-------------------------------------------
	 - Constants
	 --------------------------------------------*/

	// Application Constants

	public static final String FEA_EXTRACT_TASK_BEAN = "fea_extractTask";

	public static final String DEFAULT_ERROR_STATUS = "ERROR";

	public static final int EXTRACT_ALL_EVENT_ID = 1;

	// Audit Database Constants
	public static final String AUDIT_DATASOURCE_BEAN_NAME = "fea_auditDataSource";

	public static final String FEA_ERROR_EMAIL_BODY = "The Fieldreach Extract adapter has encountered a "
			+ "problem and has shutdown.";

	private static ServiceApplication feaCore = ExtractAdapterCoreImpl.getInstance();
	private static Email feaEmail;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	private FEAUtils() {

		log.debug(">>> ExtractAdapterUtils");

		log.debug("<<< ExtractAdapterUtils");
	}

	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	/**
	 * Get the current date in a format that can be inserted into the audit
	 * database.
	 * 
	 * @return
	 */
	public static java.sql.Date getAuditDbCurrentDate() {

		if (log.isDebugEnabled())
			log.debug(">>> getAuditDbCurrentDate");

		java.sql.Date retval = null;

		Date now = new Date();
		retval = new java.sql.Date(now.getTime());

		if (log.isDebugEnabled())
			log.debug("<<< getAuditDbCurrentDate retval=XXX");

		return retval;
	}

	/**
	 * Get the current time in a format that can be inserted into the audit
	 * database.
	 * 
	 * @return
	 */
	public static java.sql.Time getAuditDbCurrentTime() {

		if (log.isDebugEnabled())
			log.debug(">>> getAuditDbCurrentTime");

		java.sql.Time retval = null;

		Date now = new Date();
		retval = new java.sql.Time(now.getTime());

		if (log.isDebugEnabled())
			log.debug("<<< getAuditDbCurrentTime retval=XXX");

		return retval;
	}

	/**
	 * Convert the supplied Clob object to a string.
	 * 
	 * @param clob
	 * 
	 * @return
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	public static String clobToString(Clob clob) throws SQLException,
			IOException {

		if (log.isDebugEnabled())
			log.debug(">>> clobToString clob=XXX");

		InputStream in = clob.getAsciiStream();
		StringWriter w = new StringWriter();
		IOUtils.copy(in, w, Charset.defaultCharset());

		if (log.isDebugEnabled())
			log.debug("<<< clobToString retval=XXX");

		return w.toString();
	}

	/**
	 * Send an error email using the configured email notification bean and then
	 * shutdown the extract adapter instance that is running.
	 * 
	 * @param e
	 *            Exception to extract a message from to include in the error
	 *            email body.
	 */
	public static void emailAndShutdown(Exception e) {

		if (log.isDebugEnabled())
			log.debug(">>> emailAndShutdown e=XXX");

		try {

			if (feaEmail != null) {
				
				feaEmail.sendEmail(FEAUtils.FEA_ERROR_EMAIL_BODY
						+ "\n\nException Detail:\n\n" + e.getMessage());
			}
			else {
				
				log.info("Error email will not be sent, email has not been set.");
			}

			feaCore.stop();

		} catch (InstanceNotStartedException e1) {
			// Can never be thrown from here.
		}

		if (log.isDebugEnabled())
			log.debug("<<< emailAndShutdown");
	}
	
	
	/*-------------------------------------------
	 - Setter Methods
	 --------------------------------------------*/
	
	public void setNotificationEmail(Email email) {
		
		feaEmail = email;
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		
		ctx = arg0;
		
	}
	
}
