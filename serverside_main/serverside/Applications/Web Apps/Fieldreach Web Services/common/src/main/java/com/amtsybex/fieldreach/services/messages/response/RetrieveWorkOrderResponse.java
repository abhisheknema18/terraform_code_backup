
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("RetrieveWorkOrderResponse")
public class RetrieveWorkOrderResponse implements Serializable {

	private static final long serialVersionUID = -710173623245215719L;
    public static final String APPLICATION_VND_FIELDSMART_RETRIEVE_WORKORDER_1_JSON = "application/vnd.fieldsmart.retrieve-workorder-1+json";
    public static final String APPLICATION_VND_FIELDSMART_RETRIEVE_WORKORDER_1_XML = "application/vnd.fieldsmart.retrieve-workorder-1+xml";
    
    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;
    
    private ErrorMessage error;
    private String workOrderData;
    private String checksum;
    private String filename;

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
     * Get the 'workOrderData' element value.
     * 
     * @return value
     */
    public String getWorkOrderData() {
        return workOrderData;
    }

    /** 
     * Set the 'workOrderData' element value.
     * 
     * @param workOrderData
     */
    public void setWorkOrderData(String workOrderData) {
        this.workOrderData = workOrderData;
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

    /** 
     * Get the 'filename' element value.
     * 
     * @return value
     */
    public String getFilename() {
        return filename;
    }

    /** 
     * Set the 'filename' element value.
     * 
     * @param filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
