/**
 * Author:  T Murray
 * Date:    20/08/2012
 * Project: FDE019
 *
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.messages.request.RegisterAttachment;
import com.amtsybex.fieldreach.services.messages.request.WorkOrderStatusRequest;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.RetrieveWorkOrderResponse;
import com.amtsybex.fieldreach.services.messages.response.WorkIssuedResponse;

public interface RestWorkOrderController {

	/**
	 * FDE044 - MC
	 * Query the WorkIssued table and return a list of open work orders that are
	 * assigned to the user specified.
	 *
	 * @param httpHeaders
	 *            http Headers.
	 * @return Returns a WorkIssuedResponse message.
	 * @return
	 * @
	 * @throws BadRequestException
	 */
	ResponseEntity<WorkIssuedResponse> workIssuedIWS(HttpHeaders httpHeaders, String userCode, String workgroupCode)
			throws BadRequestException;
	
	/**
	 * FDE044 - MC
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
	 * @throws BadRequestException
	 */
	ResponseEntity<RetrieveWorkOrderResponse> retrieveWorkOrderSourceIWS(HttpServletRequest request, @PathVariable("workOrderno") String workOrderNo, @RequestParam(name = "districtCode") String districtCode);
	
	/**
	 * Initiate the multi-part download of a file associated with a work order. for integration webservices
	 *
	 * @param request
	 *            servlet request.
	 *
	 * @param workOrderNo
	 *            The work order number of the work order file the attachment to
	 *            be downloaded is associated with.
	 *
	 * @param fileName
	 *            The name of the attachment file to be downloaded.
	 *
	 * @return InitiateDownloadResponse populated with details of the download.
	 *
	 * @
	 *             If invalid Authorization token is passed in the request.
	 */
	ResponseEntity<InitiateDownloadResponse> initiateWorkOrderAttachmentDownloadIWS(
			HttpServletRequest request, String workOrderNo, String fileName, String districtCode);
	
	/**
	 * Download the specified part of the file referenced by the download
	 * identifier supplied.
	 *
	 * @param request
	 *            servlet request.
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
	ResponseEntity<DownloadPartResponse> downloadWorkOrderAttachmentPartIWS(HttpServletRequest request,
																			String workOrderNo, String fileName, String identifier, int partNo)
	;

	// End FDE023

	/* FDP1170 TM 29/04/2016
	 *
	 * Removed Methods:
	 * 		uploadWorkOrder
	 * 		updateWorkOrder
	 * 		cancelWorkOrder
	 * 		recallWorkOrder
	 */


	//FDE044 - MC - move webservices from fieldreachint

	/**
	 * Allows clients to upload a work order file to the server. The XML is
	 * parsed and validated and then the file is written to the file system. The
	 * workissued and workstatushistory tables are updated as part of this
	 * process.
	 *
	 * @param request
	 *            servlet request.
	 *
	 * @param workOrderNo
	 *            The work order number of the work order file being uploaded.
	 *
	 * @param workOrderXML
	 *            body of the request. This should be the XML of the workorder.
	 *
	 * @return a CallResponse Message
	 *
	 * @throws BadRequestException
	 *             If no request body is passed or if invalid request body is
	 *             passed.
	 */
	CallResponse uploadWorkOrder(HttpServletRequest request,
												 String workOrderNo, String workOrderXML, boolean releasing) throws BadRequestException;


	/**
	 * FDE051 - MC 
	 * @param request
	 * @param workOrderNo
	 * @param workOrderXML
	 * @return
	 * @throws BadRequestException
	 * @
	 */
	ResponseEntity<CallResponse> issueWorkOrder(HttpServletRequest request, String workOrderNo, String workOrderXML) throws BadRequestException;

	/**
	 * FDE051 - MC 
	 * @param request
	 * @param workOrderNo
	 * @param workOrderXML
	 * @return
	 * @throws BadRequestException
	 * @
	 */
	ResponseEntity<CallResponse> releaseWorkOrder(HttpServletRequest request, String workOrderNo, String workOrderXML) throws BadRequestException;

	/**
	 * FDE051 - MC 
	 * @param request
	 * @param workOrderNo
	 * @return
	 * @
	 */
	ResponseEntity<CallResponse> dispatchWorkOrder(HttpServletRequest request, String workOrderNo, String districtCode);

	/**
	 * Allows clients to update the details of a work order The XML is parsed
	 * and validated and then the file is written to the file system. The
	 * workissued and workstatushistory tables are updated as part of this
	 * process. The old work order file is also deleted.
	 *
	 * @param request
	 *            servlet request.
	 *
	 * @param workOrderNo
	 *            The work order number of the work order file being updated.
	 *
	 * @param workOrderXML
	 *            body of the request. This should be the XML of the workorder.
	 *
	 * @return a CallResponse Message
	 *
	 * @throws BadRequestException
	 *             If no request body is passed or if invalid request body is
	 *             passed.
	 */
	public ResponseEntity<CallResponse> updateWorkOrder(HttpServletRequest request,
														String workOrderNo, String workOrderXML) throws BadRequestException;

	/**
	 * Allows clients to cancel a work order The workissued and
	 * workstatushistory tables are updated as part of this process and the work
	 * order file is also deleted.
	 *
	 * @param workOrderStatusRequest
	 * 				Body of request holding additional Text
	 *
	 * @param request
	 *            servlet request.
	 *
	 * @param workOrderNo
	 *            The work order number of the work order file being cancelled
	 *
	 * @return a CallResponse Message
	 */
	ResponseEntity<CallResponse> cancelWorkOrder(HttpServletRequest request, String workOrderNo, String districtCode, WorkOrderStatusRequest workOrderStatusRequest);


	// FDP1073 MLM 09/12/2014
	/**
	 * Allows clients to recall a work order The workissued and
	 * workstatushistory tables are updated as part of this process and the work
	 * order file is also deleted. FDP1073 PVC#1 - MLM
	 *
	 * @param workStatusRequestMessage
	 * 				Body of request holiding additional Text
	 *
	 * @param request
	 *            servlet request.
	 *
	 * @param workOrderNo
	 *            The work order number of the work order file being cancelled
	 *
	 * @return a CallResponse Message
	 */
	ResponseEntity<CallResponse> recallWorkOrder(HttpServletRequest request, String workOrderNo, WorkOrderStatusRequest workStatusRequestMessage,
												 String districtCode);

	// End FDP1073

	// FDE029 20/01/2015 TM

	/**
	 *
	 * @param request
	 *            servlet request
	 *
	 * @param workorderNo
	 *            workorder number of the work order the attachment is associated with.
	 *
	 * @param registerAttachment
	 *            RegisterAttachment message with details of the workorder attachment
	 *            file to be registered.
	 *
	 * @return UploadInitiateResponseMessage populated with details of the
	 *         upload.
	 *
	 * @throws BadRequestException
	 *             if no request body is supplied, if the request body could not
	 *             be parsed.
	 */
	ResponseEntity<CallResponse> registerAttachment(HttpServletRequest request,
													String workorderNo, RegisterAttachment registerAttachment,
													String districtCode)
			throws BadRequestException;

	// End FDE029
}
