
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.Status;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("NextResultStatusResponse")
public class NextResultStatusResponse implements Serializable {

	private static final long serialVersionUID = 146702065584431014L;
	
    public static final String APPLICATION_VND_FIELDSMART_NEXT_STATUS_1_JSON = "application/vnd.fieldsmart.next-status-1+json";
    public static final String APPLICATION_VND_FIELDSMART_NEXT_STATUS_1_XML = "application/vnd.fieldsmart.next-status-1+xml";
    
    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;
    
    private ErrorMessage error;
    
    @JacksonXmlProperty(localName = "Status")
    @JacksonXmlElementWrapper(localName = "resultStatusList", useWrapping = true)
    private List<Status> resultStatusList = new ArrayList<Status>();

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
     * Get the list of 'resultStatusList' element items.
     * 
     * @return list
     */
    public List<Status> getResultStatusList() {
        return resultStatusList;
    }

    /** 
     * Set the list of 'resultStatusList' element items.
     * 
     * @param list
     */
    public void setResultStatusList(List<Status> list) {
    	resultStatusList = list;
    }
}
