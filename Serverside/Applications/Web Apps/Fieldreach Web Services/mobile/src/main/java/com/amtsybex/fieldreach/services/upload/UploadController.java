/**
 * Author:  T Murray
 * Date:    02/02/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.upload;

import java.util.LinkedHashMap;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse;
import com.amtsybex.fieldreach.services.messages.response.UploadPartResponse;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;

public interface UploadController {

	// FDE029 TM 21/01/2015
	// Modified method signature.
	/**
	 * Initiate an upload through web service
	 * 
	 * @param frInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param identifier
	 *            Optional. Indicate a previously started upload
	 * 
	 * @param filename
	 *            Filename used to store file when complete
	 * 
	 * @param totalSize
	 *            in bytes of file to upload
	 * 
	 * @return a response message indicating the identifier to use in uploading
	 *         parts of the file, and the maximum chunk size to use in that
	 *         process. If the upload has already begun, it will return the
	 *         starting point for the resumed transfer.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found.
	 * 
	 * @throws Exception
	 *             if file upload cannot be initiated, eg file size is too
	 *             large.
	 */
	public UploadInitiateResponse initiate(String frInstance, String identifier, String filename, long totalSize)
			throws FRInstanceException, Exception;

	/**
	 * Upload a part of a file through web service
	 * 
	 * @param frInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param identifier
	 *            previous identifier return in initiate call
	 * 
	 * @param partNo
	 *            part number being uploaded
	 * 
	 * @param datalength
	 *            length of data in this part
	 * 
	 * @param checkSum
	 *            checksum of this part (pre baseb4)
	 * 
	 * @param base64Data
	 *            data encoded in base64
	 * 
	 * @return response indicating success or failure, If complete then complete
	 *         flag will be set, If successful but not complete then next part
	 *         number to update will be returned
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found.
	 * 
	 * @throws Exception
	 */
	public UploadPartResponse receivePart(String frInstance, String identifier, int partNo, long datalength,
			String checkSum, String base64Data) throws FRInstanceException, Exception;

	// FDE037 TM 24/02/2016

	/**
	 * Set the maximum size of a file that can be uploaded.
	 * 
	 * @param maxUploadSize
	 *            Maximum size of a file that can be uploaded, in bytes.
	 */
	public void setMaxUploadSize(String maxUploadSize);

	/**
	 * Set the maximum size of chunks can be uploaded.
	 * 
	 * @param maxChunkSize
	 *            Maximum size of chunks that can be uploaded, in bytes.
	 */
	public void setMaxChunkSize(long maxChunkSize);

	/**
	 * Set the temporary storage location for uploads.
	 * 
	 * @param tempDir
	 */
	public void setTempDir(String tempDir);

	/**
	 * Set the list of file mapping rules.
	 * 
	 * @param fileTypeMappings
	 *            A map where the key is a filename pattern and the value is a
	 *            directory where files matching the filename pattern in the key
	 *            should be stored.
	 */
	public void setFileTypeMapping(LinkedHashMap<String, String> fileTypeMappings);

	/**
	 * Get the upload directory the filename supplied will be placed into.
	 * 
	 * @param filename
	 * The filename to get the upload directory for.
	 * 
	 * @return
	 * The directory the filename will be placed in during upload.
	 * 
	 * @throws ResourceTypeNotFoundException
	 * No upload directory could be found.
	 */
	public String getUploadDir(String filename) throws ResourceTypeNotFoundException;
	
	// End FDP037
}
