
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("GetConfigResponse")
public class GetConfigResponse implements Serializable {
	
	private static final long serialVersionUID = -7433434032581686520L;
    public static final String APPLICATION_VND_FIELDSMART_CONFIG_1_JSON = "application/vnd.fieldsmart.config-1+json";
    public static final String APPLICATION_VND_FIELDSMART_CONFIG_1_XML = "application/vnd.fieldsmart.config-1+xml";
    
	private ErrorMessage error;
	
	@ApiModelProperty(notes = "base64 encoded file contents")
    private String configFileSource;
    
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
     * Get the 'configFileSource' element value.
     * 
     * @return value
     */
    public String getConfigFileSource() {
        return configFileSource;
    }

    /** 
     * Set the 'configFileSource' element value.
     * 
     * @param configFileSource
     */
    public void setConfigFileSource(String configFileSource) {
        this.configFileSource = configFileSource;
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
