package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("FORMATINPUTDEFME")
public class FormatInputDefMe {

	@XStreamAlias("SEQUENCENUMBER")
    @JacksonXmlProperty(localName = "SEQUENCENUMBER")
    protected int sequencenumber;
	
	@XStreamAlias("MINENTRY")
    @JacksonXmlProperty(localName = "MINENTRY")
    protected Integer minentry;
	
	@XStreamAlias("SCRIPTID")
    @JacksonXmlProperty(localName = "SCRIPTID")
    protected Integer scriptid;

	
    public int getSEQUENCENUMBER() {
        return sequencenumber;
    }

    public void setSEQUENCENUMBER(int value) {
        this.sequencenumber = value;
    }

    public Integer getMINENTRY() {
        return minentry;
    }

    public void setMINENTRY(Integer value) {
        this.minentry = value;
    }

    public Integer getSCRIPTID() {
        return scriptid;
    }

    public void setSCRIPTID(Integer value) {
        this.scriptid = value;
    }

}
