package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("UNITOFMEASURE")
public class UnitOfMeasure {

	@XStreamAlias("UOMCATID")
    @JacksonXmlProperty(localName = "UOMCATID")
    protected int uomcatid;
    
	@XStreamAlias("UOMTYPE")
    @JacksonXmlProperty(localName = "UOMTYPE")
    protected String uomtype;
    
	@XStreamAlias("PREF")
    @JacksonXmlProperty(localName = "PREF")
    protected int pref;


    public int getUOMCATID() {
        return uomcatid;
    }

    public void setUOMCATID(int value) {
        this.uomcatid = value;
    }

    public String getUOMTYPE() {
        return uomtype;
    }

    public void setUOMTYPE(String value) {
        this.uomtype = value;
    }

    public int getPREF() {
        return pref;
    }

    public void setPREF(int value) {
        this.pref = value;
    }

}
