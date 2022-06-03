package com.amtsybex.fieldreach.gf.packages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.amtsybex.fieldreach.packages.gfspec.GFSpecPackageVersion;
import com.amtsybex.fieldreach.packages.gfspec.GfSpecMetadata;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageMetaRelease;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PackageRelease {

	private String id;

	private String name;

	private String region;

	private GFSpecPackageVersion version;

	private PackageType type;

	private String fullPath;

	private Date published;

	private Date expires;

	private long downloadSize;

	private long packageSize;

	@JacksonXmlProperty(localName = "Dependencies")
	@JacksonXmlElementWrapper(useWrapping = true, localName = "Dependency")
	private List<PackageDependency> dependencies;

	public PackageRelease() {
		super();
	}

	public PackageRelease(String id, String name, String region, GFSpecPackageVersion version, PackageType type,
			String fullPath, Date published, Date expires, long downloadSize, long packageSize,
			List<PackageDependency> dependencies) {

		super();

		this.id = id;
		this.name = name;
		this.region = region;
		this.version = version;
		this.type = type;
		this.fullPath = fullPath;
		this.published = published;
		this.expires = expires;
		this.downloadSize = downloadSize;
		this.packageSize = packageSize;
		this.dependencies = dependencies;
	}

	public PackageRelease(File file, GfSpecMetadata metadata) throws ParseException, ZipException, IOException {

		if (metadata.getId() == null)
			throw new ParseException("Id", 0);
		if (metadata.getVersion() == null)
			throw new ParseException("Version", 0);

		this.id = metadata.getId();
		this.name = metadata.getName() != null ? metadata.getName() : metadata.getId();
		this.region = metadata.getRegion();
		this.version = metadata.getVersion();
		this.type = metadata.getType();
		this.fullPath = file.getAbsolutePath();
		this.published = metadata.getPublished();
		this.expires = metadata.getExpires();
		this.downloadSize = file.length();

		if(metadata.getDependencies() == null) {
			this.dependencies = new ArrayList<PackageDependency>();
		}else {
			this.dependencies = metadata.getDependencies().stream().map(x -> new PackageDependency(x.getId(), x.getVersion())).collect(Collectors.toList());
		}

		long uncompressedSize = 0;
		try (ZipFile zip = new ZipFile(Paths.get(file.getAbsolutePath()).toFile())) {
			
			Enumeration<? extends ZipEntry> entries = zip.entries();

			while(entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				uncompressedSize += zipEntry.getSize();
			}
		}
		this.packageSize = uncompressedSize;

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

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
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
	public String getFullPath() {
		return fullPath;
	}
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
	public void setPublished(Date published) {
		this.published = published;
	}
	@JsonProperty("published")
	public String getPublished() {
		if(published == null) {
			return "0001-01-01T00:00:00";
		}
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(published);
	}

	public Date getPublishedDateObject() {
		return this.published;
	}

	@JsonProperty("expires")
	public String getExpires() {
		if(expires == null) {
			return null;
		}
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(expires);
	}
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
	public List<PackageDependency> getDependencies() {
		return dependencies;
	}
	public void setDependencies(List<PackageDependency> dependencies) {
		this.dependencies = dependencies;
	}

	public PackageMetaRelease toPackageMetaRelease()
	{
		return new PackageMetaRelease(
				this.getType(),
				this.published,
				this.getDownloadSize(),
				this.getPackageSize(),
				this.expires);
	}
}
