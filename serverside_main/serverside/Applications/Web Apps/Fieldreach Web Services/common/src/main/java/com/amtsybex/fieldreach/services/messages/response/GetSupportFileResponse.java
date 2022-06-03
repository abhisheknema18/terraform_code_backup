
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("GetSupportFileResponse")
public class GetSupportFileResponse implements Serializable {

	private static final long serialVersionUID = -2417456146738433785L;
	
	private ErrorMessage error;
    private String supportFileSource;

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
     * Get the 'supportFileSource' element value.
     * 
     * @return value
     */
    public String getSupportFileSource() {
        return supportFileSource;
    }

    /** 
     * Set the 'supportFileSource' element value.
     * 
     * @param supportFileSource
     */
    public void setSupportFileSource(String supportFileSource) {
        this.supportFileSource = supportFileSource;
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
