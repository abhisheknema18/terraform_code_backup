package com.amtsybex.fieldreach.services.messages.request;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.Item;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("UpdateScriptResult")
public class UpdateScriptResult implements Serializable {

	private static final long serialVersionUID = -3232186056086291903L;
	public static final String APPLICATION_VND_FIELDSMART_UPDATESCRIPTRESULT_1_JSON = "application/vnd.fieldsmart.update-script-result-1+json";
    public static final String APPLICATION_VND_FIELDSMART_UPDATESCRIPTRESULT_1_XML = "application/vnd.fieldsmart.update-script-result-1+xml";

	@XStreamAlias("ITEM")
    @JacksonXmlProperty(localName = "ITEM")
	protected Item item;
    
    @XStreamAlias("USERCODE")
    @JacksonXmlProperty(localName = "USERCODE")
    protected String usercode;

    
    public Item getITEM() {
    	
        return item;
    }

    public void setITEM(Item value) {
    	
        this.item = value;
    }

    public String getUSERCODE() {
    	
        return usercode;
    }

    public void setUSERCODE(String value) {
    	
        this.usercode = value;
    }

}
