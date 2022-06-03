package com.amtsybex.fieldreach.gf.packages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amtsybex.fieldreach.packages.gfspec.GFSpecPackageVersion;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageInfo;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageMeta;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageMetaDependency;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageMetaRelease;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageVersion;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import io.swagger.annotations.ApiModel;

@ApiModel
public class PackageItem {

	private String id;

	private String name;

	private String region;

	private GFSpecPackageVersion version;

	private List<PackageRelease> releases;
	
    @JacksonXmlProperty(localName = "Dependencies")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "Dependency")
    private List<PackageDependency> dependencies;
    
	public PackageItem() {
		super();
	}

	public PackageItem(String id, String name, String region, GFSpecPackageVersion version, List<PackageRelease> releases,
			List<PackageDependency> dependencies) {
		super();
		this.id = id;
		this.name = name;
		this.region = region;
		this.version = version;
		this.releases = releases;
		this.dependencies = dependencies;
	}

	public PackageItem(List<PackageRelease> packageReleases) throws IllegalArgumentException {
		
        if (packageReleases == null || packageReleases.size() == 0)
            throw new IllegalArgumentException("Package must have at least one release");
        
        PackageRelease firstRelease = packageReleases.get(0);

        this.id = firstRelease.getId();
        this.name = firstRelease.getName();
        this.region = firstRelease.getRegion();
        this.version = firstRelease.getVersion();
        this.dependencies = firstRelease.getDependencies();

        this. releases = packageReleases;

        if (this.releases.size() > 1) {
        	
        	if (this.releases.stream().skip(1).anyMatch(x -> x.getId() != id))
                throw new IllegalArgumentException("Package Releases do not have the same id: " + id);

            if (this.releases.stream().skip(1).anyMatch(x -> !x.getVersion().equals(version)))
                throw new IllegalArgumentException("Package Releases do not have the same version: " + id);

        	
        	if(this.releases.stream().skip(1).anyMatch(x -> !hasSameElements(x.getDependencies(), this.dependencies)))
        		throw new IllegalArgumentException("Package Releases do not have the same dependencies: " + id);
      
        }
        
	}
	
	public PackageInfo toPackageInfo() {
		
        return new PackageInfo(id, name, this.version == null ? null : new PackageVersion(version.getMajorVersion(), version.getMinorVersion()), region);
    }
	
	
    private static boolean hasSameElements(List<PackageDependency> list1,List<PackageDependency> list2) {
    	
    	Map<PackageDependency, Integer>  cnt = new HashMap<PackageDependency, Integer>();
    	
        for (PackageDependency s : list1) {
            if (cnt.containsKey(s))
            	 cnt.put(s, (Integer)cnt.get(s) + 1);
            else
            	cnt.put(s, Integer.valueOf(1));
        }
        for (PackageDependency s : list2) {
        	if (cnt.containsKey(s))
                cnt.put(s, (Integer)cnt.get(s) - 1);
            else
                return false;
        }
        

        return cnt.values().stream().allMatch(c -> c.equals(Integer.valueOf(0)));
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

	public List<PackageRelease> getReleases() {
		return releases;
	}

	public void setReleases(List<PackageRelease> releases) {
		this.releases = releases;
	}

	public List<PackageDependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<PackageDependency> dependencies) {
		this.dependencies = dependencies;
	}

	public PackageMeta toPackageMeta() {
		
		List<PackageMetaRelease> packageMetaReleases = null;
		if(this.getReleases() != null) {
			packageMetaReleases = this.getReleases().stream().map(PackageRelease::toPackageMetaRelease).collect(Collectors.toList());
		}
		
		List<PackageMetaDependency> packageMetaDependencies = null;
		if(this.getDependencies() != null) {
			this.getDependencies().stream().map(PackageDependency::toPackageMetaDependency).collect(Collectors.toList());
		}

		return new PackageMeta(
				this.id,
				this.name,
				this.version == null ? null : new PackageVersion(this.version.getMajorVersion(), this.version.getMinorVersion()),
				this.region,
				packageMetaReleases,
				packageMetaDependencies);
	}
}
