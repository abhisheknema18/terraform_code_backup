package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("REFSUBCAT")
public class RefSubCat {

	@XStreamAlias("REFSUBCATID")
    @JacksonXmlProperty(localName = "REFSUBCATID")
    protected int refsubcatid;
	
	@XStreamAlias("REFSUBCATDESC")
    @JacksonXmlProperty(localName = "REFSUBCATDESC")
    protected String refsubcatdesc;
	
	@XStreamAlias("REFCATID")
    @JacksonXmlProperty(localName = "REFCATID")
    protected Integer refcatid;
	
	@XStreamAlias("REFSUBCATTYPE")
    @JacksonXmlProperty(localName = "REFSUBCATTYPE")
    protected String refsubcattype;
	
	@XStreamImplicit
    @JacksonXmlProperty(localName = "REFITEM")
    @JacksonXmlElementWrapper(localName = "REFITEM", useWrapping = false)
    protected List<RefItem> refitem;


    public int getREFSUBCATID() {
        return refsubcatid;
    }

    public void setREFSUBCATID(int value) {
        this.refsubcatid = value;
    }

    public String getREFSUBCATDESC() {
        return refsubcatdesc;
    }

    public void setREFSUBCATDESC(String value) {
        this.refsubcatdesc = value;
    }

    public Integer getREFCATID() {
        return refcatid;
    }

    public void setREFCATID(Integer value) {
        this.refcatid = value;
    }

    public String getREFSUBCATTYPE() {
        return refsubcattype;
    }

    public void setREFSUBCATTYPE(String value) {
        this.refsubcattype = value;
    }

    public List<RefItem> getREFITEM() {
        if (refitem == null) {
            refitem = new ArrayList<RefItem>();
        }
        return this.refitem;
    }
    
    @Override
    public boolean equals (Object o) {
    	RefSubCat x = (RefSubCat) o;
        if (x.refsubcatid == refsubcatid) return true;
        return false;
    }

}
