/**
 * Author:  T Murray
 * Date:    27/09/2012
 * Project: FDE019
 * 
 * Copyright AMT-Sybex 2012
 * 
 * FDE020 TM 11/03/2013
 * Moved code from rest controller to this generic controller and added new
 * interface methods
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Code Re-factoring
 */
package com.amtsybex.fieldreach.services.endpoint.common.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.instance.InstanceManager;
import com.amtsybex.fieldreach.backend.instance.Transaction;
import com.amtsybex.fieldreach.backend.model.UserLocationHistory;
import com.amtsybex.fieldreach.backend.model.WorkIssued;
import com.amtsybex.fieldreach.backend.model.WorkStatusHistory;
import com.amtsybex.fieldreach.backend.model.pk.UserLocationHistoryId;
import com.amtsybex.fieldreach.backend.model.pk.WorkStatusHistoryId;
import com.amtsybex.fieldreach.backend.service.FieldreachTransactionService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.services.endpoint.common.FieldreachTransactionController;
import com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus.WORKSTATUSDESIGNATION;
import com.amtsybex.fieldreach.services.exception.TransacationAlreadyProcessedException;
import com.amtsybex.fieldreach.services.exception.WorkOrderNotFoundException;
import com.amtsybex.fieldreach.services.jms.FieldreachTransactionJMSController;
import com.amtsybex.fieldreach.services.messages.request.FRTransaction;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

public class FieldreachTransactionControllerImpl implements FieldreachTransactionController {

	private static Logger log = LoggerFactory.getLogger(FieldreachTransactionControllerImpl.class.getName());

	private FieldreachTransactionService fieldreachTransactionService;
	private FieldreachTransactionJMSController fieldreachTransactionJMSController;
	private WorkOrderController workOrderController;

	// Properties for retaining WORKSTATUS transactions.
	private boolean retainWorkStatusTrans;
	private String retainWorkStatusTransDispatchType;

	private String retainWorkStatusTransDispatchDest;

	private List<String> retainWorkStatusTransValues = new ArrayList<String>();

	@Autowired
	private InstanceManager instanceManager;
	
	// FDP1255 TM 01/11/2016
	@Autowired
	private UserService userService;
	

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	/**
	 * Default constructor.
	 * 
	 * Responsible for loading the properties in app.properties file that
	 * dictate if specific types of transaction should be retained.
	 */
	public FieldreachTransactionControllerImpl(
			@Value( "${" + Utils.FRT_WORKSTATUS_DEST_PROP + "}" ) String retainWorkStatusTransDispatchDest,
			@Value( "${" + Utils.FRT_WORKSTATUS_DEST_TYPE_PROP + "}" ) String destType,
			@Value( "${" + Utils.FRT_WORKSTATUS_RETAIN_LIST + "}" ) String statusList
			) {

		if (log.isDebugEnabled())
			log.debug(">>> FieldreachTransactionControllerImpl");

		this.retainWorkStatusTransDispatchDest = retainWorkStatusTransDispatchDest;
		// Determine if Fieldreach transaction messages should be retained.
		// and if so fetch the retention configuration of each transaction type.
		// Retention of WORKSTATUS transactions will only be enabled if the
		// destination type property is supplied and contains a value of JMS
		// or DIR.

		if (destType != null && (destType.trim().equalsIgnoreCase(Utils.FRT_JMS_DEST_TYPE)
				|| destType.trim().equalsIgnoreCase(Utils.FRT_DIR_DEST_TYPE))) {

			// Indicate WORKSTATUS transaction should be retained and store
			// the retention type.
			retainWorkStatusTrans = true;
			retainWorkStatusTransDispatchType = destType;

			// Get the list of work status values that should trigger
			// retention
			// of a WORKSTATUS transaction. These values will be converted
			// to upper case.
			if (statusList != null) {

				log.debug("Selected WORKSTATUS transactions will be retained:");

				String[] temp = statusList.split(",");

				for (String status : temp) {

					log.debug("Work Status = " + status);

					if (!status.trim().equals(""))
						retainWorkStatusTransValues.add(status.trim().toUpperCase());
				}

			} else {

				retainWorkStatusTrans = false;
				log.debug("No WORKSTATUS transactions will be retained.");
			}

			if (retainWorkStatusTransValues.size() == 0) {

				retainWorkStatusTrans = false;
				log.debug("No WORKSTATUS transactions will be retained.");
			}

		} else {

			log.debug("WORKSTATUS transactions will not be retained.");
		}

	

		if (log.isDebugEnabled())
			log.debug("<<< FieldreachTransactionControllerImpl");
	}

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.common.
	 * FieldreachTransactionController
	 * #processWorkStatusTransaction(java.lang.String,
	 * com.amtsybex.fieldreach.services.messages.FRTransaction,
	 * java.lang.String)
	 */
	@Override
	public void processWorkStatusTransaction(String frInstance, FRTransaction trans, String transFileName) throws WorkOrderNotFoundException, ResourceTypeNotFoundException,
			TransacationAlreadyProcessedException, FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> processWorkStatusTransaction");

		Transaction dbtrans = this.instanceManager.getTransaction(frInstance);

		try {

			// Check to see if the transaction has already been processed.
			boolean alreadyProcessed = fieldreachTransactionService.workStatusTransactionExists(frInstance,
					transFileName);

			if (alreadyProcessed) {

				throw new TransacationAlreadyProcessedException("Transaction already processed.");
			}

			// Check an entry exists in the work issued table.
			WorkIssued objWO = null;
			String district = trans.getItem().getTrans().getDistrictCode().trim();
			String woNo = trans.getItem().getTrans().getWorkOrderNo();

			if (district != null && !district.equals("")) {

				objWO = workOrderController.getWorkIssuedRecord(frInstance, woNo, district);
			} else {

				objWO = workOrderController.getWorkIssuedRecord(frInstance, woNo, Utils.WORKORDER_DEFAULT_DISTRICT);
			}

			// Work Order not found in the work issued table so throw exception.
			if (objWO == null) {

				throw new WorkOrderNotFoundException("Work Order not found. Unable to process " + "transaction.");
			}

			
			
			// FDP1123 TM 06/08/2015
			// Check to see if the work order is closed before proceeding.
			boolean canProcess = true;

			//FDE053 - MC - get work status from db
			if (workOrderController.getWorkStatuses().statusIsDesignated(frInstance, objWO.getWorkStatus(), WORKSTATUSDESIGNATION.CLOSED)) {

				canProcess = false;

				String sep = System.getProperty("line.separator");

				log.info("Workorder has been closed, transaction will not be processed. " + sep + "Workorder No: "
						+ trans.getItem().getTrans().getWorkOrderNo() + sep + "/nDistrictCode: "
						+ trans.getItem().getTrans().getDistrictCode() + sep + "/nWorkStatus: "
						+ trans.getItem().getTrans().getWorkStatus() + sep + "/nUserCode No: "
						+ trans.getItem().getUserCode());

			}
			
			//FDP1257 - MC - 09/11/2016
			boolean updateWorkIssued = false;
			
			if(canProcess){
				
				updateWorkIssued = true;
				 
				//FDE053 - MC - get work status from db
				if (workOrderController.getWorkStatuses().statusIsDesignated(frInstance, trans.getItem().getTrans().getWorkStatus(), WORKSTATUSDESIGNATION.CANTDO)) {
					//FDP1257 - MC - 09/11/2016
					if(!StringUtils.isEmpty(objWO.getUserCode())){
					
						if(!trans.getItem().getUserCode().equalsIgnoreCase(objWO.getUserCode())){
							
							//Not the assigned user so cannot CANTDO this work order.
							updateWorkIssued = false;
						}
					}else{

						String cantDoWorkStatus = workOrderController.getWorkStatuses().getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CANTDO);
						
						if(!fieldreachTransactionService.workStatusCantDoByAllOtherUsersInWorkGroup(frInstance, trans.getItem().getUserCode(), objWO.getWorkOrderNo(), objWO.getWorkgroupCode(), cantDoWorkStatus, objWO.getDistrictCode())){
							updateWorkIssued = false;
						}
						
					}
				}else if(workOrderController.getWorkStatuses().statusIsDesignated(null,  trans.getItem().getTrans().getWorkStatus(), WORKSTATUSDESIGNATION.OPERATIONAL)) {
					
					updateWorkIssued = false;
					
				}else if(!workOrderController.getWorkStatuses().isStatusConfigured(frInstance, trans.getItem().getTrans().getWorkStatus())){
					//FDP1257 - MC - 09/11/2016
					canProcess = false;

					String sep = System.getProperty("line.separator");

					log.info("Workorder Status is not a known WORKSTATUS, transaction will not be processed. " + sep + "Workorder No: "
							+ trans.getItem().getTrans().getWorkOrderNo() + sep + "/nDistrictCode: "
							+ trans.getItem().getTrans().getDistrictCode() + sep + "/nWorkStatus: "
							+ trans.getItem().getTrans().getWorkStatus() + sep + "/nUserCode No: "
							+ trans.getItem().getUserCode());
				}
			}


			
			// End FDP1123

			// FDE019 TTR 27/11/2012
			// LogDate and LogTime type change to BigInteger to prevent errors
			// when unmarshalling when values have leading 0

			// FDP1123 TM 06/08/2015
			// Only update the WorkIssued table if the transaction can be
			// processed.
			if (canProcess && updateWorkIssued) {

				objWO.setWorkStatus(trans.getItem().getTrans().getWorkStatus());
				objWO.setWorkStatusDate(trans.getItem().getLogDate().intValue());
				objWO.setWorkStatusTime(Integer.parseInt(trans.getItem().getLogTime()));

				if (trans.getItem().getTrans().getAdditionalText() != null)
					objWO.setAdditionalText(trans.getItem().getTrans().getAdditionalText());

				// Save changes to the WorkIssued table, throwing an exception
				// if
				// the database does not get updated.

				// FDE026 TM 15/09/2014
				// Multi instance backend support
				workOrderController.saveWorkIssued(frInstance, objWO);
				// End FDE026

			}
			// End FDP1123

			// Create a record in the WorkStatusHistory table
			WorkStatusHistory objWSH = new WorkStatusHistory();
			WorkStatusHistoryId id = new WorkStatusHistoryId();

			id.setWorkOrderNo(trans.getItem().getTrans().getWorkOrderNo());

			if (trans.getItem().getTrans().getDistrictCode() == null
					|| trans.getItem().getTrans().getDistrictCode().trim().equals("")) {

				id.setDistrictCode(objWO.getDistrictCode());

			} else {

				id.setDistrictCode(trans.getItem().getTrans().getDistrictCode());
			}

			// FDE019 TTR 27/11/2012
			// LogDate and LogTime type change to BigInteger to prevent errors
			// when unmarshalling when values have leading 0

			// FDP1123 TM 06/08/2015
			// If transaction can not be processed prefix status with [*] to
			// denote this.

			if (canProcess)
				id.setWorkStatus(trans.getItem().getTrans().getWorkStatus());
			else
				id.setWorkStatus("[*] " + trans.getItem().getTrans().getWorkStatus());

			// End FDP1123

			id.setWorkStatusDate(trans.getItem().getLogDate().intValue());
			id.setWorkStatusTime(Integer.parseInt(trans.getItem().getLogTime()));
			objWSH.setid(id);

			objWSH.setUserCode(trans.getItem().getUserCode());

			if (trans.getItem().getTrans().getAdditionalText() != null) {

				objWSH.setAdditionalText(trans.getItem().getTrans().getAdditionalText());
			}

			if (transFileName != null)
				objWSH.setTransFileName(transFileName);

			// Update workstatushistory table, throwing an exception if an error
			// occurs updating the database.

			// FDE026 TM 15/09/2014
			// Multi instance backend support
			workOrderController.saveWorkStatusHistory(frInstance, objWSH);
			// End FDE026

			// Delete the existing work order and attachments file if the
			// transaction
			// is "close work' or "cant do" transaction.
			String workStatus = trans.getItem().getTrans().getWorkStatus().toUpperCase();

			//if ((updateWorkIssued && workStatus.equals(workOrderController.getWorkStatuses().getCantDoWorkStatus()))
					//|| workStatus.equals(workOrderController.getWorkStatuses().getCloseWorkStatus())) {
			//FDE051 - MC now only delete for closed work orders
			
			if (workOrderController.getWorkStatuses().statusIsDesignated(frInstance, workStatus, WORKSTATUSDESIGNATION.CLOSED)) {

				// Delete entries from WorkIssuedFileRefs table
				this.workOrderController.deleteAttachments(frInstance, woNo, district);

				this.workOrderController.deleteWorkOrderFromFileSystem(frInstance, objWO);

				this.workOrderController.deleteAttachmentsFromFileSystem(frInstance, objWO);
			}

			// Got this far so commit transaction.
			this.instanceManager.commitTransaction(dbtrans);

		} catch (WorkOrderNotFoundException e) {

			this.instanceManager.rollbackTransaction(dbtrans);
			throw e;

		} catch (ResourceTypeNotFoundException e) {

			this.instanceManager.rollbackTransaction(dbtrans);
			throw e;

		} catch (TransacationAlreadyProcessedException e) {

			this.instanceManager.rollbackTransaction(dbtrans);
			throw e;

		} catch (FRInstanceException e) {

			this.instanceManager.rollbackTransaction(dbtrans);
			throw e;

		} catch (Throwable t) {

			log.error("UNEXPECTED EXCEPTION: " + t.getMessage());

			this.instanceManager.rollbackTransaction(dbtrans);

			throw new RuntimeException(t);
		}

		if (log.isDebugEnabled())
			log.debug("<<< processWorkStatusTransaction");
	}

	// FDP1255 TM 01/11/2016
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.common.
	 * FieldreachTransactionController#processHeartbeatTransaction(java.lang.
	 * String, com.amtsybex.fieldreach.services.messages.request.FRTransaction,
	 * java.lang.String)
	 */
	public void processHeartbeatTransaction(String frInstance, FRTransaction trans, String appCode) throws FRInstanceException {

		if (log.isDebugEnabled()) 
			log.debug(">>> processHeartbeatTransaction");
			
		// Update the UserLocationHistory table if latitude and longitude are present.
		String latitude = trans.getItem().getTrans().getLatitude();
		String longitude = trans.getItem().getTrans().getLongitude();
		
		if (latitude != null && longitude != null) {
			
			UserLocationHistory uhl = new UserLocationHistory();
			UserLocationHistoryId id = new UserLocationHistoryId(trans.getItem().getUserCode(),
					appCode, trans.getItem().getDeviceId(), trans.getItem().getLogDate().intValue(), 
					trans.getItem().getLogTime());
			
			uhl.setId(id);
			uhl.setLatitude(latitude);
			uhl.setLongitude(longitude);
			
			this.userService.updateUserLocationHistory(frInstance, uhl);
		}
			
		if (log.isDebugEnabled())
			log.debug("<<< processHeartbeatTransaction");
			
	}
	// End FDP1255
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.common.
	 * FieldreachTransactionController#retainWorkStatusTrans()
	 */
	@Override
	public boolean retainWorkStatusTrans() {

		if (log.isDebugEnabled()) {

			log.debug(">>> retainWorkStatusTrans");

			log.debug("<<< retainWorkStatusTrans");
		}

		return retainWorkStatusTrans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.common.
	 * FieldreachTransactionController
	 * #dispatchWorkStatusTransaction(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean dispatchWorkStatusTransaction(String workstatus, String message, String transFileName) {

		if (log.isDebugEnabled()) {

			log.debug(">>> dispatchWorkStatusTransaction workstatus=" + Common.CRLFEscapeString(workstatus) + " message=XXX transFileName="
					+ Common.CRLFEscapeString(transFileName));
		}

		// return value
		boolean dispatched = false;

		// Only dispatch WORKSTATUS transaction if message retention has been
		// configured.
		if (retainWorkStatusTrans) {

			// Dispatch to JMS
			if (retainWorkStatusTransDispatchType.equalsIgnoreCase(Utils.FRT_JMS_DEST_TYPE)) {

				// Check if the work status supplied is one that should be
				// retained.
				if (retainWorkStatusTransValues != null
						&& retainWorkStatusTransValues.contains(workstatus.toUpperCase())) {

					if (log.isDebugEnabled())
						log.debug(">>> dispatching WORKSTATUS transaction to JMS queue.");

					try {

						fieldreachTransactionJMSController.dispatchWorkStatusTransaction(message);

						dispatched = true;

						if (log.isDebugEnabled()) {

							log.debug("WORKSTATUS transaction successfully dispatched to JMS queue: "
									+ retainWorkStatusTransDispatchDest);
						}

					} catch (JMSException e) {

						log.error("Unable to dispatch WORKSTATUS Transaction to JMS queue. " + "message= " + Common.CRLFEscapeString(message));
					}

				} else {

					if (log.isDebugEnabled()) {

						log.debug("WORKSTATUS transactions with workstatus of '" + workstatus
								+ "' will not be retained.");
					}
				}
			}
			// Dispatch to Directory
			else if (retainWorkStatusTransDispatchType.equalsIgnoreCase(Utils.FRT_DIR_DEST_TYPE)) {

				// Check if the work status supplied is one that should be
				// retained.
				if (retainWorkStatusTransValues != null
						&& retainWorkStatusTransValues.contains(workstatus.toUpperCase())) {

					log.debug(">>> dispatching WORKSTATUS transaction to directory.");

					// Bug 2261 TM 04/09/2013

					byte[] transBytes;

					try {

						transBytes = message.getBytes(Common.UTF8_ENCODING);

					} catch (UnsupportedEncodingException e) {

						transBytes = message.getBytes();
					}

					dispatched = Common.writeBytesToFile(transBytes, transFileName, retainWorkStatusTransDispatchDest);

					// End Bug 2261

					if (dispatched) {

						if (log.isDebugEnabled()) {

							log.debug("WORKSTATUS transaction successfully dispatched to: "
									+ Common.CRLFEscapeString(retainWorkStatusTransDispatchDest) + "/" + Common.CRLFEscapeString(transFileName));
						}

					} else {

						if (log.isDebugEnabled()) {

							log.debug("Unable to dispatch WORKSTATUS Transaction to directory: "
									+ retainWorkStatusTransDispatchDest);
						}
					}

				} else {

					if (log.isDebugEnabled()) {

						log.debug("WORKSTATUS transactions with workstatus of '" + workstatus
								+ "' will not be retained.");
					}
				}
			}

		} else {

			if (log.isDebugEnabled())
				log.debug("WORKSTATUS transactions are not retained.");
		}

		log.debug("<<< dispatchWorkStatusTransaction");

		return dispatched;
	}

	/*-------------------------------------------
	 - Spring Injection Methods
	 --------------------------------------------*/

	public FieldreachTransactionService getFieldreachTransactionService() {

		return fieldreachTransactionService;
	}

	public void setFieldreachTransactionService(FieldreachTransactionService fieldreachTransactionService) {

		this.fieldreachTransactionService = fieldreachTransactionService;
	}

	public FieldreachTransactionJMSController getFieldreachTransactionJMSController() {

		return fieldreachTransactionJMSController;
	}

	public void setFieldreachTransactionJMSController(
			FieldreachTransactionJMSController fieldreachTransactionJMSController) {

		this.fieldreachTransactionJMSController = fieldreachTransactionJMSController;
	}

	public WorkOrderController getWorkOrderController() {

		return workOrderController;
	}

	public void setWorkOrderController(WorkOrderController workOrderController) {

		this.workOrderController = workOrderController;
	}

}
