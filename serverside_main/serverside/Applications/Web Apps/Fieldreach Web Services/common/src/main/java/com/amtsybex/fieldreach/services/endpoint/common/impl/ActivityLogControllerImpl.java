/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2012
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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.instance.InstanceManager;
import com.amtsybex.fieldreach.backend.instance.Transaction;
import com.amtsybex.fieldreach.backend.model.SystemActivityLog;
import com.amtsybex.fieldreach.backend.service.ActivityLogService;
import com.amtsybex.fieldreach.services.endpoint.common.ActivityLogController;
import com.amtsybex.fieldreach.utils.impl.Common;

public class ActivityLogControllerImpl implements ActivityLogController {

	private static Logger log = LoggerFactory.getLogger(ActivityLogControllerImpl.class.getName());

	private ActivityLogService activityLogService;

	private String[] enabledAppCodes;
	
	// For date/time extraction into table
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");

	@Autowired
	private InstanceManager instanceManager;

	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	
	@Override
	public void recordActivityLog(String frInstance, ACTIVITYTYPE activityType, String userCode, String deviceId, String appCode, String additionalText, String transFileName)
			throws FRInstanceException {
		
		if (log.isDebugEnabled()) {

			log.debug(">>> recordActivityLog activityType=" + Common.CRLFEscapeString(activityType.toString()) + " userCode=" + Common.CRLFEscapeString(userCode) + " deviceId=" + Common.CRLFEscapeString(deviceId));
		}

		if(!ArrayUtils.contains(enabledAppCodes, appCode)) {
			
			if (log.isDebugEnabled())
				log.debug("<<< recordActivityLog activity logging not enabled for appCode " + Common.CRLFEscapeString(appCode));
			
			return;
		}
		
		Transaction trans = this.instanceManager.getTransaction(frInstance);

		try {

			Date dateNow = new Date();
			SystemActivityLog logEntry = new SystemActivityLog(userCode, Integer.valueOf(dateFormat.format(dateNow)), timeFormat.format(dateNow) );
			
			logEntry.setDeviceId(deviceId);
			logEntry.setTransFileName(transFileName);
			logEntry.setAppCode(appCode);
			logEntry.setActivityType(activityType.toString());
			logEntry.setAdditionalText(additionalText);

			
			getActivityLogService().saveSystemActivityLogEntry(frInstance,
					logEntry);

			this.instanceManager.commitTransaction(trans);

		} catch (FRInstanceException e) {

			this.instanceManager.rollbackTransaction(trans);
			throw e;

		} catch (Throwable t) {
			
			log.error("UNEXPECTED EXCEPTION: " + t.getMessage());
			
			this.instanceManager.rollbackTransaction(trans);
			
			throw new RuntimeException(t);
		} 

		if (log.isDebugEnabled())
			log.debug("<<< recordActivityLog");
	}

	/*-------------------------------------------
	 - Spring Injection MEthods
	 --------------------------------------------*/

	public ActivityLogService getActivityLogService() {

		return activityLogService;
	}

	public void setActivityLogService(ActivityLogService activityLogService) {

		this.activityLogService = activityLogService;
	}

	public String[] getEnabledAppCodes() {
		return enabledAppCodes;
	}

	public void setEnabledAppCodes(String[] enabledAppCodes) {
		this.enabledAppCodes = enabledAppCodes;
	}


	
}
