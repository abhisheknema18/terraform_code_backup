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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "scriptResults"
})
@XmlRootElement(name = "dsResultSet")
public class ResultSet {

	@XmlElement(name = "ScriptResults", required = true)
    protected ScriptResults scriptResults;

    /**
     * Gets the value of the scriptResults property.
     * 
     * @return
     *     possible object is
     *     {@link ScriptResults }
     *     
     */
    public ScriptResults getScriptResults() {
        return scriptResults;
    }

    /**
     * Sets the value of the scriptResults property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScriptResults }
     *     
     */
    public void setScriptResults(ScriptResults value) {
        this.scriptResults = value;
    }
    
    /**
     * Set the Extended section of the scripts results section
     * 
     * @param extended
     */
    public void setExtended(Extended extended) {
    	
    	Profile prof = this.scriptResults.getPROFILE();
    	prof.setEXTENDED(extended);
    	this.scriptResults.setPROFILE(prof);
    	
    }

}
