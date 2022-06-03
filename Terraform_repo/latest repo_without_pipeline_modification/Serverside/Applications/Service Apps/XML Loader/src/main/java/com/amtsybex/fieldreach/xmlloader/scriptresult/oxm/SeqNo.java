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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class SeqNo {

    @XmlAttribute(name = "START", required = true)
    protected String start;
    @XmlAttribute(name = "NOGROUPS")
    protected String nogroups;

    /**
     * Gets the value of the start property.
     * 
     */
    public String getSTART() {
        return start;
    }

    /**
     * Sets the value of the start property.
     * 
     */
    public void setSTART(String value) {
        this.start = value;
    }

    /**
     * Gets the value of the nogroups property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public String getNOGROUPS() {
        return nogroups;
    }

    /**
     * Sets the value of the nogroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNOGROUPS(String value) {
        this.nogroups = value;
    }

}
