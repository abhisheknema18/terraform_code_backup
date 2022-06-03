package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("DEFECTSETDETAIL")
public class DefectSetDetail {

	@XStreamAlias("DEFECTSETNAME")
    @JacksonXmlProperty(localName = "DEFECTSETNAME")
    protected String defectsetname;
    
	@XStreamAlias("DEFECTCODE")
    @JacksonXmlProperty(localName = "DEFECTCODE")
    protected String defectcode;


    public String getDEFECTSETNAME() {
        return defectsetname;
    }

    public void setDEFECTSETNAME(String value) {
        this.defectsetname = value;
    }

    public String getDEFECTCODE() {
        return defectcode;
    }
    
    public void setDEFECTCODE(String value) {
        this.defectcode = value;
    }

}
