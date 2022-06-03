package com.amtsybex.fieldreach.packages;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.amtsybex.fieldreach.common.SingletonRegistry;
import com.amtsybex.fieldreach.gf.packages.PackageItem;
import com.amtsybex.fieldreach.gf.packages.PackageRelease;
import com.amtsybex.fieldreach.packages.gfspec.GFSpecPackageVersion;
import com.amtsybex.fieldreach.packages.gfspec.GfSpecMetadata;

/**
 * Singleton in memory array of packages. served by @see com.amtsybex.fieldreach.packages.PackageScanner#
 * Contains functions used by service endpoints to determine packages to return.
 * @author CroninM
 *
 */
@Component
public class PackageIndex {

	private static Logger log = LoggerFactory.getLogger(PackageIndex.class.getName());
	
	private Map<String, PackageRelease> packageReleasesByFullPath;
	private List<PackageItem> packages;
	
	private static final Object OBJ_LOCK = new Object();

	public PackageIndex() {
		super();
		packageReleasesByFullPath = new HashMap<String, PackageRelease>();
		packages = new ArrayList<PackageItem>();
	}

	public PackageIndex(Map<String, PackageRelease> packageReleasesByFullPath, List<PackageItem> packages) {
		super();
		this.packageReleasesByFullPath = packageReleasesByFullPath;
		this.packages = packages;
		
		SingletonRegistry.addInstance(this);
	}

	public void clearPackageIndex() {

		log.debug(">>> ClearPackageIndex");
		
		synchronized(OBJ_LOCK) {
			this.packageReleasesByFullPath.clear();
			this.packages.clear();
		}
		
		log.debug("<<< ClearPackageIndex");

	}

	public void addOrUpdate(File file, GfSpecMetadata metadata) throws Exception {

		log.debug(">>> addOrUpdate");
		
		synchronized(OBJ_LOCK) {
			
			log.debug("Creating new package release");
			
			PackageRelease packageRelease = new PackageRelease(file, metadata);

			PackageItem packageObj = getPackageUnsafe(metadata.getId(), metadata.getVersion());

			List<PackageRelease> packageReleases = new ArrayList<PackageRelease>();

			if(packageObj != null) {
				
				log.debug("Removing existing package");
				
				packages.remove(packageObj);
				
				log.debug("copying releases list");
				
				packageReleases.addAll(packageObj.getReleases());
			}

			log.debug("Adding new package release");
			
			packageReleases.add(packageRelease);

			log.debug("Creating and adding new package to index");
			
			packages.add(new PackageItem(packageReleases));

			packageReleasesByFullPath.put(file.getAbsolutePath(), packageRelease);
		}
		
		log.debug("<<< addOrUpdate");

	}

	public boolean remove(String fullPath) throws Exception {

		log.debug(">>> remove");
		
		boolean retValue = true;
		
		synchronized(OBJ_LOCK) {
			if(!packageReleasesByFullPath.containsKey(fullPath)) {
				
				retValue = false;
			}else {
				
				log.debug("retreiving package to remove");
				
				PackageRelease packageRelease = packageReleasesByFullPath.get(fullPath);

				if(packageRelease != null) {
					
					log.debug(packageRelease.getId() + " " + packageRelease.getVersion());
					//get the package this release belongs to
					PackageItem packageObj = getPackageUnsafe(packageRelease.getId(), packageRelease.getVersion());

					if(packageObj != null) {
						packages.remove(packageObj);
						log.debug("Package removed from index");
					}else {
						log.warn("No corresponding package to remove");
					}

					//if there's still another release for this package, add back the package without the release just removed.
					if(packageObj.getReleases() != null && packageObj.getReleases().size() > 1) {
						
						log.debug("Adding package releases");
						
						packageObj.getReleases().remove(packageRelease);
						packages.add(new PackageItem(packageObj.getReleases()));
					}

					packageReleasesByFullPath.remove(fullPath);
					
					log.debug("Package removed from lookup");
					
				}else {
					log.warn("Package not found to remove " + fullPath);
				}
				
			}
			
		}

		log.debug("<<< remove : " + retValue);
		
		return true;

	}

	public List<PackageItem> getAllPackagesSlow(){
		
		log.debug(">>> getAllPackagesSlow");
		
		synchronized(OBJ_LOCK) {
			return new ArrayList<PackageItem>(packages);
		}
		
	}

	public List<PackageItem> getPackages(String theid)
	{
		log.debug(">>> getPackages");
		
		Comparator<PackageItem> versionComparator = (p1, p2) -> p1.getVersion().compareTo(p2.getVersion());

		synchronized(OBJ_LOCK) {
			return packages.stream().filter(p -> p.getId().equals(theid)).sorted(versionComparator.reversed()).collect(Collectors.toList());
		}

	}

	public PackageItem getBestPackage(String id, Integer major)
	{
		log.debug(">>> getBestPackage");
		Comparator<PackageItem> versionComparator = (p1, p2) -> p1.getVersion().compareTo(p2.getVersion());

		synchronized(OBJ_LOCK) {
			Optional<PackageItem> returns = packages.stream().filter(p -> p.getId().equals(id) && p.getVersion().getMajorVersion() == major).sorted(versionComparator.reversed()).findFirst();
			if(returns.isPresent()) {
				return returns.get();
			}
			return null;
		}

	}

	public PackageItem getPackage(String id, GFSpecPackageVersion version) {
		
		log.debug(">>> getPackage");
		
		synchronized(OBJ_LOCK) {
			return getPackageUnsafe(id, version);
		}
	}

	public void setPackageReleasesByFullPath(Map<String, PackageRelease> packageReleasesByFullPath) {
		
		log.debug(">>> setPackageReleasesByFullPath");
		
		synchronized(OBJ_LOCK) {
			this.packageReleasesByFullPath = packageReleasesByFullPath;
		}
		
		log.debug("<<< setPackageReleasesByFullPath");
	}


	public void setPackages(List<PackageItem> packages) {
		
		log.debug(">>> setPackages");
		
		synchronized(OBJ_LOCK) {
			this.packages = packages;
		}
		
		log.debug("<<< setPackages");
	}


	private PackageItem getPackageUnsafe(String id, GFSpecPackageVersion version) {
		
		log.debug(">>> getPackageUnsafe");
		
		PackageItem result = null;
		
		for (PackageItem p: packages) {
			if (p.getId().equals(id) && p.getVersion().equals(version)) {

				return p;
			}
		}

		if (result == null) {
			log.debug("<<< getPackageUnsafe : null");
		}
		
		return result;
	}
	
}
