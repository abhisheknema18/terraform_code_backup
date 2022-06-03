package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("VALIDATION")
public class Validation {

	@XStreamAlias("VALIDATIONTYPE")
    @JacksonXmlProperty(localName = "VALIDATIONTYPE")
    protected String validationtype;
	
	@XStreamAlias("VALIDATIONDESC")
    @JacksonXmlProperty(localName = "VALIDATIONDESC")
    protected String validationdesc;
	
	@XStreamImplicit
    @JacksonXmlProperty(localName = "VALIDATIONPROPERTY")
    @JacksonXmlElementWrapper(localName = "VALIDATIONPROPERTY", useWrapping = false)
    protected List<ValidationProperty> validationproperty;

	
    public String getVALIDATIONTYPE() {
        return validationtype;
    }

    public void setVALIDATIONTYPE(String value) {
        this.validationtype = value;
    }

    public String getVALIDATIONDESC() {
        return validationdesc;
    }

    public void setVALIDATIONDESC(String value) {
        this.validationdesc = value;
    }

    public List<ValidationProperty> getVALIDATIONPROPERTY() {
        if (validationproperty == null) {
            validationproperty = new ArrayList<ValidationProperty>();
        }
        return this.validationproperty;
    }

}
