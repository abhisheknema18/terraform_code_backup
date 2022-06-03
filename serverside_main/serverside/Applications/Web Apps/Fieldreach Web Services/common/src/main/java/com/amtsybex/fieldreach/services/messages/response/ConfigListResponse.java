
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.amtsybex.fieldreach.services.messages.types.ConfigFile;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("ConfigListResponse")
public class ConfigListResponse implements Serializable {
	
	private static final long serialVersionUID = 7001353529552309869L;
    public static final String APPLICATION_VND_FIELDSMART_CONFIG_LIST_1_JSON = "application/vnd.fieldsmart.config-list-1+json";
    public static final String APPLICATION_VND_FIELDSMART_CONFIG_LIST_1_XML = "application/vnd.fieldsmart.config-list-1+xml";
    
    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;
    private ErrorMessage error;

    @JacksonXmlProperty(localName = "ConfigFile")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "configList")
    private List<ConfigFile> configList = new ArrayList<ConfigFile>();

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
     * Get the list of 'configList' element items.
     * 
     * @return list
     */
    public List<ConfigFile> getConfigList() {
        return configList;
    }

    /** 
     * Set the list of 'configList' element items.
     * 
     * @param list
     */
    public void setConfigList(List<ConfigFile> list) {
        configList = list;
    }
}
