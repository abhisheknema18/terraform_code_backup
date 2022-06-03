package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.DeltaResultList;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("ResultsHistoryDeltaResponse")
public class ResultsHistoryDeltaResponse implements Serializable {

	private static final long serialVersionUID = -5747342465915299721L;
    public static final String APPLICATION_VND_FIELDSMART_HISTORY_DELTA_1_JSON = "application/vnd.fieldsmart.history-delta-1+json";
    public static final String APPLICATION_VND_FIELDSMART_HISTORY_DELTA_1_XML = "application/vnd.fieldsmart.history-delta-1+xml";
    
    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;
    
    private ErrorMessage error;
    private DeltaResultList deltaResultList;

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



	public DeltaResultList getDeltaResultList() {
		return deltaResultList;
	}

	public void setDeltaResultList(DeltaResultList deltaResultList) {
		this.deltaResultList = deltaResultList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


}

