package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("deltaResultList")
@JacksonXmlRootElement(localName = "deltaResultList")
public class DeltaResultList implements Serializable {

	private static final long serialVersionUID = 7329516044831502713L;

	public DeltaResultList(List<Integer> deltaResultList) {
		super();
		this.deltaResultList = deltaResultList;
	}

	public DeltaResultList() {
	}

	@XStreamImplicit(itemFieldName="returnId")
	@JacksonXmlElementWrapper(localName = "returnId", useWrapping = false)
	@JacksonXmlProperty(localName = "returnId")
    private List<Integer> deltaResultList;

	public List<Integer> getDeltaResultList() {
		return deltaResultList;
	}

	public void setDeltaResultList(List<Integer> deltaResultList) {
		this.deltaResultList = deltaResultList;
	}
	
	

}
