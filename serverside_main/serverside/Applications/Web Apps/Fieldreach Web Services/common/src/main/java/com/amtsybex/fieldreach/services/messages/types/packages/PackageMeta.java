package com.amtsybex.fieldreach.services.messages.types.packages;

import java.util.List;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import io.swagger.annotations.ApiModel;

@ApiModel
public class PackageMeta {
	
    public static final String APPLICATION_VND_FIELDSMART_PACKAGEMETA_1_JSON = "application/vnd.fieldsmart.packagemeta-1+json";
    public static final String APPLICATION_VND_FIELDSMART_PACKAGEMETA_1_XML = "application/vnd.fieldsmart.packagemeta-1+xml";
    
	private String id;

	private String name;

	private PackageVersion version;

    @JacksonXmlProperty(localName = "Releases")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "PackageMetaRelease")
	private List<PackageMetaRelease> releases;
	
	private String region;

    @JacksonXmlProperty(localName = "Dependencies")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "Dependency")
	private List<PackageMetaDependency> dependencies;
	
	public PackageMeta() {
		super();
	}

	public PackageMeta(String id, String name, PackageVersion version, String region,
			List<PackageMetaRelease> releases, List<PackageMetaDependency> dependencies) {
		super();
		this.id = id;
		this.name = name;
		this.version = version;
		this.releases = releases;
		this.region = region;
		this.dependencies = dependencies;
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

	public List<PackageMetaRelease> getReleases() {
		return releases;
	}

	public void setReleases(List<PackageMetaRelease> releases) {
		this.releases = releases;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public List<PackageMetaDependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<PackageMetaDependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	
}
