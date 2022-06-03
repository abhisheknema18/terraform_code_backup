
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;
import java.math.BigInteger;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("UploadInitiateResponse")
public class UploadInitiateResponse implements Serializable {

	private static final long serialVersionUID = 4195927289278845870L;
    public static final String APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_JSON = "application/vnd.fieldsmart.upload-initiate-1+json";
    public static final String APPLICATION_VND_FIELDSMART_UPLOAD_INITIATE_1_XML = "application/vnd.fieldsmart.upload-initiate-1+xml";
    
	private String identifier;
    private ErrorMessage error;
    private long maxChunkSizeBytes;
    private BigInteger startFromPart;

    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;

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
     * Get the 'maxChunkSizeBytes' element value.
     * 
     * @return value
     */
    public long getMaxChunkSizeBytes() {
        return maxChunkSizeBytes;
    }

    /** 
     * Set the 'maxChunkSizeBytes' element value.
     * 
     * @param maxChunkSizeBytes
     */
    public void setMaxChunkSizeBytes(long maxChunkSizeBytes) {
        this.maxChunkSizeBytes = maxChunkSizeBytes;
    }

    /** 
     * Get the 'startFromPart' element value.
     * 
     * @return value
     */
    public BigInteger getStartFromPart() {
        return startFromPart;
    }

    /** 
     * Set the 'startFromPart' element value.
     * 
     * @param startFromPart
     */
    public void setStartFromPart(BigInteger startFromPart) {
        this.startFromPart = startFromPart;
    }

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
}
