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

import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_XML;
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
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.USER_CODE_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.WORKGROUP_CODE_DESCRIPTION;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.WORK_ORDER_NO_DESCRIPTION;
import static com.amtsybex.fieldreach.services.messages.request.WorkOrderStatusRequest.APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_JSON;
import static com.amtsybex.fieldreach.services.messages.request.WorkOrderStatusRequest.APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_XML;
import static com.amtsybex.fieldreach.services.messages.request.RegisterAttachment.APPLICATION_VND_FIELDSMART_ATTACHMENT_1_XML;
import static com.amtsybex.fieldreach.services.messages.request.RegisterAttachment.APPLICATION_VND_FIELDSMART_ATTACHMENT_1_JSON;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate5.HibernateJdbcException;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.WorkIssuedFileRefs;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.services.download.FileDownloadController;
import com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus.WORKSTATUSDESIGNATION;
import com.amtsybex.fieldreach.services.endpoint.rest.RestWorkOrderController;
import com.amtsybex.fieldreach.services.exception.AttachmentRegistrationException;
import com.amtsybex.fieldreach.services.exception.BadRequestException;
import com.amtsybex.fieldreach.services.exception.ChecksumMismatchException;
import com.amtsybex.fieldreach.services.exception.NoAccessException;
import com.amtsybex.fieldreach.services.exception.WorkOrderCancelException;
import com.amtsybex.fieldreach.services.exception.WorkOrderDispatchException;
import com.amtsybex.fieldreach.services.exception.WorkOrderDownloadException;
import com.amtsybex.fieldreach.services.exception.WorkOrderExistsException;
import com.amtsybex.fieldreach.services.exception.WorkOrderNotFoundException;
import com.amtsybex.fieldreach.services.exception.WorkOrderRecallException;
import com.amtsybex.fieldreach.services.exception.WorkOrderUpdateException;
import com.amtsybex.fieldreach.services.messages.request.RegisterAttachment;
import com.amtsybex.fieldreach.services.messages.request.WorkOrderStatusRequest;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.messages.response.DownloadPartResponse;
import com.amtsybex.fieldreach.services.messages.response.InitiateDownloadResponse;
import com.amtsybex.fieldreach.services.messages.response.RetrieveWorkOrderResponse;
import com.amtsybex.fieldreach.services.messages.response.WorkIssuedResponse;
import com.amtsybex.fieldreach.services.messages.types.Attachment;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.WorkIssued;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.services.utils.xml.XMLUtils;
import com.amtsybex.fieldreach.services.utils.xml.exception.XMLValidationException;
import com.amtsybex.fieldreach.utils.impl.Common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping("/workorder")
@Api(tags = "Work Order Integration")
public class RestWorkOrderControllerImpl extends BaseControllerImpl implements
		RestWorkOrderController {

	private static final Logger log = LoggerFactory.getLogger(RestWorkOrderControllerImpl.class.getName());

	@Autowired
	private WorkOrderController workOrderController;

	// FDE023 TM 08/11/2013
	@Autowired
	private FileDownloadController fileDownloadController;

	//FDE044 - MC
	@Autowired
	private UserService userService;

	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	// MEE020 TM 28/02/2013
	// Improved code in the method to have better error and exception handling.

	@Override
	@Deprecated
	@GetMapping(value = "/list",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_WORK_ISSUED_1_JSON, APPLICATION_VND_FIELDSMART_WORK_ISSUED_1_XML})
	@ApiOperation(value = "Get Work List for a User or Workgroup", notes = "This service provides client applications " +
			"with the ability to request a list of work orders that have been assigned to a specific user or workgroup")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include EXCEPTION(General Exception), AuthorizationException"),
			@ApiResponse(code = 400, message = "Bad Request - If userCode and/or workgroupCode parameters are missing from the request URI."),
	})
	public ResponseEntity<WorkIssuedResponse> workIssuedIWS(@RequestHeader HttpHeaders httpHeaders,
				@ApiParam(value = USER_CODE_DESCRIPTION) @RequestParam(name = "userCode", required = false) String userCode,
				@ApiParam(value = WORKGROUP_CODE_DESCRIPTION) @RequestParam(name = "workgroupCode", required = false) String workgroupCode)
			throws BadRequestException {

		if (log.isDebugEnabled())
			log.debug(">>> workIssuedIWS");

		// Initialise response
		WorkIssuedResponse wiResponse = new WorkIssuedResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		wiResponse.setError(errorMessage);

		// List to store the list of WorkIssued objects associated with the
		// specified user.
		List<WorkIssued> workIssuedList = new ArrayList<>();

		// get application identifier
		String applicationIdentifier = Utils
				.extractApplicationIdentifier(httpHeaders);
		
		try {

			// Debug headers
			Utils.debugHeaders(log, httpHeaders);

			if ((userCode == null || userCode.trim().equals("")) && (workgroupCode == null || workgroupCode.trim().equals("")))
				throw new BadRequestException("userCode or WorkgroupCode must be supplied");

			//FDE044
			// Get the system user linked to the mobile user.
			String autUserCode = getSystemUserFromUserPrincipal().getId();

			List<String> accessibleWorkGroupCodes = null;
			try {

				if(!this.userService.hasUnlimitedAccessibleWorkgroups(applicationIdentifier, autUserCode)) {
					
					accessibleWorkGroupCodes = new ArrayList<String>();
					
					List<HPCWorkGroups> accessibleWorkgroups = this.userService.getAccessibleWorkgroups(applicationIdentifier, autUserCode);

					for (HPCWorkGroups wg : accessibleWorkgroups)
						accessibleWorkGroupCodes.add(wg.getWorkgroupCode());
				}
			}catch(UserNotFoundException e) {
				throw new NoAccessException("Access to work orders is not Authorized");
			}
			
			List<com.amtsybex.fieldreach.backend.model.WorkIssued> workorders = null;

			if (userCode != null && userCode.length() > 0) {


				List<HPCUsers> users = userService.findHPCUsersByUserCode(applicationIdentifier, userCode);

				if(accessibleWorkGroupCodes != null) {
					for(HPCUsers user : users) {
						if(!accessibleWorkGroupCodes.contains(user.getId().getWorkgroupCode())) {
							throw new NoAccessException("Access to work orders is not Authorized");
						}
					}
				}
				
				workorders = workOrderController.getWorkOrdersByUserCode(
						applicationIdentifier, userCode, true);

			} else if (workgroupCode != null && workgroupCode.length() > 0) {

				if(accessibleWorkGroupCodes != null) {
					if(!accessibleWorkGroupCodes.contains(workgroupCode)) {
						throw new NoAccessException("Access to work orders is not Authorized");
					}
				}

				workorders = workOrderController.getWorkOrdersByWorkGroupCode(
						applicationIdentifier, workgroupCode, true);
			}

			workIssuedList = this.getWorkOrderObjects(applicationIdentifier, workorders);


		} catch (BadRequestException e) {

			// In case of BadRequestException failure.. rethrow for handler to
			// set.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch(NoAccessException e) {
			
			log.error(e.getMessage());
			
			errorMessage.setErrorCode(Utils.AUTHORIZATION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (XmlMappingException | IOException e) {
			
			log.error("XmlMappingException in /getWorkList" + e.getMessage());
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Empty Body in getWorkList");
			
		} catch (Exception e) {

			log.error("Exception in /getWorkList: " + e.getMessage());

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
			log.debug("<<< workIssuedIWS");

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
	
	
	@Override
	@Deprecated
	@GetMapping(value = "/{workOrderno}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_RETRIEVE_WORKORDER_1_JSON, APPLICATION_VND_FIELDSMART_RETRIEVE_WORKORDER_1_XML})
	@ApiOperation(value = "Get Work Order Source", 
			notes = "This service provides integration applications with a means to get work order files from the server. " +
					"The contents of the work order file are returned with base 64 encoding")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include FileNotFoundException, DownloadWOException, FRInstanceException, EXCEPTION(General Exception)"),
	})
	public ResponseEntity<RetrieveWorkOrderResponse> retrieveWorkOrderSourceIWS(HttpServletRequest request, 
				@ApiParam(value = WORK_ORDER_NO_DESCRIPTION) @PathVariable("workOrderno") String workOrderNo,
				@ApiParam(value = DISTRICT_CODE_DESCRIPTION) @RequestParam(name = "districtCode", required = false, defaultValue = Utils.WORKORDER_DEFAULT_DISTRICT) String districtCode) {


		if (log.isDebugEnabled())
			log.debug(">>> retrieveWorkOrderSourceIWS");

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
			
			//FDE044 - MC - validate against system access groups
			if(objWorkOrder.getWorkgroupCode() != null && !this.hasIWSAccessToWorkOrdersInGroup(request, objWorkOrder.getWorkgroupCode(), applicationIdentifier)) {
				throw new NoAccessException("Access to work orders is not Authorized");
			}
			//end FDE044
			
			// Check the status of the work order is valid.
			if (!workOrderController.checkStatusIsValidForRetrieval(applicationIdentifier, objWorkOrder.getWorkStatus(), true)) {

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
			
						
		} catch(NoAccessException e) {
			
			log.error(e.getMessage());
			
			errorMessage.setErrorCode(Utils.AUTHORIZATION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
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

		}
		finally {

			woResponse.setSuccess(errorMessage.getErrorCode() == null);

		}

		if (log.isDebugEnabled())
			log.debug("<<< retrieveWorkOrderSourceIWS");

		return ResponseEntity.ok(woResponse);

	}

	@Override
	@Deprecated
	@GetMapping(value = "/{workorderno}/attachment/{filename:.*}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_JSON, APPLICATION_VND_FIELDSMART_INITIATE_DOWNLOAD_1_XML})
	@ApiOperation(value = "Initiate Work Order Attachment Binary Response Download",
			notes = "This web service provides clients with a means to retrieve files associated with work orders. " +
					"This web service implements the web services multi part download mechanism")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include AuthorizationException, WorkOrderNotFoundException, MaximumFileSizeExceededException, FileNotFoundException, IOException, FRInstanceException, EXCEPTION(General Exception)"),
	})
	public ResponseEntity<InitiateDownloadResponse> initiateWorkOrderAttachmentDownloadIWS(
			HttpServletRequest request,
			@ApiParam(value = WORK_ORDER_NO_DESCRIPTION) @PathVariable("workorderno") String workOrderNo,
			@ApiParam(value = DOWNLOAD_FILE_NAME_DESCRIPTION) @PathVariable("filename") String fileName,
			@ApiParam(value = DISTRICT_CODE_DESCRIPTION) @RequestParam(name = "districtCode", required = false, defaultValue = Utils.WORKORDER_DEFAULT_DISTRICT) String districtCode) {

		if (log.isDebugEnabled()) {

			log.debug(">>> initiateWorkOrderAttachmentDownloadIWS workOrderNo="
					+ Common.CRLFEscapeString(workOrderNo) + " fileName=" + Common.CRLFEscapeString(fileName));
		}

		// Prepare response object.
		InitiateDownloadResponse objResponse = new InitiateDownloadResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);
		
		String applicationIdentifier = Utils
				.extractApplicationIdentifier(request);

		try {

			// Debug headers.
			Utils.debugHeaders(log, request);

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
			
			//FDE044 - MC - validate against system access groups
			if(!this.hasIWSAccessToWorkOrdersInGroup(request, objWO.getWorkgroupCode(), applicationIdentifier)) {
				throw new NoAccessException("Access to work orders is not Authorized");
			}
			//end FDE044

			// Get the directory that is responsible for holding work order
			// attachments.
			String attachmentDir = workOrderController.getAttachmentDirURI(
					applicationIdentifier, objWO);

			String attachmentFile = attachmentDir + File.separator + fileName;

			// Initialise the download.
			objResponse = this.fileDownloadController
					.initiateFileSystemDownload(applicationIdentifier,
							attachmentFile);

		} catch(NoAccessException e) {
			
			log.error(e.getMessage());
			
			errorMessage.setErrorCode(Utils.AUTHORIZATION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (WorkOrderNotFoundException e) {

			log.error("/getWorkorder/{workorderno}/attachment/{filename}: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.WORKORDER_NOT_FOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("/getWorkorder/{workorderno}/attachment/{filename}: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		}
		finally {

			objResponse.setSuccess(objResponse.getError().getErrorCode() == null);

		}

		if (log.isDebugEnabled())
			log.debug("<< initiateWorkOrderAttachmentDownloadIWS");

		return ResponseEntity.ok(objResponse);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestWorkOrderController
	 * #downloadWorkOrderAttachmentPartIWS(javax.servlet.http.HttpServletRequest,
	 * java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	@Deprecated
	@GetMapping(value = "/{workorderno}/attachment/{filename:.*}/multipart/{identifier}/{part}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_JSON, APPLICATION_VND_FIELDSMART_DOWNLOAD_PART_1_XML})
	@ApiOperation(value = "Work Order Attachment Download",
			notes = "This web service provides clients with a means to retrieve files associated with work orders. " +
					"This web service implements the web services multi part download mechanism")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include InvalidDownloadIdentifierException, PartNumberException, WorkOrderNotFoundException, IOException, FRInstanceException, EXCEPTION(General Exception)"),
	})
	public ResponseEntity<DownloadPartResponse> downloadWorkOrderAttachmentPartIWS(HttpServletRequest request,
		   @ApiParam(value = WORK_ORDER_NO_DESCRIPTION) @PathVariable("workorderno") String workOrderNo,
		   @ApiParam(value = DOWNLOAD_FILE_NAME_DESCRIPTION) @PathVariable("filename") String fileName,
		   @ApiParam(value = DOWNLOAD_IDENTIFIER_DESCRIPTION) @PathVariable("identifier") String identifier,
		   @ApiParam(value = DOWNLOAD_PART_DESCRIPTION) @PathVariable("part") int partNo)  {

		if (log.isDebugEnabled()) {

			log.debug(">>> downloadWorkOrderAttachmentPartIWS workOrderNo="
					+ Common.CRLFEscapeString(workOrderNo) + " fileName=" + Common.CRLFEscapeString(fileName) + " identifier="
					+ Common.CRLFEscapeString(identifier) + " part=" + partNo);
		}

		// Initialise response object
		DownloadPartResponse objResponse = new DownloadPartResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);
		
		String applicationIdentifier = Utils
				.extractApplicationIdentifier(request);

		try {

			// Debug headers.
			Utils.debugHeaders(log, request);

			//FDE044 SYSTEM ACCESS GROUP VALIDATION
			
			// Check to see if the work order already exists in the WorkIssued
			// table.
			com.amtsybex.fieldreach.backend.model.WorkIssued objWO = workOrderController.getWorkIssuedRecord(applicationIdentifier, workOrderNo);

			// If the objWO object is null then a work order was not found in
			// the the WorkIssued table
			if (objWO == null) {

				throw new WorkOrderNotFoundException("WorkOrder not found "
						+ "in the database. workorderno = " + workOrderNo);
			}
						
			if(!this.hasIWSAccessToWorkOrdersInGroup(request, objWO.getWorkgroupCode(), applicationIdentifier)) {
				throw new NoAccessException("Access to work orders is not Authorized");
			}
			//end FDE044
			
			// Get the part data.
			objResponse = this.fileDownloadController.downloadPart(applicationIdentifier, identifier, partNo);

		} catch(NoAccessException e) {
			
			log.error(e.getMessage());
			
			errorMessage.setErrorCode(Utils.AUTHORIZATION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("/getWorkorder/{workorderno}/attachment/{filename}/multipart/{identifier}/{partno}: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		}
		finally {

			objResponse.setSuccess(objResponse.getError().getErrorCode() == null);

		}

		if (log.isDebugEnabled())
			log.debug("<<< downloadWorkOrderAttachmentPartIWS");

		return ResponseEntity.ok(objResponse);
	}

	// End FDE023

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


	// FDP1170 TM 29/04/2016
	// Removed determineDistrictCode and extractDistrictFromXML

	//FDE044 - MC - move fieldreachint webservices

	/*-------------------------------------------
	- Public Methods
	--------------------------------------------*/

	
	/*
	 * FDE051 - MC 
	 * (non-Javadoc)
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestWorkOrderController#dispatchWorkOrder(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@Override
	@PutMapping(value = "/dispatch/{workOrderNo}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CALL_1_JSON, APPLICATION_VND_FIELDSMART_CALL_1_XML})
	@ApiOperation(value = "Dispatch a FieldSmart Work Order", notes = "This web service is responsible for dispatching work orders. " +
					"Dispatched work orders are made available for mobile users")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include InvalidDownloadIdentifierException, PartNumberException, WorkOrderNotFoundException, WODispatchException, IOException, EXCEPTION(General Exception)"),
	})
	public ResponseEntity<CallResponse> dispatchWorkOrder(HttpServletRequest request, @PathVariable("workOrderNo") String workOrderNo,
														  @RequestParam(name = "districtCode", required = false, defaultValue = Utils.WORKORDER_DEFAULT_DISTRICT) String districtCode) {

		if (log.isDebugEnabled())
			log.debug(">>> dispatchWorkOrder workOrderNo=" + Common.CRLFEscapeString(workOrderNo));

		CallResponse objResponse = new CallResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);
		
		String applicationIdentifier = Utils.extractApplicationIdentifier(request);

		try {
			
			com.amtsybex.fieldreach.backend.model.WorkIssued objWO = null;
			objWO = workOrderController.getWorkIssuedRecord(applicationIdentifier, workOrderNo, districtCode);

			// If the objWO object is null then a work order was not found in
			// the WorkIssued table
			if (objWO == null) {

				// Throw exception to prevent further processing
				throw new WorkOrderNotFoundException(
						"WorkOrder not found in the database."
								+ "workorderno = " + workOrderNo + " districtCode = "
								+ districtCode);

			}
			
			//FDE044 - MC - validate against system access groups
			if(!this.hasIWSAccessToWorkOrdersInGroup(request, objWO.getWorkgroupCode(), applicationIdentifier)) {
				throw new NoAccessException("Access to work orders is not Authorized");
			}
			//end FDE044
			
			if(objWO.getWorkgroupCode() == null || objWO.getWorkgroupCode().length() == 0) {
				throw new Exception("Failed to dispatch. Work order has not yet been assigned");
			}
			
			//FDE053 - MC - get work status from db
			validateWorkorderStateIsPreDispatch(workOrderNo, applicationIdentifier, objWO);

			this.workOrderController.dispatchWorkOrder(applicationIdentifier, workOrderNo, districtCode);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch(NoAccessException e) {
			
			log.error(e.getMessage());
			
			errorMessage.setErrorCode(Utils.AUTHORIZATION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (WorkOrderNotFoundException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.WORKORDER_NOT_FOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (WorkOrderDispatchException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.WORKORDER_DISPATCH_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("Exception in /dispatchwork: " + e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		}
		finally {

			objResponse.setSuccess(errorMessage.getErrorCode() == null);

		}

		if (log.isDebugEnabled())
			log.debug("<<<dispatchWorkOrder");

		return ResponseEntity.ok(objResponse);

	}

	void validateWorkorderStateIsPreDispatch(String workOrderNo, String applicationIdentifier, com.amtsybex.fieldreach.backend.model.WorkIssued objWO) throws FRInstanceException, WorkOrderDispatchException {

		if (!workOrderController.getWorkStatuses().getPreDispatchStatusList(applicationIdentifier).contains(objWO.getWorkStatus())) {

			throw new WorkOrderDispatchException(
					"Unable to dispatch workorder " + workOrderNo
					+ " with status: " + objWO.getWorkStatus());
		}
	}

	// MEE020 TM 28/02/2013
	// Improved code in the method to have better error and exception handling.

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestWorkOrderController
	 * #uploadWorkOrder(javax.servlet.http.HttpServletRequest, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@PutMapping(value = "/issue/{workOrderNo}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CALL_1_JSON, APPLICATION_VND_FIELDSMART_CALL_1_XML},
			consumes = {APPLICATION_XML})
	@ApiOperation(value = "Issue a FieldSmart Work Order", 
			notes = "This web service is responsible for validating the work order content, updating the FieldSmart " +
					"database and finally creating the work order XML file on the server. Once the work order " +
					"is issued to FieldSmart it will then be made available for download onto the mobile device of " +
					"any workgroup/user to which it has been allocated.")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include WorkOrderSchemaNotFoundException, WorkOrderValidationException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - Malformed or missing XML in the Request body."),
	})
	public ResponseEntity<CallResponse> issueWorkOrder(HttpServletRequest request,
													   @PathVariable("workOrderNo") String workOrderNo,
													   @RequestBody String workOrderXML) throws BadRequestException {
		
		return ResponseEntity.ok(this.uploadWorkOrder(request, workOrderNo, workOrderXML, false));
	}
	
	//FDE051 - MC  - moved issue work header out to two functions
	@Override
	@PutMapping(value = "/release/{workOrderNo}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CALL_1_JSON, APPLICATION_VND_FIELDSMART_CALL_1_XML},
			consumes = {APPLICATION_XML})
	@ApiOperation(value = "Release a FieldSmart Work Order",
			notes = "This service is provided for use by 3rd party systems for the purpose of releasing work orders to the FieldSmart mobile solution. " +
					"The service validates the work order content, updates the FieldSmart database and creates the work order XML file on the server. " +
					"Once the work order has been released to FieldSmart it becomes available for assignment and dispatch via FieldSmart Office.")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include WorkOrderSchemaNotFoundException, WorkOrderValidationException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - Malformed or missing XML in the Request body."),
	})
	public ResponseEntity<CallResponse> releaseWorkOrder(HttpServletRequest request,
														 @PathVariable("workOrderNo") String workOrderNo,
														 @RequestBody(required = true) String workOrderXML) throws BadRequestException {

		
		return ResponseEntity.ok(this.uploadWorkOrder(request, workOrderNo, workOrderXML, true));
	}
	
	public CallResponse uploadWorkOrder(HttpServletRequest request, String workOrderNo, String workOrderXML, boolean releasing) throws BadRequestException {

		if (log.isDebugEnabled()) {

			log.debug(">>> uploadWorkOrder workOrderNo=" + Common.CRLFEscapeString(workOrderNo)
					+ "requestBody=XXX");
		}

		CallResponse objResponse = new CallResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);

		// Get district code if this has been passed in.
		String districtCode = this.determineDistrictCode(request, workOrderXML);

		// Get application identifier
		String applicationIdentifier = Utils
				.extractApplicationIdentifier(request);

		try {

			// Debug headers
			Utils.debugHeaders(log, request);
			if (log.isDebugEnabled())
				log.debug("Body :" + Common.CRLFEscapeString(workOrderXML));

			// Body passed?
			if (workOrderXML == null || workOrderXML.trim().equals("")) {

				throw new BadRequestException(
						"Empty request body in /issuework");
			}

			// Check to see if the work order already exists in the WorkIssued
			// table.
			com.amtsybex.fieldreach.backend.model.WorkIssued objWO = null;

			objWO = workOrderController.getWorkIssuedRecord(
					applicationIdentifier, workOrderNo, districtCode);

			// If the objWO object is not null then a work order has been found
			// in the the WorkIssued table.
			if (objWO != null) {

				throw new WorkOrderExistsException(
						"WorkOrder already exists in the database."
								+ "workorderno = " + workOrderNo
								+ " districtCode = " + districtCode);
			}

			Document doc = null;

			try {

				// Parse and validate the work order XML passed.
				doc = workOrderController
						.parseAndValidateWorkOrder(workOrderXML);

			} catch (NullPointerException e) {

				log.error("Error loading schema from classpath: "
						+ workOrderController.getWorkOrderSchema());

				errorMessage.setErrorCode(Utils.WORKORDER_SCHEMA_EXCEPTION);
				errorMessage
				.setErrorDescription("Error loading schema from classpath: "
						+ workOrderController.getWorkOrderSchema());
				objResponse.setSuccess(false);

				throw new XMLValidationException(e.getMessage());

			} catch (SAXException e) {

				log.error(e.getMessage());

				errorMessage.setErrorCode(Utils.WORKORDER_VALIDATION_EXCEPTION);
				errorMessage.setErrorDescription(e.getMessage());
				objResponse.setSuccess(false);

				throw new XMLValidationException(e.getMessage());

			} catch (ParserConfigurationException e) {

				log.error(e.getMessage());

				errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
				errorMessage.setErrorDescription(e.getMessage());
				objResponse.setSuccess(false);

				throw new XMLValidationException(e.getMessage());
			}


			String WGCode = validateDocumentAndGetWGcode(objResponse, errorMessage, doc);

			//if issuing a work order or releasing a work order with an assigned work group
			if(!this.hasIWSAccessToWorkOrdersInGroup(request, WGCode, applicationIdentifier)) {
				throw new NoAccessException("Access to work orders is not Authorized");
			}
			
			//end FDE044

			// Create the work order file name.
			String woFileName = Utils.WORKORDER_FILE_PREFIX + workOrderNo
					+ Utils.WORKORDER_FILE_EXTENSION;

			// Determine the directory to write the work order file to.
			String woDir = workOrderController.resolveWoUploadDirFromXML(applicationIdentifier, doc);

			// Bug 2261 TM 04/09/2013

			byte[] woBytes;

			try {

				woBytes = workOrderXML.getBytes(Common.UTF8_ENCODING);

			} catch (UnsupportedEncodingException e) {

				woBytes = workOrderXML.getBytes();
			}

			// Write the work order file to the file system.
			// If the file gets written to the file system then update the
			// database accordingly.
			if (!Common.writeBytesToFile(woBytes, woFileName, woDir)) {

				throw new IOException("Unable to write work order file: "
						+ woDir + File.separator + woFileName);
			}

			// End Bug 2261

			// Catch any kind of database exception here and delete file
			// created.
			try {
				// FDE026 TM 15/09/2014
				// Multi instance backend support
				workOrderController.logWorkOrderUpload(applicationIdentifier, getSystemUserFromUserPrincipal().getId(),
						doc, districtCode, releasing);
				// End FDE026

			} catch (FRInstanceException e) {

				log.error("Error updating the Fieldreach database");

				// Delete the written file if database can't be updated.
				File woFile = new File(woDir + "/" + woFileName);

				if (woFile.exists())
					woFile.delete();

				throw e;
			}

		} catch (BadRequestException e) {

			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch(NoAccessException e) {
			
			log.error(e.getMessage());
			
			errorMessage.setErrorCode(Utils.AUTHORIZATION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
			
		} catch (WorkOrderExistsException e) {

			log.error("WorkOrderExistsException in /issuework "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.WORKORDER_ALREADY_EXISTS_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch (XmlMappingException e) {

			log.error("XmlMappingException in /issuework " + e.getMessage());

			// if we are unable to parse the XML it is invalid and a 400
			// BadRequest needs to be returned.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (ResourceTypeNotFoundException e) {

			// Thrown by fileResourceController.getFileTypeDir
			log.error("Unable to resolve directory to write work order file to: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage
			.setErrorDescription("Unable to resolve directory to write work order file to: "
					+ e.getMessage());
			objResponse.setSuccess(false);

		} catch (IOException e) {

			log.error("IOExcepton in /issuework " + e.getMessage());

			errorMessage.setErrorCode(Utils.IO_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch (XMLValidationException e) {

			// exception thrown previously in this method where error
			// information will already have been set.

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch (Exception e) {

			log.error("Exception in /issuework: " + e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		}
		finally {

			objResponse.setSuccess(errorMessage.getErrorCode() == null);

		}

		if (log.isDebugEnabled())
			log.debug("<<< uploadWorkOrder");

		return objResponse;
	}

	String validateDocumentAndGetWGcode(CallResponse objResponse, ErrorMessage errorMessage, Document doc) throws XMLValidationException {
		//FDE044 - MC - validate against system acess groups
		XMLUtils xmlUtils = new XMLUtils();
		String WGCode = xmlUtils.evaluateXPathToString(doc, "//" + Utils.WORKORDER_WORKGROUPCODE_ELEMENT);

		if(WGCode == null || WGCode.length() == 0) {
			
			errorMessage.setErrorCode(Utils.WORKORDER_VALIDATION_EXCEPTION);
			errorMessage.setErrorDescription("Work group code null or missing");
			objResponse.setSuccess(false);
			
			throw new XMLValidationException();
		}

		String dateString = xmlUtils.evaluateXPathToString(doc, "//" + Utils.WORKORDER_PLANSTARTDATE_ELEMENT);

		if(StringUtils.isNotEmpty(dateString) && dateString.length() != 8) {
			errorMessage.setErrorCode(Utils.WORKORDER_VALIDATION_EXCEPTION);
			errorMessage.setErrorDescription("PlanStartDate format exception");
			objResponse.setSuccess(false);
			throw new XMLValidationException();
		}

		dateString = xmlUtils.evaluateXPathToString(doc, "//" + Utils.WORKORDER_REQFINISHDATE_ELEMENT);

		if(StringUtils.isNotEmpty(dateString) && dateString.length() != 8) {
			errorMessage.setErrorCode(Utils.WORKORDER_VALIDATION_EXCEPTION);
			errorMessage.setErrorDescription("ReqFinishDate format exception");
			objResponse.setSuccess(false);
			throw new XMLValidationException();
		}	
		
		return WGCode;		
	}


	// MEE020 TM 28/02/2013
	// Improved code in the method to have better error and exception handling.

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestWorkOrderController
	 * #updateWorkOrder(javax.servlet.http.HttpServletRequest, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@Deprecated
	@PutMapping(value = "/update/{workOrderNo}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CALL_1_JSON, APPLICATION_VND_FIELDSMART_CALL_1_XML},
			consumes = {APPLICATION_XML})
	@ApiOperation(value = "Update a FieldSmart Work Order", 
			notes = "This web service is responsible for updating a work order that has previously been issued to FieldSmart. " +
					"The service will validate the new work order detail, update work order references in the FieldSmart " +
					"database and will replace the previous work order XML file on the server. " +
					"The updated work order will then be made available for download onto a mobile device. " +
					"The update service can be used to reallocate the work order to a different mobile workgroup/user " +
					"or simply to change some characteristics of the work order e.g. work order type.")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include WorkOrderNotFoundException, WOUpdateException, WorkOrderSchemaNotFoundException, WorkOrderValidationException, DatabaseException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - No request body is passed."),
	})
	public ResponseEntity<CallResponse> updateWorkOrder(HttpServletRequest request,
														@PathVariable("workOrderNo") String workOrderNo,
														@RequestBody String workOrderXML) throws BadRequestException {

		if (log.isDebugEnabled()) {

			log.debug(">>> updateWorkOrder workOrderNo=" + Common.CRLFEscapeString(workOrderNo)
					+ "requestBody=XXX");
		}

		CallResponse objResponse = new CallResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);

		// Get application identifier
		String applicationIdentifier = Utils
				.extractApplicationIdentifier(request);

		try {

			// Debug headers
			Utils.debugHeaders(log, request);
			if (log.isDebugEnabled())
				log.debug("Body :" + Common.CRLFEscapeString(workOrderXML));


			// Body passed?
			if (workOrderXML == null || workOrderXML.trim().equals("")) {

				throw new BadRequestException(
						"Empty request body in /updatework");
			}
			
			// Get district code if this has been passed in.
			String districtCode = this.determineDistrictCode(request, workOrderXML);

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

			// FDE026 TM 28/05/2014

			// Do not allow the work order to be updated if the status matches
			// the configured close work status.
			//FDE053 - MC - get work status from db
			if (this.isWorkStatusClosed(applicationIdentifier, objWO)) {

				throw new WorkOrderUpdateException(
						"Unable to update workorder " + workOrderNo
						+ " with status: " + objWO.getWorkStatus());
			}

			// End FDE026

			Document doc = null;

			try {

				// Parse and validate the work order XML passed.
				doc = workOrderController
						.parseAndValidateWorkOrder(workOrderXML);

			} catch (NullPointerException e) {

				// Handle errors loading the schema file

				log.error("Error loading schema from classpath: "
						+ workOrderController.getWorkOrderSchema());

				errorMessage.setErrorCode(Utils.WORKORDER_SCHEMA_EXCEPTION);
				errorMessage
				.setErrorDescription("Error loading schema from classpath: "
						+ workOrderController.getWorkOrderSchema());
				objResponse.setSuccess(false);

				// Re-throw to halt code execution and force the XML response to
				// be returned
				// to the client.
				throw e;

			} catch (SAXException e) {

				// Handle errors validating against the schema
				log.error(e.getMessage());

				errorMessage.setErrorCode(Utils.WORKORDER_VALIDATION_EXCEPTION);
				errorMessage.setErrorDescription(e.getMessage());
				objResponse.setSuccess(false);

				throw new XMLValidationException(e.getMessage());

			} catch (ParserConfigurationException e) {

				// Handle general errors when parsing/validating schema
				log.error(e.getMessage());

				errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
				errorMessage.setErrorDescription(e.getMessage());
				objResponse.setSuccess(false);

				throw new XMLValidationException(e.getMessage());
			}

			try {
				
				//FDE044 - MC - validate against system acess groups
				XMLUtils xmlUtils = new XMLUtils();
				String WGCode = xmlUtils.evaluateXPathToString(doc, "//" + Utils.WORKORDER_WORKGROUPCODE_ELEMENT);

				this.validateDocument(workOrderNo, objResponse, errorMessage, doc, xmlUtils, WGCode);

				if(!this.hasIWSAccessToWorkOrdersInGroup(request, WGCode, applicationIdentifier)) {
					throw new NoAccessException("Access to work orders is not Authorized");
				}
				//end FDE044

				this.workOrderController.updateWorkOrder(applicationIdentifier, getSystemUserFromUserPrincipal().getId(), doc, districtCode, workOrderNo, workOrderXML);

			} catch (NullPointerException e) {

				// Handle errors that may occur updating the database if
				// required information
				// is not present in the new work order file.

				log.error("Error updating the database, required information was null: "
						+ e.getMessage());

				errorMessage.setErrorCode(Utils.DB_EXCEPTION);
				errorMessage
				.setErrorDescription("Error updating the database, required information was null: "
						+ e.getMessage());
				objResponse.setSuccess(false);

				// Re-throw to halt code execution and force the XML response to
				// be returned
				// to the client.
				throw e;

			} catch (ResourceTypeNotFoundException e) {

				// Thrown by fileResourceController.getFileTypeDir
				log.error("Unable to resolve directory to write work order file to: "
						+ e.getMessage());

				errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
				errorMessage
				.setErrorDescription("Unable to resolve directory to write work order file to: "
						+ e.getMessage());
				objResponse.setSuccess(false);

			} catch (FRInstanceException e) {

				throw e;
			}

		} catch (BadRequestException e) {

			// In case of BadRequestException failure.. rethrow for handler to
			// set.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch(NoAccessException e) {
			
			log.error(e.getMessage());
			
			errorMessage.setErrorCode(Utils.AUTHORIZATION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (XmlMappingException e) {

			log.error("XmlMappingException in /updatework " + e.getMessage());

			// if we are unable to parse the XML it is invalid and a 400
			// BadRequest needs to be returned.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (IOException e) {

			log.error("IOExcepton in /updatework " + e.getMessage());

			errorMessage.setErrorCode(Utils.IO_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch (NullPointerException e) {

			// Exception was rethrown elsewhere to allow an XML response to be
			// returned to the client.

		} catch (HibernateJdbcException e) {

			log.error("HibernateJdbcException in /updatework " + e.getMessage());

			errorMessage.setErrorCode(Utils.DB_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch (XMLValidationException e) {

			// exception thrown previously in this method where error
			// information will already have been set.

		} catch (WorkOrderNotFoundException e) {

			log.error("WorkOrderNotFoundException in /updatework "
					+ e.getMessage());

			// Handle general errors when parsing/validating schema
			errorMessage.setErrorCode(Utils.WORKORDER_NOT_FOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch (WorkOrderUpdateException e) {

			log.error("WorkOrderUpdateException in /updatework "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.WORKORDER_UPDATE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("Exception in /updatework: " + e.getMessage());

			if (errorMessage.getErrorCode() == null) {

				errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
				errorMessage.setErrorDescription(e.getMessage());
				objResponse.setSuccess(false);
			}

		}
		finally {

			objResponse.setSuccess(errorMessage.getErrorCode() == null);

		}

		if (log.isDebugEnabled())
			log.debug("<<< updateWorkOrder");

		return ResponseEntity.ok(objResponse);
	}

	void validateDocument(String workOrderNo, CallResponse objResponse, ErrorMessage errorMessage, Document doc, XMLUtils xmlUtils, String WGCode) throws XMLValidationException {
		if(WGCode == null || WGCode.length() == 0) {
			
			errorMessage.setErrorCode(Utils.WORKORDER_VALIDATION_EXCEPTION);
			errorMessage.setErrorDescription("Work group code null or missing");
			objResponse.setSuccess(false);
			
			throw new XMLValidationException();
		}

		String woNo = xmlUtils.evaluateXPathToString(doc, "//" + Utils.WORKORDER_WORKORDERNO_ELEMENT);

		if(woNo == null || woNo.length() == 0 || !woNo.equals(workOrderNo)) {
			
			errorMessage.setErrorCode(Utils.WORKORDER_VALIDATION_EXCEPTION);
			errorMessage.setErrorDescription("Work Order Number in xml body doesnt match Work Order Number in request");
			objResponse.setSuccess(false);
			
			throw new XMLValidationException();
		}
	}

	boolean isWorkStatusClosed(String applicationIdentifier, com.amtsybex.fieldreach.backend.model.WorkIssued objWO) {
		return workOrderController.getWorkStatuses().statusIsDesignated(applicationIdentifier, objWO.getWorkStatus(), WORKSTATUSDESIGNATION.CLOSED)
				|| workOrderController.getWorkStatuses().statusIsDesignated(applicationIdentifier, objWO.getWorkStatus(), WORKSTATUSDESIGNATION.RELEASED)
				|| workOrderController.getWorkStatuses().statusIsDesignated(applicationIdentifier, objWO.getWorkStatus(), WORKSTATUSDESIGNATION.PRECLOSEAPPROVAL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestWorkOrderController
	 * #cancelWorkOrder(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@Override
	@Deprecated
	@PutMapping(value = "/cancel/{workOrderNo}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CALL_1_JSON, APPLICATION_VND_FIELDSMART_CALL_1_XML},
			consumes = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_JSON, APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_XML})
	@ApiOperation(value = "Cancel a FieldSmart Work Order",
			notes = "This web service is responsible for cancelling the work order in FieldSmart. " +
					"The service will remove the target work order from the mobile devices of any field workers to which it has allocated. " +
					"Cancelling of work orders is used to whenever the work order is no longer required to be completed.")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include WorkOrderNotFoundException, WOCancelException, WorkOrderSchemaNotFoundException, WorkOrderValidationException, DatabaseException, EXCEPTION(General Exception)"),
	})
	public ResponseEntity<CallResponse> cancelWorkOrder(HttpServletRequest request,
														@PathVariable("workOrderNo") String workOrderNo, 
														@RequestParam(name = "districtCode", required = false, defaultValue = Utils.WORKORDER_DEFAULT_DISTRICT) String districtCode,
														@RequestBody WorkOrderStatusRequest workOrderStatusRequest)  {

		if (log.isDebugEnabled())
			log.debug(">>> cancelWorkOrder workOrderNo=" + Common.CRLFEscapeString(workOrderNo));

		CallResponse objResponse = new CallResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);

		// Debug headers
		Utils.debugHeaders(log, request);

		// get application identifier
		String applicationIdentifier = Utils
				.extractApplicationIdentifier(request);

		try {
			
			String additionalText = null;
			
			if(workOrderStatusRequest != null) {
				additionalText = workOrderStatusRequest.getWorkStatusAdditionalText();
			}

			
			
			com.amtsybex.fieldreach.backend.model.WorkIssued objWO = null;
			objWO = workOrderController.getWorkIssuedRecord(applicationIdentifier, workOrderNo, districtCode);

			// If the objWO object is null then a work order was not found in
			// the WorkIssued table
			if (objWO == null) {

				// Throw exception to prevent further processing
				throw new WorkOrderNotFoundException(
						"WorkOrder not found in the database."
								+ "workorderno = " + workOrderNo + " districtCode = "
								+ districtCode);

			}
			
			//FDE044 - MC - validate against system access groups
			if(!this.hasIWSAccessToWorkOrdersInGroup(request, objWO.getWorkgroupCode(), applicationIdentifier)) {
				throw new NoAccessException("Access to work orders is not Authorized");
			}
			//end FDE044
			

			this.workOrderController.cancelWorkOrder(applicationIdentifier, objWO, additionalText);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch(NoAccessException e) {
			
			log.error(e.getMessage());
			
			errorMessage.setErrorCode(Utils.AUTHORIZATION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (WorkOrderNotFoundException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.WORKORDER_NOT_FOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (WorkOrderCancelException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.WORKORDER_CANCEL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("Exception in /cancelwork: " + e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		}
		finally {

			objResponse.setSuccess(errorMessage.getErrorCode() == null);

		}

		if (log.isDebugEnabled())
			log.debug("<<< cancelWorkOrder");

		return ResponseEntity.ok(objResponse);
	}

	// FDE023 TM 08/11/2013

	// MLM - FDP1073
	@Override
	@Deprecated
	@PutMapping(value = "/recall/{workOrderNo}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CALL_1_JSON, APPLICATION_VND_FIELDSMART_CALL_1_XML},
			consumes = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_JSON, APPLICATION_VND_FIELDSMART_WORKORDERSTATUS_1_XML})
	@ApiOperation(value = "Recalls a FieldSmart Work Order",
			notes = "This web service provides functionality for the recalling of work orders. " +
					"The service will remove the target work order from the mobile devices of any field workers to which it has allocated. " +
					"Recalling is assumed to be temporary; " +
					"it is expected that the work order will be updated and reassigned at some point in the future.")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include WorkOrderNotFoundException, WORecallException, DatabaseException, EXCEPTION(General Exception)"),
	})
	public ResponseEntity<CallResponse> recallWorkOrder(HttpServletRequest request,
														@PathVariable("workOrderNo") String workOrderNo,
														@RequestBody WorkOrderStatusRequest workStatusRequestMessage,
														@RequestParam(name = "districtCode", required = false, defaultValue = Utils.WORKORDER_DEFAULT_DISTRICT) String districtCode) {

		if (log.isDebugEnabled())
			log.debug(">>> recall workOrderNo=" + Common.CRLFEscapeString(workOrderNo));

		CallResponse objResponse = new CallResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		objResponse.setError(errorMessage);
		
		String applicationIdentifier = Utils.extractApplicationIdentifier(request);

		try {
			
			String additionalText = null;
			
			if(workStatusRequestMessage != null) {
				additionalText = workStatusRequestMessage.getWorkStatusAdditionalText();
				log.debug("Request body parsed successfully. additionalText = " + additionalText);
			}
			
			com.amtsybex.fieldreach.backend.model.WorkIssued objWO = null;
			objWO = workOrderController.getWorkIssuedRecord(applicationIdentifier, workOrderNo, districtCode);

			// If the objWO object is null then a work order was not found in
			// the WorkIssued table
			if (objWO == null) {

				// Throw exception to prevent further processing
				throw new WorkOrderNotFoundException(
						"WorkOrder not found in the database."
								+ "workorderno = " + workOrderNo + " districtCode = "
								+ districtCode);

			}
			
			//FDE044 - MC - validate against system access groups
			if(!this.hasIWSAccessToWorkOrdersInGroup(request, objWO.getWorkgroupCode(), applicationIdentifier)) {
				throw new NoAccessException("Access to work orders is not Authorized");
			}
			//end FDE044

			this.workOrderController.recallWorkOrder(applicationIdentifier, getSystemUserFromUserPrincipal().getId(), objWO.getWorkOrderNo(), objWO.getDistrictCode(), additionalText);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);

		} catch(NoAccessException e) {
			
			log.error(e.getMessage());
			
			errorMessage.setErrorCode(Utils.AUTHORIZATION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (WorkOrderNotFoundException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.WORKORDER_NOT_FOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (WorkOrderRecallException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.WORKORDER_RECALL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception e) {

			log.error("Exception in /cancelwork: " + e.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			objResponse.setSuccess(false);
		}
		finally {

			objResponse.setSuccess(errorMessage.getErrorCode() == null);

		}

		if (log.isDebugEnabled())
			log.debug("<<<recallWorkOrder");

		return ResponseEntity.ok(objResponse);
	}

	// MLM - FDP1073 - END

	// FDE029 20/01/2015 TM

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.rest.RestWorkOrderController
	 * #registerAttachment(javax.servlet.http.HttpServletRequest,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@PutMapping(value = "/{workorderno}/attachment",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CALL_1_JSON, APPLICATION_VND_FIELDSMART_CALL_1_XML},
			consumes = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_ATTACHMENT_1_JSON, APPLICATION_VND_FIELDSMART_ATTACHMENT_1_XML})
	@ApiOperation(value = "Register an Attachment to a Work Order", 
			notes = "This web service is used to register a document attachment to a work order that has previously been issued to FieldSmart. " +
					"The document attachment will then be made available for download onto the mobile device of any " +
					"workgroup/user to which it has been allocated.")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include WorkOrderNotFoundException, AttachmentRegistrationException, MaximumFileSizeExceededException, ChecksumMismatchException, IOException, FRInstanceException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - No request body is passed.")
	})
	public ResponseEntity<CallResponse> registerAttachment(HttpServletRequest request,
														   @PathVariable("workorderno") String workorderNo,
														   @RequestBody(required = true) RegisterAttachment registerAttachment,
														   @RequestParam(name = "districtCode", required = false, defaultValue = Utils.WORKORDER_DEFAULT_DISTRICT) String districtCode) throws BadRequestException {

		if (log.isDebugEnabled())
			log.debug(">>> registerAttachment");

		// Initialise response object.
		CallResponse callResponse = new CallResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		callResponse.setError(errorMessage);

		try {

			// Debug headers
			Utils.debugHeaders(log, request);

			if (log.isDebugEnabled())
				log.debug("Body :" + Common.CRLFEscapeString(registerAttachment.toString()));

			// Get application identifier from request headers
			String applicationIdentifier = Utils
					.extractApplicationIdentifier(request);


			// Verify required elements exists in the parsed XML input
			if (registerAttachment.getFileName() == null
					|| registerAttachment.getFileName().trim().equals("")) {

				throw new BadRequestException("Attachment file name not set");
			}

			if (registerAttachment.getData() == null
					|| registerAttachment.getData().trim().equals("")) {

				throw new BadRequestException("Attachment file content not set");
			}

			if (registerAttachment.getFileType() == null
					|| registerAttachment.getFileType().trim().equals("")) {

				throw new BadRequestException("Attachment file type not set");
			}

			String generatedChecksum = Common.generateMD5Checksum(
					registerAttachment.getData().getBytes());

			// Check that checksum supplied matches the generated one.
			if (generatedChecksum.equals(registerAttachment.getChecksum())) {

				throw new ChecksumMismatchException("Generated checksum does not match that supplied.");
			}

			// Check to see if the work order already exists in the WorkIssued
			// table.
			com.amtsybex.fieldreach.backend.model.WorkIssued objWO = null;
			objWO = workOrderController.getWorkIssuedRecord(
					applicationIdentifier, workorderNo, districtCode);

			// If the objWO object is null then a work order was not found in
			// the the WorkIssued table
			if (objWO == null) {

				throw new WorkOrderNotFoundException("WorkOrder not found "
						+ "in the database. workorderno = " + workorderNo
						+ " districtCode = " + districtCode);
			}
			
			//FDE044 - MC - validate against system access groups
			if(!this.hasIWSAccessToWorkOrdersInGroup(request, objWO.getWorkgroupCode(), applicationIdentifier)) {
				throw new NoAccessException("Access to work orders is not Authorized");
			}
			//end FDE044

			this.workOrderController.registerAttachment(applicationIdentifier,
					registerAttachment, objWO);

		} catch (BadRequestException e) {

			// Re-throw for handler to set.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch(NoAccessException e) {
			
			log.error(e.getMessage());
			
			errorMessage.setErrorCode(Utils.AUTHORIZATION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			
		} catch (WorkOrderNotFoundException e) {

			log.error("WorkOrderNotFoundException in /workorder/{workorderno}/attachment: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.WORKORDER_NOT_FOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			callResponse.setSuccess(false);

		} catch (AttachmentRegistrationException e) {

			log.error("AttachmentRegistrationException in /workorder/{workorderno}/attachment: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.ATTACHMENT_REGISTRATION_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			callResponse.setSuccess(false);

		} catch (IOException e) {

			log.error("IOException in /workorder/{workorderno}/attachment: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.IO_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			callResponse.setSuccess(false);

		} catch(ChecksumMismatchException e) {

			log.error("ChecksumMismatchException in /workorder/{workorderno}/attachment: "
					+ e.getMessage());

			errorMessage.setErrorCode(Utils.CHECKSUM_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
			callResponse.setSuccess(false);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (XmlMappingException e) {

			log.error("XmlMappingException in /workorder/{workorderno}/attachment: "
					+ e.getMessage());

			// If we are unable to parse the XML it is invalid and a 400
			// BadRequest needs to be returned.
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getLocalizedMessage(), e);

		} catch (Exception ex) {

			log.error("Exception in /workorder/{workorderno}/attachment: "
					+ ex.getMessage() + Utils.getStackTrace(ex));

			callResponse.getError().setErrorCode(Utils.GENERAL_EXCEPTION);
			callResponse.getError().setErrorDescription(ex.getMessage());
		}
		finally {

			callResponse.setSuccess(errorMessage.getErrorCode() == null);

		}

		if (log.isDebugEnabled())
			log.debug("<<< registerAttachment");

		return ResponseEntity.ok(callResponse);
	}

	// End FDE029

	/*-------------------------------------------
	- Private Methods
	--------------------------------------------*/

	// FDE026 TM 29/05/2014
	// Modify changed method access specifier to protected to allow access
	// from inheriting classes.

	/**
	 * Take xml string and attempt to extract a district code from this.
	 * 
	 * @param xmlMsg
	 *            XML string to extract district code from
	 * 
	 * @return Value of the extracted district code. null is returned if none
	 *         could be found.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private String extractDistrictFromXML(String xmlMsg)
			throws ParserConfigurationException, SAXException, IOException {

		if (log.isDebugEnabled())
			log.debug(">>> extractDistrictFromXML xmlMsg=XXX");

		XMLUtils xml = new XMLUtils();
		Document doc = workOrderController.parseAndValidateWorkOrder(xmlMsg);

		String dc = xml.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_DISTRICTCODE_ELEMENT);

		if (log.isDebugEnabled())
			log.debug("<<< extractDistrictFromXML");

		return dc;
	}

	/**
	 * Determine the district code from the request supplied.
	 * 
	 * @param request
	 *            Initial request.
	 * 
	 * @param workorderXml
	 *            Body passed with the initial request.
	 * 
	 * @return The value held in the districtCode parameter of the request
	 *         supplied. If not supplied look in the supplied xml for a district
	 *         code. If this is missing use NA
	 */
	private String determineDistrictCode(HttpServletRequest request,
			String workorderXml) {

		if (log.isDebugEnabled())
			log.debug(">>> determineDistrictCode request=XXX workorderXml=XXX");

		// Get district code if this has been passed in.
		String districtCode = Utils.getOptionalParam(request,
				Utils.DISTRICTCODE_PARAM);

		// If district code is not supplied extract it form the request body, if
		// it can't be found here then use the default value.
		if (districtCode == null || districtCode.equals("")) {

			try {

				districtCode = this.extractDistrictFromXML(workorderXml).trim();

			} catch (Exception e) {

				districtCode = Utils.WORKORDER_DEFAULT_DISTRICT;

				if (log.isDebugEnabled()) {

					log.debug("Unable to extract district code from XML."
							+ " Using Default value.");
				}
			}

			if (districtCode == null || districtCode.equals(""))
				districtCode = Utils.WORKORDER_DEFAULT_DISTRICT;
		}

		if (log.isDebugEnabled())
			log.debug("<<< determineDistrictCode");

		return districtCode;
	}
	
	/**
	 * FDE044 - MC
	 * @param request
	 * @param workGroupCode
	 * @param applicationIdentifier
	 * @return
	 * @throws FRInstanceException 
	 */
	private boolean hasIWSAccessToWorkOrdersInGroup(HttpServletRequest request, String workGroupCode, String applicationIdentifier) throws FRInstanceException {
		
		try {
			String autUserCode = getSystemUserFromUserPrincipal().getId();
			
			if(this.userService.hasUnlimitedAccessibleWorkgroups(applicationIdentifier, autUserCode)) {
				return true;
			}
			
			List<HPCWorkGroups> accessibleWorkgroups = this.userService.getAccessibleWorkgroups(applicationIdentifier, autUserCode);

			List<String> accessibleWorkGroupCodes = new ArrayList<String>();

			for (HPCWorkGroups wg : accessibleWorkgroups)
				accessibleWorkGroupCodes.add(wg.getWorkgroupCode());
			
			if(accessibleWorkGroupCodes.contains(workGroupCode)) {
				return true;
			}
			
		} catch (FRInstanceException e) {
			throw e;
		} catch (UserNotFoundException e) {
			return false;
		}

		return false;
		
	}


}
