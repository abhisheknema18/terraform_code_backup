package com.amtsybex.fieldreach.packages.gfspec;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.amtsybex.fieldreach.services.messages.types.packages.PackageType;


@XmlAccessorType(XmlAccessType.FIELD)
public class GfSpecMetadata {

	@XmlElement (name="id")
    private String id;

    @XmlElement (name="name")
    private String name;

    @XmlElement (name="version")
    private GFSpecPackageVersion version;
    
    @XmlElement (name="type")
    private PackageType type;
    
    @XmlElement (name="published")
    private Date published;

    @XmlElement (name="region")
    private String region;

    @XmlElement (name="expires")
    private Date expires;

    @XmlElementWrapper(name="dependencies")
    @XmlElement (name="dependency")
    private List<GfSpecDependencies> dependencies;

	public GfSpecMetadata() {
		super();
	}

	public GfSpecMetadata(String id, String name, GFSpecPackageVersion version, PackageType type, Date published,
			String region, Date expires, List<GfSpecDependencies> dependencies) {
		super();
		this.id = id;
		this.name = name;
		this.version = version;
		this.type = type;
		this.published = published;
		this.region = region;
		this.expires = expires;
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

	public GFSpecPackageVersion getVersion() {
		return version;
	}

	public void setVersion(GFSpecPackageVersion version) {
		this.version = version;
	}

	public PackageType getType() {
		return type;
	}

	public void setType(PackageType type) {
		this.type = type;
	}

	public Date getPublished() {
		return published;
	}

	public void setPublished(Date published) {
		this.published = published;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public List<GfSpecDependencies> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<GfSpecDependencies> dependencies) {
		this.dependencies = dependencies;
	}

}
