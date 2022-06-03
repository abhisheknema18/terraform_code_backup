package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("UOMNUMVALID")
public class UomNumValid {

	@XStreamAlias("SEQUENCENUMBER")
    @JacksonXmlProperty(localName = "SEQUENCENUMBER")
    protected int sequencenumber;
    
	@XStreamAlias("UOMTYPE")
    @JacksonXmlProperty(localName = "UOMTYPE")
    protected String uomtype;
    
	@XStreamAlias("UPPERLIMIT")
    @JacksonXmlProperty(localName = "UPPERLIMIT")
    protected String upperlimit;
    
	@XStreamAlias("LOWERLIMIT")
    @JacksonXmlProperty(localName = "LOWERLIMIT")
    protected String lowerlimit;
    
	@XStreamAlias("UPPERWARNING")
    @JacksonXmlProperty(localName = "UPPERWARNING")
    protected String upperwarning;
    
	@XStreamAlias("LOWERWARNING")
    @JacksonXmlProperty(localName = "LOWERWARNING")
    protected String lowerwarning;


    public int getSEQUENCENUMBER() {
        return sequencenumber;
    }

    public void setSEQUENCENUMBER(int value) {
        this.sequencenumber = value;
    }

    public String getUOMTYPE() {
        return uomtype;
    }

    public void setUOMTYPE(String value) {
        this.uomtype = value;
    }

    public String getUPPERLIMIT() {
        return upperlimit;
    }

    public void setUPPERLIMIT(String value) {
        this.upperlimit = value;
    }

    public String getLOWERLIMIT() {
        return lowerlimit;
    }

    public void setLOWERLIMIT(String value) {
        this.lowerlimit = value;
    }

    public String getUPPERWARNING() {
        return upperwarning;
    }

    public void setUPPERWARNING(String value) {
        this.upperwarning = value;
    }

    public String getLOWERWARNING() {
        return lowerwarning;
    }

    public void setLOWERWARNING(String value) {
        this.lowerwarning = value;
    }

}
