package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.PackageInfoCollection.APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.PackageInfoCollection.APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.PackageUpdate.APPLICATION_VND_FIELDSMART_PACKAGEUPDATE_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.PackageUpdate.APPLICATION_VND_FIELDSMART_PACKAGEUPDATE_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_JSON;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_XML;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.amtsybex.fieldreach.gf.packages.PackageDependency;
import com.amtsybex.fieldreach.gf.packages.PackageItem;
import com.amtsybex.fieldreach.gf.packages.PackageRelease;
import com.amtsybex.fieldreach.packages.PackageIndex;
import com.amtsybex.fieldreach.packages.gfspec.GFSpecPackageVersion;
import com.amtsybex.fieldreach.services.endpoint.rest.RestPackagesController;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.exception.PackageException;
import com.amtsybex.fieldreach.services.messages.response.PackageInfoCollection;
import com.amtsybex.fieldreach.services.messages.response.PackageUpdate;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageInfo;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageType;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageUpdateStep;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageVersion;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api(tags = "Package Sync")
public class RestPackagesControllerImpl extends BaseControllerImpl implements RestPackagesController {

	private static Logger log = LoggerFactory.getLogger(RestPackagesControllerImpl.class.getName());

	@Autowired
	private PackageIndex packageIndex;

	@Override
	@GetMapping(value = "packages", produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_JSON, APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_XML})
	@ApiOperation(value = "Get All Packages", notes = "Returns a full list of available packages, set all to true to return all versions")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include PackageException, IOException"),
	})
	public ResponseEntity<PackageInfoCollection> getAll(HttpServletRequest request, @RequestParam(value = "all", required=false, defaultValue="false") Boolean all)
			throws PackageException, BadRequestException, IOException {

		log.debug(">>> getAll");

		return this.getAllById(request, null, all);

	}

	@Override
	@GetMapping(value = "packages/{id}", produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_JSON, APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_XML})
	@ApiOperation(value = "Get Packages by id", notes = "Returns a list of available packages for the given id, set all to true to return all versions")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include PackageException, IOException"),
	})
	public ResponseEntity<PackageInfoCollection> getAllById(HttpServletRequest request, @PathVariable String id, @RequestParam(value = "all", required=false, defaultValue="false") Boolean all)
			throws PackageException, BadRequestException, IOException {

		log.debug(">>> getAllById");

		log.info(String.format("Getting packages for %s. all=%s", id, all));

		PackageInfoCollection result = null;

		List<PackageItem> packages = null;

		if(id == null) {
			packages = packageIndex.getAllPackagesSlow();

			Comparator<PackageItem> idComparitor = (p1, p2) -> p1.getId().compareTo(p2.getId());
			Comparator<PackageItem> versionComparator = (p1, p2) -> p1.getVersion().compareTo(p2.getVersion());
			Comparator<PackageItem> comparitor = idComparitor.thenComparing(versionComparator.reversed());

			packages.sort(comparitor);

		}else {
			packages = packageIndex.getPackages(id);
		}

		List<PackageInfo> packagesToReturn = new ArrayList<PackageInfo>();

		if(packages != null) {
			
			for(PackageItem candidate: packages) {

				if (all || !packagesToReturn.stream().anyMatch(p -> p.getId().equals(candidate.getId()) && p.getVersion().getMajorVersion().equals(candidate.getVersion().getMajorVersion()))) {
					packagesToReturn.add(candidate.toPackageInfo());
				}

			}
		}


		log.info(String.format("Returning %d packages", packagesToReturn.size()));

			result = new PackageInfoCollection(packagesToReturn);
		
		log.debug("<<< getAllById");

		return ResponseEntity.ok(result);
	}

	
	@GetMapping(value = "packages/{id}/{major}/{minor}/update", produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_PACKAGEUPDATE_1_JSON, APPLICATION_VND_FIELDSMART_PACKAGEUPDATE_1_XML})
	@ApiOperation(value = "Get Update Path for Package", notes = "Returns the best update path for the given package id from the given version to the required version")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include PackageException, IOException"),
			@ApiResponse(code = 400, message = "Bad Request - if the target package does not exist."),
	})
	public ResponseEntity<PackageUpdate> getPackageUpdate(HttpServletRequest request, @PathVariable String id, @PathVariable Integer major, @PathVariable Integer minor, @RequestParam(value = "from", required=false) String from)
			throws PackageException, IOException, PackageException {

		PackageUpdate result = null;
		
		GFSpecPackageVersion fromVersion = null;
		if(from != null) {

			fromVersion = GFSpecPackageVersion.parse(from);
			if(fromVersion == null) {
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "from version not properly formatted");
			}

		}

		log.info(String.format("Getting package update plan for {0}-{1}.{2} from {3}", id, major, minor, from));

		// If it's really a downgrade, turn it into an update from nothing
		if (fromVersion != null &&
			major < fromVersion.getMajorVersion()) {

			fromVersion = null;
		}

		PackageItem startPackage = fromVersion == null ? null : packageIndex.getPackage(id, fromVersion);
		PackageItem targetPackage = packageIndex.getPackage(id, new GFSpecPackageVersion(major, minor));
		List<PackageItem> allMatchingPackages = packageIndex.getPackages(id);

		List<PackageUpdateStep> updateSteps = new ArrayList<PackageUpdateStep>();

		if (targetPackage != null) {

			this.bestUpdatePath(allMatchingPackages, startPackage, targetPackage, updateSteps);
		}
		
		if(targetPackage != null) {
			
			PackageVersion version = null;
			if(targetPackage.getVersion() != null) {
				version = new PackageVersion(targetPackage.getVersion().getMajorVersion(), targetPackage.getVersion().getMinorVersion());
			}
			
			result = new PackageUpdate(id, version, updateSteps, targetPackage.getDependencies().stream().map(PackageDependency::toPackageMetaDependency).collect(Collectors.toList()));
		}else {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST);
		}

		
		log.debug("<<< getPackageUpdate");

		return ResponseEntity.ok(result);
		
	}
	
	
	@Override
	@GetMapping(value = "packages/{id}/{major}/{minor}/{type}", produces= { MediaType.APPLICATION_OCTET_STREAM_VALUE } )
	@ApiOperation(value = "Download Package Data", notes = "Service used to download package byte data")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include PackageException, IOException"),
			@ApiResponse(code = 400, message = "Bad Request - if the matching package is not found."),
	})
	public ResponseEntity<Resource> getData(
			HttpServletRequest request,
			@PathVariable("id") String id,
			@PathVariable("major") Integer major,
			@PathVariable("minor") Integer minor,
			@PathVariable("type") PackageType type,
			@RequestParam(value = "start", required=false) Long start) {

		log.debug(">>> getData");

		log.info(String.format("Getting package data for %s-%d.%d of type %s starting at %d", id, major, minor, type, start));

		PackageItem matchingPackage = packageIndex.getPackage(id, new GFSpecPackageVersion(major, minor));
		
		if (matchingPackage == null) {
			return ResponseEntity.notFound().build();
		}

		PackageRelease packageRelease = matchingPackage.getReleases().stream().filter(p -> p.getType() == type).findFirst().orElse(null);

		if (packageRelease == null) {
			return ResponseEntity.notFound().build();
		}

		Path packageFilePath = Paths.get(packageRelease.getFullPath());
		InputStream fileInputStream;
		try {
			fileInputStream = Files.newInputStream(packageFilePath, StandardOpenOption.READ);

			if (start != null && start != 0) {
				fileInputStream.skip(start);
			}

			long fileLengthInBytes;
			fileLengthInBytes = Files.size(packageFilePath);

			InputStreamResource fileStreamResource = new InputStreamResource(fileInputStream);
			
			log.debug("<<< getData");

			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.contentLength(fileLengthInBytes)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + packageFilePath.getFileName() + "\"")
					.body(fileStreamResource);
			
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} 

	}

	/**
	 * Calculate the best update path to get from fromPackage to toPackage. It is assumed all packages passed into this
	 * function relate to the same package ID.
	 * 
	 * @param allPackages All potential packages that could be associated with this update
	 * @param fromPackage Where the update is starting from. If null, we're starting from the beginning
	 * @param toPackage Where the update should ultimately end up.
	 * @param updateSteps The update steps that are filled in to get from fromPackage to toPackage
	 * @return The cost of taking the update steps
	 * @throws PackageException 
	 */
	private long bestUpdatePath(List<PackageItem> allPackages, PackageItem fromPackage, PackageItem toPackage, List<PackageUpdateStep> updateSteps) throws PackageException
	{
		//if the package versions are the same we are done
		if (fromPackage != null && fromPackage.getVersion().equals(toPackage.getVersion())) {

			return 0;
		}

		//we solve this problem using recursion.  The best update path from fromPackage to toPackage is the best update path
		//from fromPacakge to whatever package precedes the toPackage plus the cost to update to toPackage.  So, we need to 
		//figure out the package that precedes the toPackage.  Well, that's the package with the highest minor version.
		
		PackageItem prevPackage = allPackages.stream().filter(p -> p.getId().equals(toPackage.getId()) && p.getVersion().compareTo(toPackage.getVersion()) < 0).findFirst().orElse(null);
		
		GFSpecPackageVersion prevPackageVersion = null;
		if(prevPackage != null) {
			prevPackageVersion = allPackages.stream().filter(p -> p.getId().equals(toPackage.getId()) && p.getVersion().compareTo(toPackage.getVersion()) < 0).max((p1, p2) -> p1.getVersion().compareTo(p2.getVersion())).get().getVersion();
		}
		//PackageVersion prevPackageVersion = prevPackage == null ? null : prevPackage.getVersion();
		
		//We iterate through all releases available within the toPackage and find the best one.
		PackageRelease bestRelease = null;
		List<PackageUpdateStep> bestStepsToGetToPrev = null;
		long bestReleaseCost = 0;

		for (PackageRelease release: toPackage.getReleases()) {

			long releaseCost = -1;
			List<PackageUpdateStep> stepsToGetToPrev = new ArrayList<PackageUpdateStep>();

			//if the release is full we need not check the previous package, the cost of getting to the toPackage with
			//a full release is simply the cost to download the full release
			if (release.getType() == PackageType.Full) {
				
				releaseCost = release.getDownloadSize();
				
			} else if (release.getType() == PackageType.Delta && prevPackageVersion != null) {
				//if the release is a delta this is where recursion kicks in, let's use that prevPackage stuff we calculated above.
				
				long costToGetToPrev = bestUpdatePath(allPackages, fromPackage, prevPackage, stepsToGetToPrev);
				releaseCost = release.getDownloadSize() + costToGetToPrev;
			}

			//full or valid delta and the cost of this release is the best we've seen. Make it our new best!
			if (releaseCost != -1 && (bestRelease == null || releaseCost < bestReleaseCost)) {
				
				bestRelease = release;
				bestReleaseCost = releaseCost;
				bestStepsToGetToPrev = stepsToGetToPrev;
			}
		}

		if (bestRelease == null && prevPackage == null)  {
			return 0;
		}
		else if(bestRelease == null) {
			throw new PackageException("Could not find a valid release for " + toPackage.getId() + "-" + toPackage.getVersion());
		}

		if (bestStepsToGetToPrev != null &&
				!bestStepsToGetToPrev.isEmpty()) {

			updateSteps.addAll(bestStepsToGetToPrev);
		}

		PackageVersion version = null;
		if(bestRelease.getVersion() != null) {
			version = new PackageVersion(bestRelease.getVersion().getMajorVersion(), bestRelease.getVersion().getMinorVersion());
		}
		updateSteps.add(new PackageUpdateStep(version, bestRelease.getType(), bestRelease.getPublishedDateObject(), bestRelease.getDownloadSize(), bestRelease.getPackageSize()));

		return bestReleaseCost;
	}


	/*
	 * BELOW ENDPOINTS WERE CARRIED FORWARD FROM GEOFIELD BUT DEEMED SURPLUS TO REQUIREMENT. LEAVING THEM COMMENT OUT HERE FOR A SHORT TIME
	 * INCASE THEY ARE RESCOPED. 
	 */
	/*
	@Override
	@GetMapping(value = "packages/{id}")
	@ResponseBody
	private PackageInfoCollection getAllById(HttpServletRequest request, @PathVariable String id, @RequestParam(value = "all", required=false, defaultValue="false") Boolean all)
			throws PackageException, BadRequestException, IOException {

		log.info(String.format("Getting packages for {0}. all={1}", id, all));

		List<Package> matchingOrderedPackages = packageIndex.getAllPackagesSlow();

		// Order by id ascending, then by version descending.
		Collections.sort(matchingOrderedPackages, new Comparator<Package>() {
			@Override
			public int compare(Package left, Package right) {
				// -1 - less than, 1 - greater than, 0 - equal		   	
				int idComparison = left.getId().compareTo(right.getId());
				if (idComparison != 0) {
					return idComparison;					
				}

				int versionComparison = left.getVersion().compareTo(right.getVersion());
				return versionComparison * -1;
			}
		});

		if (id != null) {
			class EqualsId implements Predicate<Package> {

				private final String id;

				public EqualsId(String id) {
					this.id = id;
				}

				@Override
				public boolean evaluate(Package item) {
					return item.getId().equals(this.id);
				}
			}
			CollectionUtils.filter(matchingOrderedPackages, new EqualsId(id));
		}


		List<PackageInfo> packagesToReturn = new ArrayList<PackageInfo>();
		for (Package p: matchingOrderedPackages)
		{
			class EqualsIdAndMajorVersion implements Predicate<PackageInfo> {

				private final String id;
				private final int majorVersion;

				public EqualsIdAndMajorVersion(
						String id,
						int majorVersion) {

					this.id = id;
					this.majorVersion = majorVersion;
				}

				@Override
				public boolean evaluate(PackageInfo item) {
					return item.getId().equals(this.id) &&
							item.getVersion().getMajorVersion() == this.majorVersion;
				}
			}

			if (all || !IterableUtils.matchesAny(packagesToReturn,  new EqualsIdAndMajorVersion(p.getId(), p.getVersion().getMajorVersion()))) {
				packagesToReturn.add(p.toPackageInfo());
			}
		}

		log.info(String.format("Returning {0} packages", packagesToReturn.size()));

		PackageInfoCollection result = new PackageInfoCollection();
		result.setPackages(packagesToReturn);

		return result;
	}*/

	/*
	@Override
	@GetMapping(value = "packages/{id}/{major}", produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FUSION_PACKAGEMETA_1_JSON, APPLICATION_VND_FUSION_PACKAGEMETA_1_XML})
	@ResponseBody
	public PackageMeta getPackageByVersion(HttpServletRequest request, @PathVariable String id, @PathVariable Integer major)
			throws PackageException, BadRequestException, IOException {

		log.info(String.format("Getting best package for {0}-{1}", id, major));

		Package bestPackage = packageIndex.GetBestPackage(id, major);

		return bestPackage.toPackageMeta();
	}


	@Override
	@GetMapping(value = "packages/{id}/{major}/{minor}", produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FUSION_PACKAGEMETA_1_JSON, APPLICATION_VND_FUSION_PACKAGEMETA_1_XML})
	@ResponseBody
	public PackageMeta getPackageByVersion(HttpServletRequest request, @PathVariable String id, @PathVariable Integer major, @PathVariable Integer minor)
			throws PackageException, BadRequestException, IOException {

		log.info(String.format("Getting package {0}-{1}.{2}", id, major, minor));

		Package getPackage = packageIndex.GetPackage(id, new PackageVersion(major, minor));

		return getPackage.toPackageMeta();
	}*/

	/*
	@Override
	@GetMapping(value = "packages/{id}/{major}/update", produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FUSION_PACKAGEUPDATE_1_JSON, APPLICATION_VND_FUSION_PACKAGEUPDATE_1_XML})
	@ApiOperation(value = "Get Update Path for Package", notes = "Returns the best update path for the given package id from the given version to the required version")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include PackageException, IOException"),
			@ApiResponse(code = 400, message = "Bad Request - if the target package does not exist."),
	})
	public ResponseEntity<PackageUpdate> getPackageUpdate(HttpServletRequest request, @PathVariable String id, @PathVariable Integer major, @RequestParam(value = "from", required=false) String from)
			throws PackageException, BadRequestException, IOException {


		log.debug(">>> getPackageUpdate");

		PackageUpdate result = null;

		GFSpecPackageVersion fromVersion = null;
		if(from != null) {

			fromVersion = GFSpecPackageVersion.parse(from);
			if(fromVersion == null) {
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "from version not properly formatted");
			}

		}

		log.info(String.format("Getting package update plan for %s-%d from %s", id, major, fromVersion));

		// If it's really a downgrade, turn it into an update from nothing
		if (fromVersion != null && major < fromVersion.getMajorVersion()) {
			fromVersion = null;
		}

		PackageItem startPackage = fromVersion == null ? null : packageIndex.getPackage(id, fromVersion);
		PackageItem targetPackage = packageIndex.getBestPackage(id, major);
		List<PackageItem> allMatchingPackages = packageIndex.getPackages(id);

		List<PackageUpdateStep> updateSteps = new ArrayList<PackageUpdateStep>();

		if (targetPackage != null) {

			this.bestUpdatePath(allMatchingPackages, startPackage, targetPackage, updateSteps);
		}
		
		if(targetPackage != null) {
			
			PackageVersion version = null;
			if(targetPackage.getVersion() != null) {
				version = new PackageVersion(targetPackage.getVersion().getMajorVersion(), targetPackage.getVersion().getMinorVersion());
			}
			
			result = new PackageUpdate(id, version, updateSteps, targetPackage.getDependencies().stream().map(PackageDependency::toPackageMetaDependency).collect(Collectors.toList()));
		}else {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST);
		}

		
		log.debug("<<< getPackageUpdate");

		return ResponseEntity.ok(result);
	}*/



}
