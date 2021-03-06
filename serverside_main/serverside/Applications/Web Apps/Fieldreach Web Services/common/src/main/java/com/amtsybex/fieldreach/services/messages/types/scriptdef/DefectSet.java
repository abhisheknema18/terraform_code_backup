//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.09.15 at 12:51:32 PM BST 
//


package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("DEFECTSET")
public class DefectSet {

	@XStreamAlias("DEFECTSETNAME")
	@JacksonXmlProperty(localName = "DEFECTSETNAME")
    protected String defectsetname;
	
	@XStreamAlias("DEFECTSETDESC")
    @JacksonXmlProperty(localName = "DEFECTSETDESC")
    protected String defectsetdesc;
	
	@XStreamAlias("DEFECTSETDETAIL")
    @JacksonXmlElementWrapper(localName = "DEFECTSETDETAIL", useWrapping = true)
    @JacksonXmlProperty(localName = "DEFECTSETDETAIL")
    protected List<DefectSetDetail> defectsetdetail;

    public String getDEFECTSETNAME() {
        return defectsetname;
    }

    public void setDEFECTSETNAME(String value) {
        this.defectsetname = value;
    }

    public String getDEFECTSETDESC() {
        return defectsetdesc;
    }

    public void setDEFECTSETDESC(String value) {
        this.defectsetdesc = value;
    }

    public List<DefectSetDetail> getDEFECTSETDETAIL() {
        if (defectsetdetail == null) {
            defectsetdetail = new ArrayList<DefectSetDetail>();
        }
        return this.defectsetdetail;
    }

}
