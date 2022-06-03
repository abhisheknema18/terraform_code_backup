package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.FullDataBaseMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("ResultsHistoryDatabaseResponse")
public class ResultsHistoryDatabaseResponse implements Serializable {
	
	private static final long serialVersionUID = -6956656744174601310L;
    public static final String APPLICATION_VND_FIELDSMART_HISTORY_DB_1_JSON = "application/vnd.fieldsmart.history-db-1+json";
    public static final String APPLICATION_VND_FIELDSMART_HISTORY_DB_1_XML = "application/vnd.fieldsmart.history-db-1+xml";
    
	private ErrorMessage error;
    private FullDataBaseMessage full;

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
     * Get the 'full' element value.
     * 
     * @return value
     */
    public FullDataBaseMessage getFull() {
        return full;
    }

    /** 
     * Set the 'full' element value.
     * 
     * @param full
     */
    public void setFull(FullDataBaseMessage full) {
        this.full = full;
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
