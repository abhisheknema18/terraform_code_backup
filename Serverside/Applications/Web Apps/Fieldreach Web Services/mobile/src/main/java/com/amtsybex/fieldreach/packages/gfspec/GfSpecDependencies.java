package com.amtsybex.fieldreach.packages.gfspec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class GfSpecDependencies {

	@XmlElement (name="id")
	private String id;
	
	@XmlElement (name="version")
	private Integer version;

	public GfSpecDependencies() {
		super();
	}

	public GfSpecDependencies(String id, Integer version) {
		super();
		this.id = id;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
}
