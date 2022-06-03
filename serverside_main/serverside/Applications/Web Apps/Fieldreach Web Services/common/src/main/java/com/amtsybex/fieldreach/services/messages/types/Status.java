
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("Status")
@XStreamConverter(value=ToAttributedValueConverter.class, strings={"status"})
public class Status implements Serializable {

	private static final long serialVersionUID = -7401116565351260446L;

	@JacksonXmlText()
	private String status;
    
    @XStreamAlias("OrderNo")
    @XStreamAsAttribute
	@JacksonXmlProperty(isAttribute = true, localName = "OrderNo")
    private String orderNo; 
        
    @XStreamAlias("System")
    @XStreamAsAttribute
	@JacksonXmlProperty(isAttribute = true, localName = "System")
	private String system; 
    	
	public void setStatus(String status) {
		
		this.status = status;
	}
    
	public String getOrderNo() {
		
		return orderNo;
	}
	
	public void setOrderNo(String orderNo) {
		
		this.orderNo = orderNo;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getStatus() {
		
		return status;
	}
	
}
