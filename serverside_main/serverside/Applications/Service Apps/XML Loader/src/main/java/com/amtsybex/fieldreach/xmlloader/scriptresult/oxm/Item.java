/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	05/08/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult.oxm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "time",
    "date",
    "freetext",
    "uom",
    "prev",
    "responsefile",
    "responsefilename",
    "response",
    "defects",
    "subscriptresults"
})
public class Item {

    @XmlElement(name = "TIME")
    protected String time;
    @XmlElement(name = "DATE")
    protected String date;
    @XmlElement(name = "FREETEXT")
    protected String freetext;
    @XmlElement(name = "UOM")
    protected String uom;
    @XmlElement(name = "PREV")
    protected String prev;
    @XmlElement(name = "RESPONSEFILE")
    protected String responsefile;
    @XmlElement(name = "RESPONSEFILENAME")
    protected String responsefilename;
    @XmlElement(name = "RESPONSE")
    protected Response response;
    @XmlElement(name = "DEFECTS")
    protected Defects defects;
    @XmlElement(name = "SUBSCRIPTRESULTS")
    protected List<SubScriptResults> subscriptresults;
    @XmlAttribute(name = "SeqNo", required = true)
    protected String seqNo;
    @XmlAttribute(name = "ResOrderNo", required = true)
    protected String resOrderNo;

    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTIME() {
        return time;
    }

    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTIME(String value) {
        this.time = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDATE() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDATE(String value) {
        this.date = value;
    }

    /**
     * Gets the value of the freetext property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFREETEXT() {
        return freetext;
    }

    /**
     * Sets the value of the freetext property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFREETEXT(String value) {
        this.freetext = value;
    }

    /**
     * Gets the value of the uom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUOM() {
        return uom;
    }

    /**
     * Sets the value of the uom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUOM(String value) {
        this.uom = value;
    }

    /**
     * Gets the value of the prev property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPREV() {
        return prev;
    }

    /**
     * Sets the value of the prev property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPREV(String value) {
        this.prev = value;
    }

    /**
     * Gets the value of the responsefile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRESPONSEFILE() {
        return responsefile;
    }

    /**
     * Sets the value of the responsefile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRESPONSEFILE(String value) {
        this.responsefile = value;
    }

    /**
     * Gets the value of the responsefilename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRESPONSEFILENAME() {
        return responsefilename;
    }

    /**
     * Sets the value of the responsefilename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRESPONSEFILENAME(String value) {
        this.responsefilename = value;
    }

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link Response }
     *     
     */
    public Response getRESPONSE() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link Response }
     *     
     */
    public void setRESPONSE(Response value) {
        this.response = value;
    }

    /**
     * Gets the value of the defects property.
     * 
     * @return
     *     possible object is
     *     {@link Defects }
     *     
     */
    public Defects getDEFECTS() {
        return defects;
    }

    /**
     * Sets the value of the defects property.
     * 
     * @param value
     *     allowed object is
     *     {@link Defects }
     *     
     */
    public void setDEFECTS(Defects value) {
        this.defects = value;
    }
	
    /**
     * Gets the value of the subscriptresults property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subscriptresults property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSUBSCRIPTRESULTS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SubScriptResults }
     * 
     * 
     */
    public List<SubScriptResults> getSUBSCRIPTRESULTS() {
        if (subscriptresults == null) {
            subscriptresults = new ArrayList<SubScriptResults>();
        }
        return this.subscriptresults;
    }
    
    /**
     * Gets the value of the seqNo property.
     * 
     */
    public String getSeqNo() {
        return seqNo;
    }

    /**
     * Sets the value of the seqNo property.
     * 
     */
    public void setSeqNo(String value) {
        this.seqNo = value;
    }

    /**
     * Gets the value of the resOrderNo property.
     * 
     */
    public String getResOrderNo() {
        return resOrderNo;
    }

    /**
     * Sets the value of the resOrderNo property.
     * 
     */
    public void setResOrderNo(String value) {
        this.resOrderNo = value;
    }

}
