/**
 * Author:  Clive Moore
 * Date:    08/02/2015
 * Project: FDP1183
 * 
 * Copyright AMT-Sybex 2016
 * 
 */
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("GetScriptReportResponse")
public class GetScriptReportResponse implements Serializable {

	private static final long serialVersionUID = -4811339056633597177L;
	
	private ErrorMessage error;
    private String scriptReportSource;

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
     * Get the 'scriptReportSource' element value.
     * 
     * @return value
     */
    public String getScriptReportSource() {
        return scriptReportSource;
    }

    /** 
     * Set the 'scriptFileSource' element value.
     * 
     * @param scriptFileSource
     */
	public void setScriptReportSource(String scriptReportSource) {
        this.scriptReportSource = scriptReportSource;
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
