
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;
import java.math.BigInteger;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("UploadPartResponse")
public class UploadPartResponse implements Serializable {

	private static final long serialVersionUID = -8543077842306786127L;
    public static final String APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_JSON = "application/vnd.fieldsmart.upload-part-1+json";
    public static final String APPLICATION_VND_FIELDSMART_UPLOAD_PART_1_XML = "application/vnd.fieldsmart.upload-part-1+xml";
    
	private ErrorMessage error;

    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;
    
    private boolean complete;
    private BigInteger nextPart;

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
     * Get the 'complete' element value.
     * 
     * @return value
     */
    public boolean isComplete() {
        return complete;
    }

    /** 
     * Set the 'complete' element value.
     * 
     * @param complete
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    /** 
     * Get the 'nextPart' element value.
     * 
     * @return value
     */
    public BigInteger getNextPart() {
        return nextPart;
    }

    /** 
     * Set the 'nextPart' element value.
     * 
     * @param nextPart
     */
    public void setNextPart(BigInteger nextPart) {
        this.nextPart = nextPart;
    }
}
