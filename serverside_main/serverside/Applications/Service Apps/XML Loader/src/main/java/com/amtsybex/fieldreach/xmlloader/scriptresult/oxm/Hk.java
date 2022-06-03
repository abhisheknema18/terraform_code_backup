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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "rgroups",
    "cainvalid"
})
public class Hk {

    @XmlElement(name = "RGROUPS")
    protected List<Rgroups> rgroups;
    @XmlElement(name = "CAINVALID")
    protected List<CaInvalid> cainvalid;

    /**
     * Gets the value of the rgroups property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rgroups property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRGROUPS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Rgroups }
     * 
     * 
     */
    public List<Rgroups> getRGROUPS() {
        if (rgroups == null) {
            rgroups = new ArrayList<Rgroups>();
        }
        return this.rgroups;
    }

    /**
     * Gets the value of the cainvalid property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cainvalid property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCAINVALID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CaInvalid }
     * 
     * 
     */
    public List<CaInvalid> getCAINVALID() {
        if (cainvalid == null) {
            cainvalid = new ArrayList<CaInvalid>();
        }
        return this.cainvalid;
    }

}
