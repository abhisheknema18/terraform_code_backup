
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("FullDataBaseMessage")
public class FullDataBaseMessage implements Serializable {

	private static final long serialVersionUID = 5601638893128037939L;
	
	private String name;
    private long sizeBytes;
    private String checksum;

    /** 
     * Get the 'name' element value.
     * 
     * @return value
     */
    public String getName() {
        return name;
    }

    /** 
     * Set the 'name' element value.
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** 
     * Get the 'sizeBytes' element value.
     * 
     * @return value
     */
    public long getSizeBytes() {
        return sizeBytes;
    }

    /** 
     * Set the 'sizeBytes' element value.
     * 
     * @param sizeBytes
     */
    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    /** 
     * Get the 'checksum' element value.
     * 
     * @return value
     */
    public String getChecksum() {
        return checksum;
    }

    /** 
     * Set the 'checksum' element value.
     * 
     * @param checksum
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
