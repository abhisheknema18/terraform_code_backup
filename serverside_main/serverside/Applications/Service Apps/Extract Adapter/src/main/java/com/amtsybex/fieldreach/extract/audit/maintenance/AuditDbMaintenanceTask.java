/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	26/02/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.audit.maintenance;

import com.amtsybex.fieldreach.exception.ConfigException;

/**
 * Interface to support maintenance of the extract adapter audit database.
 */
public interface AuditDbMaintenanceTask extends Runnable {

	/**
	 * Performs maintenance on the extract adapter audit database. Maintenance tasks
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
	 * @throws ConfigException
	 *             Retention period and/or maintenance interval have not been
	 *             configured.
	 */
	public void startMaintenanceTask() throws ConfigException;

	/**
	 * Halts the audit database maintenance thread if it is currently running.
	 */
	public void stopMaintenanceTask();

	/**
	 * Sets the maximum number of days that a record should be retained in the
	 * extract adapter audit database.
	 * 
	 * @param retentionPeriod
	 *            Number of days records can be retained for.
	 * 
	 * @throws ConfigException
	 *             retentionPeriod <= 0.
	 */
	public void setRetentionPeriod(int retentionPeriod)
			throws ConfigException;
}
