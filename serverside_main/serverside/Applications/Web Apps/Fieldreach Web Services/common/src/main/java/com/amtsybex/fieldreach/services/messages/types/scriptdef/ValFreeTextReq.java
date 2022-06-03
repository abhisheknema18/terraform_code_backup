package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("VALFREETEXTREQ")
public class ValFreeTextReq {

	@XStreamAlias("SEQUENCENUMBER")
    @JacksonXmlProperty(localName = "SEQUENCENUMBER")
    protected int sequencenumber;
	
	@XStreamAlias("VALIDATIONTYPE")
    @JacksonXmlProperty(localName = "VALIDATIONTYPE")
    protected String validationtype;
	
	@XStreamAlias("RESPONSE")
    @JacksonXmlProperty(localName = "RESPONSE")
    protected String response;
	
	@XStreamAlias("DISPLAYMESSAGE")
    @JacksonXmlProperty(localName = "DISPLAYMESSAGE")
    protected String displaymessage;

	
    public int getSEQUENCENUMBER() {
        return sequencenumber;
    }

    public void setSEQUENCENUMBER(int value) {
        this.sequencenumber = value;
    }

    public String getVALIDATIONTYPE() {
        return validationtype;
    }

    public void setVALIDATIONTYPE(String value) {
        this.validationtype = value;
    }

    public String getRESPONSE() {
        return response;
    }

    public void setRESPONSE(String value) {
        this.response = value;
    }

    public String getDISPLAYMESSAGE() {
        return displaymessage;
    }

    public void setDISPLAYMESSAGE(String value) {
        this.displaymessage = value;
    }

}
