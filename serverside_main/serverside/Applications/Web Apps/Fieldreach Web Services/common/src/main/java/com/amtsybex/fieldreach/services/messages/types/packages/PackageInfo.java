package com.amtsybex.fieldreach.services.messages.types.packages;

import io.swagger.annotations.ApiModel;

@ApiModel
public class PackageInfo {

	private String id;
	
	private String name;

	private PackageVersion version;

	private String region;
	
	public PackageInfo() {
		super();
	}
	
	public PackageInfo(String id, String name, PackageVersion version, String region) {
		super();
		this.id = id;
		this.name = name;
		this.version = version;
		this.region = region;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public PackageVersion getVersion() {
		return version;
	}
	
	public void setVersion(PackageVersion version) {
		this.version = version;
	}
	
	public String getRegion() {
		return region;
	}
	
	public void setRegion(String region) {
		this.region = region;
	}
	
}
