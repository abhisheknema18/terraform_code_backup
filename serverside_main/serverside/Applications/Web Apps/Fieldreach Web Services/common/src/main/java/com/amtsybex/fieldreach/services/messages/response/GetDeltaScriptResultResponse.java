package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("GetDeltaScriptResultResponse")
public class GetDeltaScriptResultResponse   implements Serializable {
	
	private static final long serialVersionUID = -1661188199156640149L;

	public static final String APPLICATION_VND_FIELDSMART_DELTA_SCRIPT_1_JSON = "application/vnd.fieldsmart.delta-script-1+json";
	public static final String APPLICATION_VND_FIELDSMART_DELTA_SCRIPT_1_XML = "application/vnd.fieldsmart.delta-script-1+xml";
	
	@ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
	private boolean success;
    
	private ErrorMessage error;
    
	@ApiModelProperty(notes = "base 64 encoded file object")
    private String scriptResultSource;
	
	@ApiModelProperty(notes = "file checksum value", example = "C904CBAD3AA667AC3A1DCB0936B1606A")
    private String checksum;
	
	@ApiModelProperty(notes = "result extract file name", example = "RS.368859.000000100001.RC-04.20180221.XML")
    private String fileName;

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
     * 
     * @return value
     */
	public String getChecksum() {
		return checksum;
	}

	/**
	 * set the checkSumm element value
	 * 
	 * @param checksum
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	/**
	 * 
	 * @return value
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 
	 * @return value
	 */
	public String getScriptResultSource() {
		return scriptResultSource;
	}

	/**
	 * set the scriptResultSource element value
	 * 
	 * @param scriptResultSource
	 */
	public void setScriptResultSource(String scriptResultSource) {
		this.scriptResultSource = scriptResultSource;
	}

	/**
	 * set the fileName element value
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


}

