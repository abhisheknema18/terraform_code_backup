
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("CallResponse")
@ApiModel
public class CallResponse implements Serializable {
	
	private static final long serialVersionUID = 7039937942914282153L;
    public static final String APPLICATION_VND_FIELDSMART_CALL_1_JSON = "application/vnd.fieldsmart.call-response-1+json";
    public static final String APPLICATION_VND_FIELDSMART_CALL_1_XML = "application/vnd.fieldsmart.call-response-1+xml";
    
	private ErrorMessage error;

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
