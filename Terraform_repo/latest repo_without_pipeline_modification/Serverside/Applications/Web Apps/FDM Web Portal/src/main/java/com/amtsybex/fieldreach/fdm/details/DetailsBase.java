package com.amtsybex.fieldreach.fdm.details;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.Carrythrough;
import com.amtsybex.fieldreach.backend.model.CarrythroughRes;
import com.amtsybex.fieldreach.backend.model.EquipmentAttrib;
import com.amtsybex.fieldreach.backend.model.Item;
import com.amtsybex.fieldreach.backend.model.RefItem;
import com.amtsybex.fieldreach.backend.model.RepeatGroups;
import com.amtsybex.fieldreach.backend.model.ResLog;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.ScriptResults;
import com.amtsybex.fieldreach.fdm.Environment;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.SystemActivityLogger;
import com.amtsybex.fieldreach.fdm.SystemActivityLogger.ACTIVITYTYPE;
import com.amtsybex.fieldreach.fdm.equipment.EquipmentInformation;
import com.amtsybex.fieldreach.fdm.util.DateUtil;
import com.amtsybex.fieldreach.fdm.web.jsf.util.ManagedBeanHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.utils.impl.Common;

/**
 * FDE060 - add quick view so base class basic result display information
 * @author CroninM
 *
 */
public abstract class DetailsBase extends PageCodebase {

	protected static final long serialVersionUID = -4985351521073682851L;
	
	@Inject
	transient SystemActivityLogger systemActivityLogger;
	
	@Inject
	protected transient EquipmentInformation equipmentInformation;
	
	protected ReturnedScripts script;
	
	protected List<ResultSet> answers;
	
	protected Integer returnId;
	protected String scriptDescription;	
	protected String completeTime;
	protected Date completeDate;
	
	protected Map<String,String> carryThroughMap;
	protected List<CarrythroughRes> carryThrough;
	//40343 - Carrythrough Additional Information
	protected List<Carrythrough> carryThroughDef;
	protected List<CarrythroughRes> additionalInfo;
	protected List<ResLog> resLogList;

	protected ResultSet highlightedAnswer;

	protected String selectedQuestion;
	protected String selectedResponse;
	protected String selectedFreeText;
	protected boolean selectedImage;
	
	protected boolean videoTag;
	
	//40317 - toggle answered/unanswered
	protected boolean showAllQuestions;
	
	private boolean selectedOnApprovalDetail;

	public abstract void setAdditionalResponseInfo(ResultSet resposne);
	public abstract void initialiseAdditionalInformation();
	
	/**
	 * init method for building up the details screen based on the selected result
	 * 
	 * @param script	- script the user has selected on the search results screen
	 * @throws FRInstanceException 
	 */
	public void initialise(ReturnedScripts script) {
		
		try {
			
			this.script = script;
			setDetailsHeader(script);
			setReturnId(script.getId());
			
			this.setCarryThrough(getScriptResultsService().getResultCarrythrough(null, script.getId()));
			
			this.setCarryThroughDef(getScriptService().getScriptCarrythough(null, script.getScript().getId()));
			
			setAdditionalInfo(this.filterAdditionalInfo());
			
			carryThroughMap = new HashMap<String,String>();
			if (carryThrough != null) {
				for (CarrythroughRes ct : carryThrough) {
					this.carryThroughMap.put(ct.getId().getFieldName(), ct.getFieldValue());
				}
			}
			
			this.initialiseAdditionalInformation();
			
			setAnswers(	buildResponses(	getScriptService().findScriptItems(null, script.getScriptId()), 
										getScriptResultsService().getScriptResults(null, script.getId())));
			
			systemActivityLogger.recordActivityLog(null, ACTIVITYTYPE.RESULT_SEARCH_DETAIL, getUsername(), script.getId().toString(), "");
			
		}catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}

	}
	
	/**
	 * some data we need isn't stored on the script object, so we fetch additional info here
	 * 
	 * @param script	- script we need to retrieve information from
	 * @throws FRInstanceException 
	 */
	public void setDetailsHeader(ReturnedScripts script) throws FRInstanceException{
		setScriptDescription(getScriptService().findScriptByScriptCode(null, script.getScriptCode()).getScriptDesc());
		
		// we need to display dates and times in a more readable format than the format they are stored
		completeDate = DateUtil.intToDate(script.getCompleteDate());
		completeTime = DateUtil.parseTime(String.valueOf(script.getCompleteTime()));
	} 
	
	/**
	 * method responsible for building up the responses to display
	 * 
	 * @param scriptItems	- a map of script items
	 * @param scriptResults	- a list of responses for the script items
	 * 
	 * @return
	 * @throws FRInstanceException 
	 */
	public List<ResultSet> buildResponses(Map<Integer, Item> scriptItems, List<ScriptResults> scriptResults) throws FRInstanceException{
		List<ResultSet> responses = new ArrayList<ResultSet>();
		Integer otCol = this.getSystemParameterService().getSystemParams(null).getOtCol();
		
		// sort the script items
		scriptItems = getSortedScriptItemsMap(scriptItems);
				
		// get a list of repeated groups that can occur for this script
		List<RepeatGroups> repeatedGroups = null;
		if (script != null){
			repeatedGroups = getScriptService().findRepeatGroupsByScriptId(null, script.getScriptId());
		}
		
		Iterator<Item> scriptItemsIterator = scriptItems.values().iterator();
		List<Item> processedItems = new ArrayList<Item>();
		// map for storing the occurrences of repeated groups
		Map<Integer, Boolean> repeatedItems = new HashMap<Integer, Boolean>();

		boolean headerRepeated = false;
		
		RepeatGroups currentRepeatedGroup = null;
		// iterate over the list of script items
		while (scriptItemsIterator.hasNext()){
			Item scriptItem = scriptItemsIterator.next();
				
			if(currentRepeatedGroup != null && scriptItem.getId().getSequenceNumberInt() < currentRepeatedGroup.getToSeqNo()) {
				continue;
			}

			//EWP596 MC 10/05/2018. Do not check for Script Items with Null Item type
			if (scriptItem.getItemType() != null) {
			
				/*if (!scriptItem.getItemType().equals(Common.QUESTION_TYPE_HEADING)
						&& !scriptItem.getItemType().equals(Common.QUESTION_TYPE_BLOCK_MARKER)) {
					//FDE058 - populate the script item with additional information needed for edit
					scriptItem = getScriptService().populateScriptItemAssociatedData(null, scriptItem);
				}*/
				
				// only headings can have repeated group info associated with them
				if (scriptItem.getItemType().equals(Common.QUESTION_TYPE_HEADING)){
				
					// var for storing the number of times the current script item has been repeated in the results
					int occurrences = 0;
				
					// if there is a possibility of repeated groups for this script
					if (repeatedGroups !=null){
					
						// loop over the possibly repeated groups
						for (RepeatGroups repeatedGroup : repeatedGroups){
						
							// if the current script item has been repeated
							if (repeatedGroup.getId().getFromSeqNo().equals(scriptItem.getId().getSequenceNumber())){
							
								currentRepeatedGroup = repeatedGroup;
								
								// find out how many times it has been repeated by checking for instances of the "to" id in the results
								Integer lastItemFound = 0;
								
								boolean startedGroup = false;
								
								for (ScriptResults result : scriptResults){
									
									if(result.getId().getSequenceNo() > repeatedGroup.getToSeqNoInt()) {
										break;
									}else if(!startedGroup && result.getId().getSequenceNo() >= repeatedGroup.getId().getFromSeqNoInt()) {
										startedGroup = true;
										lastItemFound = result.getId().getSequenceNo();
										occurrences++;
									}
									if ((result.getId().getSequenceNo() < repeatedGroup.getToSeqNo() && lastItemFound > result.getId().getSequenceNoInt()) || result.getId().getSequenceNo().equals(repeatedGroup.getToSeqNo())){
										startedGroup = false;
									}
									lastItemFound = result.getId().getSequenceNo();
								}
							
								break;
							}
						}
					}
				
					// the header was repeated if there was 1 or more occurrences
					headerRepeated = (occurrences > 0);
				
					if (headerRepeated){
						repeatedItems.put(scriptItem.getId().getSequenceNumber(), true);
						int fromSeq = currentRepeatedGroup.getId().getFromSeqNoInt();
						int toSeq = currentRepeatedGroup.getToSeqNoInt();
					
						// for the number of times a certain header has been repeated
						for (int x = 0; x < occurrences; x++){
							// add the child items of the header to the list of processed items
							for (int i = fromSeq; i <= toSeq; i++){
								processedItems.add(scriptItems.get(i));
							}
						}
					}else{
						processedItems.add(scriptItem);
					}
				} else if (currentRepeatedGroup != null && scriptItem.getId().getSequenceNumberInt() > currentRepeatedGroup.getToSeqNo()){
					currentRepeatedGroup = null;
					headerRepeated = false;
					processedItems.add(scriptItem);
				} else if (!headerRepeated){
					processedItems.add(scriptItem);
				}
			}//EWP596 End If
		} // end iterating over script items
		
		/*
		 * at this stage we have a list of processed items, which now reflects the number of times repeated groups have
		 * been repeated
		 * 
		 * so if the initial list of script items looks like:
		 * 
		 * 1) Header A
		 * 2) Header B
		 * 3) Header C
		 * 
		 * and after parsing the responses, we have determined that had "Header B" has been repeated twice, our processed
		 * list of items now looks like:
		 * 
		 * 1) Header A
		 * 2) Header B
		 * 3) Header B <- first repeat
		 * 4) Header B <- second repeat
		 * 5) Header C
		 *  
		 *  this means we have a 1-to-1 correspondence between the list of items (repeated headers *and* their children) and the list of responses
		 */
		
		// index is used as part of p:dataTable
		int index = 0;
		
		Map<Integer, Integer> occurrences = new HashMap<Integer, Integer>();
		
		int seqDiff = 0;
		// loop over our compiled script items, and find the appropriate responses
		for (Item item : processedItems){
			
			if (item.getItemType().equals(Common.QUESTION_TYPE_HEADING)){
				
				ResultSet heading = null;
				
				// check if this item has been repeated
				boolean repeated = (repeatedItems.get(item.getId().getSequenceNumber()) != null && repeatedItems.get(item.getId().getSequenceNumber()) != false);
				
				if (repeated){
					
					// get the current occurrence of the repeated group
					Integer occurrence = occurrences.get(item.getId().getSequenceNumber());
					
					// if this is the first time we have encountered the group, we want to have a suffix of (1)
					if (occurrence == null){
						occurrence = 1;
					}
					else{
						occurrence++;
					}
					
					// update the map of occurrences, so the next occurrence will have a suffix of (2), and so on...
					occurrences.put(item.getId().getSequenceNumber(), occurrence);
					
					// set the heading of the item to have a suffix of the current occurrence in parentheses
					heading = new ResultSet(index++, item.getId().getScriptId(), item.getItemText()+" ("+occurrence+")", item.getId().getSequenceNumberInt() + seqDiff , item.getId().getSequenceNumberInt(), item.getItemType(), item.getInputType());
				}
				else{
					// if a heading hasn't been repeated, just use the heading text without a suffix
					heading = new ResultSet(index++, item.getId().getScriptId(), item.getItemText(), item.getId().getSequenceNumberInt() + seqDiff , item.getId().getSequenceNumberInt(), item.getItemType(), item.getInputType());
				}
				
				heading.setHeading(true);
				heading.setiLevel(item.getiLevel());
				heading.setScript(evaluateDynamicQuestionText(heading,responses));

				// add this heading to the list of responses
				responses.add(heading);
			}
			else if (item.getItemType().equals(Common.QUESTION_TYPE_BLOCK_MARKER)){
				// ignore end items for the time being
			}
			else{
				// for anything that isn't a heading or an end item
				boolean found = false;
				
				for (ScriptResults result : scriptResults){
					
					if(result.getId().getSequenceNo() > item.getId().getSequenceNumberInt()) {
						//we have gone past the sequence number so break as there is no point in going further.
						break;
					}
					// find the matching response for the current item
					if (result.getId().getSequenceNo().equals(item.getId().getSequenceNumber())){
						ResultSet response;
						
						// sequence order number of 0 means item text/type of null, so we don't display it
						if (result.getId().getSequenceNo() == 0){
							continue;
						}
						
						
						if (item.getItemType().equals(Common.QUESTION_TYPE_PHOTOGRAPH)){
							response = new PhotographResultSet(	index++, 
														item.getItemText(), 
														result.getResultBlob(),
														(result.getFreeText() == null ? "" : result.getFreeText().getFreeText()), 
														result.getId().getResOrderNo(),
														script.getId());
							response.setPhotograph(true);
						}
						else if (item.getItemType().equals(Common.QUESTION_TYPE_BITMAP) || item.getItemType().equals(Common.QUESTION_TYPE_SIGNATURE)){
							response = new ResultSet(	index++, 
														item.getItemText(), 
														result.getResultBlob(),
														(result.getFreeText() == null ? "" : result.getFreeText().getFreeText()), 
														result.getId().getResOrderNo(),
														script.getId());
							response.setBitmap(true);
						}
						// voice and video are currently unsupported, but we want to display a message that the user should refer to FDM
						else if (item.getItemType().equals(Common.QUESTION_TYPE_VIDEO) || item.getItemType().equals(Common.QUESTION_TYPE_VOICE_RECORDING)){
							response = new ResultSet(	index++, 
														item.getItemText(), 
														result.getResultBlob(), 
														(result.getFreeText() == null ? "" : result.getFreeText().getFreeText()), 
														result.getId().getResOrderNo(),
														script.getId());
							if (item.getItemType().equals(Common.QUESTION_TYPE_VIDEO)) {
								response.setVideo(true);
							} else {
								response.setAudio(true);
							}
						}
						else if (item.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK) || item.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK_RULE)){
							
							response = new MultiPickResultSet(	index++, 
														item.getItemText(), 
														result.getResponse(), 
														(result.getFreeText() == null ? "" : result.getFreeText().getFreeText()), 
														result.getId().getResOrderNo());
							response.setMultiPickListItems(result.getSuppResults());
							response.setMultiPickList(true);
						}
						else if(item.getItemType().equals(Common.QUESTION_TYPE_INSTRUCTION)){
							//FDP1420 - MC - change response for instruction question
							response = new StringResultSet(index++, 
									item.getItemText(), 
									(result.getResponse() != null && (result.getResponse().equals("1") || result.getResponse().equals("COMPLETED"))) ? "COMPLETED" : "NOT COMPLETED", 
									(result.getFreeText() == null ? "" : result.getFreeText().getFreeText()), 
									result.getId().getResOrderNo());

						}
						else if (item.getItemType().equals(Common.QUESTION_TYPE_TASK_LIST)){
													
							response = new MultiPickResultSet(	index++, 
														item.getItemText(), 
														result.getResponse(), 
														(result.getFreeText() == null ? "" : result.getFreeText().getFreeText()), 
														result.getId().getResOrderNo());
							response.setTaskListItems(result.getTaskList());
							response.setTaskList(true);
						}
						else if(item.getItemType().equals(Common.QUESTION_TYPE_TIME)){
							response = new DateResultSet(	index++, 
														item.getItemText(), 
														DateUtil.parseTime(result.getResponse()), 
														(result.getFreeText() == null ? "" : result.getFreeText().getFreeText()), 
														result.getId().getResOrderNo());
						}
						else if(item.getItemType().equals(Common.QUESTION_TYPE_DATE)){
							
							if (StringUtils.isNumeric(result.getResponse())){
								response = new DateResultSet(	index++, 
															item.getItemText(), 
															DateUtil.stringToDate(result.getResponse()), 
															(result.getFreeText() == null ? "" : result.getFreeText().getFreeText()), 
															result.getId().getResOrderNo());
								response.setResponse(result.getResponse());
								response.setDate(true);
							}
							else{
								response = new DateResultSet(	index++, 
															item.getItemText(), 
															result.getResponse(), 
															(result.getFreeText() == null ? "" : result.getFreeText().getFreeText()), 
															result.getId().getResOrderNo());
								response.setDate(true);
							}
						}
						else if(item.getItemType().equals(Common.QUESTION_TYPE_NUMERIC) || item.getItemType().equals(Common.QUESTION_TYPE_DECIMAL)){
							response = new NumericResultSet(	index++, 
														item.getItemText(), 
														result.getResponse(), 
														(result.getFreeText() == null ? "" : result.getFreeText().getFreeText()), 
														result.getId().getResOrderNo());
						}else if(item.getItemType().equals(Common.QUESTION_TYPE_FORMATTED_DATA_INPUT)) {
							response = new FormattedDataResultSet(	index++, 
									item.getItemText(), 
									result.getResponse(), 
									(result.getFreeText() == null ? "" : result.getFreeText().getFreeText()), 
									result.getId().getResOrderNo());
						}else if(item.getItemType().equals(Common.QUESTION_TYPE_LEVEL)) {
							response = new LevelResultSet(	index++, 
									item.getItemText(), 
									result.getResponse(), 
									(result.getFreeText() == null ? "" : result.getFreeText().getFreeText()), 
									result.getId().getResOrderNo());

						}
						else {
							response = new StringResultSet(	index++, 
									item.getItemText(), 
									result.getResponse(), 
									(result.getFreeText() == null ? "" : result.getFreeText().getFreeText()), 
									result.getId().getResOrderNo());
						}
						
						//FDP1420 - MC - add extended response
						if(result.getExtdResponse() != null) {
							//i guess it should keep only extended data we can append this with actual response when ever needed
							response.setExtendedResponse(result.getExtdResponse().getExtdResponse());
						}

						if(result.getResponseType().equals("N/A") || result.getResponseType().equals("O/R")) {
							response.setResponse(result.getResponseType());
							response.setExtendedResponse(null);
						}
						
						//FDE058 - MC - edit response details
						
						if(item.getValidation() != null) {
							
							String refResponse = null;
							if(item.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK_RULE) || item.getItemType().equals(Common.QUESTION_TYPE_SINGLE_PICK_RULE)) {
								//FDE058 - we need to update the validation list if we are dealing with a rule based validation list as it may change based on previous responses

								if(item.getItemRespRulesRef() != null) {
									Integer refSeq = item.getItemRespRulesRef().getRefSeqNo();
									for(int i=responses.size() - 1 ; i >= 0; i--) {
										ResultSet res = responses.get(i);
										if(res.getSequenceNo().equals(refSeq)) {
											refResponse = res.getResponse();
											break;
										}
									}
								}else {
									//Get the previous question that isnt a heading
									for(int i=responses.size() - 1 ; i >= 0; i--) {
										ResultSet res = responses.get(i);
										if(!res.isHeading()) {
											refResponse = res.getResponse();
											break;
										}
									}
								}
							}
							
							
							item = getScriptService().populateScriptItemValidationAndColourData(null, item, refResponse);

							response.setValidationPropertyList(item.getValidationPropertyList());
							response.generateColorMap(item);
							
						}
						//END FDE058 - MC - edit response details
						
						response.setOotFlag(result.getOotFlag());
						response.setOtCol(result.getOotFlag()!=null && result.getOotFlag()!=0 ?"font-weight:bold;color:#"+Common.convertVbToHexStrColorCode(otCol):"");
						
						if (!ManagedBeanHelper.findBean("fdmenv", Environment.class).isViewPII()) {
							if (item.getRefItems() != null) {
								for (RefItem refItem : item.getRefItems()) {
									if (refItem.getRefSubCat().getRefSubCatDesc().equalsIgnoreCase("PII")) {
										response.setViewPII(true);
									}
								}
							}
						}
						
						//	38437 - KN - setting Result Date and time
						response.setResultDate(result.getResultDate());
						response.setResultTime(result.getResultTime());
						
						//38437-KN - set Input Type for Mandatory
						response.setInputType(item.getInputType());
						
						//38437-KN - set Question Type from Item
						response.setItemType(item.getItemType());
						
						//setting decimal precision --- move down
						response.setPrecision(item.getPrecision());
						response.setFieldSize(item.getFieldSize());
						
						//40317 - toggle answered/unanswered
						response.setiLevel(item.getiLevel());
						
						response.setResponseType(result.getResponseType());
						if (result.getResponseType() != null && !(result.getResponseType().equalsIgnoreCase("OK"))) {
							response.setMandatorymodifierCheckBox(true);
						} 						
						response.setUom(result.getUom());

						
						//37392 - Set the result notes for the relevant question that it belongs to
						response.setScriptId(item.getId().getScriptId());
						response.setSequenceNo(result.getId().getSequenceNo());

						
						seqDiff = response.getResOrderNo() - response.getSequenceNo();
						// temporary hack, stops the same result being processed for two different script items
						result.getId().setSequenceNo(0);
						
						response.setScript(evaluateDynamicQuestionText(response,responses));
						
						this.setAdditionalResponseInfo(response);
						
						responses.add(response);
						found = true;
						break;
					}
				}
				
				if (!found){ // if a response isn't found, we just add the item text, and it will be labelled as unanswered
	
					ResultSet response;
					
					if (item.getItemType().equals(Common.QUESTION_TYPE_PHOTOGRAPH)){
						response = new PhotographResultSet(index++, item.getId().getScriptId(), item.getItemText(), item.getId().getSequenceNumber() + seqDiff, item.getId().getSequenceNumber(), item.getItemType(), item.getInputType());
						response.setPhotograph(true);
						
					}else if (item.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK) || item.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK_RULE) || item.getItemType().equals(Common.QUESTION_TYPE_TASK_LIST)){
						response = new MultiPickResultSet(index++, item.getId().getScriptId(), item.getItemText(), item.getId().getSequenceNumber() + seqDiff, item.getId().getSequenceNumber(), item.getItemType(), item.getInputType());
						
						
					}else if(item.getItemType().equals(Common.QUESTION_TYPE_DATE) || item.getItemType().equals(Common.QUESTION_TYPE_TIME)){
						response = new DateResultSet(index++, item.getId().getScriptId(), item.getItemText(), item.getId().getSequenceNumber() + seqDiff, item.getId().getSequenceNumber(), item.getItemType(), item.getInputType());
						
						
					}else if(item.getItemType().equals(Common.QUESTION_TYPE_NUMERIC) || item.getItemType().equals(Common.QUESTION_TYPE_DECIMAL)){
						response = new NumericResultSet(index++, item.getId().getScriptId(), item.getItemText(), item.getId().getSequenceNumber() + seqDiff, item.getId().getSequenceNumber(), item.getItemType(), item.getInputType());
						
					}else if(item.getItemType().equals(Common.QUESTION_TYPE_FORMATTED_DATA_INPUT)) {
						
						response = new FormattedDataResultSet(index++, item.getId().getScriptId(), item.getItemText(), item.getId().getSequenceNumber() + seqDiff, item.getId().getSequenceNumber(), item.getItemType(), item.getInputType());
					}else if(item.getItemType().equals(Common.QUESTION_TYPE_LEVEL)) {
						
						response = new LevelResultSet(index++, item.getId().getScriptId(), item.getItemText(), item.getId().getSequenceNumber() + seqDiff, item.getId().getSequenceNumber(), item.getItemType(), item.getInputType());
					}else {
						response = new StringResultSet(index++, item.getId().getScriptId(), item.getItemText(), item.getId().getSequenceNumber() + seqDiff, item.getId().getSequenceNumber(), item.getItemType(), item.getInputType());
					}
					
					//setting decimal precision --- move down
					response.setPrecision(item.getPrecision());
					response.setFieldSize(item.getFieldSize());
					response.setiLevel(item.getiLevel());
					
					response.setScript(evaluateDynamicQuestionText(response,responses));

					
					responses.add(response);
				}
			}
		}
		
		//40317 - toggle answered/unanswered
		for(ResultSet response : responses) {
			if(response.isHeading()) {
				response.setHasAnsweredQuestions(this.shouldShowHeading(response, responses));
			}
		}
		
		return responses;
	}
	
	/**
	 * this is a quick fix to sort the script items map
	 * 
	 * @param scriptItemsMap	- the map of script items to be sorted
	 * 
	 * @return	- sorted script items map
	 */
	public Map<Integer, Item> getSortedScriptItemsMap(Map<Integer, Item> scriptItemsMap){
		Map<Integer, Item> sortedScriptItemsMap = null;
		
		if (scriptItemsMap != null){
			sortedScriptItemsMap = new TreeMap<Integer, Item>();
			
			for (Map.Entry<Integer, Item> entry : scriptItemsMap.entrySet()) {
				sortedScriptItemsMap.put(entry.getKey(), entry.getValue());
			}
		}
		
		return sortedScriptItemsMap;
	}
	

	
	
	//40433 - create a function to get carrythrough label
		public String getLabelForCarryThroughRes(CarrythroughRes carrythroughRes) {
			
			String label = carrythroughRes!=null ? carrythroughRes.getId().getFieldName(): "";
			if(this.carryThrough!=null && this.carryThroughDef!=null && carrythroughRes !=null) {
				for(Carrythrough carDef : this.carryThroughDef)
				{
					if(carrythroughRes.getId().getFieldName().equals(carDef.getId().getFieldName())){
						//return the label in definition if presen, otherwise return the field name
						return label = carDef.getFieldLabel() != null ? carDef.getFieldLabel() : carDef.getId().getFieldName();
					}
				}
			}
			
			return label;
		}
		
		//40433 - function to filter all the GPS Entries
		public List<CarrythroughRes> filterAdditionalInfo(){
			List<CarrythroughRes> filteredAdditionalInformation = new ArrayList<CarrythroughRes>();
			
			if(this.carryThrough!=null && this.carryThroughDef!=null) {
				
				for(CarrythroughRes carryThroughRes : this.carryThrough) {
					
					for(Carrythrough carDef : this.carryThroughDef) {
						
						if(carryThroughRes.getId().getFieldName().equals(carDef.getId().getFieldName())){
							//add carrythrough to list if there is a match for result in carrythrough definition
							filteredAdditionalInformation.add(carryThroughRes);
							break;
						}
					}
				}
			}
			
			return filteredAdditionalInformation;
		}
	public String evaluateDynamicQuestionText (ResultSet response,List<ResultSet> responses) {
		
		// Get text of the current question
		
		String tempQuestionText = response.getScript();
		// Check to see if the text contains an instance of =S
		int qRefIdx = findVariableReference(tempQuestionText, 0, Common.EQUALS_SEQUENCE_FLAG);
		int endIndex = 0;
		
		while (qRefIdx != -1) {

			// Retrieve x for each instance of =Sx contained in the
			// question text

			Integer seqNo = getSequenceNumberFromString(tempQuestionText, qRefIdx, Common.EQUALS_SEQUENCE_FLAG);

			// null is returned if SequenceNo could not be evaluated.
			if (seqNo != null) {
				// Get the value of the SequenceNumber number found
				String seqNoVal = getAnswersForSequenceNumberFromReponse(response, seqNo, responses, true);
				
				seqNoVal = seqNoVal != null ? seqNoVal : "<?>";
				
				//index to end of the characters to be replaced
				endIndex = qRefIdx + seqNo.toString().length()+2;
				
				if(tempQuestionText.substring(endIndex).startsWith(Common.DOLLAR_FLAG)) {
					//replace + one char if $ is the end of the dynamic text(=Sxx$)
					tempQuestionText = tempQuestionText.substring(0, qRefIdx) + seqNoVal + tempQuestionText.substring(endIndex+1);
				}else {
					tempQuestionText = tempQuestionText.substring(0, qRefIdx) + seqNoVal + tempQuestionText.substring(endIndex);
				}
					
			}

			// Check to see if there are any more occurrences of =S in
			// the question text
			qRefIdx = findVariableReference(tempQuestionText, qRefIdx + 1, Common.EQUALS_SEQUENCE_FLAG);
		}

		// Now look for occurrences of =$
		qRefIdx = findVariableReference(tempQuestionText, 0, Common.EQUALS_DOLLAR_FLAG);

		while (qRefIdx != -1) {

			// Retrieve x for each instance of =$x contained in the
			// question
			// text

			String paramName = getVariableValue(tempQuestionText, qRefIdx);

			if (paramName != null) {
				
				String paramVal = null;

				// Get the value of the SequenceNumber number found
				paramVal = getParamValueFromCarryThrough(carryThroughMap, paramName);
				
				if(this.equipmentInformation != null && paramVal == null && paramName.startsWith("ASSET_")) {
					paramVal = getParamValueFromEquipmentData(this.equipmentInformation.getEquipmentAttribList(), paramName);
				}
				
				paramVal = paramVal != null ? paramVal : "<?>";
				
				endIndex = qRefIdx + paramName.length()+2;
				
				if(tempQuestionText.substring(endIndex).startsWith(Common.DOLLAR_FLAG)) {
					tempQuestionText = tempQuestionText.substring(0, qRefIdx) + paramVal + tempQuestionText.substring(endIndex+1);
				}else {
					tempQuestionText = tempQuestionText.substring(0, qRefIdx) + paramVal + tempQuestionText.substring(endIndex);
				}
			}

			// Check to see if there are any more occurrences of =$ in
			// the question text
			qRefIdx = findVariableReference(tempQuestionText, qRefIdx + 1, Common.EQUALS_DOLLAR_FLAG);
		}

		return tempQuestionText;
	}
	
	protected int findVariableReference(String text, int fromIdx, String variableType) {

		int retVal = -1;

		// Look for first occurrence of string specified by parameter
		// variableType
		retVal = text.toUpperCase().indexOf(variableType, fromIdx);

		return retVal;
	}
	
	protected String getParamValueFromCarryThrough(Map<String, String> carrythrourghMap, String paramName) {
		
		if (carrythrourghMap != null && carrythrourghMap.containsKey(paramName)) {
			return carrythrourghMap.get(paramName);
		}

		return null;
	}
	
	protected String getParamValueFromEquipmentData(List<EquipmentAttrib> list, String paramName) {
		if (list != null) {
			paramName = paramName.replaceFirst("ASSET_", "");
			for (EquipmentAttrib eq : list) {
				if (eq.getAttributeName().equalsIgnoreCase(paramName))
					return eq.getValue();
			}
		}
		return null;
	}
	
	protected String getAnswersForSequenceNumberFromReponse(ResultSet result, int seqNo, List<ResultSet> responses, boolean formatResponse) {
		//check whether result object is already in the list of object
		int index= responses.indexOf(result);
		//if not parse from the last
		index = index != -1 ? index : responses.size();
		
		for (int i = index - 1; i >= 0; i--) {
			ResultSet res = responses.get(i);
			//response should be valid else return null
			if (res.getSequenceNo().equals(seqNo) && res.getResponseType() != null && res.getResponseType().equalsIgnoreCase("OK")) {
				if (!ManagedBeanHelper.findBean("fdmenv", Environment.class).isViewPII()) {
					// if the response is a PII data
					if (res.isViewPII()) {
						return "**********";
					}
				}
				// if found response is Date get value from DateValue instead of response
				if(res.isDate() && res.getDateValue() != null) {
					
					if (formatResponse) {
						//if response found is Date and returning it to Question, change yyyymmdd to display format dd/MM/yyyy
						return new SimpleDateFormat(Properties.get("date_pattern")).format(res.getDateValue());
						
					}else {
						
						return DateUtil.formatDate(res.getDateValue());
					}
				}
				else {
					//returning response if found
					return res.getResponse();
				
				}
			}
		}

		return null;
	}
	

	protected Integer getSequenceNumberFromString(String text, int fromIdx, String variableType) {

		Integer retVal;
		String temp = "";

		// Trim down string to get x value from =Sx
		temp = getVariableValue(text, fromIdx);

		try {

			// Convert the number to an integer
			retVal = Integer.parseInt(temp);
		} catch (NumberFormatException e) {

			// If a number format exception is thrown then the X value
			// number is not a valid number.
			return null;
		}

		return retVal;
	}
	
	protected String getVariableValue(String text, int fromIdx) {

		int startPos;
		int endPos;
		String temp;

		// Find start position of =$ or =S in string
		startPos = fromIdx;

		// Find next " " space in string
		endPos = text.indexOf(" ", startPos);

		// No " " space after =$x or =S x
		if (endPos == -1) {
			// Use the end of the string as the end position
			endPos = text.length();
			temp = text.substring(startPos, text.length());
		}
		else {
			// " " found after =$x or =Sx
			temp = text.substring(startPos, endPos);
		}
		
		// Trim down string to get x value from =$x or =Sx
		temp = temp.substring(2);
		
		// check the string has '$' as ending and it should not match '=$'
		if(temp.contains(Common.DOLLAR_FLAG)) {
			
			temp = temp.substring(0,temp.indexOf(Common.DOLLAR_FLAG));
			
		}

		return temp;
	}
	
	
	//40317 - toggle answered/unanswered
	protected boolean shouldShowHeading(ResultSet heading, List<ResultSet> responses) {
		int index = responses.indexOf(heading);
		int level = heading.getiLevel();
		if(index >= 0) {
			for(int i=index+1; i<responses.size(); i++) {
				ResultSet answer = responses.get(i);
				if(answer != null) {
					if(answer.isAnswered()) {
						return true;
					}else if(answer.isHeading() && answer.getiLevel() <= level) {
						break;
					} else if(answer.getiLevel() < level) {
						break;
					}
				}
			}
		}
		return false;
	}
	
	
	public ReturnedScripts getScript() {
		return script;
	}
	public void setScript(ReturnedScripts script) {
		this.script = script;
	}
	public List<ResultSet> getAnswers() {
		return answers;
	}
	public void setAnswers(List<ResultSet> answers) {
		this.answers = answers;
	}
	public Integer getReturnId() {
		return returnId;
	}
	public void setReturnId(Integer returnId) {
		this.returnId = returnId;
	}
	public String getScriptDescription() {
		return scriptDescription;
	}
	public void setScriptDescription(String scriptDescription) {
		this.scriptDescription = scriptDescription;
	}
	public String getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(String completeTime) {
		this.completeTime = completeTime;
	}
	public Date getCompleteDate() {
		return completeDate;
	}
	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public Map<String, String> getCarryThroughMap() {
		return carryThroughMap;
	}
	public void setCarryThroughMap(Map<String, String> carryThroughMap) {
		this.carryThroughMap = carryThroughMap;
	}
	public List<CarrythroughRes> getCarryThrough() {
		return carryThrough;
	}
	public void setCarryThrough(List<CarrythroughRes> carryThrough) {
		this.carryThrough = carryThrough;
	}
	public List<Carrythrough> getCarryThroughDef() {
		return carryThroughDef;
	}
	public void setCarryThroughDef(List<Carrythrough> carryThroughDef) {
		this.carryThroughDef = carryThroughDef;
	}
	public List<CarrythroughRes> getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(List<CarrythroughRes> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public ResultSet getHighlightedAnswer() {
		return highlightedAnswer;
	}
	public void setHighlightedAnswer(ResultSet highlightedAnswer) {
		this.highlightedAnswer = highlightedAnswer;
	}
	public String getSelectedQuestion() {
		return selectedQuestion;
	}
	public void setSelectedQuestion(String selectedQuestion) {
		this.selectedQuestion = selectedQuestion;
	}
	public String getSelectedResponse() {
		return selectedResponse;
	}
	public void setSelectedResponse(String selectedResponse) {
		this.selectedResponse = selectedResponse;
	}
	public String getSelectedFreeText() {
		return selectedFreeText;
	}
	public void setSelectedFreeText(String selectedFreeText) {
		this.selectedFreeText = selectedFreeText;
	}
	public boolean isSelectedImage() {
		return selectedImage;
	}
	public void setSelectedImage(boolean selectedImage) {
		this.selectedImage = selectedImage;
	}
	public boolean isVideoTag() {
		return videoTag;
	}
	public void setVideoTag(boolean videoTag) {
		this.videoTag = videoTag;
	}
	public boolean isShowAllQuestions() {
		return showAllQuestions;
	}
	public void setShowAllQuestions(boolean showAllQuestions) {
		this.showAllQuestions = showAllQuestions;
	}
	public EquipmentInformation getEquipmentInformation() {
		return equipmentInformation;
	}
	public void setEquipmentInformation(EquipmentInformation equipmentInformation) {
		this.equipmentInformation = equipmentInformation;
	}
	public boolean isSelectedOnApprovalDetail() {
		return selectedOnApprovalDetail;
	}
	public void setSelectedOnApprovalDetail(boolean selectedOnApprovalDetail) {
		this.selectedOnApprovalDetail = selectedOnApprovalDetail;
	}
	public List<ResLog> getResLogList() {
		return resLogList;
	}
	public void setResLogList(List<ResLog> resLogList) {
		this.resLogList = resLogList;
	}

	
}
