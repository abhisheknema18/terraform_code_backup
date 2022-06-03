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
 */
package com.amtsybex.fieldreach.services.endpoint.common;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;

public interface ActivityLogController {

	//FDE044 - MC - tidy up this controller so we dont need a new function for every event
	public static enum ACTIVITYTYPE {
		USERINFO,
		CHANGE_PASSWORD,
		RESULT_SEARCH,
		GET_RESULT,
		GET_BINARY,
		GET_BINARY_PART,
		GET_NEXT_RESULT_WORKFLOW_STATUS,
		UPDATE_RESULT_WORKFLOW_STATUS,
		UPDATE_RESULT_RESPONSE,
		CREATE_RESULT_NOTE,
		GET_VIEW,
		GET_VALIDATION_LIST,
		GET_WORK_LIST,
		GET_WORK_ATTACHMENT,
		GET_WORK_ATTACHMENT_PART,
		UPLOAD_WORK,
		UPDATE_WORK,
		CANCEL_WORK,
		RECALL_WORK,
		REGISTER_WORK_ATTACHMENT,
		MOBILELOGIN,
		MOBILEUSERREG;
		
		public String toString() {
			
			switch(this) {
			case USERINFO:
				return "USERINFO";
			case CHANGE_PASSWORD:
				return "PASSWORD CHANGE";
			case RESULT_SEARCH:
				return "RESULT SEARCH";
			case GET_RESULT:
				return "GET RESULT";
			case GET_BINARY:
				return "GET BINARY";
			case GET_BINARY_PART:
				return "GET BINARY PART";
			case GET_NEXT_RESULT_WORKFLOW_STATUS:
				return "GET NEXT RESULT STATUS";
			case UPDATE_RESULT_WORKFLOW_STATUS:
				return "UPDATE RESULT STATUS";
			case UPDATE_RESULT_RESPONSE:
				return "UPDATE RESULT RESPONSE";
			case CREATE_RESULT_NOTE:
				return "CREATE RESULT NOTE";
			case GET_VIEW:
				return "GET VIEW";
			case GET_VALIDATION_LIST:
				return "GET VALIDATION LIST";
			case MOBILELOGIN:
			    return "MOBILE LOGIN";
			case MOBILEUSERREG:
			    return "MOBILE USER REG";

			default:
				return super.toString();
			}
			
		}
	};
	

	
/**
 * FDE044 - MC 
 * Record an activity log item based of an activity type enum
 * @param frInstance
 * @param activityType
 * @param userCode
 * @param deviceId
 * @param appCode
 * @param additionalText
 * @param transFileName
 * @throws FRInstanceException
 */
	public void recordActivityLog(String frInstance, ACTIVITYTYPE activityType, String userCode, String deviceId, String appCode, String additionalText, String transFileName)
			throws FRInstanceException;
		
	
	//FDE044 - MC remove previous two login functions

}
