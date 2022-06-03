/**
 * Author:  Rajesh Shivaramakrishnan
 * Date:    10/02/2022
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.upload.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.ServletInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.InProgressUploadsInt;
import com.amtsybex.fieldreach.backend.model.UploadPartsInt;
import com.amtsybex.fieldreach.services.endpoint.common.FileTransferController;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse;
import com.amtsybex.fieldreach.services.messages.response.UploadPartResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.upload.UploadController;
import com.amtsybex.fieldreach.services.upload.exception.UploadPartException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

public class UploadControllerImpl implements UploadController {
	
	@Autowired
	private FileTransferController fileTransferController;
	
	private static Logger log = LoggerFactory.getLogger(UploadControllerImpl.class.getName());
	
	protected long maxUploadSize;
	protected long maxChunkSize;
	protected String tempDir;
	protected Map<String, String> fileTypeMappings;
	
	@Override
	public UploadInitiateResponse initiate(String frInstance, String identifier, String filename, long totalSize)
			throws FRInstanceException, Exception {

		if (log.isDebugEnabled()) {

			log.debug(">>> initiate identifier=" + identifier + " filename=" + filename + " totalSize=" + totalSize);
		}

		UploadInitiateResponse result = new UploadInitiateResponse();
		ErrorMessage err = new ErrorMessage();
		result.setError(err);

		if (this.maxUploadSize > 0 && totalSize > this.maxUploadSize) {
			
			throw new BadRequestException(Utils.MAX_FILESIZE_EXCEEDED);

		} else {

			boolean inProgressId = fileTransferController.isExistingUpload(frInstance, identifier);

			if (!inProgressId) {
			  
			  result = this.initiateNewIntUpload(frInstance, filename, totalSize);
			  
			  } else {
			  
			  if (log.isDebugEnabled()) {
			  
			  log.debug("Initiating existing upload file=" + filename + " id=" +
			  identifier); }
			  
			  InProgressUploadsInt upload = fileTransferController.findUpload(frInstance,identifier);
			  
			  result.setStartFromPart(new BigInteger(Integer.toString(upload.getNextPartInt())));
			  result.setMaxChunkSizeBytes(this.maxChunkSize);
			  
			  result.setSuccess(true); }
			 

			if (result.isSuccess()) {

				if (log.isDebugEnabled()) {

					log.debug("Upload initiated successfully. Assigned id: " + result.getIdentifier() + " file="+filename);
				}

			} else {

				log.error("Unable to inititate upload.");
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< initiate");

		return result;
	}
	
	@Override
	public void setMaxUploadSize(String maxUploadSize) {

		if (log.isDebugEnabled())
			log.debug(">>> setMaxUploadSize maxUploadSize=" + maxUploadSize);

		if(!StringUtils.isEmpty(maxUploadSize)) {
			this.maxUploadSize = Long.parseLong(maxUploadSize);
		}else {
			this.maxUploadSize = 0;
		}
		

		if (log.isDebugEnabled())
			log.debug("<<< setMaxUploadSize");
	}


	@Override
	public void setMaxChunkSize(long maxChunkSize) {

		if (log.isDebugEnabled())
			log.debug(">>> setMaxChunkSize maxChunkSize=" + maxChunkSize);

		this.maxChunkSize = maxChunkSize;

		if (log.isDebugEnabled())
			log.debug("<<< setMaxChunkSize");
	}

	@Override
	public void setTempDir(String tempDir) {

		if (log.isDebugEnabled())
			log.debug(">>> setTempDir tempDir=" + tempDir);

		this.tempDir = tempDir;

		if (log.isDebugEnabled())
			log.debug("<<< setTempDir");
	}

	@Override
	public String getUploadDir(String filename) throws ResourceTypeNotFoundException {
		
		if (log.isDebugEnabled())
			log.debug(">>> getUploadDir filename=" + filename);

		Optional<String> retVal = null;
		
		retVal = fileTypeMappings.entrySet().stream().map(ent->ent.getKey().toUpperCase()).filter(ent->filename.toUpperCase().matches(ent)).findFirst();
		
		if (log.isDebugEnabled())
			log.debug("<<< getUploadDir");
		
		return retVal.isPresent()?fileTypeMappings.get(retVal.get().toLowerCase()):null;
	}
	
	private UploadInitiateResponse initiateNewIntUpload(String frInstance, String filename, long totalSize)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> initiateNewIntUpload file=" + filename + " totalSize=" + totalSize);
		}

		UploadInitiateResponse result = new UploadInitiateResponse();
		ErrorMessage err = new ErrorMessage();
		result.setError(err);

		int totalparts = (int) (totalSize / this.maxChunkSize);

		// If there is a remainder then we need to upload an additional part.
		if ((totalSize % this.maxChunkSize) != 0)
			totalparts++;


		// Record of upload must be created in the Fieldreach database.
		InProgressUploadsInt uploadInt = new InProgressUploadsInt();
		uploadInt.setFileName(filename);
		uploadInt.setTotalSize(totalSize);
		uploadInt.setExpectedParts(totalparts);
		uploadInt.setNextPart(1);
		uploadInt.setUploadStarted(Common.generateFieldreachDBDate());
		uploadInt.setUploadPartsInt(null);

		// Create record for upload in the database.
		fileTransferController.startUpload(frInstance, uploadInt);

		// Set the result information.
		result.setIdentifier(uploadInt.getId());
		result.setStartFromPart(new BigInteger("1"));
		result.setMaxChunkSizeBytes(this.maxChunkSize);

		// Set the success flag.
		result.setSuccess(true);

		if (log.isDebugEnabled())
			log.debug("<<< initiateNewIntUpload");

		return result;
	}
	
	@Override
	public UploadPartResponse receiveIntPart(String frInstance, String identifier, int partNo, long datalength,
			String checkSum, ServletInputStream in) throws FRInstanceException, Exception {

		if (log.isDebugEnabled()) {

			log.debug(">>> receivePart identifier=" + identifier + " partNo=" + partNo + " datalength=" + datalength
					+ " checkSum=" + checkSum + " base64Data=XXXXX");
		}

		// Return object
		UploadPartResponse result = new UploadPartResponse();
		ErrorMessage err = null;

		try {

			err = validatePartUploadInt(frInstance, identifier, partNo, datalength, checkSum, in);
			result.setError(err);
			
			if (err.getErrorCode() != null) {

				throw new UploadPartException(err.getErrorDescription());
			}

			InProgressUploadsInt upload = fileTransferController.findUpload(frInstance, identifier);
			
			File tempPartFile = null;
			File tempFile = null;
			
			if (Common.createDir(this.tempDir)) {
				
				tempPartFile = new File(FilenameUtils.normalizeNoEndSeparator(this.tempDir + File.separator + upload.getId() + "_part.tmp"));
				tempFile = new File(FilenameUtils.normalizeNoEndSeparator(this.tempDir + File.separator + upload.getId() + "_full.tmp"));
			
			}
	        try (OutputStream out = new FileOutputStream(tempPartFile, false)) 
	        {
	        	
	        	MessageDigest md = MessageDigest.getInstance("MD5");
	        	DigestOutputStream digestOut = new DigestOutputStream(out, md);
	            
	        	FileCopyUtils.copy(in, digestOut);

	            if(!checkSum.equalsIgnoreCase(this.getMessageDigest(digestOut))) {
		        	log.error("Unable to upload part: Checksum do not match: " + identifier);

					err.setErrorCode(Utils.CHECKSUM_EXCEPTION);
					err.setErrorDescription("The Checksum supplied do not match.");
					
					throw new UploadPartException("Error occured validating upload part");
	            }
	            
	            FileInputStream partIn = new FileInputStream(tempPartFile);
	            OutputStream fullOut = new FileOutputStream(tempFile, true);
	            
	            try {
	            	
	            	FileCopyUtils.copy(partIn, fullOut);
	            	
	            }
	            catch(Exception e) {
	            	
	            	log.error("Error occurred while copying file", e);
	            	
	            	fileTransferController.cancelUpload(frInstance, identifier);
	            	
		            throw new RuntimeException(e);
	            	
	            }
	            
	            tempPartFile.delete();
	        } 
	        catch (IOException ex) 
	        {
	        	log.error("Error occurred while copying file", ex);
	            throw new RuntimeException(ex);
	        }

			log.debug("instance " + frInstance + " identifier " + identifier);
			
			UploadPartsInt part = new UploadPartsInt(identifier, partNo);
			part.setChecksum(checkSum);
			part.setFileName(upload.getFileName());
			part.setFileSize(datalength);

			Set<UploadPartsInt> uploadParts = upload.getUploadPartsInt();
			uploadParts.add(part);
			upload.setUploadPartsInt(uploadParts);

			upload.setNextPart(upload.getNextPartInt() + 1);

			try {

				fileTransferController.updateUpload(frInstance, upload);

			} catch (Exception e) {

				log.error("updateUpload Error", e);
				
				tempFile.delete();
				fileTransferController.cancelUpload(frInstance, identifier);

				throw e;
			}

			if (upload.getExpectedParts() != partNo) {
				
				result.setSuccess(true);
				result.setNextPart(new BigInteger(Integer.toString(upload.getNextPartInt())));

				if (log.isDebugEnabled()) {

					log.debug("Part upload complete. More parts expected. \n " + "uploadid:" + identifier + " partno:"
							+ partNo);
				}

			} else {

				if (log.isDebugEnabled()) {

					log.debug("Last expected part, assemble upload file parts. uploadid: " + upload.getId());
				}

				String uploadDir = this.getUploadDir(upload.getFileName());
				File outFile = new File(FilenameUtils.normalizeNoEndSeparator(uploadDir + File.separator + upload.getFileName()));

				File tempOutFile = new File(FilenameUtils.normalizeNoEndSeparator(uploadDir + File.separator+ upload.getFileName() + Common.TEMP_FILE_EXTENSION));
				
				tempFile.renameTo(tempOutFile);
				
				tempOutFile.renameTo(outFile);
				
				fileTransferController.cancelUpload(frInstance, identifier);
				
				result.setComplete(true);
				result.setSuccess(true);

				if (log.isDebugEnabled()) {

					log.debug("Parts assembled successfully. uploadid: " + upload.getId());
				}

			}

		}
	 catch (UploadPartException e) {

			// UploadPart request not valid.

			InProgressUploadsInt upload = fileTransferController.findUpload(frInstance, identifier);

			result.setSuccess(false);

			if (upload != null) {

				result.setNextPart(new BigInteger(Integer.toString(upload.getNextPart())));
			}

			if (log.isDebugEnabled()) {

				log.debug("Part request reset required " + identifier + " part:" + partNo);
			}
			uploadMaintenance(frInstance);
		}
		catch (Exception e) {

			if (log.isDebugEnabled()) {

				log.error("Unexpected exception occurred uploading script result part: \n" + e.getMessage(), e);
			}

			uploadMaintenance(frInstance);

			throw e;
		}

		uploadMaintenance(frInstance);

		if (log.isDebugEnabled())
			log.debug("<<< receivePart");

		return result;
	}
	
	private void uploadMaintenance(String frInstance) {

		if (log.isDebugEnabled())
			log.debug(">>> uploadMaintenance");

		Map<String, InProgressUploadsInt> uploadCache = new HashMap<String, InProgressUploadsInt>();

		File tempdir = new File(this.tempDir);
		
		InProgressUploadsInt upload;

		for (File file : tempdir.listFiles()) {

			// Extract the identifier part of the temporary file name.
			String[] fileIds = file.getName().split("_");

			if (fileIds.length > 0) {

				try {

					if (!uploadCache.containsKey(fileIds[0])) {

						upload = fileTransferController.findUpload(frInstance, fileIds[0]);

						if (upload != null)
							uploadCache.put(fileIds[0], upload);

					} else {

						upload = uploadCache.get(fileIds[0]);
					}

					if (upload == null) {

						if (!file.delete()) {

							log.error("Failed to remove temp file " + file.getName()
									+ getReasonForFileDeletionFailureInPlainEnglish(file));
						} else {

							if (log.isDebugEnabled()) {

								log.debug("Temporary upload file deleted: " + file.getName());
							}
						}
					}

				} catch (Exception e) {

					log.error("Error processing file upload maintenance, " + "could not access the database. \n"
							+ e.getMessage());
				}
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< uploadMaintenance");
	}
	
	private String getReasonForFileDeletionFailureInPlainEnglish(File file) {

		try {

			if (!file.exists()) {

				return "It doesn't exist in the first place.";

			} else if (file.isDirectory() && file.list().length > 0) {

				return "It's a directory and it's not empty.";

			} else {

				return "Somebody else has it open, we don't have write permissions, or somebody stole my disk.";
			}

		} catch (SecurityException e) {

			return "We're sandboxed and don't have filesystem access.";
		}

	}
	
	private ErrorMessage validatePartUploadInt(String frInstance, String identifier, int partNo, long datalength,
			String checkSum, ServletInputStream in ) throws FRInstanceException, Exception {

		if (log.isDebugEnabled()) {

			log.debug(">>> validatePartUpload identifier=" + identifier + " partNo=" + partNo + " datalength="
					+ datalength + " checkSum=" + checkSum);
		}

		ErrorMessage err = new ErrorMessage();

		InProgressUploadsInt upload = fileTransferController.findUpload(frInstance, identifier);
		
		if (upload == null) {

			log.error("Unable to upload part: Invalid identifier: " + identifier);

			err.setErrorCode(Utils.INVALID_ID_EXCEPTION);
			err.setErrorDescription("The identifer supplied for the upload part is invalid.");

			return err;
		}

		if (upload.getNextPart() != partNo) {

			String msg = "Unexpected Partno. Expected: " + upload.getNextPart() + " Recieved: " + partNo;

			log.error(msg);

			err.setErrorCode(Utils.PART_NO_SEQ_EXCEPTION);
			err.setErrorDescription(msg);

			return err;
		}

		for (UploadPartsInt existingPart : upload.getUploadPartsInt()) {

			if (existingPart.getPartNoLong() == partNo) {

				// Unexpected PartNo
				String msg = "Unexpected Partno. Expected: " + upload.getNextPart() + " Recieved: " + partNo;

				log.error(msg);

				err.setErrorCode(Utils.PART_NO_SEQ_EXCEPTION);
				err.setErrorDescription(msg);

				return err;
			}
		}

		if (datalength > this.maxChunkSize) {

			String msg = "The part you are trying to upload exceeds the maximum upload " + "chunk size of " + this.maxChunkSize
					+ " bytes.";

			log.error(msg);

			err.setErrorCode(Utils.MAX_CHUNKSIZE_EXCEEDED);
			err.setErrorDescription(msg);

			return err;
		}
		return err;
	}
	
	private String getHexaString(byte[] data) {

		String result = new BigInteger(1, data).toString(16);

		return result;

	}

	private String getMessageDigest(DigestOutputStream digestOutputStream) {

		MessageDigest digest = digestOutputStream.getMessageDigest();

		byte[] digestBytes = digest.digest();

		String digestStr = getHexaString(digestBytes);

		return digestStr;

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.upload.UploadController#
	 * setFileTypeMapping(java.util.Map)
	 */
	@Override
	public void setFileTypeMapping(LinkedHashMap<String, String> fileTypeMappings) {

		if (log.isDebugEnabled())
			log.debug(">>> setFileTypeMapping fileTypeMappings=" + fileTypeMappings);

		this.fileTypeMappings = fileTypeMappings;

		if (log.isDebugEnabled())
			log.debug("<<< setFileTypeMapping");
	}
}
