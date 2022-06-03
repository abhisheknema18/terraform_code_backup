package com.amtsybex.fieldreach.fdm.analytics;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.component.EditableValueHolder;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.Item;
import com.amtsybex.fieldreach.backend.model.Script;
import com.amtsybex.fieldreach.backend.model.ScriptResultsAnalytics;
import com.amtsybex.fieldreach.backend.model.ScriptVersions;
import com.amtsybex.fieldreach.backend.model.SuppResults;
import com.amtsybex.fieldreach.backend.model.TaskListRes;
import com.amtsybex.fieldreach.backend.model.ValidationProperty;
import com.amtsybex.fieldreach.backend.service.ValidationTypeService;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.util.DateUtil;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.utils.impl.Common;

/**
 * FDE060 - script analytics/metrics
 * @author CroninM
 *
 */
@Named
@WindowScoped
public class ResultAnalysis extends PageCodebase implements Serializable {

	private static final long serialVersionUID = -1273020927825235564L;

	private static Logger _logger = LoggerFactory.getLogger(ResultAnalysis.class.getName());
	
	private TreeNode scriptRoot;  
	private TreeNode selectedScriptNode; 
	
	//private Map<HPCWorkGroups, List<HPCUsers>> workgroupMap;
	private List<HPCWorkGroups> workgroups;
	
	private boolean allWorkgroupsSelected;
	private TreeNode workgroupRoot;  
	private TreeNode[] selectedWorkgroupNodes; 
	
	private List<String> workGroupStrings;
	
	private AnalysisSearchCriteria searchCriteria;
	private AnalysisSearchCriteria undoSearchCriteria;
	
	private AnalysisSearchCriteria searchVersionCriteria;
	private AnalysisSearchCriteria undoSearchVersionCriteria;
	
	private List<AnalysisResultSet> dataResultSet;
	
	private Integer tabActiveIndex;
	
	private Item selectedQuestion;
	
	private boolean searchingVersionAnalysis;
	
	@Inject
	private ValidationTypeService validationTypeService;

	public ResultAnalysis() {
	}
	
	@PostConstruct
	public void init() {
		
		this.searchCriteria = new AnalysisSearchCriteria();
		this.undoSearchCriteria = new AnalysisSearchCriteria();
		
		this.searchVersionCriteria = new AnalysisSearchCriteria();
		this.undoSearchVersionCriteria = new AnalysisSearchCriteria();
		
		tabActiveIndex = 0;
		allWorkgroupsSelected = true;
		workGroupStrings = null;
		workgroups = null;
	}
	
	public void reset(){
		
		_logger.debug(">>> reset");
		
		this.getSearchCriteria().reset();
		this.getUndoSearchCriteria().reset();

		scriptRoot = null;  
		
		selectedScriptNode = null; 
		
		this.loadSelectedScripts();
		
		workgroupRoot = null; 
		selectedWorkgroupNodes = null; 
		allWorkgroupsSelected = true;

		workGroupStrings = null;
		
		_logger.debug("<<< reset");
	}
	
	public void cancel() {
		
		this.setCurrentSearchCriteria(new AnalysisSearchCriteria(this.getUndoSearchCriteria()));
		loadSelectedScripts();
	}
	
	/**
	 * method for building the tree of scripts
	 * 
	 * @throws UserNotFoundException
	 * @throws FRInstanceException 
	 */
	public void loadSelectedScripts(){
		
		_logger.debug(">>> loadSelectedScripts");

		try {
			this.setScriptRoot(new CheckboxTreeNode("Root", null));

			// make a map for the script categories
			Map<Integer, CheckboxTreeNode> scriptCats = new TreeMap<Integer, CheckboxTreeNode>();

			List<Script> userScripts = fetchScripts();

			// for every script
			for (Script s : userScripts){

				// check if we have created a parent node for the current script's category
				CheckboxTreeNode cat = scriptCats.get(s.getScriptCategory().getId());

				// if not, create a new parent node for the current script's category
				if (cat == null){
					cat = new CheckboxTreeNode(s.getScriptCategory(), this.getScriptRoot());
					cat.setExpanded(true);
					scriptCats.put(s.getScriptCategory().getId(), cat);
				}


				// create a node for the current script, parenting it to the node we just retrieved/created
				CheckboxTreeNode node = new CheckboxTreeNode(s, cat); 

				if(this.getSearchCriteria().getScript() != null && this.getSearchCriteria().getScript().equals(s)) {
					node.setSelected(true, true);
				}

			}
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedScripts Unknown error occurred" + e.getMessage());
		}

		_logger.debug("<<< loadSelectedScripts");

	}
	
	public List<Script> fetchScripts() throws UserNotFoundException, FRInstanceException {
		return getScriptService().getFDMScriptList(null,getUsername());
	}
	
	public void updateSelectedScripts(){

		_logger.debug(">>> updateSelectedScripts");
		this.getSearchCriteria().setScript(null);
		
		if (this.getSelectedScriptNode() != null && this.getSelectedScriptNode().getData() instanceof Script)
		this.getSearchCriteria().setScript((Script)this.getSelectedScriptNode().getData());
		
		_logger.debug("<<< updateSelectedScripts");
	}
	
	public void clearSelectedScripts(){

		_logger.debug(">>> clearSelectedScripts");
		
		this.getSearchCriteria().setScript(null);

		_logger.debug("<<< clearSelectedScripts");
	}
	
	
	public String search(){

		_logger.debug(">>> search");
		
		if(this.getSearchCriteria().getScript() == null) {
			MessageHelper.setErrorMessage(null, Properties.get("fdm_analysis_validate_scriptselection"));
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			try {
				EditableValueHolder component = (EditableValueHolder) FacesContext.getCurrentInstance().getViewRoot().findComponent("resultAnalysisTabs:analysisForm:scriptSelectionTree");
				component.setValid(false);
			}catch(Exception e) {}
			
			return null;
		}
		if(this.getSearchCriteria().getFromDate() == null ^ this.getSearchCriteria().getToDate() == null){
			
			MessageHelper.setErrorMessage(null, Properties.get("fdm_search_both_dates_need_set"));
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			try {
				EditableValueHolder component = (EditableValueHolder) FacesContext.getCurrentInstance().getViewRoot().findComponent("resultAnalysisTabs:analysisForm:fromDate");
				component.setValid(false);
				component = (EditableValueHolder) FacesContext.getCurrentInstance().getViewRoot().findComponent("resultAnalysisTabs:analysisForm:toDate");
				component.setValid(false);
			}catch(Exception e) {}
			
			return null;
		}

		if (this.getSearchCriteria().getFromDate() != null && this.getSearchCriteria().getToDate() != null) {
			if (this.getSearchCriteria().getFromDate().after(this.getSearchCriteria().getToDate())) {
				_logger.debug("<<< search from date greater than to date");
				MessageHelper.setErrorMessage(null, Properties.get("fdm_search_from_before_to"));
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				try {
					EditableValueHolder component = (EditableValueHolder) FacesContext.getCurrentInstance().getViewRoot().findComponent("resultAnalysisTabs:analysisForm:fromDate");
					component.setValid(false);
					component = (EditableValueHolder) FacesContext.getCurrentInstance().getViewRoot().findComponent("resultAnalysisTabs:analysisForm:toDate");
					component.setValid(false);
				}catch(Exception e) {}
				// throw new Exception("Planned Start Date after Required Finish Date");
				return null;
			}
		}
		
		if(this.isSearchingVersionAnalysis()) {
			return searchVersion();
		}else {
			try {
				return searchDataSet();
			} catch (UserNotFoundException e) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
				_logger.error("searchDataSet Unknown error occurred" + e.getMessage());
			}
			return null;
		}
		
		
		
	}
	
	public void onVersionTabChange(TabChangeEvent event){
		
		try {
	
			AnalysisResultSet vRes = (AnalysisResultSet)event.getData();
			
			if(vRes.getVersionAnalytics() != null) {
				//already loaded
				return;
			}
			
			List<Object> counts = getScriptResultsService().getResultVersionCounts(null, vRes.getWorkingVersion().getId(),
					this.getSearchCriteria().getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getFromDate())), 
					this.getSearchCriteria().getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getToDate())),
					this.getSearchCriteria().getWorkgroupListAsString());
			
			
			Long averageDuration = this.getScriptResultsService().getAverageDurationForVersion(null, vRes.getWorkingVersion().getId(),
					this.getSearchCriteria().getWorkgroupListAsString(),
					this.getSearchCriteria().getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getFromDate())), 
					this.getSearchCriteria().getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getToDate())));
			
			vRes.setAverageDuration(averageDuration);
			
			Map<Integer, Item> scriptItems = new HashMap<Integer, Item>();

			scriptItems = getScriptService().findByScriptIdNoAssocData(null, vRes.getWorkingVersion().getId());
			
			vRes.setLatestScriptItems(scriptItems);
			
			vRes.setRepeatGroups(this.getScriptService().findRepeatGroupsByScriptId(null, vRes.getWorkingVersion().getId()));
			vRes.setScriptItemRules(this.getScriptService().getScriptItemRulesByScriptId(null, vRes.getWorkingVersion().getId()));
			
			if(counts != null) {
				vRes.setOverAllResultCount((Long)counts.get(0));
				vRes.setDateCounts( (Map<Integer, Long>)counts.get(1));
				//vRes.setWorkgroupCounts(workGroupCounts);
				vRes.setWgCatCounts((Map<String, Map<String,Object>>)counts.get(2));
			}
			
			Map<Integer, Map<String, Long>> versionAnalytics = this.getScriptResultsService().getScriptResultsAnsweredAnalytics(
					null, 
					vRes.getWorkingVersion().getId(), 
					this.getSearchCriteria().getWorkgroupListAsString(), 
					this.getSearchCriteria().getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getFromDate())), 
					this.getSearchCriteria().getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getToDate())));
			
			vRes.setVersionAnalytics(versionAnalytics);
			
		} catch (NumberFormatException | FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage("Exception Occured");
		}
		
	}
	
	private String searchVersion() {
		
		this.undoSearchVersionCriteria = new AnalysisSearchCriteria(this.getSearchCriteria());
		
		//get results count for scriptid/script code, something like
		//getScriptResultsService().getScriptResultsAnalyticForscriptcodeanddaterange(null, scriptcode, datefrom, date to)
		//select scriptid, count(1) from returnedscripts where scriptcode='AUDVID' and completedate > 20200226 and completedate<202000301 group by scriptid;

		try {

			List<Object> countAndDateRange = getScriptResultsService().getResultCountAndDateRange(null, this.getSearchCriteria().getScript().getScriptCode(),
					this.getSearchCriteria().getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getFromDate())), 
					this.getSearchCriteria().getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getToDate())),
					null);
			
			
			List<Integer>versionIds = getScriptResultsService().getScriptVersionsForResults(null, this.getSearchCriteria().getScript().getScriptCode(),
					this.getSearchCriteria().getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getFromDate())), 
					this.getSearchCriteria().getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getToDate())),
							null);
			
			if(versionIds == null || versionIds.size() == 0) {
				MessageHelper.setGlobalWarnMessage("No Results Found");
				return null;
			}
			
			List<ScriptVersions>versions = getScriptService().findScriptVersions(null, this.getSearchCriteria().getScript().getId());
	
			List<ScriptVersions>versionsFiltered = new ArrayList<ScriptVersions>();
			
			ScriptVersions onlineVersion = getScriptService().findOnlineScriptVersion(null, this.getSearchCriteria().getScript().getId());
			
			if(onlineVersion != null) {
				versionsFiltered.add(onlineVersion);
			}
			
			
			for(ScriptVersions version : versions) {
				//if(version != onlineVersion && versionIds.contains(version.getId())) {
				if(!(version.equals(onlineVersion)) && version.getOnlineDate() != null) {
					versionsFiltered.add(version);
				}
			}
			
			versions = versionsFiltered;
			
			if(onlineVersion == null) {
				onlineVersion = versions.get(0);
			}
		
			List<Object> counts = getScriptResultsService().getResultVersionCounts(null, onlineVersion.getId(),
					this.getSearchCriteria().getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getFromDate())), 
					this.getSearchCriteria().getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getToDate())),
							null);
			
			Map<Integer, Item> scriptItems = new HashMap<Integer, Item>();
			
			scriptItems = getScriptService().findByScriptIdNoAssocData(null, onlineVersion.getId());
			 
			dataResultSet = new ArrayList<AnalysisResultSet>();
			

			
			for(ScriptVersions version : versions) {
				 
				AnalysisResultSet vRes = new AnalysisResultSet(null, null, null, version, null);
				
				vRes.setResultCountOverVersions((Long)countAndDateRange.get(2));
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				try {
					vRes.setResultFromDateOverVersions(df.parse(String.valueOf((Integer)countAndDateRange.get(0))));
				} catch (Exception e) {
					vRes.setResultFromDateOverVersions(null);
				}
				
				try {
					vRes.setResultToDateOverVersions(df.parse(String.valueOf((Integer)countAndDateRange.get(1))));
				} catch (Exception e) {
					vRes.setResultToDateOverVersions(null);
				}
				
				if(onlineVersion.equals(version)) {
					
					if(counts != null && counts.size() != 0) {

						vRes.setOverAllResultCount((Long)counts.get(0));
						vRes.setDateCounts((Map<Integer, Long>)counts.get(1));
						//vRes.setWorkgroupCounts((Map<String, Long>)counts.get(2));
						//<wgcat, <wgCatCount, map<wg, wgCount>>>
						vRes.setWgCatCounts((Map<String, Map<String,Object>>)counts.get(2));

					}
					
					Map<Integer, Map<String, Long>> versionAnalytics = this.getScriptResultsService().getScriptResultsAnsweredAnalytics(
							null, 
							version.getId(), 
							null, 
							this.getSearchCriteria().getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getFromDate())), 
							this.getSearchCriteria().getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getToDate())));
					
					
					Long averageDuration = this.getScriptResultsService().getAverageDurationForVersion(
							null, 
							version.getId(), 
							null, 
							this.getSearchCriteria().getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getFromDate())), 
							this.getSearchCriteria().getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getToDate())));
					
					vRes.setAverageDuration(averageDuration);
					
					vRes.setVersionAnalytics(versionAnalytics);
					
					vRes.setLatestScriptItems(scriptItems);
					
					vRes.setRepeatGroups(this.getScriptService().findRepeatGroupsByScriptId(null, version.getId()));
					vRes.setScriptItemRules(this.getScriptService().getScriptItemRulesByScriptId(null, version.getId()));

					
				}
				
				
				dataResultSet.add(vRes);
				 
			}
			
			this.tabActiveIndex = versions.indexOf(onlineVersion);
			
		} catch (NumberFormatException e) {
			MessageHelper.setGlobalWarnMessage("Exception Occured");
			return null;
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage("Exception Occured");
			return null;
		}
		
		return "resultAnalysis";
		
	}
	
	private String searchDataSet() throws UserNotFoundException {
		

		this.undoSearchCriteria = new AnalysisSearchCriteria(this.getSearchCriteria());
		
		try {
			
			workGroupStrings = this.getSearchCriteria().getWorkgroupListAsString();
			
			if(workGroupStrings == null || workGroupStrings.size() == 0) {
				if(!getUserService().hasUnlimitedAccessibleWorkgroups(null, this.getUsername())) {
					workGroupStrings = this.fetchWorkgroups().stream().map((workgroup) -> workgroup.getId().getWorkgroupCode()).collect(Collectors.toList());
				}
			}
		
			List<Object> counts = getScriptResultsService().getResultCounts(null, this.getSearchCriteria().getScript().getScriptCode(),
					this.getSearchCriteria().getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getFromDate())), 
					this.getSearchCriteria().getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getToDate())),
					workGroupStrings, this.getSearchCriteria().isScopeToRecentAsset());

			if(counts == null || counts.size() == 0) {
				MessageHelper.setGlobalWarnMessage("No Results Found");
				return null;
			}
			
			Long overAllResultCount = (Long)counts.get(0);
			
			Map<Integer, Long> scriptIdCounts =(Map<Integer, Long>)counts.get(1);

			List<ScriptVersions>versions = getScriptService().findScriptVersions(null, this.getSearchCriteria().getScript().getId());


			List<ScriptVersions>versionsFiltered = new ArrayList<ScriptVersions>();

			ScriptVersions onlineVersion = getScriptService().findOnlineScriptVersion(null, this.getSearchCriteria().getScript().getId());

			if(onlineVersion != null) {
				versionsFiltered.add(onlineVersion);
			}

			for(ScriptVersions version : versions) {
				if(!versionsFiltered.contains(version) && scriptIdCounts.keySet().contains(version.getId())) {
					versionsFiltered.add(version);
				}
			}

			versions = versionsFiltered;

			if(onlineVersion == null) {
				onlineVersion = versions.get(0);
			}

			Map<Integer, Map<Integer, Item>> scriptItems = new HashMap<Integer, Map<Integer, Item>>();

			for(Integer scriptId : scriptIdCounts.keySet()) {
				scriptItems.put(scriptId, getScriptService().findScriptItems(null, scriptId));
			}

			if(!scriptItems.containsKey(onlineVersion.getId())) {
				scriptItems.put(onlineVersion.getId(), getScriptService().findScriptItems(null, onlineVersion.getId()));
			}

			Map<Integer, Item> latestScriptItems = scriptItems.get(onlineVersion.getId());

			dataResultSet = new ArrayList<AnalysisResultSet>();

			AnalysisResultSet all = new AnalysisResultSet(overAllResultCount, scriptIdCounts, versions, onlineVersion, latestScriptItems);
			all.setAllScripts(scriptItems);
			all.setDateCounts((Map<Integer, Long>)counts.get(2));
			//all.setWorkgroupCounts((Map<String, Long>)counts.get(3));
			//<wgcat, <wgCatCount, map<wg, wgCount>>>
			all.setWgCatCounts((Map<String, Map<String,Object>>)counts.get(3));

			all.setRepeatGroups(this.getScriptService().findRepeatGroupsByScriptId(null, onlineVersion.getId()));
			all.setScriptItemRules(this.getScriptService().getScriptItemRulesByScriptId(null, onlineVersion.getId()));

			dataResultSet.add(all);

			for(ScriptVersions version : versions) {

				dataResultSet.add(new AnalysisResultSet(scriptIdCounts.get(version.getId()), null, null, version, scriptItems.get(version.getId())));

			}
			
			//reduce workgroups for following question searches to the number of workgroups returned in the counts
			workGroupStrings = new ArrayList<String>();
			
			for (Entry wcEntry : all.getWgCatCounts().entrySet()) {
				Map wgCounts = (Map)((Map)wcEntry.getValue()).get("wgcounts");
				
				for(Object wgEntry : wgCounts.entrySet()) {
					workGroupStrings.add(((Entry)wgEntry).getKey().toString());
				}
			}

		} catch (NumberFormatException e) {
			MessageHelper.setGlobalWarnMessage("Exception Occured");
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage("Exception Occured");
		}

		return "resultAnalysis";
		
	}
	
	public boolean isQuestionSelectionDisabled(Item question) {
		
		if(question == null || question.getItemType() == null) {
			return true;
		}
		if(this.isSearchingVersionAnalysis()) {
			if(question.getItemType() == null
					|| question.getItemType().equals(Common.QUESTION_TYPE_HEADING)
					|| question.getItemType().equals(Common.QUESTION_TYPE_BLOCK_MARKER)) {
				return true;
			}else {
				return false;
			}
		}else {
			if(question.getItemType().equals(Common.QUESTION_TYPE_LEVEL)
					|| question.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK)
					|| question.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK_RULE)
					|| question.getItemType().equals(Common.QUESTION_TYPE_SINGLE_PICK)
					|| question.getItemType().equals(Common.QUESTION_TYPE_SINGLE_PICK_RULE)
					|| question.getItemType().equals(Common.QUESTION_TYPE_TASK_LIST)
					|| question.getItemType().equals(Common.QUESTION_TYPE_YESNO)
					|| question.getItemType().equals(Common.QUESTION_TYPE_CONDITION)
					|| question.getItemType().equals(Common.QUESTION_TYPE_CSV_LINE_ITEM)
					|| question.getItemType().equals(Common.QUESTION_TYPE_LEVEL)
					) {
				return false;
			}
		}

		
		return true;
	}
	

	public void questionSelectedVersion(AnalysisResultSet selectedResultSet, Item question) {
		
		try {
			
			AnalysisResultSetResults resultSet = null;
			
			if(selectedResultSet.getQuestionResults() != null) {
				for(AnalysisResultSetResults res: selectedResultSet.getQuestionResults()) {
					if(res.getQuestion().equals(question)) {
						resultSet = res;
						
						break;
					}
				}
			}

			if(resultSet == null) {
				resultSet = new AnalysisResultSetResults();
				resultSet.setQuestion(question);
				if(selectedResultSet.getQuestionResults() == null) {
					List<AnalysisResultSetResults> r = new ArrayList<AnalysisResultSetResults>();
					r.add(resultSet);
					selectedResultSet.setQuestionResults(r);
				}else {
					selectedResultSet.getQuestionResults().add(0, resultSet);
				}
			}else {
				//move it back up to the top
				selectedResultSet.getQuestionResults().remove(resultSet);
				selectedResultSet.getQuestionResults().add(0, resultSet);
			}
			//only keep the last 5
			if(selectedResultSet.getQuestionResults() != null && selectedResultSet.getQuestionResults().size() == 5) {
				selectedResultSet.getQuestionResults().remove(4);
			}
			
			for(Integer i=resultSet.getQuestion().getId().getSequenceNumberInt(); i >= 0; i--) {
				
				if(selectedResultSet.getLatestScriptItems().containsKey(i)) {
					Item item = selectedResultSet.getLatestScriptItems().get(i);
					if(item != null && item.getItemType().equals(Common.QUESTION_TYPE_HEADING)) {
						resultSet.setHeading(item.getItemText());
						break;
					}
				}

			}
			
			resultSet.setNumericTotal(0);
			resultSet.setNumericAverage(0);
			resultSet.setNumericRangeLower(0);
			resultSet.setNumericRangeUpper(0);
			
			if(question.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK)
					|| question.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK_RULE)
					|| question.getItemType().equals(Common.QUESTION_TYPE_SINGLE_PICK)
					|| question.getItemType().equals(Common.QUESTION_TYPE_SINGLE_PICK_RULE)
					|| question.getItemType().equals(Common.QUESTION_TYPE_TASK_LIST)
					|| question.getItemType().equals(Common.QUESTION_TYPE_CONDITION)
					|| question.getItemType().equals(Common.QUESTION_TYPE_CSV_LINE_ITEM)
					|| question.getItemType().equals(Common.QUESTION_TYPE_YESNO)) {
				
				resultSet.setCanShowBar(true);
				resultSet.setShowMetricSinglePickBar(true);
				
				List<Integer> idList = new ArrayList<Integer>();
				idList.add(selectedResultSet.getWorkingVersion().getId());
				
				List<ScriptResultsAnalytics> res = this.getScriptResultsService().getScriptResults(null, idList, this.getSearchCriteria().getWorkgroupListAsString(),
						this.getSearchCriteria().getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getFromDate())), 
						this.getSearchCriteria().getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getToDate())),
								question.getId().getSequenceNumber());
				
				
				List<ValidationProperty> props = validationTypeService.getValidationPropertyByValidationTypeWeightScoreDesc(null, question.getValidation());
				
				
				Map<String, Long> responseCounts = new TreeMap<String, Long>();
				
				if(props != null) {
					for(ValidationProperty prop : props) {
						responseCounts.put(prop.getValidationProperty(), Long.valueOf(0));
					}
				}


				
				if(res != null) {
					for(ScriptResultsAnalytics result : res) {

						if(result.getResponseType().equalsIgnoreCase("N/A") || result.getResponseType().equalsIgnoreCase("O/R")) {
							
							if(responseCounts.containsKey(result.getResponseType())) {
								responseCounts.put(result.getResponseType(), responseCounts.get(result.getResponseType()) + 1);
							}else {
								responseCounts.put(result.getResponseType(), Long.valueOf(1));
							}
							
						}else {
							if(question.getItemType().equals(Common.QUESTION_TYPE_TASK_LIST)) {
								
								List<TaskListRes> taskResults = this.getScriptResultsService().findTaskListRes(null, result.getId().getReturnId(), result.getId().getResOrderNo());
								
								for(TaskListRes taskres : taskResults) {
									
									if(taskres.getTask().getTaskCompleteCode().equals("TC")) {
										
										if(responseCounts.containsKey(taskres.getTask().getTaskDesc())) {
											responseCounts.put(taskres.getTask().getTaskDesc(), responseCounts.get(taskres.getTask().getTaskDesc()) + 1);
										}else {
											responseCounts.put(taskres.getTask().getTaskDesc(), Long.valueOf(1));
										}
										
									}
								}
								
							}else if(result.getSuppResultsSet() != null && result.getSuppResultsSet().size() > 0) {
								
								if(responseCounts.containsKey(result.getResponse())) {
									responseCounts.put(result.getResponse(), responseCounts.get(result.getResponse()) + 1);
								}else {
									responseCounts.put(result.getResponse(), Long.valueOf(1));
								}
								
								for(SuppResults supRes: result.getSuppResultsSet()) {
									
									if(responseCounts.containsKey(supRes.getId().getResponse())) {
										responseCounts.put(supRes.getId().getResponse(), responseCounts.get(supRes.getId().getResponse()) + 1);
									}else {
										responseCounts.put(supRes.getId().getResponse(), Long.valueOf(1));
									}
									
								}
								
							}else if(result.getResponse() != null) {
								if(responseCounts.containsKey(result.getResponse())) {
									responseCounts.put(result.getResponse(), responseCounts.get(result.getResponse()) + 1);
								}else {
									responseCounts.put(result.getResponse(), Long.valueOf(1));
								}

							}
						}
					}
				}

				
				resultSet.setResponseCounts(responseCounts);
				
			}else {
				resultSet.setCanShowBar(false);
				resultSet.setShowMetricSinglePickBar(false);
				Map<String, Map<String, Long>> responseTypeWithDateAndCount = this.getScriptResultsService().getScriptResultsTypeCountByDate(null, selectedResultSet.getWorkingVersion().getId(),
						this.getSearchCriteria().getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getFromDate())), 
						this.getSearchCriteria().getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getToDate())),
								 this.getSearchCriteria().getWorkgroupListAsString(),
								 question.getId().getSequenceNumber());
				
				if(responseTypeWithDateAndCount != null) {
					resultSet.setResponseCountsByDate(responseTypeWithDateAndCount.get("OK"));
					resultSet.setNaCountsByDate(responseTypeWithDateAndCount.get("N/A"));
					resultSet.setOrCountsByDate(responseTypeWithDateAndCount.get("O/R"));
				}
			}

		} catch (NumberFormatException | FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage("Exception Occured");
		}
		
		
	}
	
	public void loadTimeLineForValidationMetrics(AnalysisResultSet selectedResultSet, AnalysisResultSetResults resultSet) {
		
		try {
			
			if(resultSet.getResponseCountsByDate() == null) {
				Map<String, Map<String, Long>> responseTypeWithDateAndCount;
				
				responseTypeWithDateAndCount = this.getScriptResultsService().getScriptResultsTypeCountByDate(null, selectedResultSet.getWorkingVersion().getId(),
						this.getSearchCriteria().getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getFromDate())), 
						this.getSearchCriteria().getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getToDate())),
								 this.getSearchCriteria().getWorkgroupListAsString(),
								 resultSet.getQuestion().getId().getSequenceNumber());
				
				if(responseTypeWithDateAndCount != null) {
					resultSet.setResponseCountsByDate(responseTypeWithDateAndCount.get("OK"));
					resultSet.setNaCountsByDate(responseTypeWithDateAndCount.get("N/A"));
					resultSet.setOrCountsByDate(responseTypeWithDateAndCount.get("O/R"));
				}
			}
			
			//toggle the graph
			resultSet.setCanShowBar(true);
			resultSet.setShowMetricSinglePickBar(!resultSet.isShowMetricSinglePickBar());
			
			
		} catch (NumberFormatException | FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage("Exception Occured");
		}
		

		
	}
	
	public void deleteChart(AnalysisResultSet selectedResultSet, AnalysisResultSetResults resultSet) {
		selectedResultSet.getQuestionResults().remove(resultSet);
	}
	
	
	public void questionSelectedDataSet(AnalysisResultSet selectedResultSet, Item question) {
		//get all the answered for the selected questions seq/res order (seq/res order might be different per script?)
		boolean numeric = false;
		boolean graphable = false;
		try {
			if(//getSelectedQuestion().getItemType().equals(Common.QUESTION_TYPE_CALCULATION)
					//|| getSelectedQuestion().getItemType().equals(Common.QUESTION_TYPE_DATE)
					//|| getSelectedQuestion().getItemType().equals(Common.QUESTION_TYPE_TIME)
					//|| getSelectedQuestion().getItemType().equals(Common.QUESTION_TYPE_DECIMAL)
					question.getItemType().equals(Common.QUESTION_TYPE_LEVEL)
					|| question.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK)
					|| question.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK_RULE)
					|| question.getItemType().equals(Common.QUESTION_TYPE_CSV_LINE_ITEM)
					//|| getSelectedQuestion().getItemType().equals(Common.QUESTION_TYPE_NUMERIC)
					|| question.getItemType().equals(Common.QUESTION_TYPE_SINGLE_PICK)
					|| question.getItemType().equals(Common.QUESTION_TYPE_SINGLE_PICK_RULE)
					|| question.getItemType().equals(Common.QUESTION_TYPE_TASK_LIST)
					|| question.getItemType().equals(Common.QUESTION_TYPE_CONDITION)
					|| question.getItemType().equals(Common.QUESTION_TYPE_YESNO)) {
				graphable = true;
			}else if(question.getItemType().equals(Common.QUESTION_TYPE_CALCULATION)
					|| question.getItemType().equals(Common.QUESTION_TYPE_DECIMAL)
					|| question.getItemType().equals(Common.QUESTION_TYPE_LEVEL)
					|| question.getItemType().equals(Common.QUESTION_TYPE_NUMERIC)) {
				numeric = true;
			}
			
			if(!numeric && !graphable) {
				return;
			}
			
			AnalysisResultSetResults resultSet = null;
			
			if(selectedResultSet.getQuestionResults() != null) {
				for(AnalysisResultSetResults res: selectedResultSet.getQuestionResults()) {
					if(res.getQuestion().equals(question)) {
						resultSet = res;
						
						break;
					}
				}
			}

			if(resultSet == null) {
				resultSet = new AnalysisResultSetResults();
				resultSet.setQuestion(question);
				if(selectedResultSet.getQuestionResults() == null) {
					List<AnalysisResultSetResults> r = new ArrayList<AnalysisResultSetResults>();
					r.add(resultSet);
					selectedResultSet.setQuestionResults(r);
				}else {
					selectedResultSet.getQuestionResults().add(0, resultSet);
				}
			}else {
				//move it back up to the top
				selectedResultSet.getQuestionResults().remove(resultSet);
				selectedResultSet.getQuestionResults().add(0, resultSet);
			}
			//only keep the last 5
			if(selectedResultSet.getQuestionResults() != null && selectedResultSet.getQuestionResults().size() == 5) {
				selectedResultSet.getQuestionResults().remove(4);
			}
			
			
			resultSet.setNumericTotal(0);
			resultSet.setNumericAverage(0);
			resultSet.setNumericRangeLower(0);
			resultSet.setNumericRangeUpper(0);
			
			resultSet.setShowGraph(graphable);
			resultSet.setShowNumericGraph(numeric);

			Map<Integer, List<Item>> items = selectedResultSet.getItemsForItemInVersions(resultSet);

			List<Map<String, Long>> scriptResults = new ArrayList<Map<String, Long>>();
			
			Long countedResponses = Long.valueOf(0);
			
			for (Entry mapItem : items.entrySet()) {
				
				List<Integer> ids = new ArrayList<Integer>();
				for(Item item : (List<Item>)mapItem.getValue()) {
					ids.add(item.getId().getScriptId());
				}

				boolean multipick = question.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK) || question.getItemType().equals(Common.QUESTION_TYPE_MULTI_PICK_RULE);
				boolean tasklist = question.getItemType().equals(Common.QUESTION_TYPE_TASK_LIST);
				
				Map<String, Long> resCounts = this.getScriptResultsService().getScriptResultsCounts(null, ids, this.workGroupStrings,
						this.getSearchCriteria().getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getFromDate())), 
						this.getSearchCriteria().getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.getSearchCriteria().getToDate())),
								 (Integer)mapItem.getKey(), multipick, tasklist, this.getSearchCriteria().getScript().getScriptCode(), this.getSearchCriteria().isScopeToRecentAsset());
				
				if(resCounts != null) {
					scriptResults.add(resCounts);
				}
				
			}

			
			Map<String, Long> responseCounts = new TreeMap<String, Long>();
			
			for(Map<String, Long> resCounts : scriptResults) {
				
				for(Map.Entry<String, Long> entry : resCounts.entrySet()) {
					
					if(entry != null && entry.getValue() != null) {
						countedResponses += entry.getValue();
					}
					
					if(responseCounts.containsKey(entry.getKey())) {
						responseCounts.put(entry.getKey(), entry.getValue() + responseCounts.get(entry.getKey()));
					}else {
						responseCounts.put(entry.getKey(), entry.getValue());
					}

				}
				
			}

			int count = 0;
			Double total = Double.valueOf(0);
			
			resultSet.setResponseCount(countedResponses);
			resultSet.setResponseCounts(responseCounts);
			

			for(Integer i=resultSet.getQuestion().getId().getSequenceNumberInt(); i >= 0; i--) {
				
				if(selectedResultSet.getLatestScriptItems().containsKey(i)) {
					Item item = selectedResultSet.getLatestScriptItems().get(i);
					if(item != null && item.getItemType().equals(Common.QUESTION_TYPE_HEADING)) {
						resultSet.setHeading(item.getItemText());
						break;
					}
				}

			}

			
		} catch (NumberFormatException e) {
			MessageHelper.setGlobalWarnMessage("Exception Occured");
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage("Exception Occured");
		}
		//generate dataset for graphs and output
		//graphs needed include, show number of times answered with same question, show date time line of answers???
		//[respoonse, {count, [dates]]

		//multipick and tasklist need to be handled differently
	}
	
	public TreeNode getScriptRoot() {
		return scriptRoot;
	}

	public void setScriptRoot(TreeNode scriptRoot) {
		this.scriptRoot = scriptRoot;
	}

	public TreeNode getSelectedScriptNode() {
		return selectedScriptNode;
	}

	public void setSelectedScriptNode(TreeNode selectedScriptNode) {
		this.selectedScriptNode = selectedScriptNode;
		this.updateSelectedScripts();
	}

	public AnalysisSearchCriteria getUndoSearchCriteria() {
		if(this.searchingVersionAnalysis) {
			return this.undoSearchVersionCriteria;
		}else {
			return this.undoSearchCriteria;
		}
	}

	public AnalysisSearchCriteria getSearchCriteria() {
		if(this.searchingVersionAnalysis) {
			return this.searchVersionCriteria;
		}else {
			return this.searchCriteria;
		}
	}
	

	public void setCurrentSearchCriteria(AnalysisSearchCriteria searchCriteria) {
		if(this.searchingVersionAnalysis) {
			this.searchVersionCriteria = searchCriteria;
		}else {
			this.searchCriteria = searchCriteria;
		}
		
	}

	public String getTitle() {
		if(searchingVersionAnalysis) {
			return "Script Metrics";
		}
		return "Script Result Data Set Analytics";
	}

	public List<AnalysisResultSet> getDataResultSet() {
		return dataResultSet;
	}

	public void setDataResultSet(List<AnalysisResultSet> dataResultSet) {
		this.dataResultSet = dataResultSet;
	}

	public Integer getTabActiveIndex() {
		return tabActiveIndex;
	}

	public void setTabActiveIndex(Integer tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	public Item getSelectedQuestion() {
		return selectedQuestion;
	}

	public void setSelectedQuestion(Item selectedQuestion) {
		this.selectedQuestion = selectedQuestion;
	}

	public boolean isAllWorkgroupsSelected() {
		return allWorkgroupsSelected;
	}

	public void setAllWorkgroupsSelected(boolean allWorkgroupsSelected) {
		this.allWorkgroupsSelected = allWorkgroupsSelected;
	}

	public TreeNode getWorkgroupRoot() {
		return workgroupRoot;
	}

	public void setWorkgroupRoot(TreeNode workgroupRoot) {
		this.workgroupRoot = workgroupRoot;
	}

	public TreeNode[] getSelectedWorkgroupNodes() {
		return selectedWorkgroupNodes;
	}

	public void setSelectedWorkgroupNodes(TreeNode[] selectedWorkgroupNodes) {
		this.selectedWorkgroupNodes = selectedWorkgroupNodes;
	}
	
	public void loadSelectedWorkgroups(){
		
		_logger.debug(">>> loadSelectedWorkgroups");
		try {
			this.setWorkgroupRoot(new CheckboxTreeNode("Root", null));

			// make a map for the workgroup categories
			Map<Integer, CheckboxTreeNode> workgroupCats = new TreeMap<Integer, CheckboxTreeNode>();

			List<HPCWorkGroups> workgroupList = fetchWorkgroups();

			for (HPCWorkGroups w : workgroupList){

				// check if we have created a parent node for the current workgroup's category
				CheckboxTreeNode cat = workgroupCats.get(w.getWgCatId());

				// if not, create a new parent node for the current workgroup's category
				if (cat == null){
					cat = new CheckboxTreeNode(w.getHpcWgCat().getWgCatDesc(), this.getWorkgroupRoot());
					cat.setExpanded(true);
					workgroupCats.put(w.getWgCatId(), cat);
				}

				// create a node for the current script, parenting it to the node we just retrieved/created
				CheckboxTreeNode node = new CheckboxTreeNode(w, cat); 
				
				if(this.getSearchCriteria().getWorkgroups() != null) {
					for(HPCWorkGroups group : this.getSearchCriteria().getWorkgroups()) {
						if(group.getId().equals(w.getId())) {
							node.setSelected(true, true);
							break;
						}
					}

				}
			}
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedWorkgroups Unknown error occurred" + e.getMessage());
		}
			
		_logger.debug("<<< loadSelectedWorkgroups");
	}
	
	public List<HPCWorkGroups> fetchWorkgroups() throws UserNotFoundException, FRInstanceException {
		
		_logger.debug(">>> fetchWorkgroups");
		
		workgroups = getUserService().getAccessibleWorkgroups(null, getUsername());
		
		_logger.debug("<<< fetchWorkgroups");
		
		return workgroups;
	}
	
	/*public Map<HPCWorkGroups, List<HPCUsers>> getWorkgroupMap() throws UserNotFoundException, FRInstanceException {

		_logger.debug(">>> getWorkgroupMap");

		if (workgroupMap == null){
			workgroupMap = getUserService().getFDMHPCWorkgroupToUserMap(null,getUsername(), true);
		}

		_logger.debug("<<< getWorkgroupMap");

		return workgroupMap;
	}*/
	
	public void clearSelectedWorkgroups() {
		
		_logger.debug(">>> clearSelectedWorkgroups");
		
		if(this.getSelectedWorkgroupNodes() != null) {
			for (TreeNode tn : this.getSelectedWorkgroupNodes()){
				tn.setSelected(false);
			}
		}
		
		this.setSelectedWorkgroupNodes(null);
		
		_logger.debug("<<< clearSelectedWorkgroups");
	}
	
	public void updateSelectedWorkgroups(){
		
		_logger.debug(">>> updateSelectedWorkgroups");
		
		List<TreeNode> selectedWorkgroupNodeList = Arrays.asList(this.getSelectedWorkgroupNodes());

		this.getSearchCriteria().getWorkgroups().clear();

		for (TreeNode tn : selectedWorkgroupNodeList){
			// discard the parent nodes
			if (tn.getData() instanceof HPCWorkGroups)
				this.getSearchCriteria().getWorkgroups().add((HPCWorkGroups)tn.getData());
		}
		
		if(this.getSearchCriteria().getWorkgroups().size() == 0) {
			this.allWorkgroupsSelected = true;
		}else {
			this.allWorkgroupsSelected = false;
		}
		
		_logger.debug("<<< updateSelectedWorkgroups");
	}

	public boolean isSearchingVersionAnalysis() {
		return searchingVersionAnalysis;
	}

	public void setSearchingVersionAnalysis(boolean searchingVersionAnalysis) {
		this.searchingVersionAnalysis = searchingVersionAnalysis;
	}
	
	public void initSearchBarIsSearchingVersion(boolean searchingVersionAnalysis) {
		this.setSearchingVersionAnalysis(searchingVersionAnalysis);
		this.loadSelectedScripts();
	}
	
	
}
