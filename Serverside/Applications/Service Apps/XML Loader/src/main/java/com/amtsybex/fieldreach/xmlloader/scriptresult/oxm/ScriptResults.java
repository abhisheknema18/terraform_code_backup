/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	05/08/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult.oxm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
public class ScriptResults {

    @XmlElement(name = "PROFILE", required = true)
    protected Profile profile;
    @XmlElement(name = "RESULTS", required = true)
    protected Results results;
    @XmlElement(name = "HK")
    protected Hk hk;
    @XmlElement(name = "CARRYTHROUGH")
    protected CarryThrough carrythrough;
    @XmlAttribute(name = "VersionNo", required = true)
    protected String versionNo;
    @XmlAttribute(name = "ScriptId", required = true)
    protected String scriptId;
    @XmlAttribute(name = "ScriptCodeId", required = true)
    protected String scriptCodeId;
    @XmlAttribute(name = "ScriptCode", required = true)
    protected String scriptCode;
    @XmlAttribute(name = "OnlineDate", required = true)
    protected String onlineDate;
    @XmlAttribute(name = "ItemCount", required = true)
    protected String itemCount;

    /**
     * Gets the value of the profile property.
     * 
     * @return
     *     possible object is
     *     {@link PROFILE }
     *     
     */
    public Profile getPROFILE() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     * 
     * @param value
     *     allowed object is
     *     {@link PROFILE }
     *     
     */
    public void setPROFILE(Profile value) {
        this.profile = value;
    }

    /**
     * Gets the value of the results property.
     * 
     * @return
     *     possible object is
     *     {@link RESULTS }
     *     
     */
    public Results getRESULTS() {
        return results;
    }

    /**
     * Sets the value of the results property.
     * 
     * @param value
     *     allowed object is
     *     {@link RESULTS }
     *     
     */
    public void setRESULTS(Results value) {
        this.results = value;
    }

    /**
     * Gets the value of the hk property.
     * 
     * @return
     *     possible object is
     *     {@link HK }
     *     
     */
    public Hk getHK() {
        return hk;
    }

    /**
     * Sets the value of the hk property.
     * 
     * @param value
     *     allowed object is
     *     {@link HK }
     *     
     */
    public void setHK(Hk value) {
        this.hk = value;
    }

    /**
     * Gets the value of the carrythrough property.
     * 
     * @return
     *     possible object is
     *     {@link CARRYTHROUGH }
     *     
     */
    public CarryThrough getCARRYTHROUGH() {
        return carrythrough;
    }

    /**
     * Sets the value of the carrythrough property.
     * 
     * @param value
     *     allowed object is
     *     {@link CARRYTHROUGH }
     *     
     */
    public void setCARRYTHROUGH(CarryThrough value) {
        this.carrythrough = value;
    }

    /**
     * Gets the value of the versionNo property.
     * 
     */
    public String getVersionNo() {
        return versionNo;
    }

    /**
     * Sets the value of the versionNo property.
     * 
     */
    public void setVersionNo(String value) {
        this.versionNo = value;
    }

    /**
     * Gets the value of the scriptId property.
     * 
     */
    public String getScriptId() {
        return scriptId;
    }

    /**
     * Sets the value of the scriptId property.
     * 
     */
    public void setScriptId(String value) {
        this.scriptId = value;
    }

    /**
     * Gets the value of the scriptCodeId property.
     * 
     */
    public String getScriptCodeId() {
        return scriptCodeId;
    }

    /**
     * Sets the value of the scriptCodeId property.
     * 
     */
    public void setScriptCodeId(String value) {
        this.scriptCodeId = value;
    }

    /**
     * Gets the value of the scriptCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScriptCode() {
        return scriptCode;
    }

    /**
     * Sets the value of the scriptCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScriptCode(String value) {
        this.scriptCode = value;
    }

    /**
     * Gets the value of the onlineDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnlineDate() {
        return onlineDate;
    }

    /**
     * Sets the value of the onlineDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnlineDate(String value) {
        this.onlineDate = value;
    }

    /**
     * Gets the value of the itemCount property.
     * 
     */
    public String getItemCount() {
        return itemCount;
    }

    /**
     * Sets the value of the itemCount property.
     * 
     */
    public void setItemCount(String value) {
        this.itemCount = value;
    }

}
