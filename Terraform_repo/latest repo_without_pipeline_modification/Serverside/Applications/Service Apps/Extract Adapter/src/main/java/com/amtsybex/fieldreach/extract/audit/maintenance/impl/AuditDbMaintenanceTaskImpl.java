/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	26/02/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.audit.maintenance.impl;

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
import com.amtsybex.fieldreach.extract.audit.maintenance.AuditDbMaintenanceTask;
import com.amtsybex.fieldreach.extract.audit.service.AuditService;

/**
 * Class to support maintenance of the extract adapter audit database.
 */
public class AuditDbMaintenanceTaskImpl implements AuditDbMaintenanceTask,
		ApplicationListener<ContextStartedEvent> {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory
			.getLogger(AuditDbMaintenanceTaskImpl.class.getName());

	private int retentionPeriod;

	private ScheduledExecutorService auditDbMaintenanceScheduler;

	@Autowired(required = true)
	@Qualifier("fea_auditService")
	private AuditService auditService;
	
	@Autowired
	private SystemEventService mSystemEventLogger;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	private AuditDbMaintenanceTaskImpl() {

		this.retentionPeriod = -1;
		this.auditDbMaintenanceScheduler = null;
		this.auditService = null;
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

			this.performMaintenance();

		} catch (Exception e) {

			log.error("Exception in XML loader audit database maintenance thread.");
			log.error(e.getMessage());
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
	 * Don't start the audit database maintenance thread until after the Spring
	 * application context has fully loaded. When context is loaded a
	 * ContextStartEvent will be fired, this is the signal to start the audit
	 * database maintenance process.
	 */
	@Override
	public void onApplicationEvent(ContextStartedEvent arg0) {

		if (log.isDebugEnabled())
			log.debug(">>> onApplicationEvent arg0=XXX");

		try {

			this.startMaintenanceTask();

		} catch (ConfigException e) {

			log.error("Failed to start audit database maintenance process.");
			
			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "Failed to start audit database maintenance process.", "Failed to start audit database maintenance process. " + e.getLocalizedMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P1));
			
			throw new RuntimeException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<< onApplicationEvent");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.maintenance.AuditDbMaintenanceTask
	 * #performMaintenance()
	 */
	@Override
	public void performMaintenance() {

		if (log.isDebugEnabled())
			log.debug(">>> performMaintenance");

		try {

			this.auditService.performAuditDbMaintenance(this.retentionPeriod);

		} catch (HibernateException e) {

			log.error("EXCEPTION: " + e.getMessage());
		}

		if (log.isDebugEnabled())
			log.debug("<<< performMaintenance ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.maintenance.AuditDbMaintenanceTask
	 * #startMaintenanceTask()
	 */
	@Override
	public void startMaintenanceTask() throws ConfigException {

		if (log.isDebugEnabled())
			log.debug(">>> startMaintenanceTask");

		if (this.retentionPeriod == -1)
			throw new ConfigException(
					"Retention period has not been conifgured.");

		this.auditDbMaintenanceScheduler = Executors
				.newSingleThreadScheduledExecutor();

		this.auditDbMaintenanceScheduler.scheduleAtFixedRate(this, 0,
				24, TimeUnit.HOURS);

		if (log.isDebugEnabled()) {

			log.debug("Audit database maintenance process started!");
			log.debug("<<< startMaintenanceTask");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.maintenance.AuditDbMaintenanceTask
	 * #stopMaintenanceTask()
	 */
	@Override
	public void stopMaintenanceTask() {

		if (log.isDebugEnabled())
			log.debug(">>> stopMaintenanceTask");

		if (this.auditDbMaintenanceScheduler != null) {

			this.auditDbMaintenanceScheduler.shutdownNow();
			this.auditDbMaintenanceScheduler = null;

			if (log.isDebugEnabled())
				log.debug("Audit database maintenance process stopped!");
		}

		if (log.isDebugEnabled())
			log.debug("<<< stopMaintenanceTask");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.audit.maintenance.AuditDbMaintenanceTask
	 * #setRetentionPeriod(int)
	 */
	@Override
	public void setRetentionPeriod(int retentionPeriod)
			throws ConfigException {

		if (log.isDebugEnabled())
			log.debug(">>> setRetentionPeriod retentionPeriod="
					+ retentionPeriod);

		if (retentionPeriod <= 0)
			throw new ConfigException(
					"Retention period must be greater than 0.");

		this.retentionPeriod = retentionPeriod;

		if (log.isDebugEnabled())
			log.debug("<<< setRetentionPeriod");
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Perform cleanup when the bean is being destroyed.
	 */
	@PreDestroy
	private void destroy() {

		this.auditDbMaintenanceScheduler.shutdown();

	}

}
