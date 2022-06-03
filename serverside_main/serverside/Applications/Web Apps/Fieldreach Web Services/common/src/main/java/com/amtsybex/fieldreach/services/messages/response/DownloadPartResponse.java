
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("DownloadPartResponse")
public class DownloadPartResponse implements Serializable {

	private static final long serialVersionUID = -6714463914543227835L;
	public static final String APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON = "application/vnd.fieldsmart.download-part-1+json";
	public static final String APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML = "application/vnd.fieldsmart.download-part-1+xml";

    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
	private boolean success;
    private ErrorMessage error;

    @ApiModelProperty(notes = "Identifier returned by the web services when initialising the download")
    private String identifier;
    
    @ApiModelProperty(notes = "base64 encoded file part")
    private String partData;

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
     * Get the 'identifier' element value.
     * 
     * @return value
     */
    public String getIdentifier() {
        return identifier;
    }

    /** 
     * Set the 'identifier' element value.
     * 
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /** 
     * Get the 'partData' element value.
     * 
     * @return value
     */
    public String getPartData() {
        return partData;
    }

    /** 
     * Set the 'partData' element value.
     * 
     * @param partData
     */
    public void setPartData(String partData) {
        this.partData = partData;
    }
}
