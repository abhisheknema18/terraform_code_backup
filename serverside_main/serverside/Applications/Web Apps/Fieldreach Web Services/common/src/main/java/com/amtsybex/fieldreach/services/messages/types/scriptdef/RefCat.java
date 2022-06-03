package com.amtsybex.fieldreach.services.messages.types.scriptdef;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("REFCAT")
public class RefCat {

	@XStreamAlias("REFCATID")
    @JacksonXmlProperty(localName = "REFCATID")
    protected int refcatid;
    
	@XStreamAlias("REFCATDESC")
    @JacksonXmlProperty(localName = "REFCATDESC")
    protected String refcatdesc;
    
	@XStreamImplicit
    @JacksonXmlProperty(localName = "REFSUBCAT")
    @JacksonXmlElementWrapper(localName = "REFSUBCAT", useWrapping = false)
    protected List<RefSubCat> refsubcat;

	
    public int getREFCATID() {
        return refcatid;
    }

    public void setREFCATID(int value) {
        this.refcatid = value;
    }

    public String getREFCATDESC() {
        return refcatdesc;
    }

    public void setREFCATDESC(String value) {
        this.refcatdesc = value;
    }

    public List<RefSubCat> getREFSUBCAT() {
        if (refsubcat == null) {
            refsubcat = new ArrayList<RefSubCat>();
        }
        return this.refsubcat;
    }
    
    @Override
    public boolean equals (Object o) {
    	RefCat x = (RefCat) o;
        if (x.refcatid == refcatid) return true;
        return false;
    }

}
