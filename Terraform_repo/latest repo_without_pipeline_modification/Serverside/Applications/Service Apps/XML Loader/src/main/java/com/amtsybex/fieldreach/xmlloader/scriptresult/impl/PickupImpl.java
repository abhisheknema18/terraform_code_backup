/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	30/07/2014
 * 
 * Modified by T Murray
 * FDP1165
 * 20/11/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.amtsybex.fieldreach.utils.impl.Common;
import com.amtsybex.fieldreach.xmlloader.scriptresult.Pickup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to perform the task of picking up script result files from the file
 * system.
 */
public class PickupImpl implements Pickup {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory
			.getLogger(PickupImpl.class.getName());

	private int lastPickupCount;
	
	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public PickupImpl() {

		this.lastPickupCount = 0;
	}

	
	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.scriptresult.standalone.ScriptResultPickup
	 * #getScriptResultPickupFiles(java.lang.String)
	 */
	@Override
	public PickupFilesImpl getScriptResultPickupFiles(String dir) {

		if (log.isDebugEnabled())
			log.debug(">>> getPickupFiles");

		this.lastPickupCount = 0;

		List<File> pickupDirs = this.getPickupDirList(dir);

		// Iterate of the list of pickup directories and build a map of valid
		// and invalid script result files.

		Map<String, List<File>> validFiles = new HashMap<String, List<File>>();
		Map<String, List<File>> invalidFiles = new HashMap<String, List<File>>();

		// Iterate over each pickup directory
		for (File pickupDir : pickupDirs) {

			// Get list of files only from the directory.
			File[] files = pickupDir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File current, String name) {

					return new File(current, name).isFile();
				}
			});

			List<File> validPickupFiles = new ArrayList<File>();
			List<File> invalidPickupFiles = new ArrayList<File>();

			// Iterate over each file in the current pickup directory
			// adding valid and invalid script result files to the appropriate
			// list.
			for (File pickupFile : Arrays.asList(files)) {

				String fileName = pickupFile.getName().toLowerCase();

				// File is 'valid' if it follows the script result naming
				// convention.
				if (this.isValidScriptResultFilename(fileName)) {

					if (log.isDebugEnabled())
						log.debug("Pickup valid script result file: "
								+ fileName);
					
					validPickupFiles.add(pickupFile);

				} else {

					if (log.isDebugEnabled())
						log.debug("Pickup invalid script result file: "
								+ fileName);

					invalidPickupFiles.add(pickupFile);
				}
				
				lastPickupCount++;
			}

			// Determine key for the map of valid and invalid files.

			// The key is the name of the sub directory the files were found in.
			// Key will be null where the name of the directory the files were 
			// found is the configured pickup folder.
			String key = null;

			if (!FilenameUtils.normalizeNoEndSeparator(dir).equals(
					FilenameUtils.normalizeNoEndSeparator(pickupDir
							.getAbsolutePath())))
				key = pickupDir.getName();

			if (!validPickupFiles.isEmpty())
				validFiles.put(key, validPickupFiles);

			if (!invalidPickupFiles.isEmpty())
				invalidFiles.put(key, invalidPickupFiles);
			
		}

		PickupFilesImpl retval = new PickupFilesImpl(
				validFiles, invalidFiles);

		if (log.isDebugEnabled())
			log.debug("<<< getPickupFiles");

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.scriptresult.standalone.ScriptResultPickup
	 * #getLastPickupCount()
	 */
	@Override
	public int getLastPickupCount() {

		if (log.isDebugEnabled()) {

			log.debug(">>> getLastPickupCount");
			log.debug("<<< getLastPickupCount");
		}

		return this.lastPickupCount;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.scriptresult.standalone.ScriptResultPickup
	 * #getPickupTempFiles(java.lang.String)
	 */
	@Override
	public Map<String, List<String>> getPickupTempFiles(String dir) {

		if (log.isDebugEnabled())
			log.debug(">>> getPickupTempFiles dir=" + dir);

		List<File> pickupDirs = this.getPickupDirList(dir);

		Map<String, List<String>> tempFiles = new HashMap<String, List<String>>();

		// Iterate over each pickup directory
		for (File pickupDir : pickupDirs) {

			// Get list of files only from the directory.
			File[] files = pickupDir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File current, String name) {

					return new File(current, name).isFile();
				}
			});

			List<String> temp = new ArrayList<String>();

			// Iterate over each file in the current pickup directory
			// adding valid and invalid script result files to the appropriate
			// list.
			for (File pickupFile : Arrays.asList(files)) 
				temp.add(pickupFile.getAbsolutePath());

			// The key is the name of the sub directory the files were found in.
			// Key will
			// be null where the name of the directory the files were found
			// is the configured pickup folder.
			String key = null;

			if (!FilenameUtils.normalizeNoEndSeparator(dir).equals(
					FilenameUtils.normalizeNoEndSeparator(pickupDir
							.getAbsolutePath())))
				key = pickupDir.getName();

			if (!temp.isEmpty())
				tempFiles.put(key, temp);
		}

		if (log.isDebugEnabled())
			log.debug("<<< getPickupTempFiles");

		return tempFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.xmlloader.scriptresult.standalone.ScriptResultPickup
	 * #isValidScriptResultFilename(java.lang.String)
	 */
	@Override
	public boolean isValidScriptResultFilename(String filename) {

		if (log.isDebugEnabled())
			log.debug(">>> isValidFilename filename=" + filename);

		boolean isValid = filename
				.startsWith(Common.SCRIPT_RESULT_PREFIX)
				&& filename.endsWith(Common.SCRIPT_RESULT_EXTENSION);

		if (log.isDebugEnabled())
			log.debug("<<< isValidFilename retval=" + isValid);

		return isValid;
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Get list of the directories that should be scanned for files. The list
	 * will consist of the base directory supplied in the dir parameter and all
	 * sub directories it contains.
	 * 
	 * @param dir
	 *            Base directory to scan for sub directories.
	 * 
	 * @return List of File objects representing the directories to be scanned
	 *         for files.
	 */
	private List<File> getPickupDirList(String dir) {

		if (log.isDebugEnabled())
			log.debug(">>> getPickupDirList dir=" + dir);

		List<File> pickupDirs = new ArrayList<File>();

		// Get list of sub directories from supplied directory.
		File pickupDir = new File(dir);

		File[] subDirs = pickupDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File current, String name) {

				return new File(current, name).isDirectory();
			}
		});

		Arrays.sort(subDirs);

		// Add directory supplied in dir parameter and then
		// list of sub directories.
		pickupDirs.add(pickupDir);
		pickupDirs.addAll(1, Arrays.asList(subDirs));

		if (log.isDebugEnabled())
			log.debug("<<< getPickupDirList");

		return pickupDirs;
	}

}
