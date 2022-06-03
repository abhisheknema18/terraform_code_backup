package com.amtsybex.fieldreach.services.messages.response;

import java.util.List;

import com.amtsybex.fieldreach.services.messages.types.packages.PackageMetaDependency;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageUpdateStep;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageVersion;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import io.swagger.annotations.ApiModel;

@ApiModel
public class PackageUpdate {
	
    public static final String APPLICATION_VND_FIELDSMART_PACKAGEUPDATE_1_JSON = "application/vnd.fieldsmart.package-update-1+json";
    public static final String APPLICATION_VND_FIELDSMART_PACKAGEUPDATE_1_XML = "application/vnd.fieldsmart.package-update-1+xml";
    
	private String id;
	
	private PackageVersion version;
	
    @JacksonXmlProperty(localName = "PackageUpdateStep")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "Steps")
	private List<PackageUpdateStep> steps;
	
    @JacksonXmlProperty(localName = "Dependencies")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "PackageMetaDependency")
	private List<PackageMetaDependency> dependencies;
	
	public PackageUpdate() {
		super();
	}
	
	public PackageUpdate(String id, PackageVersion version, List<PackageUpdateStep> steps,
			List<PackageMetaDependency> dependencies) {
		super();
		this.id = id;
		this.version = version;
		this.steps = steps;
		this.dependencies = dependencies;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public PackageVersion getVersion() {
		return version;
	}
	
	public void setVersion(PackageVersion version) {
		this.version = version;
	}
	
	public List<PackageUpdateStep> getSteps() {
		return steps;
	}
	
	public void setSteps(List<PackageUpdateStep> steps) {
		this.steps = steps;
	}
	
	public List<PackageMetaDependency> getDependencies() {
		return dependencies;
	}
	
	public void setDependencies(List<PackageMetaDependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	

}
