package com.amtsybex.fieldreach.services.messages.types;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

@XStreamAlias("ITEM")
public class Item implements Serializable {

	private static final long serialVersionUID = 2857221721107591571L;

	@XStreamAlias("TIME")
    @JacksonXmlProperty(localName = "TIME")
    protected String time;
	
	@XStreamAlias("DATE")
    @JacksonXmlProperty(localName = "DATE")
    protected String date;
    
	@XStreamAlias("FREETEXT")
    @JacksonXmlProperty(localName = "FREETEXT")
    protected String freetext;
    
	@XStreamAlias("UOM")
    @JacksonXmlProperty(localName = "UOM")
    protected String uom;
    
	@XStreamAlias("PREV")
    @JacksonXmlProperty(localName = "PREV")
    protected String prev;
    
	@XStreamAlias("RESPONSEFILE")
    @JacksonXmlProperty(localName = "RESPONSEFILE")
    protected String responsefile;
    
	@XStreamAlias("RESPONSEFILENAME")
    @JacksonXmlProperty(localName = "RESPONSEFILENAME")
    protected String responsefilename;
    
	@XStreamAlias("RESPONSE")
    @JacksonXmlProperty(localName = "RESPONSE")
    protected Response response;
    
    @XStreamAlias("SeqNo")
    @XStreamAsAttribute
    @JacksonXmlProperty(localName = "SeqNo", isAttribute = true)
    protected int seqNo;
    
    @XStreamAlias("ResOrderNo")
    @XStreamAsAttribute
    @JacksonXmlProperty(localName = "ResOrderNo", isAttribute = true)
    protected int resOrderNo;


    public String getTIME() {
        return time;
    }

    public void setTIME(String value) {
        this.time = value;
    }

    public String getDATE() {
        return date;
    }

    public void setDATE(String value) {
        this.date = value;
    }

    public String getFREETEXT() {
        return freetext;
    }

    public void setFREETEXT(String value) {
        this.freetext = value;
    }

    public String getUOM() {
        return uom;
    }

    public void setUOM(String value) {
        this.uom = value;
    }

    public String getPREV() {
        return prev;
    }

    public void setPREV(String value) {
        this.prev = value;
    }

    public String getRESPONSEFILE() {
        return responsefile;
    }

    public void setRESPONSEFILE(String value) {
        this.responsefile = value;
    }
    
    public String getRESPONSEFILENAME() {
        return responsefilename;
    }

    public void setRESPONSEFILENAME(String value) {
        this.responsefilename = value;
    }

    public Response getRESPONSE() {
        return response;
    }

    public void setRESPONSE(Response value) {
        this.response = value;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int value) {
        this.seqNo = value;
    }

    public int getResOrderNo() {
        return resOrderNo;
    }

    public void setResOrderNo(int value) {
        this.resOrderNo = value;
    }

}
