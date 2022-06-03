/**
 * Author:  T Murray
 * Date:    20/08/2012
 * Project: FDE019
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import javax.servlet.http.HttpServletRequest;

import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.RetrieveWorkOrderResponse;
import com.amtsybex.fieldreach.services.messages.response.WorkIssuedResponse;
import com.amtsybex.fieldreach.services.messages.types.WorkAllocationMode;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.amtsybex.fieldreach.services.exception.AuthenticationException;
import com.amtsybex.fieldreach.services.exception.BadRequestException;

public interface RestWorkOrderController {

	/**
	 * Query the WorkIssued table and return a list of open work orders that are
	 * assigned to the user specified.
	 * 
	 * @param httpHeaders
	 *            http Headers.
	 * 
	 * @return Returns a WorkIssuedResponse message.
	 * 
	 * @throws BadRequestException
	 *             If userCode or workgroupCode parameters are missing or empty.
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	ResponseEntity<WorkIssuedResponse> workIssued(HttpHeaders httpHeaders, WorkAllocationMode workAllocationMode)
			throws BadRequestException, AuthenticationException;

	/**
	 * Returns the base 64 encoded contents of the work order file specified in
	 * the request. The method determines the filename and location of the work
	 * order file.
	 * 
	 * @param request
	 *            servlet request.
	 * 
	 * @param workOrderNo
	 *            work order number of the work order source to be retrieved.
	 * 
	 * @return Returns a RetrieveWorkOrderResponse message.
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	public ResponseEntity<RetrieveWorkOrderResponse> retrieveWorkOrderSource(HttpServletRequest request,
																			 String workOrderNo,
																			 String districtCode) ;
	
	/**
	 * Initiate the multi-part download of a file associated with a work order.
	 * 
	 * @param httpHeaders
	 *            http Headers.
	 * 
	 * @param workOrderNo
	 *            The work order number of the work order file the attachment to
	 *            be downloaded is associated with.
	 *            
	 * @param districtCode
	 * 			  District code of the work order file the attachment to
	 *            be downloaded is associated with.
	 * 
	 * @param fileName
	 *            The name of the attachment file to be downloaded.
	 *            
	 * 
	 * @return InitiateDownloadResponse populated with details of the download.
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	ResponseEntity<InitiateDownloadResponse> initiateWorkOrderAttachmentDownload(
			HttpHeaders httpHeaders, String workOrderNo, String fileName, String districtCode);
	
	/**
	 * Download the specified part of the file referenced by the download
	 * identifier supplied.
	 * 
	 * @param httpHeaders
	 *            http Headers.
	 * 
	 * @param workOrderNo
	 *            The work order number of the work order file the attachment
	 *            part to be downloaded is associated with.
	 * 
	 * @param fileName
	 *            The name of the attachment file to download a part for
	 * @param identifier
	 * 
	 * @param partNo
	 *            The part of the file to be downloaded.
	 * 
	 * @return DownloadPartResponse message containing data for the requested
	 *         part.
	 * 
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	ResponseEntity<DownloadPartResponse> downloadWorkOrderAttachmentPart(HttpHeaders httpHeaders,
																		 String workOrderNo, String fileName, String identifier, int partNo)
			;

	// End FDE023
}
