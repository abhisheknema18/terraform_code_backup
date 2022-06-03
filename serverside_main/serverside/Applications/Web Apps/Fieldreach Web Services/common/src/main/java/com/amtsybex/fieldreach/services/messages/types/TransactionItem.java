
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;
import java.math.BigInteger;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("TransactionItem")
public class TransactionItem implements Serializable {

	private static final long serialVersionUID = 9217869508838522012L;
	
	@JacksonXmlProperty(localName = "UserCode")
	private String UserCode;

    @JacksonXmlProperty(localName = "LogDate")
    private BigInteger LogDate; // TM 27/11/2012 FDE019 TTR Change to BigInteger to prevent unmarshalling error when integers begin with 0

    @JacksonXmlProperty(localName = "LogTime")
    private String LogTime; //FDP1316 - MC - should be string

    @JacksonXmlProperty(localName = "DeviceId")
    private String DeviceId;
    
    @JacksonXmlProperty(localName = "Trans")
    private TransType Trans; // FDP1255 TM 01/11/2016

    /** 
     * Get the 'UserCode' element value.
     * 
     * @return value
     */
    public String getUserCode() {
        return UserCode;
    }

    /** 
     * Set the 'UserCode' element value.
     * 
     * @param userCode
     */
    public void setUserCode(String userCode) {
        this.UserCode = userCode;
    }

    /** 
     * Get the 'LogDate' element value.
     * 
     * @return value
     */
    public BigInteger getLogDate() {
        return LogDate;
    }

    /** 
     * Set the 'LogDate' element value.
     * 
     * @param logDate
     */
    public void setLogDate(BigInteger logDate) {
        this.LogDate = logDate;
    }

    /** 
     * Get the 'LogTime' element value.
     * 
     * @return value
     */
    public String getLogTime() {
        return LogTime;
    }

    /** 
     * Set the 'LogTime' element value.
     * 
     * @param logTime
     */
    public void setLogTime(String logTime) {
        this.LogTime = logTime;
    }

    /** 
     * Get the 'DeviceId' element value.
     * 
     * @return value
     */
    public String getDeviceId() {
        return DeviceId;
    }

    /** 
     * Set the 'DeviceId' element value.
     * 
     * @param deviceId
     */
    public void setDeviceId(String deviceId) {
        this.DeviceId = deviceId;
    }

    /** 
     * Get the 'Trans' element value.
     * 
     * @return value
     */
    public TransType getTrans() {
        return Trans;
    }

    /** 
     * Set the 'Trans' element value.
     * 
     * @param trans
     */
    public void setTrans(TransType trans) {
        this.Trans = trans;
    }
}
