package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("RESPONSE")
@XStreamConverter(value=ToAttributedValueConverter.class, strings={"value"})
public class Response implements Serializable {

	private static final long serialVersionUID = 7346303970403679959L;

	@JacksonXmlText
	protected String value;
     
     @XStreamAlias("Type")
     @XStreamAsAttribute
     @JacksonXmlProperty(localName = "Type", isAttribute = true)
     protected String type;
     
     @XStreamAlias("RESPONSE_Display")
     @XStreamAsAttribute
     @JacksonXmlProperty(localName = "RESPONSE_Display", isAttribute = true)
     protected String responseDisplay;
     
     @XStreamAlias("LINENNO")
     @XStreamAsAttribute
     @JacksonXmlProperty(localName = "LINENNO", isAttribute = true)
     protected String lineno;
     
     @XStreamAlias("LABEL")
     @XStreamAsAttribute
     @JacksonXmlProperty(localName = "LABEL", isAttribute = true)
     protected String label;

     public String getValue() {
         return value;
     }

     public void setValue(String value) {
         this.value = value;
     }

     public String getType() {
         return type;
     }

     public void setType(String value) {
         this.type = value;
     }

     public String getRESPONSEDisplay() {
         return responseDisplay;
     }

     public void setRESPONSEDisplay(String value) {
         this.responseDisplay = value;
     }

     public String getLINENO() {
         return lineno;
     }

     public void setLINENO(String value) {
         this.lineno = value;
     }

     public String getLABEL() {
         return label;
     }

     public void setLABEL(String value) {
         this.label = value;
     }
     
}
