package com.amtsybex.fieldreach.services.endpoint.common.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.InProgressUploadsInt;
import com.amtsybex.fieldreach.backend.service.FileTransferService;
import com.amtsybex.fieldreach.services.endpoint.common.FileTransferController;
import com.amtsybex.fieldreach.utils.impl.Common;

public class FileTransferControllerImpl implements FileTransferController{
	
	private static Logger log = LoggerFactory.getLogger(FileTransferControllerImpl.class.getName());
	
	@Autowired
	private FileTransferService fileTransferService;

	@Override
	public boolean isExistingUpload(String frInstance, String id) throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> isExistingUpload frInstance=" + Common.CRLFEscapeString(frInstance) + " id="
					+ Common.CRLFEscapeString(id));
		}

		boolean result = false;

		InProgressUploadsInt upload = fileTransferService.findInProgressUploadInt(
				frInstance, id);

		if (upload != null)
			result = true;

		if (log.isDebugEnabled())
			log.debug("<<< isExistingUpload");

		return result;
	}
	
	@Override
	public InProgressUploadsInt findUpload(String frInstance, String id)
			throws FRInstanceException {

		InProgressUploadsInt upload;

		if (log.isDebugEnabled())
			log.debug(">>> findUpload frInstance=" + Common.CRLFEscapeString(frInstance) + " id=" + Common.CRLFEscapeString(id));

		upload = fileTransferService.findInProgressUploadInt(frInstance, id);

		if (log.isDebugEnabled())
			log.debug("<<< findUpload");

		return upload;
	}
	
	@Override
	public void startUpload(String frInstance, InProgressUploadsInt entity)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> startUpload frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " entity=xxx");
		}

		fileTransferService.insertInProgressUploadInt(frInstance, entity);

		if (log.isDebugEnabled())
			log.debug("<<< startUpload");
	}
	
	@Override
	public void updateUpload(String frInstance, InProgressUploadsInt entity)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> updateUpload frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " entity=xxx");
		}

		fileTransferService.updateInProgressUploadInt(frInstance, entity);

		log.debug("<<< updateUpload");
	}
	
	@Override
	public void cancelUpload(String frInstance, String id)
			throws FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> cancelupload frInstance=" + Common.CRLFEscapeString(frInstance) + " id=" + Common.CRLFEscapeString(id));

		fileTransferService.deleteInProgressUploadInt(frInstance, id);

		if (log.isDebugEnabled())
			log.debug("<<< cancelupload");
	}

	public FileTransferService getFileTransferService() {
		return fileTransferService;
	}

	public void setFileTransferService(FileTransferService fileTransferService) {
		this.fileTransferService = fileTransferService;
	}
	
}
