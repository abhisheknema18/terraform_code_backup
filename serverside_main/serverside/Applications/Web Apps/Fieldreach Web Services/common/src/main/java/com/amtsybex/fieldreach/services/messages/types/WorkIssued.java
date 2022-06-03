
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("WorkIssued")
public class WorkIssued implements Serializable {

	private static final long serialVersionUID = 1683696872819833208L;
	
	private String workOrderNo;
    private String workOrderDesc;
    private String issuedTime;
    private String equipNo;
    private String equipDesc;
    private String altEquipRef;
    private Integer planStartDate;
    private Integer reqFinishDate;
    private String userCode;
    private String woType;
    private String sourceFileName;
    private String districtCode;
    private String workStatus;
    private Integer workStatusDate;
    private String workStatusTime;
    private String additionalText;
    private int issuedDate;
    private String workgroupCode;
    private Integer fileCreateDate;
    private String fileCreateTime;
    
    @JacksonXmlElementWrapper(localName = "attachments", useWrapping = true)
    @JacksonXmlProperty(localName = "Attachment")
    private List<Attachment> attachments = new ArrayList<Attachment>();
    
    /** 
     * Get the 'workOrderNo' element value.
     * 
     * @return value
     */
    public String getWorkOrderNo() {
        return workOrderNo;
    }

    /** 
     * Set the 'workOrderNo' element value.
     * 
     * @param workOrderNo
     */
    public void setWorkOrderNo(String workOrderNo) {
        this.workOrderNo = workOrderNo;
    }

    /** 
     * Get the 'workOrderDesc' element value.
     * 
     * @return value
     */
    public String getWorkOrderDesc() {
        return workOrderDesc;
    }

    /** 
     * Set the 'workOrderDesc' element value.
     * 
     * @param workOrderDesc
     */
    public void setWorkOrderDesc(String workOrderDesc) {
        this.workOrderDesc = workOrderDesc;
    }

    /** 
     * Get the 'issuedTime' element value.
     * 
     * @return value
     */
    public String getIssuedTime() {
        return issuedTime;
    }

    /** 
     * Set the 'issuedTime' element value.
     * 
     * @param issuedTime
     */
    public void setIssuedTime(String issuedTime) {
        this.issuedTime = issuedTime;
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
     * Get the 'planStartDate' element value.
     * 
     * @return value
     */
    public Integer getPlanStartDate() {
        return planStartDate;
    }

    /** 
     * Set the 'planStartDate' element value.
     * 
     * @param planStartDate
     */
    public void setPlanStartDate(Integer planStartDate) {
        this.planStartDate = planStartDate;
    }

    /** 
     * Get the 'reqFinishDate' element value.
     * 
     * @return value
     */
    public Integer getReqFinishDate() {
        return reqFinishDate;
    }

    /** 
     * Set the 'reqFinishDate' element value.
     * 
     * @param reqFinishDate
     */
    public void setReqFinishDate(Integer reqFinishDate) {
        this.reqFinishDate = reqFinishDate;
    }

    /** 
     * Get the 'userCode' element value.
     * 
     * @return value
     */
    public String getUserCode() {
        return userCode;
    }

    /** 
     * Set the 'userCode' element value.
     * 
     * @param userCode
     */
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    /** 
     * Get the 'woType' element value.
     * 
     * @return value
     */
    public String getWoType() {
        return woType;
    }

    /** 
     * Set the 'woType' element value.
     * 
     * @param woType
     */
    public void setWoType(String woType) {
        this.woType = woType;
    }

    /** 
     * Get the 'sourceFileName' element value.
     * 
     * @return value
     */
    public String getSourceFileName() {
        return sourceFileName;
    }

    /** 
     * Set the 'sourceFileName' element value.
     * 
     * @param sourceFileName
     */
    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    /** 
     * Get the 'districtCode' element value.
     * 
     * @return value
     */
    public String getDistrictCode() {
        return districtCode;
    }

    /** 
     * Set the 'districtCode' element value.
     * 
     * @param districtCode
     */
    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    /** 
     * Get the 'workStatus' element value.
     * 
     * @return value
     */
    public String getWorkStatus() {
        return workStatus;
    }

    /** 
     * Set the 'workStatus' element value.
     * 
     * @param workStatus
     */
    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    /** 
     * Get the 'workStatusDate' element value.
     * 
     * @return value
     */
    public Integer getWorkStatusDate() {
        return workStatusDate;
    }

    /** 
     * Set the 'workStatusDate' element value.
     * 
     * @param workStatusDate
     */
    public void setWorkStatusDate(Integer workStatusDate) {
        this.workStatusDate = workStatusDate;
    }

    /** 
     * Get the 'workStatusTime' element value.
     * 
     * @return value
     */
    public String getWorkStatusTime() {
        return workStatusTime;
    }

    /** 
     * Set the 'workStatusTime' element value.
     * 
     * @param workStatusTime
     */
    public void setWorkStatusTime(String workStatusTime) {
        this.workStatusTime = workStatusTime;
    }

    /** 
     * Get the 'additionalText' element value.
     * 
     * @return value
     */
    public String getAdditionalText() {
        return additionalText;
    }

    /** 
     * Set the 'additionalText' element value.
     * 
     * @param additionalText
     */
    public void setAdditionalText(String additionalText) {
        this.additionalText = additionalText;
    }

    /** 
     * Get the 'issuedDate' element value.
     * 
     * @return value
     */
    public int getIssuedDate() {
        return issuedDate;
    }

    /** 
     * Set the 'issuedDate' element value.
     * 
     * @param issuedDate
     */
    public void setIssuedDate(int issuedDate) {
        this.issuedDate = issuedDate;
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
     * Get the 'fileCreateDate' element value.
     * 
     * @return value
     */
    public Integer getFileCreateDate() {
        return fileCreateDate;
    }

    /** 
     * Set the 'fileCreateDate' element value.
     * 
     * @param fileCreateDate
     */
    public void setFileCreateDate(Integer fileCreateDate) {
        this.fileCreateDate = fileCreateDate;
    }
    
    /** 
     * Get the 'fileCreateTime' element value.
     * 
     * @return value
     */
    public String getFileCreateTime() {
        return fileCreateTime;
    }

    /** 
     * Set the 'fileCreateTime' element value.
     * 
     * @param fileCreateTime
     */
    public void setFileCreateTime(String fileCreateTime) {
        this.fileCreateTime = fileCreateTime;
    }

	public List<Attachment> getAttachments() {
		
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		
		this.attachments = attachments;
	}
	
}
