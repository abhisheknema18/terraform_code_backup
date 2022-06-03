package com.amtsybex.fieldreach.fdm.search;

import java.io.Serializable;

import com.amtsybex.fieldreach.backend.model.ReturnedScripts;

/**
 * bean for displaying search results
 */
public class SearchResult implements Serializable{

	private static final long serialVersionUID = 6319596684884910437L;
	
	private Integer id;
	private ReturnedScripts returnedScript;
	private String equipDesc;
	private Integer completeDate;
	private Integer completeTime;
	private String workOrderNo;
	private String workOrderDesc;
	private String altEquipRef;
	private String summaryDesc;
	private String completeUser;
	private String resultStatus;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public ReturnedScripts getReturnedScript() {
		return returnedScript;
	}
	public void setReturnedScript(ReturnedScripts returnedScript) {
		this.returnedScript = returnedScript;
	}
	public String getEquipDesc() {
		return equipDesc;
	}
	public void setEquipDesc(String equipDesc) {
		this.equipDesc = equipDesc;
	}
	public Integer getCompleteDate() {
		return completeDate;
	}
	public void setCompleteDate(Integer completeDate) {
		this.completeDate = completeDate;
	}
	public String getWorkOrderNo() {
		return workOrderNo;
	}
	public void setWorkOrderNo(String workOrderNo) {
		this.workOrderNo = workOrderNo;
	}
	public String getWorkOrderDesc() {
		return workOrderDesc;
	}
	public void setWorkOrderDesc(String workOrderDesc) {
		this.workOrderDesc = workOrderDesc;
	}
	public String getAltEquipRef() {
		return altEquipRef;
	}
	public void setAltEquipRef(String altEquipRef) {
		this.altEquipRef = altEquipRef;
	}
	public String getSummaryDesc() {
		return summaryDesc;
	}
	public void setSummaryDesc(String summaryDesc) {
		this.summaryDesc = summaryDesc;
	}
	public String getCompleteUser() {
		return completeUser;
	}
	public void setCompleteUser(String completeUser) {
		this.completeUser = completeUser;
	}
	public String getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	public Integer getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(Integer completeTime) {
		this.completeTime = completeTime;
	}
}
