
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("File")
public class File implements Serializable {

	private static final long serialVersionUID = -6479210626718692971L;
	
	private String Name;
    private String Checksum;

    /** 
     * Get the 'Name' element value.
     * 
     * @return value
     */
    public String getName() {
        return Name;
    }

    /** 
     * Set the 'Name' element value.
     * 
     * @param name
     */
    public void setName(String name) {
        this.Name = name;
    }

    /** 
     * Get the 'Checksum' element value.
     * 
     * @return value
     */
    public String getChecksum() {
        return Checksum;
    }

    /** 
     * Set the 'Checksum' element value.
     * 
     * @param checksum
     */
    public void setChecksum(String checksum) {
        this.Checksum = checksum;
    }
}
