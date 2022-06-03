/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	29/07/2014
 * 
 * Modified by T Murray
 * FDP1165
 * 20/11/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.audit.maintenance;

import com.amtsybex.fieldreach.xmlloader.audit.exception.MaintenanceTaskConfigException;

/**
 * Interface to support maintenance of the XML loader audit database.
 */
public interface AuditDbMaintenanceTask extends Runnable {

	/**
	 * Performs maintenance on the XML loader audit database. Maintenance tasks
	 * include:
	 * 
	 * Deleting audit records that are older than the configured specified
	 * retention period.
	 */
	public void performMaintenance();

	/**
	 * Starts a maintenance thread which will perform maintenance of the audit
	 * database at the configured interval.
	 * 
	 * @throws MaintenanceTaskConfigException
	 *             Retention period and/or maintenance interval have not been
	 *             configured.
	 */
	public void startMaintenanceTask() throws MaintenanceTaskConfigException;

	/**
	 * Halts the audit database maintenance thread if it is currently running.
	 */
	public void stopMaintenanceTask();

	/**
	 * Sets the maximum number of days that a record should be retained in the
	 * XML loader audit database.
	 * 
	 * @param retentionPeriod
	 *            Number of days records can be retained for.
	 * 
	 * @throws MaintenanceTaskConfigException
	 *             retentionPeriod <= 0.
	 */
	public void setRetentionPeriod(int retentionPeriod)
			throws MaintenanceTaskConfigException;
	
}
