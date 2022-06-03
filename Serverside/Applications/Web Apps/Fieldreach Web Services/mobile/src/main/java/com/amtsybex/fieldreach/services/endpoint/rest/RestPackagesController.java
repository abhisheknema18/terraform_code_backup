package com.amtsybex.fieldreach.services.endpoint.rest;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.exception.PackageException;
import com.amtsybex.fieldreach.services.messages.response.PackageInfoCollection;
import com.amtsybex.fieldreach.services.messages.response.PackageUpdate;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageType;


public interface RestPackagesController {
	 
	/**
	 * Retrieve a full list of available packages, boolean all includes all versions
	 * @param request
	 * @param all
	 * @return
	 * @throws PackageException
	 * @throws BadRequestException
	 * @throws IOException
	 */
	public ResponseEntity<PackageInfoCollection> getAll(HttpServletRequest request, Boolean all)
			throws PackageException, BadRequestException, IOException;
	
	/**
	 * Retrieve package info for the given id, boolean all includes all versions
	 * @param request
	 * @param id
	 * @param all
	 * @return
	 * @throws PackageException
	 * @throws BadRequestException
	 * @throws IOException
	 */
	public ResponseEntity<PackageInfoCollection> getAllById(HttpServletRequest request, String id, Boolean all)
			throws PackageException, BadRequestException, IOException;
	
	/**
	 * Get package update path from package to package version
	 * @param request
	 * @param id
	 * @param major
	 * @param from
	 * @return
	 * @throws PackageException
	 * @throws BadRequestException
	 * @throws IOException
	 */
	/*public ResponseEntity<PackageUpdate> getPackageUpdate(HttpServletRequest request,String id,Integer major, String from) 
			throws PackageException, BadRequestException, IOException;*/
	
	/**
	 * Get package update path from package to package version
	 * @param request
	 * @param id
	 * @param major
	 * @param minor
	 * @param from
	 * @return
	 * @throws PackageException
	 * @throws BadRequestException
	 * @throws IOException
	 * @throws PackageException
	 */
	public ResponseEntity<PackageUpdate> getPackageUpdate(HttpServletRequest request, @PathVariable String id, @PathVariable Integer major, @PathVariable Integer minor, @RequestParam(value = "from", required=false) String from)
			throws PackageException, IOException, PackageException;
	/**
	 * 
	 * @param request
	 * @param id
	 * @param major
	 * @param minor
	 * @param type
	 * @param start
	 * @return
	 * @throws PackageException
	 * @throws BadRequestException
	 * @throws IOException
	 */
	public ResponseEntity<Resource> getData(HttpServletRequest request, String id, Integer major, Integer minor, PackageType type, Long start);

	
	/*
	 * BELOW ENDPOINTS WERE CARRIED FORWARD FROM GEOFIELD BUT DEEMED SURPLUS TO REQUIREMENT. LEAVING THEM COMMENT OUT HERE FOR A SHORT TIME
	 * INCASE THEY ARE RESCOPED. 
	 */
	
	/*

	public PackageMeta getPackageByVersion(HttpServletRequest request, String id, Integer major)
			throws PackageException, BadRequestException, IOException;
	
	public PackageMeta getPackageByVersion(HttpServletRequest request,String id,Integer major,Integer minor)
			throws PackageException, BadRequestException, IOException;
		
	public PackageUpdate getPackageUpdate(HttpServletRequest request,String id,Integer major,Integer minor, String from)
			throws PackageException, BadRequestException, IOException;
	*/
}
