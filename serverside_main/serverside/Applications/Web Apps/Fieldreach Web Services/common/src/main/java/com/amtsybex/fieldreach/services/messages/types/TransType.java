
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

// FDP1255 TM 01/11/2016
// Changed from WOTransType to more generic TransType
@XStreamAlias("TransType")
public class TransType implements Serializable {

	// FDP1255 TM 01/11/2016
	// Added additional fields

	private static final long serialVersionUID = 3139414241882264244L;

	//FDP1266 - MC - 08/03/2017 - added type in here to avoid second parsing that was being done later.
	@XStreamAsAttribute
	private String type;
	 
	// WORKSTATUS
    @JacksonXmlProperty(localName = "WorkOrderNo")
    private String WorkOrderNo;
    @JacksonXmlProperty(localName = "DistrictCode")
    private String DistrictCode;
    @JacksonXmlProperty(localName = "WorkStatus")
    private String WorkStatus;
    @JacksonXmlProperty(localName = "AdditionalText")
    private String AdditionalText;
    
    // HEARTBEAT
    @JacksonXmlProperty(localName = "Latitude")
    private String Latitude;
    @JacksonXmlProperty(localName = "Longitude")
    private String Longitude;

    // End FDP1255

    /** 
     * Get the 'WorkOrderNo' element value.
     * 
     * @return value
     */
    public String getWorkOrderNo() {
        return WorkOrderNo;
    }

    /** 
     * Set the 'WorkOrderNo' element value.
     * 
     * @param workOrderNo
     */
    public void setWorkOrderNo(String workOrderNo) {
        this.WorkOrderNo = workOrderNo;
    }

    /** 
     * Get the 'DistrictCode' element value.
     * 
     * @return value
     */
    public String getDistrictCode() {
        return DistrictCode;
    }

    /** 
     * Set the 'DistrictCode' element value.
     * 
     * @param districtCode
     */
    public void setDistrictCode(String districtCode) {
        this.DistrictCode = districtCode;
    }

    /** 
     * Get the 'WorkStatus' element value.
     * 
     * @return value
     */
    public String getWorkStatus() {
        return WorkStatus;
    }

    /** 
     * Set the 'WorkStatus' element value.
     * 
     * @param workStatus
     */
    public void setWorkStatus(String workStatus) {
        this.WorkStatus = workStatus;
    }

    /** 
     * Get the 'AdditionalText' element value.
     * 
     * @return value
     */
    public String getAdditionalText() {
        return AdditionalText;
    }

    /** 
     * Set the 'AdditionalText' element value.
     * 
     * @param additionalText
     */
    public void setAdditionalText(String additionalText) {
        this.AdditionalText = additionalText;
    }

    /** 
     * Get the 'type' attribute value.
     * 
     * @return value
     */
    public String getType() {
        return type;
    }

    /** 
     * Set the 'type' attribute value.
     * 
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    // FDP1255 TM 01/11/2016
    
	public String getLatitude() {
		return Latitude;
	}

	public void setLatitude(String latitude) {
		this.Latitude = latitude;
	}

	public String getLongitude() {
		return Longitude;
	}

	public void setLongitude(String longitude) {
		this.Longitude = longitude;
	}
	
	// End FDP1255
}
