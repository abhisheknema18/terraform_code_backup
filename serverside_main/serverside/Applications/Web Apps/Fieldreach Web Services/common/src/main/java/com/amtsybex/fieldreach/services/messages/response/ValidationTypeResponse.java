
package com.amtsybex.fieldreach.services.messages.response;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.Validation;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ValidationTypeResponse implements Serializable {

	private static final long serialVersionUID = 5300413416510797077L;
    public static final String APPLICATION_VND_FIELDSMART_VALIDATION_TYPE_1_JSON = "application/vnd.fieldsmart.validation-type-1+json";
    public static final String APPLICATION_VND_FIELDSMART_VALIDATION_TYPE_1_XML = "application/vnd.fieldsmart.validation-type-1+xml";

    private ErrorMessage error;
    
    @JacksonXmlElementWrapper(localName = "validations", useWrapping = true)
    @JacksonXmlProperty(localName = "validation")
    private List<Validation> validations = new ArrayList<Validation>();

    @ApiModelProperty(notes = "Call is either Successful (true) or Unsuccessful (false)")
    private boolean success;

    @ApiModelProperty(notes = "If the request checksum matches the current checksum")
    private int checksumMatch;

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
     * Get the list of 'validations' element items.
     * 
     * @return list
     */
    public List<Validation> getValidations() {
        return validations;
    }

    /** 
     * Set the list of 'validations' element items.
     * 
     * @param list
     */
    public void setValidations(List<Validation> list) {
        validations = list;
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
     * Get the 'checksumMatch' element value.
     * 
     * @return value
     */
    public int getChecksumMatch() {
        return checksumMatch;
    }

    /** 
     * Set the 'checksumMatch' element value.
     * 
     * @param checksumMatch
     */
    public void setChecksumMatch(int checksumMatch) {
        this.checksumMatch = checksumMatch;
    }
}
