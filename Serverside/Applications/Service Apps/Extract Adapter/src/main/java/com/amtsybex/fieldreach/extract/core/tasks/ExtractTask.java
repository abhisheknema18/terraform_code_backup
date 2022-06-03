/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	27/05/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.core.tasks;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.StatusUpdateException;
import com.amtsybex.fieldreach.backend.model.SystemEventLog;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTCATEGORY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTTYPE;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SEVERITY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SOURCESYSTEM;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.backend.service.SystemEventService;
import com.amtsybex.fieldreach.extract.ExtractCandidate;
import com.amtsybex.fieldreach.extract.audit.service.AuditService;
import com.amtsybex.fieldreach.extract.core.ExtractAdapterDispatcher;
import com.amtsybex.fieldreach.extract.core.ExtractEngine;
import com.amtsybex.fieldreach.extract.core.impl.ExtractAdapterControllerImpl;
import com.amtsybex.fieldreach.extract.utils.FEAUtils;

public class ExtractTask implements Runnable {

	private static final Logger log = LoggerFactory
			.getLogger(ExtractAdapterControllerImpl.class.getName());
	
	@Autowired
	private SystemEventService mSystemEventLogger;
	
	@Autowired(required = true)
	@Qualifier("scriptResultsService")
	private ScriptResultsService scriptResultsService;

	@Autowired(required = true)
	@Qualifier("scriptService")
	private ScriptService scriptService;

	@Autowired(required = true)
	@Qualifier("fea_auditService")
	private AuditService auditService;

	@Autowired(required = true)
	@Qualifier("fieldreachExtractEngine")
	private ExtractEngine extractEngine;

	@Autowired(required = true)
	@Qualifier("fea_dispatcher")
	private ExtractAdapterDispatcher feaDispatcher;
	
	private boolean extractBinary;
	private String destination;
	private String errorStatus;
	
	private boolean ignoreNAOR; // FDP1235 TM 25/07/2016
	private boolean ignoreBlankFieldRole; //FDP1383 - MC
	
	private ExtractCandidate candidate;
	
	
	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/
	
	public ExtractTask() {
		
		log.debug(">>> ExtractTask");
		
		this.errorStatus = FEAUtils.DEFAULT_ERROR_STATUS;
		
		this.ignoreNAOR = true; // FDP1235 TM 25/07/2016
		
		this.ignoreBlankFieldRole = false; //FDP1383 - MC
		
		log.debug("<<< ExtractTask");
	}
		
	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/
	
	/**
	 * Set the extraction candidate that contains details of the
	 * script result to be extracted.
	 * 
	 * @param candidate
	 */
	public void setExtractCandidate(ExtractCandidate candidate) {
		
		this.candidate = candidate;
	}
	
	/**
	 * Indicate if binary responses should be extracted for script results.
	 * 
	 * @param extractBinary
	 */
	public void setExtractBinary(boolean extractBinary) {
		
		this.extractBinary = extractBinary;
	}
	
	/**
	 * Set the value of the 'Destination' element in the extracted script
	 * result.
	 * 
	 * @param destination
	 */
	public void setDestination(String destination) {
		
		this.destination = destination;
	}
	
	/**
	 * The status that script results will be set to if an error
	 * occurs during the extraction process/
	 * 
	 * @param errorStatus
	 */
	public void setErrorStatus(String errorStatus) {
		
		this.errorStatus = errorStatus;
	}
	
	// FDP1235 TM 25/07/2016
	
	/**
	 * Indicate if NA and OR responses should be extracted or ignored.
	 * 
	 * @param ignoreNaOr
	 */
	public void setIgnoreNaOr(boolean ignoreNaOr) {
		
		this.ignoreNAOR = ignoreNaOr;
	}
	
	// End FDP1235
	
	
	/**
	 * Indicate if NA and OR responses should be extracted or ignored.
	 * FDP1383 - MC
	 * @param ignoreNaOr
	 */
	public void setIgnoreBlankFieldRole(boolean ignoreBlankFieldRole) {
		
		this.ignoreBlankFieldRole = ignoreBlankFieldRole;
	}
	
	
	
		
	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		log.debug(">>> performExtraction");
		
		String extractXML = "";

		try {
			
			log.info("Starting extract. Instance: "+ candidate.getInstance() + " ReturnId: " + candidate.getReturnId() + " ScriptCodeId: "
					+ candidate.getScriptCodeId() + " Status: " + candidate.getStatusValue());
				
			// 1) Call the appropriate method in the extract engine API
			// depending on the event type of the extraction candidate.
			if (candidate.getEventId() == FEAUtils.EXTRACT_ALL_EVENT_ID) {
				
				log.info("Performing 'Extract All'...");
				
				extractXML = this.extractEngine.extractScriptResultAll(
						candidate.getInstance(), candidate.getReturnId(), this.destination,
						this.extractBinary, false, this.ignoreNAOR, this.ignoreBlankFieldRole);

			} else {
				
				log.info("Performing standard extract...");
				
				extractXML = this.extractEngine.extractScriptResult(
						candidate.getInstance(), candidate.getReturnId(),
						candidate.getEventId(), this.destination, this.ignoreNAOR, this.ignoreBlankFieldRole);
			}

			// Update ExtractCandidate object to contain extracted XML.
			candidate.setXml(extractXML);

			// 2) Dispatch the extracted message.
			this.feaDispatcher.dispatch(candidate);

			
		} catch (Exception e) {
			
			// FDP1248 TM 30/09/2016
			// Catch any kind of exception.
			
			log.error(e.getMessage());
			
			this.logExtractError(candidate.getInstance(), candidate.getReturnId(),
					candidate.getAuditId(), e.getMessage());
			
			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractTask Exception ReturnId " + candidate.getReturnId(), "ExtractTask Exception ReturnId " + candidate.getReturnId() + " " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P2));

		}
			
		log.debug("<<< performExtraction");
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
	private void logExtractError(String frInstance, int returnId,
			String auditId, String errorDetail) {

		log.debug(">>> logExtractError");

		try {

			try {

				// Update the ResultStausLog and ReturnedScripts tables in the
				// Fieldreach database.
				this.scriptResultsService.updateResultErrorStatus(frInstance,
						returnId, this.errorStatus);

				// Update the embedded audit database to log the extraction
				// error.
				this.auditService.logExtractionError(auditId, errorDetail);

			} catch (StatusUpdateException e) {

				log.error(e.getMessage());

				String newError = "StatusUpdateException: " + e.getMessage()
						+ "\n\n Original Exception:" + errorDetail;
				this.auditService.logExtractionError(auditId, newError);
				
				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractTask StatusUpdateException ReturnId " + returnId, "ExtractTask StatusUpdateException ReturnId " + returnId + " " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P2));

			} catch (FRInstanceException e) {

				log.error(e.getMessage());

				String newError = "FRInstanceException: " + e.getMessage()
						+ "\n\n Original Exception:" + errorDetail;

				this.auditService.logExtractionError(auditId, newError);				
				
				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractTask FRInstanceException ReturnId " + returnId, "ExtractTask FRInstanceException ReturnId " + returnId + " " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P2));

			}

		} catch (HibernateException e) {

			log.error(e.getMessage());

			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractTask logExtractError HibernateException " + returnId, "ExtractTask logExtractError HibernateException " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P1));

			FEAUtils.emailAndShutdown(e);
			return;
		}

		log.debug("<<< logExtractError");
	}

}
