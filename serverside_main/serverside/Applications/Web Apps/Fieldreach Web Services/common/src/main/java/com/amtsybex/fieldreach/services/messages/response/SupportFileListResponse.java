
package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.SupportFile;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import io.swagger.annotations.ApiModelProperty;

@XStreamAlias("SupportFileListResponse")
public class SupportFileListResponse implements Serializable {

	private static final long serialVersionUID = -2021492260039960493L;
    public static final String APPLICATION_VND_FIELDSMART_SUPPORT_FILELIST_1_JSON = "application/vnd.fieldsmart.support-filelist-1+json";
    public static final String APPLICATION_VND_FIELDSMART_SUPPORT_FILELIST_1_XML = "application/vnd.fieldsmart.support-filelist-1+xml";
    
    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;
    
    private ErrorMessage error;
    
    @JacksonXmlElementWrapper(localName = "supportFileList", useWrapping = true)
    @JacksonXmlProperty(localName = "SupportFile")
    private List<SupportFile> supportFileList = new ArrayList<SupportFile>();

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
     * Get the list of 'supportFileList' element items.
     * 
     * @return list
     */
    public List<SupportFile> getSupportFileList() {
        return supportFileList;
    }

    /** 
     * Set the list of 'supportFileList' element items.
     * 
     * @param list
     */
    public void setSupportFileList(List<SupportFile> list) {
        supportFileList = list;
    }
}
