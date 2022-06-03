/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	22/08/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult.oxm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
public class SubScriptResults {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "RESULTSFILE", required = true)
    protected String resultsfile;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the resultsfile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRESULTSFILE() {
        return resultsfile;
    }

    /**
     * Sets the value of the resultsfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRESULTSFILE(String value) {
        this.resultsfile = value;
    }

}
