package com.amtsybex.fieldreach.packages.gfspec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class GFSpecPackageVersion implements Comparable<GFSpecPackageVersion>{

	@XmlTransient
	@JsonIgnore
	private int majorVersion;

	@XmlTransient
	@JsonIgnore
	private int minorVersion;

	public GFSpecPackageVersion() {
		super();
	}

	public GFSpecPackageVersion(int majorVersion, int minorVersion) {
		super();
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}

	public static GFSpecPackageVersion parse(String version) {

		GFSpecPackageVersion packageVersion = null;
		if(version != null) {
			if(version.contains(".")) {
				String[] versions = version.split("[.]");
				if(versions != null && versions.length == 2) {
					try {
						Integer majorV = Integer.parseInt(versions[0]);
						Integer minorV = Integer.parseInt(versions[1]);
						packageVersion = new GFSpecPackageVersion(majorV, minorV);
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
		GFSpecPackageVersion v = GFSpecPackageVersion.parse(version);
		if(v != null) {
			this.majorVersion = v.majorVersion;
			this.minorVersion = v.minorVersion;
		}
	}

	public void setVersionString(String versionString) {
		if(StringUtils.isEmpty(versionString) || !versionString.contains(".")) {
			this.majorVersion = 0;
			this.minorVersion = 0;
		}else{
			this.majorVersion = Integer.valueOf(versionString.split(".")[0]);
			this.minorVersion = Integer.valueOf(versionString.split(".")[1]);
		}
		this.majorVersion = Integer.valueOf(versionString.split(".")[0]);
		this.minorVersion = Integer.valueOf(versionString.split(".")[1]);
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}
	
	@Override
	public String toString() {
		return this.majorVersion + "." + this.getMinorVersion();
	}

	@Override
	public int compareTo(GFSpecPackageVersion other) {
		if (other == null) {
			return 1;
		}
		
		Integer majorComparison = Integer.compare(this.majorVersion, other.majorVersion);
		
		if (majorComparison != 0) {
			return majorComparison;
		}
		
		Integer minorComparison = Integer.compare(this.minorVersion,  other.minorVersion);
		
		return minorComparison; 
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
		GFSpecPackageVersion other = (GFSpecPackageVersion) obj;
		if (majorVersion != other.majorVersion)
			return false;
		if (minorVersion != other.minorVersion)
			return false;
		return true;
	}
}