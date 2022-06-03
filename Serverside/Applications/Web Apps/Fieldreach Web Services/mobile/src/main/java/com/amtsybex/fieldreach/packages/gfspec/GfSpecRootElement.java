package com.amtsybex.fieldreach.packages.gfspec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name = "package")
@XmlAccessorType(XmlAccessType.FIELD)
public class GfSpecRootElement {

	@XmlElement (name ="metadata")
	private GfSpecMetadata Metadata;

	public GfSpecRootElement() {
		super();
	}

	public GfSpecRootElement(GfSpecMetadata metadata) {
		super();
		Metadata = metadata;
	}

	public GfSpecMetadata getMetadata() {
		return Metadata;
	}

	public void setMetadata(GfSpecMetadata metadata) {
		Metadata = metadata;
	}
	
}
