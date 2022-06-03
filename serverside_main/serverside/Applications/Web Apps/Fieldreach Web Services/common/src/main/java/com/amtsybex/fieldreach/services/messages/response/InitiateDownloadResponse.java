
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;
import java.math.BigInteger;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("InitiateDownloadResponse")
public class InitiateDownloadResponse implements Serializable {
	
	private static final long serialVersionUID = -7177379333581513475L;

    public static final String APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_JSON = "application/vnd.fieldsmart.initiate-download-1+json";
    public static final String APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_XML = "application/vnd.fieldsmart.initiate-download-1+xml";

    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
	private boolean success;
    private ErrorMessage error;
    private String identifier;
    private BigInteger totalParts;
    private String checksum;
    private String fileName;

    /** 
     * Get the 'success' element value.
     * 
     * @return value
     */
    public boolean isSuccess() {
        return success;
    }

    /** 
     * Set the 'success' element value.
     * 
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /** 
     * Get the 'error' element value.
     * 
     * @return value
     */
    public ErrorMessage getError() {
        return error;
    }

    /** 
     * Set the 'error' element value.
     * 
     * @param error
     */
    public void setError(ErrorMessage error) {
        this.error = error;
    }

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
     * Get the 'totalParts' element value.
     * 
     * @return value
     */
    public BigInteger getTotalParts() {
        return totalParts;
    }

    /** 
     * Set the 'totalParts' element value.
     * 
     * @param totalParts
     */
    public void setTotalParts(BigInteger totalParts) {
        this.totalParts = totalParts;
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

    /** 
     * Get the 'fileName' element value.
     * 
     * @return value
     */
    public String getFileName() {
        return fileName;
    }

    /** 
     * Set the 'fileName' element value.
     * 
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
