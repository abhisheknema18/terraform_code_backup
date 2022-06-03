package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("DEFECTNOTE")
public class DefectNote {

	@XStreamAlias("DEFECTCODE")
	@JacksonXmlProperty(localName = "DEFECTCODE")
    protected String defectcode;
	
	@XStreamAlias("SNID")
	@JacksonXmlProperty(localName = "SNID")
    protected Integer snid;

    public String getDEFECTCODE() {
        return defectcode;
    }

    public void setDEFECTCODE(String value) {
        this.defectcode = value;
    }

    public Integer getSNID() {
        return snid;
    }

    public void setSNID(Integer value) {
        this.snid = value;
    }

}
