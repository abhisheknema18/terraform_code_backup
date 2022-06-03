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
    "seqno"
})
public class Rgroups {

    @XmlElement(name = "SEQNO")
    protected List<SeqNo> seqno;

    /**
     * Gets the value of the seqno property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the seqno property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSEQNO().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SeqNo }
     * 
     * 
     */
    public List<SeqNo> getSEQNO() {
        if (seqno == null) {
            seqno = new ArrayList<SeqNo>();
        }
        return this.seqno;
    }

}
