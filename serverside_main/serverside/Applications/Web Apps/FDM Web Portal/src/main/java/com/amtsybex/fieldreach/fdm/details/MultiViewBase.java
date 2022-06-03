package com.amtsybex.fieldreach.fdm.details;

import java.util.ArrayList;
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
import com.amtsybex.fieldreach.backend.model.Item;
import com.amtsybex.fieldreach.backend.model.RefItem;
import com.amtsybex.fieldreach.backend.model.RepeatGroups;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.ScriptResults;
import com.amtsybex.fieldreach.fdm.Environment;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.SystemActivityLogger;
import com.amtsybex.fieldreach.fdm.util.DateUtil;
import com.amtsybex.fieldreach.fdm.web.jsf.util.ManagedBeanHelper;
import com.amtsybex.fieldreach.utils.impl.Common;

public class MultiViewBase extends PageCodebase {

	private static final long serialVersionUID = 2343223632678422290L;

	protected List<DetailsBase> details;
	
	private Map<Integer, Boolean> repeatedItems;
	private RepeatGroups currentRepeatedGroup;
	
	private Integer otCol; 
	private List<RepeatGroups> repeatedGroups;
	private List<Carrythrough> scriptCarrythoughDef;
	
	@Inject
	transient SystemActivityLogger systemActivityLogger;
	
	public void initialise(List<ReturnedScripts> scripts, boolean csvImport) throws FRInstanceException {
		
		otCol = this.getSystemParameterService().getSystemParams(null).getOtCol();
		
		details = new ArrayList<DetailsBase>();
		Map<Integer, List<ScriptResults>> detailsScriptResults = new HashMap<Integer, List<ScriptResults>>();
		
		Map<Integer, Item> scriptItems = null;
		
		for(ReturnedScripts script : scripts) {
		
			if(scriptItems == null) {
				//First time through this loop
				scriptItems = getSortedScriptItemsMap(getScriptService().findScriptItems(null, script.getScriptId()));
				
				repeatedGroups = getScriptService().findRepeatGroupsByScriptId(null, script.getScriptId());
				
				if(csvImport) {
					scriptCarrythoughDef = getScriptService().getScriptCarrythough(null,script.getScriptId());
				}
			}

			DetailsBase detail = new Details();
			detail.setScript(script);
			detail.setReturnId(script.getId());
			this.setDetailsHeader(detail, script);
			
			if(csvImport && scriptCarrythoughDef != null && scriptCarrythoughDef.size() > 0) {

				detail.setCarryThrough(getScriptResultsService().getResultCarrythrough(null, script.getId()));
				
				detail.setCarryThroughDef(scriptCarrythoughDef);
				
				detail.setAdditionalInfo(detail.filterAdditionalInfo());
				
				detail.carryThroughMap = new HashMap<String,String>();
				if (detail.carryThrough != null) {
					for (CarrythroughRes ct : detail.carryThrough) {
						detail.carryThroughMap.put(ct.getId().getFieldName(), ct.getFieldValue());
					}
				}
			}

			List<ScriptResults> scriptResults = getScriptResultsService().getScriptResults(null, script.getId());
			
			if(csvImport) {
				//TODO maybe come back to this. it should just not load images rather than strip them out after loading them.
				//stripping out here to avoid memory build up.
				for (ScriptResults result : scriptResults){
					result.setResultBlob(null);
				}
			}
			detailsScriptResults.put(detail.getReturnId(), scriptResults);
			
			details.add(detail);
			
		}
		
		List<Item> processedItems = this.buildQuestions(scriptItems, details, detailsScriptResults);

		for(DetailsBase detail : details) {
			
			detail.setAnswers(buildResponses(scriptItems, detail, processedItems, detailsScriptResults, csvImport));
			
			//systemActivityLogger.recordActivityLog(null, ACTIVITYTYPE.RESULT_SEARCH_DETAIL, getUsername(), detail.getScript().getId().toString(), "");
			
		}
		
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
	
	
	public void setDetailsHeader(DetailsBase detail, ReturnedScripts script) throws FRInstanceException{
		
		//detail.setScriptDescription(getScriptService().findScriptByScriptCode(null, script.getScriptCode()).getScriptDesc());
		detail.setScriptDescription(script.getScript() == null? null :script.getScript().getScriptDesc());
		// we need to display dates and times in a more readable format than the format they are stored
		detail.setCompleteDate(script.getCompleteDate() == null ? null : DateUtil.intToDate(script.getCompleteDate()));
		detail.setCompleteTime(script.getCompleteTime() == null ? null : DateUtil.parseTime(String.valueOf(script.getCompleteTime())));
	}
	

	public List<Item> buildQuestions(Map<Integer, Item> scriptItems, List<DetailsBase> details, Map<Integer, List<ScriptResults>> detailsScriptResults) throws FRInstanceException {
		// get a list of repeated groups that can occur for this script
		
		Iterator<Item> scriptItemsIterator = scriptItems.values().iterator();
		List<Item> processedItems = new ArrayList<Item>();
		
		// map for storing the occurrences of repeated groups
		repeatedItems = new HashMap<Integer, Boolean>();

		boolean headerRepeated = false;
		currentRepeatedGroup = null;
		
		// iterate over the list of script items
		while (scriptItemsIterator.hasNext()){
			Item scriptItem = scriptItemsIterator.next();
			
			if(currentRepeatedGroup != null && scriptItem.getId().getSequenceNumberInt() < currentRepeatedGroup.getToSeqNo()) {
				continue;
			}
			
			//EWP596 MC 10/05/2018. Do not check for Script Items with Null Item type
			if (scriptItem.getItemType() != null) {
			
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
							
								int tempoccurrences = 0;
								for(DetailsBase detail : details) {
									
									Integer lastItemFound = 0;
									
									boolean startedGroup = false;
									
									
									// find out how many times it has been repeated by checking for instances of the "to" id in the results
									for (ScriptResults result : detailsScriptResults.get(detail.getReturnId())) {
										
										if(result.getId().getSequenceNo() > repeatedGroup.getToSeqNoInt()) {
											break;
										}else if(!startedGroup && result.getId().getSequenceNo() >= repeatedGroup.getId().getFromSeqNoInt()) {
											startedGroup = true;
											lastItemFound = result.getId().getSequenceNo();
											tempoccurrences++;
										}
										if ((result.getId().getSequenceNo() < repeatedGroup.getToSeqNo() && lastItemFound > result.getId().getSequenceNoInt()) || result.getId().getSequenceNo().equals(repeatedGroup.getToSeqNo())){
											startedGroup = false;
										}
										lastItemFound = result.getId().getSequenceNo();
									}
									
									if(tempoccurrences > occurrences) {
										occurrences = tempoccurrences;
									}
									
									tempoccurrences = 0;
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
								repeatedItems.put(scriptItems.get(i).getId().getSequenceNumber(), true);
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
		
		return processedItems;
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
	public List<ResultSet> buildResponses(Map<Integer, Item> scriptItems, DetailsBase detail, List<Item> processedItems, Map<Integer, List<ScriptResults>> detailsScriptResults, boolean csvImport) throws FRInstanceException{
		
		List<ResultSet> responses = new ArrayList<ResultSet>();
		
		ReturnedScripts script = detail.getScript();

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
				heading.setScript(this.evaluateDynamicQuestionText(heading));
				
				
				// add this heading to the list of responses
				responses.add(heading);
			}
			else if (item.getItemType().equals(Common.QUESTION_TYPE_BLOCK_MARKER)){
				// ignore end items for the time being
			}
			else{
				// for anything that isn't a heading or an end item
				boolean found = false;
				
				for (ScriptResults result : detailsScriptResults.get(detail.getReturnId())){
					
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
							
							if(!csvImport || item.getItemType().equals(Common.QUESTION_TYPE_TASK_LIST)) {
								item = getScriptService().populateScriptItemValidationAndColourData(null, item, refResponse);
								response.setValidationPropertyList(item.getValidationPropertyList());
								response.generateColorMap(item);
							}
							

						}

						response.setOotFlag(result.getOotFlag());
						response.setOtCol(result.getOotFlag()!=null && result.getOotFlag()!=0 ?"font-weight:bold;color:#"+Common.convertVbToHexStrColorCode(otCol):"");
						
						response.setScriptRefItemList(item.getScriptRefItemList() == null ? null : new ArrayList<>(item.getScriptRefItemList()));
						if(!ManagedBeanHelper.findBean("fdmenv", Environment.class).isViewPII()) {
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
						
						response.setScript(this.evaluateDynamicQuestionText(response));
						
						if(csvImport) {
							addRepeateToCSVResponse(item, response, occurrences);
						}
						
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
					
					responses.add(response);
					
					
					if(csvImport) {
						addRepeateToCSVResponse(item, response, occurrences);
					}
				}

			}
		}
		
		return responses;
	}
	
	private void addRepeateToCSVResponse(Item item, ResultSet response, Map<Integer, Integer> occurrences) {
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
			
			response.setScript(response.getScript()+" ("+occurrence+")");
			
		}
	}
	
	protected int findVariableReference(String text, int fromIdx, String variableType) {

		int retVal = -1;

		// Look for first occurrence of string specified by parameter
		// variableType
		retVal = text.toUpperCase().indexOf(variableType, fromIdx);

		return retVal;
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
	
	public String evaluateDynamicQuestionText (ResultSet response) {
		
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
				String seqNoVal =  "<?>";
				
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
				paramVal = "<?>";
				
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
	


	
	
}
