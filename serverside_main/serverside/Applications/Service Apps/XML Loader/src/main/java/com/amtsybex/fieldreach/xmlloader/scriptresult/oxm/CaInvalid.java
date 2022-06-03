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
    "idx"
})
public class CaInvalid {

    @XmlElement(name = "IDX")
    protected List<Idx> idx;

    /**
     * Gets the value of the idx property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the idx property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIDX().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Idx }
     * 
     * 
     */
    public List<Idx> getIDX() {
        if (idx == null) {
            idx = new ArrayList<Idx>();
        }
        return this.idx;
    }

}
