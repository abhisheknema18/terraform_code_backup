/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	20/08/2014
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
    "defect"
})
public class Defects {

    @XmlElement(name = "DEFECT")
    protected List<Defect> defect;
    @XmlAttribute(name = "DefectCount", required = true)
    protected int defectCount;
    @XmlAttribute(name = "DefectSet", required = true)
    protected String defectSet;
    
    public List<Defect> getDEFECT() {
    	
        if (defect == null) 
            defect = new ArrayList<Defect>();
        
        return this.defect;
    }

    /**
     * Gets the value of the defectCount property.
     * 
     */
    public int getDefectCount() {
        return defectCount;
    }

    /**
     * Sets the value of the defectCount property.
     * 
     */
    public void setDefectCount(int value) {
        this.defectCount = value;
    }

    /**
     * Gets the value of the defectSet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefectSet() {
        return defectSet;
    }

    /**
     * Sets the value of the defectSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefectSet(String value) {
        this.defectSet = value;
    }
    
}
