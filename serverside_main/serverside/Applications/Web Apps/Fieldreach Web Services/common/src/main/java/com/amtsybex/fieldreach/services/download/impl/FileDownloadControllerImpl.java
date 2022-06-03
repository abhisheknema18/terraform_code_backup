/**
 * Author:  T Murray
 * Date:    02/02/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2012
 * 
 * *****************************
 * 
 * Modified: FDE026
 * 			 TM 27/05/2014 
 * 
 * In progress downloads are now tracked in the Fieldreach database rather than 
 * in memory. There is also now no need to persist in the memory record to disk as this
 * is held in the database.
 * 
 * Multiple Fieldreach instance support also implemented
 * 
 * *****************************
 */
package com.amtsybex.fieldreach.services.download.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.InProgressDownloads;
import com.amtsybex.fieldreach.backend.model.Item;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.ScriptResultBlb;
import com.amtsybex.fieldreach.backend.model.ScriptResults;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.services.download.FileDownloadController;
import com.amtsybex.fieldreach.services.download.exception.ActiveDownloadIOException;
import com.amtsybex.fieldreach.services.download.exception.DownloadPartSequenceException;
import com.amtsybex.fieldreach.services.download.exception.InvalidDownloadIdentifierException;
import com.amtsybex.fieldreach.services.download.exception.MaxDownloadSizeExceededException;
import com.amtsybex.fieldreach.services.endpoint.common.FileTransferController;
import com.amtsybex.fieldreach.services.exception.ScriptResultNotFoundException;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

/**
 * A class to facilitate the initiation and download of files in multiple parts.
 */
public class FileDownloadControllerImpl implements FileDownloadController {

	static Logger log = LoggerFactory.getLogger(FileDownloadControllerImpl.class
			.getName());

	// Expected name of properties defined in the spring application context
	// file.
	private static final String MAX_DOWNLOAD_SIZE = "maxDownloadSize";
	private static final String MAX_CHUNK_SIZE = "maxChunkSize";
	private static final String TEMP_DIR = "tempDir";
	private Map<String, String> props;

	// Data layer service objects
	@Autowired
	private ScriptResultsService scriptResultsService;
	
	@Autowired
	private ScriptService scriptService; //FDP1115 TM 15/07/2015

	@Autowired
	private FileTransferController fileTransferController;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	/**
	 * Default constructor
	 */
	public FileDownloadControllerImpl() {

	}

	/*-------------------------------------------
	 - Spring injection Methods
	 --------------------------------------------*/

	/**
	 * Sets the configuration properties for multi part downloads.
	 * 
	 * @param props
	 *            A map containing the multi part download configuration. The
	 *            key of the map is the property name and the value is the
	 *            property value.
	 */
	public void setProperties(Map<String, String> props) {

		log.debug(">>> setProperties props = XXX");

		this.props = props;

		log.debug("<<< setProperties");
	}

	/**
	 * Retrieve the configuration for multi part downloads.
	 * 
	 * @return A map containing the multi part download configuration. The key
	 *         of the map is the property name and the value is the property
	 *         value.
	 */
	public Map<String, String> getProperties() {

		log.debug(">>> getProperties");

		log.debug("<<< getProperties");

		return props;
	}

	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	// FDE023 TM 11/11/2013
	// Provide implementation for this method.

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.download.FileDownloadController#
	 * initiateFileSystemDownload(java.lang.String, java.lang.String)
	 */
	@Override
	public InitiateDownloadResponse initiateFileSystemDownload(
			String frInstance, String fileURI) {

		if(log.isDebugEnabled())
			log.debug(">>> initiateFileSystemDownload file=" + Common.CRLFEscapeString(fileURI));

		InitiateDownloadResponse objResponse = new InitiateDownloadResponse();

		objResponse = this.initiateDownload(frInstance, fileURI, 0);

		log.debug("<<< initiateFileSystemDownload");

		return objResponse;
	}

	// End FDE023

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.download.FileDownloadController#
	 * initiateScriptResultsBlbDownload(java.lang.String, int, int)
	 */
	@Override
	public InitiateDownloadResponse initiateScriptResultsBlbDownload(
			String frInstance, int returnid, int resorderno) {

		log.debug(">>> initiateScriptResultsBlbDownload returnid=" + returnid
				+ "resorderno=" + resorderno);

		// Initiate return object
		InitiateDownloadResponse objResponse = new InitiateDownloadResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);

		try {

			// Attempt to get script result and verify that it is not null
			ScriptResults objResult = this.scriptResultsService
					.getScriptResult(frInstance, returnid, resorderno);

			if (objResult == null)
				throw new ScriptResultNotFoundException(
						"Script result not found: returnid=" + returnid
								+ " resorderno=" + resorderno);

			// Extract binary response from script result and verify that it
			// is not null
			ScriptResultBlb objBinaryResp = objResult.getResultBlob();

			if (objBinaryResp == null)
				throw new ScriptResultNotFoundException(
						"Binary response not found: returnid=" + returnid
								+ " resorderno=" + resorderno);

			// FDP1115 TM 15/07/2015
			// Verify Filename in DB is not null and if it is generate one.
			
			String tempFileName;
			
			if(objBinaryResp.getFileName() == null) {
				
				tempFileName = returnid + "." + resorderno;
				
				// Determine file extension.
				
				// Get the question Type
				ReturnedScripts retScript = 
						this.scriptResultsService.getReturnedScript(frInstance, returnid);
				
				Map<Integer, Item> items = 
						this.scriptService.findScriptItems(frInstance, retScript.getScriptIdInt());
				
				Item item = items.get(objResult.getId().getSequenceNoInt());
								
				if(item.getItemType().equalsIgnoreCase("PHOTOGRAPH")) {
					
					tempFileName = tempFileName + ".jpg";
				}
				else if(item.getItemType().equalsIgnoreCase("BITMAP") || item.getItemType().equalsIgnoreCase("SIGNATURE")) {
					
					tempFileName = tempFileName + ".bmp";
				}
				
			} else {
				
				tempFileName = objBinaryResp.getFileName();
			}

			// FDE034 TM 25/09/2015
			// Always write file to file system to ensure the latest version is retrieved.
			
			byte[] data = Utils.blobToBytes(objBinaryResp.getBlobResult());
				
			boolean dbFileExists = Common.writeBytesToFile(data,
					tempFileName, props.get(TEMP_DIR));

			// End FDE034
			
			// Create entry in the database for the download if the file to be
			// downloaded is extracted from the database and written to the file system
			// successfully.
			
			if (dbFileExists) {
				
				objResponse = this.initiateDownload(frInstance, props.get(TEMP_DIR) + "/"
								+ tempFileName, 1);
			}
			else {
				
				throw new ActiveDownloadIOException("Unable to create data file from database: "
								+ props.get(TEMP_DIR) + "/" + tempFileName);
			}
			
			// End FDP1115
		} 
		catch (ScriptResultNotFoundException e) {

			log.error("initiateScriptResultsBlbDownload: " + e.getMessage());

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		} 
		catch (ActiveDownloadIOException e) {

			log.error("initiateScriptResultsBlbDownload: " + e.getMessage());

			errorMessage.setErrorCode(Utils.IO_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

			objResponse.setSuccess(false);
		}
		catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
		}
		catch(Exception e) {
			
			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
		}
		
		log.debug("<<< initiateScriptResultsBlbDownload file = XXX");

		return objResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.download.FileDownloadController#downloadPart
	 * (java.lang.String, java.lang.String, int)
	 */
	@Override
	public DownloadPartResponse downloadPart(String frInstance,
			String identifier, int partNo) {

		if(log.isDebugEnabled())
			log.debug(">>> downloadPart identifier=" + Common.CRLFEscapeString(identifier) + " partNo=" + partNo);

		// Initiate return object
		DownloadPartResponse objResponse = new DownloadPartResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);

		try {

			// Check that the identifier supplied is a valid active download
			if (!fileTransferController.isExistingDownload(frInstance,
					identifier))
				throw new InvalidDownloadIdentifierException(
						"Download identifier is invalid: " + identifier);

			// Get the current upload by id
			InProgressDownloads download = fileTransferController.findDownload(
					frInstance, identifier);

			// Now check the part requested is valid
			if (partNo <= 0 || partNo > download.getExpectedParts())
				throw new DownloadPartSequenceException(
						"Invalid part number recieved: " + partNo);

			// Extract part data from the download
			byte[] partData = this.getChunk(download, partNo);

			// Populate response object
			objResponse.setPartData(Common.encodeBase64(partData));
			objResponse.setSuccess(true);
		} 
		catch (InvalidDownloadIdentifierException e) {

			log.error("downloadPart: " + e.getMessage());

			errorMessage.setErrorCode(Utils.INVALID_DOWNLOAD_ID_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		} 
		catch (DownloadPartSequenceException e) {

			log.error("downloadPart: " + e.getMessage());

			errorMessage.setErrorCode(Utils.PART_NUMBER_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		}
		// FDE023 TM 11/11/2013
		catch (FileNotFoundException e) {

			log.error("downloadPart: " + e.getMessage());

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		} 
		catch (IOException e) {

			log.error("downloadPart: " + e.getMessage());

			errorMessage.setErrorCode(Utils.IO_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		} 
		catch (FRInstanceException e) {
			
			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage
					.setErrorDescription(e.getMessage());
		}
		// End FDE023
		catch (Exception e) {

			log.error("downloadPart: " + e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		}

		log.debug("<<< downloadPart");

		return objResponse;
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Calculate how many parts a file of the size specified would have to be
	 * downloaded over.
	 * 
	 * @param fileSize
	 *            Size of the file to be downloaded in bytes.
	 * 
	 * @return Number of parts the file should be downloaded over.
	 */
	private int calculateTotalParts(long fileSize) {

		log.debug(">>> calculateTotalParts fileSize=" + fileSize);

		long chunkSize = Long.parseLong(props.get(MAX_CHUNK_SIZE));

		int totalparts = (int) (fileSize / chunkSize);

		// If there is a remainder we need to upload an additional part.
		if ((fileSize % chunkSize) != 0)
			totalparts++;

		log.debug("<<< calculateTotalParts retval=" + totalparts);

		return totalparts;
	}

	/**
	 * Method to extract data for the part specified from the in progress
	 * download supplied.
	 * 
	 * @param download
	 *            InProgressDownloads object to extract part data from.
	 * 
	 * @param partNo
	 *            Part number of the chunk of data to extract from the supplied
	 *            in progress download.
	 * 
	 * @return Chunk of data that corresponds to the part number specified. This
	 *         is un-encoded data.
	 * 
	 * @throws FileNotFoundException
	 *             The file referenced by the download parameter supplied can
	 *             not be found on the file system.
	 * 
	 * @throws IOFoundException
	 *             An error occurred when trying to read the file referenced by
	 *             the download parameter.
	 */
	private byte[] getChunk(InProgressDownloads download, int partNo)
			throws FileNotFoundException, IOException {

		log.debug(">>> getChunk download=xxx downloadDataBytes=xxx partNo="
				+ partNo);

		byte[] retVal = null;

		// Get data from the file being downloaded
		File objFile = new File(download.getFileUri());
		byte[] downloadDataBytes = Common.getBytesFromFile(objFile);

		// If the file is smaller than chunk size return all data, otherwise
		// return data for that part only.
		int chunkSize = Integer.parseInt(props.get(MAX_CHUNK_SIZE));

		if (chunkSize >= downloadDataBytes.length) {

			retVal = downloadDataBytes;
		}
		else {

			int startIdx = (partNo - 1) * chunkSize;
			int endIdx = 0;

			if (partNo == download.getExpectedParts())
				endIdx = downloadDataBytes.length;
			else
				endIdx = (startIdx + chunkSize);

			retVal = Arrays.copyOfRange(downloadDataBytes, startIdx, endIdx);
		}

		log.debug("<<< getChunk");

		return retVal;
	}

	//
	// End FDE023

	/**
	 * Initiate a multi part download for a file located on the file system.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param fileURI
	 *            The URI of the file to be downloaded. The value of this
	 *            parameter should be absolute and include the path to file.
	 * 
	 * @param dbFile
	 *            Value of 1 indicates the file being downloaded is located in
	 *            the Fieldreach database. Value of 0 indicates the file exists
	 *            on the file system.
	 * 
	 * @return InitiateDownloadResponse object populated with initialisation
	 *         information to enable to multipart download.
	 */
	private InitiateDownloadResponse initiateDownload(String frInstance,
			String fileURI, int dbFile) {

		if(log.isDebugEnabled())
			log.debug(">>> initiateDownload fileURI=" + Common.CRLFEscapeString(fileURI) + " dbFile=" + dbFile);

		InitiateDownloadResponse objResponse = new InitiateDownloadResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);

		try {

			File objFile = new File(fileURI);

			// Get data and check that the file being downloaded is not larger
			// than the maximum size allowed:
			byte[] data = Common.getBytesFromFile(objFile);
			long maxSize = Long.parseLong(props.get(MAX_DOWNLOAD_SIZE));

			if (data.length > maxSize) {

				throw new MaxDownloadSizeExceededException(
						"The file you are trying to download exceeds"
								+ " the maximum size. File Size: "
								+ data.length + " bytes. "
								+ "Maximum allowed: " + maxSize + " bytes.");
			}

			int expectedParts = this.calculateTotalParts(data.length);

			String checksum = Common.generateMD5Checksum(data);

			String fileName = objFile.getName();

			// Add the download to the list of in progress downloads
			InProgressDownloads download = new InProgressDownloads();
			download.setExpectedParts(expectedParts);
			download.setFileUri(fileURI);
			download.setDownloadStarted(Common.generateFieldreachDBDate());
			download.setDbFile(dbFile);

			// FDE026 TM 15/09/2014
			// Multi instance backend support
			fileTransferController.startDownload(frInstance, download);
			// End FDE026
			
			// Populate return object
			objResponse.setIdentifier(download.getId());
			objResponse.setChecksum(checksum);
			objResponse.setFileName(fileName);
			objResponse.setTotalParts(BigInteger.valueOf(expectedParts));
			objResponse.setSuccess(true);
		} 
		catch (FileNotFoundException e) {

			log.error("initiateDownload: " + e.getMessage());

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		} 
		catch (IOException e) {

			log.error("initiateDownload: " + e.getMessage());

			errorMessage.setErrorCode(Utils.IO_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		} 
		catch (MaxDownloadSizeExceededException e) {

			log.error("initiateDownload: " + e.getMessage());

			errorMessage.setErrorCode(Utils.MAX_FILESIZE_EXCEEDED);
			errorMessage.setErrorDescription(e.getMessage());

			objResponse.setSuccess(false);
		}
		catch (FRInstanceException e) {
			
			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage
					.setErrorDescription(e.getMessage());
		} 
		catch (Exception e) {

			log.error("initiateFileSystemDownload: " + e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		}

		log.debug("<<< initiateDownload");

		return objResponse;
	}

}