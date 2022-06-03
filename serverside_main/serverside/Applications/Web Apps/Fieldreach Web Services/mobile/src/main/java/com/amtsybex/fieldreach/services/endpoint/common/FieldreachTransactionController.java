/**
 * Author:  T Murray
 * Date:    27/09/2012
 * Project: FDE019
 * 
 * Copyright AMT-Sybex 2012
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 */
package com.amtsybex.fieldreach.services.endpoint.common;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.services.exception.TransacationAlreadyProcessedException;
import com.amtsybex.fieldreach.services.exception.WorkOrderNotFoundException;
import com.amtsybex.fieldreach.services.messages.request.FRTransaction;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;

/**
 * Interface to allow common operations around Fieldreach transactions. These
 * operations are not specific to any type of web service implementation such as
 * SOAP or REST.
 * 
 * FDE020 TM 11/03/2013 Moved code from rest controller to this generic
 * controller and added new interface methods
 */
public interface FieldreachTransactionController {

	/**
	 * Process a WORKSTATUS transaction message. The method will be responsible
	 * for updating the workissued and workstatushistory tables as well as
	 * deleting/updating any workorder files if required.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param trans
	 *            FRTransaction object representing the WORKSTATUS xml supplied.
	 * 
	 * @param transFileName
	 *            Name of the transaction file. This can be null.
	 * 
	 * @throws WorkOrderNotFoundException
	 *             If the work order referenced in the WORKSTATUS message can
	 *             not be found in the Fieldreach database.
	 * 
	 * @throws ResourceTypeNotFoundException
	 *             If the location that work orders are stored on the server can
	 *             not be resolved.
	 * 
	 * @throws TransactionAlreadyProcessedException
	 *             If a record exists in the workstatushistory table with same
	 *             transFilename supplied for the work order the transaction
	 *             references.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured.  An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public void processWorkStatusTransaction(String frInstance,
			FRTransaction trans, String transFileName) throws WorkOrderNotFoundException,
			ResourceTypeNotFoundException,
			TransacationAlreadyProcessedException, FRInstanceException;
	
	// FDP1255 TM 01/11/2016
	/**
	 * Process a HEARTBEAT transaction message. The method will be responsible
	 * for updating the UserActivityStatus and UserLocationHistory tables.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param trans
	 *            FRTransaction object representing the HEARTBEAT xml supplied.
	 * 
	 * @param transFileName
	 *            Code of the application the request originated from.
	 *            
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public void processHeartbeatTransaction(String frInstance,
			FRTransaction trans, String appCode) throws  FRInstanceException;

	// End FDP1255
	
	/**
	 * Indicates if WORKSTATUS transaction files are to be retained when they
	 * are processed.
	 * 
	 * @return true if WORKSTATUS transactions are to be retained, otherwise
	 *         false is returned.
	 */
	public boolean retainWorkStatusTrans();

	/**
	 * Determines if WORKSTATUS transactions are to be retained and dispatches
	 * the transaction to the configured JMS queue or directory if the
	 * transaction work status matches one of those configured for retention.
	 * 
	 * @param workstatus
	 *            The work status value of the WORKSTATUS transaction being
	 *            processed.
	 * 
	 * @param message
	 *            The WORKSTATUS transaction XML.
	 * 
	 * @param transFileName
	 *            Name of the transaction file. being processed.
	 * 
	 * @return True if the transaction is dispatched, and false if it is not.
	 */
	public boolean dispatchWorkStatusTransaction(String workstatus,
			String message, String transFileName);

}
