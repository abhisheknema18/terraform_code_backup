package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("SCRIPTREFITEM")
public class ScriptRefItem {

	@XStreamAlias("ID")
    @JacksonXmlProperty(localName = "ID")
    protected int id;
	
	@XStreamAlias("SEQUENCENUMBER")
    @JacksonXmlProperty(localName = "SEQUENCENUMBER")
    protected int sequencenumber;
	
	@XStreamAlias("REFID")
    @JacksonXmlProperty(localName = "REFID")
    protected int refid;
	
	@XStreamAlias("IDTYPE")
    @JacksonXmlProperty(localName = "IDTYPE")
    protected String idtype;

	
    public int getID() {
        return id;
    }

    public void setID(int value) {
        this.id = value;
    }

    public int getSEQUENCENUMBER() {
        return sequencenumber;
    }

    public void setSEQUENCENUMBER(int value) {
        this.sequencenumber = value;
    }

    public int getREFID() {
        return refid;
    }

    public void setREFID(int value) {
        this.refid = value;
    }

    public String getIDTYPE() {
        return idtype;
    }

    public void setIDTYPE(String value) {
        this.idtype = value;
    }

}
