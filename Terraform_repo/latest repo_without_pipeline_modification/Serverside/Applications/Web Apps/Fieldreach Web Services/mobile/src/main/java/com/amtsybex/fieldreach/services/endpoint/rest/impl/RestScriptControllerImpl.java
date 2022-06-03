/**
 * Author:  T Goodwin & T Murray
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Author T Goodwin
 * Date : 10/07/2012
 * Project : FDE018
 * Description : Add application identifier to script retrieval
 * 
 * Copyright AMT-Sybex 2015
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Removal of /v1 namespace from web service end points 
 * and code re-factoring.
 * 
 * Amended:
 * FDE034 TM 18/08/2015
 * Refactored code that unmarhsals responses and converts them to a string.
 * Moved this to a re-usable method in the super class.
 * 
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.GetScriptResponse.APPLICATION_VND_FIELDSMART_SCRIPT_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.GetScriptResponse.APPLICATION_VND_FIELDSMART_SCRIPT_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.ScriptListResponse.APPLICATION_VND_FIELDSMART_SCRIPT_LIST_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.ScriptListResponse.APPLICATION_VND_FIELDSMART_SCRIPT_LIST_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.ScriptQuestionDefinitionResponse.APPLICATION_VND_FIELDSMART_SCRIPT_QUESTION_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.ScriptQuestionDefinitionResponse.APPLICATION_VND_FIELDSMART_SCRIPT_QUESTION_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_JSON;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.SCRIPT_ID_DESCRIPTION;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.ScriptQuestionDefNotFoundException;
import com.amtsybex.fieldreach.backend.exception.ScriptQuestionDefNotSupportedException;
import com.amtsybex.fieldreach.backend.model.DefectNote;
import com.amtsybex.fieldreach.backend.model.DefectSet;
import com.amtsybex.fieldreach.backend.model.DefectSetDetail;
import com.amtsybex.fieldreach.backend.model.FormatInputDef;
import com.amtsybex.fieldreach.backend.model.GenNumValid;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.Item;
import com.amtsybex.fieldreach.backend.model.ItemFileRef;
import com.amtsybex.fieldreach.backend.model.ItemRespRules;
import com.amtsybex.fieldreach.backend.model.MeasureCategories;
import com.amtsybex.fieldreach.backend.model.Media;
import com.amtsybex.fieldreach.backend.model.PublishedScripts;
import com.amtsybex.fieldreach.backend.model.ScriptRefItem;
import com.amtsybex.fieldreach.backend.model.ScriptResults;
import com.amtsybex.fieldreach.backend.model.ScriptVersions;
import com.amtsybex.fieldreach.backend.model.UnitOfMeasure;
import com.amtsybex.fieldreach.backend.model.UomNumValid;
import com.amtsybex.fieldreach.backend.model.ValFreeTextReq;
import com.amtsybex.fieldreach.backend.model.ValidationProperty;
import com.amtsybex.fieldreach.backend.model.ValidationType;
import com.amtsybex.fieldreach.backend.service.MediaService;
import com.amtsybex.fieldreach.backend.service.ScriptResultsService;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.backend.service.ValidationTypeService;
import com.amtsybex.fieldreach.services.endpoint.common.ReferenceOnlyFileController;
import com.amtsybex.fieldreach.services.endpoint.common.ScriptController;
import com.amtsybex.fieldreach.services.endpoint.rest.RestScriptController;
import com.amtsybex.fieldreach.services.exception.AuthenticationException;
import com.amtsybex.fieldreach.services.messages.response.GetScriptResponse;
import com.amtsybex.fieldreach.services.messages.response.ScriptListResponse;
import com.amtsybex.fieldreach.services.messages.response.ScriptQuestionDefinitionResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.scriptdef.ItemData;
import com.amtsybex.fieldreach.services.messages.types.scriptdef.ItemType;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api(tags = "Script/Results")
public class RestScriptControllerImpl extends BaseControllerImpl implements RestScriptController {

	private static final Logger log = LoggerFactory.getLogger(RestScriptControllerImpl.class.getName());
	
	@Autowired
	private ScriptController scriptController;
	
	// FDE020 TM 28/02/2013
	@Autowired
	private UserService userService;
	
	@Autowired
	private ScriptService scriptService;

	@Autowired
	private MediaService mediaService;

	@Autowired
	private ReferenceOnlyFileController referenceOnlyFileController;

	@Autowired
	private ScriptResultsService scriptResultsService;

	// FDE034 CM 14/09/2015
	@Autowired
	private ValidationTypeService validationTypeService;

	// FDE034 CM End
	
	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestScriptController#
	 * getScriptSource(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	@GetMapping(value = "/script/{id}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_SCRIPT_1_JSON, APPLICATION_VND_FIELDSMART_SCRIPT_1_XML})
	@ApiOperation(value = "Retrieve Script Source", 
			notes = "This service provides client applications with a means to download script files from the server."
					+ " The contents of the script file are returned with base 64 encoding")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include FileNotFoundException, FRInstanceException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - The scriptId parameter is not of type Number/Integer."),
	})
	@ResponseBody
	public ResponseEntity<GetScriptResponse> getScriptSource(@RequestHeader HttpHeaders httpHeaders, 
									@ApiParam(value = SCRIPT_ID_DESCRIPTION) @PathVariable("id") Integer scriptId) {

		if (log.isDebugEnabled())
			log.debug(">>> getScriptsource scriptId=" + scriptId);

		// Initialise response
		GetScriptResponse getScriptresponse = new GetScriptResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		getScriptresponse.setError(errorMessage);

		try {

			// FDE019 05/09/2012 TM
			// Moved Header Debug to Utility method
			// Debug headers
			Utils.debugHeaders(log, httpHeaders);
			// End FDE019

			// FDE019 10/10/2012
			String scriptSource = scriptController.getScriptSourceContents(scriptId,
					Utils.extractApplicationIdentifier(httpHeaders));

			getScriptresponse.setScriptFileSource(scriptSource);

			// Decode the Base64 data and create a checksum
			getScriptresponse
					.setChecksum(Common.generateMD5Checksum(Common.decodeBase64(scriptSource)));
			// End FDE019

		} catch (ResourceNotFoundException e) {

			log.error("Resource file not found " + scriptId + " type=" + Common.CRLFEscapeString(Utils.SCRIPT_FILE_TYPE));

			errorMessage.setErrorCode(Utils.FILE_NOT_FOUND);
			errorMessage.setErrorDescription("Resource File not found");

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception ex) {

			log.error("Exception in get Script Source " + ex.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(ex.getMessage());

		} finally {

			getScriptresponse.setSuccess(errorMessage.getErrorCode() == null);

		}

		log.debug("<<< getScriptSource");

		return ResponseEntity.ok(getScriptresponse);
	}
	
	// FDP1242 TM 25/08/2016 - Moved endpoint to integration web services

	/*
	 * FDE034 CM 07/09/2015 (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.rest.RestScriptController#
	 * getScriptQuestionDefinition(javax.servlet.http.HttpServletRequest,
	 * java.lang.String, int)
	 */
	@Deprecated
	@GetMapping(value = "/script/{id}/question/{seqno}",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_SCRIPT_QUESTION_1_JSON, APPLICATION_VND_FIELDSMART_SCRIPT_QUESTION_1_XML})
	@ApiOperation(value = "Get Script Question Definition",
			notes = "This endpoint provides the definition of individual script questions. " +
					"Clients will be able to request the definition of a particular question in a script by supplying " +
					"the script id and the sequence number of the question they require the definition of")
	@ResponseBody
	@Override
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include ScriptQuestionDefNotFoundException, ScriptQuestionDefNotSupportedException, FRInstanceException, EXCEPTION(General Exception)"),
			@ApiResponse(code = 400, message = "Bad Request - The body of the request is missing or invalid parameters"),
	})
	public ResponseEntity<ScriptQuestionDefinitionResponse> getScriptQuestionDefinition(@RequestHeader HttpHeaders httpHeaders, 
														@RequestParam(name = "returnId", required = false) String returnId, 
				@ApiParam(value = SCRIPT_ID_DESCRIPTION) @PathVariable("id") int scriptId, 
				@ApiParam(value = "Sequence number of the script") @PathVariable("seqno") int sequenceNumber) {

		if (log.isDebugEnabled())
			log.debug(">>> getScriptQuestionDefinition scriptId=" + scriptId + " seqno=" + sequenceNumber);

		// Initialise response
		ScriptQuestionDefinitionResponse responseObject = new ScriptQuestionDefinitionResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		responseObject.setError(errorMessage);

		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

		try {

			Utils.debugHeaders(log, httpHeaders);

			// Locate the Script Item in the Item table
			Item scriptQuestion = scriptService.findScriptItem(applicationIdentifier, scriptId, sequenceNumber);

			// Item not found so throw an exception.
			if (scriptQuestion == null) {

				throw new ScriptQuestionDefNotFoundException("Unable to find script Question definition. "
						+ "scriptId: " + scriptId + " seqNo:" + sequenceNumber);
			}

			// Check The question is a supported type
			if (scriptQuestion.getItemType().equals(Common.QUESTION_TYPE_ATTACHMENT)
					|| scriptQuestion.getItemType().equals(Common.QUESTION_TYPE_BITMAP)
					|| scriptQuestion.getItemType().equals(Common.QUESTION_TYPE_CALCULATION)
					|| scriptQuestion.getItemType().equals(Common.QUESTION_TYPE_EMBEDDED_SPREADSHEET)
					|| scriptQuestion.getItemType().equals(Common.QUESTION_TYPE_BLOCK_MARKER)
					|| scriptQuestion.getItemType().equals(Common.QUESTION_TYPE_EXTERNAL_APP)
					|| scriptQuestion.getItemType().equals(Common.QUESTION_TYPE_HEADING)
					|| scriptQuestion.getItemType().equals(Common.QUESTION_TYPE_SIGNATURE)
					|| scriptQuestion.getItemType().equals(Common.QUESTION_TYPE_VIDEO)
					|| scriptQuestion.getItemType().equals(Common.QUESTION_TYPE_VOICE_RECORDING)) {

				throw new ScriptQuestionDefNotSupportedException("Question type can not be retrieved: "
						+ scriptQuestion.getItemType() + " scriptId:" + scriptId + " seqNo:" + sequenceNumber);
			}

			// Begin to build response object
			ItemData itemData = new ItemData();
			ItemType itemType = new ItemType();

			itemData.setITEM(itemType);
			responseObject.setItemData(itemData);

			itemType.setALTERNATEREF(scriptQuestion.getAlternateRef());
			itemType.setDATAREF(scriptQuestion.getDataRef());
			itemType.setDEFECTSETNAME(scriptQuestion.getDefectSetName());
			itemType.setFIELDSIZE(scriptQuestion.getFieldSize());
			itemType.setILEVEL(scriptQuestion.getiLevel());
			itemType.setINPUTTYPE(scriptQuestion.getInputType());
			itemType.setITEMTEXT(scriptQuestion.getItemText());
			itemType.setITEMTYPE(scriptQuestion.getItemType());
			itemType.setPRECISION(scriptQuestion.getPrecision());
			itemType.setRELWEIGHT(scriptQuestion.getRelWeight());
			itemType.setSCRIPTID(scriptId);
			itemType.setSEQUENCENUMBER(sequenceNumber);
			itemType.setUOMCATID(scriptQuestion.getUomCatId());
			//Set Validation to default validation list to start with, change later on for Rule Based or MP Rule Based questions.  
			itemType.setVALIDATION(scriptQuestion.getValidation());  
			itemType.setDEFECTSETNAME(scriptQuestion.getDefectSetName());

			// Check for associated formatedInputDefMe table entries
			if (!(scriptQuestion.getFormatInputDefMe() == null)) {
				com.amtsybex.fieldreach.services.messages.types.scriptdef.FormatInputDefMe formatInputDefMeType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.FormatInputDefMe();

				formatInputDefMeType.setSEQUENCENUMBER(sequenceNumber);
				formatInputDefMeType.setSCRIPTID(scriptId);
				formatInputDefMeType.setMINENTRY(scriptQuestion.getFormatInputDefMe().getMinEntry());

				itemData.getFORMATINPUTDEFME().add(formatInputDefMeType);
			}

			// Check for associated defectSetItems and Defects
			if (!(scriptQuestion.getDefectSetName() == null)) {
				// DefectSetTable
				DefectSet defectSet = scriptService.findByDefectSetName(applicationIdentifier,
						scriptQuestion.getDefectSetName());

				com.amtsybex.fieldreach.services.messages.types.scriptdef.DefectSet defectSetType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.DefectSet();

				defectSetType.setDEFECTSETNAME(defectSet.getId());
				defectSetType.setDEFECTSETDESC(defectSet.getDefectSetDesc());

				// List of DefectSets for the Defect defectSetDetails =
				List<DefectSetDetail> defectSetDetails = scriptService
						.findDefectSetDetailByDefectSetName(applicationIdentifier, scriptQuestion.getDefectSetName());

				for (DefectSetDetail defectSetDetail : defectSetDetails) {
					com.amtsybex.fieldreach.services.messages.types.scriptdef.Defects defectsType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.Defects();

					com.amtsybex.fieldreach.services.messages.types.scriptdef.DefectSetDetail defectSetDetailType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.DefectSetDetail();

					defectSetDetailType.setDEFECTSETNAME(defectSetDetail.getId().getDefectSetName());
					defectSetDetailType.setDEFECTCODE(defectSetDetail.getId().getDefectCode());

					defectSetType.getDEFECTSETDETAIL().add(defectSetDetailType);

					defectsType.setDEFECTCODE(defectSetDetail.getId().getDefectCode());
					defectsType.setDEFECTDESCRIPTION(defectSetDetail.getId().getDefectSetName());

					DefectNote defectNote = scriptService.findDefectNoteByDefectCode(applicationIdentifier,
							defectSetDetail.getId().getDefectCode());

					if (!(defectNote == null)) {
						com.amtsybex.fieldreach.services.messages.types.scriptdef.DefectNote defectNoteType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.DefectNote();

						defectNoteType.setDEFECTCODE(defectNote.getId().getDefectCode());
						defectNoteType.setSNID(defectNote.getId().getSnid());

						defectsType.getDEFECTNOTE().add(defectNoteType);
					}

					itemData.getDEFECTS().add(defectsType);

				}

				itemData.setDEFECTSET(defectSetType);

			}

			// Check for associated formatInput definitions
			List<FormatInputDef> formatInputDefList = scriptService.findFormatInputDef(applicationIdentifier, scriptId,
					sequenceNumber);

			if (!(formatInputDefList == null)) {
				for (FormatInputDef formatInputDef : formatInputDefList) {
					com.amtsybex.fieldreach.services.messages.types.scriptdef.FormatInputDef formatInputDefType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.FormatInputDef();

					formatInputDefType.setSEQUENCENUMBER(formatInputDef.getId().getSequenceNoInt());
					formatInputDefType.setCHARPOS(formatInputDef.getId().getCharPosInt());
					formatInputDefType.setCHARTYPE(formatInputDef.getCharType());
					formatInputDefType.setALLOWEDCHARS(formatInputDef.getAllowedChars());
					formatInputDefType.setCASE(formatInputDef.getCaseField());

					itemType.getFORMATINPUTDEF().add(formatInputDefType);
				}

			}

			// Check for associated GenNumValid entries
			GenNumValid genNumValid = scriptService.findGenNumValidbyScriptIdSequenceNo(applicationIdentifier, scriptId,
					sequenceNumber);

			if (!(genNumValid == null)) {
				com.amtsybex.fieldreach.services.messages.types.scriptdef.GenNumValid genNumValidType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.GenNumValid();

				genNumValidType.setSEQUENCENUMBER(genNumValid.getId().getSequenceNumber());
				genNumValidType.setUPPERLIMIT(genNumValid.getUpperLimit());
				genNumValidType.setLOWERLIMIT(genNumValid.getLowerLimit());
				genNumValidType.setUPPERWARNING(genNumValid.getUpperWarning());
				genNumValidType.setLOWERWARNING(genNumValid.getLowerWarning());

				itemType.setGENNUMVALID(genNumValidType);

			}
			
			//Update Rule Based Questions validation list if necessary.  
			if((scriptQuestion.getItemType().equals(Common.QUESTION_TYPE_SINGLE_PICK_RULE)) 
					|| (scriptQuestion.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK_RULE)))
			{
				
			
				if(!(scriptQuestion.getItemRespRulesRef() == null)){
					
					// CM 30/10/2015 Bug 4479 
					//Only try to resolve if returnID is populated.  
					if(!(returnId == null))
					{
					
						//Get Question number of RefSeqNo from ItemRespRulesRef
						Integer refSeqNo = scriptQuestion.getItemRespRulesRef().getRefSeqNo();
						
						//Get response of this question by first occurrence of sequenceNumber scriptResultsTable
						ScriptResults scriptResult = scriptResultsService.getScriptResultBySequenceNumber(applicationIdentifier, Integer.parseInt(returnId), refSeqNo);
					
						//Leave as default validation list if refSeqNo is not answered.  
						if(!(scriptResult == null))
						{
						
							//Find the validation list associated with this response.  
							ItemRespRules itemRespRules 
									= scriptService.findRespRulesByScriptIdSequenceNumberPrevResponse(applicationIdentifier, scriptId, sequenceNumber, scriptResult.getResponse());
							
							
							if (!(itemRespRules == null)) 
							{
								itemType.setVALIDATION(itemRespRules.getId().getValidationType());
							}
						}
					}
				}
				
			}

			// Check for associated validation Lists
			if (!(scriptQuestion.getValidation() == null)
					&& !(scriptQuestion.getItemType().equals(Common.QUESTION_TYPE_FORMATTED_DATA_INPUT))) {

				ValidationType validationType;
				List<ValidationProperty> validationPropertyList;
				List<ValFreeTextReq> valFreeTextReqList;

				//validationType = validationTypeService.getValidationType(applicationIdentifier,scriptQuestion.getValidation());
				validationType = validationTypeService.getValidationType(applicationIdentifier,itemType.getVALIDATION());

				
				if (!(validationType == null)) {
					com.amtsybex.fieldreach.services.messages.types.scriptdef.Validation validation = new com.amtsybex.fieldreach.services.messages.types.scriptdef.Validation();

					validation.setVALIDATIONTYPE(validationType.getId());
					validation.setVALIDATIONDESC(validationType.getDescription());

					//validationPropertyList = validationTypeService.getValidationPropertyByValidationTypeWeightScoreDesc(
					//		applicationIdentifier, scriptQuestion.getValidation());
					
					validationPropertyList = validationTypeService.getValidationPropertyByValidationTypeWeightScoreDesc(
							applicationIdentifier, itemType.getVALIDATION());

					
					//Bug 4478 CM 02/10/2015
					boolean validOptionsDefined = false;
					
					for (ValidationProperty validationProperty : validationPropertyList) {
						
						if(!(validationProperty.getValidationProperty().startsWith("$")))
						{
							validOptionsDefined = true;
							break;
						}
						
					}
					
					for (ValidationProperty validationProperty : validationPropertyList) {
						com.amtsybex.fieldreach.services.messages.types.scriptdef.ValidationProperty validationPropertyType 
												= new com.amtsybex.fieldreach.services.messages.types.scriptdef.ValidationProperty();

						if(validationProperty.getValidationProperty().startsWith("$"))
						{
							if(!(validOptionsDefined))
							{
								//Identified as dynamic validation list.  
								validation.getVALIDATIONPROPERTY().clear();
								
								//ADD NO OPTIONS DEFINED ITEM
								validationPropertyType.setVALIDATIONPROPERTY("NO OPTIONS DEFINED");
								validationPropertyType.setVALIDATIONTYPE(validationProperty.getValidationType());
								validation.getVALIDATIONPROPERTY().add(validationPropertyType);
								
								break;
							}
							
						}
						else
						{
						
							validationPropertyType.setCOLOUR(validationProperty.getColour());
							validationPropertyType.setEQUIVVALUE(validationProperty.getEquivValue());
							validationPropertyType.setVALIDATIONPROPERTY(validationProperty.getValidationProperty());
							validationPropertyType.setVALIDATIONTYPE(validationProperty.getValidationType());
							validationPropertyType.setWEIGHTSCORE(validationProperty.getWeightScore());
	
							validation.getVALIDATIONPROPERTY().add(validationPropertyType);
						}
						//Bug 4478 End
					}

					itemData.getVALIDATION().add(validation);

				}
				// Check for associated valFreeText entries
				
				valFreeTextReqList = validationTypeService.findByScriptIDSequenceNumberValidationType(
						applicationIdentifier, scriptId, sequenceNumber, itemType.getVALIDATION());
				
				if (!(valFreeTextReqList == null)) {
					for (ValFreeTextReq valFreeTextReq : valFreeTextReqList) {
						com.amtsybex.fieldreach.services.messages.types.scriptdef.ValFreeTextReq valFreeTextReqType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.ValFreeTextReq();

						valFreeTextReqType.setDISPLAYMESSAGE(valFreeTextReq.getDisplayMessage());
						valFreeTextReqType.setRESPONSE(valFreeTextReq.getId().getResponse());
						valFreeTextReqType.setSEQUENCENUMBER(valFreeTextReq.getId().getSequenceNo());
						valFreeTextReqType.setVALIDATIONTYPE(valFreeTextReq.getId().getValidationType());

						itemType.getVALFREETEXTREQ().add(valFreeTextReqType);
					}
				}

			}

			// Check for associated UOMNumValids
			List<UomNumValid> UOMNumValids = scriptService.findUomNumValidbyScriptIdSequenceNo(applicationIdentifier,
					scriptId, sequenceNumber);

			if (!(UOMNumValids == null)) {
				for (UomNumValid uomNumValid : UOMNumValids) {
					com.amtsybex.fieldreach.services.messages.types.scriptdef.UomNumValid uomNumValidType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.UomNumValid();

					uomNumValidType.setSEQUENCENUMBER(uomNumValid.getId().getSequenceNumber());
					uomNumValidType.setUOMTYPE(uomNumValid.getId().getUomType());
					uomNumValidType.setLOWERWARNING(uomNumValid.getLowerWarning());
					uomNumValidType.setUPPERWARNING(uomNumValid.getUpperWarning());
					uomNumValidType.setLOWERLIMIT(uomNumValid.getLowerLimit());
					uomNumValidType.setUPPERLIMIT(uomNumValid.getUpperLimit());

					itemType.getUOMNUMVALID().add(uomNumValidType);
				}

			}

			// Search for Unit Of Measure Categories
			if (!(scriptQuestion.getUomCatId() == null)) {
				MeasureCategories measureCategories;
				List<UnitOfMeasure> unitOfMeasureList;

				measureCategories = scriptService.findByUOMCatID(applicationIdentifier,
						scriptQuestion.getUomCatIdInt());

				com.amtsybex.fieldreach.services.messages.types.scriptdef.MeasureCategories measureCategoriesType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.MeasureCategories();

				if (!(measureCategories == null)) {

					measureCategoriesType.setCATNAME(measureCategories.getCatName());
					measureCategoriesType.setUOMCATID(measureCategories.getId());

					unitOfMeasureList = scriptService.findUOMByUOMCatID(applicationIdentifier,
							scriptQuestion.getUomCatId());

					for (UnitOfMeasure unitOfMeasure : unitOfMeasureList) {
						com.amtsybex.fieldreach.services.messages.types.scriptdef.UnitOfMeasure unitOfMeasureType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.UnitOfMeasure();

						unitOfMeasureType.setUOMCATID(unitOfMeasure.getId().getUomCatId());
						unitOfMeasureType.setUOMTYPE(unitOfMeasure.getId().getUomType());
						unitOfMeasureType.setPREF(unitOfMeasure.getPref());

						measureCategoriesType.getUNITOFMEASURE().add(unitOfMeasureType);
					}

					itemData.getMEASURECATEGORIES().add(measureCategoriesType);
				}
			}

			// Search for associated script ref items and corresponding details
			// from RefCat, RefSubCat and RefItem tables
			List<ScriptRefItem> scriptRefItemList = scriptService
					.findScriptRefItemsbyScriptIdSequenceNumber(applicationIdentifier, scriptId, sequenceNumber);

			if (!(scriptRefItemList == null)) {
				for (ScriptRefItem scriptRefItem : scriptRefItemList) {

					//if scriptrefItem already added then don't add again.  

					com.amtsybex.fieldreach.services.messages.types.scriptdef.RefItem refItemType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.RefItem();

					com.amtsybex.fieldreach.services.messages.types.scriptdef.RefSubCat refSubCatType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.RefSubCat();

					com.amtsybex.fieldreach.services.messages.types.scriptdef.RefCat refCatType = new com.amtsybex.fieldreach.services.messages.types.scriptdef.RefCat();

					
					refItemType.setREFID(scriptRefItem.getRefItem().getId());
					refItemType.setOTHERREF(scriptRefItem.getRefItem().getOtherRef());
					refItemType.setREFDESC(scriptRefItem.getRefItem().getRefDesc());
					refItemType.setREFSUBCATID(scriptRefItem.getRefItem().getRefSubCat().getIdInt());

					refSubCatType.setREFCATID(scriptRefItem.getRefItem().getRefSubCat().getRefCat().getId());
					refSubCatType.setREFSUBCATDESC(scriptRefItem.getRefItem().getRefSubCat().getRefSubCatDesc());
					refSubCatType.setREFSUBCATTYPE(scriptRefItem.getRefItem().getRefSubCat().getRefSubCatType());
					refSubCatType.setREFSUBCATID(scriptRefItem.getRefItem().getRefSubCat().getId());

					refCatType.setREFCATDESC(scriptRefItem.getRefItem().getRefSubCat().getRefCat().getRefCatDesc());
					refCatType.setREFCATID(scriptRefItem.getRefItem().getRefSubCat().getRefCat().getId());
					
					//Added equals comapaitaor to each of the Types so we can look up and see if they already exist. 
					//if so we get them from the arrays and dont add them again.  
					
					if(itemData.getREFCAT().contains(refCatType))
					{
						refCatType = itemData.getREFCAT().get(itemData.getREFCAT().indexOf(refCatType));
					}
					else
					{
						itemData.getREFCAT().add(refCatType);
					}
					
					if(refCatType.getREFSUBCAT().contains(refSubCatType))
					{
						refSubCatType = refCatType.getREFSUBCAT().get(refCatType.getREFSUBCAT().indexOf(refSubCatType));
					}
					else
					{
						refCatType.getREFSUBCAT().add(refSubCatType);
					}
					
					if(refSubCatType.getREFITEM().contains(refItemType))
					{
						refItemType = refSubCatType.getREFITEM().get(refSubCatType.getREFITEM().indexOf(refItemType));
					}
					else
					{
						refSubCatType.getREFITEM().add(refItemType);
					}
					

				}

			}

		} catch (ScriptQuestionDefNotFoundException e) {

			log.error(e.getMessage());
			errorMessage.setErrorCode(Utils.QUESTION_DEF_NOT_FOUND_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (ScriptQuestionDefNotSupportedException e) {

			log.error(e.getMessage());
			errorMessage.setErrorCode(Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception ex) {

			log.error("Exception in getScriptQuestionDefinition " + ex.getMessage());

			errorMessage.setErrorCode(Utils.GENERAL_EXCEPTION);
			errorMessage.setErrorDescription(ex.getMessage());

		} finally {

			responseObject.setSuccess(errorMessage.getErrorCode() == null);

		}

		log.debug("<<< getScriptQuestionDefinition");

		return ResponseEntity.ok(responseObject);

	}
	
	@Override
	@GetMapping(value = "/script/list",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_SCRIPT_LIST_1_JSON, APPLICATION_VND_FIELDSMART_SCRIPT_LIST_1_XML})
	@ApiOperation(value = "Get Script File List",
			notes = "This service provides client applications with a means to retrieve a list of scripts"
					+ " that are available to download for a particular user.")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include FRInstanceException, EXCEPTION(General Exception)"),
	})
	public ResponseEntity<ScriptListResponse> getScriptList(@RequestHeader HttpHeaders httpHeaders)  {

		log.debug(">>> getScriptList");
		
		HPCUsers hpcUsers = null;

		// Initialise response
		ScriptListResponse scriptListResponse = new ScriptListResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		scriptListResponse.setError(errorMessage);
		
		// Get application identifier
		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

		try {

			// Debug headers
			Utils.debugHeaders(log, httpHeaders);
			hpcUsers = this.getUserDetailsFromUserPrincipal(applicationIdentifier);

			// Search database for workgroup code.
			HPCWorkGroups objWorkgroup = hpcUsers.getWorkGroup();

			// Set Script List in response.
			scriptListResponse.setScriptList(this.getScripts(objWorkgroup, applicationIdentifier));

		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		}catch (AuthenticationException e) {

			log.error(e.getMessage());
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, e.getLocalizedMessage(), e);

		}
		
		finally {

			scriptListResponse.setSuccess(errorMessage.getErrorCode() == null);

		}

		log.debug("<<< getScriptList");

		return ResponseEntity.ok(scriptListResponse);
	}

	// FDE034 TM 04/09/2015

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Retrieve a list of scripts available to the work group referenced by the
	 * request. The web service check to see if the workgroup specified is
	 * associated with a script profile and will return all scripts in that
	 * profile. If no profile is found, all online scripts are returned.
	 * 
	 * @param workgroup
	 *            HPCWorkgroups object representing the workgroup to check for a
	 *            script profile
	 * 
	 * @param applicationIdentifier
	 *            Application Identifier of the device that made the request.
	 * 
	 * @return List of Script objects to be added to the ScriptList field of a
	 *         ScriptListResponse object.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured.
	 */
	private List<com.amtsybex.fieldreach.services.messages.types.Script> getScripts(HPCWorkGroups workgroup,
			String applicationIdentifier) throws FRInstanceException {

		log.debug(">>> getScriptsForUser objUser=XXX request=XXX");

		// Get script profile from work group.
		Integer profileId = null;

		if (workgroup != null)
			profileId = workgroup.getProfileId();

		// If a profile was found then get scripts associated with that profile,
		// otherwise
		// get all online scripts.
		List<ScriptVersions> lstScripts = null;

		if (profileId != null && profileId > 0) {

			if (log.isDebugEnabled())
				log.debug("Get scripts for profile " + profileId);

			lstScripts = scriptService.findScriptVersionForProfile(applicationIdentifier, profileId);

		} else {

			log.debug("Get online scripts");

			lstScripts = scriptService.findOnlineScriptVersions(applicationIdentifier);
		}

		// Build a List of Script objects to be returned by the method.
		List<com.amtsybex.fieldreach.services.messages.types.Script> lstAuthScripts = null;

		if (lstScripts != null && lstScripts.size() > 0) {

			if (log.isDebugEnabled())
				log.debug("Number of script versions :" + lstScripts.size());

			lstAuthScripts = new ArrayList<com.amtsybex.fieldreach.services.messages.types.Script>();

			// Iterate over each Script entity returned from the database.
			for (ScriptVersions objScriptVersion : lstScripts) {

				PublishedScripts publishedScript = null;
				// FDP1027 TM 29/04/2014
				// Get the online script version rather than getting all
				// versions and
				// iterating over the list to find the online version.

				if (objScriptVersion != null && objScriptVersion.getOnlineStatusInt() == 1) {

					try {

						publishedScript = scriptService.findPublishedScript(applicationIdentifier, objScriptVersion.getId());
						
						if(publishedScript !=null) {
							
							com.amtsybex.fieldreach.services.messages.types.Script script = new com.amtsybex.fieldreach.services.messages.types.Script();
	
							script.setScriptCode(objScriptVersion.getScript().getScriptCode());
							script.setScriptDesc(objScriptVersion.getScript().getScriptDesc());
	
							script.setDateCreated(
									new BigInteger(String.format("%d", objScriptVersion.getDateCreatedInt())));
	
							script.setVersionNumber(
									new BigInteger(String.format("%d", objScriptVersion.getVersionNumberInt())));
	
							// Id is the scriptversion id (not the scriptid)
							script.setScriptId(objScriptVersion.getId().toString());
							script.setChecksum(publishedScript.getChecksum());
							script.setScriptFileName(publishedScript.getScriptFileName());
	
							// Add script references
							addReferenceOnlyFiles(script, applicationIdentifier);
	
							// Add to list of scripts.
							lstAuthScripts.add(script);
							
						}

					} catch (NumberFormatException ne) {

						log.error("Format Exception for ScriptVersion " + ne.getMessage());
					}
				}
				// End FDP1027
			}

		} else {

			log.debug("No user scripts ");
		}

		log.debug("<<< getScriptsForUser");

		return lstAuthScripts;
	}

	/**
	 * Method to add reference only files of a script to returning message
	 * 
	 * @param script
	 *            Script class to add references to. If script referenced in
	 *            script message has reference only file, these will be added as
	 *            file def elements. If the file exists in script resource
	 *            folder, then checksum will also be returned.
	 * 
	 * @param applicationIdentifier
	 *            Application Identifier of the device that made the request.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured.
	 */
	private void addReferenceOnlyFiles(com.amtsybex.fieldreach.services.messages.types.Script script,
			String applicationIdentifier) throws FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> addReferenceOnlyFiles script=" + script.getScriptCode());

		// Get list of reference only files for the script
		// Then determine if exist

		List<ItemFileRef> lstItemFileRef = scriptService.getItemFileRefByScriptId(applicationIdentifier,
				Integer.valueOf(script.getScriptId()).intValue());

		// Keep track of media items already referenced so we don't configure
		// duplicates
		List<Integer> itemsAlreadyAdded = new ArrayList<Integer>();

		if (lstItemFileRef != null && lstItemFileRef.size() > 0) {

			for (ItemFileRef objFileRef : lstItemFileRef) {

				if (log.isDebugEnabled()) {

					log.debug("Item file ref :" + objFileRef.getFileName() + " mediaid="
							+ objFileRef.getId().getMediaId());
				}

				if (objFileRef.getId().getMediaId() != null
						&& !itemsAlreadyAdded.contains(objFileRef.getId().getMediaId())) {

					Media objMedia = mediaService.getRefOnlyMedia(applicationIdentifier, objFileRef.getId().getMediaId());

					if (log.isDebugEnabled()) {

						log.debug("Media :" + (objMedia == null ? "NULL" : objMedia.getFileName()) + "Ref only ?:"
								+ (objMedia == null ? "NULL" : objMedia.getRefOnlyInt()));
					}

					if (objMedia != null ) {

						if (log.isDebugEnabled()) {

							log.debug("Ref only media file :" + objMedia.getFileName());
						}

						// Keep track of what is added.
						itemsAlreadyAdded.add(objFileRef.getId().getMediaId());

						com.amtsybex.fieldreach.services.messages.types.File objFile = new com.amtsybex.fieldreach.services.messages.types.File();

						objFile.setName(objMedia.getFileName());

						// Add checksum if file exists
						if (referenceOnlyFileController.referenceOnlyFileExists(objMedia.getFileName(),
								applicationIdentifier)) {

							objFile.setChecksum(referenceOnlyFileController
									.referenceOnlyFileChecksum(objMedia.getFileName(), applicationIdentifier));
						} else {

							objFile.setChecksum("");

							log.warn("Script reference file " + objMedia.getFileName() + " does not exist");
						}

						// Add to list (whether exists or not)
						script.getFilerefs().add(objFile);
					}
				}
			}

		} else {

			log.debug("No references");
		}

		log.debug("<<< addReferenceOnlyFiles");
	}
}
