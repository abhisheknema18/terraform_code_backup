
package com.amtsybex.fieldreach.services.messages.request;

import java.io.Serializable;
import java.math.BigInteger;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("UploadPart")
public class UploadPart implements Serializable {
	
	private static final long serialVersionUID = -3248828431220612426L;
	
	private String identifier;
    private BigInteger partNumber;
    private String data;
    private long dataLength;
    private String checksum;

    /** 
     * Get the 'identifier' element value.
     * 
     * @return value
     */
    public String getIdentifier() {
        return identifier;
    }

    /** 
     * Set the 'identifier' element value.
     * 
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /** 
     * Get the 'partNumber' element value.
     * 
     * @return value
     */
    public BigInteger getPartNumber() {
        return partNumber;
    }

    /** 
     * Set the 'partNumber' element value.
     * 
     * @param partNumber
     */
    public void setPartNumber(BigInteger partNumber) {
        this.partNumber = partNumber;
    }

    /** 
     * Get the 'data' element value.
     * 
     * @return value
     */
    public String getData() {
        return data;
    }

    /** 
     * Set the 'data' element value.
     * 
     * @param data
     */
    public void setData(String data) {
        this.data = data;
    }

    /** 
     * Get the 'dataLength' element value.
     * 
     * @return value
     */
    public long getDataLength() {
        return dataLength;
    }

    /** 
     * Set the 'dataLength' element value.
     * 
     * @param dataLength
     */
    public void setDataLength(long dataLength) {
        this.dataLength = dataLength;
    }

    /** 
     * Get the 'checksum' element value.
     * 
     * @return value
     */
    public String getChecksum() {
        return checksum;
    }

    /** 
     * Set the 'checksum' element value.
     * 
     * @param checksum
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
