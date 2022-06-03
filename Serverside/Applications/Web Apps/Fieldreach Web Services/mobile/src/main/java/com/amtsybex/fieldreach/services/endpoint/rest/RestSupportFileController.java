/**
 * Author:  T Murray
 * Date:    29/05/2014
 * Project: FDE026
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Removal of /v1 namespace from web service end points 
 * and code re-factoring.
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.SupportFileListResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;


public interface RestSupportFileController {

	/**
	 * Retrieve a list of files contained in a configured support file directory
	 * 
	 * @param httpHeaders
	 *            http Headers
	 * 
	 * @return SupportFileListresponse message.
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	ResponseEntity<SupportFileListResponse> getSupportFileList(HttpHeaders httpHeaders);

	/**
	 * Initiate the multi-part download of the support file specified.
	 * 
	 * @param httpHeaders
	 *            http Headers
	 * 
	 * @param fileName
	 *            Name of the support file to be downloaded.
	 * 
	 * @return InitiateDownloadResponse populated with details of the download.
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	ResponseEntity<InitiateDownloadResponse> initiateSupportFileDownload(HttpHeaders httpHeaders,
																		 String fileName);


	
	/**
	 * Download the specified part of the support file referenced by the
	 * download identifier supplied.
	 * 
	 * @param httpHeaders
	 *            http Headers
	 * 
	 * @param fileName
	 *            Name of the support file to be downloaded.
	 * 
	 * @param identifier
	 *            Unique id to identify the in progress download for which the
	 *            part is being donwloaded.
	 * 
	 * @param partNo
	 *            The part of the support file to be downloaded.
	 * 
	 * @return DownloadPartResponse message containing data for the requested
	 *         part.
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	ResponseEntity<DownloadPartResponse> downloadSupportFilePart(HttpHeaders httpHeaders,
																 String fileName, String identifier, int partNo);


}
