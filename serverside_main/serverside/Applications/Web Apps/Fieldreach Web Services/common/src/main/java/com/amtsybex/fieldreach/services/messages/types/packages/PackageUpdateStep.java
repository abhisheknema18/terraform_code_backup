package com.amtsybex.fieldreach.services.messages.types.packages;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel
public class PackageUpdateStep {

	PackageVersion version;
	
	PackageType type;

	Date published;

	long downloadSize;

	long packageSize;

	public PackageUpdateStep() {
		super();
	}

	public PackageUpdateStep(PackageVersion version, PackageType type, Date published, long downloadSize,
			long packageSize) {
		super();
		this.version = version;
		this.type = type;
		this.published = published;
		this.downloadSize = downloadSize;
		this.packageSize = packageSize;
	}

	public PackageVersion getVersion() {
		return version;
	}

	public void setVersion(PackageVersion version) {
		this.version = version;
	}

	public PackageType getType() {
		return type;
	}

	public void setType(PackageType type) {
		this.type = type;
	}

	public void setPublished(Date published) {
		this.published = published;
	}

	public long getDownloadSize() {
		return downloadSize;
	}

	public void setDownloadSize(long downloadSize) {
		this.downloadSize = downloadSize;
	}

	public long getPackageSize() {
		return packageSize;
	}

	public void setPackageSize(long packageSize) {
		this.packageSize = packageSize;
	}

	@JsonProperty("Published")
	public String getPublished() {
		if(published == null) {
			return "0001-01-01T00:00:00";
		}
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(published);
	}
	
}
