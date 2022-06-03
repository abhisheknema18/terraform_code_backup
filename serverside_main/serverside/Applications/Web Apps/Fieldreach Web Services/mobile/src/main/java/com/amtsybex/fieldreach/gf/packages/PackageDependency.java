package com.amtsybex.fieldreach.gf.packages;

import com.amtsybex.fieldreach.services.messages.types.packages.PackageMetaDependency;

import io.swagger.annotations.ApiModel;

@ApiModel
public class PackageDependency implements Comparable<PackageDependency> {

	private String id;

	private Integer version;
	
	public PackageDependency() {
		super();
	}
	
	public PackageDependency(String id, Integer version) {
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
	
	public PackageMetaDependency toPackageMetaDependency()
	{
		return new PackageMetaDependency(
				this.getId(),
				this.getVersion());
	}
	
	@Override
	public int compareTo(PackageDependency o) {
        
		if (this.equals(o))
            return 1;

        var compareResult = id.compareTo(o.id);

        if (compareResult == 0)
            compareResult = version.compareTo(o.getVersion());

        return compareResult;
	}
	
}
