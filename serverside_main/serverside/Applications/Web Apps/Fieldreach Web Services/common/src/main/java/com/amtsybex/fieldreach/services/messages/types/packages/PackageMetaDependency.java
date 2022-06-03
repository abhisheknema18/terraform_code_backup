package com.amtsybex.fieldreach.services.messages.types.packages;

import io.swagger.annotations.ApiModel;

@ApiModel
public class PackageMetaDependency {
	
    public String Id;

    public Integer Version;
	
	public PackageMetaDependency() {
		super();
	}
	
	public PackageMetaDependency(String id, Integer version) {
		super();

		this.Id = id;
		this.Version = version;		
	}
	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		this.Id = id;
	}
	public Integer getVersion() {
		return Version;
	}
	public void setVersion(Integer version) {
		this.Version = version;
	}
	
}
