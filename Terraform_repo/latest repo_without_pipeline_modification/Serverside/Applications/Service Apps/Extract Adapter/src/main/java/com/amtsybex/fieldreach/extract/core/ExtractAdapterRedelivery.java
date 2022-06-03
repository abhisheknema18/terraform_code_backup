/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	10/02/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.core;

import com.amtsybex.fieldreach.exception.ConfigException;

/**
 * Interface to support re-delivery of extracted messages that could not be
 * dispatched at the time of extraction.
 */
public interface ExtractAdapterRedelivery extends Runnable {

	/**
	 * Performs the re-delivery process for messages that need to be re-delivered.
	 */
	public void performRedelivery();

	/**
	 * Starts a thread which will perform the re-delivery process at the 
	 * configured interval.
	 * 
	 * @throws ConfigException
	 *             Re-delivery interval has not been configured correctly.
	 */
	public void startRedeliveryTask() throws ConfigException;

	/**
	 * Halts the re-delivery thread if it is currently running.
	 */
	public void stopRedeliveryTask();

	/**
	 * Set the interval (in milliseconds) at which the re-delivery task should run.
	 * 
	 * @param redeliveryInterval
	 *            Interval at which the re-delivery task executes
	 *            in milliseconds.
	 * 
	 * @throws ConfigException
	 *             redeliveryInterval <= 0.
	 */
	public void setRedeliveryInterval(int redeliveryInterval)
			throws ConfigException;	
	
	/**
	 * Set the interval (in milliseconds) at which the re-delivery task should run.
	 * 
	 * @param maxAttempts
	 *            Maximum number of times re-delivery will be attempted. Value of 0
	 *            or less disables re-delivery.
	 */
	public void setRedeliveryMaxAttempts(int maxAttempts);
	
}
