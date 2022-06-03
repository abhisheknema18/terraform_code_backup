/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	23/12/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.instance.Instance;
import com.amtsybex.fieldreach.backend.model.InterfaceQueue;
import com.amtsybex.fieldreach.backend.model.ScriptStatusEventTypes;
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
import com.amtsybex.fieldreach.extract.core.ExtractAdapterController;
import com.amtsybex.fieldreach.extract.core.tasks.ExtractTask;
import com.amtsybex.fieldreach.extract.utils.FEAUtils;
import com.amtsybex.fieldreach.interfaces.ServiceApplication;

/**
 * Class responsible for managing the extraction process.
 */
public class ExtractAdapterControllerImpl implements ExtractAdapterController {

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
	@Qualifier("fea_threadPool")
	private TaskExecutor taskExecutor;
	
	private Map<String, Instance> fieldreachInstances;

	private Map<String, List<ExtractCandidate>> extractionCandidates;

	private static ServiceApplication feaCore = ExtractAdapterCoreImpl.getInstance();
	
	
	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	private ExtractAdapterControllerImpl() {

		if (log.isDebugEnabled())
			log.debug(">>> ExtractAdapterControllerImpl");

		this.extractionCandidates = new HashMap<String, List<ExtractCandidate>>();

		if (log.isDebugEnabled())
			log.debug("<<< ExtractAdapterControllerImpl");
	}
	
	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 * 
	 * Poll the InterfaceQueue table in each configured Fieldreach instance
	 */
	@Override
	public void run() {

		if (log.isDebugEnabled())
			log.debug(">>> run");

		log.info("Polling InterfaceQueue...");
		
		for (String key : this.fieldreachInstances.keySet()) {

			List<ExtractCandidate> extractCandidates = new ArrayList<ExtractCandidate>();

			// If only one instance configured then don't specify
			// instance name.
			if (this.fieldreachInstances.size() == 1)
				key = null;

			try {

				// Get records on the InterfaceQueue table for the current
				// instance.
				List<InterfaceQueue> iqEntries = this.scriptResultsService
						.getInterfaceQueueEntries(key);

				// Lookup the ScriptStatusEventTypes table to see if any records
				// found on the InterfaceQueue table are candidates for
				// extraction.
				// I.e. they have a matching ScripCodeId and StatusValue.
				for (InterfaceQueue iq : iqEntries) {

					List<ScriptStatusEventTypes> extractConfig = this.scriptService
							.findScriptStatusEventTypes(key, iq.getId()
									.getScriptCodeId(), iq.getId()
									.getResultStatus());

					for (ScriptStatusEventTypes config : extractConfig) {

						ExtractCandidate candidate = new ExtractCandidate(key,
								iq.getId().getReturnIdInt(), config.getId()
										.getScriptCodeIdInt(), config.getId()
										.getStatusValue(), config.getId()
										.getEventIdInt());

						// Log an extraction candidate being found.
						try {

							String auditId = this.auditService
									.logExtractionCandidate(
											candidate.getReturnId(),
											candidate.getInstance());

							candidate.setAuditId(auditId);

							extractCandidates.add(candidate);
							
							// Once extraction has been logged delete entry from the interfacequeue table.
							this.scriptResultsService.deleteInterfaceQueueEntry(candidate.getInstance(), iq);
							
						} catch (HibernateException e) {

							log.error(e.getMessage());
							
							mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractController HibernateException Application shutting down", "HibernateException Application shutting down " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P1, candidate.getReturnId()));
							
							FEAUtils.emailAndShutdown(e);
							return;
						}
					}
				}

				// Store the list of extraction candidate details for the
				// current instance.
				if (!extractCandidates.isEmpty())
					this.extractionCandidates.put(key, extractCandidates);
				
				log.info(this.extractionCandidates.size() + " extraction candidates found in instance: " + key);
				
			} catch (FRInstanceException e) {

				log.error(e.getMessage());

				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ExtractController FRInstanceException Application shutting down", "FRInstanceException Application shutting down " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P1));
				
				FEAUtils.emailAndShutdown(e);
				return;
			}
		}

		// Now that a list of candidates for extraction has been determined
		// perform the extraction.
		
		for (String key : this.extractionCandidates.keySet()) {

			// Get extraction candidates for current Fieldreach instance.
			List<ExtractCandidate> candidates = this.extractionCandidates
					.get(key);

			for (ExtractCandidate candidate : candidates) {
				
				try {

					ExtractTask task = (ExtractTask) feaCore.getBean(
							FEAUtils.FEA_EXTRACT_TASK_BEAN);
					
					task.setExtractCandidate(candidate);
		
					taskExecutor.execute(task);
		
				} catch (Exception e) {
					
					log.error("Unable to execute task. Instance: " + candidate.getInstance() + 
							" ReturnId: " + candidate.getReturnId());
					
					mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "Unable to execute task.", "Unable to execute task. Instance: " 
					+ candidate.getInstance()  + " " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "FEA", SEVERITY.P2, candidate.getReturnId()));
					
				}
			
			}
		}

		// Reset the list of extraction candidates.
		this.extractionCandidates = new HashMap<String, List<ExtractCandidate>>();

		log.info("Polling complete.");
		
		if (log.isDebugEnabled())
			log.debug("<<< run");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.extract.core.ExtractAdapterController#
	 * setFieldreachInstances(java.util.Map)
	 */
	@Override
	public void setFieldreachInstances(Map<String, Instance> instances) {

		if (log.isDebugEnabled())
			log.debug(">>> setFieldreachInstances instances=XXX");

		this.fieldreachInstances = instances;

		if (log.isDebugEnabled())
			log.debug("<<< setFieldreachInstances");
	}
	
}
