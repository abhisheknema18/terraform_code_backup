package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("VALIDATIONPROPERTY")
public class ValidationProperty {

	@XStreamAlias("VALIDATIONTYPE")
    @JacksonXmlProperty(localName = "VALIDATIONTYPE")
    protected String validationtype;
    
	@XStreamAlias("VALIDATIONPROPERTY")
    @JacksonXmlProperty(localName = "VALIDATIONPROPERTY")
    protected String validationproperty;
    
	@XStreamAlias("EQUIVVALUE")
    @JacksonXmlProperty(localName = "EQUIVVALUE")
    protected String equivvalue;
    
	@XStreamAlias("WEIGHTSCORE")
    @JacksonXmlProperty(localName = "WEIGHTSCORE")
    protected Integer weightscore;
    
	@XStreamAlias("COLOUR")
    @JacksonXmlProperty(localName = "COLOUR")
    protected String colour;


    public String getVALIDATIONTYPE() {
        return validationtype;
    }

    public void setVALIDATIONTYPE(String value) {
        this.validationtype = value;
    }

    public String getVALIDATIONPROPERTY() {
        return validationproperty;
    }

    public void setVALIDATIONPROPERTY(String value) {
        this.validationproperty = value;
    }

    public String getEQUIVVALUE() {
        return equivvalue;
    }

    public void setEQUIVVALUE(String value) {
        this.equivvalue = value;
    }

    public Integer getWEIGHTSCORE() {
        return weightscore;
    }

    public void setWEIGHTSCORE(Integer value) {
        this.weightscore = value;
    }

    public String getCOLOUR() {
        return colour;
    }

    public void setCOLOUR(String value) {
        this.colour = value;
    }

}
