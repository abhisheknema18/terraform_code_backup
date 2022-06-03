
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Script")
public class Script implements Serializable {

	private static final long serialVersionUID = -6061040641546511156L;
	
	private String scriptCode;
    private String scriptId;
    private String scriptFileName;
    private String scriptDesc;
    private BigInteger versionNumber;
    private BigInteger dateCreated;
    
    @JacksonXmlElementWrapper(localName = "filerefs", useWrapping = true)
    @JacksonXmlProperty(localName = "File")
    private List<File> filerefs = new ArrayList<File>();
    
    private String checksum;

    /** 
     * Get the 'scriptCode' element value.
     * 
     * @return value
     */
    public String getScriptCode() {
        return scriptCode;
    }

    /** 
     * Set the 'scriptCode' element value.
     * 
     * @param scriptCode
     */
    public void setScriptCode(String scriptCode) {
        this.scriptCode = scriptCode;
    }

    /** 
     * Get the 'scriptId' element value.
     * 
     * @return value
     */
    public String getScriptId() {
        return scriptId;
    }

    /** 
     * Set the 'scriptId' element value.
     * 
     * @param scriptId
     */
    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    /** 
     * Get the 'scriptDesc' element value.
     * 
     * @return value
     */
    public String getScriptDesc() {
        return scriptDesc;
    }

    /** 
     * Set the 'scriptDesc' element value.
     * 
     * @param scriptDesc
     */
    public void setScriptDesc(String scriptDesc) {
        this.scriptDesc = scriptDesc;
    }

    /** 
     * Get the 'versionNumber' element value.
     * 
     * @return value
     */
    public BigInteger getVersionNumber() {
        return versionNumber;
    }

    /** 
     * Set the 'versionNumber' element value.
     * 
     * @param versionNumber
     */
    public void setVersionNumber(BigInteger versionNumber) {
        this.versionNumber = versionNumber;
    }

    /** 
     * Get the 'dateCreated' element value.
     * 
     * @return value
     */
    public BigInteger getDateCreated() {
        return dateCreated;
    }

    /** 
     * Set the 'dateCreated' element value.
     * 
     * @param dateCreated
     */
    public void setDateCreated(BigInteger dateCreated) {
        this.dateCreated = dateCreated;
    }

	public List<File> getFilerefs() {
		return filerefs;
	}

	public void setFilerefs(List<File> filerefs) {
		this.filerefs = filerefs;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getScriptFileName() {
		return scriptFileName;
	}

	public void setScriptFileName(String scriptFileName) {
		this.scriptFileName = scriptFileName;
	}
	
}
