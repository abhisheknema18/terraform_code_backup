package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("DEFECTS")
public class Defects {

	@XStreamAlias("DEFECTCODE")
    @JacksonXmlProperty(localName = "DEFECTCODE")
    protected String defectcode;
    
	@XStreamAlias("DEFECTDESCRIPTION")
    @JacksonXmlProperty(localName = "DEFECTDESCRIPTION")
    protected String defectdescription;
    
	@XStreamImplicit
    @JacksonXmlElementWrapper(localName = "DEFECTNOTE", useWrapping = false)
    @JacksonXmlProperty(localName = "DEFECTNOTE")
    protected List<DefectNote> defectnote;

    public String getDEFECTCODE() {
        return defectcode;
    }

    public void setDEFECTCODE(String value) {
        this.defectcode = value;
    }

    public String getDEFECTDESCRIPTION() {
        return defectdescription;
    }

    public void setDEFECTDESCRIPTION(String value) {
        this.defectdescription = value;
    }

    public List<DefectNote> getDEFECTNOTE() {
        if (defectnote == null) {
            defectnote = new ArrayList<DefectNote>();
        }
        return this.defectnote;
    }

}
