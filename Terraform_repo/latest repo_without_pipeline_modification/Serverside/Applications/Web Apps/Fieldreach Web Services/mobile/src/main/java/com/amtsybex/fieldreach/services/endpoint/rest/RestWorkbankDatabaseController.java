/**
 * Author:  T Murray
 * Date:    20/01/2015
 * Project: FDE029
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import javax.servlet.http.HttpServletRequest;

import com.amtsybex.fieldreach.services.exception.BadRequestException;

public interface RestWorkbankDatabaseController {
    
    /**
	 * Fetch details on the latest workbank database for the
	 * WBClass and DBArea passed in. This method should be used for databases
	 * that are stored in directory structure defined in web services configuration
	 * using $WBCLASS and $DBAREA placeholders.
	 * 
	 * @param request
	 *            Http Request.
	 * 
	 * @return an WorkBankDataBaseResponse message containing details of the
	 *         database name, size and checksum, or error code and description
	 *         if a version of the asset database cannot be found for the
	 *         database class.
	 * 
	 * @
	 * 
	 * @throws BadRequestException
	 *             Query string parameter 'wbClass' is not supplied.
	 */
	public String getDatabaseFileDetails(HttpServletRequest request)
			throws BadRequestException;

	/**
	 * Initiate the multi-part download of the workbank database specified.
	 * 
	 * @param request
	 *            servlet request
	 * 
	 * @param fileName
	 *            Name of the workbank database to be downloaded.
	 * 
	 * @return InitiateDownloadResponse populated with details of the download.
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 *        
	 * @throws BadRequestException
	 *             Query string parameter 'wbClass' is not supplied.
	 */
	public String initiateDbDownload(HttpServletRequest request, String fileName)
			throws BadRequestException;

	/**
	 * Download the specified part of the workbank database referenced by the
	 * download identifier supplied.
	 * 
	 * @param request
	 *            servlet request
	 * 
	 * @param fileName
	 *            Name of the workbank database to be downloaded.
	 * 
	 * @param identifier
	 *            Unique id to identify the in progress download for which the
	 *            part is being downloaded.
	 * 
	 * @param partNo
	 *            The part of the workbank database to be downloaded.
	 * 
	 * @return DownloadPartResponse message containing data for the requested
	 *         part.
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	public String downloadDbPart(HttpServletRequest request, String fileName,
			String identifier, int partNo) ;

}
