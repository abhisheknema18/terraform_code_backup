
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ScriptResult")
public class ScriptResult implements Serializable {

	private static final long serialVersionUID = -7964948760506075223L;
	
	private String returnId;
    private String scriptCode;
    private String deviceId;
    private String workorderNo;
    private String workorderDesc;
    private String equipNo;
    private String equipDesc;
    private String altEquipRef;
    private String summaryDesc;
    private String completeDate;
    private String completeTime;
    private String completeUser;
    private String completeUserName;
    private String completeCode;
    private String workgroupCode;
    private String resultStatus;
    private String totalWeightScore;
    private String resAssocCode;
    private String scriptDesc;
    private String workgroupDesc;

    /** 
     * Get the 'returnId' element value.
     * 
     * @return value
     */
    public String getReturnId() {
        return returnId;
    }

    /** 
     * Set the 'returnId' element value.
     * 
     * @param returnId
     */
    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    /** 
     * Get the 'scriptCode' element value.
     * 
     * @return value
     */
    public String getScriptCode() {
        return scriptCode;
    }

    /** 
     * Set the 'scriptCode' element value.
     * 
     * @param scriptCode
     */
    public void setScriptCode(String scriptCode) {
        this.scriptCode = scriptCode;
    }

    /** 
     * Get the 'deviceId' element value.
     * 
     * @return value
     */
    public String getDeviceId() {
        return deviceId;
    }

    /** 
     * Set the 'deviceId' element value.
     * 
     * @param deviceId
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /** 
     * Get the 'workorderNo' element value.
     * 
     * @return value
     */
    public String getWorkorderNo() {
        return workorderNo;
    }

    /** 
     * Set the 'workorderNo' element value.
     * 
     * @param workorderNo
     */
    public void setWorkorderNo(String workorderNo) {
        this.workorderNo = workorderNo;
    }

    /** 
     * Get the 'workorderDesc' element value.
     * 
     * @return value
     */
    public String getWorkorderDesc() {
        return workorderDesc;
    }

    /** 
     * Set the 'workorderDesc' element value.
     * 
     * @param workorderDesc
     */
    public void setWorkorderDesc(String workorderDesc) {
        this.workorderDesc = workorderDesc;
    }

    /** 
     * Get the 'equipNo' element value.
     * 
     * @return value
     */
    public String getEquipNo() {
        return equipNo;
    }

    /** 
     * Set the 'equipNo' element value.
     * 
     * @param equipNo
     */
    public void setEquipNo(String equipNo) {
        this.equipNo = equipNo;
    }

    /** 
     * Get the 'equipDesc' element value.
     * 
     * @return value
     */
    public String getEquipDesc() {
        return equipDesc;
    }

    /** 
     * Set the 'equipDesc' element value.
     * 
     * @param equipDesc
     */
    public void setEquipDesc(String equipDesc) {
        this.equipDesc = equipDesc;
    }

    /** 
     * Get the 'altEquipRef' element value.
     * 
     * @return value
     */
    public String getAltEquipRef() {
        return altEquipRef;
    }

    /** 
     * Set the 'altEquipRef' element value.
     * 
     * @param altEquipRef
     */
    public void setAltEquipRef(String altEquipRef) {
        this.altEquipRef = altEquipRef;
    }

    /** 
     * Get the 'summaryDesc' element value.
     * 
     * @return value
     */
    public String getSummaryDesc() {
        return summaryDesc;
    }

    /** 
     * Set the 'summaryDesc' element value.
     * 
     * @param summaryDesc
     */
    public void setSummaryDesc(String summaryDesc) {
        this.summaryDesc = summaryDesc;
    }

    /** 
     * Get the 'completeDate' element value.
     * 
     * @return value
     */
    public String getCompleteDate() {
        return completeDate;
    }

    /** 
     * Set the 'completeDate' element value.
     * 
     * @param completeDate
     */
    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }

    /** 
     * Get the 'completeTime' element value.
     * 
     * @return value
     */
    public String getCompleteTime() {
        return completeTime;
    }

    /** 
     * Set the 'completeTime' element value.
     * 
     * @param completeTime
     */
    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    /** 
     * Get the 'completeUser' element value.
     * 
     * @return value
     */
    public String getCompleteUser() {
        return completeUser;
    }

    /** 
     * Set the 'completeUser' element value.
     * 
     * @param completeUser
     */
    public void setCompleteUser(String completeUser) {
        this.completeUser = completeUser;
    }

    /** 
     * Get the 'completeUserName' element value.
     * 
     * @return value
     */
    public String getCompleteUserName() {
        return completeUserName;
    }

    /** 
     * Set the 'completeUserName' element value.
     * 
     * @param completeUserName
     */
    public void setCompleteUserName(String completeUserName) {
        this.completeUserName = completeUserName;
    }

    /** 
     * Get the 'completeCode' element value.
     * 
     * @return value
     */
    public String getCompleteCode() {
        return completeCode;
    }

    /** 
     * Set the 'completeCode' element value.
     * 
     * @param completeCode
     */
    public void setCompleteCode(String completeCode) {
        this.completeCode = completeCode;
    }

    /** 
     * Get the 'workgroupCode' element value.
     * 
     * @return value
     */
    public String getWorkgroupCode() {
        return workgroupCode;
    }

    /** 
     * Set the 'workgroupCode' element value.
     * 
     * @param workgroupCode
     */
    public void setWorkgroupCode(String workgroupCode) {
        this.workgroupCode = workgroupCode;
    }

    /** 
     * Get the 'resultStatus' element value.
     * 
     * @return value
     */
    public String getResultStatus() {
        return resultStatus;
    }

    /** 
     * Set the 'resultStatus' element value.
     * 
     * @param resultStatus
     */
    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    /** 
     * Get the 'totalWeightScore' element value.
     * 
     * @return value
     */
    public String getTotalWeightScore() {
        return totalWeightScore;
    }

    /** 
     * Set the 'totalWeightScore' element value.
     * 
     * @param totalWeightScore
     */
    public void setTotalWeightScore(String totalWeightScore) {
        this.totalWeightScore = totalWeightScore;
    }

    /** 
     * Get the 'resAssocCode' element value.
     * 
     * @return value
     */
    public String getResAssocCode() {
        return resAssocCode;
    }

    /** 
     * Set the 'resAssocCode' element value.
     * 
     * @param resAssocCode
     */
    public void setResAssocCode(String resAssocCode) {
        this.resAssocCode = resAssocCode;
    }
    
    /** 
     * Get the 'scriptDesc' element value.
     * 
     * @return value
     */
    public String getScriptDesc() {
        return scriptDesc;
    }

    /** 
     * Set the 'scriptDesc' element value.
     * 
     * @param scriptDesc
     */
    public void setScriptDesc(String scriptDesc) {
        this.scriptDesc = scriptDesc;
    }

    /** 
     * Get the 'workgroupDesc' element value.
     * 
     * @return value
     */
    public String getWorkgroupDesc() {
        return workgroupDesc;
    }

    /** 
     * Set the 'workgroupDesc' element value.
     * 
     * @param workgroupDesc
     */
    public void setWorkgroupDesc(String workgroupDesc) {
        this.workgroupDesc = workgroupDesc;
    }
    
}
