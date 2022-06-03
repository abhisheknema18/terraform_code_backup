package com.amtsybex.fieldreach.fdm;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.SystemActivityLog;
import com.amtsybex.fieldreach.backend.service.ActivityLogService;

/**
 * FDE056 - log system activity
 * @author CroninM
 *
 */
@Named
public class SystemActivityLogger {

	@Inject
	transient private ActivityLogService activityLogger;
	
	static Logger _logger = LoggerFactory.getLogger(SystemActivityLogger.class.getName());
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
	
	public static enum ACTIVITYTYPE {
		LOGON,
		LOGON_FAILURE,
		CHANGE_PASSWORD,
		LOGOUT,
		RESULT_SEARCH,
		RESULT_SEARCH_DETAIL,
		PRINT_RESULT_WORD,
		PRINT_RESULT_PDF,
		WORK_SEARCH,
		WORK_SEARCH_DETAIL,
	  SYSTEM_USER_REG_FAIL,
	  SYSTEM_USER_REG;
		
		public String toString() {
			// there is a 20 byte limit on this value in the db
			switch(this) {
			case LOGON:
				return "LOGON";
			case LOGON_FAILURE:
				return "LOGON FAILURE";
			case CHANGE_PASSWORD:
				return "PASSWORD CHANGE";
			case LOGOUT:
				return "LOGOUT";
			case RESULT_SEARCH:
				return "SEARCH RESULT";
			case RESULT_SEARCH_DETAIL:
				return "VIEW RESULT SET";
			case PRINT_RESULT_WORD:
				return "SCRIPT RES DOC:DOCX";
			case PRINT_RESULT_PDF:
				return "SCRIPT RES DOC:PDF";
			case WORK_SEARCH:
				return "SEARCH WORK";
			case WORK_SEARCH_DETAIL:
				return "VIEW WORK DETAIL";
			case SYSTEM_USER_REG_FAIL:
			  return "SYSTEM USER REG FAIL";
			case SYSTEM_USER_REG:
			  return "SYSTEM USER REG";

			default:
				return super.toString();
			}
			
		}
	};
	
	public static String getRemoteAddress() {
		
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		
	    String ipAddress = request.getHeader("X-FORWARDED-FOR");
	    if (ipAddress != null) {
	        ipAddress = ipAddress.replaceFirst(",.*", "");
	    } else {
	        ipAddress = request.getRemoteHost();
	    }
	    return ipAddress;
	}
	
	public void recordActivityLog(String frInstance, ACTIVITYTYPE activityType, String userCode, String additionalText, String transFileName) {
	
		try {
			Date dateNow = new Date();
			SystemActivityLog logEntry = new SystemActivityLog(userCode, Integer.valueOf(dateFormat.format(dateNow)), timeFormat.format(dateNow) );
			
			logEntry.setDeviceId(getRemoteAddress());
			logEntry.setTransFileName(transFileName);
			logEntry.setAppCode("WEBP");
			logEntry.setActivityType(activityType.toString());
			logEntry.setAdditionalText(additionalText);
			
			activityLogger.saveSystemActivityLogEntry(frInstance, logEntry);
			
		} catch (FRInstanceException e) {
			_logger.error("FRInstance Exception Logging " + activityType.toString() + e.getMessage());
		} catch (Exception e) {
			_logger.error("Unknown Exception Logging Login " + activityType.toString() + e.getMessage());
		}


	}
	
	
}
