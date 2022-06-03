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
 * Code Re-factoring
 */
package com.amtsybex.fieldreach.services.endpoint.common.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.instance.InstanceManager;
import com.amtsybex.fieldreach.backend.instance.Transaction;
import com.amtsybex.fieldreach.backend.model.WorkIssued;
import com.amtsybex.fieldreach.backend.model.WorkIssuedFileRefs;
import com.amtsybex.fieldreach.backend.model.WorkIssuedLog;
import com.amtsybex.fieldreach.backend.model.WorkStatusHistory;
import com.amtsybex.fieldreach.backend.model.pk.WorkIssuedFileRefsId;
import com.amtsybex.fieldreach.backend.model.pk.WorkIssuedId;
import com.amtsybex.fieldreach.backend.model.pk.WorkIssuedLogId;
import com.amtsybex.fieldreach.backend.model.pk.WorkStatusHistoryId;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.backend.service.WorkOrderService;
import com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus.WORKSTATUSDESIGNATION;
import com.amtsybex.fieldreach.services.exception.AttachmentRegistrationException;
import com.amtsybex.fieldreach.services.exception.WorkOrderCancelException;
import com.amtsybex.fieldreach.services.exception.WorkOrderCloseException;
import com.amtsybex.fieldreach.services.exception.WorkOrderDispatchException;
import com.amtsybex.fieldreach.services.exception.WorkOrderNotFoundException;
import com.amtsybex.fieldreach.services.exception.WorkOrderRecallException;
import com.amtsybex.fieldreach.services.messages.request.RegisterAttachment;
import com.amtsybex.fieldreach.services.resource.FileResourceController;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.services.utils.xml.XMLUtils;
import com.amtsybex.fieldreach.utils.impl.Common;

/**
 * Class to allow common operations around work orders to be performed. These
 * operations are not specific to any type of web service implementation such as
 * SOAP or REST.
 */
public class WorkOrderControllerImpl implements WorkOrderController {

	private static Logger log = LoggerFactory.getLogger(WorkOrderControllerImpl.class
			.getName());

	private FileResourceController fileResourceController;

	private WorkOrderService workOrderService;

	private String workOrderSchema;

	@Autowired
	private WorkStatus workStatuses;

	// FDP1073 - MLM
	// FDP1110 TM 01/07/02015
	//private String recallWorkStatus = "RECALLED";
	
	private boolean statusChanged;

	@Autowired
	private InstanceManager instanceManager;
	
	@Autowired
	private UserService userService;

	/*-------------------------------------------
	 - Constructor Methods
	 --------------------------------------------*/

	public WorkOrderControllerImpl() {

	}

	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * getWorkOrdersByUserCode(java.lang.String, java.lang.String)
	 */
	@Override
	public List<WorkIssued> getWorkOrdersByUserCode(String frInstance,
			String userCode, boolean includeReleased) throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getWorkOrdersByUserCode frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " userCode=" + Common.CRLFEscapeString(userCode));

			log.debug("<<< getWorkOrdersByUserCode");
		}

		return workOrderService.findWorkIssuedByUserCode(frInstance, userCode, this.workStatuses.getWorkIssuedIgnoreStatusList(frInstance, includeReleased));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * getWorkOrdersByWorkGroupCode(java.lang.String, java.lang.String)
	 */
	@Override
	public List<WorkIssued> getWorkOrdersByWorkGroupCode(String frInstance, String workgroupCode, boolean includeReleased) throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getWorkOrdersByWorkGroupCode frInstance="
					+ Common.CRLFEscapeString(frInstance) + " workgroupCode=" + Common.CRLFEscapeString(workgroupCode));

			log.debug("<<< getWorkOrdersByWorkGroupCode");
		}

		return workOrderService.findWorkIssuedByWorkgroupCode(frInstance, workgroupCode, this.workStatuses.getWorkIssuedIgnoreStatusList(frInstance, includeReleased));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * getWorkOrdersByWorkGroupCodeCat(java.lang.String, java.lang.String)
	 */
	@Override
	public List<WorkIssued> getWorkOrdersByWorkGroupCodeCat(String frInstance, String workgroupCode, boolean includeReleased) throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getWorkOrdersByWorkGroupCodeCat frInstance="
					+ Common.CRLFEscapeString(frInstance) + " workgroupCode=" + Common.CRLFEscapeString(workgroupCode));

			log.debug("<<< getWorkOrdersByWorkGroupCodeCat");
		}

		return workOrderService.findWorkIssuedByWorkgroupCodeCat(frInstance, workgroupCode, this.workStatuses.getWorkIssuedIgnoreStatusList(frInstance, includeReleased));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * getWorkOrderSourceContents(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String getWorkOrderSourceContents(String woNumber, String districtCode, String applicationIdentifier)
			throws ResourceTypeNotFoundException, ResourceNotFoundException, FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getWorkOrderSourceContents  woNumber=" + Common.CRLFEscapeString(woNumber) + " applicationIdentifier=" + Common.CRLFEscapeString(applicationIdentifier));
		}

		// Initialise variables...
		String retval = null;
		WorkIssued objWorkOrder = null;
		String fileName = null;

		objWorkOrder = workOrderService.findWorkIssued(applicationIdentifier, woNumber, districtCode);

		// If no work order record is found in the WorkIssued table throw an
		// exception.
		if (objWorkOrder == null) {

			String msg = "Entry not found in WorkIssued. workorderno=" + woNumber + " districtCode=" + districtCode;

			throw new ResourceNotFoundException(msg);
		}

		// Create the file name of the work order to be retrieved.
		fileName = objWorkOrder.getSourceFileName();

		if (fileName == null || fileName.trim().equals("")) {

			fileName = Utils.WORKORDER_FILE_PREFIX + woNumber + Utils.WORKORDER_FILE_EXTENSION;
		}

		if (log.isDebugEnabled())
			log.debug("Filename =" + Common.CRLFEscapeString(fileName));

		// Determine the sub directory the work order file will be stored in.
		// This will be either user code or workgroup code.
		String subDir = objWorkOrder.getUserCode();
		if (subDir == null || subDir.length() == 0) {
			subDir = objWorkOrder.getWorkgroupCode();
		}

		// Encode contents of the work order file to base 64.
		retval = fileResourceController.getBase64Content(fileName, Utils.WORKORDER_FILE_TYPE, subDir, applicationIdentifier);

		if (log.isDebugEnabled())
			log.debug("<<< getWorkOrderSourceContents");

		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * logWorkOrderUpload(java.lang.String, org.w3c.dom.Document,
	 * java.lang.String)
	 */
	@Override
	public void logWorkOrderUpload(String frInstance, String modifyUser, Document doc, String districtCode, boolean releasing) throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> logWorkOrderUpload frInstance=" + Common.CRLFEscapeString(frInstance) + " doc=XXX districtCode=" + Common.CRLFEscapeString(districtCode));
		}

		Transaction trans = this.instanceManager.getTransaction(frInstance);

		try {

			// Get current date and time. Use this to set the status date and
			// status time so that these values are the same in the workissued
			// and workstatushistory tables.

			Integer currentDate = Common.generateFieldreachDBDate();
			Integer currentTime = Common.generateFieldreachDBTime();

			// Map the values in the work order XML to a WorkIssued object
			WorkIssued objWorkIssued = createWorkIssuedRecordFromXML(frInstance, doc,
					districtCode);

			objWorkIssued.setIssuedDate(currentDate);
			objWorkIssued.setIssuedTime(currentTime);
			
			//FDE051 - MC 
			if(releasing){
				
				objWorkIssued.setWorkStatus(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.RELEASED));
			}

			// FDE026 TM 15/09/2014
			// Multi instance backend support
			workOrderService.saveWorkIssued(frInstance, objWorkIssued);

			// If work issued is updated successfully then update
			// WorkStatusHistory.
			WorkStatusHistory objWorkStatusHistory = mapWorkOrderToWorkStatusHistory(
					doc, districtCode, objWorkIssued.getWorkStatus());

			objWorkStatusHistory.setWorkStatusDate(currentDate);
			objWorkStatusHistory.setWorkStatusTime(currentTime);

			workOrderService.saveWorkStatusHistory(frInstance,
					objWorkStatusHistory);
			
			
			WorkIssuedLogId workLogItemId = new WorkIssuedLogId(objWorkIssued.getId().getWorkOrderNo(), objWorkIssued.getId().getDistrictCode(), 
					modifyUser, 
					Common.generateFieldreachDBDate(), 
					Utils.correctFieldreachDBTime(Common.generateFieldreachDBTime()));
			
			WorkIssuedLog workLogItem = new WorkIssuedLog(workLogItemId, objWorkIssued.getWorkOrderDesc(), objWorkIssued.getIssuedTime(), objWorkIssued.getEquipNo(), objWorkIssued.getEquipDesc(),
					objWorkIssued.getAltEquipRef(), objWorkIssued.getPlanStartDate(), objWorkIssued.getReqFinishDate(), objWorkIssued.getUserCode(), objWorkIssued.getWoType(),
					objWorkIssued.getAdditionalText(), objWorkIssued.getIssuedDate(), objWorkIssued.getWorkgroupCode(), objWorkIssued.getLatitude(), objWorkIssued.getLongitude(), 
					objWorkIssued.getPlanStartTime(), objWorkIssued.getReqFinishTime());
			
			this.workOrderService.saveWorkIssuedLog(frInstance, workLogItem);

			this.instanceManager.commitTransaction(trans);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (Throwable t) {

			log.error("UNEXPECTED EXCEPTION: " + t.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw new RuntimeException(t);
		}

		if (log.isDebugEnabled())
			log.debug("<<< logWorkOrderUpload");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * updateWorkOrder(java.lang.String, org.w3c.dom.Document,
	 * java.lang.String,java.lang.String,java.lang.String)
	 */
	@Override
	public void updateWorkOrder(String frInstance, String modifyUser, Document doc, String districtCode, String workOrderNo, String workOrderXML) throws FRInstanceException, ResourceTypeNotFoundException {
		
		if (log.isDebugEnabled()) {

			log.debug(">>> updateWorkOrder frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " doc=XXX districtCode=" + Common.CRLFEscapeString(districtCode) + " workorderNo=" + Common.CRLFEscapeString(workOrderNo) );
		}

		Transaction trans = this.instanceManager.getTransaction(frInstance);
		
		try {
			com.amtsybex.fieldreach.backend.model.WorkIssued oldWo = this.getWorkIssuedRecord(frInstance, workOrderNo, districtCode);
			
			// Create the temporary work order file name.
			String woTempFileName = Utils.WORKORDER_FILE_PREFIX
					+ workOrderNo + Utils.WORKORDER_TEMP_FILE_EXTENSION;

			// Determine the directory to write the temporary work order
			// file to.
			String tempWoDir = this.resolveWoUploadDirFromXML(frInstance, doc);

			// Write the temporary work order file to the file system.
			// If the file gets written to the file system then update the
			// database accordingly.

			// Bug 2261 TM 04/09/2013

			byte[] woBytes;

			try {

				woBytes = workOrderXML.getBytes(Common.UTF8_ENCODING);

			} catch (UnsupportedEncodingException e) {

				woBytes = workOrderXML.getBytes();
			}

			// Throw an IO exception if the temporary work order file can
			// not be written to the file system.
			if (!Common.writeBytesToFile(woBytes, woTempFileName, tempWoDir)) {

				throw new IOException("Unable to write work order file: "
						+ tempWoDir + File.separator + woTempFileName);
			}

			// End Bug 2261

			// Before updating the database get the old work order file name
			// and location
			String oldWoFileName = Utils.WORKORDER_FILE_PREFIX
					+ workOrderNo + Utils.WORKORDER_FILE_EXTENSION;

			String oldWODir = this.resolveWoUploadDirFromDB(frInstance, oldWo);

			// FDE026 TM 15/09/2014
			// Multi instance backend support
			try {
				
				WorkIssued objWorkIssued = updateWorkIssuedRecordFromXML(frInstance, doc, districtCode);
				
				this.logWorkOrderUpdate(frInstance, modifyUser, objWorkIssued);

			} catch (FRInstanceException e) {
				
				// Delete the written file if database can't be updated.
				File woTempFile = new File(tempWoDir + "/" + woTempFileName);

				if (woTempFile.exists())
					woTempFile.delete();

				throw e;
			}
			// End FDE026

			// Delete the old work order file and rename the temporary work
			// order file
			File oldWoFile = new File(oldWODir + "/" + oldWoFileName);

			if (oldWoFile.exists())
				oldWoFile.delete();

			// Check to see if holding directory is now empty and delete it
			// if it is
			File oldWoDir = new File(oldWODir);

			if (oldWoDir.isDirectory()) {

				if (oldWoDir.list().length == 0)
					oldWoDir.delete();
			}

			// now rename the temporary file
			File woTempFile = new File(tempWoDir + "/" + woTempFileName);
			boolean result = false;

			if (woTempFile.exists()) {

				result = woTempFile.renameTo(new File(tempWoDir + "/"
						+ oldWoFileName));
			}

			if (!result)
				woTempFile.delete();
			
			this.instanceManager.commitTransaction(trans);
			
		}catch (ResourceTypeNotFoundException e) {

			// Thrown by fileResourceController.getFileTypeDir
			log.error("Unable to resolve directory to write work order file to: "
					+ e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (Throwable t) {

			log.error("UNEXPECTED EXCEPTION: " + t.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw new RuntimeException(t);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateWorkOrder");
		


	}
	
	
	@Override
	public void modifyWorkOrder(String frInstance, String modifyUser, WorkIssued work) throws FRInstanceException, ResourceTypeNotFoundException {
		
		if (log.isDebugEnabled()) {

			log.debug(">>> updateWorkOrder frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " doc=XXX districtCode=" + Common.CRLFEscapeString(work.getId().getDistrictCode()) + " workorderNo=" + Common.CRLFEscapeString(work.getId().getWorkOrderNo()));
		}

		Transaction trans = this.instanceManager.getTransaction(frInstance);
		
		try {
			com.amtsybex.fieldreach.backend.model.WorkIssued updateWO = this.getWorkIssuedRecord(frInstance, work.getId().getWorkOrderNo(), work.getId().getDistrictCode());
			
			// Create the temporary work order file name.
			String woTempFileName = Utils.WORKORDER_FILE_PREFIX
					+ work.getId().getWorkOrderNo() + Utils.WORKORDER_TEMP_FILE_EXTENSION;

			// Determine the directory to write the temporary work order
			// file to.
			String tempWoDir = this.resolveWoUploadDirFromDB(frInstance, work);

			// Before updating the database get the old work order file name
			// and location
			String oldWoFileName = Utils.WORKORDER_FILE_PREFIX
					+ work.getId().getWorkOrderNo() + Utils.WORKORDER_FILE_EXTENSION;

			String oldWODir = this.resolveWoUploadDirFromDB(frInstance, updateWO);
			
			// Write the temporary work order file to the file system.
			// If the file gets written to the file system then update the
			// database accordingly.

			// Bug 2261 TM 04/09/2013
			
			String workOrderXML = this.getXMLAndModifyWorkOrderFromWorkIssued(frInstance, work, updateWO);

			byte[] woBytes;

			try {

				woBytes = workOrderXML.getBytes(Common.UTF8_ENCODING);

			} catch (UnsupportedEncodingException e) {

				woBytes = workOrderXML.getBytes();
			}

			// Throw an IO exception if the temporary work order file can
			// not be written to the file system.
			if (!Common.writeBytesToFile(woBytes, woTempFileName, tempWoDir)) {

				throw new IOException("Unable to write work order file: "
						+ tempWoDir + File.separator + woTempFileName);
			}

			// End Bug 2261

			// FDE026 TM 15/09/2014
			// Multi instance backend support
			try {
				
				this.logWorkOrderUpdate(frInstance, modifyUser, updateWO);

			} catch (FRInstanceException e) {
				
				// Delete the written file if database can't be updated.
				File woTempFile = new File(tempWoDir + "/" + woTempFileName);

				if (woTempFile.exists())
					woTempFile.delete();

				throw e;
			}
			// End FDE026

			// Delete the old work order file and rename the temporary work
			// order file
			File oldWoFile = new File(oldWODir + "/" + oldWoFileName);

			if (oldWoFile.exists())
				oldWoFile.delete();

			// Check to see if holding directory is now empty and delete it
			// if it is
			File oldWoDir = new File(oldWODir);

			if (oldWoDir.isDirectory()) {

				if (oldWoDir.list().length == 0)
					oldWoDir.delete();
			}

			// now rename the temporary file
			File woTempFile = new File(tempWoDir + "/" + woTempFileName);
			boolean result = false;

			if (woTempFile.exists()) {

				result = woTempFile.renameTo(new File(tempWoDir + "/"
						+ oldWoFileName));
			}

			if (!result)
				woTempFile.delete();
			
			this.instanceManager.commitTransaction(trans);
			
		}catch (ResourceTypeNotFoundException e) {

			// Thrown by fileResourceController.getFileTypeDir
			log.error("Unable to resolve directory to write work order file to: "
					+ e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (Throwable t) {

			log.error("UNEXPECTED EXCEPTION: " + t.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw new RuntimeException(t);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateWorkOrder");
		


	}
	
	/*
	@Override
	public void updateWorkOrder(String frInstance, WorkIssued workIssued, Document doc, String workOrderXML) throws FRInstanceException, ResourceTypeNotFoundException {
		
		if (log.isDebugEnabled()) {

			log.debug(">>> updateWorkOrder frInstance=" + frInstance
					+ " doc=XXX districtCode=" + workIssued.getId().getDistrictCode() + " workorderNo=" + workIssued.getId().getWorkOrderNo() );
		}

		Transaction trans = this.instanceManager.getTransaction(frInstance);
		
		try {
			com.amtsybex.fieldreach.backend.model.WorkIssued oldWo = this.getWorkIssuedRecord(frInstance, workIssued.getId().getWorkOrderNo(), workIssued.getId().getDistrictCode());
			
			// Create the temporary work order file name.
			String woTempFileName = Utils.WORKORDER_FILE_PREFIX
					+ workIssued.getId().getWorkOrderNo() + Utils.WORKORDER_TEMP_FILE_EXTENSION;

			// Determine the directory to write the temporary work order
			// file to.
			String tempWoDir = this.resolveWoUploadDirFromDB(frInstance, workIssued);

			// Write the temporary work order file to the file system.
			// If the file gets written to the file system then update the
			// database accordingly.

			// Bug 2261 TM 04/09/2013

			byte[] woBytes;

			try {

				woBytes = workOrderXML.getBytes(Common.UTF8_ENCODING);

			} catch (UnsupportedEncodingException e) {

				woBytes = workOrderXML.getBytes();
			}

			// Throw an IO exception if the temporary work order file can
			// not be written to the file system.
			if (!Common.writeBytesToFile(woBytes, woTempFileName, tempWoDir)) {

				throw new IOException("Unable to write work order file: "
						+ tempWoDir + File.separator + woTempFileName);
			}

			// End Bug 2261

			// Before updating the database get the old work order file name
			// and location
			String oldWoFileName = Utils.WORKORDER_FILE_PREFIX
					+ workIssued.getId().getWorkOrderNo() + Utils.WORKORDER_FILE_EXTENSION;

			String oldWODir = this.resolveWoUploadDirFromDB(frInstance, oldWo);

			// FDE026 TM 15/09/2014
			// Multi instance backend support
			try {
				
				WorkIssued objWorkIssued = updateWorkIssuedRecordFromXML(frInstance, doc, workIssued.getDistrictCode());
				
				oldWo.setUser(workIssued.getUser());
				oldWo.setWorkGroup(workIssued.getWorkGroup());
				
				this.logWorkOrderUpdate(frInstance, objWorkIssued);

			} catch (FRInstanceException e) {
				
				// Delete the written file if database can't be updated.
				File woTempFile = new File(tempWoDir + "/" + woTempFileName);

				if (woTempFile.exists())
					woTempFile.delete();

				throw e;
			}
			// End FDE026

			// Delete the old work order file and rename the temporary work
			// order file
			File oldWoFile = new File(oldWODir + "/" + oldWoFileName);

			if (oldWoFile.exists())
				oldWoFile.delete();

			// Check to see if holding directory is now empty and delete it
			// if it is
			File oldWoDir = new File(oldWODir);

			if (oldWoDir.isDirectory()) {

				if (oldWoDir.list().length == 0)
					oldWoDir.delete();
			}

			// now rename the temporary file
			File woTempFile = new File(tempWoDir + "/" + woTempFileName);
			boolean result = false;

			if (woTempFile.exists()) {

				result = woTempFile.renameTo(new File(tempWoDir + "/"
						+ oldWoFileName));
			}

			if (!result)
				woTempFile.delete();
			
			this.instanceManager.commitTransaction(trans);
			
		}catch (ResourceTypeNotFoundException e) {

			// Thrown by fileResourceController.getFileTypeDir
			log.error("Unable to resolve directory to write work order file to: "
					+ e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (Throwable t) {

			log.error("UNEXPECTED EXCEPTION: " + t.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw new RuntimeException(t);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateWorkOrder");
		


	}*/


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * logWorkOrderUpdate(java.lang.String, org.w3c.dom.Document,
	 * java.lang.String)
	 */
	@Override
	public void logWorkOrderUpdate(String frInstance, String modifyUser, WorkIssued workIssued) throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> logWorkOrderUpdate frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " doc=XXX districtCode=" + Common.CRLFEscapeString(workIssued.getDistrictCode()));
		}

		Transaction trans = this.instanceManager.getTransaction(frInstance);

		try {

			// Get current date and time. Use this to set the status date and
			// status time so that these values are the same in the workissued
			// and workstatushistory tables.
			Integer currentDate = Common.generateFieldreachDBDate();
			Integer currentTime = Common.generateFieldreachDBTime();

			// Find record for the work order in workissued table and Map the
			// values in the work order XML to the WorkIssued object found.
			/*WorkIssued objWorkIssued = updateWorkIssuedRecordFromXML(
					frInstance, doc, districtCode);*/

			if (workIssued != null) {

				//TODO - FDE051 - MC this seems to be a bit of a hack. maybe need to remove the workgroup code and user code from work issued
				//and just work from the workgroup and user objects?
				if(workIssued.getWorkgroupCode() == null) {
					workIssued.setWorkGroup(null);
				}else {
					workIssued.setWorkGroup(this.userService.findHPCWorkGroup(frInstance, workIssued.getWorkgroupCode()));
				}
				
				if(workIssued.getUserCode() == null) {
					workIssued.setUser(null);
				}else {
					workIssued.setUser(this.userService.findHPCUser(frInstance, workIssued.getUserCode(), workIssued.getWorkgroupCode()));
				} //END TODO
				
				// FDE026 TM 15/09/2014
				// Multi instance backend support
				workOrderService.saveWorkIssued(frInstance, workIssued);

				// If work issued is updated successfully and the status was changed 
				// then update WorkStatusHistory.
				if(this.statusChanged) {
					
					WorkStatusHistory objWorkStatusHistory = mapWorkOrderToWorkStatusHistory(workIssued);
	
					objWorkStatusHistory.setWorkStatusDate(currentDate);
					objWorkStatusHistory.setWorkStatusTime(currentTime);
	
					workOrderService.saveWorkStatusHistory(frInstance,
							objWorkStatusHistory);
				}
				
				WorkIssuedLogId workLogItemId = new WorkIssuedLogId(workIssued.getId().getWorkOrderNo(), workIssued.getId().getDistrictCode(), 
						modifyUser, 
						Common.generateFieldreachDBDate(), 
						Utils.correctFieldreachDBTime(Common.generateFieldreachDBTime()));
				
				WorkIssuedLog workLogItem = new WorkIssuedLog(workLogItemId, workIssued.getWorkOrderDesc(), workIssued.getIssuedTime(), workIssued.getEquipNo(), workIssued.getEquipDesc(),
						workIssued.getAltEquipRef(), workIssued.getPlanStartDate(), workIssued.getReqFinishDate(), workIssued.getUserCode(), workIssued.getWoType(),
						workIssued.getAdditionalText(), workIssued.getIssuedDate(), workIssued.getWorkgroupCode(), workIssued.getLatitude(), workIssued.getLongitude(),
						workIssued.getPlanStartTime(), workIssued.getReqFinishTime());
				
				this.workOrderService.saveWorkIssuedLog(frInstance, workLogItem);
			}

			this.instanceManager.commitTransaction(trans);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (Throwable t) {

			log.error("UNEXPECTED EXCEPTION: " + t.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw new RuntimeException(t);
		}

		if (log.isDebugEnabled())
			log.debug("<<< logWorkOrderUpdate");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * getWorkOrderSchema()
	 */
	@Override
	public String getWorkOrderSchema() {

		if (log.isDebugEnabled()) {

			log.debug(">>> getWorkOrderSchema");

			log.debug("<<< getWorkOrderSchema");
		}

		return workOrderSchema;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * getWorkIssuedRecord(java.lang.String, java.lang.String)
	 */
	@Override
	public WorkIssued getWorkIssuedRecord(String frInstance, String workOrderNo)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getWorkIssuedRecord frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " workOrderNo=" + Common.CRLFEscapeString(workOrderNo));

			log.debug("<<< getWorkIssuedRecord");
		}

		return workOrderService.findWorkIssuedByWorkOrderNo(frInstance,
				workOrderNo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * getWorkIssuedRecord(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public WorkIssued getWorkIssuedRecord(String frInstance,
			String workOrderNo, String districtCode) throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getWorkIssuedRecord frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " workOrderNo=" + Common.CRLFEscapeString(workOrderNo) + " districtCode="
					+ Common.CRLFEscapeString(districtCode));

			log.debug("<<< getWorkIssuedRecord");
		}

		return workOrderService.findWorkIssued(frInstance, workOrderNo,
				districtCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * saveWorkIssued(java.lang.String,
	 * com.amtsybex.fieldreach.backend.model.WorkIssued)
	 */
	@Override
	public void saveWorkIssued(String frInstance, WorkIssued entity)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getWorkIssuedRecord frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " entity=xxx");
		}

		// FDE026 TM 15/09/2014
		// Multi instance backend support

		workOrderService.saveWorkIssued(frInstance, entity);

		// End FDE026

		if (log.isDebugEnabled())
			log.debug("<<< getWorkIssuedRecord");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * saveWorkStatusHistory(java.lang.String,
	 * com.amtsybex.fieldreach.backend.model.WorkStatusHistory)
	 */
	@Override
	public void saveWorkStatusHistory(String frInstance,
			WorkStatusHistory entity) throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> saveWorkStatusHistory frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " entity=xxx");
		}

		// FDE026 TM 15/09/2014
		// Multi instance backend support
		workOrderService.saveWorkStatusHistory(frInstance, entity);
		// End FDE026

		if (log.isDebugEnabled())
			log.debug("<<< saveWorkStatusHistory");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * cancelWorkOrder(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void cancelWorkOrder(String frInstance, WorkIssued objWO, String additionalText) throws FRInstanceException,
			WorkOrderNotFoundException, WorkOrderCancelException {

		if (log.isDebugEnabled()) {

			log.debug(">>> cancelWorkOrder=" + Common.CRLFEscapeString(frInstance)
					+ " woNo=" + Common.CRLFEscapeString(objWO.getWorkOrderNo()) + " districtCode=" + Common.CRLFEscapeString(objWO.getDistrictCode()));
		}

		Transaction trans = this.instanceManager.getTransaction(frInstance);

		try {

			// Check the status of the work order is valid
			if (!this.checkStatusIsValidForCancel(frInstance, objWO.getWorkStatus())) {

				// Throw exception to prevent further processing
				throw new WorkOrderCancelException(
						"Unable to cancel workorder " + objWO.getWorkOrderNo() + " with status: "
								+ objWO.getWorkStatus());
			}

			// Update Fieldreach Database
			Integer currentDate = Common.generateFieldreachDBDate();
			Integer currentTime = Common.generateFieldreachDBTime();

			// Update WorkIssued Table
			objWO.setWorkStatus(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CANCELLED));
			objWO.setWorkStatusDate(currentDate);
			objWO.setWorkStatusTime(currentTime);

			workOrderService.saveWorkIssued(frInstance, objWO);

			// Update WorkStatusHistory table.
			WorkStatusHistory objWorkStatusHistory = new WorkStatusHistory();
			WorkStatusHistoryId objId = new WorkStatusHistoryId();

			objId.setWorkOrderNo(objWO.getWorkOrderNo());
			objId.setWorkStatus(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CANCELLED));
			objId.setWorkStatusDate(currentDate);
			objId.setWorkStatusTime(currentTime);
			objId.setDistrictCode(objWO.getDistrictCode());

			objWorkStatusHistory.setid(objId);
			objWorkStatusHistory.setUserCode(Utils.SYSTEM_USERCODE);
			
			//FDE051 - MC
			objWorkStatusHistory.setAdditionalText(additionalText);

			workOrderService.saveWorkStatusHistory(frInstance,
					objWorkStatusHistory);

			// Update WorkIssuedFileRefs table
			workOrderService.deleteAttachments(frInstance, objWO.getWorkOrderNo(), objWO.getDistrictCode());

			// Now remove files from the file system.
			try {

				this.deleteWorkOrderFromFileSystem(frInstance, objWO);

				this.deleteAttachmentsFromFileSystem(frInstance, objWO);

			} catch (ResourceTypeNotFoundException e) {

				throw new WorkOrderNotFoundException(e.getMessage());
			}

			this.instanceManager.commitTransaction(trans);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (WorkOrderNotFoundException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (WorkOrderCancelException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (Throwable t) {

			log.error(t.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw new RuntimeException(t);
		}

		if (log.isDebugEnabled())
			log.debug("<<< cancelWorkOrder");
	}

	
	/*
	 * FDE051 - MC 
	 * (non-Javadoc)
	 * @see com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#dispatchWorkOrder(java.lang.String, com.amtsybex.fieldreach.backend.model.WorkIssued)
	 */
	@Override
	public void dispatchWorkOrder(String frInstance, String workOrderNo, String districtCode) throws FRInstanceException,
	WorkOrderNotFoundException, WorkOrderDispatchException {

		if (log.isDebugEnabled()) {

			log.debug(">>> dispatchWorkOrder=" + Common.CRLFEscapeString(frInstance)
					+ " woNo=" + Common.CRLFEscapeString(workOrderNo) + " districtCode=" + Common.CRLFEscapeString(districtCode));
		}

		Transaction trans = this.instanceManager.getTransaction(frInstance);

		try {
			
			WorkIssued objWO = this.workOrderService.findWorkIssued(frInstance, workOrderNo, districtCode);

			//MLM FDP1073 PVC#1 - Only check for cancel or close
			if(objWO.getWorkStatus().equals(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CLOSED))
						||objWO.getWorkStatus().equals(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CANCELLED))) {

				// Throw exception to prevent further processing
				throw new WorkOrderDispatchException(
						"Unable to dispatch workorder " + objWO.getWorkOrderNo() + " with status: "
								+ objWO.getWorkStatus());
			}


			// Update Fieldreach Database
			Integer currentDate = Common.generateFieldreachDBDate();
			Integer currentTime = Common.generateFieldreachDBTime();

			// Update WorkIssued Table
			if(objWO.getWorkStatus().contentEquals(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.RELEASED))) {
				objWO.setWorkStatus(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.ISSUED));
			}else {
				objWO.setWorkStatus(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.REISSUED));
			}
			
			objWO.setWorkStatusDate(currentDate);
			objWO.setWorkStatusTime(currentTime);

			workOrderService.saveWorkIssued(frInstance, objWO);

			WorkStatusHistory objWorkStatusHistory = new WorkStatusHistory();
			WorkStatusHistoryId objId = new WorkStatusHistoryId();

			objId.setWorkOrderNo(objWO.getWorkOrderNo());
			objId.setWorkStatus(objWO.getWorkStatus());
			objId.setWorkStatusDate(currentDate);
			objId.setWorkStatusTime(currentTime);
			objId.setDistrictCode(objWO.getDistrictCode());

			objWorkStatusHistory.setid(objId);
			objWorkStatusHistory.setUserCode(Utils.SYSTEM_USERCODE);

			workOrderService.saveWorkStatusHistory(frInstance,
					objWorkStatusHistory);
			
			String oldWoFileName = Utils.WORKORDER_FILE_PREFIX
					+ workOrderNo + Utils.WORKORDER_FILE_EXTENSION;

			String woDir = this.resolveWoUploadDirFromDB(frInstance, objWO);
			woDir = woDir + "/" + oldWoFileName;
			Date date = new Date();
			
			BasicFileAttributeView attributes = Files.getFileAttributeView(Paths.get(woDir), BasicFileAttributeView.class);
		    FileTime time = FileTime.fromMillis(date.getTime());
		    attributes.setTimes(time, time, time);

			
			this.instanceManager.commitTransaction(trans);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		}  catch (WorkOrderDispatchException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (Throwable t) {

			log.error(t.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw new RuntimeException(t);
		}

		if (log.isDebugEnabled())
			log.debug("<<< dispatchWorkOrder");
	}
	
	/*
	 * FDP1073 - MLM - Added (Based on cancelWorkOrder) (non-Javadoc)
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * recallWorkOrder(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void recallWorkOrder(String frInstance, String recallUser, String workOrderNo, String districtCode, String additionalText) throws FRInstanceException,
			WorkOrderNotFoundException, WorkOrderRecallException {

		if (log.isDebugEnabled()) {

			log.debug(">>> recallWorkOrder=" + Common.CRLFEscapeString(frInstance)
					+ " woNo=" + Common.CRLFEscapeString(workOrderNo) + " districtCode=" + Common.CRLFEscapeString(districtCode));
		}

		Transaction trans = this.instanceManager.getTransaction(frInstance);

		try {
			
			WorkIssued objWO = this.workOrderService.findWorkIssued(frInstance, workOrderNo, districtCode);

			//MLM FDP1073 PVC#1 - Only check for cancel or close
			//FDE051 - MC - check aditional statuses
			if(!this.checkStatusIsValidForRecall(frInstance, objWO.getWorkStatus())) {

				// Throw exception to prevent further processing
				throw new WorkOrderRecallException(
						"Unable to recall workorder " + workOrderNo + " with status: "
								+ objWO.getWorkStatus());
			}


			// Update Fieldreach Database
			Integer currentDate = Common.generateFieldreachDBDate();
			Integer currentTime = Common.generateFieldreachDBTime();

			objWO.setWorkStatus(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.RECALLED));
			
			objWO.setWorkStatusDate(currentDate);
			objWO.setWorkStatusTime(currentTime);

			workOrderService.saveWorkIssued(frInstance, objWO);

			WorkStatusHistory objWorkStatusHistory = new WorkStatusHistory();
			WorkStatusHistoryId objId = new WorkStatusHistoryId();

			objId.setWorkOrderNo(objWO.getWorkOrderNo());
			objId.setWorkStatus(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.RECALLED));
			objId.setWorkStatusDate(currentDate);
			objId.setWorkStatusTime(currentTime);
			objId.setDistrictCode(objWO.getDistrictCode());

			objWorkStatusHistory.setid(objId);
			objWorkStatusHistory.setUserCode(recallUser);
			
			//FDE051 - MC - add additional text to work status history
			objWorkStatusHistory.setAdditionalText(additionalText);

			workOrderService.saveWorkStatusHistory(frInstance,
					objWorkStatusHistory);
			
			this.instanceManager.commitTransaction(trans);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		}  catch (WorkOrderRecallException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (Throwable t) {

			log.error(t.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw new RuntimeException(t);
		}

		if (log.isDebugEnabled())
			log.debug("<<< recallWorkOrder");
	}
	
	public void closeWorkOrder(String frInstance, String closeUser, String workOrderNo, String districtCode) throws FRInstanceException,
	WorkOrderNotFoundException, WorkOrderCloseException{
		
		if (log.isDebugEnabled()) {

			log.debug(">>> closeWorkOrder=" + frInstance
					+ " woNo=" +workOrderNo + " districtCode=" + districtCode);
		}

		Transaction trans = this.instanceManager.getTransaction(frInstance);

		try {
			
			WorkIssued objWO = this.workOrderService.findWorkIssued(frInstance, workOrderNo, districtCode);

			String preCloseApproval = this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.PRECLOSEAPPROVAL);
			
			if(preCloseApproval == null || !objWO.getWorkStatus().equalsIgnoreCase(preCloseApproval)) {

				// Throw exception to prevent further processing
				throw new WorkOrderCloseException(
						"Unable to close workorder " + objWO.getWorkOrderNo() + " with status: "
								+ objWO.getWorkStatus());
			}


			// FDP1202 24/03/2016 TM
			// Cache work order file location for deletion prior to database update.
			String woFileName = objWO.getSourceFileName();
			
			if(StringUtils.isEmpty(woFileName))
				woFileName = Utils.WORKORDER_FILE_PREFIX + objWO.getWorkOrderNo() + Utils.WORKORDER_FILE_EXTENSION;
				
			String woDir = this.resolveWoUploadDirFromDB(frInstance, objWO);
			// End FDP1202
			
			// Update Fieldreach Database
			Integer currentDate = Common.generateFieldreachDBDate();
			Integer currentTime = Common.generateFieldreachDBTime();

			objWO.setWorkStatus(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CLOSED));
			
			objWO.setWorkStatusDate(currentDate);
			objWO.setWorkStatusTime(currentTime);

			workOrderService.saveWorkIssued(frInstance, objWO);

			WorkStatusHistory objWorkStatusHistory = new WorkStatusHistory();
			WorkStatusHistoryId objId = new WorkStatusHistoryId();

			objId.setWorkOrderNo(objWO.getWorkOrderNo());
			objId.setWorkStatus(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CLOSED));
			objId.setWorkStatusDate(currentDate);
			objId.setWorkStatusTime(currentTime);
			objId.setDistrictCode(objWO.getDistrictCode());

			objWorkStatusHistory.setid(objId);
			objWorkStatusHistory.setUserCode(closeUser);

			workOrderService.saveWorkStatusHistory(frInstance,
					objWorkStatusHistory);

			// Update WorkIssuedFileRefs table
			workOrderService.deleteAttachments(frInstance, objWO.getWorkOrderNo(), objWO.getDistrictCode());

			// Now remove files from the file system.
			
			// FDP1202 24/03/2016 TM
			File woFile = new File(woDir +"/" + woFileName);
			
			if(woFile.exists())
				if(!woFile.delete())
					log.error("WorkOrder could not be deleted [" + Common.CRLFEscapeString(woFile.getAbsolutePath()) + "]");
			
			//Delete attachments
			File attachmentDir = new File(this.getAttachmentDirURI(frInstance, objWO));
			
			if(attachmentDir.exists() && attachmentDir.isDirectory()) {
				
				try {
					
					FileUtils.deleteDirectory(attachmentDir);
				}
				catch(IOException e) {
					
					log.error("Directory could not be deleted [" + Common.CRLFEscapeString(attachmentDir.getAbsolutePath()) + "]");
				}
			}
			
			// Delete Holding Dir
			File woHoldingDir = new File(woDir);
			
			if(woHoldingDir.isDirectory()) {
				
				if(woHoldingDir.list().length==0)
					if (!woHoldingDir.delete())
						log.error("Directory could not be deleted [" + Common.CRLFEscapeString(woHoldingDir.getAbsolutePath()) + "]");
			}
			
			// End FDP1202
			
			this.instanceManager.commitTransaction(trans);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		}  catch (WorkOrderCloseException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (Throwable t) {

			log.error(t.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw new RuntimeException(t);
		}

		if (log.isDebugEnabled())
			log.debug("<<< closeWorkOrder");
		
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * resolveWoUploadDirFromXML(org.w3c.dom.Document,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String resolveWoUploadDirFromXML(String frInstance, Document doc) throws ResourceTypeNotFoundException {

		if (log.isDebugEnabled())
			log.debug(">>> resolveWoUploadDirFromXML doc=XXX request=XXX");

		XMLUtils xmlUtils = new XMLUtils();

		// Determine the directory to write the work order file to.
		String woDir = fileResourceController.getFileTypeDir(
				Utils.WORKORDER_FILE_TYPE,
				frInstance);

		// Now determine the sub folder the work order is placed in.
		String subDir = null;

		// Determine the sub directory. This should be UserCode or WorkGroupCode
		// if
		// no UserCode can be found.
		subDir = xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_USERCODE_ELEMENT);

		if (subDir == null || subDir.length() == 0) {

			subDir = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_WORKGROUPCODE_ELEMENT);
		}

		woDir = woDir + File.separator + subDir;

		if (log.isDebugEnabled())
			log.debug("<<< resolveWoUploadDirFromXML");

		return woDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * resolveWoUploadDirFromDB(java.lang.String,
	 * com.amtsybex.fieldreach.backend.model.WorkIssued,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String resolveWoUploadDirFromDB(String frInstance,
			WorkIssued workOrder) throws ResourceTypeNotFoundException {

		if (log.isDebugEnabled()) {

			log.debug(">>> resolveWoUploadDirFromDB frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " workOrder=XXX request=XXX");
		}

		// Determine the directory to write the work order file to.
		String woDir = fileResourceController.getFileTypeDir(
				Utils.WORKORDER_FILE_TYPE, frInstance);

		// Now determine the sub folder the work order is placed in.

		// FDE023 TM 08/11/2013
		// Moved code to a common method to promote code reuse
		String subDir = this.getWorkOrderSubDir(workOrder);
		// End FDE023

		woDir = woDir + File.separator + subDir;

		if (log.isDebugEnabled())
			log.debug("<<< resolveWoUploadDirFromDB");

		return woDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * parseAndValidateWorkOrder(java.lang.String)
	 */
	@Override
	public Document parseAndValidateWorkOrder(String workOrderXML)
			throws SAXException, ParserConfigurationException, IOException {

		if (log.isDebugEnabled()) {

			log.debug(">>> parseAndValidateWorkOrder workOrderXML="
					+ Common.CRLFEscapeString(workOrderXML));
		}

		Document doc = null;

		// Get the schema being used to validate the work order XML
		SchemaFactory factory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");

		Schema schema = factory.newSchema(this.getClass().getClassLoader()
				.getResource(this.getWorkOrderSchema()));

		// Parse and validate the XML file. Exceptions thrown if any error
		// occurs.
		XMLUtils xmlUtils = new XMLUtils(schema);
		doc = xmlUtils.parseXML(workOrderXML);

		if (log.isDebugEnabled())
			log.debug("<<< parseAndValidateWorkOrder");

		return doc;
	}


	private boolean checkStatusIsValidForRecall(String frInstance, String status) throws FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> checkStatusIsValidForIntegration status=" + status);

		boolean result = true;

		List<String> ignoreStatusList = new ArrayList<String>();

		ignoreStatusList.add(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CANCELLED));
		ignoreStatusList.add(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CANTDO));
		ignoreStatusList.add(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CLOSED));
		ignoreStatusList.add(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.RELEASED));
		ignoreStatusList.add(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.RECALLED));
		
		String preCloseApproval = this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.PRECLOSEAPPROVAL);
		if(preCloseApproval != null) {
			ignoreStatusList.add(preCloseApproval);
		}
		

		for (String currentStatus : ignoreStatusList) {

			if (currentStatus.toUpperCase().equals(status.toUpperCase())) {

				result = false;
				break;
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< checkStatusIsValidForIntegration");

		return result;
	}
	
	private boolean checkStatusIsValidForCancel(String frInstance, String status) throws FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> checkStatusIsValidForIntegration status=" + status);

		boolean result = true;

		List<String> ignoreStatusList = new ArrayList<String>();

		ignoreStatusList.add(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CANCELLED));
		ignoreStatusList.add(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CANTDO));
		ignoreStatusList.add(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CLOSED));

		for (String currentStatus : ignoreStatusList) {

			if (currentStatus.toUpperCase().equals(status.toUpperCase())) {

				result = false;
				break;
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< checkStatusIsValidForIntegration");

		return result;
	}

	
	public boolean checkStatusIsValidForRetrieval(String frInstance, String status, boolean includeReleased) throws FRInstanceException {
		
		if (log.isDebugEnabled())
			log.debug(">>> checkStatusIsValidForRetrieval status=" + status);

		boolean result = true;

		List<String> ignoreStatusList = this.workStatuses.getWorkIssuedIgnoreStatusList(frInstance, includeReleased);

		for (String currentStatus : ignoreStatusList) {

			if (currentStatus.toUpperCase().equals(status.toUpperCase())) {

				result = false;
				break;
			}
		}

		if (log.isDebugEnabled())
			log.debug("<<< checkStatusIsValidForRetrieval");

		return result;
	}

	// FDE023 TM 08/11/2013

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * getAttachmentDirURI(java.lang.String,
	 * com.amtsybex.fieldreach.backend.model.WorkIssued)
	 */
	@Override
	public String getAttachmentDirURI(String applicationIdentifier,
			WorkIssued workIssued) throws ResourceTypeNotFoundException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getAttachmentDirURIapplicationIdentifier= "
					+ Common.CRLFEscapeString(applicationIdentifier) + "workIssued=XXX");
		}

		String retVal;

		// Now determine the directory attachments will be stored.
		String woRoot = this.fileResourceController.getFileTypeDir(
				Utils.WORKORDER_FILE_TYPE, applicationIdentifier);

		// Construct attachment directory strong
		retVal = woRoot + File.separator + this.getAttachmentSubDir(workIssued);

		if (log.isDebugEnabled())
			log.debug("<<< getAttachmentDirURI");

		return FilenameUtils.normalize(retVal);
	}

	// FDE029 TM 22/01/2015
	// Changed method access modifier to public

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * getWorkOrderSubDir(com.amtsybex.fieldreach.backend.model.WorkIssued)
	 */
	@Override
	public String getWorkOrderSubDir(WorkIssued workorder)
			throws ResourceTypeNotFoundException {

		if (log.isDebugEnabled())
			log.debug(">>> getWorkOrderSubDir workorder=XXX");

		String subDir = null;

		// Determine the sub directory. This should be UserCode or WorkGroupCode
		// if no UserCode can be found.
		subDir = workorder.getUserCode();

		if (subDir == null || subDir.length() == 0) 
			subDir = workorder.getWorkgroupCode();

		if (log.isDebugEnabled())
			log.debug("<<< getWorkOrderSubDir");

		return subDir;
	}

	// End FDE023

	// FDE029 TM 22/01/2015
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * getAttachmentSubDir(com.amtsybex.fieldreach.backend.model.WorkIssued)
	 */
	@Override
	public String getAttachmentSubDir(WorkIssued workorder)
			throws ResourceTypeNotFoundException {

		if (log.isDebugEnabled())
			log.debug(">>> getAttachmentSubDi workorder=XXX");

		String dir = null;

		dir = Utils.WORKORDER_ATTACHMENT_DIR + File.separator
				+ workorder.getDistrictCode() + "_"
				+ workorder.getId().getWorkOrderNo();

		if (log.isDebugEnabled())
			log.debug("<<< getAttachmentSubDir");

		return dir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * registerAttachment(java.lang.String,
	 * com.amtsybex.fieldreach.services.messages.RegisterAttachment,
	 * com.amtsybex.fieldreach.backend.model.WorkIssued)
	 */
	@Override
	public void registerAttachment(String frInstance,
			RegisterAttachment registerAttachment, WorkIssued workIssued)
			throws FRInstanceException, ResourceTypeNotFoundException,
			IOException, AttachmentRegistrationException, Exception {

		if (log.isDebugEnabled()) {

			log.debug(">>> registerAttachment frInstance=" + Common.CRLFEscapeString(frInstance)
					+ "registerAttachment=XXX workIssued=XXX");
		}

		Transaction trans = this.instanceManager.getTransaction(frInstance);

		try {

			// Check to see if the work order status allows an attachment to be
			// registered.
			if (this.workStatuses.getWorkIssuedIgnoreStatusList(frInstance, true).contains(workIssued.getWorkStatus())) {

				throw new AttachmentRegistrationException(
						"Unable to register attachment:"
								+ registerAttachment.getFileName());
			}

			// Update the WorkIssuedFileRefs table
			WorkIssuedFileRefs wifr = new WorkIssuedFileRefs();
			WorkIssuedFileRefsId id = new WorkIssuedFileRefsId();

			id.setWorkOrderNo(workIssued.getId().getWorkOrderNo());
			id.setDistrictCode(workIssued.getId().getDistrictCode());
			id.setFileName(registerAttachment.getFileName());

			wifr.setId(id);
			wifr.setType(registerAttachment.getFileType());
			wifr.setDescription(registerAttachment.getFileDesc());

			this.workOrderService.registerAttachment(frInstance, wifr);

			// Now create the attachment on the file system.
			boolean fileWritten = Common
					.writeBytesToFile(Common.decodeBase64(registerAttachment
									.getData()), registerAttachment
							.getFileName(), this.getAttachmentDirURI(
							frInstance, workIssued));

			if (!fileWritten) {

				throw new IOException(
						"Unable to create attachment on file system: "
								+ registerAttachment.getFileName());
			}

			this.instanceManager.commitTransaction(trans);

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (ResourceTypeNotFoundException e) {

			log.error(e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (IOException e) {

			log.error("Unable to create attachment on file system: "
					+ registerAttachment.getFileName());

			this.instanceManager.rollbackTransaction(trans);

			throw e;

		} catch (Exception e) {

			log.error("UNEXPECTED EXCEPTION: " + e.getMessage());

			this.instanceManager.rollbackTransaction(trans);

			throw e;
		}

		if (log.isDebugEnabled())
			log.debug("<<< initiateAttachmentUpload");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * deleteWorkOrderFromFileSystem(java.lang.String,
	 * com.amtsybex.fieldreach.backend.model.WorkIssued)
	 */
	@Override
	public void deleteWorkOrderFromFileSystem(String applicationIdentifier,
			WorkIssued workIssued) throws ResourceTypeNotFoundException {

		if (log.isDebugEnabled()) {

			log.debug(">>> deleteWorkOrderFromFileSystem applicationIdentifier= "
					+ Common.CRLFEscapeString(applicationIdentifier) + " workIssued=XXX");
		}

		String woDir = this.resolveWoUploadDirFromDB(applicationIdentifier,
				workIssued);

		// Delete the work order file
		File woFile = new File(woDir + File.separator
				+ getWorkorderFileName(workIssued));

		if (woFile.exists())
			woFile.delete();

		// Check to see if work order holding directory is now empty and
		// delete it if it is.
		File woHoldingDir = new File(woDir);

		if (woHoldingDir.isDirectory()) {

			if (woHoldingDir.list().length == 0)
				woHoldingDir.delete();
		}

		if (log.isDebugEnabled())
			log.debug("<<< deleteWorkOrderFromFileSystem");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * deleteAttachmentsFromFileSystem(java.lang.String,
	 * com.amtsybex.fieldreach.backend.model.WorkIssued)
	 */
	@Override
	public void deleteAttachmentsFromFileSystem(String applicationIdentifier,
			WorkIssued workIssued) throws ResourceTypeNotFoundException {

		// Delete any attachments that may be associated with the work
		// order
		File attachmentDir = new File(this.getAttachmentDirURI(
				applicationIdentifier, workIssued));

		if (attachmentDir.exists() && attachmentDir.isDirectory()) {

			try {

				FileUtils.deleteDirectory(attachmentDir);

			} catch (IOException e) {

			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * getWorkorderFileName(com.amtsybex.fieldreach.backend.model.WorkIssued)
	 */
	@Override
	public String getWorkorderFileName(WorkIssued workIssued) {

		if (log.isDebugEnabled())
			log.debug(">>> getWorkorderFileName workIssued=XXX");

		String woFileName = workIssued.getSourceFileName();

		if (woFileName == null)
			woFileName = Utils.WORKORDER_FILE_PREFIX
					+ workIssued.getId().getWorkOrderNo()
					+ Utils.WORKORDER_FILE_EXTENSION;

		if (log.isDebugEnabled())
			log.debug("<<< getWorkorderFileName");

		return woFileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * deleteAttachments(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteAttachments(String frInstance, String workOrderNo,
			String districtCode) throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> deleteAttachments frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " workOrderNo=" + Common.CRLFEscapeString(workOrderNo) + " districtCode="
					+ Common.CRLFEscapeString(districtCode));
		}

		this.workOrderService.deleteAttachments(frInstance, workOrderNo,
				districtCode);

		if (log.isDebugEnabled())
			log.debug("<<< deleteAttachments");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.WorkOrderController#
	 * findAttachments(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<WorkIssuedFileRefs> findAttachments(String frInstance,
			String workorderNo, String districtCode) throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> findAttachments frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " workorderNo=" + Common.CRLFEscapeString(workorderNo) + " districtCode="
					+ Common.CRLFEscapeString(districtCode));
		}

		List<WorkIssuedFileRefs> retVal = this.workOrderService
				.findAttachments(frInstance, workorderNo, districtCode);

		if (log.isDebugEnabled())
			log.debug("<<< findAttachments");

		return retVal;
	}

	// End FDE029

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Takes an XML document and create a new WorkIssued object by mapping the
	 * values from the document. All records created will be assigned a
	 * WorkStatus value of WORKISSUED.
	 * 
	 * @param doc
	 *            XML document to map values from.
	 * 
	 * @param district
	 *            The district the work order belongs to.
	 * 
	 * @return A WorkIssued object with values mapped from the XML document
	 *         passed in.
	 * @throws FRInstanceException 
	 */
	private WorkIssued createWorkIssuedRecordFromXML(String frInstance, Document doc,
			String districtCode) throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> mapWorkOrderToWorkIssued doc=XXX districtCode="
					+ Common.CRLFEscapeString(districtCode));
		}

		XMLUtils xmlUtils = new XMLUtils();
		WorkIssued objWorkIssued = new WorkIssued();

		Integer currentDate = Common.generateFieldreachDBDate();
		Integer currentTime = Common.generateFieldreachDBTime();

		// Get WorkOrderNo and DistrictCode for the id
		String woNo = xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_WORKORDERNO_ELEMENT);

		// If default value is being used for district code check the work order
		// to
		// see if we can get a value for district code from there.
		if (districtCode.equals(Utils.WORKORDER_DEFAULT_DISTRICT)) {

			String dc = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_DISTRICTCODE_ELEMENT);

			if (dc != null && !dc.trim().equals(""))
				districtCode = dc;
		}

		WorkIssuedId objId = new WorkIssuedId(woNo, districtCode);
		objWorkIssued.setId(objId);

		objWorkIssued.setWorkOrderDesc(xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_WORKORDERDESC_ELEMENT));

		objWorkIssued.setIssuedTime(currentTime);

		objWorkIssued.setEquipNo(xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_EQUIPNO_ELEMENT));

		objWorkIssued.setEquipDesc(xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_EQUIPDESC_ELEMENT));

		objWorkIssued.setAltEquipRef(xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_ALTEQUIPREF_ELEMENT));

		objWorkIssued.setPlanStartDate(convertStringToInteger(xmlUtils
				.evaluateXPathToString(doc, "//"
						+ Utils.WORKORDER_PLANSTARTDATE_ELEMENT)));
		
		String startTime = xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_PLANSTARTTIME_ELEMENT);
		
		Pattern timePattern = Pattern.compile("([01]?[0-9]|2[0-3])[0-5][0-9][0-5][0-9]"); 
		
		if(startTime != null) {
			if(startTime.length() == 4) {
				startTime = StringUtils.rightPad(startTime, 6, "0");
			}
			Matcher timeMatcher = timePattern.matcher(startTime); 
			if(!timeMatcher.matches()) {
				log.error("PlanStartTime " + Common.CRLFEscapeString(startTime) +" cannot be parsed for Workorder=" + Common.CRLFEscapeString(objWorkIssued.getWorkOrderNo()));
				
				startTime = null;
			}
		}
		objWorkIssued.setPlanStartTime(startTime);
		

		objWorkIssued.setReqFinishDate(convertStringToInteger(xmlUtils
				.evaluateXPathToString(doc, "//"
						+ Utils.WORKORDER_REQFINISHDATE_ELEMENT)));
		
		String finishTime = xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_REQFINISHTIME_ELEMENT);
		
		if(finishTime != null) {
			if(finishTime.length() == 4) {
				finishTime = StringUtils.rightPad(finishTime, 6, "0");
			}
			Matcher timeMatcher = timePattern.matcher(finishTime); 
			if(!timeMatcher.matches()) {
				log.error("ReqFinishTime " + Common.CRLFEscapeString(finishTime) +" cannot be parsed for Workorder=" + Common.CRLFEscapeString(objWorkIssued.getWorkOrderNo()));
				
				finishTime = null;
			}
		}
		objWorkIssued.setReqFinishTime(finishTime);

		objWorkIssued.setUserCode(xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_USERCODE_ELEMENT));

		objWorkIssued.setWoType(xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_WOTYPE_ELEMENT));

		objWorkIssued.setSourceFileName(Utils.WORKORDER_FILE_PREFIX + woNo
				+ Utils.WORKORDER_FILE_EXTENSION);

		objWorkIssued.setReturnId(null);

		objWorkIssued.setWorkStatus(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.ISSUED));

		objWorkIssued.setWorkStatusDate(currentDate);

		objWorkIssued.setWorkStatusTime(currentTime);

		objWorkIssued.setAdditionalText(xmlUtils.evaluateXPathToString(doc,
				"//" + Utils.WORKORDER_ADDITIONALTEXT_ELEMENT));

		objWorkIssued.setIssuedDate(currentDate);

		objWorkIssued.setWorkgroupCode(xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_WORKGROUPCODE_ELEMENT));
		
		objWorkIssued.setLatitude(xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_LATITUDE_ELEMENT));
		
		objWorkIssued.setLongitude(xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_LONGITUDE_ELEMENT));

		if (log.isDebugEnabled())
			log.debug("<<< mapWorkOrderToWorkIssued");

		return objWorkIssued;
	}

	/**
	 * Takes an XML document and uses it to update an existing record in the
	 * work issued table by mapping the values it contains to columns in the
	 * table.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param doc
	 *            XML document to map values from.
	 * 
	 * @param district
	 *            The district the work order belongs to.
	 * 
	 * @return A WorkIssued object with values mapped from the XML document
	 *         passed in. Null if the record to be updated could not be found.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured.
	 */
	private WorkIssued updateWorkIssuedRecordFromXML(String frInstance,
			Document doc, String district) throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> updateWorkIssuedFromXML frInstance=" + Common.CRLFEscapeString(frInstance)
					+ " doc=xxx district=" + Common.CRLFEscapeString(district));
		}

		this.statusChanged = false;
		boolean workOrderReallocated = false;
		
		// First locate the work order in the database.
		XMLUtils xmlUtils = new XMLUtils();
		WorkIssued objWO = null;
		String woNo = xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_WORKORDERNO_ELEMENT);

		// If default value is being used for district code check the work order
		// to see if we can get a value for district code from there.
		if (district.equals(Utils.WORKORDER_DEFAULT_DISTRICT)) {

			String dc = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_DISTRICTCODE_ELEMENT);

			if (dc != null && !dc.trim().equals(""))
				district = dc;
		}

		objWO = workOrderService.findWorkIssued(frInstance, woNo, district);

		// Save the usercode and workgroup so we can determine if the workorder
		// is being
		// reallocated or updated.
		String oldUserCode = objWO.getUserCode();
		String oldWorkgroup = objWO.getWorkgroupCode();

		// Update the work issued record if it was found
		if (objWO != null) {

			Integer currentDate = Common.generateFieldreachDBDate();
			Integer currentTime = Common.generateFieldreachDBTime();

			// Only update values if they are not null

			String woDesc = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_WORKORDERDESC_ELEMENT);
			objWO.setWorkOrderDesc(woDesc);

			String equipNo = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_EQUIPNO_ELEMENT);
			objWO.setEquipNo(equipNo);

			String equipDesc = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_EQUIPDESC_ELEMENT);
			if (equipDesc != null && !equipDesc.trim().equals(""))
				objWO.setEquipDesc(equipDesc);

			String altEquipRef = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_ALTEQUIPREF_ELEMENT);
			objWO.setAltEquipRef(altEquipRef);

			Integer planStartDate = convertStringToInteger(xmlUtils
					.evaluateXPathToString(doc, "//"
							+ Utils.WORKORDER_PLANSTARTDATE_ELEMENT));
			objWO.setPlanStartDate(planStartDate);
			
			
			Pattern timePattern = Pattern.compile("([01]?[0-9]|2[0-3])[0-5][0-9][0-5][0-9]"); 
			
			String startTime = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_PLANSTARTTIME_ELEMENT);
			
			if(startTime != null) {
				if(startTime.length() == 4) {
					startTime = StringUtils.rightPad(startTime, 6, "0");
				}
				Matcher timeMatcher = timePattern.matcher(startTime); 
				if(!timeMatcher.matches()) {
					log.error("PlanStartTime " + Common.CRLFEscapeString(startTime) +" cannot be parsed for Workorder=" + Common.CRLFEscapeString(objWO.getWorkOrderNo()));
					
					startTime = null;
				}
			}
			objWO.setPlanStartTime(startTime);

			Integer reqFinDate = convertStringToInteger(xmlUtils
					.evaluateXPathToString(doc, "//"
							+ Utils.WORKORDER_REQFINISHDATE_ELEMENT));
			objWO.setReqFinishDate(reqFinDate);
			
			String finishTime = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_REQFINISHTIME_ELEMENT);
			
			
			if(finishTime != null) {
				if(finishTime.length() == 4) {
					finishTime = StringUtils.rightPad(finishTime, 6, "0");
				}
				Matcher timeMatcher = timePattern.matcher(finishTime); 
				if(!timeMatcher.matches()) {
					log.error("ReqFinishTime " + Common.CRLFEscapeString(finishTime) +" cannot be parsed for Workorder=" + Common.CRLFEscapeString(objWO.getWorkOrderNo()));
					
					finishTime = null;
				}
				
			}
			objWO.setReqFinishTime(finishTime);

			String userCode = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_USERCODE_ELEMENT);
			objWO.setUserCode(userCode);

			// Check to see if usercode has changed
			//FDE051 - bugs around null user codes
			if(StringUtils.isEmpty(userCode) && !StringUtils.isEmpty(oldUserCode)) {
				workOrderReallocated = true;
			}else if (!StringUtils.isEmpty(userCode) && (!userCode.equals(oldUserCode))) {
				workOrderReallocated = true;
			}
				

			String woType = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_WOTYPE_ELEMENT);
			objWO.setWoType(woType);

			objWO.setSourceFileName(Utils.WORKORDER_FILE_PREFIX + woNo
					+ Utils.WORKORDER_FILE_EXTENSION);

			objWO.setReturnId(null);

			objWO.setWorkStatusDate(currentDate);
			objWO.setWorkStatusTime(currentTime);

			String addText = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_ADDITIONALTEXT_ELEMENT);
			objWO.setAdditionalText(addText);

			objWO.setLatitude(xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_LATITUDE_ELEMENT));
			
			objWO.setLongitude(xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_LONGITUDE_ELEMENT));
			
			String workGroup = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_WORKGROUPCODE_ELEMENT);
			objWO.setWorkgroupCode(workGroup);

			// Check to see if workgroup has changed
			if (workGroup != null && (!workGroup.equals(oldWorkgroup)))
				workOrderReallocated = true;

			if (workOrderReallocated || objWO.getWorkStatus().equals(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CANTDO))
					|| objWO.getWorkStatus().equals(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CANCELLED))) {

				objWO.setWorkStatus(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.REISSUED));

				this.statusChanged = true;
				
			} 

		}

		if (log.isDebugEnabled())
			log.debug("<<< updateWorkIssuedFromXML");

		return objWO;
	}
	
	
	private String getXMLAndModifyWorkOrderFromWorkIssued(String frInstance, WorkIssued work, WorkIssued oldWork) throws ResourceTypeNotFoundException, ResourceNotFoundException, FRInstanceException, ParserConfigurationException, SAXException, IOException, TransformerException {

		this.statusChanged = false;
		
		String workorder64 = this.getWorkOrderSourceContents(work.getWorkOrderNo(), work.getDistrictCode(), null);
		
		if(workorder64 == null || workorder64.length() == 0) {
			throw new ResourceNotFoundException();
		}
		
		 //update work order xml
		
		String workorderXML = new String(Base64.decodeBase64(workorder64.getBytes()));

		XMLUtils xmlUtils = new XMLUtils();
		Document doc = xmlUtils.parseXML(workorderXML);
		
		
		
		if(!this.workStatuses.getPreDispatchStatusList(frInstance).contains(work.getWorkStatus())
				&& !this.workStatuses.statusIsDesignated(frInstance, work.getWorkStatus(), WORKSTATUSDESIGNATION.PRECLOSEAPPROVAL)
				&& !this.workStatuses.statusIsDesignated(frInstance, work.getWorkStatus(), WORKSTATUSDESIGNATION.CLOSED)
				&& !this.workStatuses.statusIsDesignated(frInstance, work.getWorkStatus(), WORKSTATUSDESIGNATION.CANCELLED)
				&& (!StringUtils.equals(work.getWorkgroupCode(), oldWork.getWorkgroupCode())
						|| !StringUtils.equals(work.getUserCode(), oldWork.getUserCode())) ) {
			
			oldWork.setWorkStatus(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.REISSUED));
			work.setWorkStatus(this.workStatuses.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.REISSUED));
			this.statusChanged = true;
		}
		
		Node woDetailNode = doc.getElementsByTagName("WorkOrderDetail").item(0);
		
		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_WORKGROUPCODE_ELEMENT, work.getWorkgroupCode());
		oldWork.setWorkgroupCode(work.getWorkgroupCode());
		oldWork.setWorkGroup(work.getWorkGroup());

		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_USERCODE_ELEMENT, work.getUserCode());
		oldWork.setUserCode(work.getUserCode());
		oldWork.setUser(work.getUser());

		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_ADDITIONALTEXT_ELEMENT, work.getAdditionalText());
		oldWork.setAdditionalText(work.getAdditionalText());

		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_PLANSTARTDATE_ELEMENT, String.valueOf(work.getPlanStartDate()));
		oldWork.setPlanStartDate(work.getPlanStartDate());
		
		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_PLANSTARTTIME_ELEMENT, work.getPlanStartTime());
		oldWork.setPlanStartTime(work.getPlanStartTime());

		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_REQFINISHDATE_ELEMENT,  String.valueOf(work.getReqFinishDate()));
		oldWork.setReqFinishDate(work.getReqFinishDate());
		
		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_REQFINISHTIME_ELEMENT,  work.getReqFinishTime());
		oldWork.setReqFinishTime(work.getReqFinishTime());

		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_WORKORDERDESC_ELEMENT, work.getWorkOrderDesc());
		oldWork.setWorkOrderDesc(work.getWorkOrderDesc());

		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_EQUIPNO_ELEMENT, work.getEquipNo());
		oldWork.setEquipNo(work.getEquipNo());

		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_EQUIPDESC_ELEMENT, work.getEquipDesc());
		oldWork.setEquipDesc(work.getEquipDesc());
		
		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_ALTEQUIPREF_ELEMENT, work.getAltEquipRef());
		oldWork.setAltEquipRef(work.getAltEquipRef());
		
		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_WOTYPE_ELEMENT, work.getWoType());
		oldWork.setWoType(work.getWoType());
		
		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_LATITUDE_ELEMENT, work.getLatitude());
		oldWork.setLatitude(work.getLatitude());
		
		this.updateXMLField(doc, woDetailNode, Utils.WORKORDER_LONGITUDE_ELEMENT, work.getLongitude());
		oldWork.setLongitude(work.getLongitude());
		

		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc.getDocumentElement());

		trans.transform(source, result);
		
		return sw.toString();
	}
	
	
	private void updateXMLField(Document doc, Node woDetailNode, String key, String value) {

		
		NodeList modifyNodes = doc.getElementsByTagName(key);

		if(value != null) {
			Node modifyNode = null;
			if(modifyNodes != null && modifyNodes.getLength() > 0) {
				modifyNode = modifyNodes.item(0);
			}else {
				modifyNode = doc.createElement(key);
				woDetailNode.appendChild(modifyNode);
			}
			
			modifyNode.setTextContent(value);
			
		}else {
			if(modifyNodes != null && modifyNodes.getLength() > 0) {
				try {
					woDetailNode.removeChild(modifyNodes.item(0));
				}catch(Exception e) {
					log.warn("Unable to find the node "+ Common.CRLFEscapeString(modifyNodes.item(0).getLocalName()));
				}
			}
		}
	}

	/**
	 * Takes an XML document and create a new WorkStatusHistory object by
	 * mapping the values from the document.
	 * 
	 * @param doc
	 *            XML document to map values from.
	 * 
	 * @param status
	 *            Value of the work status to be assigned to the record being
	 *            created.
	 * 
	 * @return A WorkStatusHistory object with values mapped from the XML
	 *         document passed in.
	 */
	private WorkStatusHistory mapWorkOrderToWorkStatusHistory(Document doc,
			String dc, String status) {

		if (log.isDebugEnabled()) {

			log.debug(">>> mapWorkOrderToWorkStatusHistory doc=XXX dc=" + Common.CRLFEscapeString(dc)
					+ " status=" + Common.CRLFEscapeString(status));
		}

		WorkStatusHistory objWorkStatusHistory = new WorkStatusHistory();
		WorkStatusHistoryId objId = new WorkStatusHistoryId();

		XMLUtils xmlUtils = new XMLUtils();

		// Create primary key
		objId.setWorkOrderNo(xmlUtils.evaluateXPathToString(doc, "//"
				+ Utils.WORKORDER_WORKORDERNO_ELEMENT));

		objId.setWorkStatus(status);
		objId.setWorkStatusDate(Common.generateFieldreachDBDate());
		objId.setWorkStatusTime(Common.generateFieldreachDBTime());

		// If default value is being used for district code check the work order
		// to see if we can get a value for district code from there.
		if (dc.equals(Utils.WORKORDER_DEFAULT_DISTRICT)) {

			String district = xmlUtils.evaluateXPathToString(doc, "//"
					+ Utils.WORKORDER_DISTRICTCODE_ELEMENT);

			if (district != null && !district.trim().equals(""))
				dc = district;
		}

		objId.setDistrictCode(dc);

		// Set fields in the WorkStatusHistory object.
		objWorkStatusHistory.setid(objId);
		objWorkStatusHistory.setUserCode(Utils.SYSTEM_USERCODE);
		//FDE051 - MC - this additional text is different to the work order additional text and should be got from the request payload
		//objWorkStatusHistory.setAdditionalText(xmlUtils.evaluateXPathToString(doc, "//" + Utils.WORKORDER_ADDITIONALTEXT_ELEMENT));

		if (log.isDebugEnabled())
			log.debug("<<< mapWorkOrderToWorkStatusHistory");

		return objWorkStatusHistory;
	}
	
	
	private WorkStatusHistory mapWorkOrderToWorkStatusHistory(WorkIssued workIssued) {

		if (log.isDebugEnabled()) {

			log.debug(">>> mapWorkOrderToWorkStatusHistory doc=XXX dc=" + Common.CRLFEscapeString(workIssued.getDistrictCode())
					+ " status=" + Common.CRLFEscapeString(workIssued.getWorkStatus()));
		}

		WorkStatusHistory objWorkStatusHistory = new WorkStatusHistory();
		WorkStatusHistoryId objId = new WorkStatusHistoryId();


		// Create primary key
		objId.setWorkOrderNo(workIssued.getId().getWorkOrderNo());
		objId.setWorkStatus(workIssued.getWorkStatus());
		objId.setWorkStatusDate(Common.generateFieldreachDBDate());
		objId.setWorkStatusTime(Common.generateFieldreachDBTime());

		objId.setDistrictCode(workIssued.getDistrictCode());

		// Set fields in the WorkStatusHistory object.
		objWorkStatusHistory.setid(objId);
		objWorkStatusHistory.setUserCode(Utils.SYSTEM_USERCODE);
		//FDE051 - MC - this additional text is different to the work order additional text and should be got from the request payload
		//objWorkStatusHistory.setAdditionalText(workIssued.getAdditionalText());

		if (log.isDebugEnabled())
			log.debug("<<< mapWorkOrderToWorkStatusHistory");

		return objWorkStatusHistory;
	}

	/**
	 * Takes a string and converts it to an Integer object.
	 * 
	 * @param val
	 *            The string to be evaluated to an Integer object.
	 * 
	 * @return Integer representation of the String passed in. Null is returned
	 *         if a conversion cannot be made.
	 */
	private Integer convertStringToInteger(String val) {

		if (log.isDebugEnabled())
			log.debug(">>> convertStringToInteger val=" + Common.CRLFEscapeString(val));

		Integer retval = null;

		try {

			retval = Integer.parseInt(val);

		} catch (Exception e) {

			retval = null;
		}

		if (log.isDebugEnabled())
			log.debug("<<< convertStringToInteger");

		return retval;
	}


	/*-------------------------------------------
	 - Spring injection Methods
	 --------------------------------------------*/

	public WorkOrderService getWorkOrderService() {

		return workOrderService;
	}

	public void setWorkOrderService(WorkOrderService workOrderService) {

		this.workOrderService = workOrderService;
	}

	public FileResourceController getFileResourceController() {

		return fileResourceController;
	}

	public void setFileResourceController(
			FileResourceController fileResourceController) {

		this.fileResourceController = fileResourceController;
	}

	public void setWorkOrderSchema(String schema) {

		log.debug(">>> setWorkOrderSchema");

		// If no schema name is supplied then use the default schema.
		if (schema == null || schema.trim().equals("")) {

			this.workOrderSchema = Utils.WORKORDER_DEFAULT_SCHEMA;

		} else {

			this.workOrderSchema = schema;
		}

		if (log.isDebugEnabled()) {

			log.debug(">>> setWorkOrderSchema. schema set: "
					+ this.workOrderSchema);
		}
	}
	

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public WorkStatus getWorkStatuses() {
		return workStatuses;
	}

	public void setWorkStatuses(WorkStatus workStatuses) {
		this.workStatuses = workStatuses;
	}

	@Override
	public boolean hasWorkOrdersForWorkGroup(String frInstance, String workGroupCode)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> hasWorkOrdersForWorkGroup frInstance="+ Common.CRLFEscapeString(frInstance));

			log.debug("<<< hasWorkOrdersForWorkGroup");
		}

		return workOrderService.hasWorkOrdersForWorkGroup(frInstance, workGroupCode);
	
	}
	
	
	
}
