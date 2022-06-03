
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.WorkIssued;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("WorkIssuedResponse")
public class WorkIssuedResponse implements Serializable {

	private static final long serialVersionUID = 4091762615169865310L;
    public static final String APPLICATION_VND_FIELDSMART_WORK_ISSUED_1_JSON = "application/vnd.fieldsmart.work-issued-1+json";
    public static final String APPLICATION_VND_FIELDSMART_WORK_ISSUED_1_XML = "application/vnd.fieldsmart.work-issued-1+xml";

    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;
    
    private ErrorMessage error;

    @JacksonXmlElementWrapper(useWrapping = true, localName = "workIssuedList")
    @JacksonXmlProperty(localName = "WorkIssued")
    private List<WorkIssued> workIssuedList = new ArrayList<WorkIssued>();

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
     * Get the list of 'workIssuedList' element items.
     * 
     * @return list
     */
    public List<WorkIssued> getWorkIssuedList() {
        return workIssuedList;
    }

    /** 
     * Set the list of 'workIssuedList' element items.
     * 
     * @param list
     */
    public void setWorkIssuedList(List<WorkIssued> list) {
        workIssuedList = list;
    }
}
