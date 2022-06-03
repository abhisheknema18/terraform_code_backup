
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("GetScriptResponse")
@ApiModel(value = "GetScriptResponse")
public class GetScriptResponse implements Serializable {
	
	private static final long serialVersionUID = 1850152571120874868L;
	
    public static final String APPLICATION_VND_FIELDSMART_SCRIPT_1_JSON = "application/vnd.fieldsmart.script-1+json";
    public static final String APPLICATION_VND_FIELDSMART_SCRIPT_1_XML = "application/vnd.fieldsmart.script-1+xml";
	
	private ErrorMessage error;
	
	@ApiModelProperty(notes = "base64 encode file contents", example = "PD94bWwgdFuZGFPSJ5ZXMiPz4NCjxkc1NbWxucz0i0cDovL3XB1cmkub3J")
    private String scriptFileSource;
	
    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;
	
    private String checksum;

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
     * Get the 'scriptFileSource' element value.
     * 
     * @return value
     */
    public String getScriptFileSource() {
        return scriptFileSource;
    }

    /** 
     * Set the 'scriptFileSource' element value.
     * 
     * @param scriptFileSource
     */
    public void setScriptFileSource(String scriptFileSource) {
        this.scriptFileSource = scriptFileSource;
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
}
