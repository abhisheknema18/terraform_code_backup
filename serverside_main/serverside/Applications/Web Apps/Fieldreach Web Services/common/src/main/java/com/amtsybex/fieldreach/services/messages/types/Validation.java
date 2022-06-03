
package com.amtsybex.fieldreach.services.messages.types;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Validation implements Serializable {

	private static final long serialVersionUID = 5514148453904093151L;
	
	@JacksonXmlElementWrapper(localName = "validationProperties", useWrapping = true)
    @JacksonXmlProperty(localName = "validationProperty")
	private List<ValidationProperty> validationProperties = new ArrayList<ValidationProperty>();

    private String validationType;
    
    private String validationDesc;

    /** 
     * Get the list of 'validationProperties' element items.
     * 
     * @return list
     */
    public List<ValidationProperty> getValidationProperties() {
        return validationProperties;
    }

    /** 
     * Set the list of 'validationProperties' element items.
     * 
     * @param list
     */
    public void setValidationProperties(List<ValidationProperty> list) {
        validationProperties = list;
    }

    /** 
     * Get the 'validationType' element value.
     * 
     * @return value
     */
    public String getValidationType() {
        return validationType;
    }

    /** 
     * Set the 'validationType' element value.
     * 
     * @param validationType
     */
    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    /** 
     * Get the 'validationDesc' element value.
     * 
     * @return value
     */
    public String getValidationDesc() {
        return validationDesc;
    }

    /** 
     * Set the 'validationDesc' element value.
     * 
     * @param validationDesc
     */
    public void setValidationDesc(String validationDesc) {
        this.validationDesc = validationDesc;
    }
}
