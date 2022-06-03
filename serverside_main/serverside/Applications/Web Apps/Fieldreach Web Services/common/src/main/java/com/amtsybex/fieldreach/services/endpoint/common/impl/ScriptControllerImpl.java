/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Author:  T Goodwin
 * Date:    10/07/2012
 * Project: FDE018
 * Description : Add in support for app identifiers
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 * Copyright AMT-Sybex 2015
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Code Re-factoring
 */
package com.amtsybex.fieldreach.services.endpoint.common.impl;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.MaxResultsExceededException;
import com.amtsybex.fieldreach.backend.instance.InstanceManager;
import com.amtsybex.fieldreach.backend.instance.Transaction;
import com.amtsybex.fieldreach.backend.model.ExtdResponse;
import com.amtsybex.fieldreach.backend.model.Item;
import com.amtsybex.fieldreach.backend.model.PublishedScripts;
import com.amtsybex.fieldreach.backend.model.ResBlobLog;
import com.amtsybex.fieldreach.backend.model.ResLog;
import com.amtsybex.fieldreach.backend.model.ResRespLog;
import com.amtsybex.fieldreach.backend.model.ResSuppLog;
import com.amtsybex.fieldreach.backend.model.ResTaskLog;
import com.amtsybex.fieldreach.backend.model.ResTxtLog;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.Script;
import com.amtsybex.fieldreach.backend.model.ScriptResultBlb;
import com.amtsybex.fieldreach.backend.model.ScriptResults;
import com.amtsybex.fieldreach.backend.model.ScriptResultsTxt;
import com.amtsybex.fieldreach.backend.model.ScriptVersions;
import com.amtsybex.fieldreach.backend.model.SuppResults;
import com.amtsybex.fieldreach.backend.model.TaskListRes;
import com.amtsybex.fieldreach.backend.model.TaskListValues;
import com.amtsybex.fieldreach.backend.model.pk.ResRespLogId;
import com.amtsybex.fieldreach.backend.model.pk.ResSuppLogId;
import com.amtsybex.fieldreach.backend.model.pk.ResTaskLogId;
import com.amtsybex.fieldreach.backend.model.pk.ScriptResultId;
import com.amtsybex.fieldreach.backend.model.pk.SuppResultsId;
import com.amtsybex.fieldreach.backend.model.pk.TaskListResId;
import com.amtsybex.fieldreach.backend.model.pk.TaskListValuesId;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.services.endpoint.common.ScriptController;
import com.amtsybex.fieldreach.services.exception.ResponseUpdateNotSupportedException;
import com.amtsybex.fieldreach.services.exception.ScriptItemNotFoundException;
import com.amtsybex.fieldreach.services.exception.ScriptResultNotFoundException;
import com.amtsybex.fieldreach.services.messages.request.UpdateScriptResult;
import com.amtsybex.fieldreach.services.messages.types.Response;
import com.amtsybex.fieldreach.services.resource.FileResourceController;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.utils.impl.Common;

public class ScriptControllerImpl implements ScriptController {

	private static Logger log = LoggerFactory.getLogger(ScriptControllerImpl.class.getName());

	private FileResourceController fileResourceController;

	private ScriptService scriptService;

	private ScriptResultsService scriptResultsService; // FDE020 TM 23/01/2013

	// FDP987 TM 13/09/2013
	private String omitScriptCodesAH;
	private List<String> omitScriptCodesAHList = new ArrayList<String>();
	
	// End FDP987

	@Autowired
	private InstanceManager instanceManager;

	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.common.ScriptController#
	 * getScriptSourceContents(java.lang.String, java.lang.String)
	 */
	@Override
	public String getScriptSourceContents(Integer scriptId, String applicationIdentifier)
			throws ResourceNotFoundException, FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getPublishedScriptContent scriptId=" + scriptId
					+ " applicationIdentifier=" + Common.CRLFEscapeString(applicationIdentifier));
		}

		// Given a script we need to get the script code so we can
		// make up the actual file name we need to serve.
		// The script id passed is a specific version of a script.
		// Check it exists first, then formulate file name as a product of
		// the script code and the id

		String retval = null;

		PublishedScripts publishedScriptContent = getScriptService().findPublishedScript(applicationIdentifier,
				Integer.valueOf(scriptId));
		
		Blob scriptFile = publishedScriptContent !=null ? publishedScriptContent.getScriptFile() : null;
		
		if (scriptFile == null) {

			log.error("Script " + scriptId + " not found");
			throw new ResourceNotFoundException();
		}
		
		try {
			
			retval = Common.encodeBase64(scriptFile.getBytes(1, (int) scriptFile.length()));
			
		} catch (SQLException e) {
			
			throw new FRInstanceException();
		}

		if (log.isDebugEnabled())
			log.debug("<<< getPublishedScriptContent");

		return retval;
	}

	// FDE020 TM 23/01/2013

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.common.ScriptController#
	 * getScriptByScriptId(java.lang.String, java.lang.Integer)
	 */
	@Override
	public Script getScriptByScriptId(String applicationidentifier, Integer scriptId) throws FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> getScriptByScriptId scriptId=" + scriptId);

		// return value
		Script objScript = null;

		ScriptVersions objVersion = this.scriptService.findScriptVersion(applicationidentifier, scriptId);

		if (objVersion != null) {

			objScript = this.scriptService.findScript(applicationidentifier, objVersion.getScriptCodeId());
		}

		if (log.isDebugEnabled())
			log.debug("<<< getScriptByScriptId");

		return objScript;
	}

	// FDP998 TM 20/09/2013
	// Modify method signature to include extra parameters

	// FDP1009 TM 17/12/2013
	// Modified method signature and updated implementation to handle this.

	// FDE034 TM 18/08/2015
	// Modified method signature.

	// FDP1152 TM 06/11/2015
	// Modified method signature - Added systemUserCode parameter.

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.common.ScriptController#
	 * getScriptResults(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Integer, java.lang.Integer, java.util.List, java.util.List,
	 * java.util.List, java.util.List, java.lang.String, java.lang.String,
	 * java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<ReturnedScripts> getScriptResults(String frInstance, String systemUserCode, String equipno,
			Integer fromDate, Integer toDate, List<String> scriptCodes, List<String> resultStatusList,
			List<String> userCodes, List<String> workgroupCodes, String altEquipRef, String workOrderNo,
			Integer resAssocReturnId, Integer viewId) throws MaxResultsExceededException, FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getScriptResults frInstance= " + Common.CRLFEscapeString(frInstance) + " systemUserCode=" + Common.CRLFEscapeString(systemUserCode)
					+ " equipno=" + Common.CRLFEscapeString(equipno) + " fromDate=" + fromDate + " toDate=" + toDate + "scriptCodes=XXX "
					+ "resultStatusList=XXX userCodes=XXX workgroupCodes=XXX " + "altEquipRef=" + Common.CRLFEscapeString(altEquipRef)
					+ " workOrderNo=" + Common.CRLFEscapeString(workOrderNo) + " resAssocReturnId=" + resAssocReturnId);
		}

		List<ReturnedScripts> results = null;

		try {

			if (workOrderNo != null) {

				// FDP1152 TM 06/11/2015
				
				if (systemUserCode == null) {
					
					results = this.scriptResultsService.getScriptResultsByWorkOrderNo(frInstance, workOrderNo);
					
				} else {
					
					results = this.scriptResultsService.getScriptResultsByWorkOrderNo(frInstance, systemUserCode, workOrderNo);
				}
				
				// End FDP1152
				
			} else if (resAssocReturnId != null) {

				ReturnedScripts res = this.scriptResultsService.getReturnedScript(frInstance, resAssocReturnId);

				// FDP1152 TM 30/11/2015
				if (systemUserCode == null) {
					
					if (res != null && res.getResAssocCode() != null)
						results = this.scriptResultsService.getScriptResultsByResAssocCode(frInstance,
								res.getResAssocCode());
				} else {
					
					if (res != null && res.getResAssocCode() != null)
						results = this.scriptResultsService.getScriptResultsByResAssocCode(frInstance, systemUserCode,
								res.getResAssocCode());
				}
				
				// End FDP1152
				
				// Remove result with returnid used to determine resaccosccode.
				
				if (results != null) {
					
					Iterator<ReturnedScripts> i = results.iterator();
	
					while (i.hasNext()) {
						
						ReturnedScripts rs = i.next();
	
						if (rs.getId().intValue() == resAssocReturnId) {
	
							i.remove();
							break;
						}
					}
				}

			} else if (viewId != null) {

				// FDP1152 TM 06/11/2015
				
				if (systemUserCode == null) {
					
					results = this.scriptResultsService.getScriptResultsByViewId(frInstance, viewId);
					
				} else {
						
					results = this.scriptResultsService.getScriptResultsByViewId(frInstance, systemUserCode, viewId);
				}

				// End FDP1152
				
			} else {

				// FDP1152 TM 06/11/2015
				
				if (systemUserCode == null) {
					
					results = this.scriptResultsService.getScriptResults(frInstance, equipno, fromDate, null, toDate, scriptCodes,
							resultStatusList, userCodes, workgroupCodes, altEquipRef);
					
				} else {
				
					results = this.scriptResultsService.getScriptResults(frInstance, systemUserCode, equipno, fromDate,  null, toDate, 
							scriptCodes, resultStatusList, userCodes, workgroupCodes, altEquipRef);
				}
				
				// End FDP1152
			}

		} catch (MaxResultsExceededException e) {

			log.error(e.getMessage());

			throw e;

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			throw e;

		} catch (Throwable t) {

			log.error("UNEXPECTED EXCEPTION: " + t.getMessage());

			throw new RuntimeException(t);
		}

		log.debug("<<< getScriptResults");

		return results;
	}

	// END FDE020

	// FDE034 TM 08/09/2015

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.common.ScriptController#
	 * updateResultResponse(java.lang.String,
	 * com.amtsybex.fieldreach.backend.model.ScriptResults,
	 * com.amtsybex.fieldreach.services.messages.request.UpdateScriptResult)
	 */
	@Override
	public void updateResultResponse(String frInstance, int returnId, int resOrderNo, UpdateScriptResult usr)
			throws FRInstanceException, ScriptResultNotFoundException, ResponseUpdateNotSupportedException,
			ScriptItemNotFoundException {

		if (log.isDebugEnabled())
			log.debug(">>> updateResultResponse frInstance=" + Common.CRLFEscapeString(frInstance) + " sr=XXX usr=XXX");

		boolean addFlag = false;
		int scriptId, seqNo;

		Transaction trans = null;

		try {

			trans = this.instanceManager.getTransaction(frInstance);

			// First search the ReturnedScripts table to see if a record with
			// the ReturnId supplied can be found.
			ReturnedScripts rs = scriptResultsService.getReturnedScript(frInstance, returnId);

			// Script result not found in ReturnedScripts to throw exception.
			if (rs == null)
				throw new ScriptResultNotFoundException("Unable to locate result in returnedscripts. returnId:" + returnId);

			// Begin by searching for a record in the ScriptResults table. If we
			// find one then we are
			// editing an existing response, otherwise we are adding one.
			ScriptResults sr = scriptResultsService.getScriptResultNoAssocData(frInstance, returnId, resOrderNo);

			// Response does not exist so it will be added.
			if (sr == null)
				addFlag = true;
			
			if(!addFlag && sr.getId().getSequenceNo() != usr.getITEM().getSeqNo()) {
				throw new ResponseUpdateNotSupportedException("Response cannot be updated, existing sequence numbers do not match for resordernumber. returnId: " + returnId + " resOrderNo:" + resOrderNo);
			}

			// Check to see if the question can indeed be updated.

			// Store information required for use later.
			scriptId = rs.getScriptId();
			seqNo = usr.getITEM().getSeqNo();

			// Now search the Item table to find the definition of the question
			// that will have
			// its response added/updated.
			Item item = scriptService.findScriptItem(frInstance, scriptId, seqNo);

			// Item could not be found so throw an exception.
			if (item == null)
				throw new ScriptItemNotFoundException("Unable to locate Item. scriptId:" + scriptId + " seqNo:" + seqNo);

			// Now ensure that the question type is one of those that supports
			// the updating of responses.
			List<String> invalidTypes = Arrays.asList(Common.QUESTION_TYPE_ATTACHMENT, Common.QUESTION_TYPE_BITMAP,
					Common.QUESTION_TYPE_CALCULATION, Common.QUESTION_TYPE_EMBEDDED_SPREADSHEET,
					Common.QUESTION_TYPE_BLOCK_MARKER, Common.QUESTION_TYPE_EXTERNAL_APP, Common.QUESTION_TYPE_HEADING,
					Common.QUESTION_TYPE_SIGNATURE, Common.QUESTION_TYPE_VIDEO, Common.QUESTION_TYPE_VOICE_RECORDING);

			if (invalidTypes.contains(item.getItemType().toUpperCase()))
				throw new ResponseUpdateNotSupportedException("Response cannot be updated. returnId: " + returnId + " resOrderNo:" + resOrderNo);

			// Got this far so update can occur. Update the ScriptResults
			// record.

			// If adding we need to create a ScriptResults record to be inserted
			// to the database.
			if (addFlag) {

				sr = new ScriptResults();

				ScriptResultId id = new ScriptResultId();
				id.setReturnId(returnId);
				id.setSequenceNo(usr.getITEM().getSeqNo());
				id.setResOrderNo(resOrderNo);

				sr.setId(id);
				
				sr.setResultDate(Integer.parseInt(usr.getITEM().getDATE()));
				sr.setResultTime(Integer.parseInt(usr.getITEM().getTIME()));
			}

			// Save response to update the various edit logs later
			String previousResponse = null;
			
			if (sr.getResponseType() != null && sr.getResponseType().equals(Common.RESPONSE_TYPE_OK)) {
				if(item.getItemType().equals(Common.QUESTION_TYPE_INSTRUCTION)) {
					previousResponse = sr.getResponse().equals("1")||sr.getResponse().equalsIgnoreCase("COMPLETED")?"COMPLETED":"NOT COMPLETED";
				}
				else {
					previousResponse = sr.getResponse();
				}
				if(sr.getExtdResponse() != null) {
					previousResponse += sr.getExtdResponse().getExtdResponse();
				}
			}else {
				previousResponse = sr.getResponseType();
			}

			String previousUom = sr.getUom();
			String previousFreeText;
			if (sr.getFreeText() == null)
				previousFreeText = null;
			else
				previousFreeText = sr.getFreeText().getFreeText();

			// Generate a new EditRefNo for logging purposes.
			int editRefNo = this.scriptResultsService.getNextEditRefNo(frInstance);

			ResLog resLog = new ResLog();
			// Update the ScriptResults record with the information supplied in
			// the UpdateScriptResults object.

			// Process the response, i.e determine its type and any
			// supplementary tables
			// if we are dealing with a Multipick or task list etc.
			String response = this.processResponse(frInstance, returnId, seqNo, resOrderNo, item.getItemType(), usr, sr,
					editRefNo,resLog);

			sr.setResponse(response);
			sr.setResponseType(usr.getITEM().getRESPONSE().getType());
			sr.setUom(usr.getITEM().getUOM());
			
			sr.setPreValue(StringUtils.left(usr.getITEM().getPREV(), 100));

			// Setting OOT Flag and calculating scores DEFERRED IN FDE034

			/*
			 * sr.setOotFlag(this.getOOTFlag(itemElement, response));
			 * 
			 * ScriptResultScore srScore = this.calculateScore(itemElement,
			 * response); sr.setScore(srScore.getScore());
			 * sr.setMaxScore(srScore.getMaxScore());
			 */

			// Save changes to ScriptResults table.

			// FDP1199 TM 15/03/2016
			
			if(addFlag)
				this.scriptResultsService.insertScriptResults(frInstance, sr);
			else
				this.scriptResultsService.saveScriptResults(frInstance, sr);
			
			// End FDP1199

			// Update/Delete Freetext as required.

			if (sr.getFreeText() != null
					&& (usr.getITEM().getFREETEXT() == null || usr.getITEM().getFREETEXT().equals("")))
				this.scriptResultsService.deleteScriptResultsTxt(frInstance, returnId, sr.getId().getResOrderNo());
			else if (usr.getITEM().getFREETEXT() != null && !usr.getITEM().getFREETEXT().equals(""))
				this.updateScriptResultsTxt(frInstance, returnId, usr, sr);

			// Now update edit logs

			/// Update ResLog table


			resLog.setId(editRefNo);
			resLog.setReturnId(returnId);
			resLog.setSequenceNumber(usr.getITEM().getSeqNo());
			resLog.setResOrderNo(resOrderNo);
			resLog.setUpdateUser(usr.getUSERCODE());
			resLog.setUpdateDate(Common.generateFieldreachDBDate());
			resLog.setUpdateTime(Common.generateFieldreachDBTime());

			if (addFlag)
				resLog.setEditType("A");
			else
				resLog.setEditType("E");

			// Update ResRespLog table

			ResRespLog resRespLog = new ResRespLog();
			ResRespLogId id = new ResRespLogId();

			id.setReturnId(returnId);
			id.setEditRefNo(editRefNo);

			resRespLog.setId(id);
			resRespLog.setResponse(previousResponse);
			resRespLog.setUom(previousUom);

			//tempfix- TODO
			//this.scriptResultsService.saveResRespLog(frInstance, resRespLog);

			// Update ResTxtLog table

			ResTxtLog resTxtLog = new ResTxtLog();

			resTxtLog.setId(id);
			resTxtLog.setFreeText(previousFreeText);
			
			//tempfix- TODO
			//this.scriptResultsService.saveResTxtLog(frInstance, resTxtLog);
			
			resLog.setResRespLog(resRespLog);
			resLog.setResTxtLog(resTxtLog);
			
			
			this.scriptResultsService.saveResLog(frInstance, resLog);

			// Commit changes to database.

			this.instanceManager.commitTransaction(trans);

		} catch (ScriptResultNotFoundException e) {

			if (trans != null)
				this.instanceManager.rollbackTransaction(trans);

			throw e;
		} catch (ResponseUpdateNotSupportedException e) {

			if (trans != null)
				this.instanceManager.rollbackTransaction(trans);

			throw e;
		} catch (ScriptItemNotFoundException e) {

			if (trans != null)
				this.instanceManager.rollbackTransaction(trans);

			throw e;
		} catch (FRInstanceException e) {

			if (trans != null)
				this.instanceManager.rollbackTransaction(trans);

			throw e;
			
		//FDP1177 TM 21/08/2016
		} catch (Throwable t) {
		
			log.error("UNEXPECTED EXCEPTION: " + t.getMessage());
			
			if (trans != null)
				this.instanceManager.rollbackTransaction(trans);
			
			throw new RuntimeException(t);
		} 
		//End FDP1177

		log.debug("<<< updateResultResponse");
	}

	// End FDE034

	/*-------------------------------------------
	 - Spring Injection Methods
	 --------------------------------------------*/

	public FileResourceController getFileResourceController() {

		return fileResourceController;
	}

	public void setFileResourceController(FileResourceController fileResourceController) {

		this.fileResourceController = fileResourceController;
	}

	public ScriptService getScriptService() {

		return scriptService;
	}

	public void setScriptService(ScriptService scriptService) {

		this.scriptService = scriptService;
	}

	// FDE020 TM 23/01/2013
	public ScriptResultsService getScriptResultsService() {

		return scriptResultsService;
	}

	public void setScriptResultsService(ScriptResultsService scriptResultsService) {

		this.scriptResultsService = scriptResultsService;
	}

	// END FDE020

	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/

	// FDP987 TM 13/09/2013

	/**
	 * Accepts a list of script codes that should be omitted from Asset History
	 * searches.
	 * 
	 * @param scriptCodeList
	 *            Comma delimited list of script codes that are to be omitted
	 *            from any Asset History searches.
	 */
	public void setOmitFromAHSearch(String scriptCodeList) {

		if (log.isDebugEnabled())
			log.debug(">>> setOmitFromAHSearch scriptCodeList=" + scriptCodeList);

		String[] temp;

		if (scriptCodeList != null) {

			omitScriptCodesAH = scriptCodeList;

			temp = scriptCodeList.split(",");

			for (int i = 0; i < temp.length; i++)
				omitScriptCodesAHList.add(temp[i].trim());
		}

		if (log.isDebugEnabled())
			log.debug("<<< setOmitFromAHSearch ");
	}

	/**
	 * Gets the comma delimited list of script codes to be omitted from Asset
	 * History searches.
	 * 
	 * @return Comma delimited list of script codes that will be omitted from
	 *         Asset History searches.
	 */
	public String getOmitFromAHSearch() {

		if (log.isDebugEnabled()) {

			log.debug(">>> getOmitFromAHSearch");

			log.debug("<<< getOmitFromAHSearch");
		}

		return omitScriptCodesAH;
	}

	/**
	 * Gets the list of script codes to be omitted from Asset History searches.
	 * 
	 * @return List of script codes that will be omitted from Asset History
	 *         searches.
	 */
	public List<String> getOmitFromAHSearchList() {

		if (log.isDebugEnabled()) {

			log.debug(">>> getOmitFromAHSearchList");

			log.debug("<<< getOmitFromAHSearchList");
		}

		return omitScriptCodesAHList;
	}

	// End FDP987

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	// FDE034 TM 09/09/2015

	/**
	 * Function to generate a response value for insertion into the
	 * ScriptResults table using the supplied UpdateScriptResult item. If the
	 * response type is N/A or N/R this will be used as the response.
	 * 
	 * If the response type is OK and the response originates from one of 3
	 * special item types (MULTI PICK, MULTI PICK (RULE BASED), TASK LIST) the
	 * response will be derived and various supplementary tables will also be
	 * updated. These tables are SuppResults, TaskListRes and TaskListValues.
	 * 
	 * For all other item types, the first 100 characters of the response will
	 * be returned and the remainder stored in the ExtdResponse table.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to perform database operations on.
	 * 
	 * @param returnId
	 *            ReturnI of the script result to generate a response for.
	 * 
	 * @param seqNo
	 *            SequenceNumber of the question to generate a response for.
	 * 
	 * @param resOrderNo
	 *            SequenceNumber of the question to generate a response for.
	 * 
	 * @param itemType
	 *            Type of the question to generate a response for.
	 * 
	 * @param usr
	 *            UpdateScriptResults object used to generate response.
	 * 
	 * @param sr
	 *            ScriptResults object the response is to be generated for.
	 * 
	 * @return Processed string to be stored in the response field of the
	 *         ScriptResults table.
	 * 
	 * @throws FRInstanceException
	 */
	private String processResponse(String frInstance, int returnId, int seqNo, int resOrderNo, String itemType,
			UpdateScriptResult usr, ScriptResults sr, int editRefNo,ResLog resLog) throws FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> processResponse frInstance=" + Common.CRLFEscapeString(frInstance) + " returnId=" + returnId + " seqNo=" + seqNo
					+ " resOrderNo=" + resOrderNo + " itemType=" + Common.CRLFEscapeString(itemType) + " user=XXX sr=XXX");

		String parsedResponse = null;

		Response response = usr.getITEM().getRESPONSE();

		
		if (itemType.equals(Common.QUESTION_TYPE_MULTI_PICK_RULE)
				|| itemType.equals(Common.QUESTION_TYPE_MULTI_PICK)) {

			// Process MULTI PICK AND MULTI PICK (RULE BASED).

			// First See if there are any existing records in the
			// SuppResults table
			// to update the ResSuppLog
			List<SuppResults> logValues = this.scriptResultsService.findSuppResults(frInstance, returnId,
					resOrderNo);

			Set<ResSuppLog> resSuppLogSet = new HashSet<ResSuppLog>();
			// Then remove any existing records and update ResSuppLog
			for (SuppResults log : logValues) {

				ResSuppLog resSuppLog = new ResSuppLog();
				ResSuppLogId id = new ResSuppLogId();

				id.setReturnId(returnId);
				id.setEditRefNo(editRefNo);
				id.setResponse(log.getId().getResponse());

				resSuppLog.setId(id);

				resSuppLogSet.add(resSuppLog);
				
				//tempfix- TODO
				//this.scriptResultsService.saveResSuppLog(frInstance, resSuppLog);

				this.scriptResultsService.deleteSuppResults(frInstance, log);
				log = null;
			}
			resLog.setResSupLog(resSuppLogSet);
			if (response.getType().equals(Common.RESPONSE_TYPE_OK)) {
				String[] responses = response.getValue().split("\\" + Common.MULTI_PICK_RESPONSE_DELIMITER);

				// First response will be inserted into ScriptResults table.
				parsedResponse = responses[0];
				
				// Then create a record for in the SuppResults table for the
				// each of remaining responses.
				for (int i = 1; i < responses.length; i++)
					this.updateSuppResults(frInstance, returnId, seqNo, resOrderNo, responses[i]);
			}
			

		}else if (itemType.equals(Common.QUESTION_TYPE_TASK_LIST)) {

			// Process TASK LIST.

			// First See if there are any existing records in the
			// TaskListRes table
			// to update the ResTaskLog
			List<TaskListRes> logValues = this.scriptResultsService.findTaskListRes(frInstance, returnId,
					resOrderNo);

			Set<ResTaskLog> resTaskLogSet = new HashSet<ResTaskLog>();
			// Then remove any existing records and update ResSuppLog
			for (TaskListRes log : logValues) {

				ResTaskLog resTaskLog = new ResTaskLog();
				ResTaskLogId id = new ResTaskLogId();

				id.setReturnId(returnId);
				id.setEditRefNo(editRefNo);
				id.setTaskOrderNo(log.getTask().getId().getTaskOrderNo());

				resTaskLog.setId(id);

				resTaskLog.setTaskDesc(log.getTask().getTaskDesc());
				resTaskLog.setTaskCompleteCode(log.getTask().getTaskCompleteCode());

				resTaskLogSet.add(resTaskLog);
				
				//tempfix- TODO
				//this.scriptResultsService.saveResTaskLog(frInstance, resTaskLog);

				this.scriptResultsService.deleteTaskListRes(frInstance, log);
			}

			resLog.setResTaskLog(resTaskLogSet);
			if (response.getType().equals(Common.RESPONSE_TYPE_OK)) {
				
				String[] tasks = response.getValue().split("\\" + Common.TASK_LIST_RESPONSE_DELIMITER);

				// Parse each task
				int totalTasks = tasks.length;
				int completedTasks = 0;
				List<String> taskNames = new ArrayList<String>();
				List<String> completeCodes = new ArrayList<String>();

				for (String task : tasks) {

					String taskName = StringUtils.left(task, task.length() - 3);
					String completeCode = StringUtils.right(task, 2);

					if (completeCode.equalsIgnoreCase(Common.TASK_LIST_TASK_COMPLETE))
						completedTasks++;

					taskNames.add(taskName);
					completeCodes.add(completeCode);
				}

				// Response for ScriptResults table
				parsedResponse = completedTasks + " of " + totalTasks;

				// Update the TaskListRes and TaskListValues tables.
				this.processTaskListResponse(frInstance, returnId, seqNo, resOrderNo, taskNames, completeCodes);
				
			}
		} else if (itemType.equals(Common.QUESTION_TYPE_PHOTOGRAPH)) {

			// Process PHOTOGRAPH questions

			// Find the current response
			ScriptResultBlb srb = sr.getResultBlob();
			
			ResBlobLog rbl = new ResBlobLog();
			ResRespLogId id = new ResRespLogId();
			id.setReturnId(returnId);
			id.setEditRefNo(editRefNo);

			rbl.setId(id);

			if (srb != null) {

				rbl.setResultBlob(Common.blobToByteArray(srb.getBlobResult()));
				rbl.setFileName(srb.getFileName());

			} else {
		
				rbl.setResultBlob(null);
				rbl.setFileName(null);
			}
			
			// Update the ResBlobLog table
			resLog.setResBlobLog(rbl);
			//tempfix- TODO
			//this.scriptResultsService.saveResBlobLog(frInstance, rbl);
			

			if (response.getType().equals(Common.RESPONSE_TYPE_OK)) {
				
				Blob blob;

				try {

					blob = new SerialBlob(Common.decodeBase64(usr.getITEM().getRESPONSE().getValue()));

				} catch (SerialException e) {

					throw new FRInstanceException(e);

				} catch (SQLException e) {

					throw new FRInstanceException(e);
				}

				if (srb == null) {
					srb = new ScriptResultBlb();
					ScriptResultId srId = new ScriptResultId();
					srId.setReturnId(returnId);
					srId.setSequenceNo(seqNo);
					srId.setResOrderNo(resOrderNo);
					srb.setId(srId);
				}
				
				srb.setBlobResult(blob);
				srb.setFileName(usr.getITEM().getRESPONSEFILENAME());
				sr.setResultBlob(srb);

				parsedResponse = null;
			}else {
				if(sr.getResultBlob() != null) {
					this.scriptResultsService.deleteScriptResultsBlb(frInstance, srb);
					sr.setResultBlob(null);
				}
			}
			

		}else {

			// Process all other responses.

			if (response.getType().equals(Common.RESPONSE_TYPE_OK) && response.getValue().length() > 100) {
				// Updated ExtdResponse table if required.

				// Use first 100 characters for the response.
				parsedResponse = StringUtils.left(response.getValue(), 100);

				// Create record in ExtdResponse table with remainder of the
				// response.
				String extdResponse = StringUtils.right(response.getValue(), response.getValue().length() - 100);

				this.updateExtdResponse(frInstance, returnId, extdResponse, sr);

			} else {

				parsedResponse = response.getValue();

				// Delete existing extd response.
				sr.setExtdResponse(null);
				scriptResultsService.deleteExtdResponse(frInstance, returnId, resOrderNo);
			}

			
		}

		if (log.isDebugEnabled())
			log.debug("<<< processResponse");

		return parsedResponse;
	}

	/**
	 * Create a record in the SuppResults table using the information supplied.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to apply database operations to.
	 * 
	 * @param returnid
	 *            ReturnId of the script result the record is associated with.
	 * 
	 * @param seqNo
	 *            SequenceNumber of the script question the record is associated
	 *            with.
	 * 
	 * @param resOrderNo
	 *            ResOrderNo of the response the record is associated with.
	 * 
	 * @param response
	 * 
	 * @throws FRInstanceException
	 */
	private void updateSuppResults(String frInstance, int returnId, int seqNo, int resOrderNo, String response)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> updateSuppResults frInstance=" + Common.CRLFEscapeString(frInstance) + " returnId=" + returnId + " seqNo=" + seqNo
					+ " resOrderNo=" + resOrderNo + " response=XXX");
		}

		SuppResults supp = new SuppResults();
		SuppResultsId id = new SuppResultsId();

		id.setReturnId(returnId);
		id.setResOrderNo(resOrderNo);
		id.setResponse(response);

		supp.setId(id);
		supp.setSequenceNo(seqNo);

		try {

			this.scriptResultsService.saveSuppResults(frInstance, supp);

		} catch (Exception e) {

			log.error("Error updating SuppResults table:\n" + e.getMessage());
			throw new FRInstanceException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateSuppResults");
	}

	/**
	 * Update/Create record in the ExtdResponse table using the information
	 * supplied.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to perform database operations on.
	 * 
	 * @param returnId
	 *            ReturnId of the script result being updated.
	 * 
	 * @param extdResponse
	 *            Value to insert into the ExtdResponse table
	 * 
	 * @param sr
	 *            ScriptResults object the extended response is associated with.
	 * 
	 * @throws FRInstanceException
	 */
	private void updateExtdResponse(String frInstance, int returnId, String extdResponse, ScriptResults sr)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> updateExtdResponse frInstance=" + Common.CRLFEscapeString(frInstance) + " returnId=" + returnId
					+ " extdResponse=XXX sr=XXX");
		}

		ExtdResponse er = sr.getExtdResponse();

		if (er == null) {

			er = new ExtdResponse();
			ScriptResultId id = new ScriptResultId();

			id.setReturnId(returnId);
			id.setSequenceNo(sr.getId().getSequenceNo());
			id.setResOrderNo(sr.getId().getResOrderNo());

			er.setId(id);
		}

		er.setExtdResponse(extdResponse);

		try {

			this.scriptResultsService.saveExtdResponse(frInstance, er);

		} catch (Exception e) {

			log.error("Error updating ExtdResponse table:\n" + e.getMessage());
			throw new FRInstanceException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateExtdResponse");
	}

	/**
	 * Update/Create a record in the FreeText table using the supplied
	 * information supplied.
	 * 
	 * @param frInstance
	 *            Fieldreach instance database operation will be applied to.
	 * 
	 * @param returnId
	 *            Returnid of the result to be updated.
	 * 
	 * @param usr
	 *            UpdateScriptResult object to be used to update the
	 *            ScriptResultsTxt table.
	 * 
	 * @param sr
	 *            ScriptResults object the FreeText is associated with.
	 * 
	 * @throws FRInstanceException
	 */
	private void updateScriptResultsTxt(String frInstance, int returnId, UpdateScriptResult usr, ScriptResults sr)
			throws FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> updateScriptResultsTxt frInstance=" + Common.CRLFEscapeString(frInstance) + " returnId=" + returnId
					+ " usr=XXX sr=XXX");

		ScriptResultsTxt srt = sr.getFreeText();

		if (srt == null) {

			srt = new ScriptResultsTxt();
			ScriptResultId srtId = new ScriptResultId();

			srtId.setReturnId(returnId);
			srtId.setSequenceNo(usr.getITEM().getSeqNo());
			srtId.setResOrderNo(usr.getITEM().getResOrderNo());

			srt.setId(srtId);
		}

		srt.setFreeText(usr.getITEM().getFREETEXT());

		try {

			this.scriptResultsService.saveScriptResultsTxt(frInstance, srt);

		} catch (Exception e) {

			log.error("Error updating ScriptResultsTxt table:\n" + e.getMessage());

			throw new FRInstanceException(e);
		}

		if (log.isDebugEnabled())
			log.debug("<<< updateScriptResultsTxt");
	}

	/**
	 * Create records in the TaskListRes and TaskListValues tables using the
	 * information supplied.
	 * 
	 * @param frInstance
	 *            Fieldreach instance to perform database operations on.
	 * 
	 * @param returnid
	 *            ReturnId of the script result the record is associated with.
	 * 
	 * @param seqNo
	 *            SequenceNumber of the script question the record is associated
	 *            with.
	 * 
	 * @param resOrderNo
	 *            ResOrderNo of the response the record is associated with.
	 * 
	 * @param taskNames
	 *            List of task names to create records in the TaskListRes table
	 *            for.
	 * 
	 * @param completeCodes
	 *            List of complete codes to create records in the TaskListValues
	 *            table for.
	 * 
	 * @throws FRInstanceException
	 */
	private void processTaskListResponse(String frInstance, int returnId, int seqNo, int resOrderNo,
			List<String> taskNames, List<String> completeCodes) throws FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> processTaskListResponse frInstance=" + Common.CRLFEscapeString(frInstance) + " returnId=" + returnId + " seqNo="
					+ seqNo + " resOrderNo=" + resOrderNo + " taskNames=XXX completeCodes=XXX");

		int nextTaskListResId;

		for (int i = 0; i < taskNames.size(); i++) {

			nextTaskListResId = scriptResultsService.getNextTaskListResId(frInstance);

			// Update TaskListRes table
			TaskListRes tlr = new TaskListRes();
			TaskListResId tlrId = new TaskListResId();

			tlrId.setReturnId(returnId);
			tlrId.setTaskListResId(nextTaskListResId);
			tlrId.setSequenceNo(seqNo);
			tlrId.setResOrderNo(resOrderNo);

			tlr.setId(tlrId);

			this.scriptResultsService.saveTaskListRes(frInstance, tlr);

			// Create entries in TaskListValues table.
			TaskListValues tlv = new TaskListValues();
			TaskListValuesId tlvId = new TaskListValuesId();

			tlvId.setTaskListId(nextTaskListResId);
			tlvId.setTaskOrderNo(i + 1);

			tlv.setId(tlvId);
			tlv.setTaskCompleteCode(completeCodes.get(i));
			tlv.setTaskDesc(taskNames.get(i));

			this.scriptResultsService.saveTaskListValues(frInstance, tlv);
		}

		if (log.isDebugEnabled())
			log.debug("<<< processTaskListResponse");
	}

	// End FDE034
}
