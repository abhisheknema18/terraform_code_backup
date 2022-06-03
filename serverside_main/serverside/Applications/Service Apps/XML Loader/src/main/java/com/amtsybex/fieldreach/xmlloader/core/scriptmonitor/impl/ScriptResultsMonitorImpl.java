package com.amtsybex.fieldreach.xmlloader.core.scriptmonitor.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import java.math.BigDecimal;
import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.instance.InstanceManager;
import com.amtsybex.fieldreach.backend.instance.Transaction;
import com.amtsybex.fieldreach.backend.model.MonitorAlertStatusLog;
import com.amtsybex.fieldreach.backend.model.MonitorAlerts;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.RuntimeCalcResults;
import com.amtsybex.fieldreach.backend.model.ScriptMonitor;
import com.amtsybex.fieldreach.backend.model.SystemEventLog;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTCATEGORY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTTYPE;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SEVERITY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SOURCESYSTEM;
import com.amtsybex.fieldreach.backend.service.ScriptMonitorService;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.backend.service.SystemEventService;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.amtsybex.fieldreach.xmlloader.core.scriptmonitor.ScriptResultsMonitor;

public class ScriptResultsMonitorImpl implements ScriptResultsMonitor{

	private static final Logger log = LoggerFactory.getLogger(ScriptResultsMonitorImpl.class.getName());
	
	@Autowired
	private InstanceManager instanceManager;
	
	@Autowired
	private SystemEventService mSystemEventLogger;

	@Autowired(required = true)
	@Qualifier("scriptResultsService")
	protected ScriptResultsService scriptResultsService;

	@Autowired(required = true)
	@Qualifier("scriptService")
	protected ScriptService scriptService;
	
	@Autowired(required = true)
	@Qualifier("scriptMonitorService")
	protected ScriptMonitorService scriptMonitorService;
	
	public void performScriptMonitor(String instance, ReturnedScripts workingScriptResult) {
		
		log.debug(">>> performScriptMonitor");
		
		Integer scriptId = workingScriptResult.getScriptId();
		
		Transaction trans = null;
		try {
			trans = instanceManager.getTransaction(instance);

			List<ScriptMonitor> monitors = this.scriptMonitorService.getActiveScriptMonitorForScriptId(instance, scriptId);
			
			if(monitors != null) {
				
				for(ScriptMonitor monitor : monitors) {
					
					if(this.evaluateMonitor(instance, monitor, workingScriptResult)) {
						//log alert
						log.debug("Monitor " + monitor.getId() + " evaluated for script id " + scriptId);
						
						MonitorAlerts alert = new MonitorAlerts();
						
						alert.setAlertStatus("O");
						alert.setAlertText(monitor.getAlertText());
						alert.setAlertType(monitor.getAlertType());
						alert.setEquipNo(workingScriptResult.getEquipNo());
						alert.setMonitorId(monitor.getId());
						alert.setReturnId(workingScriptResult.getId());
						alert.setScriptId(monitor.getScriptId());
						alert.setCreateDate(Common.generateFieldreachDBDate());
						alert.setCreateTime(StringUtils.leftPad(String.valueOf(Common.generateFieldreachDBTime()), 6, '0'));
						
						this.scriptMonitorService.saveMonitorAlert(instance, alert);

						MonitorAlertStatusLog statusLog = new MonitorAlertStatusLog();
						
						statusLog.setAlertId(alert.getId());
						statusLog.setStatusDate(alert.getCreateDate());
						statusLog.setStatus(alert.getAlertStatus());
						statusLog.setStatusTime(alert.getCreateTime());
						statusLog.setUserCode(Common.SYSTEM_USERCODE);
						
						this.scriptMonitorService.saveMonitorAlertStatusLog(instance, statusLog);
						
						log.info("Monitor Alert raised for scriptId=" + scriptId + " and equipNo=" + workingScriptResult.getEquipNo());
						
					}
				}
			}
			
			this.instanceManager.commitTransaction(trans);
			
		} catch (FRInstanceException e) {
			
			this.instanceManager.rollbackTransaction(trans);
			
			log.error("Error performing ScriptMonitor on scriptId: " + scriptId + "\n" + e.getMessage());
			
			mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "ScriptResultPickup Unable to update Audit database: A HibernateException occured", "Unable to update Audit database: A HibernateException occured " + e.getMessage() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
			
		}
		
		log.debug("<<< performScriptMonitor");
		
	}
	
	private boolean evaluateMonitor(String frInstance, ScriptMonitor monitor, ReturnedScripts workingResult) throws FRInstanceException {
		
		log.debug(">>> evaluateMonitor " + monitor.getId());
		
		boolean evaluated = false;
		
		if(monitor.getScriptMonitorDefn().getPatternType().equalsIgnoreCase("CO")) {
			
			evaluated = this.evaluateMonitorConsecutiveOccurences(frInstance, monitor, workingResult);
			
		}else if(monitor.getScriptMonitorDefn().getPatternType().equalsIgnoreCase("OT")) {
			
			evaluated = this.evaluateMonitorOccuranceTimeline(frInstance, monitor, workingResult);
			
		}else if(monitor.getScriptMonitorDefn().getPatternType().equalsIgnoreCase("DEV")) {
			
			evaluated =  this.evaluateMonitorDeviation(frInstance, monitor, workingResult);
			
		}
		
		log.debug("<<< evaluateMonitor " + monitor.getId() + " result " + evaluated);
		
		return evaluated;
	}
	
	private boolean evaluateMonitorConsecutiveOccurences(String frInstance, ScriptMonitor monitor, ReturnedScripts workingResult) throws FRInstanceException {
		
		//get count runtime calcs for given monitor and number of consecutive
		Long count = scriptMonitorService.getRuntimeCalcValuesInOccurrences(frInstance, monitor.getScriptId(), workingResult.getEquipNo(), monitor.getScriptMonitorDefn().getItem(), monitor.getScriptMonitorDefn().getOccurrences(), monitor.getScriptMonitorDefn().getOp(), monitor.getScriptMonitorDefn().getValue());
		
		if(count != null && count >= monitor.getScriptMonitorDefn().getOccurrences()) {
			return true;
		}
		
		return false;
	}
	
	private boolean evaluateMonitorOccuranceTimeline(String frInstance, ScriptMonitor monitor, ReturnedScripts workingResult) throws FRInstanceException {
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, monitor.getScriptMonitorDefn().getTimelineDays() * -1);
		Date fromDate = cal.getTime();
		Integer fromDateInt = Common.generateFieldreachDBDate(fromDate);
		
		Long count = scriptMonitorService.getRuntimeCalcValuesInDateRange(frInstance, monitor.getScriptId(), workingResult.getEquipNo(), monitor.getScriptMonitorDefn().getItem(), fromDateInt, monitor.getScriptMonitorDefn().getOp(), monitor.getScriptMonitorDefn().getValue());
		
		if(count != null && count >= monitor.getScriptMonitorDefn().getOccurrences()) {
			return true;
		}
		
		return false;
	}
	
	private boolean evaluateMonitorDeviation(String frInstance, ScriptMonitor monitor, ReturnedScripts workingResult) throws FRInstanceException {
		
		//get most recent value
		List<RuntimeCalcResults> results = scriptMonitorService.getMostRecentTwoValuesForScriptId(frInstance, monitor.getScriptId(), workingResult.getEquipNo(), monitor.getScriptMonitorDefn().getItem());
		//check if this is a percentage greater/less/either than the monitor value
		if(results != null) {
			
			RuntimeCalcResults previous = null;
			RuntimeCalcResults current = null;
			
			if(workingResult.getId().equals(results.get(0).getId().getReturnId())) {
				previous = results.get(1);
				current = results.get(0);
			}else {
				previous = results.get(0);
				current = results.get(1);
			}

			BigDecimal value = new BigDecimal(monitor.getScriptMonitorDefn().getValue());
			

			BigDecimal currentValue = new BigDecimal(String.valueOf(current.getCalculationValue()));
			BigDecimal previousValue = new BigDecimal(String.valueOf(previous.getCalculationValue()));
			
			BigDecimal maxDifference = previousValue.multiply(value).divide(new BigDecimal(100));

			if(monitor.getScriptMonitorDefn().getDeviationCheck().equalsIgnoreCase("ABOVE")) {
				
				BigDecimal difference = currentValue.subtract(previousValue);
				
				if(difference.compareTo(BigDecimal.ZERO) > 0) {
					if(difference.compareTo(maxDifference) > 0) {
						return true;
					}
				}

			}else if(monitor.getScriptMonitorDefn().getDeviationCheck().equalsIgnoreCase("BELOW")) {
				
				BigDecimal difference = previousValue.subtract(currentValue);
				
				if(difference.compareTo(BigDecimal.ZERO) > 0) {
					if(difference.compareTo(maxDifference) > 0) {
						return true;
					}
				}
				
			}else {
				//above or below
				BigDecimal difference = currentValue.subtract(previousValue);
				
				if(difference.compareTo(BigDecimal.ZERO) > 0) {
					if(difference.compareTo(maxDifference) > 0) {
						return true;
					}
				}
				
				difference = previousValue.subtract(currentValue);
				
				if(difference.compareTo(BigDecimal.ZERO) > 0) {
					if(difference.compareTo(maxDifference) > 0) {
						return true;
					}
				}
			}
			
		}
		
		return false;
	}
}

