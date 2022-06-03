package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("FORMATINPUTDEF")
public class FormatInputDef {

	@XStreamAlias("SEQUENCENUMBER")
    @JacksonXmlProperty(localName = "SEQUENCENUMBER")
    protected int sequencenumber;
	
	@XStreamAlias("CHARPOS")
    @JacksonXmlProperty(localName = "CHARPOS")
    protected int charpos;
	
	@XStreamAlias("CHARTYPE")
    @JacksonXmlProperty(localName = "CHARTYPE")
    protected String chartype;
	
	@XStreamAlias("ALLOWEDCHARS")
    @JacksonXmlProperty(localName = "ALLOWEDCHARS")
    protected String allowedchars;
	
	@XStreamAlias("CASE")
    @JacksonXmlProperty(localName = "CASE")
    protected String _case;

	
    public int getSEQUENCENUMBER() {
        return sequencenumber;
    }

    public void setSEQUENCENUMBER(int value) {
        this.sequencenumber = value;
    }

    public int getCHARPOS() {
        return charpos;
    }

    public void setCHARPOS(int value) {
        this.charpos = value;
    }

    public String getCHARTYPE() {
        return chartype;
    }

    public void setCHARTYPE(String value) {
        this.chartype = value;
    }

    public String getALLOWEDCHARS() {
        return allowedchars;
    }

    public void setALLOWEDCHARS(String value) {
        this.allowedchars = value;
    }

    public String getCASE() {
        return _case;
    }

    public void setCASE(String value) {
        this._case = value;
    }

}
