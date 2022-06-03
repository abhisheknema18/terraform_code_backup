
package com.amtsybex.fieldreach.services.messages.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("VALIDATIONTYPEREQUEST")
public class VALIDATIONTYPEREQUEST implements Serializable {
	
	private static final long serialVersionUID = 3608477129632074415L;
	
	private List<String> VALIDATIONTYPELIST = new ArrayList<String>();
    private String CHECKSUM;

    /** 
     * Get the list of 'VALIDATIONTYPE' element items.
     * 
     * @return list
     */
    public List<String> getVALIDATIONTYPELIST() {
        return VALIDATIONTYPELIST;
    }

    /** 
     * Set the list of 'VALIDATIONTYPE' element items.
     * 
     * @param list
     */
    public void setVALIDATIONTYPELIST(List<String> list) {
        VALIDATIONTYPELIST = list;
    }

    /** 
     * Get the 'CHECKSUM' element value.
     * 
     * @return value
     */
    public String getCHECKSUM() {
        return CHECKSUM;
    }

    /** 
     * Set the 'CHECKSUM' element value.
     * 
     * @param CHECKSUM
     */
    public void setCHECKSUM(String CHECKSUM) {
        this.CHECKSUM = CHECKSUM;
    }
}
