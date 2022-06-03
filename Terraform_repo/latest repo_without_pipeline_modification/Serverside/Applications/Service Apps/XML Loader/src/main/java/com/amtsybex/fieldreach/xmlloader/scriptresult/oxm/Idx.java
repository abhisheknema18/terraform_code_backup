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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
public class Idx {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "JTS")
    protected String jts;

    /**
     * Gets the value of the value property.
     * 
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the jts property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public String getJTS() {
        return jts;
    }

    /**
     * Sets the value of the jts property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setJTS(String value) {
        this.jts = value;
    }

}
