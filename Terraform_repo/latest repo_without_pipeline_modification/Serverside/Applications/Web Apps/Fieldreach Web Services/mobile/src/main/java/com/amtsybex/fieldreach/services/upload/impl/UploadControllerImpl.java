/**
 * Author:  T Murray
 * Date:    02/02/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2015
 * 
 * *****************************
 * 
 * Modified: FDE026
 * 			 TM 22/05/2014 
 * 
 * In progress uploads are now tracked in the Fieldreach database rather than 
 * in memory. There is also now no need to persist in the memory record to disk as this
 * is held in the database.
 * 
 * Also implement support for multiple Fieldreach instances.
 * 
 * *****************************
 * 
 * Amended:
 * FDE029 TM 21/01/2015
 * Changes to upload mechanism to support the creation of sub directories
 * in the configured upload folder. Also code re-factoring.
 */
package com.amtsybex.fieldreach.services.upload.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.InProgressUploads;
import com.amtsybex.fieldreach.backend.model.UploadParts;
import com.amtsybex.fieldreach.exception.ConfigException;
import com.amtsybex.fieldreach.services.endpoint.common.FileTransferController;
import com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse;
import com.amtsybex.fieldreach.services.messages.response.UploadPartResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.upload.UploadController;
import com.amtsybex.fieldreach.services.upload.exception.UploadPartException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

/**
 * A class to allow the initiation and upload of files in multiple parts.
 */
public class UploadControllerImpl implements UploadController {

	private static Logger log = LoggerFactory.getLogger(UploadControllerImpl.class.getName());

	// FDE037 TM 24/02/2016

	protected long maxUploadSize;
	protected long maxChunkSize;
	protected String tempDir;
	protected Map<String, String> fileTypeMappings;

	// End FDE037

	@Autowired
	protected FileTransferController fileTransferController;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	/**
	 * Default constructor
	 */
	public UploadControllerImpl() {

	}

	// FDE034 TM 24/02/2016
	
	// Verify configuration supplied
	
	@PostConstruct
	private void verifyConfig() throws ConfigException {
		
		if (log.isDebugEnabled())
			log.debug(">>> verifyConfig");
		
		// Verify all regular expressions in the filetype mappings are valid.
		for (Map.Entry<String, String> entry : this.fileTypeMappings.entrySet()) {
			
	        try {
	        	
	            Pattern.compile(entry.getKey());
	            
	        } catch (PatternSyntaxException e) {
	        	
	            log.error("Invalid filename pattern defined in upload controller: " + entry.getKey());
	            throw new ConfigException(e);
	        }
		}
		
		if (log.isDebugEnabled())
			log.debug("<<< verifyConfig");
	}
	
	// End FDE034

	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	// FDE026 TM 23/05/2014
	// Code re-organised to be more readable and also introduced changes
	// to use Fieldreach database to manage uploads. multiple Fieldreach
	// instance support also implemented.

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.upload.UploadController#initiate(java
	 * .lang.String, java.lang.String, java.lang.String, long)
	 */
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

			log.error("Size of the file being uploaded exceeds the maximum allowed." + totalSize + " file:" + filename);

			err.setErrorCode(Utils.MAX_FILESIZE_EXCEEDED);
			err.setErrorDescription(
					"The file you are trying to upload exceed the maximum upload " + "limit of " + this.maxUploadSize + " bytes.");

			result.setSuccess(false);

		} else {

			// Check to see if this will be a new upload or is an in progress
			// upload.
			boolean inProgressId = fileTransferController.isExistingUpload(frInstance, identifier);

			if (!inProgressId) {
				
				// FDE037 TM 24/02/2016
				// Check config to ensure an upload directory has been configured for the file
				// being uploaded.
				
				try {
					
					this.getUploadDir(filename);
					
					result = this.initiateNewUpload(frInstance, filename, totalSize);
					
				} catch (ResourceTypeNotFoundException e) {
					
					String msg = "Upload directory for the file' " + filename + "' could not be found.";
					
					log.error(msg);

					err.setErrorCode(Utils.UNKNOWN_FILE_TYPE_EXCEPTION);
					err.setErrorDescription(msg);

					result.setSuccess(false);
				}
							
				// End FDE037
							
			} else {

				if (log.isDebugEnabled()) {

					log.debug("Initiating existing upload file=" + filename + " id=" + identifier);
				}

				InProgressUploads upload = fileTransferController.findUpload(frInstance, identifier);

				// Set the result information.
				result.setIdentifier(identifier);
				result.setStartFromPart(new BigInteger(Integer.toString(upload.getNextPartInt())));
				result.setMaxChunkSizeBytes(this.maxChunkSize);

				result.setSuccess(true);
			}

			if (result.isSuccess()) {

				if (log.isDebugEnabled()) {

					log.debug("Upload initiated successfully. Assigned id: " + result.getIdentifier() + " file="
							+ filename);
				}

			} else {

				log.error("Unable to inititate upload.");
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< initiate");

		return result;
	}

	//
	// FDP980 TM 23/08/2013
	// Modified the code in the method to make the processing of uploads
	// more robust

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.upload.BaseUploadController#receivePart
	 * (java.lang.String, java.lang.String, int, long, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public UploadPartResponse receivePart(String frInstance, String identifier, int partNo, long datalength,
			String checkSum, String base64Data) throws FRInstanceException, Exception {

		if (log.isDebugEnabled()) {

			log.debug(">>> receivePart identifier=" + identifier + " partNo=" + partNo + " datalength=" + datalength
					+ " checkSum=" + checkSum + " base64Data=XXXXX");
		}

		// Return object
		UploadPartResponse result = new UploadPartResponse();
		ErrorMessage err = null;

		try {

			// Validate the part being uploaded

			err = validatePartUpload(frInstance, identifier, partNo, datalength, checkSum, base64Data);

			result.setError(err);

			if (err.getErrorCode() != null) {

				throw new UploadPartException("Error occured validating upload part");
			}

			// Write the part data to the file system and update the upload
			// record in the database.

			byte[] decodedByteData = Common.decodeBase64(base64Data);
			String partFileName = Utils.generateUploadPartFileName(identifier, partNo);

			if (!Common.writeBytesToFile(decodedByteData, partFileName, this.tempDir)) {

				String msg = "receivePart: Unable to write temporary upload file." + partFileName
						+ " Upload of part aborted. partno: " + partNo;

				log.error(msg);

				throw new UploadPartException(msg);
			}

			// Upload part data was written to temporary file on the file
			// system. Update record of in progress upload in the database
			// by updating list of associated parts and by incrementing the
			// next expected part by 1.

			InProgressUploads upload = fileTransferController.findUpload(frInstance, identifier);

			UploadParts part = new UploadParts(identifier, partNo);
			part.setChecksum(checkSum);
			part.setFileName(partFileName);
			part.setFileSize(datalength);

			Set<UploadParts> uploadParts = upload.getUploadParts();
			uploadParts.add(part);
			upload.setUploadParts(uploadParts);

			
			upload.setNextPart(upload.getNextPartInt() + 1);

			try {

				fileTransferController.updateUpload(frInstance, upload);

			} catch (Exception e) {

				// Delete temporary file that was created.
				File tempPartFile = new File(this.tempDir + "/" + partFileName);

				tempPartFile.delete();

				// Re-throw exception so database transaction is rolled back and
				// exception is handled elsewhere.
				throw e;
			}

			// If all parts have been received then assemble the parts and write
			// the file to the file system, otherwise treat as a normal part and
			// await the next part.

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

				// Assemble the parts of the upload and write them to their
				// target destination.
				boolean successfulWrite = assembleParts(upload);

				//Cancel upload to mark as complete. putting this here before checking for successful write as if this is unsuccessful there is no way of trying again anyway
				fileTransferController.cancelUpload(frInstance, identifier);
				
				if (!successfulWrite) {

					throw new Exception("An unexpected error occured writing the upload file to disk. " + "uploadid: "
							+ identifier);
				}

				result.setComplete(true);
				result.setSuccess(true);

				if (log.isDebugEnabled()) {

					log.debug("Parts assembled successfully. uploadid: " + upload.getId());
				}

			}

		} catch (UploadPartException e) {

			// UploadPart request not valid.

			InProgressUploads upload = fileTransferController.findUpload(frInstance, identifier);

			result.setSuccess(false);

			if (upload != null) {

				result.setNextPart(new BigInteger(Integer.toString(upload.getNextPart())));
			}

			if (log.isDebugEnabled()) {

				log.debug("Part request reset required " + identifier + " part:" + partNo);
			}

			//No point in running this twice, error is not rethrown here so this will be run below
			//uploadMaintenance(frInstance);

		} catch (Exception e) {

			if (log.isDebugEnabled()) {

				log.debug("Unexpected exception occurred uploading script result part: \n" + e.getMessage());
			}

			uploadMaintenance(frInstance);

			// Rethrow exception to be handled elsewhere.
			throw e;
		}

		uploadMaintenance(frInstance);

		if (log.isDebugEnabled())
			log.debug("<<< receivePart");

		return result;
	}

	// FDE037 TM 24/02/2016

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.upload.UploadController#setMaxUploadSize
	 * (int)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.upload.UploadController#setMaxChunkSize(
	 * int)
	 */
	@Override
	public void setMaxChunkSize(long maxChunkSize) {

		if (log.isDebugEnabled())
			log.debug(">>> setMaxChunkSize maxChunkSize=" + maxChunkSize);

		this.maxChunkSize = maxChunkSize;

		if (log.isDebugEnabled())
			log.debug("<<< setMaxChunkSize");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.upload.UploadController#setTempDir(java.
	 * lang.String)
	 */
	@Override
	public void setTempDir(String tempDir) {

		if (log.isDebugEnabled())
			log.debug(">>> setTempDir tempDir=" + tempDir);

		this.tempDir = tempDir;

		if (log.isDebugEnabled())
			log.debug("<<< setTempDir");
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

	/*
	 * (non-Javadoc)
	 * @see com.amtsybex.fieldreach.services.upload.UploadController#getUploadDir(java.lang.String)
	 */
	@Override
	public String getUploadDir(String filename) throws ResourceTypeNotFoundException {
		
		if (log.isDebugEnabled())
			log.debug(">>> getUploadDir filename=" + filename);

		String retVal = null;
		
		// Iterate over map of file type mappings looking for a filename pattern that
		// matches the name of the upload file.
		for (Map.Entry<String, String> entry : this.fileTypeMappings.entrySet()) {
			
			// Convert wildcards to regex expression.
			String regex = entry.getKey().toUpperCase();
			
			if (filename.toUpperCase().matches(regex)) {
				
				retVal = entry.getValue();
				log.debug("Upload dir '" + retVal + "' found for file '" + filename + "'");
				
				break;
			}
		}
		
		if (log.isDebugEnabled())
			log.debug("<<< getUploadDir");
		
		return retVal;
	}
	
	// End FDE037

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	// FDP980 TM 23/08/2013
	// Made some changes to logging and code formatting.
	/**
	 * A method to perform the various validation checks required as part of a
	 * multi part upload. The method will return an ErrorMessage object
	 * populated with exception messages, should any validation errors occur.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param identifier
	 *            Unique identifier to identify which upload this part belongs
	 *            to.
	 * 
	 * @param partNo
	 *            Part number this part represent out of N parts.
	 * 
	 * @param datalength
	 *            The size of this part in bytes as specified by the client.
	 * 
	 * @param checkSum
	 *            The checksum generated on the client for the data being
	 *            uploaded.
	 * 
	 * @param base64data
	 *            The data of this part after it has been encoded to base64.
	 * 
	 * @return An ErrorMessage object populated with exception messages if any
	 *         validation error occur. If no validation errors occur the fields
	 *         the ErrorMessage object contains will be null.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found.
	 */
	private ErrorMessage validatePartUpload(String frInstance, String identifier, int partNo, long datalength,
			String checkSum, String base64Data) throws FRInstanceException, Exception {

		if (log.isDebugEnabled()) {

			log.debug(">>> validatePartUpload identifier=" + identifier + " partNo=" + partNo + " datalength="
					+ datalength + " checkSum=" + checkSum + " base64Data=XXXXX");
		}

		ErrorMessage err = new ErrorMessage();

		// Get record of the current upload
		InProgressUploads upload = fileTransferController.findUpload(frInstance, identifier);

		// First check if the identifier is valid.
		if (upload == null) {

			// Invalid identifier
			log.error("Unable to upload part: Invalid identifier: " + identifier);

			err.setErrorCode(Utils.INVALID_ID_EXCEPTION);
			err.setErrorDescription("The identifer supplied for the upload part is invalid.");

			return err;
		}

		// Now check the part is in sequence.
		if (upload.getNextPart() != partNo) {

			String msg = "Unexpected Partno. Expected: " + upload.getNextPart() + " Recieved: " + partNo;

			log.error(msg);

			err.setErrorCode(Utils.PART_NO_SEQ_EXCEPTION);
			err.setErrorDescription(msg);

			return err;
		}

		// FDP980 TM 23/08/2013
		// Check to see if the part being uploaded has already been uploaded.

		for (UploadParts existingPart : upload.getUploadParts()) {

			if (existingPart.getPartNoLong() == partNo) {

				// Unexpected PartNo
				String msg = "Unexpected Partno. Expected: " + upload.getNextPart() + " Recieved: " + partNo;

				log.error(msg);

				err.setErrorCode(Utils.PART_NO_SEQ_EXCEPTION);
				err.setErrorDescription(msg);

				return err;
			}
		}

		// End FDP980

		// Check the size of the part (specified by the client) does not exceed
		// the maximum part size allowed.

		if (datalength > this.maxChunkSize) {

			String msg = "The part you are trying to upload exceeds the maximum upload " + "chunk size of " + this.maxChunkSize
					+ " bytes.";

			log.error(msg);

			err.setErrorCode(Utils.MAX_CHUNKSIZE_EXCEEDED);
			err.setErrorDescription(msg);

			return err;
		}

		// Check the size of all parts uploaded so far (including this one) do
		// not exceed the maximum upload size allowed. Calculation based on
		// sizes supplied by the client.

		long totalPartsSize = datalength;

		byte[] decodedDataBytes = Common.decodeBase64(base64Data);

		for (UploadParts part : upload.getUploadParts())
			totalPartsSize = totalPartsSize + part.getFileSize();

		if (this.maxUploadSize > 0 && totalPartsSize > this.maxUploadSize) {

			String msg = "The file you are trying to upload exceed the maximum upload " + "limit of " + this.maxUploadSize
					+ " bytes.";

			log.error(msg);

			err.setErrorCode(Utils.MAX_FILESIZE_EXCEEDED);
			err.setErrorDescription(msg);

			return err;
		}

		// Validate the checksum for this part.
		if (!Common.verifyMD5Checksum(decodedDataBytes, checkSum)) {

			String msg = "The checksum supplied for the part does not match" + " the checksum generated.";

			log.error(msg);

			err.setErrorCode(Utils.CHECKSUM_EXCEPTION);
			err.setErrorDescription(msg);

			return err;
		}

		log.debug("<<< validatePartUpload");

		return err;
	}

	// FDP980 TM 23/08/2013
	// New method to assemble upload parts.
	/**
	 * Assembles all of the individual parts for the upload supplied.The parts
	 * are then written to the target location.
	 * 
	 * @param upload
	 *            InProgressUploads object that you want to assemble the parts
	 *            for.
	 * 
	 * @return True if the parts are assembled correctly and written to the
	 *         target location. False if the part assembly or writing of the
	 *         file to the target destination is not successful.
	 */
	private boolean assembleParts(InProgressUploads upload) {

		boolean successfulWrite;

		log.debug(">>> assembleParts uploadid=" + upload.getId());

		// Prepare to assemble the parts for the completed upload.
		// Assembled file will be written to a temporary file and then renamed.

		// FDE037 TM 24/02/2016
		
		// Determine where the uploaded file needs to be placed.
		
		String uploadFileName = upload.getFileName();
		String uploadDir = null;

		try {
			
			uploadDir = this.getUploadDir(uploadFileName);
			
		} catch (ResourceTypeNotFoundException e1) {

			// Exception will never be thrown as we check to see if a directory
			// exists before initiating an upload.
		}
		
		uploadFileName = FilenameUtils.normalizeNoEndSeparator(uploadDir + File.separator + uploadFileName);
		
		File outFile = new File(uploadFileName);

		File tempOutFile = new File(uploadFileName + Common.TEMP_FILE_EXTENSION);

		// End FDE037
		
		// Prepare file output stream to write parts to temporary file.
		FileOutputStream fop = null;

		try {

			// Create the upload directory if it doesn't exist
			File fileDir = new File(FilenameUtils.getFullPath(uploadFileName));

			if (!fileDir.exists())
				fileDir.mkdirs();

			// If temporary output file doesn't exist create it
			if (!tempOutFile.exists())
				tempOutFile.createNewFile();

			fop = new FileOutputStream(tempOutFile);

			// Loop through each part writing its data to the temporary file.
			for (UploadParts upPart : upload.getUploadParts()) {

				if (log.isDebugEnabled()) {

					log.debug("Writing part data. part=" + upPart.getId().getPartNoInt() + "\n uploadid="
							+ upload.getId() + "\n filename=" + outFile.getAbsolutePath());
				}

				byte[] partData = getUploadPartDataBytes(upPart.getFileName());

				fop.write(partData);
			}

			fop.flush();
			fop.close();

			// Remove output file if it already exists.
			if (outFile.exists()) {

				if (outFile.delete()) {

					// Now rename temporary file to actual file name.
					if (tempOutFile.renameTo(outFile)) {

						successfulWrite = true;

					} else {

						successfulWrite = false;
					}

				} else {

					successfulWrite = false;
				}

			} else {

				// Now rename temporary file to actual file name.
				if (tempOutFile.renameTo(outFile)) {

					successfulWrite = true;

				} else {

					successfulWrite = false;
				}
			}

		} catch (IOException e) {

			successfulWrite = false;

			log.error("Error assembling parts for upload. id=" + upload.getId() + " \n" + e.getMessage());

			tempOutFile.delete();
			outFile.delete();

		} finally {

			try {

				if (fop != null)
					fop.close();

			} catch (IOException e) {

				log.error("Error assembling parts. id=" + upload.getId() + " \n" + e.getMessage());
			}
		}

		if (successfulWrite) {

			if (log.isDebugEnabled()) {

				log.debug("Upload file successfully assembled:" + outFile.getAbsolutePath());
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< assembleParts");

		return successfulWrite;
	}

	/**
	 * Perform some maintenance and remove any temporary files that are not
	 * linked to an upload part in the database.
	 */
	private void uploadMaintenance(String frInstance) {

		if (log.isDebugEnabled())
			log.debug(">>> uploadMaintenance");

		Map<String, InProgressUploads> uploadCache = new HashMap<String, InProgressUploads>();

		File tempdir = new File(this.tempDir);
		
		if(tempdir.exists()) {
			
			for (File file : tempdir.listFiles()) {

				// Extract the identifier part of the temporary file name.
				String[] fileIds = file.getName().split("_");

				if (fileIds.length > 0) {

					try {

						InProgressUploads upload;

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
		}


		if (log.isDebugEnabled())
			log.debug("<<< uploadMaintenance");
	}

	// End FDP980

	/**
	 * Get a unique identifier for new upload.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * @return unique identifier.
	 * 
	 *         Null is returned if a connection could not be made to database to
	 *         verify if generated id is unique.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found.
	 */
	private String getNewUploadId(String frInstance) throws FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> getNewUploadId");

		String id = UUID.randomUUID().toString();

		while (fileTransferController.isExistingUpload(frInstance, id))
			id = UUID.randomUUID().toString();

		if (log.isDebugEnabled())
			log.debug("<<< getNewUploadId " + id);

		return id;
	}

	/**
	 * Method to initiate a new upload and create a record of it in the
	 * Fieldreach database.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param fileName
	 *            The name of the file to be uploaded.
	 * 
	 * @param totalSize
	 *            The total size in bytes of the file being uploaded.
	 * 
	 * @return Returns an UploadInitiateResponse object populated the success or
	 *         error set. If the Initiation process was a success details of the
	 *         upload including id and the next part expected will also be
	 *         returned in this object.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found.
	 */
	private UploadInitiateResponse initiateNewUpload(String frInstance, String filename, long totalSize)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> initiateNewUpload file=" + filename + " totalSize=" + totalSize);
		}

		UploadInitiateResponse result = new UploadInitiateResponse();
		ErrorMessage err = new ErrorMessage();
		result.setError(err);

		int totalparts = (int) (totalSize / this.maxChunkSize);

		// If there is a remainder then we need to upload an additional part.
		if ((totalSize % this.maxChunkSize) != 0)
			totalparts++;

		String id = this.getNewUploadId(frInstance);

		// Record of upload must be created in the Fieldreach database.
		InProgressUploads upload = new InProgressUploads();
		upload.setId(id);
		upload.setFileName(filename);
		upload.setTotalSize(totalSize);
		upload.setExpectedParts(totalparts);
		upload.setNextPart(1);
		upload.setUploadStarted(Common.generateFieldreachDBDate());
		upload.setUploadParts(null);

		// Create record for upload in the database.
		fileTransferController.startUpload(frInstance, upload);

		// Set the result information.
		result.setIdentifier(id);
		result.setStartFromPart(new BigInteger("1"));
		result.setMaxChunkSizeBytes(this.maxChunkSize);

		// Set the success flag.
		result.setSuccess(true);

		if (log.isDebugEnabled())
			log.debug("<<< initiateNewUpload");

		return result;
	}

	/**
	 * Method to fetch the data from an upload part temporary file.
	 * 
	 * @param fileName
	 *            The name of the temporary upload part file you want to extract
	 *            the data from.
	 * 
	 * @return Data extracted form the temporary upload file specified. Null is
	 *         returned if no data could be retrieved.
	 */
	private byte[] getUploadPartDataBytes(String fileName) {

		if (log.isDebugEnabled())
			log.debug(">>> getUploadPartDataBytes");

		byte[] uploadPartData = null;

		File dir = new File(this.tempDir);

		if (dir.exists()) {

			for (File file : dir.listFiles()) {

				if (file.getName().equals(fileName)) {

					try {

						uploadPartData = Common.getBytesFromFile(file);

					} catch (FileNotFoundException e) {

						log.error("Temp file not found " + e.toString());

					} catch (IOException ex) {

						log.error("IOException getting temp data " + ex.getMessage());
					}
				}
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< getUploadPartDataBytes");

		return uploadPartData;
	}

	/**
	 * Get reason for failure to delete file
	 * 
	 * @param file
	 *            file that could not be deleted.
	 * 
	 * @return message indicating reason for failure to delete file
	 */
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

}
