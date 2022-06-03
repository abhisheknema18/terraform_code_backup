/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	30/07/2014
 * 
 * Modified by T Murray
 * FDP1165
 * 20/11/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.core.pickup;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.instance.InstanceManager;
import com.amtsybex.fieldreach.backend.instance.Transaction;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.SystemEventLog;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTCATEGORY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTTYPE;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SEVERITY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SOURCESYSTEM;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.backend.service.SystemEventService;
import com.amtsybex.fieldreach.exception.ConfigException;
import com.amtsybex.fieldreach.xmlloader.audit.maintenance.impl.AuditDbMaintenanceTaskImpl;
import com.amtsybex.fieldreach.xmlloader.audit.message.AuditMessage;
import com.amtsybex.fieldreach.xmlloader.audit.service.AuditService;
import com.amtsybex.fieldreach.xmlloader.core.scriptmonitor.ScriptResultsMonitor;
import com.amtsybex.fieldreach.xmlloader.exception.scriptresult.LoadScriptResultException;
import com.amtsybex.fieldreach.xmlloader.exception.scriptresult.ValidationException;
import com.amtsybex.fieldreach.xmlloader.scriptresult.Dispatch;
import com.amtsybex.fieldreach.xmlloader.scriptresult.Load;
import com.amtsybex.fieldreach.xmlloader.scriptresult.Pickup;
import com.amtsybex.fieldreach.xmlloader.scriptresult.PickupFiles;
import com.amtsybex.fieldreach.xmlloader.scriptresult.Validation;
import com.amtsybex.fieldreach.xmlloader.scriptresult.impl.PickupImpl;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.ResultSet;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.XmlElements;
import com.amtsybex.fieldreach.xmlloader.utils.XmlLoaderUtils;
import com.amtsybex.fieldreach.xmlloader.utils.XmlLoaderUtils.FileTypes;

/**
 * Pickup component implementation for script result files.
 */
public class ScriptResultPickup {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory.getLogger(ScriptResultPickup.class.getName());

	private String pickupDir;

	private Pickup srPickupHelper;

	List<String> generatedIdCache;
	
	@Autowired(required = true)
	@Qualifier("fxl_auditService")
	protected AuditService auditService;

	@Autowired(required = true)
	@Qualifier("scriptResultsService")
	protected ScriptResultsService scriptResultsService;

	@Autowired(required = true)
	@Qualifier("scriptService")
	protected ScriptService scriptService;
	
	@Autowired(required = true)
	@Qualifier("fxl_auditDbMaintenanceTask")
	private AuditDbMaintenanceTaskImpl auditMaintenance;

	@Autowired(required = true)
	@Qualifier("fxl_scriptResultValidator")
	private Validation resultValidator;

	@Autowired(required = true)
	@Qualifier("fxl_scriptResultLoader")
	private Load resultLoader;

	@Autowired(required = true)
	@Qualifier("fxl_scriptResultDispatcher")
	private Dispatch resultDispatcher;
	
	@Autowired
	private InstanceManager instanceManager;
	
	@Autowired
	private SystemEventService mSystemEventLogger;

	@Autowired
	@Qualifier("fxl_scriptMonitor")
	protected ScriptResultsMonitor scriptResultsMonitor;
	
	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public ScriptResultPickup() {

		super();

		this.pickupDir = null;

		this.srPickupHelper = new PickupImpl();

		this.generatedIdCache = new ArrayList<String>();
	}

	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 * 
	 * Responsible for polling configured pickup directory and processing files
	 * found there.
	 */
	public void poll() {

		if (log.isDebugEnabled())
			log.debug(">>> poll");

		try {

			if (log.isInfoEnabled()) {

				log.info("\n***********************************************\n" + "Starting poll of pickup directory\n '"
						+ this.pickupDir + "\n" + "***********************************************");
			}

			// Get list of files from the configured pickup directory.
			PickupFiles pickupFiles = srPickupHelper.getScriptResultPickupFiles(this.pickupDir);

			if (log.isInfoEnabled()) {

				log.info("\n***********************************************\n" + "Polling complete. "
						+ srPickupHelper.getLastPickupCount() + " file(s) found.\n"
						+ "***********************************************");
			}

			// Process 'Valid' files found in the pickup directory.
			if (!pickupFiles.getValidFiles().isEmpty())
				this.processFiles(FileTypes.VALID, pickupFiles.getValidFiles());

			// Process 'Invalid' files found in the pickup directory.
			if (!pickupFiles.getInvalidFiles().isEmpty())
				this.processFiles(FileTypes.INVALID, pickupFiles.getInvalidFiles());

		} catch (Throwable t) {

			log.error("Error in directory pickup thread:\n" + ExceptionUtils.getStackTrace(t));
			
			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Error in directory pickup thread", "Error in directory pickup thread " + t.getLocalizedMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P2));
			
		}

		if (log.isDebugEnabled())
			log.debug("<<< poll");
	}

	/**
	 * Set the directory to poll for script result files.
	 * 
	 * @param dir
	 * 
	 * @throws ConfigException
	 *             The pickup directory specified does not exist.
	 */
	public void setPickupDirectory(String dir) throws ConfigException {

		if (log.isDebugEnabled())
			log.debug(">>> setPickupDir dir=" + dir);

		String normalizedDir = FilenameUtils.normalizeNoEndSeparator(dir);
		File objDir = new File(normalizedDir);

		if (!objDir.exists())
			throw new ConfigException("Directory '" + normalizedDir + "' does not exist.");

		if (!objDir.isDirectory())
			throw new ConfigException("Directory '" + normalizedDir + "' does not a valid directory.");

		this.pickupDir = dir;

		if (log.isDebugEnabled())
			log.debug("<<< setPickupDir");
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Process the supplied list of files.
	 * 
	 * Valid script result files will be validated, loaded and then dispatched
	 * ot the archive. Invalid files will be dispatched directly to the error
	 * directory.
	 * 
	 * @param fileType
	 *            Enum value identifying what type of files have been supplied.
	 *            I.e valid files will be loaded, invalid files sent directly to
	 *            error.
	 * 
	 * @param filesForDispatch
	 *            Map where the value is a list of File objects for dispatch.
	 *            The Key of the map is the name of the sub directory the files
	 *            were found in. Key will be null where the name of the
	 *            directory the files were found is the configured pickup
	 *            folder.
	 */
	private void processFiles(FileTypes fileType, Map<String, List<File>> filesForDispatch) {

		if (log.isDebugEnabled())
			log.debug(">>> processFiles fileType=XXX filesForDispatch=XXX");

		for (Map.Entry<String, List<File>> entry : filesForDispatch.entrySet()) {

			String instance = entry.getKey();
			List<File> files = entry.getValue();

			for (File file : files) {

				// 1) Generate id for the file picked up.
				String id = this.generateId();

				// 2) Gather information required to update audit database
				AuditMessage msg = XmlLoaderUtils.generateAuditMessage(id, instance, file);

				Transaction trans = null;
				
				switch (fileType) {

				// 3) Validate, load and dispatch to archive or dispatch
				// directly to error.
				case VALID: {
					
					try {

						try {
							
							// a) Log pickup
							this.auditService.logPickupSuccess(msg);
						
						} catch (HibernateException e) {

							log.error("Unable to update Audit database: A HibernateException occured.\n" + e.getMessage());
							
							mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Unable to update Audit database: A HibernateException occured", "Unable to update Audit database: A HibernateException occured " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
							
						}
						
						trans = this.instanceManager.getTransaction(msg.getTargetInstance());
						
						// b) Parse and Validate the file
						ResultSet srXml = this.parseAndValidateScriptResult(msg);

						// c) Load the file
						this.loadScriptResult(srXml, msg);

						try {
							ReturnedScripts workingScriptResult = null;
							//only need to do the script monitor checks if we are uploading a new result with equipment information
							if(srXml.getScriptResults().getPROFILE().getEXTENDED().getValue(XmlElements.EQUIPNO) != null) {
								workingScriptResult = this.scriptResultsService.getReturnedScript(instance, this.scriptResultsService.getNextReturnId(instance)-1);
								//Run any required ScriptMonitor functions
								if(scriptResultsMonitor != null && workingScriptResult != null) {
									scriptResultsMonitor.performScriptMonitor(msg.getTargetInstance(), workingScriptResult);
								}
							}

						}catch (Exception e) {
							log.error("Script Monitoring Exception ", e);
							mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "Script Monitoring Error", "Error monitoring script " + msg.getFilename() + " " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
							//do nothing as monitoring is non critical
						}
						
						this.instanceManager.commitTransaction(trans);
						
						// d) Archive the file
						this.sendToArchive(file, msg);

						
					} catch (ValidationException e) {

						this.instanceManager.rollbackTransaction(trans);
						
						log.error("Error validating script result: " + msg.getFilename() + "\n" + e.getMessage());
						
						mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Error validating script result", "Error validating script result: " + msg.getFilename() + " " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P2));
						

						msg.setErrorText(e.getMessage());
						msg.setDispatcherDestination(XmlLoaderUtils.DISPATCHER_ERROR);

						try {

							this.auditService.logValidationError(msg);
							
						} catch (HibernateException ex) {
							
							log.error("Unable to update Audit database: A HibernateException occured.\n" + e.getMessage());
							
							mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Unable to update Audit database: A HibernateException occured", "Unable to update Audit database: A HibernateException occured " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
							
						}
						
						this.sendToError(file, msg);

					} /*catch (LoadScriptResultException e) {

						this.instanceManager.rollbackTransaction(trans);
						
						log.error("Error loading script result: " + msg.getFilename() + "\n" + e.getMessage());

						mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Error loading script result", "Error loading script result: " + msg.getFilename() + " " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P2));
						
						msg.setErrorText(e.getMessage());
						msg.setDispatcherDestination(XmlLoaderUtils.DISPATCHER_ERROR);

						try {

							this.auditService.logLoadError(msg);
							
						} catch (HibernateException ex) {
							
							log.error("Unable to update Audit database: A HibernateException occured.\n" + e.getMessage());
							
							mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Unable to update Audit database: A HibernateException occured", "Unable to update Audit database: A HibernateException occured " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
							
						}
						
						this.sendToError(file, msg);
						
					}*/ catch (FRInstanceException e) {
						
						log.error("Error creating transaction:\n" + e.getMessage());
						
						mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Error creating transaction", "Error creating transaction: " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P2));
						
					} catch (Throwable t) {
						
						// FDP1187 TM 01/03/2016
						
						this.instanceManager.rollbackTransaction(trans);
						
						log.error("Unexpected error loading script result: " + msg.getFilename() + "\n" + t.getMessage());

						mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Unexpected error loading script result", "Unexpected error loading script result: " + msg.getFilename() + " " + t.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P2));
						
						msg.setErrorText(t.getMessage());
						msg.setDispatcherDestination(XmlLoaderUtils.DISPATCHER_ERROR);

						try {

							this.auditService.logLoadError(msg);
							
						} catch (HibernateException ex) {
							
							log.error("Unable to update Audit database: A HibernateException occured.\n" + t.getMessage());
							
							mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Unable to update Audit database: A HibernateException occured", "Unable to update Audit database: A HibernateException occured " + ex.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
							
						}
						
						this.sendToError(file, msg);
												
						// End FDP1187
					}

					break;
				}

				case INVALID: {

					msg.setDispatcherDestination(XmlLoaderUtils.DISPATCHER_ERROR);

					msg.setErrorText("Invalid script result file name: " + msg.getFilename());

					this.auditService.logPickupError(msg);

					sendToError(file, msg);

					break;
				}

				default:
					break;
				}
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< processFiles");
	}

	// FDP1165 TM 24/11/2015
	// Modified method signature.
	/**
	 * Parse and validate the script result file.
	 *             
	 * @param msg
	 *            AuditMessage used to update the audit database.
	 * 
	 * @throws ValidationException
	 * @throws UnsupportedEncodingException 
	 */
	private ResultSet parseAndValidateScriptResult(AuditMessage msg) throws ValidationException, UnsupportedEncodingException {

		if (log.isDebugEnabled())
			log.debug(">>> validateScriptResult file=XXX msg=XXX");
		
		ResultSet resultXml = null;
		
		try {

			if (log.isInfoEnabled())
				log.info("Validating script result file: " + msg.getFilename());

			// Validate the result file XML.
			resultXml = this.resultValidator.parseAndValidateXml(msg.getFileUri());
			
			// Result already loaded into Fieldreach database?
			this.resultValidator.previouslyLoaded(msg.getTargetInstance(), msg.getFilename());

			// Validate script version
			this.resultValidator.validateScriptVersion(msg.getTargetInstance(), resultXml);

			if (log.isInfoEnabled())
				log.info("Validation Passed! " + msg.getFilename());

			// Result has been parsed so we can update workgroup and complete date in audit message.
			msg.setWorkgroup(resultXml.getScriptResults().getPROFILE().getGENERAL().getWORKGROUPCODE());
			msg.setCompleteDate(resultXml.getScriptResults().getPROFILE().getGENERAL().getCOMPLETEDATE());
			
			this.auditService.logValidationSuccess(msg);
			
		} catch (FRInstanceException e) {

			throw new ValidationException(e);

		} catch (ValidationException e) {

			throw e;
			
		} catch (HibernateException e) {
			
			log.error("Unable to update Audit database: A HibernateException occured.\n" + e.getMessage());
			
			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Unable to update Audit database: A HibernateException occured", "Unable to update Audit database: A HibernateException occured " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
			
		}

		if (log.isDebugEnabled())
			log.debug("<<< validateScriptResult");
		
		return resultXml;
	}

	// FDP1165 TM 24/11/2015
	// Modified method signature.
	/**
	 * Load the script result file specified into the database.
	 * 
	 * @param srXml
	 *            Parsed script result file to be loaded.
	 * 
	 * @param msg
	 *            AuditMessage used to update the audit database.
	 * 
	 * @throws LoadScriptResultException
	 */
	private void loadScriptResult(ResultSet srXml, AuditMessage msg) throws LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> loadScriptResult file=XXX msg=XXX");

		try {

			if (log.isInfoEnabled()) {

				log.info("Loading script result file into Fieldreach database: " + msg.getFilename());
			}

			this.resultLoader.loadScriptResult(msg.getTargetInstance(), srXml, msg.getFileUri());

			if (log.isInfoEnabled()) {

				log.info("Script result file committed to Fieldreach database: " + msg.getFilename());
			}

			msg.setDispatcherDestination(XmlLoaderUtils.DISPATCHER_ARCHIVE);
			
			this.auditService.logLoadSuccess(msg);

		} catch (LoadScriptResultException e) {

			throw e;

		} catch (FRInstanceException e) {

			throw new LoadScriptResultException(e);
			
		} catch (HibernateException e) {
			
			log.error("Unable to update Audit database: A HibernateException occured.\n" + e.getMessage());
			
			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Unable to update Audit database: A HibernateException occured", "Unable to update Audit database: A HibernateException occured " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
			
		}
		
		if (log.isDebugEnabled())
			log.debug("<<< loadScriptResult");
	}

	/**
	 * Send the file specified to the archive directory, and update the audit
	 * database to indicate that the file was delivered to the archive directory.
	 * 
	 * @param file
	 *            File to be moved to the archive directory.
	 * 
	 * @param msg
	 *            AuditMessage used to update the audit database.
	 */
	private void sendToArchive(File file, AuditMessage msg) {
		
		if (log.isDebugEnabled())
			log.debug(">>> sendToArchive file=XXX msg=XXX");
				
		String subDir = XmlLoaderUtils.generateSubDir(msg);
		
		try {
	
			this.resultDispatcher.dispatchToArchive(subDir,
					msg.getFileUri());

			this.auditService.logDispatchArchive(msg);
			
		} catch (HibernateException e) {

			log.error("Unable to update Audit database: A HibernateException occured.\n" + e.getMessage());
			
			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Unable to update Audit database: A HibernateException occured", "Unable to update Audit database: A HibernateException occured " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));

		} catch (IOException e) {

			try {

				String errorMsg = "IO error occured dispatching file: " + msg.getFileUri() + " \n" + e.getMessage();

				log.error(msg.toString());

				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup IO error occured dispatching file", "IO error occured dispatching file: " + msg.getFileUri() + "  " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P2));

				msg.setErrorText(errorMsg);
				this.auditService.logDispatcherError(msg);

			} catch (HibernateException ex) {

				log.error("Unable to update Audit database: A HibernateException occured.\n" + ex.getMessage());
				
				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Unable to update Audit database: A HibernateException occured", "Unable to update Audit database: A HibernateException occured " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
				
			}
		}
		
		if (log.isDebugEnabled())
			log.debug("<<< sendToError");
	}
	
	/**
	 * Send the file specified to the error directory, and update the audit
	 * database to indicate that the file was delivered to the error directory.
	 * 
	 * @param file
	 *            File to be moved to the error directory.
	 * 
	 * @param msg
	 *            AuditMessage used to update the audit database.
	 */
	private void sendToError(File file, AuditMessage msg) {

		if (log.isDebugEnabled())
			log.debug(">>> sendToError file=XXX msg=XXX");

		String subDir = XmlLoaderUtils.generateSubDir(msg);

		try {

			this.resultDispatcher.dispatchToError(subDir, msg.getFileUri(), msg.getErrorText());

			this.auditService.logDispatchError(msg);

		} catch (HibernateException e) {

			log.error("Unable to update Audit database: A HibernateException occured.\n" + e.getMessage());
			
			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Unable to update Audit database: A HibernateException occured", "Unable to update Audit database: A HibernateException occured " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
			

		} catch (IOException e) {

			try {

				String errorMsg = "IO error occured dispatching file: " + msg.getFileUri() + " \n" + e.getMessage();

				log.error(msg.toString());
				
				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup IO error occured dispatching file", "IO error occured dispatching file: " + msg.getFileUri() + "  " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P2));

				msg.setErrorText(errorMsg);
				this.auditService.logDispatcherError(msg);

			} catch (HibernateException ex) {

				log.error("Unable to update Audit database: A HibernateException occured.\n" + ex.getMessage());
				
				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Unable to update Audit database: A HibernateException occured", "Unable to update Audit database: A HibernateException occured " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
				
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< sendToError");
	}

	/**
	 * Generates a unique id for the message being constructed for dispatch to a
	 * component. The generated id will be cached to prevent duplicate ids being
	 * generated before entries are committed to the audit database.
	 * 
	 * @return
	 */
	private String generateId() {

		if (log.isDebugEnabled())
			log.debug(">>> generateId");

		String id = this.auditService.generateId();

		while (this.generatedIdCache.contains(id))
			id = this.auditService.generateId();

		this.generatedIdCache.add(id);

		if (log.isDebugEnabled())
			log.debug("<<< generateId");

		return id;
	}

}
