/**
 * Author:  T Murray
 * Date:    22/05/2014
 * Project: FDE026
 * 
 * Copyright AMT-Sybex 2014
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Code Re-factoring
 */
package com.amtsybex.fieldreach.services.endpoint.common.impl;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.instance.InstanceManager;
import com.amtsybex.fieldreach.backend.instance.Transaction;
import com.amtsybex.fieldreach.backend.model.InProgressDownloads;
import com.amtsybex.fieldreach.backend.model.InProgressUploads;
import com.amtsybex.fieldreach.backend.model.UploadParts;
import com.amtsybex.fieldreach.backend.service.FileTransferService;
import com.amtsybex.fieldreach.services.endpoint.common.FileTransferController;
import com.amtsybex.fieldreach.utils.impl.Common;

public class FileTransferControllerImpl implements FileTransferController {

	private static Logger log = LoggerFactory.getLogger(FileTransferControllerImpl.class.getName());

	private FileTransferService fileTransferService;

	private String fileUploadtempDir;

	@Autowired
	private InstanceManager backendInstanceManager;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	/**
	 * Default constructor.
	 * 
	 * Responsible for loading the properties in app.properties file that
	 * dictate if specific types of transaction should be retained.
	 */
	public FileTransferControllerImpl() {

	}

	/*-------------------------------------------
	 - Interface Implementation Methods
	 --------------------------------------------*/


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.FileTransferController
	 * #isExistingUpload(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isExistingUpload(String frInstance, String id)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> isExistingUpload frInstance=" + Common.CRLFEscapeString(frInstance) + " id="
					+ Common.CRLFEscapeString(id));
		}

		boolean result = false;

		InProgressUploads upload = fileTransferService.findInProgressUpload(
				frInstance, id);

		if (upload != null)
			result = true;

		if (log.isDebugEnabled())
			log.debug("<<< isExistingUpload");

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.FileTransferController
	 * #uploadMaintenance(int)
	 */
	@Override
	public void uploadMaintenance(int maxDays) throws Exception {

		if (log.isDebugEnabled())
			log.debug(">>> uploadMaintenance maxDays=" + maxDays);

		for (String frInstance : backendInstanceManager
				.getConfiguredInstanceNames()) {

			List<InProgressUploads> expiredUploads = fileTransferService
					.getExpiredUploads(frInstance, maxDays);

			String tempUploadDir = this.fileUploadtempDir;

			for (InProgressUploads upload : expiredUploads) {

				// Delete any upload parts associated with the upload from the
				// file system.
				for (UploadParts part : upload.getUploadParts()) {

					String partFile = tempUploadDir + "/" + part.getFileName();

					File tempFile = new File(partFile);

					tempFile.delete();
				}

				Transaction trans = this.backendInstanceManager
						.getTransaction(frInstance);

				try {

					fileTransferService.deleteInProgressUpload(frInstance,
							upload.getId());

					this.backendInstanceManager.commitTransaction(trans);

				} catch (FRInstanceException e) {

					this.backendInstanceManager.rollbackTransaction(trans);
					throw e;

				} catch (Throwable t) {
					
					log.error("UNEXPECTED EXCEPTION: " + t.getMessage());
					
					this.backendInstanceManager.rollbackTransaction(trans);
					
					throw new RuntimeException(t);
				} 
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< uploadMaintenance");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.FileTransferController
	 * #findDownload(java.lang.String, java.lang.String)
	 */
	@Override
	public InProgressDownloads findDownload(String frInstance, String id)
			throws FRInstanceException {

		InProgressDownloads download;

		if (log.isDebugEnabled()) {
			
			log.debug(">>> findDownload frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " entity=xxx");
		}

		download = fileTransferService.findInProgressDownload(frInstance, id);

		if (log.isDebugEnabled())
			log.debug("<<< findDownload");

		return download;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.FileTransferController
	 * #startDownload(java.lang.String,
	 * com.amtsybex.fieldreach.backend.model.InProgressDownloads)
	 */
	@Override
	public void startDownload(String frInstance, InProgressDownloads entity)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {
			
			log.debug(">>> startDownload frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " entity=xxx");
		}

		fileTransferService.insertInProgressDownload(frInstance, entity);

		if (log.isDebugEnabled())
			log.debug("<<< startDownload");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.FileTransferController
	 * #downloadMaintenance(int)
	 */
	@Override
	public void downloadMaintenance(int maxDays) throws Exception {

		if (log.isDebugEnabled())
			log.debug(">>> downloadMaintenance maxDays=" + maxDays);

		// Perform maintenance for each Fieldreach instance configured

		for (String frInstance : backendInstanceManager
				.getConfiguredInstanceNames()) {

			List<InProgressDownloads> expiredDownloads = fileTransferService
					.getExpiredDownloads(frInstance, maxDays);

			for (InProgressDownloads download : expiredDownloads) {

				boolean doDelete = true;

				// Delete temporary file created for database download. The
				// record will only be removed from inProgressDownloads if
				// temporary file is deleted.
				if (download.getDbFile() == 1) {

					File dbFile = new File(download.getFileUri());

					if (dbFile.exists())
						doDelete = dbFile.delete();
				}

				if (doDelete) {

					Transaction trans = this.backendInstanceManager
							.getTransaction(frInstance);

					try {

						fileTransferService.deleteInProgressDownload(
								frInstance, download.getId());

						this.backendInstanceManager.commitTransaction(trans);

					} catch (FRInstanceException e) {

						this.backendInstanceManager.rollbackTransaction(trans);
						throw e;

					} catch (Throwable t) {
						
						log.error("UNEXPECTED EXCEPTION: " + t.getMessage());
						
						this.backendInstanceManager.rollbackTransaction(trans);
						
						throw new RuntimeException(t);
					} 
				}
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< downloadMaintenance");
	}

	@Override
	public boolean isExistingDownload(String frInstance, String id)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> isExistingDownload frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " id=" + Common.CRLFEscapeString(id));
		}

		boolean result = false;

		InProgressDownloads download = fileTransferService
				.findInProgressDownload(frInstance, id);

		if (download != null)
			result = true;

		if (log.isDebugEnabled())
			log.debug("<<< isExistingDownload");

		return result;
	}

	/*-------------------------------------------
	 - Spring Injection Methods
	 --------------------------------------------*/

	/**
	 * Specify FileTransferService object to be used for carrying out file
	 * transfer tasks.
	 * 
	 * @param fileTransferService
	 */
	public void setFileTransferService(FileTransferService fileTransferService) {

		this.fileTransferService = fileTransferService;
	}

	/**
	 * Get the FileTransferService object used for carrying out file transfer
	 * tasks.
	 * 
	 * @return
	 */
	public FileTransferService GetFileTransferService() {

		return this.fileTransferService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.FileTransferController
	 * #setUploadTempDir(java.lang.String)
	 */
	@Override
	public void setUploadTempDir(String tempDir) {

		this.fileUploadtempDir = tempDir;
	}

}
