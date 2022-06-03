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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.instance.InstanceManager;
import com.amtsybex.fieldreach.backend.model.InProgressDownloads;
import com.amtsybex.fieldreach.backend.model.InProgressUploads;
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
	 * #findUpload(java.lang.String, java.lang.String)
	 */
	@Override
	public InProgressUploads findUpload(String frInstance, String id)
			throws FRInstanceException {

		InProgressUploads upload;

		if (log.isDebugEnabled())
			log.debug(">>> findUpload frInstance=" + Common.CRLFEscapeString(frInstance) + " id=" + Common.CRLFEscapeString(id));

		upload = fileTransferService.findInProgressUpload(frInstance, id);

		if (log.isDebugEnabled())
			log.debug("<<< findUpload");

		return upload;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.FileTransferController
	 * #startUpload(java.lang.String,
	 * com.amtsybex.fieldreach.backend.model.InProgressUploads)
	 */
	@Override
	public void startUpload(String frInstance, InProgressUploads entity)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> startUpload frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " entity=xxx");
		}

		fileTransferService.insertInProgressUpload(frInstance, entity);

		if (log.isDebugEnabled())
			log.debug("<<< startUpload");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.FileTransferController
	 * #cancelUpload(java.lang.String, java.lang.String)
	 */
	@Override
	public void cancelUpload(String frInstance, String id)
			throws FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> cancelupload frInstance=" + Common.CRLFEscapeString(frInstance) + " id=" + Common.CRLFEscapeString(id));

		fileTransferService.deleteInProgressUpload(frInstance, id);

		if (log.isDebugEnabled())
			log.debug("<<< cancelupload");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.FileTransferController
	 * #updateUpload(java.lang.String,
	 * com.amtsybex.fieldreach.backend.model.InProgressUploads)
	 */
	@Override
	public void updateUpload(String frInstance, InProgressUploads entity)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> updateUpload frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " entity=xxx");
		}

		fileTransferService.updateInProgressUpload(frInstance, entity);

		log.debug("<<< updateUpload");
	}

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
	
	@Override
	public void setUploadTempDir(String tempDir) {

		this.fileUploadtempDir = tempDir;
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

}
