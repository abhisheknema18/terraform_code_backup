/**
 * Project: FDE026
 * Author:	T Murray
 * Date:	26/08/2014
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.xmlloader.scriptresult.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amtsybex.fieldreach.backend.model.SystemEventLog;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTCATEGORY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.EVENTTYPE;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SEVERITY;
import com.amtsybex.fieldreach.backend.model.SystemEventLog.SOURCESYSTEM;
import com.amtsybex.fieldreach.backend.service.SystemEventService;
import com.amtsybex.fieldreach.exception.ConfigException;
import com.amtsybex.fieldreach.xmlloader.scriptresult.Dispatch;

/**
 * Class to perform the task of dispatching script result files to the
 * archive/error directories.
 */
public class DispatchImpl implements Dispatch {

	/*-------------------------------------------
	 - Member Variables
	 --------------------------------------------*/

	private static final Logger log = LoggerFactory
			.getLogger(DispatchImpl.class.getName());

	private String archiveDir;

	private String errorDir;
	
	@Autowired
	private SystemEventService mSystemEventLogger;
	

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	/**
	 * @param archiveDir
	 *            Location of the Archive directory
	 * 
	 * @param errorDir
	 *            Location of the error directory
	 * 
	 * @throws ConfigException
	 *             If the supplied locations do not exist or are not valid
	 *             directories.
	 */
	public DispatchImpl(String archiveDir, String errorDir)
			throws ConfigException {

		if (log.isDebugEnabled())
			log.debug(">>> ScriptResultDispatcherImpl dir=" + archiveDir);

		// Archive Dir
		
		// FDP1253 TM 19/10/2016
		if(archiveDir != null && !archiveDir.trim().equals("")) {
			
			File archive = new File(archiveDir);
	
			if (!archive.exists())
				throw new ConfigException(
						"Archive directory does not exist:" + archiveDir);
	
			if (!archive.isDirectory())
				throw new ConfigException(
						"Archive directory is not a directory:" + archiveDir);
			
			this.archiveDir = archiveDir;
			log.info("Archiving enabled: " + this.archiveDir);
			
		} else {
			
			this.archiveDir = null;
			log.info("Archiving disabled");
			
		}
		// End FDP1253
		
		// Error Dir
		File error = new File(errorDir);

		if (!error.exists())
			throw new ConfigException(
					"Error directory does not exist:" + errorDir);

		if (!error.isDirectory())
			throw new ConfigException(
					"Error directory is not a directory:" + errorDir);

		this.errorDir = errorDir;

		if (log.isDebugEnabled())
			log.debug("<<< ScriptResultDispatcherImpl");
	}

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.scriptresult.standalone.
	 * ScriptResultDispatcher#dispatchToArchive(java.lang.String)
	 */
	@Override
	public void dispatchToArchive(String file) throws IOException {

		// FDP1253 TM 19/10/2016
		if(this.archiveDir != null) {
			
			if (log.isDebugEnabled())
				log.debug(">>> dispatchToArchive file=" + file);
	
				this.dispatchToArchive(null, file);
			
			if (log.isDebugEnabled())
				log.debug("<<< dispatchToArchive");
			
		} else {
			
			// Delete file form pickup folder.
			File src = new File(file);
			
			if (!FileUtils.deleteQuietly(src)) {
				log.error("Unable to delete temp file: "
						+ src.getAbsolutePath());
				
				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "Unable to delete temp file", "Unable to delete temp file: " + src.getAbsolutePath() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P2));
			}
		}
		//End FDP1253
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.scriptresult.standalone.
	 * ScriptResultDispatcher#dispatchToError(java.lang.String)
	 */
	@Override
	public void dispatchToError(String file, String error) throws IOException {

		if (log.isDebugEnabled())
			log.debug(">>> dispatchToError file=" + file);

		this.dispatchToError(null, file, error);

		if (log.isDebugEnabled())
			log.debug("<<< dispatchToError");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.scriptresult.standalone.
	 * ScriptResultDispatcher#dispatchToArchive(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void dispatchToArchive(String subDir, String file)
			throws IOException {
		
		// FDP1253 TM 19/10/2016
		if(this.archiveDir != null) {
			
			if (log.isDebugEnabled())
				log.debug(">>> dispatchToArchive subDir=" + subDir + " file="
						+ file);
			
			String dest = dispatch(this.archiveDir, subDir, file);
	
			if (log.isInfoEnabled())
				log.info("File dispatched to ARCHIVE:\n" + dest + "\n");
	
			if (log.isDebugEnabled())
				log.debug("<<< dispatchToArchive");
			
		} else {
			
			// Delete file form pickup folder.
			File src = new File(file);
			
			if (!FileUtils.deleteQuietly(src)) {
				log.error("Unable to delete temp file: "
						+ src.getAbsolutePath());
				
				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "Unable to delete temp file", "Unable to delete temp file: " + src.getAbsolutePath() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.xmlloader.scriptresult.standalone.
	 * ScriptResultDispatcher#dispatchToError(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void dispatchToError(String subDir, String file, String error)
			throws IOException {

		if (log.isDebugEnabled())
			log.debug(">>> dispatchToError subDir=" + subDir + " file=" + file);
		
		String dest = dispatch(this.errorDir, subDir, file);

		if (log.isInfoEnabled()) {

			log.info("File dispatched to ERROR:\n" + dest + "\nReason:\n"
					+ error + "\n");
		}

		if (log.isDebugEnabled())
			log.debug("<<< dispatchToError");
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	private String dispatch(String dispatchDir, String subDir, String file)
			throws IOException {

		if (log.isDebugEnabled()) {

			log.debug(">>> dispatch dispatchDir=" + dispatchDir + " subDir="
					+ subDir + " file=" + file);
		}

		String filename = FilenameUtils.getName(file);

		File src = new File(file);
		File dest;

		if (subDir != null && !subDir.trim().equals("")
				&& !subDir.trim().equalsIgnoreCase("null")) {

			String dir = FilenameUtils.normalizeNoEndSeparator(dispatchDir
					+ File.separator + subDir);

			File directory = new File(dir);

			if (!directory.exists()) {

				if (!directory.mkdirs())
					throw new IOException("Error creating sub directory: "
							+ dir);
			}

			dest = new File(FilenameUtils.normalizeNoEndSeparator(dir
					+ File.separator + filename));
		} else {

			dest = new File(FilenameUtils.normalizeNoEndSeparator(dispatchDir
					+ File.separator + filename));
		}

		try {

			// FDP1165 TM 25/11/2015
			// Improve performance of archiving.
			FileInputStream fis = new FileInputStream(src);
			FileOutputStream fos = new FileOutputStream(dest);
			
			FileChannel inChannel = fis.getChannel();
			FileChannel outChannel = fos.getChannel();
			
			try {
				
				inChannel.transferTo(0, inChannel.size(), outChannel);
			} 
			catch (IOException e) {
				
				throw e;
			}
			finally {
				
				if (fis != null) fis.close();
				if (fos != null) fos.close();
				if (inChannel != null) inChannel.close();
				if (outChannel != null) outChannel.close();
			}
		        
			// End FDP1165

			if (!FileUtils.deleteQuietly(src)) {
				log.error("Unable to delete temp file: "
						+ src.getAbsolutePath());
				
				mSystemEventLogger.saveSystemEventLog(null, new SystemEventLog(EVENTCATEGORY.EXCEPTION, EVENTTYPE.ERROR, "Unable to delete temp file", "Unable to delete temp file: " + src.getAbsolutePath() , SOURCESYSTEM.FIELDREACH, "XML-LOADER", SEVERITY.P3));
			}
			
		} catch (IOException e) {

			throw e;
		}

		if (log.isDebugEnabled())
			log.debug("<<< dispatch");

		return dest.getAbsolutePath();
	}

}
