package com.amtsybex.fieldreach.fdm.details;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.io.IOUtils;
import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.FormatInputDef;
import com.amtsybex.fieldreach.backend.model.Item;
import com.amtsybex.fieldreach.backend.model.NextStatusDef;
import com.amtsybex.fieldreach.backend.model.ResLog;
import com.amtsybex.fieldreach.backend.model.ResultNotes;
import com.amtsybex.fieldreach.backend.model.ResultStatusLog;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.ScriptStatusDef;
import com.amtsybex.fieldreach.backend.model.ScriptVersions;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.model.UomNumValid;
import com.amtsybex.fieldreach.extract.core.ExtractEngine;
import com.amtsybex.fieldreach.fdm.Environment;
import com.amtsybex.fieldreach.fdm.SystemActivityLogger.ACTIVITYTYPE;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardServiceManager;
import com.amtsybex.fieldreach.fdm.search.SearchResult;
import com.amtsybex.fieldreach.fdm.search.SearchResults;
import com.amtsybex.fieldreach.fdm.util.DateUtil;
import com.amtsybex.fieldreach.fdm.web.jsf.util.ContextHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.ManagedBeanHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.services.endpoint.common.ScriptController;
import com.amtsybex.fieldreach.services.exception.ResponseUpdateNotSupportedException;
import com.amtsybex.fieldreach.services.exception.ScriptItemNotFoundException;
import com.amtsybex.fieldreach.services.exception.ScriptResultNotFoundException;
import com.amtsybex.fieldreach.services.messages.request.UpdateScriptResult;
import com.amtsybex.fieldreach.utils.impl.Common;

@Named
@WindowScoped
public class Details extends DetailsBase implements Serializable {
	
	private static final long serialVersionUID = 124006445498749887L;
	
	private static Logger _logger = LoggerFactory.getLogger(Details.class.getName());
	
	private ResultSet selectedAnswer;

	private String addResNotes;
	private List<ResultNotes> notesData; 
	private Integer selectedRowCount;
	
	//FDE058 - Support for "Notes" associated to script results
	private ResultNotes selectedResultNotes;
	private boolean addResultNotesFlag;
	private boolean addResponseFlag;
	private ResultSet rowLevelDetails;
	private Integer resultNotesRowNo = 0;
	private List<ResultNotes> resultNotes;
	private Boolean showEditLog;
	
	private List<ResultStatusLog> resultHistoryStatusLog;

	private String regExp="^'\"";
	
	private boolean isResultEditable;
	
	
	private boolean mandatoryResponse;
	private String mandatoryResponseErrorMessage;
	//private int mandateFormatFields;
	
	private Integer editDate;
	private Integer editTime;
	
	@Inject
	transient SearchResults searchResults;
	
	@Inject
	transient DashboardServiceManager serviceManager;
	
	@Inject
	transient ScriptController scriptController;
	
	
	//FDP1406
	private Map<String, Object> nextStatuses;
	//FDP1406
	private String nextStatus;	
	
	//FDE060 - KN - Associated Results List
	protected List<ReturnedScripts> assoScriptResults;
	
	
	/**
	 * init method for building up the details screen based on the selected result
	 * 
	 * @param script	- script the user has selected on the search results screen
	 * @throws FRInstanceException 
	 */
	@Override
	public void initialiseAdditionalInformation(){
		
		try {
			
			SystemUsers systemUser = this.getUserService().getSystemUser(null, this.getUsername());
	        setIsResultEditable(this.getScriptResultsService().canEditResult(null, systemUser, this.getReturnId()));
	        
	        this.setNotesData(getScriptResultsService().getResultNotes(null, String.valueOf(script.getId())));
			
			//37392 - Set the Collection of notes at script level
	        setResultNotes(this.getNotesData().stream()
	                .filter(n -> n.getId().getSequenceNumber().equals(0))
	                .collect(Collectors.toList()));
	        
			//40118 - fetch the result status history with extended system users info and render it
	        setResultHistoryStatusLog(getScriptResultsService().getScriptResultStatusHistory(null, script.getId(), true));

			getEquipmentInformation().populateEquipmentAssetInformation(this.script.getEquipNo());
			
			this.setAssoScriptResults(getScriptResultsService().findAssociatedScriptResults(null, script.getResAssocCode(), this.returnId));
			
		}catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
	}


	//@Override
	public void setAdditionalResponseInfo(ResultSet response) {
		
		response.setResultNotes(this.getNotesData().stream()
                .filter(n -> n.getId().getSequenceNumber().equals(response.getSequenceNo()) &&  n.getId().getResOrderNo().equals(response.getResOrderNo()))
                .collect(Collectors.toList()));
		
		try {
			
			response.setResLogList(this.getScriptResultsService().getResLogListForResponse(null, this.returnId, response.getSequenceNo(), response.getResOrderNo()));
			response.setIsLogPresent(response.getResLogList().size()>0?true:false);
			
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
	}

	
	public Boolean isEditable(String item) {
		if (isResultEditable) {
			if (item.equals(Common.QUESTION_TYPE_BITMAP) || item.equals(Common.QUESTION_TYPE_VIDEO)
					|| item.equals(Common.QUESTION_TYPE_CALCULATION) || item.equals(Common.QUESTION_TYPE_GEOMETRY)
					|| item.equals(Common.QUESTION_TYPE_GPSLOCATION) || item.equals(Common.QUESTION_TYPE_CSV_LINE_ITEM)
					|| item.equals(Common.QUESTION_TYPE_SIGNATURE)
					|| item.equals(Common.QUESTION_TYPE_VOICE_RECORDING)) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}
	
	public void setSelectedAnswer(ResultSet selectedAnswer) {
		this.selectedAnswer = selectedAnswer;
		
		//setting NA and OR toggle for each response
		//setting it to avoid session issue 
		if (this.selectedAnswer.getResponseType() != null && !(this.selectedAnswer.getResponseType().equalsIgnoreCase("OK"))) {
			this.selectedAnswer.setMandatorymodifierCheckBox(true);
		} else {
			this.selectedAnswer.setMandatorymodifierCheckBox(false);
		}
		
		this.selectedAnswer.setDeltaFreeText(this.selectedAnswer.getFreeText());
	
		if (selectedAnswer.getInputType().equalsIgnoreCase("M")) {
			this.mandatoryResponse = true;
			this.mandatoryResponseErrorMessage = "This is a mandatory question. Please enter a response.";
			
		} else if (selectedAnswer.getInputType().equalsIgnoreCase("MN")) {
			this.mandatoryResponse = true;
			this.mandatoryResponseErrorMessage = "This question must be answered or the N/A option checked.";
			
		} else if (selectedAnswer.getInputType().equalsIgnoreCase("MO")) {
			this.mandatoryResponse = true;
			this.mandatoryResponseErrorMessage = "This question must be answered or the over-ride option checked.";
			
		} else {
			//cannot set null values for Optional questions
			this.mandatoryResponse = true;
			this.mandatoryResponseErrorMessage = "You cannot add an empty response for this question.";
		}
		
		
		Item item;
		ResLog resLog;
		try {
			item = this.getScriptService().findByScriptIdWithAccocData(null,  this.selectedAnswer.getScriptId(), this.selectedAnswer.getSequenceNo());
			//39234 - Get the updated edited date from ResLog for selected row information
			resLog = this.getScriptResultsService().getLatestResLogItem(null,returnId, this.selectedAnswer.getSequenceNo(), this.selectedAnswer.getResOrderNo());
			this.selectedAnswer.setValFreeTextReqList(item.getValFreeTextReqList() == null ? null : new ArrayList<>(item.getValFreeTextReqList()));
			this.selectedAnswer.setGenNumValid(item.getGenNumValid());
			this.selectedAnswer.setUOMNumValids(item.getUomNumValidList() == null ? null : new ArrayList<>(item.getUomNumValidList()));
			
			//Formatted input char pos in ascending order
			List<FormatInputDef> formatInputDefList =(List<FormatInputDef>) (item.getFormatInputDefList().stream().collect(Collectors.toList()));
			Collections.sort(formatInputDefList, (o1, o2) -> o1.getId().getCharPos().compareTo(o2.getId().getCharPos()));
			this.selectedAnswer.setFormatInputDefList(item.getFormatInputDefList() == null ? null : formatInputDefList);
			
			this.selectedAnswer.setFormatInputDefMe(item.getFormatInputDefMe());
			this.selectedAnswer.setScriptRefItemList(item.getScriptRefItemList() == null ? null : new ArrayList<>(item.getScriptRefItemList()));
			
			//39234 - get the updated date from ResLog. Set it to display if present
			this.editDate = resLog!=null?resLog.getUpdateDate():null;
			this.editTime = resLog!=null?resLog.getUpdateTime():null;
			

			
			if(item.getValidation() != null) {
				//unanswered question so we need to go get the validation type stuff.
				String refResponse = null;
				if(item.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK_RULE) || item.getItemType().equals(Common.QUESTION_TYPE_SINGLE_PICK_RULE)) {
					//FDE058 - we need to update the validation list if we are dealing with a rule based validation list as it may change based on previous responses

					if(item.getItemRespRulesRef() != null) {
						Integer refSeq = item.getItemRespRulesRef().getRefSeqNo();
						
						for(int i=answers.indexOf(selectedAnswer) - 1 ; i >= 0; i--) {
							ResultSet res = answers.get(i);
							if(res.getSequenceNo().equals(refSeq)) {
								refResponse = res.getResponse();
								break;
							}
						}
					}else {
						//Get the previous question that isnt a heading
						for(int i=answers.indexOf(selectedAnswer) - 1 ; i >= 0; i--) {
							ResultSet res = answers.get(i);
							if(!res.isHeading()) {
								refResponse = res.getResponse();
								break;
							}
						}
					}
				}

				item = getScriptService().populateScriptItemValidationAndColourData(null, item, refResponse);

				this.selectedAnswer.setValidationPropertyList(item.getValidationPropertyList());
				this.selectedAnswer.generateColorMap(item);
				
			}
			
			
			if(item != null) {
				if(item.getUomCatId() != null && item.getUomCatId() > 0) {
					item.setUnitOfMeasureList(this.getScriptService().findUOMByUOMCatID(null, item.getUomCatId()));
					this.selectedAnswer.setUnitOfMeasureList(item.getUnitOfMeasureList());
				}
			}
			this.selectedAnswer.setEditPanelResponse();
		//	this.selectedAnswer.setValidationLimits();
			this.selectedAnswer.setDeltaUom(this.selectedAnswer.getUom());
			
			if(this.selectedAnswer instanceof NumericResultSet) {
				if(this.selectedAnswer.getGenNumValid()!= null) {
					String temp = getParamValueForResponse(this.selectedAnswer, this.answers, this.selectedAnswer.getGenNumValid().getLowerLimit(), false);					
					((NumericResultSet) this.selectedAnswer).setInputLowerLimit(temp!= null ? new BigDecimal(temp) : null);
					
					 temp = getParamValueForResponse(this.selectedAnswer, this.answers, this.selectedAnswer.getGenNumValid().getUpperLimit(), false);
					((NumericResultSet) this.selectedAnswer).setInputUpperLimit(temp!= null ? new BigDecimal(temp) : null);
					
					 temp = getParamValueForResponse(this.selectedAnswer, this.answers, this.selectedAnswer.getGenNumValid().getLowerWarning(), false);
					((NumericResultSet) this.selectedAnswer).setInputLowerWarn(temp!= null ? new BigDecimal(temp) : null);
					
					 temp = getParamValueForResponse(this.selectedAnswer, this.answers, this.selectedAnswer.getGenNumValid().getUpperWarning(), false);
					((NumericResultSet) this.selectedAnswer).setInputUpperWarn(temp!= null ? new BigDecimal(temp) : null);
				}
				
				this.setUomValidationLimits();
				
			}else if(this.selectedAnswer instanceof DateResultSet) {
				if (this.selectedAnswer.getGenNumValid() != null) {
					String temp = getParamValueForResponse(this.selectedAnswer, this.answers, this.selectedAnswer.getGenNumValid().getLowerLimit(), false);	
					((DateResultSet) this.selectedAnswer).setDateLowerLimit(temp!= null && !temp.toUpperCase().contains("TODAY") ? DateUtil.stringToDate(temp): null);
					
					 temp = getParamValueForResponse(this.selectedAnswer, this.answers, this.selectedAnswer.getGenNumValid().getUpperLimit(), false);
					((DateResultSet) this.selectedAnswer).setDateUpperLimit(temp!= null && !temp.toUpperCase().contains("TODAY") ? DateUtil.stringToDate(temp): null);
					
					 temp = getParamValueForResponse(this.selectedAnswer, this.answers, this.selectedAnswer.getGenNumValid().getLowerWarning(), false);
					((DateResultSet) this.selectedAnswer).setDateLowerWarn(temp!= null && !temp.toUpperCase().contains("TODAY") ? DateUtil.stringToDate(temp): null);
					
					 temp = getParamValueForResponse(this.selectedAnswer, this.answers, this.selectedAnswer.getGenNumValid().getUpperWarning(), false);
					((DateResultSet) this.selectedAnswer).setDateUpperWarn(temp!= null && !temp.toUpperCase().contains("TODAY") ? DateUtil.stringToDate(temp): null);
				}
			}

		
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
		
	}
	
	public void setUomValidationLimits() {
		if(this.selectedAnswer.getUOMNumValids()!=null) {
			for(UomNumValid uomValid :this.selectedAnswer.getUOMNumValids()) {
				if(uomValid.getId().getUomType().equalsIgnoreCase(this.selectedAnswer.getDeltaUom())) {
					
					String temp =getParamValueForResponse(this.selectedAnswer, this.answers, uomValid.getLowerLimit(), false);	
					((NumericResultSet) this.selectedAnswer).setInputLowerLimit(temp!= null ? new BigDecimal(temp) : null);
					
					temp = getParamValueForResponse(this.selectedAnswer, this.answers, uomValid.getUpperLimit(), false);	
					((NumericResultSet) this.selectedAnswer).setInputUpperLimit(temp!= null ? new BigDecimal(temp) : null);
					
					temp = getParamValueForResponse(this.selectedAnswer, this.answers, uomValid.getLowerWarning(), false);	
					((NumericResultSet) this.selectedAnswer).setInputLowerWarn(temp!= null ? new BigDecimal(temp) : null);
					
					temp = getParamValueForResponse(this.selectedAnswer, this.answers, uomValid.getUpperWarning(),false);
					((NumericResultSet) this.selectedAnswer).setInputUpperWarn(temp!= null ? new BigDecimal(temp) : null);
					break;
				}
			}
		}
	}
	
	private String getParamValueForResponse(ResultSet response, List<ResultSet> responses, String param,
			Boolean formatResponse) {
		
		if (param != null) {

			if (param.toUpperCase().startsWith(Common.EQUALS_SEQUENCE_FLAG)) {

				Integer seqNo = getSequenceNumberFromString(param, 0, Common.EQUALS_SEQUENCE_FLAG);
				if(seqNo != null)
					return getAnswersForSequenceNumberFromReponse(response, seqNo, responses, formatResponse);

			} else if (param.startsWith(Common.EQUALS_DOLLAR_FLAG)) {

				String paramValue = getVariableValue(param, 0);
				String value = getParamValueFromCarryThrough(this.carryThroughMap, paramValue);
				
				if (value == null && paramValue.startsWith("ASSET_")) {
					value = getParamValueFromEquipmentData(this.equipmentInformation.getEquipmentAttribList(), paramValue);
				}
				return value;

			}
			
		}

		return param;
	}
	
	//FDP1406
	public void loadNextStatuses() throws FRInstanceException {
		//get available statuses to show in dropdown
		ScriptVersions version = this.getScriptService().findScriptVersion(null, this.getScript().getScriptId());

		List<NextStatusDef> nextDef = this.getScriptResultsService().getNextStatusDefByScriptCodeID(null, version.getScriptCodeId());
		
		if (nextDef == null || nextDef.isEmpty())
			return;
		
		nextStatuses = new LinkedHashMap<String, Object>();

		nextStatuses.put(this.getScript().getResultStatus(), this.getScript().getResultStatus());
		
		nextStatus = this.getScript().getResultStatus();
		
		for (NextStatusDef def : nextDef) {

			if (def.getId().getStatusValue().equalsIgnoreCase(this.getScript().getResultStatus())) {

				nextStatuses.put(def.getNextStatusValue(),def.getNextStatusValue());
			}
		}
		
	}
	
	//FDP1406
	public void updateNextStatus() {
		
		if (nextStatus == null || nextStatus.equals("")) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_details_failed_to_update_status"));
			return;
		}
		
		try {
			
			this.getScriptResultsService().updateResultStatus(null, returnId, nextStatus, this.getUsername());
			
			this.setScript(this.getScriptResultsService().getReturnedScript(null, returnId));
			
			for(SearchResult res : searchResults.getResults()) {
				if(res.getReturnedScript().getId() == this.getScript().getId()) {
					res.setReturnedScript(this.getScript());
					res.setResultStatus(nextStatus);
					break;
				}
			}

			//39979 - update the result status history after saving the new status.
			this.setResultHistoryStatusLog(this.getScriptResultsService().getScriptResultStatusHistory(null, this.script.getId(), true));
		} catch (Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_details_failed_to_update_status"));
		}
	}
	
	protected boolean isCurrentStatusSystemStatus() throws FRInstanceException {
		
		//if not error status then check if status is system status
		//get available statuses to show in dropdown
		ScriptVersions version = this.getScriptService().findScriptVersion(null, this.getScript().getScriptId());
	
		List<ScriptStatusDef> scriptStatusDef =  this.getScriptResultsService().getScriptStatusDefByScriptCodeID(null, version.getScriptCodeId());
		
		if (scriptStatusDef == null || scriptStatusDef.isEmpty())
			return false;
		
		// Check to see if status is a system status
		for (ScriptStatusDef ssd : scriptStatusDef) {

			if (ssd.getStatusValue().equals(this.getScript().getResultStatus())) {

				if (Integer.toString(ssd.getSysStatusFlag()).equals("1")) {
					return true;
				}

				break;
			}
		}
		
		return false;
	}
	/**
	 * FDP1406
	 * @return
	 * @throws FRInstanceException
	 */
	public boolean isResultStatusEditable() throws FRInstanceException {
		
		
		String[] closeStatuses = this.serviceManager.getWorkOrderController().getWorkStatuses().getResultCloseApprovalStatuses();
		
		if(closeStatuses != null) {
			List<String> cStatusList = new ArrayList<String>(Arrays.asList(closeStatuses));
			
			if(cStatusList.contains(this.getScript().getResultStatus())) {
				return false;
			}
		}
		
		SystemUsers sysUser = getUserService().getSystemUser(null, getUsername());
		
		String fdmCode = sysUser.getFdmGroupCode();
		
		//check if has update status 
		
		if(fdmCode != null && fdmCode.length() > 0 && this.getUserService().hasResultStatusUpdateGroupFunction(null, fdmCode)) {
			
			this.loadNextStatuses();
			
			if(this.getNextStatuses() != null && this.getNextStatuses().size() > 1 && !this.isCurrentStatusSystemStatus()) {
				return true;
			}
		}
		
		return false;
	}


	
	
	public void fileUploadListener(FileUploadEvent e) throws IOException, SerialException, SQLException {
		// Get uploaded file from the FileUploadEvent
		byte[] bytes = IOUtils.toByteArray(e.getFile().getInputStream());
		
		PhotographResultSet data = (PhotographResultSet) this.selectedAnswer;
		data.updateNewPhotgraph(new SerialBlob(bytes));
	}

	
	
	/**
	 * save method for bitmaps, using the fdm file name convention
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	public void saveBitmap() throws IOException, SQLException{
		try {
			ContextHelper.download(ContextHelper.MimeType.IMAGE_BMP, (getSelectedAnswer().getBlobFileName() == null ? Properties.get("fdm_details_save_image_prefix")+getSelectedAnswer().getReturnId()+"."+getSelectedAnswer().getResOrderNo()+Properties.get("fdm_details_save_image_bitmap_extension") : getSelectedAnswer().getBlobFileName()), getSelectedAnswer().getBlob());
		} catch (Exception e) {
			e.toString();
		}
	}
	
	/**
	 * save method for photographs, using the fdm file name convention
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	public void savePhotograph() throws IOException, SQLException{
		try {
			ContextHelper.download(ContextHelper.MimeType.IMAGE_JPEG, (getSelectedAnswer().getBlob() == null ? Properties.get("fdm_details_save_image_prefix")+getSelectedAnswer().getReturnId()+"."+getSelectedAnswer().getResOrderNo()+Properties.get("fdm_details_save_image_jpeg_extension") : getSelectedAnswer().getBlobFileName()), getSelectedAnswer().getBlob());
		} catch (Exception e) {
			e.toString();
		}
	}
	
	public void saveVideoAudio() throws IOException, SQLException{
		try {
			ContextHelper.download(ContextHelper.MimeType.VIDEO_MP4, (getSelectedAnswer().getBlobFileName() == null ? Properties.get("fdm_details_save_image_prefix")+getSelectedAnswer().getReturnId()+"."+getSelectedAnswer().getResOrderNo()+Properties.get("fdm_details_save_image_jpeg_extension") : getSelectedAnswer().getBlobFileName()), getSelectedAnswer().getBlob());
		} catch (Exception e) {
			e.toString();
		}
	}
	
	//37392 - Delete the selected result note
	public void deleteResultNote() throws FRInstanceException {
		//remove from question level
		if(rowLevelDetails != null) {
			rowLevelDetails.getResultNotes().remove(selectedResultNotes);
			rowLevelDetails = null;
		}
		else {
			//remove from script level
			this.resultNotes.remove(selectedResultNotes);
		}
		
		this.getScriptResultsService().deleteResultNote(null, selectedResultNotes);
		//call initialize to refresh the page to reflect the updated information
		
		//clear the cache information after update
		addResNotes = null;
		
	}
	
	//37392 - add or update the new/existing result note
	public void addUpdateResultNote() throws FRInstanceException {

			//check if the operation is add or update
			if(addResultNotesFlag) {
				//get the row id if it is a question level result note
				if(rowLevelDetails!=null) {
					//create the result note selected with relevant row details
					selectedResultNotes = new ResultNotes(this.returnId,rowLevelDetails.getSequenceNo(),rowLevelDetails.getResOrderNo(),addResNotes,this.getUsername(),Common.generateFieldreachDBDate(),Common.generateFieldreachDBTime());
					//clear the cache information after update
					if(rowLevelDetails.getResultNotes() == null) {
						rowLevelDetails.setResultNotes(new ArrayList<ResultNotes>());
					}
					rowLevelDetails.getResultNotes().add(0, selectedResultNotes);
					rowLevelDetails = null;
				}
				else {
					//create the result note for the script level questions
					selectedResultNotes = new ResultNotes(this.returnId,0,0,addResNotes,this.getUsername(),Common.generateFieldreachDBDate(),Common.generateFieldreachDBTime());
					if(this.resultNotes == null) {
						this.resultNotes = new ArrayList<ResultNotes>();
					}
					this.resultNotes.add(0, selectedResultNotes);
				}
			}
			this.getScriptResultsService().createResultNote(null, selectedResultNotes);
			addResNotes = null;
			
		}
	
	public void saveResponse(boolean skipValidation) {
		//for saving each response
		//TO DO
		if (!skipValidation) {
			if (!this.selectedAnswer.isResponseValid()) {
				return;
			}
		}
		
		try {
							
			UpdateScriptResult usr = new UpdateScriptResult();
			usr.setUSERCODE(this.getUsername());
			com.amtsybex.fieldreach.services.messages.types.Item usrItem = new com.amtsybex.fieldreach.services.messages.types.Item();
			usrItem.setDATE(String.valueOf(Common.generateFieldreachDBDate())); //only used for add but can be added anyway, just wont be used
			usrItem.setTIME(String.valueOf(Common.generateFieldreachDBTime())); //only used for add but can be added anyway, just wont be used

			//free text entered by user, this needs to be revertable if the user hits cancel etc
			usrItem.setFREETEXT(this.selectedAnswer.getDeltaFreeText());
			//previous response to question (photograph maybe others). some questions wont set this, test each in FDM first to see if it saves this
			//on clicking edit you may need to set a currentResponse inside the resultSet object to be used here or to revert changes if cancel clicked etc.
			usrItem.setPREV(selectedAnswer.getResponse()); 
			
			
			
			usrItem.setResOrderNo(selectedAnswer.getResOrderNo() == null ? 0 : selectedAnswer.getResOrderNo());

			com.amtsybex.fieldreach.services.messages.types.Response response = new com.amtsybex.fieldreach.services.messages.types.Response();
			
			String responseValue="";
			if(this.selectedAnswer.isMandatorymodifierCheckBox()) {
				response.setType(this.selectedAnswer.getInputType().equalsIgnoreCase("MO") ? "O/R" : "N/A");
				response.setValue(responseValue);
			}else {
			//get value from each result set only if it is of response type ok
				responseValue = this.selectedAnswer.generateSaveResponse();
				response.setType("OK");
				response.setValue(responseValue!= null ? responseValue : "");
			}
			 //responsetype should be OK N/A or O/R depending on the edit screen

			//need to format response for multipick and tasklist, response for photograph should be selectedAnswer.getBase64Photograph() but without the mime type at the front, maybe create a new function to get just the base64 encoded blob
			//you should create a function for this inside the Resultset object, in future we will subclass the resultset object on a per questiontype basis and override abstract functions for this sort of thing
			
			
		
			usrItem.setRESPONSE(response);
			usrItem.setUOM(this.selectedAnswer.getDeltaUom());
			
			usrItem.setSeqNo(selectedAnswer.getSequenceNo());

			usr.setITEM(usrItem);

			this.scriptController.updateResultResponse(null, returnId, selectedAnswer.getResOrderNo(), usr);
			
			//Setting values in Current Result Object
			this.selectedAnswer.setResponseType(response.getType());
			this.selectedAnswer.setAnswered(true);
			this.selectedAnswer.setUom(this.selectedAnswer.getDeltaUom());
			if(this.selectedAnswer.getResponseType().equalsIgnoreCase("OK")) {
				this.selectedAnswer.setCurrentResponse();
			}else {
				this.selectedAnswer.setResponse(this.selectedAnswer.getResponseType());
				//these objects will deleted in DB if it is NA or OR
				this.selectedAnswer.setTaskListItems(null);
				this.selectedAnswer.setMultiPickListItems(null);
				this.selectedAnswer.setExtendedResponse(null);
			}
			
			//setting free text back to main page
			this.selectedAnswer.setFreeText(this.selectedAnswer.getDeltaFreeText());
	           
	        this.selectedAnswer.setResLogList(this.getScriptResultsService().getResLogListForResponse(null, this.returnId, this.selectedAnswer.getSequenceNo(), this.selectedAnswer.getResOrderNo()));
	        this.selectedAnswer.setIsLogPresent(this.selectedAnswer.getResLogList().size()>0?true:false);
			//40317 - toggle answered/unanswered, reevaluate if 
			for(ResultSet res : this.answers) {
				if(res.isHeading()) {
					res.setHasAnsweredQuestions(this.shouldShowHeading(res, this.answers));
				}
			}
			
		} catch (FRInstanceException | ScriptResultNotFoundException | ResponseUpdateNotSupportedException
				| ScriptItemNotFoundException e) {
			// TODO Auto-generated catch block - show error messages as MessageHelper.setGlobalErrorMessage
		}
		
		
	}
	
	
	/**
	 * FDE059 - MC - extract script report to doc or pdf
	 * @param pdf
	 * @return
	 */
	public StreamedContent extractScriptResult(boolean pdf) {
		
		try {
			
			String templateLocation = serviceManager.getPortalPropertyUtil().props().getResult().getTemplatelocation();
			final String templatePrefix = "fr_template.docx";
			final String scriptCodeTemplatePrefix = "fr_" + this.getScript().getScriptCode().toLowerCase() + "_template";
			
			File directory = new File(templateLocation);
			
			XWPFDocument overrideTemplate = null;
			
			//check template directory for override templates
			if(directory.exists() && directory.isDirectory()) {
				
				//check using filter on script code
				FilenameFilter templateFilter = new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {

						String lowercaseName = name.toLowerCase();
						
						if (lowercaseName.startsWith(scriptCodeTemplatePrefix)
								&& lowercaseName.endsWith(".docx")) {

							return true;

						} else {

							return false;
						}
					}
				};
			
				File[] files = directory.listFiles(templateFilter);
				
				//if no override for script, check for overrride of template
				if(files == null || files.length == 0) {
					
					templateFilter = new FilenameFilter() {

						@Override
						public boolean accept(File dir, String name) {

							String lowercaseName = name.toLowerCase();
							
							if (lowercaseName.equals(templatePrefix)) {

								return true;

							} else {

								return false;
							}
						}
					};
					
					files = directory.listFiles(templateFilter);
				}
				
				
				//if script code or template override
				if(files != null && files.length != 0) {
					overrideTemplate = new XWPFDocument(new FileInputStream(files[0]));
				}else if(overrideTemplate == null) {
					
					try {
						Resource appresource = new ClassPathResource("/FR_TEMPLATE.docx");
						overrideTemplate = new XWPFDocument(appresource.getInputStream());
					}catch(Exception e) {
						//do nothing, just use extract engines template
					}
		
				}
				
			}
			
			ExtractEngine fieldreachExtractEngine = (ExtractEngine) WebApplicationContextUtils.getRequiredWebApplicationContext((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getBean("fieldreachExtractEngine");
			boolean extractPII = ManagedBeanHelper.findBean("fdmenv", Environment.class).isViewPII();
			byte[] bytes = fieldreachExtractEngine.getScriptResultAsDocOrPDF(null, script.getId(), showAllQuestions, pdf, overrideTemplate, extractPII);

			DefaultStreamedContent db = null;
			
			if(pdf) {
				
				db = DefaultStreamedContent.builder()
						.contentType("application/pdf")
						.name(this.getScript().getScriptCode() + "." + this.getScript().getId() + ".pdf")
						.stream(() -> new ByteArrayInputStream(bytes)).build();
				
				systemActivityLogger.recordActivityLog(null, ACTIVITYTYPE.PRINT_RESULT_PDF, getUsername(), script.getId().toString(), "");
				
			}else {
				
				db = DefaultStreamedContent.builder()
						.contentType("application/msword")
						.name(this.getScript().getScriptCode() + "." + this.getScript().getId() + ".doc")
						.stream(() -> new ByteArrayInputStream(bytes)).build();
				
				systemActivityLogger.recordActivityLog(null, ACTIVITYTYPE.PRINT_RESULT_WORD, getUsername(), script.getId().toString(), "");
			}
			
			
			StreamedContent file = db;
			
			return file;
			
		} catch (Exception e1) {
			e1.printStackTrace();
			_logger.error("Error generating result download " + e1);
			MessageHelper.setGlobalErrorMessage("Error generating result download");
		}
		
		
		return null;
	}

	public String getRegExp() {
		return regExp;
	}

	public void setRegExp(String regExp) {
		this.regExp = regExp;
	}

	public boolean isMandatoryResponse() {
		return mandatoryResponse;
	}

	public void setMandatoryResponse(boolean mandatoryResponse) {
		this.mandatoryResponse = mandatoryResponse;
	}

	public String getMandatoryResponseErrorMessage() {
		return mandatoryResponseErrorMessage;
	}

	public void setMandatoryResponseErrorMessage(String mandatoryResponseErrorMessage) {
		this.mandatoryResponseErrorMessage = mandatoryResponseErrorMessage;
	}

	public boolean isResultEditable() {
		return isResultEditable;
	}

	public void setIsResultEditable(boolean isResultEditable) {
		this.isResultEditable = isResultEditable;
	}
	

	//FDP1406
	public String getNextStatus() {
		return nextStatus;
	}

	//FDP1406
	public void setNextStatus(String nextStatus) {
		this.nextStatus = nextStatus;
	}

	//FDP1406
	public Map<String, Object> getNextStatuses() {
		return nextStatuses;
	}
	
	//FDP1406
	public void setNextStatuses(Map<String, Object> nextStatuses) {
		this.nextStatuses = nextStatuses;
	}	
	
	public ResultSet getSelectedAnswer() {
		if (selectedAnswer == null)
			selectedAnswer = new ResultSet();
		return selectedAnswer;
	}

	public List<ResultStatusLog> getResultHistoryStatusLog() {
		return resultHistoryStatusLog;
	}

	public void setResultHistoryStatusLog(List<ResultStatusLog> resultHistoryStatusLog) {
		this.resultHistoryStatusLog = resultHistoryStatusLog;
	}

	public List<ResultNotes> getNotesData() {
		return notesData;
	}

	public void setNotesData(List<ResultNotes> notesData) {
		this.notesData = notesData;
	}
	

	//FDE058 - Support for "Notes" associated to script results
	public String getAddResNotes() {
		return addResNotes;
	}

	public void setAddResNotes(String addResNotes) {
		this.addResNotes = addResNotes;
	}
	
	public List<ResultNotes> getResultNotes() {
		return resultNotes;
	}

	public void setResultNotes(List<ResultNotes> resultNotes) {
		this.resultNotes = resultNotes;
	}
	
	public ResultNotes getSelectedResultNotes() {
		return selectedResultNotes;
	}

	public void setSelectedResultNotes(ResultNotes selectedResultNotes) {
		this.selectedResultNotes = selectedResultNotes;
	}

	public boolean isAddResultNotesFlag() {
		return addResultNotesFlag;
	}

	public void setAddResultNotesFlag(boolean addEditResultNotesFlag) {
		this.addResultNotesFlag = addEditResultNotesFlag;
	}
	
	public ResultSet getRowLevelDetails() {
		return rowLevelDetails;
	}

	public void setRowLevelDetails(ResultSet rowLevelDetails) {
		this.rowLevelDetails = rowLevelDetails;
	}
	
	public Integer getResultNotesRowNo() {
		return resultNotesRowNo;
	}

	public void setResultNotesRowNo(Integer resultNotesRowNo) {
		this.resultNotesRowNo = resultNotesRowNo;
	}
	
	public Integer getSelectedRowCount() {
		return selectedRowCount;
	}

	public void setSelectedRowCount(Integer selectedRowCount) {
		this.selectedRowCount = selectedRowCount;
	}
	
	public boolean isAddResponseFlag() {
		return addResponseFlag;
	}

	public void setAddResponseFlag(boolean addResponseFlag) {
		this.addResponseFlag = addResponseFlag;
	}

	public Integer getEditDate() {
		return editDate;
	}

	public void setEditDate(Integer editDate) {
		this.editDate = editDate;
	}

	public Integer getEditTime() {
		return editTime;
	}

	public void setEditTime(Integer editTime) {
		this.editTime = editTime;
	}
	
	public List<ReturnedScripts> getAssoScriptResults() {
		return assoScriptResults;
	}
	public void setAssoScriptResults(List<ReturnedScripts> assoScriptResults) {
		this.assoScriptResults = assoScriptResults;
	}

	public Boolean getShowEditLog() {
		return showEditLog;
	}

	public void setShowEditLog(Boolean showEditLog) {
		this.showEditLog = showEditLog;
	}

}

