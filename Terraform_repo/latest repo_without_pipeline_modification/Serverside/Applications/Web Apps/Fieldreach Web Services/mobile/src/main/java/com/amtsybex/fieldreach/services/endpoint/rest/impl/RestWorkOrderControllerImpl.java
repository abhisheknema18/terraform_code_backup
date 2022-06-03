/**
 * Author:  T Murray
 * Date:    20/08/2012
 * Project: FDE019
 * 
 * Copyright AMT-Sybex 2012
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Removal of /v1 namespace from web service end points 
 * and code refactoring.
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse.APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse.APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse.APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse.APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.RetrieveWorkOrderResponse.APPLICATION_VND_FIELDSMART_RETRIEVE_WORKORDER_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.RetrieveWorkOrderResponse.APPLICATION_VND_FIELDSMART_RETRIEVE_WORKORDER_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.WorkIssuedResponse.APPLICATION_VND_FIELDSMART_WORK_ISSUED_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.WorkIssuedResponse.APPLICATION_VND_FIELDSMART_WORK_ISSUED_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_JSON;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DISTRICT_CODE_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_FILE_NAME_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_IDENTIFIER_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.DOWNLOAD_PART_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.WORK_ALLOC_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.WORK_ORDER_NO_DESCRIPTION;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.WorkIssuedFileRefs;
import com.amtsybex.fieldreach.services.download.FileDownloadController;
import com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController;
import com.amtsybex.fieldreach.services.endpoint.rest.RestWorkOrderController;
import com.amtsybex.fieldreach.services.exception.AuthenticationException;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.exception.WorkOrderDownloadException;
import com.amtsybex.fieldreach.services.exception.WorkOrderNotFoundException;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.RetrieveWorkOrderResponse;
import com.amtsybex.fieldreach.services.messages.response.WorkIssuedResponse;
import com.amtsybex.fieldreach.services.messages.types.Attachment;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.WorkAllocationMode;
import com.amtsybex.fieldreach.services.messages.types.WorkIssued;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api(tags = "Work Issued")
@RequestMapping("/workorder")
public class RestWorkOrderControllerImpl extends BaseControllerImpl implements
RestWorkOrderController {

	private static final Logger log = LoggerFactory.getLogger(RestWorkOrderControllerImpl.class.getName());

	@Autowired
	private WorkOrderController workOrderController;

	// FDE023 TM 08/11/2013
	@Autowired
	private FileDownloadController fileDownloadController;

	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	// MEE020 TM 28/02/2013
	// Improved code in the method to have better error and exception handling.

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestWorkOrderController
	 * #workIssued(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@GetMapping(value = "/issued",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_WORK_ISSUED_1_JSON, APPLICATION_VND_FIELDSMART_WORK_ISSUED_1_XML})
	@ApiOperation(value = "Request Work Issued to a User or Workgroup", 
			notes = "This service provides client applications with the ability to request a list of work orders that " +
					"have been assigned to a specific user or workgroup")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include FRInstanceException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - If userCode and/or workgroupCode parameters are missing from the request URI.")
	})
	public ResponseEntity<WorkIssuedResponse> workIssued(@RequestHeader HttpHeaders httpHeaders,
				 @ApiParam(value = WORK_ALLOC_DESCRIPTION) @RequestParam(name = "workAllocationMode", required = false) WorkAllocationMode workAllocationMode)
			throws BadRequestException, AuthenticationException {

		if (log.isDebugEnabled())
			log.debug(">>> workIssued");

		// Initialise response
		WorkIssuedResponse wiResponse = new WorkIssuedResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		wiResponse.setError(errorMessage);

		// List to store the list of WorkIssued objects associated with the
		// specified user.
		List<WorkIssued> workIssuedList = new ArrayList<WorkIssued>();

		// get application identifier
		String applicationIdentifier = Utils
				.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers
			Utils.debugHeaders(log, httpHeaders);
			
			//FDP1302 - MC - Get the usercode from the aut header and compare it to the passed in user
            HPCUsers authUser = this.getUserDetailsFromUserPrincipal(applicationIdentifier);

            String userCode = authUser.getId().getUserCode();

			// workgroupCode parameter required
			String workgroupCode = authUser.getId().getWorkgroupCode();
			// If workAllocationMode is missing or empty then set to U by
			// default.

			// Check that workAllocationMode parameter has been supplied
			if (workAllocationMode == null)
				workAllocationMode = WorkAllocationMode.U;

			// Get list of work by either userCode if it is present, otherwise
			// use workgroupCode
			List<com.amtsybex.fieldreach.backend.model.WorkIssued> workorders = null;

			if (workAllocationMode.equals(WorkAllocationMode.U)) {

				workorders = workOrderController.getWorkOrdersByUserCode(
						applicationIdentifier, userCode, false);

			} else if (workAllocationMode
					.equals(WorkAllocationMode.W)) {

				workorders = workOrderController.getWorkOrdersByWorkGroupCode(
						applicationIdentifier, workgroupCode, false);

			} else if (workAllocationMode
					.equals(WorkAllocationMode.MW)) {

				workorders = workOrderController
						.getWorkOrdersByWorkGroupCodeCat(applicationIdentifier,
								workgroupCode, false);
			}

			workIssuedList = this.getWorkOrderObjects(applicationIdentifier, workorders);

		} 
		catch(AuthenticationException ex) {
            log.warn(ex.getMessage());
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), ex);
        }

		catch (FRInstanceException e) {
			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("Exception in /workissued: " + e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		}
		finally {

			// Determine if the request was a success
			wiResponse.setSuccess(errorMessage.getErrorCode() == null);

			// Set the list of work issued.
			if (wiResponse.isSuccess())
				wiResponse.setWorkIssuedList(workIssuedList);

		}

		if (log.isDebugEnabled())
			log.debug("<<< workIssued");

		return ResponseEntity.ok(wiResponse);
	}

	private List<WorkIssued> getWorkOrderObjects(String applicationIdentifier, List<com.amtsybex.fieldreach.backend.model.WorkIssued> workorders) throws ResourceTypeNotFoundException, FRInstanceException, FileNotFoundException, IOException {

		// List to store the list of WorkIssued objects associated with the
		// specified user.
		List<WorkIssued> workIssuedList = new ArrayList<WorkIssued>();

		// Retrieve the list of work that has been issued to the user
		// specified and create A list of WorkIssued objects from this
		// to write back to the response.
		for (com.amtsybex.fieldreach.backend.model.WorkIssued work : workorders) {

			WorkIssued objWorkIssued = new WorkIssued();

			// Set mandatory information
			objWorkIssued.setWorkOrderNo(work.getWorkOrderNo());
			objWorkIssued.setDistrictCode(work.getDistrictCode());
			objWorkIssued.setIssuedDate(work.getIssuedDateInt());
			objWorkIssued.setWorkgroupCode(work.getWorkgroupCode());

			// Set optional information
			if (work.getWorkOrderDesc() != null)
				objWorkIssued.setWorkOrderDesc(work.getWorkOrderDesc());

			if (work.getIssuedTime() != null)
				objWorkIssued.setIssuedTime(Utils
						.correctFieldreachDBTime(work.getIssuedTimeInt()));

			if (work.getEquipNo() != null)
				objWorkIssued.setEquipNo(work.getEquipNo());

			if (work.getEquipDesc() != null)
				objWorkIssued.setEquipDesc(work.getEquipDesc());

			if (work.getAltEquipRef() != null)
				objWorkIssued.setAltEquipRef(work.getAltEquipRef());

			if (work.getPlanStartDate() != null)
				objWorkIssued.setPlanStartDate(work.getPlanStartDateInt());

			if (work.getReqFinishDate() != null)
				objWorkIssued.setReqFinishDate(work.getReqFinishDateInt());

			if (work.getUserCode() != null)
				objWorkIssued.setUserCode(work.getUserCode());

			if (work.getWoType() != null)
				objWorkIssued.setWoType(work.getWoType());

			if (work.getWoType() != null)
				objWorkIssued.setWoType(work.getWoType());

			if (work.getSourceFileName() != null)
				objWorkIssued.setSourceFileName(work.getSourceFileName());

			if (work.getWorkStatus() != null)
				objWorkIssued.setWorkStatus(work.getWorkStatus());

			if (work.getWorkStatusDate() != null)
				objWorkIssued
				.setWorkStatusDate(work.getWorkStatusDateInt());

			if (work.getWorkStatusTime() != null) {

				objWorkIssued.setWorkStatusTime(Utils
						.correctFieldreachDBTime(work
								.getWorkStatusTimeInt()));
			}

			if (work.getAdditionalText() != null)
				objWorkIssued.setAdditionalText(work.getAdditionalText());

			// FDE029 TM 26/01/2015

			List<Attachment> attachments = new ArrayList<Attachment>();

			List<WorkIssuedFileRefs> wiFileRefs = this.workOrderController
					.findAttachments(applicationIdentifier, work.getId()
							.getWorkOrderNo(), work.getId()
							.getDistrictCode());

			for (WorkIssuedFileRefs attachment : wiFileRefs) {

				String attachmentDir = this.workOrderController.getAttachmentDirURI(
						applicationIdentifier, work);

				String attachmentURI = FilenameUtils.normalizeNoEndSeparator(
						attachmentDir + File.separator +attachment.getId().getFileName());

				File file = new File(attachmentURI);

				// Only add attachment to list if it exists on the file
				// system.
				if (file.exists()) {

					Attachment att = new Attachment();
					att.setFileDesc(attachment.getDescription());
					att.setType(attachment.getType());
					att.setFileName(attachment.getId().getFileName());
					att.setChecksum(Common.generateMD5Checksum(Common
							.getBytesFromFile(file)));

					attachments.add(att);
				}
			}

			objWorkIssued.setAttachments(attachments);

			// End FDE029

			// Get Last Modified Date and time from the work order file
			// itself.
			String woDir = workOrderController.resolveWoUploadDirFromDB(
					applicationIdentifier, work);

			String woFile = this.workOrderController
					.getWorkorderFileName(work);

			File file = new File(woDir + File.separator + woFile);

			if (file.exists()) {

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

				if (work.getWorkStatusDate() != null) {

					objWorkIssued.setFileCreateDate(Integer.parseInt(sdf
							.format(file.lastModified())));
				}

				sdf = new SimpleDateFormat("HHmmss");

				if (work.getWorkStatusTime() != null) {

					objWorkIssued.setFileCreateTime(sdf.format(file
							.lastModified()));
				}
			}

			// Add the WorkIssued object to the list of WorkIssued entries
			// to be returned.
			workIssuedList.add(objWorkIssued);
		}

		return workIssuedList;
	}

	

	// MEE020 TM 28/02/2013
	// Improved code in the method to have better error and exception handling.

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestWorkOrderController
	 * #retrieveWorkOrderSource(javax.servlet.http.HttpServletRequest,
	 * java.lang.String)
	 */
	@Override
	@GetMapping(value = "/{workOrderno}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_RETRIEVE_WORKORDER_1_JSON, APPLICATION_VND_FIELDSMART_RETRIEVE_WORKORDER_1_XML})
	@ApiOperation(value = "Retrieve Work Order Source",
			notes = "This service provides client applications with a means to retrieve work order files from the server. " +
					"The contents of the work order file are returned with base 64 encoding\t\n"
					+ "districtCode(Optional Parameter) - District code of the work order. If this is not supplied a default value of NA will be used.")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include FileNotFoundException, DownloadWOException, FRInstanceException, EXCEPTION(General Exception)"),
	})
	public ResponseEntity<RetrieveWorkOrderResponse> retrieveWorkOrderSource(HttpServletRequest request,
				 @ApiParam(value = WORK_ORDER_NO_DESCRIPTION) @PathVariable("workOrderno") String workOrderNo,
				 @ApiParam(value = DISTRICT_CODE_DESCRIPTION) @RequestParam(name = "districtCode", required = false, defaultValue = Utils.WORKORDER_DEFAULT_DISTRICT) String districtCode) {

		if (log.isDebugEnabled())
			log.debug(">>> retrieveWorkOrderSource workorderno=" + Common.CRLFEscapeString(workOrderNo));

		// Initialise response
		RetrieveWorkOrderResponse woResponse = new RetrieveWorkOrderResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		woResponse.setError(errorMessage);

		// get application identifier
		String applicationIdentifier = Utils.extractApplicationIdentifier(request);

		try {

			// Debug headers
			Utils.debugHeaders(log, request);


			// Check to see if record exists in the work issued table
			final com.amtsybex.fieldreach.backend.model.WorkIssued objWorkOrder = workOrderController
					.getWorkIssuedRecord(applicationIdentifier, workOrderNo,
							districtCode);

			if (objWorkOrder == null) {

				throw new WorkOrderNotFoundException(
						"Entry not found in WorkIssued. workorderno="
								+ workOrderNo + " districtCode=" + districtCode);
			}
			
			// Check the status of the work order is valid.
			if (!workOrderController.checkStatusIsValidForRetrieval(applicationIdentifier, objWorkOrder.getWorkStatus(), false) ) {

				throw new WorkOrderDownloadException(
						"Unable to download workorder " + workOrderNo
						+ " with status: "
						+ objWorkOrder.getWorkStatus().toUpperCase());
			}

			// Get source of the work order specified and set this section
			// of the response.
			String woSource =  workOrderController.getWorkOrderSourceContents(
					workOrderNo, districtCode, applicationIdentifier);
			
			woResponse.setWorkOrderData(woSource);

			// Set checksum in response. Calculate from decoded Base64 data.
			woResponse.setChecksum(Common.generateMD5Checksum(Common.decodeBase64(woSource)));

			// Get the work order source file name.
			woResponse.setFilename(this.workOrderController
					.getWorkorderFileName(objWorkOrder));
			

		} catch (WorkOrderNotFoundException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (WorkOrderDownloadException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.WORKORDER_DOWNLOAD_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (ResourceNotFoundException e) {

			log.error("Workorder file not found. WorkOrderNo = " + Common.CRLFEscapeString(workOrderNo));

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (ResourceTypeNotFoundException e) {

			log.error("Resource type not found. WorkOrderNo = " + Common.CRLFEscapeString(workOrderNo));

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("Exception in /retrieveWorkOrder/{workOrderno}: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
		}
		finally {

			woResponse.setSuccess(errorMessage.getErrorCode() == null);

		}

		if (log.isDebugEnabled())
			log.debug("<<< retrieveWorkOrderSource");

		return ResponseEntity.ok(woResponse);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestWorkOrderController
	 * #initiateWorkOrderAttachmentDownload
	 * (javax.servlet.http.HttpServletRequest, java.lang.String,
	 * java.lang.String)
	 */
	@GetMapping(value = "/{workorderno}/attachment/{filename:.*}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_JSON, APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_XML})
	@ApiOperation(value = "Initiate Work Order Attachment Binary Response Download",
			notes = "This web service provides clients with a means to retrieve files associated with work orders. " +
					"This web service implements the web services multi part download mechanism")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include WorkOrderNotFoundException, MaximumFileSizeExceededException, FileNotFoundException, IOException, FRInstanceException, EXCEPTION(General Exception)"),
	})
	public ResponseEntity<InitiateDownloadResponse> initiateWorkOrderAttachmentDownload(
			@RequestHeader HttpHeaders httpHeaders,
			@ApiParam(value = WORK_ORDER_NO_DESCRIPTION) @PathVariable("workorderno") String workOrderNo,
			@ApiParam(value = DOWNLOAD_FILE_NAME_DESCRIPTION) @PathVariable("filename") String fileName,
			@ApiParam(value = DISTRICT_CODE_DESCRIPTION) @RequestParam(name = "districtCode", required = false, defaultValue = Utils.WORKORDER_DEFAULT_DISTRICT) String districtCode) {

		if (log.isDebugEnabled()) {

			log.debug(">>> initiateWorkOrderAttachmentDownload workOrderNo="
					+ Common.CRLFEscapeString(workOrderNo) + " fileName=" + Common.CRLFEscapeString(fileName));
		}

		// Prepare response object.
		InitiateDownloadResponse objResponse = new InitiateDownloadResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);
		
		String applicationIdentifier = Utils
				.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers.
			Utils.debugHeaders(log, httpHeaders);

			// Check to see if the work order already exists in the WorkIssued
			// table.
			com.amtsybex.fieldreach.backend.model.WorkIssued objWO = null;
			objWO = workOrderController.getWorkIssuedRecord(
					applicationIdentifier, workOrderNo, districtCode);

			// If the objWO object is null then a work order was not found in
			// the the WorkIssued table
			if (objWO == null) {

				throw new WorkOrderNotFoundException("WorkOrder not found "
						+ "in the database. workorderno = " + workOrderNo
						+ " districtCode = " + districtCode);
			}

			// Get the directory that is responsible for holding work order
			// attachments.
			String attachmentDir = workOrderController.getAttachmentDirURI(
					applicationIdentifier, objWO);

			String attachmentFile = attachmentDir + File.separator + fileName;

			// Initialise the download.
			objResponse = this.fileDownloadController
					.initiateFileSystemDownload(applicationIdentifier,
							attachmentFile);

		} catch (WorkOrderNotFoundException e) {

			log.error("/workorder/{workorderno}/attachment/{filename}: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.WORKORDER_NOT_FOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("/workorder/{workorderno}/attachment/{filename}: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		}
		finally {

			objResponse.setSuccess(objResponse.getError().getErrorCode() == null);

		}

		if (log.isDebugEnabled())
			log.debug("<< initiateWorkOrderAttachmentDownload");

		return ResponseEntity.ok(objResponse);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestWorkOrderController
	 * #downloadWorkOrderAttachmentPart(javax.servlet.http.HttpServletRequest,
	 * java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	@GetMapping(value = "/{workorderno}/attachment/{filename:.*}/multipart/{identifier}/{part}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON, APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML})
	@ApiOperation(value = "Work Order Attachment Download",
			notes = "This web service provides clients with a means to retrieve files associated with work orders. " +
					"This web service implements the web services multi part download mechanism")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include InvalidDownloadIdentifierException, PartNumberException, WorkOrderNotFoundException, IOException"),
	})
	public ResponseEntity<DownloadPartResponse> downloadWorkOrderAttachmentPart(@RequestHeader HttpHeaders httpHeaders,
			@ApiParam(value = WORK_ORDER_NO_DESCRIPTION) @PathVariable("workorderno") String workOrderNo,
			@ApiParam(value = DOWNLOAD_FILE_NAME_DESCRIPTION) @PathVariable("filename") String fileName,
			@ApiParam(value = DOWNLOAD_IDENTIFIER_DESCRIPTION) @PathVariable("identifier") String identifier,
			@ApiParam(value = DOWNLOAD_PART_DESCRIPTION) @PathVariable("part") int partNo)  {

		if (log.isDebugEnabled()) {

			log.debug(">>> downloadWorkOrderAttachmentPart workOrderNo="
					+ Common.CRLFEscapeString(workOrderNo) + " fileName=" + Common.CRLFEscapeString(fileName) + " identifier="
					+ Common.CRLFEscapeString(identifier) + " part=" + partNo);
		}

		// Initialise response object
		DownloadPartResponse objResponse = new DownloadPartResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);

		String applicationIdentifier = Utils
				.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers.
			Utils.debugHeaders(log, httpHeaders);

			// Get the part data.
			objResponse = this.fileDownloadController.downloadPart(
					applicationIdentifier, identifier, partNo);

		} catch (Exception e) {

			log.error("/workorder/{workorderno}/attachment/{filename}/multipart/{identifier}/{partno}: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		}
		finally {

			objResponse.setSuccess(objResponse.getError().getErrorCode() == null);

		}

		if (log.isDebugEnabled())
			log.debug("<<< downloadWorkOrderAttachmentPart");

		return ResponseEntity.ok(objResponse);
	}
	

	/* FDP1170 TM 29/04/2016
	 * 
	 * Removed Methods:
	 * 		uploadWorkOrder
	 * 		updateWorkOrder
	 * 		cancelWorkOrder
	 * 		recallWorkOrder
	 * 		registerAttachment
	 */

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

}

/*-------------------------------------------
 - Private Classes
 --------------------------------------------*/

// Class to handle validation errors when validating a workorder against an xsd
class workorderValidationErrorHandler implements ErrorHandler {

	static Logger log = LoggerFactory.getLogger(workorderValidationErrorHandler.class
			.getName());

	@Override
	public void error(SAXParseException e) throws SAXException {

		log.error(e.getMessage());
		throw new SAXException(e.getMessage());
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {

		log.error(e.getMessage());
		throw new SAXException(e.getMessage());
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {

		log.error(e.getMessage());
		throw new SAXException(e.getMessage());
	}
}
