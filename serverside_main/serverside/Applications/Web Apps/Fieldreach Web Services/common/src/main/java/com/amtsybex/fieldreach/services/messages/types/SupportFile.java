
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("SupportFile")
public class SupportFile implements Serializable {

	private static final long serialVersionUID = 996462841359363282L;
	
	private String fileName;
    private String checkSum;

    /** 
     * Get the 'fileName' element value.
     * 
     * @return value
     */
    public String getFileName() {
        return fileName;
    }

    /** 
     * Set the 'fileName' element value.
     * 
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /** 
     * Get the 'checkSum' element value.
     * 
     * @return value
     */
    public String getCheckSum() {
        return checkSum;
    }

    /** 
     * Set the 'checkSum' element value.
     * 
     * @param checkSum
     */
    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }
}
