/**
 * Author:  T Murray
 * Date:    16/03/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2015
 * 
 * Author : Tony Goodwin
 * Date :   20/06/2012
 * SPR:     FDP940
 * Description : Remove redirect at request of Network Rail.
 *
 * Amended:
 * FDE029 TM 19/01/2015
 * Changes to asset database download process and code re-factoring.
 * 
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.response.DatabaseResponse;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;


public interface RestAssetDatabaseController {
    
    /**
	 * Fetch details on the latest asset hierarchy database for the
	 * DBClass and DBArea passed in. This method should be used for databases
	 * that are stored in directory structure defined in web services configuration
	 * using $DBCLASS and $DBAREA placeholders.
	 * 
	 * @param httpHeaders
	 *            Http Request.
	 * 
	 * @return an AssetDataBaseResponse message containing details of the
	 *         database name, size and checksum, or error code and description
	 *         if a version of the asset database cannot be found for the
	 *         database class.
	 * 
	 * @
	 * 
	 * @throws BadRequestException
	 *             Query string parameter 'dbClass' is not supplied.
	 */
	ResponseEntity<DatabaseResponse> getDatabaseFileDetails(HttpHeaders httpHeaders, String dbClass, String dbArea)
			throws BadRequestException;

	/**
	 * Initiate the multi-part download of the asset database specified.
	 * 
	 * @param httpHeaders
	 *            servlet request
	 * 
	 * @param fileName
	 *            Name of the asset database to be downloaded.
	 * 
	 * @return InitiateDownloadResponse populated with details of the download.
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 *        
	 * @throws BadRequestException
	 *             Query string parameter 'dbClass' is not supplied.
	 */
	ResponseEntity<InitiateDownloadResponse> initiateDbDownload(HttpHeaders httpHeaders, String fileName, String dbClass,
																String dbArea) throws BadRequestException;

	/**
	 * Download the specified part of the asset database referenced by the
	 * download identifier supplied.
	 * 
	 * @param httpHeaders
	 *            servlet request
	 * 
	 * @param fileName
	 *            Name of the asset database to be downloaded.
	 * 
	 * @param identifier
	 *            Unique id to identify the in progress download for which the
	 *            part is being donwloaded.
	 * 
	 * @param partNo
	 *            The part of the asset database to be downloaded.
	 * 
	 * @return DownloadPartResponse message containing data for the requested
	 *         part.
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	ResponseEntity<DownloadPartResponse> downloadDbPart(HttpHeaders httpHeaders, String fileName,
															   String identifier, int partNo);

}
