package com.amtsybex.fieldreach.services.endpoint.rest;

import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.response.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public interface RestResultHistoryController {

	 /**
		 * Fetch details on the latest result history database for the
		 * DBClass and DBArea passed in. This method should be used for databases
		 * that are stored in directory structure defined in web services configuration
		 * using $DBCLASS and $DBAREA placeholders.
		 * 
		 * @param httpHeaders
		 *            Http Headers.
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
	 ResponseEntity<ResultsHistoryDatabaseResponse> getDatabaseFileDetails(HttpHeaders httpHeaders, String dbClass, String dbArea)
				throws BadRequestException;

		/**
		 * Initiate the multi-part download of the asset database specified.
		 * 
		 * @param httpHeaders
		 *            Http Headers
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
		ResponseEntity<InitiateDownloadResponse> initiateDbDownload(HttpHeaders httpHeaders, String dbClass, String dbArea, String fileName)
				throws BadRequestException;

		/**
		 * Download the specified part of the asset database referenced by the
		 * download identifier supplied.
		 * 
		 * @param httpHeaders
		 *            http Headers
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
															String identifier, int partNo) ;
		
		
		/**
		 * FDE043 - MC
		 * 
		 * Fetch details on the latest result history files for the date,
		 * DBClass and DBArea passed in. This method should be used for files
		 * that are stored in directory structure defined in web services configuration
		 * using $DBCLASS and $DBAREA placeholders.
		 * 
		 * @param httpHeaders 
		 * 			http Headers
		 * @param deltaSearchDate
		 * @param deltaSearchTime
		 * @param dbClass
		 * @param dbArea
		 * @return
		 * @
		 * @throws BadRequestException
		 */
		ResponseEntity<ResultsHistoryDeltaResponse> getDeltaResultsList(HttpHeaders httpHeaders, Integer deltaSearchDate, String deltaSearchTime, String dbClass, String dbArea) throws BadRequestException;
		
		/**
		 * FDE043 - MC
		 *
		 * @param httpHeaders
		 * @param returnId
		 * @return
		 * @
		 */
		ResponseEntity<GetDeltaScriptResultResponse> getDeltaResult(HttpHeaders httpHeaders, int returnId) ;
		
}
