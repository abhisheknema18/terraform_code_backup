package com.amtsybex.fieldreach.services.messages.response;

import java.util.List;

import com.amtsybex.fieldreach.services.messages.types.packages.PackageInfo;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import io.swagger.annotations.ApiModel;

@ApiModel
public class PackageInfoCollection {

    public static final String APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_JSON = "application/vnd.fieldsmart.package-info-1+json";
    public static final String APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_XML = "application/vnd.fieldsmart.package-info-1+xml";
    
    @JacksonXmlProperty(localName = "PackageInfo")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "Packages")
	private List<PackageInfo> packages;

	
	public PackageInfoCollection() {
		super();
	}
	
	public PackageInfoCollection(List<PackageInfo> packages) {
		super();
		this.packages = packages;
	}

	public List<PackageInfo> getPackages() {
		return packages;
	}

	public void setPackages(List<PackageInfo> packages) {
		this.packages = packages;
	}
	
	
}
