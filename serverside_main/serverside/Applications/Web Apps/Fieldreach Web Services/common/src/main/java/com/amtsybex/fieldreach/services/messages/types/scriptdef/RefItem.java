package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("REFITEM")
public class RefItem {

	@XStreamAlias("REFID")
    @JacksonXmlProperty(localName = "REFID")
    protected int refid;
    
	@XStreamAlias("REFDESC")
    @JacksonXmlProperty(localName = "REFDESC")
    protected String refdesc;
    
	@XStreamAlias("REFSUBCATID")
    @JacksonXmlProperty(localName = "REFSUBCATID")
    protected Integer refsubcatid;
    
	@XStreamAlias("OTHERREF")
    @JacksonXmlProperty(localName = "OTHERREF")
    protected String otherref;

	
    public int getREFID() {
        return refid;
    }

    public void setREFID(int value) {
        this.refid = value;
    }

    public String getREFDESC() {
        return refdesc;
    }

    public void setREFDESC(String value) {
        this.refdesc = value;
    }

    public Integer getREFSUBCATID() {
        return refsubcatid;
    }

    public void setREFSUBCATID(Integer value) {
        this.refsubcatid = value;
    }

    public String getOTHERREF() {
        return otherref;
    }

    public void setOTHERREF(String value) {
        this.otherref = value;
    }
    
    @Override
    public boolean equals (Object o) {
    	RefItem x = (RefItem) o;
        if (x.refid == refid) return true;
        return false;
    }

}
