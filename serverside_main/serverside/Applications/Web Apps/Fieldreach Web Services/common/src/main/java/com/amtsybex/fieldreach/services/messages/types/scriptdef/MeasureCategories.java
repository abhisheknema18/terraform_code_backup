package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("MEASURECATEGORIES")
public class MeasureCategories {

	@XStreamAlias("UOMCATID")
    @JacksonXmlProperty(localName = "UOMCATID")
    protected int uomcatid;
    
	@XStreamAlias("CATNAME")
    @JacksonXmlProperty(localName = "CATNAME")
    protected String catname;
    
	@XStreamImplicit
    @JacksonXmlProperty(localName = "UNITOFMEASURE")
    @JacksonXmlElementWrapper(localName = "UNITOFMEASURE", useWrapping = false)
    protected List<UnitOfMeasure> unitofmeasure;

    public int getUOMCATID() {
        return uomcatid;
    }

    public void setUOMCATID(int value) {
        this.uomcatid = value;
    }

    public String getCATNAME() {
        return catname;
    }

    public void setCATNAME(String value) {
        this.catname = value;
    }

    public List<UnitOfMeasure> getUNITOFMEASURE() {
        if (unitofmeasure == null) {
            unitofmeasure = new ArrayList<UnitOfMeasure>();
        }
        return this.unitofmeasure;
    }

}
