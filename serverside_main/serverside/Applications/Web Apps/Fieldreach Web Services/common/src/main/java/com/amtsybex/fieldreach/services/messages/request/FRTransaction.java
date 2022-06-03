
package com.amtsybex.fieldreach.services.messages.request;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.TransactionItem;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("FRTransaction")
public class FRTransaction implements Serializable {
	
	private static final long serialVersionUID = 5523563903276981135L;
	
    public static final String APPLICATION_VND_FIELDSMART_TRANSACTION_1_JSON = "application/vnd.fieldsmart.transaction-1+json";
    public static final String APPLICATION_VND_FIELDSMART_TRANSACTION_1_XML = "application/vnd.fieldsmart.transaction-1+xml";
	
	@JacksonXmlProperty(localName = "Item")
	private TransactionItem Item;

    /** 
     * Get the 'Item' element value.
     * 
     * @return value
     */
    public TransactionItem getItem() {
        return Item;
    }

    /** 
     * Set the 'Item' element value.
     * 
     * @param item
     */
    public void setItem(TransactionItem item) {
        this.Item = item;
    }
    
}
