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
 */
package com.amtsybex.fieldreach.services.endpoint.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.WorkIssued;
import com.amtsybex.fieldreach.backend.model.WorkIssuedFileRefs;
import com.amtsybex.fieldreach.backend.model.WorkStatusHistory;
import com.amtsybex.fieldreach.services.exception.AttachmentRegistrationException;
import com.amtsybex.fieldreach.services.exception.WorkOrderCancelException;
import com.amtsybex.fieldreach.services.exception.WorkOrderCloseException;
import com.amtsybex.fieldreach.services.exception.WorkOrderDispatchException;
import com.amtsybex.fieldreach.services.exception.WorkOrderNotFoundException;
import com.amtsybex.fieldreach.services.exception.WorkOrderRecallException;
import com.amtsybex.fieldreach.services.messages.request.RegisterAttachment;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;

/**
 * Interface to allow common operations around work orders to be performed.
 * These operations are not specific to any type of web service implementation
 * such as SOAP or REST.
 */
public interface WorkOrderController {

	/**
	 * Retrieve all work orders for a specific user code.
	 * 
	 * The method will examine the WorkIssued table and look for records with a
	 * user code that matches that passed in the userCode parameter. Any records
	 * that contain this user code and have an allowed work status will be
	 * returned.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param userCode
	 *            User code of the user that you want to retrieve a list of work
	 *            orders for.
	 * 
	 * @return A list of WorkIssued objects representing the work orders that
	 *         are available to the user passed in.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public List<WorkIssued> getWorkOrdersByUserCode(String frInstance, String userCode, boolean includeReleased) throws FRInstanceException;

	/**
	 * Retrieve all work orders for a specific work group
	 * 
	 * The method will examine the WorkIssued table and look for records with a
	 * work group code that matches that passed in the workgroupCode parameter.
	 * Any records that contain this work group code and have an allowed work
	 * status will be returned.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param workgroupCode
	 *            workgroupCode that you want to retrieve a list of work orders
	 *            for.
	 * 
	 * @return A list of WorkIssued objects representing the work orders that
	 *         are available to the workgroupCode passed in.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public List<WorkIssued> getWorkOrdersByWorkGroupCode(String frInstance, String workgroupCode, boolean includeReleased) throws FRInstanceException;

	/**
	 * Retrieve work orders that belong to all work groups associated with the
	 * work group category of work group passed in.
	 * 
	 * The method will take the work group code passed in and determine what
	 * work group category it belongs to. This is then used to get all the work
	 * groups belonging to this category. The method will examine the WorkIssued
	 * table and look for records with a work group code that matches one of the
	 * workgroups in the resolved work group category. Any records that contain
	 * this work group code and have an allowed work status will be returned.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param workgroupCode
	 *            workgroupCode that you want to
	 * 
	 * @return A list of WorkIssued objects representing the work orders that
	 *         are associated with the work group category of work group passed
	 *         in.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public List<WorkIssued> getWorkOrdersByWorkGroupCodeCat(String frInstance, String workgroupCode, boolean includeReleased) throws FRInstanceException;

	/**
	 * Retrieve the source for the given work order number.
	 * 
	 * Using the work order number passed in the method determines the name and
	 * location of the work order file and then returns its contents encoded in
	 * base 64.
	 * 
	 * @param woNumber
	 *            The work order number of the file you want to retrieve the
	 *            source for.
	 * 
	 * @param districtCode
	 *            District code the work order you want to retrieve belongs to.
	 *            This should be null if district code is not required.
	 * 
	 * @param applicationIdentifier
	 *            The application identifier of the client that has requested
	 *            the work order file.
	 * 
	 * @return Returns the base 64 encoded contents of the work order specified.
	 * 
	 * @throws ResourceTypeNotFoundException
	 *             If the location of work order files has not been configured.
	 * 
	 * @throws ResourceNotFoundException
	 *             If an entry is not found in the WorkIssed table for the work
	 *             order passed in the woNumber parameter. Or if the work order
	 *             file to be retrieved can not be located.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public String getWorkOrderSourceContents(String woNumber, String districtCode, String applicationIdentifier)
			throws ResourceTypeNotFoundException, ResourceNotFoundException, FRInstanceException;

	/**
	 * Creates a record in the WorkIssued and WorkStatusHistory tables by
	 * mapping values from the parsed XML document passed in.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param doc
	 *            Parsed work order XML file where values are to be mapped from.
	 * 
	 * @param districtCode
	 *            District code of the work order.	 
	 *             
	 * @param modifyUser
	 *            User modifying work order - default to SYSTEM.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public void logWorkOrderUpload(String frInstance, String modifyUser, Document doc, String districtCode, boolean releasing) throws FRInstanceException;
	
	
	/**
	 * FDE051 - MC
	 * @param frInstance
	 * "param modifyUser
	 * @param doc
	 * @param districtCode
	 * @param workOrderNo
	 * @param workOrderXML
	 * @throws FRInstanceException
	 */
	public void updateWorkOrder(String frInstance, String modifyUser, Document doc, String districtCode, String workOrderNo, String workOrderXML) throws FRInstanceException, ResourceTypeNotFoundException;
	
	
	/**
	 * FDE051 - MC
	 * @param frInstance
	 * @param modifyUser
	 * @param work
	 * @throws FRInstanceException
	 * @throws ResourceTypeNotFoundException
	 */
	public void modifyWorkOrder(String frInstance, String modifyUser, WorkIssued work) throws FRInstanceException, ResourceTypeNotFoundException;
	
	
	/**
	 * FDE051 MC
	 * @param frInstance
	 * @param work
	 * @param doc
	 * @throws FRInstanceException
	 * @throws ResourceTypeNotFoundException
	 */
	//public void updateWorkOrder(String frInstance, WorkIssued work, Document doc, String workOrderXML) throws FRInstanceException, ResourceTypeNotFoundException;
	

	/**
	 * Updates a record in the WorkIssued and creates 1 record in the
	 * WorkStatusHistory tables by mapping values from the parsed XML document
	 * passed in.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param modifyUser
	 *            User modifying work order 
	 *             
	 * @param doc
	 *            Parsed work order XML file where values are to be mapped from.
	 * 
	 * @param districtCode
	 *            District code of the work order.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public void logWorkOrderUpdate(String frInstance, String modifyUser, WorkIssued workIssued) throws FRInstanceException;

	/**
	 * FDE044 - MC - change the params to take a work order as we needed to get this back in the rest controller so no point getting it twice
	 * 
	 * Updates a record in the WorkIssued table and creates 1 record in the
	 * WorkStatusHistory table and removes entries from the WorkIssuedFileRefs
	 * table.
	 * 
	 * The workorder file will also be removed from the file system and any
	 * attachments associated with it will also be removed.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param objWO
	 *            Workorder to be canceled
	 *            
	 * @param additionalText
	 * 				Additional text used for work status history
	 * 
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 * 
	 * @throws WorkOrderNotFoundException
	 *             Record could not be found in the WorkIssued table.
	 * 
	 * @throws WorkOrderCancelException
	 *             That status of the work order does not allow it to be
	 *             cancelled.
	 */
	public void cancelWorkOrder(String frInstance, WorkIssued objWO, String additionalText) throws FRInstanceException,
			WorkOrderNotFoundException, WorkOrderCancelException;

	/**
	 * FDE044 - MC - change the params to take a work order as we needed to get this back in the rest controller so no point getting it twice
	 * 
	 * Recalls the work order //MLM - FDP1073
	 * 
	 * @param frmInstance
	 *            Fieldreach instance to be used. Null means default instance.
	 *            
	 * @param recallUser 
	 * 				user that is recalling the work order
	 * 
	 * @param workOrderNo
	 *            WorkOrder to recall
	 *            
	 * @param districtCode
	 *            WorkOrder to recall        
	 *            
	 * @param additionalText
	 *            additionalText for work status history
	 * 
	 * @throws FRInstanceException
	 *             FieldReach Instance supplied has not been configured or an
	 *             exception occurred accessing this instance.
	 * 
	 * @throws WorkOrderNotFoundException
	 *             Record could not be found in the WorkIssued table.
	 * 
	 * @throws WorkOrderRecallException
	 *             That status of the work order does not allow it to be
	 *             recalled.
	 */
	public void recallWorkOrder(String frInstance, String recallUser, String workOrderNo, String districtCode, String additionalText) throws FRInstanceException,
	WorkOrderNotFoundException, WorkOrderRecallException;
	
	
	/**
	 * FDE051 MC
	 * @param frInstance
	 * @param closeUser
	 * @param workOrderNo
	 * @param districtCode
	 * @throws FRInstanceException
	 * @throws WorkOrderNotFoundException
	 * @throws WorkOrderCloseException
	 */
	public void closeWorkOrder(String frInstance, String closeUser, String workOrderNo, String districtCode) throws FRInstanceException,
	WorkOrderNotFoundException, WorkOrderCloseException;
	
	/**
	 * FDE051 - MC
	 * @param frInstance
	 * @param workOrderNo
	 * @param districtCode
	 * @throws FRInstanceException
	 * @throws WorkOrderNotFoundException
	 * @throws WorkOrderDispatchException
	 */
	public void dispatchWorkOrder(String frInstance, String workOrderNo, String districtCode) throws FRInstanceException,
	WorkOrderNotFoundException, WorkOrderDispatchException;

	/**
	 * Get the name of the schema file to be used to validate work order XML.
	 * This schema file needs to exists on the application classpath.
	 * 
	 * @return
	 */
	public String getWorkOrderSchema();

	/**
	 * Method to retrieve a record from the WokrIssued table.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param workOrderNo
	 *            The work order number of the record to be retrieved.
	 * 
	 * @return A WorkIssued object. Null is returned of no work order could be
	 *         found.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public WorkIssued getWorkIssuedRecord(String frInstance, String workOrderNo)
			throws FRInstanceException;

	/**
	 * Method to retrieve a record from the WorkIssued table.
	 * 
	 * @param workOrderNo
	 *            The work order number of the record to be retrieved.
	 * 
	 * @param districtCode
	 *            The district code of the record to be retrieved.
	 * 
	 * @return A WorkIssued object. Null is returned of no work order could be
	 *         found.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public WorkIssued getWorkIssuedRecord(String frInstance, String workOrderNo, String districtCode) throws FRInstanceException;

	/**
	 * Method to update/insert a record into the WorkIssued table.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param entity
	 *            A WorkIssued object to update/insert a record into the
	 *            WorkIssued table.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public void saveWorkIssued(String frInstance, WorkIssued entity) throws FRInstanceException;

	/**
	 * Method to update/insert a record into the WorkStatusHistory table.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param entity
	 *            A WorkStatusHistory object to update/insert a record into the
	 *            WorkStatusHistory table.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	
	public void saveWorkStatusHistory(String frInstance, WorkStatusHistory entity) throws FRInstanceException;

	/**
	 * Determine the folder that a work order should be placed in when uploading
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 *            
	 * @param doc
	 *            workorder XML being uploaded.
	 * 
	 * @return The directory that the work order file being uploaded should be
	 *         placed in.
	 * 
	 * @throws ResourceTypeNotFoundException
	 *             If unable to resolve the folder to place the work order in.
	 */
	public String resolveWoUploadDirFromXML(String frInstance, Document doc) throws ResourceTypeNotFoundException;

	// FDE029 TM 26/01/2015
	// Modify method signature.
	/**
	 * Determine the folder that a work order should be placed in by inspecting
	 * the database
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param workOrder
	 *            WorkIssued object to determine work order location form..
	 * 
	 * @return The directory that the work order file being uploaded should be
	 *         placed in.
	 * 
	 * @throws ResourceTypeNotFoundException
	 *             If unable to resolve the folder to place the work order in.
	 */
	public String resolveWoUploadDirFromDB(String frInstance, WorkIssued workOrder) throws ResourceTypeNotFoundException;

	// End FDE029

	/**
	 * Method to parse and validate the XML supplied to ensure it is a valid
	 * work order file.
	 * 
	 * @param workOrderXML
	 *            WorkOrder XML string to be parsed and validated.
	 * 
	 * @return Document object representing the parsed XML.
	 * 
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public Document parseAndValidateWorkOrder(String workOrderXML) throws SAXException, ParserConfigurationException, IOException;
	
	
	/**
	 * FDE051 - MC
	 * @param status
	 * @param includeReleased
	 * @return
	 */
	public boolean checkStatusIsValidForRetrieval(String applicationIdentifier, String status, boolean includeReleased) throws FRInstanceException;
	
	
	/**
	 * FDE051 - MC
	 * @return
	 */
	public WorkStatus getWorkStatuses();
	
	// FDE023 TM 08/11/2013

	/**
	 * Get the URI of the directory that stores attachments for the specified
	 * work order.
	 * 
	 * @param applicationIdentifier
	 *            The application identifier of the client that has requested
	 *            the work order file.
	 * 
	 * @param workIssued
	 *            WorkIssued object representing the work order to get the
	 *            attachment directory of.
	 * 
	 * @return Returns a String representation of the URI of the attachment
	 *         folder.
	 * 
	 * @throws ResourceTypeNotFoundException
	 *             If the location of work order files has not been configured.
	 */
	public String getAttachmentDirURI(String applicationIdentifier, WorkIssued workIssued) throws ResourceTypeNotFoundException;

	// End FDE023

	// FDE029 TM 22/01/2015

	/**
	 * Get the sub directory a work order xml file should be held in.
	 * 
	 * @param workorder
	 *            WorkOrderEntity for the work order we want to determine the
	 *            sub directory the work order XML file should be stored in.
	 * 
	 * @return Usercode if the usercode is present in the WorkOrder entity
	 *         supplied, or WorkgroupCode if the workgroupcode is present. If
	 *         both are present, UserCode is returned.
	 * 
	 * @throws ResourceTypeNotFoundException
	 *             Neither UserCode or WorkGroupCode are present in the
	 *             WorkOrder entity supplied.
	 */
	public String getWorkOrderSubDir(WorkIssued workorder) throws ResourceTypeNotFoundException;

	/**
	 * Get the sub directory that attachments will be held in for the supplied
	 * work order.
	 * 
	 * @param workorder
	 *            WorkOrderEntity for the work order we want to determine the
	 *            sub directory the work order attachment file should be stored
	 *            in.
	 * 
	 * @return usercode or workgroupcode/districtcode_workorderno
	 * 
	 * @throws ResourceTypeNotFoundException
	 *             Neither UserCode or WorkGroupCode are present in the
	 *             WorkOrder entity supplied.
	 */
	public String getAttachmentSubDir(WorkIssued workorder) throws ResourceTypeNotFoundException;

	/**
	 * Create a record in the WorkIssuedFileRefs table and then create the
	 * attachment on the file system.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param registerAttachment
	 *            RegisterAttachment request body from initial request.
	 * 
	 * @param workIssued
	 *            WorkIssued objected representing the work order the attachment
	 *            is associated with.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found.
	 * 
	 * @throws IOException
	 *             Error occurred creating the attachment on the file system.
	 * 
	 * @throws AttachmentRegistrationException
	 *             Status of the supplied WorkIssued object does not allow an
	 *             attachment to be registered.
	 * 
	 * @throws ResourceTypeNotFoundException
	 *             Error occurred trying to generate the attachment file sub
	 *             directory.
	 * 
	 * @throws Exception
	 *             General exception occurs during initiation process.
	 */
	public void registerAttachment(String frInstance, RegisterAttachment registerAttachment, WorkIssued workIssued)
			throws FRInstanceException, ResourceTypeNotFoundException, IOException, AttachmentRegistrationException, Exception;

	/**
	 * Delete the work order referenced by the WorkIssued objected supplied from
	 * the file system.
	 * 
	 * @param applicationIdentifier
	 *            Application identifier specified with the initial request.
	 * 
	 * @param workIssued
	 *            WorkIssued record to delete the work order file for.
	 * 
	 * @throws ResourceTypeNotFoundException
	 *             Unable to determine the root location for work order files.
	 */
	public void deleteWorkOrderFromFileSystem(String applicationIdentifier, WorkIssued workIssued) throws ResourceTypeNotFoundException;

	/**
	 * Delete attachments associated to the work order referenced by the
	 * WorkIssued objected supplied from the file system.
	 * 
	 * @param applicationIdentifier
	 *            Application identifier specified with the initial request.
	 * 
	 * @param workIssued
	 *            WorkIssued record to delete the work order file for.
	 * 
	 * @throws ResourceTypeNotFoundException
	 *             Unable to determine the root location for work order files.
	 */
	public void deleteAttachmentsFromFileSystem(String applicationIdentifier, WorkIssued workIssued) throws ResourceTypeNotFoundException;

	/**
	 * Generate work order file name for the supplied WorkIssued object
	 * 
	 * @param workIssued
	 *            WorkIssued object representing the work order to generate a
	 *            file name for.
	 * @return
	 */
	public String getWorkorderFileName(WorkIssued workIssued);

	/**
	 * Delete entries from the WorkIssuedFileRefs table with a work order number
	 * and district code matching that supplied.
	 * 
	 * @param frInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param workOrderNo
	 *            Work order number of the work order to delete attachments for.
	 * 
	 * @param districtCode
	 *            District code of the work order to delete attachments for.
	 * 
	 * @throws FRInstanceException
	 */
	public void deleteAttachments(String frInstance, String workOrderNo, String districtCode) throws FRInstanceException;

	/**
	 * Method to find entries in the WorkIssuedFileRefs table based on primary
	 * key of workorderno and districtcode.
	 * 
	 * @param FRInstance
	 *            Name of the Fieldreach instance to be used.
	 * 
	 * @param workorderNo
	 *            Workorderno of the records to be deleted.
	 * 
	 * @param districtCode
	 *            District code of the records to be deleted.
	 * 
	 * @return A list of WorkIssuedFileRefs matching the criteria supplied.
	 * 
	 * @throws FRInstanceException
	 *             The Fieldreach instance supplied could not be found.
	 */
	public List<WorkIssuedFileRefs> findAttachments(String frInstance, String workorderNo, String districtCode) throws FRInstanceException;
	
	// End FDE029
	
	/**
	 * Method to find workorder contains the supplied workgroupcodes
	 * 
	 * @param frInstance
	 * 			  Name of the Fieldreach instance to be used.
	 * @param workGroupCode
	 * 			  Check the Script Results contains workgroup supplied
	 * 
	 * @return boolean to show supplied workgroup has script results
	 * 
	 * @throws FRInstanceException
	 * 			  The Fieldreach instance supplied could not be found.
	 */
	public boolean hasWorkOrdersForWorkGroup(String frInstance, String workGroupCode) throws FRInstanceException;

}
