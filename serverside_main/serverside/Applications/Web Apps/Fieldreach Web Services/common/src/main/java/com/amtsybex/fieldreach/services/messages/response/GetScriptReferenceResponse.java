
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("GetScriptReferenceResponse")
public class GetScriptReferenceResponse implements Serializable {

	private static final long serialVersionUID = 5526010492552407555L;

    public static final String APPLICATION_VND_FIELDSMART_SCRIPT_REF_1_JSON = "application/vnd.fieldsmart.script-reference-1+json";
    public static final String APPLICATION_VND_FIELDSMART_SCRIPT_REF_1_XML = "application/vnd.fieldsmart.script-reference-1+xml";
    
	private ErrorMessage error;
	
	@ApiModelProperty(notes = "base64 encoded file contents")
    private String referenceFileSource;

    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;

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
     * Get the 'referenceFileSource' element value.
     * 
     * @return value
     */
    public String getReferenceFileSource() {
        return referenceFileSource;
    }

    /** 
     * Set the 'referenceFileSource' element value.
     * 
     * @param referenceFileSource
     */
    public void setReferenceFileSource(String referenceFileSource) {
        this.referenceFileSource = referenceFileSource;
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
