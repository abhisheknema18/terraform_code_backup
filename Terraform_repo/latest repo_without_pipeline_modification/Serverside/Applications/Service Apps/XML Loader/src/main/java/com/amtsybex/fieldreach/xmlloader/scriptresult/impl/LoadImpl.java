/**
x * Project: FDE026
 * Author:	T Murray
 * Date:	07/08/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult.impl;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.instance.InstanceManager;
import com.amtsybex.fieldreach.backend.instance.Transaction;
import com.amtsybex.fieldreach.backend.model.CarrythroughRes;
import com.amtsybex.fieldreach.backend.model.EquipmentAttrib;
import com.amtsybex.fieldreach.backend.model.EquipmentData;
import com.amtsybex.fieldreach.backend.model.ExtdResponse;
import com.amtsybex.fieldreach.backend.model.GenNumValid;
import com.amtsybex.fieldreach.backend.model.InterfaceQueue;
import com.amtsybex.fieldreach.backend.model.Item;
import com.amtsybex.fieldreach.backend.model.ResultStatusLog;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.RuntimeCalcResults;
import com.amtsybex.fieldreach.backend.model.Script;
import com.amtsybex.fieldreach.backend.model.ScriptResultBlb;
import com.amtsybex.fieldreach.backend.model.ScriptResultsDef;
import com.amtsybex.fieldreach.backend.model.ScriptResultsTxt;
import com.amtsybex.fieldreach.backend.model.ScriptStatusDef;
import com.amtsybex.fieldreach.backend.model.SourceFiles;
import com.amtsybex.fieldreach.backend.model.SubScriptLink;
import com.amtsybex.fieldreach.backend.model.SuppResults;
import com.amtsybex.fieldreach.backend.model.SystemEventLog;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTCATEGORY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTTYPE;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SEVERITY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SOURCESYSTEM;
import com.amtsybex.fieldreach.backend.model.TaskListRes;
import com.amtsybex.fieldreach.backend.model.TaskListValues;
import com.amtsybex.fieldreach.backend.model.UomNumValid;
import com.amtsybex.fieldreach.backend.model.ValidationProperty;
import com.amtsybex.fieldreach.backend.model.WorkFlowRules;
import com.amtsybex.fieldreach.backend.model.WorkFlowRulesDef;
import com.amtsybex.fieldreach.backend.model.pk.CarrythroughResId;
import com.amtsybex.fieldreach.backend.model.pk.InterfaceQueueId;
import com.amtsybex.fieldreach.backend.model.pk.ResultStatusLogId;
import com.amtsybex.fieldreach.backend.model.pk.RuntimeCalcResultsId;
import com.amtsybex.fieldreach.backend.model.pk.ScriptResultId;
import com.amtsybex.fieldreach.backend.model.pk.ScriptResultsDefId;
import com.amtsybex.fieldreach.backend.model.pk.SubScriptLinkId;
import com.amtsybex.fieldreach.backend.model.pk.SuppResultsId;
import com.amtsybex.fieldreach.backend.model.pk.TaskListResId;
import com.amtsybex.fieldreach.backend.model.pk.TaskListValuesId;
import com.amtsybex.fieldreach.backend.service.EquipmentService;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.backend.service.SystemEventService;
import com.amtsybex.fieldreach.backend.service.ValidationTypeService;
import com.amtsybex.fieldreach.backend.service.WorkOrderService;
import com.amtsybex.fieldreach.exception.ConfigException;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.amtsybex.fieldreach.xmlloader.exception.scriptresult.LoadScriptResultException;
import com.amtsybex.fieldreach.xmlloader.scriptresult.Load;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.DataItem;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Defect;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Extended;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.General;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Response;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.ResultSet;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.SubScriptResults;
import com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.XmlElements;
import com.amtsybex.fieldreach.xmlloader.utils.XmlLoaderUtils;

/**
 * Class to perform the task of loading a script result file into the Fieldreach
 * database.
 */
public class LoadImpl implements Load {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory.getLogger(LoadImpl.class.getName());

	@Autowired
	private SystemEventService mSystemEventLogger;
	
	// Backend service objects
	private ScriptResultsService scriptResultsService;
	
	//FDE060 - KN - EquipmentData table transaction made through  Equipment service
	private EquipmentService equipmentService;
	
	private ScriptService scriptService;
	private WorkOrderService workOrderService;
	private ValidationTypeService validationTypeService;

	private InstanceManager instanceManager;

	private boolean importResultToDb;

	private ResultSet resultSet;
	
	private String fileUri;

	private List<GenNumValid> genNumValid;
	private List<UomNumValid> uomNumValid;

	Map<Integer, Item> scriptItems;

	private String fieldreachInstance;
	private String fileName;

	private ReturnedScripts retScript;
	
	// Generated/Calculated values
	private String initialStatus;
	private int duration;
	private boolean calcScoreFlag;
	private int generatedReturnId;

	// Flags
	private boolean initialStatusSystemFlag;
	private boolean sourceFileImportFlag;
	
	// FDE037 TM 29/02/2016
	private String geometryDataDir;
	private List<String> geometryFiles;
	// End FDE037
	private Boolean equipmentAttribLoad;
	private Integer equipmentAttribRefreshDays;
	
	List<WorkFlowRules> workflowRules;
	private List<String> responseCacheForWorkFlowRules;
	private Integer ootFlagForWorkFlowRules;
	private boolean hasFreeTextForWorkFlowRules;
	

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public LoadImpl(ScriptResultsService scriptResultsService, ScriptService scriptService,
			WorkOrderService workOrderService, ValidationTypeService validationTypeService,
			InstanceManager instanceManager,EquipmentService equipmentService) {

		if (log.isDebugEnabled())
			log.debug(">>> ScriptResultLoaderImpl");

		this.scriptResultsService = scriptResultsService;
		this.scriptService = scriptService;
		this.workOrderService = workOrderService;
		this.validationTypeService = validationTypeService;
		this.equipmentService = equipmentService;

		this.instanceManager = instanceManager;

		this.importResultToDb = false;

		if (log.isDebugEnabled())
			log.debug("<<< ScriptResultLoaderImpl");
	}

	
	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	// FDP1165 TM 24/11/2015 - Modified method signature.

	public Boolean getEquipmentAttribLoad() {
		if(this.equipmentAttribLoad!=null) {
			return this.equipmentAttribLoad;
		}
		else {
			return true;
		}
	}


	public void setEquipmentAttribLoad(Boolean equipmentAttribLoad) {
		this.equipmentAttribLoad = equipmentAttribLoad;
	}


	public Integer getEquipmentAttribRefreshDays() {
		if(this.equipmentAttribRefreshDays!=null) {
			return this.equipmentAttribRefreshDays;
		}else{
			return 20;
		}
	}


	public void setEquipmentAttribRefreshDays(Integer equipmentAttribRefreshDays) {
		this.equipmentAttribRefreshDays = equipmentAttribRefreshDays;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.scriptresult.Load#loadScriptResult(java
	 * .lang.String,
	 * com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.ResultSet,
	 * java.lang.String)
	 */
	@Override
	public void loadScriptResult(String frInstance, ResultSet srXml, String fileUri)
			throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> loadScriptResult frInstance=" + frInstance + " srXml=XXX");

		this.fieldreachInstance = frInstance;

		this.fileUri = fileUri;
		this.resultSet = srXml;
		
		this.fileName = FilenameUtils.getName(fileUri);
		
		this.geometryFiles = new ArrayList<String>(); // FDE037 TM 29/02/2016
		
		responseCacheForWorkFlowRules = new ArrayList<String>();
		ootFlagForWorkFlowRules = 0;
		hasFreeTextForWorkFlowRules = false;
		
		Transaction trans = null;
		
		try {

			// Begin transaction
			trans = this.instanceManager.getTransaction(frInstance);
			
			this.scriptItems = this.scriptService.findScriptItemsNoRefItems(this.fieldreachInstance,
					Integer.parseInt(this.resultSet.getScriptResults().getScriptId()));

			this.genNumValid = this.scriptService.findGenNumValidbyScriptId(this.fieldreachInstance,
					Integer.parseInt(this.resultSet.getScriptResults().getScriptId()));

			this.uomNumValid = this.scriptService.findUomNumValidbyScriptId(this.fieldreachInstance,
					Integer.parseInt(this.resultSet.getScriptResults().getScriptId()));
			

			workflowRules = scriptService.getWorkFlowRulesByScriptId(this.fieldreachInstance, Integer.parseInt(this.resultSet.getScriptResults().getScriptId()));
					
			// Perform initial calculations.
			this.determineInitialResultStatus(Integer.parseInt(this.resultSet.getScriptResults().getScriptCodeId()));

			this.calculateDuration();

			this.setCalculateScoresFlag();

			// Begin updating the Fieldreach database.
			this.updatedReturnedScripts();

			this.updatedWorkIssued();

			this.processResults();

			this.updateCarryThroughRes();

			this.updateSourceFiles();

			this.calculateWeightScores();

			this.updateScriptResultXml();
			
			this.updateEquipmentData();
			
			//FDP1430 - mc - runtime calculations
			this.updateRuntimeCalcResults();
			
			this.determineWorkFlowRuleStatus(Integer.parseInt(this.resultSet.getScriptResults().getScriptId()),Integer.parseInt(this.resultSet.getScriptResults().getScriptCodeId()));
			
			this.updatedResultStatusLog();
			
			this.updateInterfaceQueue();

			this.instanceManager.commitTransaction(trans);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			if (trans != null)
				this.instanceManager.rollbackTransaction(trans);

			this.geometryTidyUp(); // FDE037 TM 29/02/2016
			
			throw e;

		} catch (LoadScriptResultException e) {

			log.error(e.getMessage());

			if (trans != null)
				this.instanceManager.rollbackTransaction(trans);

			this.geometryTidyUp(); // FDE037 TM 29/02/2016
			
			throw e;

		} catch (Throwable t) {

			log.error(t.getMessage());

			if (trans != null)
				this.instanceManager.rollbackTransaction(trans);

			this.geometryTidyUp(); // FDE037 TM 29/02/2016
			
			throw new RuntimeException(t);
		}

		if (log.isDebugEnabled())
			log.debug("<<< loadScriptResult");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.scriptresult.standalone.
	 * ScriptResultLoader #setScriptResultDbImport(boolean)
	 */
	@Override
	public void setScriptResultDbImport(boolean dbImport) {

		if (log.isDebugEnabled())
			log.debug(">>> setScriptResultDbImport dbImport=" + dbImport);

		this.importResultToDb = dbImport;

		if (log.isDebugEnabled())
			log.debug("<<< setScriptResultDbImport");
	}

	// FDE037 TM 29/02/2016
	
	/*
	 * (non-Javadoc)
	 * @see com.amtsybex.fieldreach.xmlloader.scriptresult.Load#setGeometryDataDir(java.lang.String)
	 */
	@Override
	public void setGeometryDataDir(String dir) throws ConfigException {
		
		if (log.isDebugEnabled())
			log.debug(">>> setGeometryDataDir dir=" + dir);

		String normalizedDir = FilenameUtils.normalizeNoEndSeparator(dir);
		File objDir = new File(normalizedDir);

		if (!objDir.exists())
			throw new ConfigException("Directory '" + normalizedDir + "' does not exist.");

		if (!objDir.isDirectory())
			throw new ConfigException("Directory '" + normalizedDir + "' is not a valid directory.");

		this.geometryDataDir = dir;
		
		if (log.isDebugEnabled())
			log.debug("<<< setGeometryDataDir");
		
		//geometryDataDir
	}
	
	// End FDE037
	
	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/
	
	private void cacheResponseForWorkFlowStatusEvaluation(String response) {
		if(workflowRules != null && workflowRules.size() > 0) {
			this.responseCacheForWorkFlowRules.add(response.toUpperCase());
		}
	}

	private void determineWorkFlowRuleStatus(int scriptId, int scriptCodeId) throws FRInstanceException, LoadScriptResultException {
		
		if (log.isDebugEnabled())
			log.debug(">>> determineWorkFlowRuleStatus scriptCodeId=" + scriptId);
		
		if(workflowRules != null && workflowRules.size() > 0) {
			
			List<ScriptStatusDef> scriptStatusDef = this.scriptResultsService.getScriptStatusDefByScriptCodeID(this.fieldreachInstance, scriptCodeId);
			
			for(WorkFlowRules wfRule : workflowRules) {
				
				if(this.checkRule(wfRule)) {

					log.debug("Rule " + wfRule.getRuleName() + " evaluated to TRUE for returnid " + this.retScript.getId());
					
					for (ScriptStatusDef statusDef : scriptStatusDef) {

						if (statusDef.getStatusValue().equals(wfRule.getTargetStatus())) {
							
							log.info("Workflow Rule target status " + wfRule.getTargetStatus() + " set for returnid " + this.retScript.getId() );
							
							this.initialStatus = statusDef.getStatusValue();
							this.initialStatusSystemFlag = statusDef.getSysStatusFlag() != 0;
							break;
						}
					}
					
					log.debug("Initial status " + this.initialStatus + "set for returnid" + this.retScript.getId());
					
					retScript.setResultStatus(this.initialStatus);
					
					//save the result status
					try {

						this.scriptResultsService.saveReturnedScript(this.fieldreachInstance, retScript);

					} catch (Exception e) {

						log.error("Error updating ReturnedScripts table:\n" + e.getMessage());
						throw new LoadScriptResultException(e);
					}
					
					break;
				}else {
					log.debug("Rule " + wfRule.getRuleName() + "evaluated to FALSE for returnid" + this.retScript.getId());
				}
			}
			
		}

		if (log.isDebugEnabled())
			log.debug("<<< determineWorkFlowRuleStatus initialStatus=" + this.initialStatus);

	}
	
	private boolean checkRule(WorkFlowRules rule) {
		
		if (log.isDebugEnabled())
			log.debug(">>> checkRule rule=" + rule.getRuleName());
		
		boolean isAND = rule.getLogicOperator().equals("AND");
		
		for(WorkFlowRulesDef ruleDef: rule.getWorkFlowRulesDef()) {
			if(this.checkRuleDef(ruleDef)) {
				log.debug("RuleDef " + ruleDef.getId() + " evaluated to TRUE for returnid " + this.retScript.getId());
				if(!isAND) {
					return true;
				}
			}else {
				log.debug("RuleDef " + ruleDef.getId() + " evaluated to FALSE for returnid " + this.retScript.getId());
				if(isAND) {
					return false;
				}
			}
		}
		
		if(isAND) {
			return true;
		}
		
		return false;
	}
	
	private boolean checkRuleDef(WorkFlowRulesDef rule) {
		
		if (log.isDebugEnabled())
			log.debug(">>> checkRuleDef rule=" + rule.getRuleItem());
		
		String ruleValue = rule.getRuleValue();
		if(ruleValue.startsWith("$")) {
			ruleValue = this.resultSet.getScriptResults().getPROFILE().getEXTENDED().getValue(ruleValue.substring(1));
			if(ruleValue == null) {
				return false;
			}else {
				ruleValue = ruleValue.toUpperCase();
			}
		}
		
		if(rule.getRuleItem().equalsIgnoreCase("RESPONSE")) {
			//check responses
			if(rule.getOperator().equalsIgnoreCase("IS")){
				
				if(ruleValue.equalsIgnoreCase("OOT")) {
					if(this.ootFlagForWorkFlowRules == 1) {
						return true;
					}
				}
				
			}else if(rule.getOperator().equalsIgnoreCase("HAS")){
				
				if(ruleValue.equalsIgnoreCase("FREETEXT")) {
					return hasFreeTextForWorkFlowRules;
				}

			}else if(rule.getOperator().equals("=")) {
				
				return this.responseCacheForWorkFlowRules.contains(ruleValue.toUpperCase());
				
			}else if(rule.getOperator().equals("<>")){
				
				return !this.responseCacheForWorkFlowRules.contains(ruleValue.toUpperCase());
				
			}else {
				
				for(String value : this.responseCacheForWorkFlowRules) {
					
					BigDecimal ruleValueDecimal = new BigDecimal(ruleValue);
					
					try {
						BigDecimal valueDecimal = new BigDecimal(value);
						
						if(rule.getOperator().equals("<")) {
							if(valueDecimal.compareTo(ruleValueDecimal) < 0) {
								return true;
							}
						}else if(rule.getOperator().equals(">")) {
							if(valueDecimal.compareTo(ruleValueDecimal) > 0) {
								return true;
							}
						}else if(rule.getOperator().equals("<=")) {
							if(valueDecimal.compareTo(ruleValueDecimal) <= 0) {
								return true;
							}
						}else if(rule.getOperator().equals(">=")) {
							if(valueDecimal.compareTo(ruleValueDecimal) >= 0) {
								return true;
							}
						}
					} catch (NumberFormatException e) {
						return false;
					}
				}
			}
			
		}else {
			//check extended
			String extValue = null;
			
			if(rule.getRuleItem().startsWith("$")) {
				extValue = this.resultSet.getScriptResults().getPROFILE().getEXTENDED().getValue(rule.getRuleItem().substring(1));
			}else {
				extValue = this.resultSet.getScriptResults().getPROFILE().getEXTENDED().getValue(rule.getRuleItem());
			}

			
			if(extValue == null) {
				return false;
			}else if(rule.getOperator().equals("=")) {
				
				return ruleValue.equalsIgnoreCase(extValue.toUpperCase());
				
			}else if(rule.getOperator().equals("<>")){
				
				return !ruleValue.equalsIgnoreCase(extValue.toUpperCase());
				
			}else {
				
				try {
					BigDecimal ruleValueDecimal = new BigDecimal(ruleValue);
					BigDecimal valueDecimal = new BigDecimal(extValue);
					
					if(rule.getOperator().equals("<")) {
						if(valueDecimal.compareTo(ruleValueDecimal) < 0) {
							return true;
						}
					}else if(rule.getOperator().equals(">")) {
						if(valueDecimal.compareTo(ruleValueDecimal) > 0) {
							return true;
						}
					}else if(rule.getOperator().equals("<=")) {
						if(valueDecimal.compareTo(ruleValueDecimal) <= 0) {
							return true;
						}
					}else if(rule.getOperator().equals(">=")) {
						if(valueDecimal.compareTo(ruleValueDecimal) >= 0) {
							return true;
						}
					}
				} catch (NumberFormatException e) {
					return false;
				}
			}
		}
		
		return false;
		
	}
	
	/**
	 * Determine the initial status of the script result when it is loaded into
	 * the Fieldreach database.
	 * 
	 * @param scriptCodeId
	 * 
	 * @throws FRInstanceException
	 */
	private void determineInitialResultStatus(int scriptCodeId) throws FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> determineInitialResultStatus scriptCodeId=" + scriptCodeId);

		String minStatus = "";
		int minOrder = Integer.MAX_VALUE;
		boolean isSystem = false;

		// Initial result status will be the status with the smallest
		// StatusOrderNo
		List<ScriptStatusDef> scriptStatusDef = this.scriptResultsService
				.getScriptStatusDefByScriptCodeID(this.fieldreachInstance, scriptCodeId);

		for (ScriptStatusDef statusDef : scriptStatusDef) {

			if (statusDef.getId().getStatusOrderNo() < minOrder) {

				minStatus = statusDef.getStatusValue();
				minOrder = statusDef.getId().getStatusOrderNo();
				isSystem = statusDef.getSysStatusFlag() != 0;
			}
		}

		// Default values if no status record can be found.
		if (minStatus.equals("")) {

			minStatus = XmlLoaderUtils.DEFAULT_STATUS;
			isSystem = false;
		}

		this.initialStatus = minStatus;
		this.initialStatusSystemFlag = isSystem;

		if (log.isDebugEnabled())
			log.debug("<<< determineInitialResultStatus initialStatus=" + this.initialStatus);
	}

	/**
	 * Calculate duration value for the script result. Duration will be measured
	 * in minutes and is the difference between start date/time and complete
	 * date/time.
	 */
	private void calculateDuration() {

		if (log.isDebugEnabled())
			log.debug(">>> calculateDuration");

		int startDateInt, completeDateInt, startTimeInt, completeTimeInt;
		//30704 : Update Xsd to accept string and relevant changes to Script result xsd
		startDateInt = Integer.parseInt(this.resultSet.getScriptResults().getPROFILE().getGENERAL().getSTARTDATE());
		completeDateInt = Integer
				.parseInt(this.resultSet.getScriptResults().getPROFILE().getGENERAL().getCOMPLETEDATE());

		startTimeInt = Integer.parseInt(this.resultSet.getScriptResults().getPROFILE().getGENERAL().getSTARTTIME());
		completeTimeInt = Integer
				.parseInt(this.resultSet.getScriptResults().getPROFILE().getGENERAL().getCOMPLETETIME());

		// Convert values to Java dates
		Date startDate = Common.convertFieldreachDate(startDateInt);
		Date completeDate = Common.convertFieldreachDate(completeDateInt);

		//PRB0040626 - MC - need to pass in galse here as format is HHMMM
		Date startTime = Common.convertFieldreachTime(startTimeInt, false);
		Date completeTime = Common.convertFieldreachTime(completeTimeInt, false);

		long dayDiff = Math.abs(startDate.getTime() - completeDate.getTime());
		long timeDiff = Math.abs(startTime.getTime() - completeTime.getTime());

		int duration = (int) ((TimeUnit.MILLISECONDS.toMinutes(dayDiff)) + (TimeUnit.MILLISECONDS.toMinutes(timeDiff)));

		if (duration < 0)
			duration = 0;

		this.duration = duration;

		if (log.isDebugEnabled())
			log.debug("<<< calculateDuration duration=" + duration);
	}

	/**
	 * Determine if scores need to be calculated.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void setCalculateScoresFlag() throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> setCalculateScoresFlag");

		Script script = this.scriptService.findScript(this.fieldreachInstance,
				Integer.parseInt(this.resultSet.getScriptResults().getScriptCodeId()));

		if (script == null)
			throw new LoadScriptResultException("Error accessing Script table. Unable to load result:"
					+ this.resultSet.getScriptResults().getPROFILE().getGENERAL().getRESULTSFILE());

		this.calcScoreFlag = script.getCalcScoreInt() != 0;

		if (log.isDebugEnabled())
			log.debug("<<< setCalculateScoresFlag calcScoreFlag=" + this.calcScoreFlag);
	}

	/**
	 * Create a new record in the ReturnedScripts table for the script result.
	 * 
	 * @return returnId generated when record is added to ReturnedScripts table.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void updatedReturnedScripts() throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> updatedReturnedScripts");

		retScript = new ReturnedScripts();

		try {
			
			// FDP1165 TM 25/11/2015
			// Removed synchronised block as code no longer multi threaded.
			this.generatedReturnId = scriptResultsService.getNextReturnId(this.fieldreachInstance);

		} catch (Exception e) {

			throw new LoadScriptResultException("Error generating ReturnId for new entry in ReturnedScripts.");
		}

		// FDP1165 TM 25/11/2015
		// Store General and Extended sections in variables rather than making multiple calls.
		General gen = this.resultSet.getScriptResults().getPROFILE().getGENERAL();
		Extended ext = this.resultSet.getScriptResults().getPROFILE().getEXTENDED();
		
		retScript.setId(this.generatedReturnId);

		retScript.setScriptId(Integer.parseInt(this.resultSet.getScriptResults().getScriptId()));

		retScript.setScriptCode(this.resultSet.getScriptResults().getScriptCode());

		retScript.setScriptStatus(gen.getSCRIPTSTATUS());

		retScript.setDeviceId(gen.getDEVICEID());

		retScript.setWorkOrderNo(ext.getValue(XmlElements.WORKORDERNO));

		retScript.setWorkOrderDesc(ext.getValue(XmlElements.WORKORDERDESC));

		retScript.setEquipNo(ext.getValue(XmlElements.EQUIPNO));

		retScript.setEquipDesc(ext.getValue(XmlElements.EQUIPDESC));

		retScript.setSummaryDesc(gen.getSUMMDESC());

		retScript.setStartDate(Integer.parseInt(gen.getSTARTDATE()));

		retScript.setStartTime(Integer.parseInt(gen.getSTARTTIME()));

		retScript.setCompleteDate(Integer.parseInt(gen.getCOMPLETEDATE()));

		retScript.setCompleteTime(Integer.parseInt(gen.getCOMPLETETIME()));

		retScript.setDuration(this.duration);
	
		// FDP1100 TM 19/05/2015

		retScript.setCompleteUser(gen.getUSERCODE());

		// End FDP1100

		retScript.setCompleteCode(gen.getCOMPLETECODE());

		retScript.setWorkGroup(gen.getWORKGROUPCODE());

		retScript.setReturnDate(Common.generateFieldreachDBDate(new Date()));

		retScript.setResultStatus(this.initialStatus);

		retScript.setReturnFile(this.fileName);

		retScript.setAltEquipRef(ext.getValue(XmlElements.ALTEQUIPREF));

		retScript.setRunType(gen.getRUNTYPE());

		retScript.setResAssocCode(gen.getRESASSOCCODE());
		
		// End FDP1165
		
		try {

			this.scriptResultsService.saveReturnedScript(this.fieldreachInstance, retScript);

		} catch (Exception e) {

			log.error("Error updating ReturnedScripts table:\n" + e.getMessage());
			throw new LoadScriptResultException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updatedReturnedScripts");
	}

	/**
	 * Create new record in the ResultStatusLog table.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void updatedResultStatusLog() throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> updatedResultStatusLog");

		ResultStatusLog rsl = new ResultStatusLog();

		ResultStatusLogId id = new ResultStatusLogId();
		id.setReturnId(this.generatedReturnId);
		id.setResultStatus(this.initialStatus);

		rsl.setId(id);
		rsl.setUserCode(Common.SYSTEM_USERCODE);
		rsl.setAquiredDate(Common.generateFieldreachDBDate());
		rsl.setAquiredTime(Common.generateFieldreachDBTime());

		try {

			this.scriptResultsService.saveResultStatusLog(this.fieldreachInstance, rsl);

		} catch (Exception e) {

			log.error("Error updating ResultStatusLog table:\n" + e.getMessage());
			throw new LoadScriptResultException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updatedResultStatusLog");
	}

	/**
	 * Updates the returnId of the record in the work issued table if necessary.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void updatedWorkIssued() throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> updatedWorkIssued returnId=" + this.generatedReturnId);

		try {

			if (this.resultSet.getScriptResults().getPROFILE().getGENERAL().getRUNTYPE()
					.equalsIgnoreCase(XmlLoaderUtils.MAIN_SCRIPT_RUN_TYPE)) {

				String sourceFile = this.resultSet.getScriptResults().getPROFILE().getGENERAL().getSOURCEFILE();

				if (sourceFile != null && !sourceFile.trim().equals("")) {

					this.sourceFileImportFlag = this.workOrderService.sourceFileUpdate(this.fieldreachInstance,
							this.generatedReturnId, sourceFile);
				}
			}

		} catch (Exception e) {

			log.error("Error updating WorkIssued table:\n" + e.getMessage());

			throw new LoadScriptResultException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updatedWorkIssued");
	}

	/**
	 * Create the appropriate records in the Fieldreach database for each child
	 * 'Item' element contained in the 'Results' element of the script result.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void processResults() throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> processResults");

		for (com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Item item : this.resultSet.getScriptResults()
				.getRESULTS().getITEM()) {

			this.updateScriptResults(item);

			this.updateSubScriptLink(item);

			this.updateScriptResultsTxt(item);

			this.updateScriptResultBlb(item);

			this.updateScriptResultsDef(item);

		}

		if (log.isDebugEnabled())
			log.debug("<<< processResults");
	}

	/**
	 * Create a record in the ScriptResults table using the supplied 'Item'
	 * element.
	 * 
	 * @param itemElement
	 *            Item object extracted from the script result file being
	 *            processed.
	 * 
	 * @returnResponse value inserted into the ScriptResults table.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private String updateScriptResults(com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Item itemElement)
			throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> updateScriptResults itemElement=XXX");

		// Begin by processing the response Element.
		String response = this.processResponse(itemElement, this.scriptItems.get(Integer.parseInt(itemElement.getSeqNo())));

		com.amtsybex.fieldreach.backend.model.ScriptResults sr = new com.amtsybex.fieldreach.backend.model.ScriptResults();

		ScriptResultId id = new ScriptResultId();
		id.setReturnId(this.generatedReturnId);
		id.setSequenceNo(Integer.parseInt(itemElement.getSeqNo()));
		id.setResOrderNo(Integer.parseInt(itemElement.getResOrderNo()));

		sr.setId(id);
		sr.setResponse(response);
		sr.setResponseType(itemElement.getRESPONSE().getType());
		sr.setResultDate(Integer.parseInt(itemElement.getDATE()));
		sr.setResultTime(Integer.parseInt(itemElement.getTIME()));
		sr.setUom(itemElement.getUOM());
		sr.setPreValue(itemElement.getPREV());
		sr.setStatus(this.resultSet.getScriptResults().getPROFILE().getGENERAL().getSCRIPTSTATUS());

		sr.setOotFlag(this.getOOTFlag(itemElement, response));
		
		if(sr.getOotFlag() == 1) {
			ootFlagForWorkFlowRules = 1;
		}

		ScriptResultScore srScore = this.calculateScore(itemElement, response);
		sr.setScore(srScore.getScore());
		sr.setMaxScore(srScore.getMaxScore());

		try {

			this.scriptResultsService.insertScriptResults(this.fieldreachInstance, sr);

		} catch (Exception e) {

			log.error("Error updating ScriptResults table:\n" + e.getMessage());
			throw new LoadScriptResultException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateScriptResults");

		return response;
	}

	/**
	 * Function to generate a response value for insertion into the
	 * ScriptResults table using the supplied Response element. If the response
	 * type is N/A or N/R this will be used as the response.
	 * 
	 * If the response type is OK and the response originates from one of 3
	 * special item types (MULTI PICK, MULTI PICK (RULE BASED), TASK LIST) the
	 * response will be derived and various supplementary tables will also be
	 * updated. These tables are SuppResults, TaskListRes and TaskListValues.
	 * 
	 * For all other item types, the first 100 characters of the response will
	 * be returned and the remainder stored in the ExtdResponse table.
	 * 
	 * @param itemElement
	 *            Item element to generate a response value for.
	 * 
	 * @param scriptItem
	 *            Corresponding script Item for the question that generated the
	 *            Response.
	 * 
	 * @return Value that should be inserted into the response column of the
	 *         ScriptResults table.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private String processResponse(com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Item itemElement, Item scriptItem)
			throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> processResponse itemElement=XXX scriptItem=XXX");

		String parsedResponse = null;

		Response response = itemElement.getRESPONSE();

		if (response.getType().equals(Common.RESPONSE_TYPE_OK)) {

			if (scriptItem.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK_RULE)
					|| scriptItem.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK)) {

				// Process MULTI PICK AND MULTI PICK (RULE BASED).

				String[] responses = response.getValue().split("\\" + Common.MULTI_PICK_RESPONSE_DELIMITER);

				// First response will be inserted into ScriptResults table.
				parsedResponse = responses[0];

				cacheResponseForWorkFlowStatusEvaluation(responses[0]);
				
				// Create a record for in the SuppResults table for the each of
				// remaining responses.
				for (int i = 1; i < responses.length; i++) {

					cacheResponseForWorkFlowStatusEvaluation(responses[i]);
					
					this.updateSuppResults(responses[i], Integer.parseInt(itemElement.getSeqNo()), Integer.parseInt(itemElement.getResOrderNo()));
				}

			} else if (scriptItem.getItemType().equals(Common.QUESTION_TYPE_TASK_LIST)) {

				// Process TASK LIST.

				String[] tasks = response.getValue().split("\\" + Common.TASK_LIST_RESPONSE_DELIMITER);

				// Parse each task
				int totalTasks = tasks.length;
				int completedTasks = 0;
				List<String> taskNames = new ArrayList<String>();
				List<String> completeCodes = new ArrayList<String>();

				for (String task : tasks) {

					String taskName = StringUtils.left(task, task.length() - 3);
					String completeCode = StringUtils.right(task, 2);

					if (completeCode.equalsIgnoreCase(Common.TASK_LIST_TASK_COMPLETE)) {
						cacheResponseForWorkFlowStatusEvaluation(taskName);
						completedTasks++;
					}

					taskNames.add(taskName);
					completeCodes.add(completeCode);
				}

				// Response for ScriptResults table
				parsedResponse = completedTasks + " of " + totalTasks;

				// Update the TaskListRes and TaskListValues tables
				this.processTaskListResponse(taskNames, completeCodes, Integer.parseInt(itemElement.getSeqNo()),
						Integer.parseInt(itemElement.getResOrderNo()));

			} else if((scriptItem.getItemType().equals(Common.QUESTION_TYPE_GEOMETRY))) {
				
				// FDE037 TM 29/02/2016
				// Handle GEOMETRY response
	
				byte[] decodedData = Common.decodeBase64(itemElement.getRESPONSEFILE());
								
				boolean written = Common.writeBytesToFile(decodedData, 
						itemElement.getRESPONSEFILENAME(), this.geometryDataDir);
				
				if(!written) {
					
					throw new LoadScriptResultException("Unable to write binary GEOMETRY data to disk.");
				}
				
				// Record the file written so it can be deleted in the event of an exception
				this.geometryFiles.add(itemElement.getRESPONSEFILENAME());
				
				String[] attributeData = response.getValue().split("\\" + Common.MULTI_PICK_RESPONSE_DELIMITER);

				// Create a record for in the SuppResults table for the each response.
				for (int i = 0; i < attributeData.length; i++) {
					this.updateSuppResults(attributeData[i], Integer.parseInt(itemElement.getSeqNo()), Integer.parseInt(itemElement.getResOrderNo()));
					cacheResponseForWorkFlowStatusEvaluation(attributeData[i]);
				}
				
				// End FDE037
				
			} else {

				// Process all other responses.
				cacheResponseForWorkFlowStatusEvaluation(response.getValue());
				// Updated ExtdResponse table if required.
				if (response.getValue().length() > 100) {

					// Use first 100 characters for the response.
					parsedResponse = StringUtils.left(response.getValue(), 100);

					// Create record in ExtdResponse table with remainder of the
					// response.
					String extdResponse = StringUtils.right(response.getValue(), response.getValue().length() - 100);

					this.updateExtdResponse(extdResponse, Integer.parseInt(itemElement.getSeqNo()), Integer.parseInt(itemElement.getResOrderNo()));

				} else {

					parsedResponse = response.getValue();
				}
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< processResponse");

		return parsedResponse;
	}

	/**
	 * Create a record in the SuppResults table using the information supplied.
	 * 
	 * @param response
	 * @param seqNo
	 * @param resOrderNo
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void updateSuppResults(String response, int seqNo, int resOrderNo)
			throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled()) {

			log.debug(">>> updateSuppResults response=" + response + " seqNo=" + seqNo + "resOrderNo=" + resOrderNo);
		}

		SuppResults sr = new SuppResults();
		SuppResultsId id = new SuppResultsId();

		id.setReturnId(this.generatedReturnId);
		id.setResOrderNo(resOrderNo);
		id.setResponse(response);

		sr.setId(id);
		sr.setSequenceNo(seqNo);

		try {

			this.scriptResultsService.saveSuppResults(this.fieldreachInstance, sr);

		} catch (Exception e) {

			log.error("Error updating SuppResults table:\n" + e.getMessage());
			throw new LoadScriptResultException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateSuppResults");
	}

	/**
	 * Create new record in the ExtdResponse table using the information
	 * supplied.
	 * 
	 * @param extdResponse
	 * @param seqNo
	 * @param resOrderNo
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void updateExtdResponse(String extdResponse, int seqNo, int resOrderNo)
			throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled()) {

			log.debug(">>> updateExtdResponse extdResponse=XXX seqNo=" + seqNo + " resOrderNo=" + resOrderNo);
		}

		ExtdResponse er = new ExtdResponse();
		ScriptResultId id = new ScriptResultId();

		id.setReturnId(this.generatedReturnId);
		id.setSequenceNo(seqNo);
		id.setResOrderNo(resOrderNo);

		er.setId(id);
		er.setExtdResponse(extdResponse);

		try {

			this.scriptResultsService.saveExtdResponse(this.fieldreachInstance, er);

		} catch (Exception e) {

			log.error("Error updating ExtdResponse table:\n" + e.getMessage());
			throw new LoadScriptResultException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateExtdResponse");
	}

	/**
	 * Create records in the TaskListRes and TaskListValues tables using the
	 * information supplied.
	 * 
	 * @param taskNames
	 * @param completeCodes
	 * @param seqNo
	 * @param resOrderNo
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void processTaskListResponse(List<String> taskNames, List<String> completeCodes, int seqNo, int resOrderNo)
			throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled()) {

			log.debug(">>> processTaskListResponse taskNames=XXX completeCodes=XXX seqNo=" + seqNo + " resOrderNo="
					+ resOrderNo);
		}

		int nextTaskListResId;

		for (int i = 0; i < taskNames.size(); i++) {

			// Generate TaskListId.
			try {
				
				// FDP1165 TM 25/11/2015
				// Removed synchronised block as code no longer multi threaded.
				nextTaskListResId = scriptResultsService.getNextTaskListResId(this.fieldreachInstance);

			} catch (Exception e) {

				throw new LoadScriptResultException("Error generating TaskListResId for new entry in TaskListRes.");
			}

			// Update TaskListRes table
			TaskListRes tlr = new TaskListRes();

			TaskListResId tlrId = new TaskListResId();
			tlrId.setReturnId(this.generatedReturnId);
			tlrId.setTaskListResId(nextTaskListResId);
			tlrId.setSequenceNo(seqNo);
			tlrId.setResOrderNo(resOrderNo);

			tlr.setId(tlrId);

			try {

				this.scriptResultsService.saveTaskListRes(this.fieldreachInstance, tlr);

			} catch (Exception e) {

				log.error("Error updating TaskListValues table:\n" + e.getMessage());
				throw new LoadScriptResultException(e);
			}

			// Create entries in TaskListValues table.
			TaskListValues tlv = new TaskListValues();

			TaskListValuesId tlvId = new TaskListValuesId();

			tlvId.setTaskListId(nextTaskListResId);
			tlvId.setTaskOrderNo(i + 1);

			tlv.setId(tlvId);
			tlv.setTaskCompleteCode(completeCodes.get(i));
			tlv.setTaskDesc(taskNames.get(i));

			try {

				this.scriptResultsService.saveTaskListValues(this.fieldreachInstance, tlv);

			} catch (Exception e) {

				log.error("Error updating TaskListValues table:\n" + e.getMessage());
				throw new LoadScriptResultException(e);
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< processTaskListResponse");
	}

	/**
	 * Create a record on the SubScriptLink table if required.
	 * 
	 * @param itemElement
	 *            Item object extracted from the script result file being
	 *            processed.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void updateSubScriptLink(com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Item itemElement)
			throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> updateSubScriptLink");

		try {

			for (SubScriptResults subRes : itemElement.getSUBSCRIPTRESULTS()) {

				SubScriptLink ssl = new SubScriptLink();
				SubScriptLinkId id = new SubScriptLinkId();

				id.setReturnId(this.generatedReturnId);
				id.setSequenceNo(Integer.parseInt(itemElement.getSeqNo()));
				id.setResOrderNo(Integer.parseInt(itemElement.getResOrderNo()));
				id.setReturnFile(subRes.getRESULTSFILE());

				ssl.setId(id);

				this.scriptResultsService.saveSubScriptLink(this.fieldreachInstance, ssl);
			}

		} catch (Exception e) {

			log.error("Error updating SubScriptLink table:\n" + e.getMessage());
			throw new LoadScriptResultException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateSubScriptLink");
	}

	/**
	 * Create a record in the FreeText table using the supplied 'Item' element
	 * if necessary.
	 * 
	 * @param itemElement
	 *            Item object extracted from the script result file being
	 *            processed.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void updateScriptResultsTxt(com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Item itemElement)
			throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> updateScriptResultsTxt itemElement=XXX");

		if (itemElement.getFREETEXT() != null && !itemElement.getFREETEXT().trim().equals("")) {

			hasFreeTextForWorkFlowRules = true;
			
			ScriptResultsTxt srt = new ScriptResultsTxt();

			ScriptResultId srtId = new ScriptResultId();
			srtId.setReturnId(this.generatedReturnId);
			srtId.setSequenceNo(Integer.parseInt(itemElement.getSeqNo()));
			srtId.setResOrderNo(Integer.parseInt(itemElement.getResOrderNo()));

			srt.setId(srtId);
			srt.setFreeText(itemElement.getFREETEXT());

			try {

				this.scriptResultsService.saveScriptResultsTxt(this.fieldreachInstance, srt);

			} catch (Exception e) {

				log.error("Error updating ScriptResultsTxt table:\n" + e.getMessage());
				throw new LoadScriptResultException(e);
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateScriptResultsTxt");
	}

	/**
	 * Calculate score for the item supplied if necessary.
	 * 
	 * @param itemElement
	 *            Item object extracted from the script result file being
	 *            processed.
	 * 
	 * @param valProperty
	 *            Validation property value to search for when calculating the
	 *            score.
	 * @return Returns a ScriptResultScore populated with score and max score
	 *         values.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private ScriptResultScore calculateScore(com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Item itemElement,
			String valProperty) throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled()) {

			log.debug(">>> calculateScore itemElement=XXX valProperty=" + valProperty);
		}

		ScriptResultScore srScore = new ScriptResultScore();

		// Only calculate score if response OK and calculate scores flag has
		// been set.
		if (this.calcScoreFlag
				&& itemElement.getRESPONSE().getType().equalsIgnoreCase(Common.RESPONSE_TYPE_OK)) {

			Item scriptItem = this.scriptItems.get(Integer.valueOf(itemElement.getSeqNo()));

			Integer importance = -1;
			Integer score = 0;
			Integer maxScore = 0;
			Integer weightScore = null;
			Integer relWeight = scriptItem.getRelWeight();

			// Calculate importance and score values first.
			if (relWeight != null) {

				ValidationProperty valProp = this.validationTypeService.getValidationProperty(this.fieldreachInstance,
						scriptItem.getValidation(), valProperty);

				if (valProp != null && valProp.getWeightScore() != null) {

					weightScore = valProp.getWeightScore();

					importance = relWeight;
					score = weightScore * relWeight;
				}
			}

			// Calculate maxScore
			if (importance != -1 && score > 0) {

				// Get maximum weight score for validation type associated wioth
				// script item.
				List<ValidationProperty> valProps = this.validationTypeService
						.getValidationPropertyByValidationTypeWeightScoreDesc(this.fieldreachInstance,
								scriptItem.getValidation());

				ValidationProperty maxValProp = null;

				if (valProps != null && !valProps.isEmpty())
					maxValProp = valProps.get(0);

				Integer maxWeightScore = maxValProp.getWeightScore();

				if (maxWeightScore != null && maxWeightScore != 0)
					maxScore = maxWeightScore * importance;
			}

			srScore.setScore(score);
			srScore.setMaxScore(maxScore);
		}

		if (log.isDebugEnabled())
			log.debug("<<< calculateScore");

		return srScore;
	}

	/**
	 * Create record in the ScriptResultsBlb table if necessary
	 * 
	 * @param itemElement
	 *            Item object extracted from the script result file being
	 *            processed.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void updateScriptResultBlb(com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Item itemElement)
			throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> updateScriptResultBlb itemElement=XXX");

		if (!itemElement.getRESPONSEFILE().equals("")) {

			try {

				ScriptResultBlb srb = new ScriptResultBlb();

				ScriptResultId srbId = new ScriptResultId();
				srbId.setReturnId(this.generatedReturnId);
				srbId.setSequenceNo(Integer.parseInt(itemElement.getSeqNo()));
				srbId.setResOrderNo(Integer.parseInt(itemElement.getResOrderNo()));

				srb.setId(srbId);
				srb.setFileName(itemElement.getRESPONSEFILENAME());

				Blob blob = new SerialBlob(Common.decodeBase64(itemElement.getRESPONSEFILE()));

				srb.setBlobResult(blob);

				this.scriptResultsService.saveScriptResultBlb(this.fieldreachInstance, srb);

			} catch (SerialException e) {

				log.error("Error updating ScriptResultBlb table:\n" + e.getMessage());
				throw new LoadScriptResultException(e);

			} catch (SQLException e) {

				log.error("Error updating ScriptResultBlb table:\n" + e.getMessage());
				throw new LoadScriptResultException(e);

			} catch (Exception e) {

				log.error("Error updating ScriptResultBlb table:\n" + e.getMessage());
				throw new LoadScriptResultException(e);
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateScriptResultBlb");
	}

	/**
	 * Calculate the OOTFlag for the script result if necessary.
	 * 
	 * @param itemElement
	 *            Item object extracted from the script result file being
	 *            processed.
	 * 
	 * @param response
	 *            Response value to check for OOT.
	 * 
	 * @return ootFlag value.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private int getOOTFlag(com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Item itemElement, String response)
			throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> getOOTFlag response=" + response + " itemElement=XXX");

		int ootFlag;

		boolean upperFlag = false, lowerFlag = false;

		Item scriptItem = this.scriptItems.get(Integer.valueOf(itemElement.getSeqNo()));

		// Only set OOT for numeric and decimal questions.
		if ((scriptItem.getItemType().equals(Common.QUESTION_TYPE_NUMERIC)
				|| scriptItem.getItemType().equals(Common.QUESTION_TYPE_DECIMAL))
				&& itemElement.getRESPONSE().getType().equals(Common.RESPONSE_TYPE_OK)) {

			double upperWarning = 0, lowerWarning = 0;
			String upperWarningStr = null, lowerWarningStr = null;

			// Get upper and lower warning values from UomNumValid if UOM
			// is present, otherwise get from GenNumValid.
			if (itemElement.getUOM() != null && !itemElement.getUOM().equals("")) {

				for (UomNumValid uom : this.uomNumValid) {

					if (uom.getId().getSequenceNumber() == Integer.parseInt(itemElement.getSeqNo())
							&& uom.getId().getUomType().equals(itemElement.getUOM())) {

						lowerWarningStr = uom.getLowerWarning();
						upperWarningStr = uom.getUpperWarning();
					}
				}

			} else {

				for (GenNumValid gen : this.genNumValid) {

					if (gen.getId().getSequenceNumber() == Integer.parseInt(itemElement.getSeqNo())) {

						lowerWarningStr = gen.getLowerWarning();
						upperWarningStr = gen.getUpperWarning();
					}
				}
			}

			// Convert response into a double value for comparison.
			double respDouble = Double.parseDouble(response);

			// Convert upper and lower warning from string to double and set
			// warning flags.
			if (lowerWarningStr != null && !lowerWarningStr.trim().equals("")) {

				lowerWarning = Double.parseDouble(lowerWarningStr);

				if (respDouble <= lowerWarning)
					lowerFlag = true;
			}

			if (upperWarningStr != null && !upperWarningStr.trim().equals("")) {

				upperWarning = Double.parseDouble(upperWarningStr);

				if (respDouble >= upperWarning)
					upperFlag = true;
			}

		}

		ootFlag = ((upperFlag || lowerFlag) ? 1 : 0);

		if (log.isDebugEnabled())
			log.debug("<<< getOOTFlag");

		return ootFlag;
	}

	/**
	 * Add records to the ScriptResultsDef table if required.
	 * 
	 * @param itemElement
	 *            Item object extracted from the script result file being
	 *            processed.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void updateScriptResultsDef(com.amtsybex.fieldreach.xmlloader.scriptresult.oxm.Item itemElement)
			throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> updateScriptResultsDef itemElement=XXX");

		if (itemElement.getDEFECTS() != null) {

			for (Defect defect : itemElement.getDEFECTS().getDEFECT()) {

				ScriptResultsDef srd = new ScriptResultsDef();

				ScriptResultsDefId id = new ScriptResultsDefId();
				id.setReturnId(this.generatedReturnId);
				id.setSequenceNo(Integer.parseInt(itemElement.getSeqNo()));
				id.setResOrderNo(Integer.parseInt(itemElement.getResOrderNo()));
				id.setDefectCode(defect.getValue());

				srd.setId(id);
				srd.setAction(defect.getAction());
				srd.setRating(defect.getRate());
				srd.setLogNo(defect.getLogNo());

				try {

					this.scriptResultsService.saveScriptResultsDef(this.fieldreachInstance, srd);

				} catch (Exception e) {

					log.error("Error updating ScriptResultsDef table:\n" + e.getMessage());
					throw new LoadScriptResultException(e);
				}
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateScriptResultsDef");
	}

	
	
	private void updateRuntimeCalcResults() throws FRInstanceException, LoadScriptResultException {
		
		if (log.isDebugEnabled())
			log.debug(">>> updateRuntimeCalcResults");
		
		if (this.resultSet.getScriptResults().getPROFILE().getEXTENDED() != null) {
			
			String key = null;

			for (Entry<String, String> entry : this.resultSet.getScriptResults().getPROFILE().getEXTENDED().getValues().entrySet()) {
			    
				key = entry.getKey().toUpperCase();
				if (key.startsWith("RTC_")) {
			        
					RuntimeCalcResults calcResult = new RuntimeCalcResults( new RuntimeCalcResultsId(this.generatedReturnId, key), Double.parseDouble(entry.getValue()));

					try {
						this.scriptResultsService.saveRuntimeCalcResults(this.fieldreachInstance, calcResult);
					} catch (Exception e) {

						log.error("Error updating RuntimeCalcResults table:\n" + e.getMessage());
						throw new LoadScriptResultException(e);
					}
			    }
			}
			
		}
		
		
		if (log.isDebugEnabled())
			log.debug("<<< updateRuntimeCalcResults");
	}
	
	/**
	 * Add/Update records to the EquipmentData table if required.
	 * 
	 * FDP1293 - MC - update equipment data table from extended section
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 * @throws ParseException 
	 */
	private void updateEquipmentData() throws FRInstanceException, LoadScriptResultException, ParseException {
		
		if (log.isDebugEnabled())
			log.debug(">>> updateEquipmentData");
		
		if (this.resultSet.getScriptResults().getPROFILE().getEXTENDED() != null) {
			
			String equipNo = this.resultSet.getScriptResults().getPROFILE().getEXTENDED().getValues().get("ASSET_EQUIPNO");
			
			EquipmentData equipData = this.equipmentService.getEquipmentData(null,equipNo);
			
			String key = null;
			
			Boolean doRefresh = false;
			
			List<EquipmentAttrib> equipAttribList = null;
			
			EquipmentAttrib equipmentAttribute = null;
			
			Integer equipmentAtrribUpdateDate = null;
			
			if(this.getEquipmentAttribLoad()) {
					
					//41290 - Update EquipmentAttrib Table
					equipAttribList = new ArrayList<EquipmentAttrib>();
					
					equipmentAtrribUpdateDate =  equipData!=null ? equipData.getAttribDate() : null;
					//check if the refresh date has expired
					doRefresh = isRefreshDayExpired(equipData);
					
					//if refresh date expired delete existing data for that equip no
					if(doRefresh) {
						this.equipmentService.deleteExistingEquipAtrrib(null,equipNo);
					}
			}
			for (Entry<String, String> entry : this.resultSet.getScriptResults().getPROFILE().getEXTENDED().getValues().entrySet()) {
				key = entry.getKey().toUpperCase();
				
				//if data is to be loaded and refresh day expired
				if(this.getEquipmentAttribLoad() && doRefresh) {
					if (key.startsWith("ASSET_")) {
						
						equipmentAttribute = new EquipmentAttrib();
						
						String attribName = key.replaceFirst("ASSET_", "");
						
						equipmentAttribute.setEquipNo(equipNo);
						equipmentAttribute.setAttributeName(attribName);
						equipmentAttribute.setValue(entry.getValue());
						
						equipAttribList.add(equipmentAttribute);
					}
				}
				
				if (key.startsWith("ASSET_")) {
					
					if(equipData == null){
			        	equipData = new EquipmentData();
			        }
					
					if(key.equals("ASSET_EQUIPNO")){
						equipData.setEquipNo(entry.getValue());
						
					}else if(key.equals("ASSET_PARENTEQUIPNO")){
						
						equipData.setParentEquipNo(entry.getValue());
						
					}else if(key.equals("ASSET_ALTEQUIPREF")){
						
						equipData.setAltEquipRef(entry.getValue());
						
					}else if(key.equals("ASSET_EQUIPDESC")){
						
						equipData.setEquipDesc(entry.getValue());
						
					}else if(key.equals("ASSET_EQUIPTYPE")){
						
						equipData.setEquipType(entry.getValue());
						
					}else if(key.equals("ASSET_LATITUDE")){
						//FDP1430 - MC - add Latitude Longitude to EquipmentData
						equipData.setLatitude(entry.getValue());
						
					}else if(key.equals("ASSET_LONGITUDE")){
						//FDP1430 - MC - add Latitude Longitude to EquipmentData
						equipData.setLongitude(entry.getValue());
					}
			    }
				
			}
			//check the flag, if refresh has been done, update the completed date from scipt results if yes.
			//update with the existing date in db if no refresh has occured
			if(equipData!=null) {
				equipData.setAttribDate(doRefresh ? Integer.valueOf(this.resultSet.getScriptResults().getPROFILE().getGENERAL().getCOMPLETEDATE())  : equipmentAtrribUpdateDate);
			}
			
			try {
				//save the new list of equip attrib elements to equipAttrib table
				if(equipAttribList != null && equipAttribList.size()!=0){
					this.equipmentService.saveEquipmentAttrib(this.fieldreachInstance,equipAttribList);
				}
				
				if(equipData != null && equipData.getEquipNo().length() > 0){
					this.equipmentService.saveEquipmentData(this.fieldreachInstance, equipData);
				}
			} catch (Exception e) {

				log.error("Error updating EquipmentData table:\n" + e.getMessage());
				throw new LoadScriptResultException(e);
			}
			
		}
		if (log.isDebugEnabled())
			log.debug("<<< updateEquipmentData");
	}
	
	/**
	 * method to check if the refresh day is expired and the new equip attrib needs to be loaded again
	 * 
	 * FDE60 - update equipment attrib table
	 * @param 
	 * equipNo
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 * @throws ParseException 
	 */
	public boolean isRefreshDayExpired(EquipmentData equipData) throws FRInstanceException, ParseException{
		//if equipment data doesn't exist skip load to equip attrib
			
			Integer equipmentAtrribUpdateDate = equipData!=null? equipData.getAttribDate() : null;
			//if date in equipAttrib is null, new data is to be added.
			//if it exists, check for expiry
		
			if(equipmentAtrribUpdateDate!=null) {
			
				//Date of completion of script results
				Integer scriptResultCompletionDate = Integer.valueOf(this.resultSet.getScriptResults().getPROFILE().getGENERAL().getCOMPLETEDATE());
				
				SimpleDateFormat sdf =  new SimpleDateFormat("yyyyMMdd");
		        
				Calendar c = Calendar.getInstance();
				
				//Last updated date in equipmentData Db
				c.setTime(sdf.parse(String.valueOf(equipmentAtrribUpdateDate))); 
				
				//number of days added to the last updated date to determine next refresh day
				c.add(Calendar.DATE, this.getEquipmentAttribRefreshDays()); 
				
				//the date value for the next refresh to occur
				Integer nextRefreshDate = Integer.valueOf(sdf.format(c.getTime()));
				
				//if scriptCompletion date is after next refresh date then set to doRefresh
				//if script completion day is same or before refresh date do not refresh the data
				//43559 - Bug fix
				if(sdf.parse(scriptResultCompletionDate.toString()).after(sdf.parse(nextRefreshDate.toString())) 
						|| sdf.parse(scriptResultCompletionDate.toString()).equals(sdf.parse(nextRefreshDate.toString()))){
					return true;
				}
			}
			else {
				return true;
			}
		//flag indicates that data refresh is skipped and AttriDate would not be updated
		return false;
	}
	
	
	
	
	/**
	 * Add records to the CarryThroughRes table if required.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void updateCarryThroughRes() throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> updateCarryThroughRes");

		if (this.resultSet.getScriptResults().getCARRYTHROUGH() != null) {

			for (DataItem dataItem : this.resultSet.getScriptResults().getCARRYTHROUGH().getDATAITEM()) {

				CarrythroughRes ctRes = new CarrythroughRes();

				CarrythroughResId id = new CarrythroughResId();
				id.setReturnId(this.generatedReturnId);
				id.setFieldName(dataItem.getFieldName());

				ctRes.setId(id);
				ctRes.setFieldValue(dataItem.getValue());

				try {

					this.scriptResultsService.saveCarrythroughRes(this.fieldreachInstance, ctRes);

				} catch (Exception e) {

					log.error("Error updating CarrythorughRes table:\n" + e.getMessage());
					throw new LoadScriptResultException(e);
				}
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateCarryThroughRes");
	}

	/**
	 * Add a record to the SourceFiles table if required.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void updateSourceFiles() throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> updateSourceFiles");

		String sourceFile = this.resultSet.getScriptResults().getPROFILE().getGENERAL().getSOURCEFILE();

		if ((sourceFile != null && !sourceFile.trim().equals("")) && this.resultSet.getScriptResults().getPROFILE()
				.getGENERAL().getRUNTYPE().equals(XmlLoaderUtils.MAIN_SCRIPT_RUN_TYPE) && this.sourceFileImportFlag) {

			SourceFiles sf = new SourceFiles();

			sf.setId(this.generatedReturnId);
			sf.setSourceFileName(sourceFile);

			try {

				this.scriptResultsService.saveSourceFiles(this.fieldreachInstance, sf);

			} catch (Exception e) {

				log.error("Error updating CarrythorughRes table:\n" + e.getMessage());
				throw new LoadScriptResultException(e);
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateSourceFiles");
	}

	/**
	 * Calculates and sets the WeightScore in the ScriptResults table for each
	 * record with a returnId matching that supplied. Sets the values of the
	 * TotalWeightScore records in the ReturnedScripts table with a ReturnId
	 * matching that supplied.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void calculateWeightScores() throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> calculateWeightScores");

		try {

			this.scriptResultsService.calculateWeightScores(this.fieldreachInstance, this.generatedReturnId);

		} catch (Exception e) {

			log.error("Error calculating weight scores:\n" + e.getMessage());
			throw new LoadScriptResultException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<<< calculateWeightScores");
	}

	/**
	 * Create a record on the InterfaceQueue table if required.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void updateInterfaceQueue() throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> updateInterfaceQueue");

		try {

			if (this.initialStatusSystemFlag) {

				InterfaceQueue iq = new InterfaceQueue();
				InterfaceQueueId id = new InterfaceQueueId();

				id.setReturnId(this.generatedReturnId);
				id.setResultStatus(this.initialStatus);
				id.setScriptCodeId(Integer.parseInt(this.resultSet.getScriptResults().getScriptCodeId()));
				iq.setId(id);

				this.scriptResultsService.saveInterfaceQueue(this.fieldreachInstance, iq);
			}

		} catch (Exception e) {

			log.error("Error updating InterfaceQueue table:\n" + e.getMessage());
			throw new LoadScriptResultException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateInterfaceQueue");
	}

	// FDE037 TM 29/02/2016
	
	/**
	 * Method to remove any geometry questions that were created but need to be
	 * removed due to the result not loading.
	 */
	private void geometryTidyUp() {
		
		if (log.isDebugEnabled())
			log.debug(">>> geometryTidyUp");
		
		for (String file : this.geometryFiles) {
			
			File geometryFile = new File (this.geometryDataDir + File.separatorChar + file);
			
			if (!geometryFile.delete()) {
				log.error("Unable to delete file: " + geometryFile.getName());
				
				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "load geometry Unable to delete file", "Unable to delete file: " + geometryFile.getName() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
			}
		}
		
		if (log.isDebugEnabled())
			log.debug("<<< geometryTidyUp");
	}
	
	// End FDE037
	
	/**
	 * Create a record on the ScriptResultXml table if required.
	 * 
	 * @throws FRInstanceException
	 * @throws LoadScriptResultException
	 */
	private void updateScriptResultXml() throws FRInstanceException, LoadScriptResultException {

		if (log.isDebugEnabled())
			log.debug(">>> updateScriptResultXml");

		try {

			if (this.importResultToDb) {

				File file = new File(this.fileUri);

				this.scriptResultsService.saveScriptResultXml(this.fieldreachInstance, this.generatedReturnId, file);
			}

		} catch (Exception e) {

			log.error("Error updating ScriptResultXml table:\n" + e.getMessage());
			throw new LoadScriptResultException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateScriptResultXml");
	}

	class ScriptResultScore {

		private int score;
		private int maxScore;

		public ScriptResultScore() {

		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public int getMaxScore() {
			return maxScore;
		}

		public void setMaxScore(int maxScore) {
			this.maxScore = maxScore;
		}

	}
}
