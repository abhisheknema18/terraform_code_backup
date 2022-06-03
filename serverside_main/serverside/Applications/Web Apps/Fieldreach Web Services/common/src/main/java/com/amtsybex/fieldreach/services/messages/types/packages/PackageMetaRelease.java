package com.amtsybex.fieldreach.services.messages.types.packages;

import java.text.SimpleDateFormat;
import java.util.Date;


import io.swagger.annotations.ApiModel;

@ApiModel
public class PackageMetaRelease {

	private PackageType type;

	private Date published;

	private Date expires;

	private long downloadSize;

	private long packageSize;

	public PackageMetaRelease() {
		super();
	}

	public PackageMetaRelease(PackageType type, Date published, long downloadSize, long packageSize, Date expires) {
		super();
		this.type = type;
		this.published = published;
		this.downloadSize = downloadSize;
		this.packageSize = packageSize;
		this.expires = expires;
	}

	public PackageType getType() {
		return type;
	}

	public void setType(PackageType type) {
		this.type = type;
	}

	/*
	public Date getPublished() {
		return published;
	}*/
	
	public String getPublished() {
		if(published == null) {
			return "0001-01-01T00:00:00";
		}
		
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(published);
	}

	public void setPublished(Date published) {
		this.published = published;
	}

	public String getExpires() {
		if(expires == null) {
			return null;
		}
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(expires);
	}

	/*
	public Date getExpires() {
		return expires;
	}*/

	public void setExpires(Date expires) {
		this.expires = expires;
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
	
	
	
	
}
