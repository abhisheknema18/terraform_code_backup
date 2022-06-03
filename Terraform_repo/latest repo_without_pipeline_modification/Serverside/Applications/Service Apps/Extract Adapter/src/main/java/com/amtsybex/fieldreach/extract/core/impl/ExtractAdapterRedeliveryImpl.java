/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	10/02/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.core.impl;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;

import com.amtsybex.fieldreach.backend.model.SystemEventLog;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTCATEGORY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTTYPE;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SEVERITY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SOURCESYSTEM;
import com.amtsybex.fieldreach.backend.service.SystemEventService;
import com.amtsybex.fieldreach.exception.ConfigException;
import com.amtsybex.fieldreach.exception.InstanceNotStartedException;
import com.amtsybex.fieldreach.extract.ExtractCandidate;
import com.amtsybex.fieldreach.extract.audit.model.ExtractAudit;
import com.amtsybex.fieldreach.extract.audit.model.Redelivery;
import com.amtsybex.fieldreach.extract.audit.service.AuditService;
import com.amtsybex.fieldreach.extract.core.ExtractAdapterDispatcher;
import com.amtsybex.fieldreach.extract.core.ExtractAdapterRedelivery;
import com.amtsybex.fieldreach.extract.utils.FEAUtils;
import com.amtsybex.fieldreach.interfaces.ServiceApplication;
import com.amtsybex.fieldreach.utils.Email;

/**
 * Class to support maintenance of the XML loader audit database.
 */
public class ExtractAdapterRedeliveryImpl implements ExtractAdapterRedelivery,
		ApplicationListener<ContextStartedEvent> {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory
			.getLogger(ExtractAdapterRedeliveryImpl.class.getName());

	@Autowired
	private SystemEventService mSystemEventLogger;
	
	private int redeliveryInterval;
	private int maxAttempts;

	private ScheduledExecutorService redeliveryScheduler;

	@Autowired(required = true)
	@Qualifier("fea_auditService")
	private AuditService auditService;

	@Autowired(required = true)
	@Qualifier("fea_dispatcher")
	private ExtractAdapterDispatcher feaDispatcher;

	private static ServiceApplication feaCore = ExtractAdapterCoreImpl.getInstance();

	@Autowired(required = true)
	@Qualifier("fea_notificationEmail")
	private Email feaEmail;
	
	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	private ExtractAdapterRedeliveryImpl() {

		this.redeliveryInterval = -1;
		this.maxAttempts = -1;
		this.redeliveryScheduler = null;

	}
	
	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		if (log.isDebugEnabled())
			log.debug(">>> run");

		try {

			if (this.maxAttempts > 0)
				this.performRedelivery();

		} catch (Exception e) {

			log.error("Exception in Extract Adpater redelivery thread.");
			log.error(e.getMessage());
			
			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "Exception in Extract Adpater redelivery thread.", "Exception in Extract Adpater redelivery thread. " + e.getMessage(), SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P3));

		}

		log.debug("<<< run");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationListener#onApplicationEvent(org
	 * .springframework.context.ApplicationEvent)
	 * 
	 * Don't start the re-delivery thread until after the Spring application
	 * context has fully loaded. When context is loaded a ContextStartEvent will
	 * be fired, this is the signal to start the re-delivery process.
	 */
	@Override
	public void onApplicationEvent(ContextStartedEvent arg0) {

		if (log.isDebugEnabled())
			log.debug(">>> onApplicationEvent arg0=XXX");

		try {

			if (this.maxAttempts > 0) {

				this.startRedeliveryTask();

			} else {

				if (log.isDebugEnabled())
					log.debug("Redelivery strategy not enabled.");
			}

		} catch (ConfigException e) {

			log.error("Failed to start the redelivery process.");
			
			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "Failed to start the redelivery process.", "Failed to start the redelivery process. " , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P1));
			
			throw new RuntimeException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<< onApplicationEvent");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.core.ExtractAdapterRedelivery#
	 * performRedelivery()
	 */
	@Override
	public void performRedelivery() {

		if (log.isDebugEnabled())
			log.debug(">>> performRedelivery");

		try {

			// Get candidates for re-delivery
			List<Redelivery> redeliveries = this.auditService
					.findRedeliveryCandidates(this.maxAttempts);

			for (Redelivery redelivery : redeliveries) {

				ExtractAudit audit = this.auditService
						.findExtractAudit(redelivery.getId());

				ExtractCandidate extract = new ExtractCandidate();

				extract.setAuditId(audit.getId());
				extract.setInstance(audit.getInstance());
				extract.setReturnId(audit.getReturnId());
				extract.setXml(redelivery.getMessage());
				extract.setRedelivery(true);

				// Attempt to dispatch
				this.feaDispatcher.dispatch(extract);
			}

			// Now halt Re-delivery for any messages that exceed the
			// configured number of maximum redelivery attempts.
			this.auditService.cancelExpiredRedeliveries(this.maxAttempts);

		} catch (HibernateException e) {

			log.error("Redelivery Error: " + e.getMessage());

			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "RedeliveryAdapter HibernateException", "RedeliveryAdapter HibernateException. " + e.getMessage(), SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P1));

			this.emailAndShutdown(e);
			return;
		} 

		if (log.isDebugEnabled())
			log.debug("<<< performRedelivery ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.core.ExtractAdapterRedelivery#
	 * startRedeliveryTask()
	 */
	@Override
	public void startRedeliveryTask() throws ConfigException {

		if (log.isDebugEnabled())
			log.debug(">>> startRedeliveryTask");

		if (this.redeliveryInterval == -1)
			throw new ConfigException(
					"Redelivery interval has not been configured.");

		this.redeliveryScheduler = Executors.newSingleThreadScheduledExecutor();

		this.redeliveryScheduler.scheduleAtFixedRate(this, 0,
				this.redeliveryInterval, TimeUnit.MILLISECONDS);

		if (log.isDebugEnabled()) {

			log.debug("Redelivery process started!");
			log.debug("<<< startRedeliveryTask");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.core.ExtractAdapterRedelivery#
	 * stopRedeliveryTask()
	 */
	@Override
	public void stopRedeliveryTask() {

		if (log.isDebugEnabled())
			log.debug(">>> stopRedeliveryTask");

		if (this.redeliveryScheduler != null && this.maxAttempts > 0) {

			this.redeliveryScheduler.shutdownNow();
			this.redeliveryScheduler = null;

			if (log.isDebugEnabled())
				log.debug("Redelivery process stopped!");
		}

		if (log.isDebugEnabled())
			log.debug("<<< stopRedeliveryTask");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.core.ExtractAdapterRedelivery#
	 * setRedeliveryInterval(int)
	 */
	@Override
	public void setRedeliveryInterval(int redeliveryInterval)
			throws ConfigException {

		if (log.isDebugEnabled())
			log.debug(">>> setRedeliveryInterval redeliveryInterval="
					+ redeliveryInterval);

		if (redeliveryInterval <= 0)
			throw new ConfigException(
					"Redelivery interval must be greater than 0.");

		this.redeliveryInterval = redeliveryInterval;

		if (log.isDebugEnabled())
			log.debug("<<< setRedeliveryInterval");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.core.ExtractAdapterRedelivery#
	 * setRedeliveryMaxAttempts(int)
	 */
	@Override
	public void setRedeliveryMaxAttempts(int maxAttempts) {

		if (log.isDebugEnabled())
			log.debug(">>> setRedeliveryMaxAttempts maxAttempts=" + maxAttempts);

		this.maxAttempts = maxAttempts;

		if (log.isDebugEnabled())
			log.debug("<<< setRedeliveryMaxAttempts");
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Perform cleanup when the bean is being destroyed.
	 */
	@PreDestroy
	private void destroy() {

		this.stopRedeliveryTask();
	}

	/**
	 * Send an error email using the configured email notification bean
	 * and then shutdown the extract adapter instance that is running.
	 * 
	 * @param e
	 * Exception to extract a message from to include in the error email body.
	 */
	private void emailAndShutdown(Exception e) {
		
		if (log.isDebugEnabled())
			log.debug(">>> emailAndShutdown e=XXX");
		
		try {

			this.feaEmail
					.sendEmail(FEAUtils.FEA_ERROR_EMAIL_BODY
							+ "\n\nException Detail:\n\n"
							+ e.getMessage());
			
			feaCore.stop();

		} catch (InstanceNotStartedException e1) {
			// Can never be thrown from here.
		}

		if (log.isDebugEnabled())
			log.debug("<<< emailAndShutdown");
	}
	
}
