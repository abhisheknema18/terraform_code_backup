/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	08/01/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.core.impl;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.NextStatusNotFoundException;
import com.amtsybex.fieldreach.backend.exception.StatusUpdateException;
import com.amtsybex.fieldreach.backend.instance.InstanceManager;
import com.amtsybex.fieldreach.backend.instance.Transaction;
import com.amtsybex.fieldreach.backend.model.SystemEventLog;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTCATEGORY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTTYPE;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SEVERITY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SOURCESYSTEM;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.backend.service.SystemEventService;
import com.amtsybex.fieldreach.exception.ConfigException;
import com.amtsybex.fieldreach.extract.ExtractCandidate;
import com.amtsybex.fieldreach.extract.audit.service.AuditService;
import com.amtsybex.fieldreach.extract.core.ExtractAdapterDispatcher;
import com.amtsybex.fieldreach.extract.jms.FeaJmsController;
import com.amtsybex.fieldreach.extract.utils.FEAUtils;
import com.amtsybex.fieldreach.utils.impl.Common;

/**
 * Class responsible for dispatching messages extracted from the Fieldreach
 * database.
 */
public class ExtractAdapterDispatcherImpl implements ExtractAdapterDispatcher {

	private static final Logger log = LoggerFactory.getLogger(ExtractAdapterDispatcherImpl.class.getName());

	@Autowired
	private SystemEventService mSystemEventLogger;
	
	@Autowired
	private InstanceManager instanceManager;

	@Autowired(required = true)
	@Qualifier("scriptResultsService")
	private ScriptResultsService scriptResultsService;

	@Autowired(required = true)
	@Qualifier("fea_auditService")
	private AuditService auditService;

	@Autowired(required = false)
	@Qualifier("feaJmsController")
	private FeaJmsController feaJmsController;

	private String errorStatus;
	private String dirDestination;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	private ExtractAdapterDispatcherImpl() {

		if (log.isDebugEnabled())
			log.debug(">>> ExtractAdapterDispatcherImpl");

		if (log.isDebugEnabled())
			log.debug("<<< ExtractAdapterDispatcherImpl");
	}

	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.core.ExtractAdapterDispatcher#dispatch
	 * (com.amtsybex.fieldreach.extract.ExtractCandidate)
	 */
	@Override
	public void dispatch(ExtractCandidate extract) {

		if (log.isDebugEnabled())
			log.debug(">>> dispatch extract=XXX");

		Transaction trans = null;

		try {

			// Start a Fieldreach transaction. Updates to the Fieldreach
			// database
			// will be carried out first and then rolled back if the extracted
			// message
			// cannot be delivered to its destination.
			trans = instanceManager.getTransaction(extract.getInstance());

			if (this.feaJmsController != null) {

				// JMS dispatch

				try {

					// Update result status in the Fieldreach database.
					this.scriptResultsService.updateResultStatus(extract.getInstance(), extract.getReturnId());

					// Dispatch to JMS queue.
					if(extract.isRedelivery()) {
						
						log.info("Attemping redelivery. Instance: "+ extract.getInstance() + " ReturnId: " + extract.getReturnId() + " ScriptCodeId: "
								+ extract.getScriptCodeId() + " Status: " + extract.getStatusValue());
					}
					
					this.feaJmsController.dispatch(extract.getInstance(), extract.getReturnId(), extract.getXml());

					// Update audit database to log dispatch of message.
					this.auditService.logDispatchSuccess(extract.getAuditId());

					this.instanceManager.commitTransaction(trans);
					
					// FDP1171 TM 11/01/2016
					// Log the extracted file.
					log.info("Message Extracted: " + extract.getXml());
					// End FDP1171

				} catch (JMSException e) {
					
					log.error("Dispatch Failed. Instance: "+ extract.getInstance() + " ReturnId: " + extract.getReturnId() + " ScriptCodeId: "
							+ extract.getScriptCodeId() + " Status: " + extract.getStatusValue());
					
					log.error(e.getMessage());

					this.instanceManager.rollbackTransaction(trans);

					mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractDispatch JMS Dispatch Failed", "Dispatch Failed. Instance: "+ extract.getInstance() + " ScriptCodeId: "
							+ extract.getScriptCodeId() + " Status: " + extract.getStatusValue() + " " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P2, extract.getReturnId()));
	
					if (!extract.isRedelivery()) {

						this.logDispatchError(extract.getAuditId(), extract.getXml());
						
					} else {

						this.logRedeliveryError(extract.getAuditId());
					}
				}

			} else {

				// File system dispatch

				try {

					// Update result status in the Fieldreach database.
					this.scriptResultsService.updateResultStatus(extract.getInstance(), extract.getReturnId());

					// Dispatch to file system.
					if(extract.isRedelivery()) {
						
						log.info("Attemping redelivery. Instance: "+ extract.getInstance() + " ReturnId: " + extract.getReturnId() + " ScriptCodeId: "
								+ extract.getScriptCodeId() + " Status: " + extract.getStatusValue());
					}
					
					this.directoryDispatch(extract);

					// Update audit database to log dispatch of message.
					this.auditService.logDispatchSuccess(extract.getAuditId());

					this.instanceManager.commitTransaction(trans);
					
					// FDP1171 TM 11/01/2016
					// Log the extracted file.
					log.info("Message Extracted: " + extract.getXml());
					// End FDP1171

				} catch (Exception e) {
					//FDP1259 - MC - catch Exception instead of IOException
					log.error("Dispatch Failed. Instance: "+ extract.getInstance() + " ReturnId: " + extract.getReturnId() + " ScriptCodeId: "
							+ extract.getScriptCodeId() + " Status: " + extract.getStatusValue());

					log.error(e.getMessage());

					this.instanceManager.rollbackTransaction(trans);

					mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractDispatch DIR Dispatch Failed ", "Dispatch Failed. Instance: "+ extract.getInstance() + " ScriptCodeId: "
							+ extract.getScriptCodeId() + " Status: " + extract.getStatusValue() + " " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P2, extract.getReturnId()));
					
					if (!extract.isRedelivery()) {

						this.logDispatchError(extract.getAuditId(), extract.getXml());
						
					} else {

						this.logRedeliveryError(extract.getAuditId());
					}
				}
			}

		} catch (NextStatusNotFoundException e) {

			log.error(e.getMessage());
			
			if (trans != null)
				this.instanceManager.rollbackTransaction(trans);

			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractDispatcher NextStatusNotFoundException", "ExtractDispatcher NextStatusNotFoundException " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P2, extract.getReturnId()));

			this.logStatusUpdateError(extract.getInstance(), extract.getReturnId(), extract.getAuditId(),
					e.getMessage());

		} catch (StatusUpdateException e) {

			log.error(e.getMessage());
			
			if (trans != null)
				this.instanceManager.rollbackTransaction(trans);

			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractDispatcher StatusUpdateException", "ExtractDispatcher StatusUpdateException returnId " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P2, extract.getReturnId()));

			this.logStatusUpdateError(extract.getInstance(), extract.getReturnId(), extract.getAuditId(),
					e.getMessage());
			

		} catch (FRInstanceException e) {

			log.error(e.getMessage());
			
			if (trans != null)
				this.instanceManager.rollbackTransaction(trans);

			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractDispatcher FRInstanceException", "ExtractDispatcher FRInstanceException returnId " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P2, extract.getReturnId()));

			try {

				this.auditService.logExtractionError(extract.getAuditId(), e.getMessage());

			} catch (HibernateException ex) {

				log.error(ex.getMessage());

				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractDispatcher HibernateException. Shutting Down", "ExtractDispatcher HibernateException. Shutting Down " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P1, extract.getReturnId()));

				FEAUtils.emailAndShutdown(ex);
				return;
			}

		} catch (HibernateException e) {

			log.error(e.getMessage());

			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractDispatcher HibernateException. Shutting Down", "ExtractDispatcher HibernateException. Shutting Down " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P1, extract.getReturnId()));

			FEAUtils.emailAndShutdown(e);
			return;
		}

		
		if (log.isDebugEnabled())
			log.debug("<<< dispatch");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.core.ExtractAdapterDispatcher#
	 * setErrorStatus (java.lang.String)
	 */
	@Override
	public void setErrorStatus(String errorStatus) {

		if (log.isDebugEnabled())
			log.debug(">>> setErrorStatus errorStatus=" + errorStatus);

		this.errorStatus = errorStatus;

		if (log.isDebugEnabled())
			log.debug("<<< setErrorStatus");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.core.ExtractAdapterDispatcher#
	 * setDirDestination(java.lang.String)
	 */
	@Override
	public void setDirDestination(String dirDest) {

		if (log.isDebugEnabled())
			log.debug(">>> setDirDestination dirDest=" + dirDest);

		this.dirDestination = dirDest;

		if (log.isDebugEnabled())
			log.debug("<<< setDirDestination");
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Dispatch supplied extraction candidate to the file system.
	 * 
	 * @param extract
	 *            ExtractCandidate object contain details on the file to be
	 *            dispatched.
	 * 
	 * @throws IOException
	 */
	private void directoryDispatch(ExtractCandidate extract) throws Exception {
		//FDP1259 - MC - changing to use common code to write file to temp then rename to xml
		//original problem in FDP1259 was that createnewfile was called after setting up the 
		//Fileoutputstream
		
		if (log.isDebugEnabled())
			log.debug(">>> directoryDispatch extract=XXX");

		String filename = "FEA_" + extract.getReturnId() + ".xml";
		String dir = this.dirDestination;

		log.info("Dispatching message to directory["+ dir + File.separator + filename + "]");
		
		try{
			if(Common.writeBytesToFile(extract.getXml().getBytes(), filename, dir)){
				if (log.isDebugEnabled())
					log.debug("File dispatched to " + FilenameUtils.normalizeNoEndSeparator(dir + File.separator
							+ filename));
				
				log.info("Dispatch success!");
			}else{
				log.error("Dispatch failed!");

				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractDispatcher Dispatch Failed", "ExtractDispatcher Dispatch Failed" , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P3, extract.getReturnId()));

			}
		} catch (Exception e) {

			log.error("Dispatch failed!");
			log.error(e.getMessage());

			throw e;
		}

				
		if (log.isDebugEnabled())
			log.debug("<<< directoryDispatch");
	}

	/**
	 * Update the Fieldreach database and embedded database to indicate that an
	 * error occurred during the process of updating the result status.
	 * 
	 * @param frInstance
	 *            The Fieldreach instance to update.
	 * 
	 * @param returnId
	 *            ReturnId of the result that was extraction failed for.
	 * 
	 * @param auditId
	 *            Id of the audit record to update.
	 * 
	 * @param errorDetail
	 *            The reason that extraction failed.
	 */
	private void logStatusUpdateError(String frInstance, int returnId, String auditId, String errorDetail) {

		if (log.isDebugEnabled())
			log.debug(">>> logStatusUpdateError");

		try {

			try {

				// Update the ResultStausLog and ReturnedScripts tables in the
				// Fieldreach database.
				this.scriptResultsService.updateResultErrorStatus(frInstance, returnId, this.errorStatus);

				// Update the embedded audit database to log the error.
				this.auditService.logExtractionError(auditId, errorDetail);

			} catch (StatusUpdateException e) {

				log.error(e.getMessage());

				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractDispatcher StatusUpdateException", "ExtractDispatcher StatusUpdateException " + e.getMessage(), SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P3, returnId));

				String newError = e.getMessage() + "\n\n" + errorDetail;
				this.auditService.logExtractionError(auditId, newError);

			} catch (FRInstanceException e) {

				log.error(e.getMessage());

				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractDispatcher logStatusUpdateError FRInstanceException", "ExtractDispatcher logStatusUpdateError FRInstanceException " + e.getMessage(), SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P3, returnId));

				String newError = e.getMessage() + "\n\n" + errorDetail;
				this.auditService.logExtractionError(auditId, newError);
			}

		} catch (HibernateException e) {

			log.error(e.getMessage());

			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractDispatcher logStatusUpdateError HibernateException.", "ExtractDispatcher logStatusUpdateError HibernateException " + e.getMessage(), SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P1, returnId));

			FEAUtils.emailAndShutdown(e);
			return;
		}

		if (log.isDebugEnabled())
			log.debug("<<< logStatusUpdateError");
	}

	/**
	 * Update the audit record in the embedded database to indicate that an
	 * error occurred during attempted redelivery of the extracted XML message
	 * to its destination, and update the redelivery table.
	 * 
	 * @param auditId
	 *            id of the record in the audit database to update.
	 */
	private void logRedeliveryError(String auditId) {

		if (log.isDebugEnabled())
			log.debug(">>> logRedeliveryError auditId=" + auditId);

		try {

			this.auditService.logRedeliveryException(auditId);

		} catch (HibernateException e) {

			log.error(e.getMessage());

			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractDispatcher logRedeliveryError HibernateException", "ExtractDispatcher logRedeliveryError HibernateException " + e.getMessage(), SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P1));

			FEAUtils.emailAndShutdown(e);
			return;
		}

		if (log.isDebugEnabled())
			log.debug("<<< logRedeliveryError");
	}

	/**
	 * Update the audit record in the embedded database to indicate that an
	 * error occurred during dispatch of the extracted XML message to its
	 * destination, and update the redelivery table.
	 * 
	 * @param auditId
	 *            id of the record in the audit database to update.
	 * 
	 * @param xmlContent
	 *            the body of the XML message that could not be dispatched.
	 */
	private void logDispatchError(String auditId, String xmlContent) {

		if (log.isDebugEnabled())
			log.debug(">>> logDispatchError auditId=" + auditId + " xmlConent=XXX");

		try {

			this.auditService.logDispatchException(auditId, xmlContent);

		} catch (HibernateException e) {

			log.error(e.getMessage());

			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractDispatcher logRedeliveryError HibernateException", "ExtractDispatcher logRedeliveryError HibernateException " + e.getMessage(), SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P1));

			FEAUtils.emailAndShutdown(e);
			return;
		}

		if (log.isDebugEnabled())
			log.debug("<<< logDispatchError");
	}

	/**
	 * Ensure that if the dispatcher is configured for directory dispatch that
	 * the supplied directory is valid and exists on the file system.
	 * 
	 * @throws ConfigException
	 */
	@PostConstruct
	private void validateConfig() throws ConfigException {

		if (log.isDebugEnabled())
			log.debug(">>> logDispatchSuccess");

		// If directory dispatch is configured then we need to verify the
		// configured directory is valid and exists.
		if (this.feaJmsController == null) {

			File dir = new File(this.dirDestination);

			if (!dir.isDirectory()) {

				throw new ConfigException(
						"Configured extract destination is not a valid directory or does not exist: "
								+ this.dirDestination);
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< logDispatchSuccess");
	}

}
