package com.amtsybex.fieldreach.services.messages.types.packages;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;


@XmlAccessorType(XmlAccessType.FIELD)
public class PackageVersion {

	@XmlTransient
	@JsonIgnore
	private Integer majorVersion;
	
	@XmlTransient
	@JsonIgnore
	private Integer minorVersion;
	
	public PackageVersion() {
		super();
	}
	
	public PackageVersion(Integer majorVersion, Integer minorVersion) {
		super();
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}
	
	public PackageVersion(String version) {
		super();
		PackageVersion v = PackageVersion.parse(version);
		if(v != null) {
			this.majorVersion = v.majorVersion;
			this.minorVersion = v.minorVersion;
		}
	}
	
	public static PackageVersion parse(String version) {
		
		PackageVersion packageVersion = null;
		if(version != null) {
			if(version.contains(".")) {
				String[] versions = version.split("[.]");
				if(versions != null && versions.length == 2) {
					try {
						Integer majorV = Integer.parseInt(versions[0]);
						Integer minorV = Integer.parseInt(versions[1]);
						packageVersion = new PackageVersion(majorV, minorV);
					}catch(NumberFormatException e) {
						return null;
					}
				}else {
					return null;
				}
			}else {
				return null;
			}
		}
		return packageVersion;
	}
	
	@XmlValue
	@JsonValue
	public String getVersion() {
		return majorVersion + "." + minorVersion;
	}
	
	public void setVersion(String version) {
		PackageVersion v = PackageVersion.parse(version);
		if(v != null) {
			this.majorVersion = v.majorVersion;
			this.minorVersion = v.minorVersion;
		}
	}

	public Integer getMajorVersion() {
		return majorVersion;
	}
	
	public void setMajorVersion(Integer majorVersion) {
		this.majorVersion = majorVersion;
	}
	
	public Integer getMinorVersion() {
		return minorVersion;
	}
	
	public void setMinorVersion(Integer minorVersion) {
		this.minorVersion = minorVersion;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + majorVersion;
		result = prime * result + minorVersion;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PackageVersion other = (PackageVersion) obj;
		if (majorVersion != other.majorVersion)
			return false;
		if (minorVersion != other.minorVersion)
			return false;
		return true;
	}
}
