
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("GetScriptResultResponse")
public class GetScriptResultResponse implements Serializable {

	private static final long serialVersionUID = 5200682653374567864L;
	
    public static final String APPLICATION_VND_FIELDSMART_SCRIPT_RESULT_1_JSON = "application/vnd.fieldsmart.script-result-1+json";
    public static final String APPLICATION_VND_FIELDSMART_SCRIPT_RESULT_1_XML = "application/vnd.fieldsmart.script-result-1+xml";
    
	private ErrorMessage error;
	
	@ApiModelProperty(notes = "Script Result Source (Base64 encoded)")
    private String scriptResultSource;

    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;
    
    @ApiModelProperty(notes = "file checksum, can be used to check if file has been updated.")
    private String checksum;
    
    @ApiModelProperty(notes = "true if the user has access to edit the script result")
    private boolean edit;

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
     * Get the 'scriptResultSource' element value.
     * 
     * @return value
     */
    public String getScriptResultSource() {
        return scriptResultSource;
    }

    /** 
     * Set the 'scriptResultSource' element value.
     * 
     * @param scriptResultSource
     */
    public void setScriptResultSource(String scriptResultSource) {
        this.scriptResultSource = scriptResultSource;
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

	public boolean getEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}
}
