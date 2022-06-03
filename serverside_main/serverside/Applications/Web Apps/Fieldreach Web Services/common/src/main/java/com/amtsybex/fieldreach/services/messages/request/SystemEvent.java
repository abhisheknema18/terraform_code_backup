package com.amtsybex.fieldreach.services.messages.request;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * FDE044 - MC - System Event Log
 * @author CroninM
 *
 */
@XStreamAlias("SystemEvent")
public class SystemEvent  implements Serializable{

	private static final long serialVersionUID = -8569663595923975370L;
    public static final String APPLICATION_VND_FIELDSMART_SYSTEMEVENT_1_JSON = "application/vnd.fieldsmart.system-event-1+json";
    public static final String APPLICATION_VND_FIELDSMART_SYSTEMEVENT_1_XML = "application/vnd.fieldsmart.system-event-1+xml";
	
	private int eventDate;
	private String eventTime;
	private String eventCategory;
	private String eventType;
	private String eventSummary;
	private String eventDesc;
	private String sourceSystem;
	private String application;
	private int severity;
	private String userCode;
	private String errorCode;
	private Long returnId;
	private String workOrderNo;
	private String districtCode;

	public int getEventDate() {
		return eventDate;
	}
	public void setEventDate(int eventDate) {
		this.eventDate = eventDate;
	}
	public String getEventTime() {
		return eventTime;
	}
	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}
	public String getEventCategory() {
		return eventCategory;
	}
	public void setEventCategory(String eventCategory) {
		this.eventCategory = eventCategory;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getEventSummary() {
		return eventSummary;
	}
	public void setEventSummary(String eventSummary) {
		this.eventSummary = eventSummary;
	}
	public String getEventDesc() {
		return eventDesc;
	}
	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}
	public String getSourceSystem() {
		return sourceSystem;
	}
	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public int getSeverity() {
		return severity;
	}
	public void setSeverity(int severity) {
		this.severity = severity;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public Long getReturnId() {
		return returnId;
	}
	public void setReturnId(Long returnId) {
		this.returnId = returnId;
	}
	public String getWorkOrderNo() {
		return workOrderNo;
	}
	public void setWorkOrderNo(String workOrderNo) {
		this.workOrderNo = workOrderNo;
	}
	public String getDistrictCode() {
		return districtCode;
	}
	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
