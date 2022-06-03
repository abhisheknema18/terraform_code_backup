package com.amtsybex.fieldreach.fdm.search;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.application.ViewExpiredException;
import javax.faces.component.EditableValueHolder;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.MaxResultsExceededException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.PersonalViews;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.Script;
import com.amtsybex.fieldreach.backend.model.ViewDefn;
import com.amtsybex.fieldreach.backend.model.pk.ViewDefnId;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.property.PortalPropertyUtil;
import com.amtsybex.fieldreach.fdm.util.DateUtil;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.utils.impl.Common;


@Named
@WindowScoped
public class Search extends PageCodebase implements Serializable {

	private static final long serialVersionUID = -2891420421941388255L;

	private static Logger _logger = LoggerFactory.getLogger(Search.class.getName());
	
	@Inject
	transient SearchResults searchResults;
	
	@Inject
	transient private PortalPropertyUtil portalPropertyUtil;	


	private List<String>resultApprovalStatuses;
	
	//PRB0050354 - MC - undo on cancel of edit criteria
	private SearchCriteria searchCriteria;
	private SearchCriteria undoSearchCriteria;
	
	//FDE060 - KN - ManagePersonalViews
	private boolean manageViewPage;
	private SearchCriteria personalViewSearchCriteria;

	private boolean newPersonalView;
	private PersonalViews currentPersonalViews;
	private PersonalViews selectedEditPersonalViews;
	
	
	//FDP1379 - MC - prerenderview variables
	private String directequipNo; 
	private String directWorkOrderNo; 
	private String directMessage; 
	private String directMessageSummary; 
	
	//32370 - KN - return id param variable
	private Integer directReturnId;
	

	private TreeNode scriptRoot;  
	private TreeNode[] selectedScriptNodes; 


	private TreeNode workgroupRoot;  
	private TreeNode[] selectedWorkgroupNodes; 


	private TreeNode userRoot;  
	private TreeNode[] selectedUserNodes; 


	private TreeNode resultRoot;  
	private TreeNode[] selectedResultNodes; 
	
	private PersonalViews selectedPersonalViews;
	private List<PersonalViews> personalViewList;
	
	private boolean personalViewMenuSearch;

	private int activeTab = 0;

	
	@PostConstruct
	public void init() {
		this.searchCriteria = new SearchCriteria();
		this.undoSearchCriteria = new SearchCriteria();
		
		this.personalViewSearchCriteria = new SearchCriteria();
		this.currentPersonalViews =  new PersonalViews();

		resultApprovalStatuses = portalPropertyUtil.props().getApproval().getResult();
		loadPersonalViewList();
	}
	
	private void loadPersonalViewList() {
		try {
			
			this.personalViewList = this.getUserService().getPersonalViews(null, getUsername());

			setPersonalViewMenuSearch(false);
			for(PersonalViews menuList : this.personalViewList) {
				if(menuList.getSearchMenu()!= null && menuList.getSearchMenu() == 1) {
					setPersonalViewMenuSearch(true);
					break;
				}
			}
				
		} catch (FRInstanceException e) {
			throw new ViewExpiredException();
		}
	}
	
	public void cancel() {
		this.searchCriteria = new SearchCriteria(this.undoSearchCriteria);
	}
	
	public SearchCriteria getCurrentWorkingSearchCriteria() {
		
		if (this.isManageViewPage()) {
			return personalViewSearchCriteria;
			
		} else {
			return searchCriteria;
			
		}
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
				if(this.manageViewPage) {
					
					if(this.personalViewSearchCriteria.getScripts() != null) {
						for(Script script: this.personalViewSearchCriteria.getScripts()) {
							if(script.getScriptCode().equalsIgnoreCase(s.getScriptCode())){
								node.setSelected(true, true);
								break;
							}
							
						}
					}
					
				}else {

					if(this.searchCriteria.getScripts() != null && this.searchCriteria.getScripts().contains(s)) {
						node.setSelected(true, true);
					}
				}

			}
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedScripts Unknown error occurred" + e.getMessage());
		}

		_logger.debug("<<< loadSelectedScripts");

	}
	
	/**
	 * parse the array of selected nodes into a list for the corresponding drop down menu
	 */
	public void updateSelectedScripts(){

		_logger.debug(">>> updateSelectedScripts");
		SearchCriteria criteria = getCurrentWorkingSearchCriteria();
		
		criteria.getScripts().clear();
			
		for (TreeNode tn :this.getSelectedScriptNodes()){
			// discard the parent nodes
			if (tn.getData() instanceof Script)
				criteria.getScripts().add((Script)tn.getData());
		}
		
		
		_logger.debug("<<< updateSelectedScripts");
	}
	
	public void clearSelectedScripts(){

		_logger.debug(">>> clearSelectedScripts");		
		
		if(this.getSelectedScriptNodes() != null) {
			for (TreeNode tn : this.getSelectedScriptNodes()){
				tn.setSelected(false);
			}
		}

		this.setSelectedScriptNodes(null);
		
		_logger.debug("<<< clearSelectedScripts");
	}

	/**
	 * method for building the tree of workgroups
	 * 
	 * @throws UserNotFoundException
	 * @throws FRInstanceException 
	 */
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
				
				if(this.manageViewPage) {
					
					if(this.personalViewSearchCriteria.getWorkgroups() != null) {
						
						for(HPCWorkGroups workgrps: this.personalViewSearchCriteria.getWorkgroups()) {
							if(workgrps.getWorkgroupCode().equalsIgnoreCase(w.getWorkgroupCode())){
								node.setSelected(true, true);
								break;
							}
							
						}
						
					}
					
				}else {
				
					if(this.searchCriteria.getWorkgroups() != null && this.searchCriteria.getWorkgroups().contains(w)) {
						node.setSelected(true, true);
					}
				}
			}
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedWorkgroups Unknown error occurred" + e.getMessage());
		}
			
		_logger.debug("<<< loadSelectedWorkgroups");
	}

	
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
	
	/**
	 * parse the array of selected nodes into a list for the corresponding drop down menu
	 */
	public void updateSelectedWorkgroups(){
		
		_logger.debug(">>> updateSelectedWorkgroups");
		
		SearchCriteria criteria = getCurrentWorkingSearchCriteria();
		
		List<TreeNode> selectedWorkgroupNodeList = Arrays.asList(this.getSelectedWorkgroupNodes());
		
	
			criteria.getWorkgroups().clear();
			
			for (TreeNode tn : selectedWorkgroupNodeList){
				// discard the parent nodes
				if (tn.getData() instanceof HPCWorkGroups)
					criteria.getWorkgroups().add((HPCWorkGroups)tn.getData());
			}
		
		
		_logger.debug("<<< updateSelectedWorkgroups");
	}

	/**
	 * method for building the tree of users
	 * 
	 * @throws UserNotFoundException
	 * @throws FRInstanceException 
	 */
	public void loadSelectedUsers(){
		
		_logger.debug(">>> loadSelectedUsers");
		
		try {
			this.setUserRoot(new CheckboxTreeNode("Root", null));

			// make a map for the workgroup categories
			Map<String, CheckboxTreeNode> workgroupCats = new TreeMap<String, CheckboxTreeNode>();

			List<HPCUsers> userList = fetchUsers();

			for (HPCUsers u : userList){

				// check if we have created a parent node for the current workgroup's category
				CheckboxTreeNode cat = workgroupCats.get(u.getWorkgroupCode());

				// if not, create a new parent node for the current workgroup's category
				if (cat == null){
					HPCWorkGroups group = getUserService().findHPCWorkGroup(null, u.getWorkgroupCode());

					cat = new CheckboxTreeNode(group.getWorkgroupDesc(), this.getUserRoot());
					cat.setExpanded(true);
					workgroupCats.put(u.getWorkgroupCode(), cat);
				}

				// create a node for the current script, parenting it to the node we just retrieved/created
				CheckboxTreeNode node = new CheckboxTreeNode(u, cat); 
				
				if(this.manageViewPage) {
					
					if(this.personalViewSearchCriteria.getUsers() != null) {
						
						for(HPCUsers usr: this.personalViewSearchCriteria.getUsers()) {
							if(usr.getId().getUserCode().equalsIgnoreCase(u.getId().getUserCode())){
								node.setSelected(true, true);
								break;
							}
						}
						
					}
					
				}else {
				
					if(this.searchCriteria.getUsers() != null && this.searchCriteria.getUsers().contains(u)) {
						node.setSelected(true, true);
					}
				}
			}
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedUsers Unknown error occurred" + e.getMessage());
		}
		_logger.debug("<<< loadSelectedUsers");

	}

	/**
	 * parse the array of selected nodes into a list for the corresponding drop down menu
	 */
	public void updateSelectedUsers() {

		_logger.debug(">>> updateSelectedUsers");

		SearchCriteria criteria = getCurrentWorkingSearchCriteria();

		List<TreeNode> selectedUserNodeList = Arrays.asList(this.getSelectedUserNodes());

		criteria.getUsers().clear();

		for (TreeNode tn : selectedUserNodeList) {
			// discard the parent nodes
			if (tn.getData() instanceof HPCUsers)
				criteria.getUsers().add((HPCUsers) tn.getData());
		}
	
		_logger.debug("<<< updateSelectedUsers");

	}
	
	public void clearSelectedUsers(){

		_logger.debug(">>> clearSelectedUsers");
		
				
		if(this.getSelectedUserNodes() != null) {
			for (TreeNode tn : this.getSelectedUserNodes()){
				tn.setSelected(false);
			}
		}
		
		this.setSelectedUserNodes(null);
		
		_logger.debug("<<< clearSelectedUsers");
	}

	/**
	 * method for building the tree of statuses
	 * 
	 * @throws UserNotFoundException
	 * @throws FRInstanceException 
	 */
	public void loadSelectedResults(){
		
		_logger.debug(">>> loadSelectedResults");

		try {
			this.setResultRoot(new CheckboxTreeNode("Root", null));
			List<String> statusList = fetchResultStatuses();
			for (String s : statusList){
				CheckboxTreeNode node = new CheckboxTreeNode(s, this.getResultRoot()); 
				
				if(this.manageViewPage) {
					
					if(this.personalViewSearchCriteria.getResultStatuses() != null && this.personalViewSearchCriteria.getResultStatuses().contains(s)) {
						node.setSelected(true, true);
					}
					
				}else {
	
					if(this.searchCriteria.getResultStatuses() != null && this.searchCriteria.getResultStatuses().contains(s)) {
						node.setSelected(true, true);
					}
				}
			}
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedResults Unknown error occurred" + e.getMessage());
		}

		_logger.debug("<<< loadSelectedResults");
	}

	/**
	 * parse the array of selected nodes into a list for the corresponding drop down menu
	 */
	public void updateSelectedResultStatuses(){
		
		_logger.debug(">>> updateSelectedResultStatuses");
		
		SearchCriteria criteria = getCurrentWorkingSearchCriteria();
		
		List<TreeNode> selectedResultNodeList = Arrays.asList(this.getSelectedResultNodes());
		
	
			criteria.getResultStatuses().clear();

			for (TreeNode tn : selectedResultNodeList){
				criteria.getResultStatuses().add((String)tn.getData());
			}
			
			
		
		_logger.debug("<<< updateSelectedResultStatuses");
	}
	
	public void clearSelectedResults(){

		_logger.debug(">>> clearSelectedResults");
		
		if(this.getSelectedResultNodes() != null) {
			for (TreeNode tn : this.getSelectedResultNodes()){
				tn.setSelected(false);
			}
		}
		
		this.setSelectedResultNodes(null);
		
		
		_logger.debug("<<< clearSelectedResults");
	}



	public Map<HPCWorkGroups, List<HPCUsers>> getWorkgroupMap() throws UserNotFoundException, FRInstanceException {

		_logger.debug(">>> getWorkgroupMap");
		
		Map<HPCWorkGroups, List<HPCUsers>> workgroupMap;

			workgroupMap = getUserService().getFDMHPCWorkgroupToUserMap(null,getUsername(), true);

		_logger.debug("<<< getWorkgroupMap");

		return workgroupMap;
	}


	public List<Script> fetchScripts() throws UserNotFoundException, FRInstanceException {
		return getScriptService().getFDMScriptList(null,getUsername());
	}




	/**
	 * get a list of workgroups from the workgroup/user map
	 * @throws FRInstanceException 
	 */
	public List<HPCWorkGroups> fetchWorkgroups() throws UserNotFoundException, FRInstanceException {
		
		_logger.debug(">>> fetchWorkgroups");
		
		List<HPCWorkGroups> fetchedWorkgroups = new ArrayList<HPCWorkGroups>();
		Map<HPCWorkGroups, List<HPCUsers>> map = getWorkgroupMap();

		if (map != null){
			Iterator<HPCWorkGroups> workgroupIterator = map.keySet().iterator();
			while(workgroupIterator.hasNext()){
				fetchedWorkgroups.add(workgroupIterator.next());
			}
		}

		_logger.debug("<<< fetchWorkgroups");
		
		return fetchedWorkgroups;
	}




	/**
	 * get a list of users from the workgroup/user map
	 * @throws FRInstanceException 
	 */
	public List<HPCUsers> fetchUsers() throws UserNotFoundException, FRInstanceException {
		
		_logger.debug(">>> fetchUsers");
		
		List<HPCUsers> fetchedUsers = new ArrayList<HPCUsers>();
		Map<HPCWorkGroups, List<HPCUsers>> map = this.getWorkgroupMap();

		Iterator<List<HPCUsers>> workgroupIterator = map.values().iterator();
		while(workgroupIterator.hasNext()){
			List<HPCUsers> userList = workgroupIterator.next();
			if (userList != null){
				for (HPCUsers u : userList)
					fetchedUsers.add(u);
			}
		}
		
		_logger.debug("<<< fetchUsers");
		
		return fetchedUsers;
	}

	public List<String> fetchResultStatuses() throws UserNotFoundException, FRInstanceException {
		return getScriptResultsService().getFDMResultStatusList(null, getUsername());
	}

	public String getDirectequipNo() {
		return directequipNo;
	}

	public void setDirectequipNo(String directequipNo) {
		this.directequipNo = directequipNo;
	}

	public String getDirectWorkOrderNo() {
		return directWorkOrderNo;
	}

	public void setDirectWorkOrderNo(String directWorkOrderNo) {
		this.directWorkOrderNo = directWorkOrderNo;
	}

	public Integer getDirectReturnId() {
		return directReturnId;
	}

	public void setDirectReturnId(Integer directReturnId) {
		this.directReturnId = directReturnId;
	}



	public int getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(int activeTab) {
		this.activeTab = activeTab;
	}

	
	/**
	 * FDE049 - MC
	 * @param workOrderNo
	 * @throws MaxResultsExceededException
	 * @throws FRInstanceException
	 */
	public void searchByWorkOrder(String workOrderNo) {
		
		_logger.debug(">>> searchByWorkOrder");
		
		try {
			searchResults.reset();
			
			List<ReturnedScripts> returnedScripts = getScriptResultsService().getScriptResultsByWorkOrderNo(null, workOrderNo);
				
			if (returnedScripts != null && returnedScripts.size() > 0){
				searchResults.init(returnedScripts);
			}
			
			searchResults.setReturnToWork(true);
		
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("searchByWorkOrder Unknown error occurred" + e.getMessage());
		}
		_logger.debug("<<< searchByWorkOrder");
	}
	
	public String searchApprovals() {
		
		this.reset();
		PrimeFaces.current().multiViewState().clearAll();
		searchResults.reset();

		searchResults.setApprovalMode(true);

		List<ReturnedScripts> returnedScripts = null;

		try {
			returnedScripts = getScriptResultsService().getScriptResults(	null, getUsername(), 
					null, 
					null,
					null,
					null, 
					null, 
					resultApprovalStatuses, 
					null, 
					null, null);
			
			if (returnedScripts != null && returnedScripts.size() > 0){
				searchResults.init(returnedScripts);
				activeTab = 1;
			}
			else{
				// if no results are found, display a warning message stating so
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_results_found"));
				activeTab = 0;
			}

		} catch (MaxResultsExceededException e) {
			// if too many results are returned, tell the user to reduce search options
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_results_too_many_results_found_label"));
			activeTab = 0;
		} catch (UserNotFoundException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
			activeTab = 0;
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			activeTab = 0;
			_logger.error("search Unknown error occurred" + e.getMessage());
		}
		
		return "search";
	}

	
	public void searchAndHideFilterOnDisplay() {
		this.searchResults.reset();
		this.search();
		this.activeTab = 0;
		this.reset();
	}
	/**
	 * main search method
	 * @throws FRInstanceException 
	 */
	public String search(){

		_logger.debug(">>> search");
		
		try {
			
			

			this.undoSearchCriteria = new SearchCriteria(this.searchCriteria);
			
			
			List<ReturnedScripts> returnedScripts = null;

			// if no search terms are entered, find all scripts
			if (emptySearchTerms()){
				
				try {
					returnedScripts = getScriptResultsService().getAllScriptResults(null, getUsername());
				} catch (MaxResultsExceededException e) {
					// if too many results are returned, tell the user to reduce search options
					MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_results_too_many_results_found_label"));
					return null;
				} catch (UserNotFoundException e) {
					MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
					return null;
				}
			}else {
				// if the reference number is blank, search using the detail fields
				//if (this.searchCriteria.getReferenceNumber() == null || "".equals(this.searchCriteria.getReferenceNumber().trim())){

					if(this.searchCriteria.getFromDate() == null ^ this.searchCriteria.getToDate() == null){
						
						MessageHelper.setErrorMessage(null, Properties.get("fdm_search_both_dates_need_set"));
						FacesContext.getCurrentInstance().validationFailed();
						PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
						try {
							EditableValueHolder component = (EditableValueHolder) FacesContext.getCurrentInstance().getViewRoot().findComponent("resultSearchTabs:searchForm:fromDate");
							component.setValid(false);
							component = (EditableValueHolder) FacesContext.getCurrentInstance().getViewRoot().findComponent("resultSearchTabs:searchForm:toDate");
							component.setValid(false);
						}catch(Exception e) {}
						
						return null;
					}

					if (this.searchCriteria.getFromDate() != null && this.searchCriteria.getToDate() != null) {
						if (this.searchCriteria.getFromDate().after(this.searchCriteria.getToDate())) {
							_logger.debug("<<< search from date greater than to date");
							MessageHelper.setErrorMessage(null, Properties.get("fdm_search_from_before_to"));
							FacesContext.getCurrentInstance().validationFailed();
							PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
							try {
								EditableValueHolder component = (EditableValueHolder) FacesContext.getCurrentInstance().getViewRoot().findComponent("resultSearchTabs:searchForm:fromDate");
								component.setValid(false);
								component = (EditableValueHolder) FacesContext.getCurrentInstance().getViewRoot().findComponent("resultSearchTabs:searchForm:toDate");
								component.setValid(false);
							}catch(Exception e) {}
							// throw new Exception("Planned Start Date after Required Finish Date");
							return null;
						}
					}
				
					List<String> scriptCodes = null;
					List<String> groupCodes = null;
					List<String> userCodes = null;
					List<String> statuses = null;

					// build a list of script codes from the list of script objects
					if (this.searchCriteria.getScripts().size() > 0){
						scriptCodes = new ArrayList<String>();
						for (Script s : this.searchCriteria.getScripts()){
							scriptCodes.add(s.getScriptCode());
						}
					}

					// build a list of workgroup codes from the list of workgroup objects
					if (this.searchCriteria.getWorkgroups().size() > 0){
						groupCodes = new ArrayList<String>();
						for (HPCWorkGroups w : this.searchCriteria.getWorkgroups()){
							groupCodes.add(w.getWorkgroupCode());
						}
					}

					// build a list of user codes from the list of user objects
					if (this.searchCriteria.getUsers().size() > 0){
						userCodes = new ArrayList<String>();
						for (HPCUsers u : this.searchCriteria.getUsers()){
							userCodes.add(u.getId().getUserCode());
						}
					}

					// copy the list of statuses
					if (this.searchCriteria.getResultStatuses().size() > 0){
						statuses = this.searchCriteria.getResultStatuses();
					}

					try {
						returnedScripts = getScriptResultsService().getScriptResults(	null, getUsername(), 
								null, 
								(this.searchCriteria.getFromDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.searchCriteria.getFromDate()))), 
								(this.searchCriteria.getFromDate() == null ? null : Integer.valueOf(new SimpleDateFormat("HHmm").format(this.searchCriteria.getFromDate()))), 
								(this.searchCriteria.getToDate() == null ? null : Integer.valueOf(DateUtil.formatDate(this.searchCriteria.getToDate()))), 
								scriptCodes, 
								statuses, 
								userCodes, 
								groupCodes, null);

					} catch (MaxResultsExceededException e) {
						// if too many results are returned, tell the user to reduce search options
						MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_results_too_many_results_found_label"));
						return null;
					} catch (UserNotFoundException e) {
						MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
						return null;
					}catch(Exception e) {
						MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
						_logger.error("search Unknown error occurred" + e.getMessage());
						return null;
					}
				//}
				//else{
					// if the reference number isn't blank, search using it, ignoring all other search fields
					//PRB0050354 - MC - move this to its own function
					//this.searchReference();
				//}
			}


			// if results are found, initialise the search results bean, and switch tabs
			if (returnedScripts != null && returnedScripts.size() > 0){
				//need to reset table filter in case filter was set last time in a search
				//cant reset it the same as work order list in clearFilters because
				//this would break the work order filter if navigation to script results
				//within work order.
				PrimeFaces.current().multiViewState().clearAll();
				searchResults.reset();
				searchResults.init(returnedScripts);
				activeTab = 1;
			}
			else{
				// if no results are found, display a warning message stating so
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_results_found"));
				return null;
			}

		}catch(Exception e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
			_logger.error("search Unknown error occurred" + e.getMessage());
			return null;
		}
		_logger.debug("<<< search");

		return "search";

	}
	
	public void initiateEditPersonalView() throws UserNotFoundException, FRInstanceException {
		
		newPersonalView = false;
		this.currentPersonalViews = this.selectedEditPersonalViews;
		
		this.personalViewSearchCriteria = new SearchCriteria();
		
		this.personalViewSearchCriteria.setViewName(this.currentPersonalViews.getViewName());
		this.personalViewSearchCriteria.setViewDesc(this.currentPersonalViews.getViewDesc());
		
		if(this.currentPersonalViews.getSearchMenu() !=null && this.currentPersonalViews.getSearchMenu() == 1) {
			this.personalViewSearchCriteria.setSearchOnMenu(true);
		}
		
		if(this.currentPersonalViews.getViewDefn() != null) {
			
			for(ViewDefn searchValue  : this.currentPersonalViews.getViewDefn()) {
				
				if(searchValue.getId().getFieldName().equalsIgnoreCase("SCRIPTCODEID")) {
					
					Integer scriptId = Integer.parseInt(searchValue.getId().getFieldValue());
					personalViewSearchCriteria.getScripts().add(getScriptService().findScript(null, scriptId));
									
				}else if (searchValue.getId().getFieldName().equalsIgnoreCase("WORKGROUP")) {
					
					String workgroupCode = searchValue.getId().getFieldValue();
					personalViewSearchCriteria.getWorkgroups().add(getUserService().findHPCWorkGroup(null,  workgroupCode));
									
				}else if (searchValue.getId().getFieldName().equalsIgnoreCase("RESULTSTATUS")) {
					
					personalViewSearchCriteria.getResultStatuses().add(searchValue.getId().getFieldValue());
									
				}else if (searchValue.getId().getFieldName().equalsIgnoreCase("USERCODE")) {

					personalViewSearchCriteria.getUsers().add(getUserService().findHPCUsersByUserCode(null, searchValue.getId().getFieldValue()).get(0));
										
				}else if (searchValue.getId().getFieldName().equalsIgnoreCase("DATEFROM")) {
					String fromDate = getDateFormat(searchValue.getId().getFieldValue());
					this.personalViewSearchCriteria.setPersonalViewFromDate(fromDate);
					
				}else if (searchValue.getId().getFieldName().equalsIgnoreCase("DATETO")) {
					String toDate = getDateFormat(searchValue.getId().getFieldValue());
					this.personalViewSearchCriteria.setPersonalViewToDate(toDate);
					
				}
			}
			
		}

	}
	
	public String getDateFormat(String strDate) {
		String dateFormat;
		if(!strDate.toUpperCase().contains("TODAY")) {
			if(strDate.contains("-")) {
				return strDate.replaceAll("-", "/");
			}
			dateFormat = strDate.substring(6) + "/" +strDate.substring(4, 6)+ "/" +strDate.substring(0, 4).toString();
			
		}else {
			return strDate;
		}
		
		return dateFormat;
	}
	
	public void initiateNewPersonalView() {
		newPersonalView = true;
		this.personalViewSearchCriteria = new SearchCriteria();
		this.currentPersonalViews = new PersonalViews();
	}
	
	public void resetManagePersonalView() {
		
		if(newPersonalView) {
			this.personalViewSearchCriteria.setViewName("");
			this.personalViewSearchCriteria.setViewDesc("");
		}
		this.personalViewSearchCriteria.reset();
	}
	
	public void updatePersonalViews() throws FRInstanceException, UserNotFoundException {
		
		try {
			if(this.personalViewSearchCriteria.getViewName().getBytes("UTF-8").length > 30){
				MessageHelper.setErrorMessage("messages", "The View Name size is more than the allowed limit");
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return;
			}
		
		
			if(this.personalViewSearchCriteria.getViewDesc().getBytes("UTF-8").length > 50){
				MessageHelper.setErrorMessage("messages", "The View Description size is more than the allowed limit");
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return;
			}
		
		} catch (UnsupportedEncodingException e) {
			MessageHelper.setErrorMessage("messages", "The View Name/Description size is more than the allowed limit");
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			return;
		}
		
		Set<ViewDefn> viewDefList = new HashSet<ViewDefn>();
		for(Script script : this.personalViewSearchCriteria.getScripts()) {
			viewDefList.add(createViewDefn("SCRIPTCODEID",script.getId().toString()));
		}
			
		for(HPCWorkGroups wrg : this.personalViewSearchCriteria.getWorkgroups()) {
			viewDefList.add(createViewDefn("WORKGROUP",wrg.getWorkgroupCode()));
		}
			
		for(HPCUsers usr : this.personalViewSearchCriteria.getUsers()) {
			viewDefList.add(createViewDefn("USERCODE",usr.getId().getUserCode()));
		}
			
		for(String resultStatus : this.personalViewSearchCriteria.getResultStatuses()) {
			viewDefList.add(createViewDefn("RESULTSTATUS",resultStatus));
		}
		
		if (this.personalViewSearchCriteria.isSearchOnMenu()) {
			this.currentPersonalViews.setSearchMenu(1);
		} else {
			this.currentPersonalViews.setSearchMenu(0);
		}
			
		boolean invalidDate = true;
		Integer fromDate= 0;
		Integer toDate= 0;
		List<String> formatStrings = Arrays.asList("dd/MM/yyyy", "d/M/yyyy", "dd/M/yyyy", "d/MM/yyyy");
		
		if (this.personalViewSearchCriteria.getPersonalViewFromDate() != null && !this.personalViewSearchCriteria.getPersonalViewFromDate().equalsIgnoreCase("")) {

			if (this.personalViewSearchCriteria.getPersonalViewFromDate().toUpperCase().startsWith("TODAY")) {
				try {
					int indx= this.personalViewSearchCriteria.getPersonalViewFromDate().indexOf("-");
					
					if(this.personalViewSearchCriteria.getPersonalViewFromDate().trim().equalsIgnoreCase("TODAY")) {
						invalidDate= false;
					}else if(indx > 0){
						Integer.parseInt(this.personalViewSearchCriteria.getPersonalViewFromDate().substring(indx+1).trim());
						invalidDate= false;
					}else {
						invalidDate= true;
					}
					
					fromDate = Integer.parseInt(Common.processToday(this.personalViewSearchCriteria.getPersonalViewFromDate()));
					viewDefList.add(createViewDefn("DATEFROM",this.personalViewSearchCriteria.getPersonalViewFromDate()));
				} catch (Exception e) {
					invalidDate = true;
				}

			} else {
				Date date = null;
				try {

					for (String formatString : formatStrings) {
						SimpleDateFormat sdf = new SimpleDateFormat(formatString);
						date = sdf.parse(this.personalViewSearchCriteria.getPersonalViewFromDate());
						if (this.personalViewSearchCriteria.getPersonalViewFromDate().trim().equals(sdf.format(date))) {
							fromDate = Integer.parseInt((DateUtil.formatDate(date)));
							invalidDate = false;
							viewDefList.add(createViewDefn("DATEFROM", fromDate.toString().trim()));
							break;
						}
						invalidDate = true;
					}
				} catch (Exception ex) {
					invalidDate = true;
				}
				
			}

			if (invalidDate) {
				MessageHelper.setErrorMessage("messages", "Invalid date format");
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return;
			}

		} else {
			if (this.personalViewSearchCriteria.getPersonalViewToDate() != null && !this.personalViewSearchCriteria.getPersonalViewToDate().equalsIgnoreCase("")) {
				MessageHelper.setErrorMessage("messages", "For Date searches both Date From and Date To must be specified.");
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return;
			}
			
		}
			
		if (this.personalViewSearchCriteria.getPersonalViewToDate() != null && !this.personalViewSearchCriteria.getPersonalViewToDate().equalsIgnoreCase("")) {

			if (this.personalViewSearchCriteria.getPersonalViewToDate().toUpperCase().startsWith("TODAY")) {
				try {
					
					int indx= this.personalViewSearchCriteria.getPersonalViewToDate().indexOf("-");
					
					if(this.personalViewSearchCriteria.getPersonalViewToDate().trim().equalsIgnoreCase("TODAY")) {
						invalidDate= false;
					}else if(indx > 0){
						Integer.parseInt(this.personalViewSearchCriteria.getPersonalViewToDate().substring(indx+1).trim());
						invalidDate= false;
					}else {
						invalidDate= true;
					}
					
					toDate = Integer.parseInt(Common.processToday(this.personalViewSearchCriteria.getPersonalViewToDate()));
				
					viewDefList.add(createViewDefn("DATETO",this.personalViewSearchCriteria.getPersonalViewToDate()));
				} catch (Exception e) {
					invalidDate = true;
				}

			} else {
				Date date = null;
				try {

					for (String formatString : formatStrings) {
						SimpleDateFormat sdf = new SimpleDateFormat(formatString);
						date = sdf.parse(this.personalViewSearchCriteria.getPersonalViewToDate());
						if (this.personalViewSearchCriteria.getPersonalViewToDate().trim().equals(sdf.format(date))) {
							toDate = Integer.parseInt((DateUtil.formatDate(date)));
							invalidDate = false;
							viewDefList.add(createViewDefn("DATETO", toDate.toString().trim()));
							break;
						}
						invalidDate = true;
					}
					
				} catch (Exception ex) {
					invalidDate = true;
				}
			}

			if (invalidDate) {
				MessageHelper.setErrorMessage("messages", "Invalid date format");
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return;
			}

		} else {
			if (this.personalViewSearchCriteria.getPersonalViewFromDate() != null && !this.personalViewSearchCriteria.getPersonalViewFromDate().equalsIgnoreCase("")) {
				MessageHelper.setErrorMessage("messages","For Date searches both Date From and Date To must be specified.");
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				return;
			}
		}
		
		if(toDate < fromDate) {
			MessageHelper.setErrorMessage("messages", "The date for Date To cannot be before the Date From date.");
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			return;
		}
	
		
		if(viewDefList.size() < 1) {
			MessageHelper.setErrorMessage("messages", "The Personal View does not have any search criteria specified.");
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			return;
		}
		
			
		this.currentPersonalViews.setViewDefn(viewDefList);

		if(this.currentPersonalViews.getCreateUser() == null){
			this.currentPersonalViews.setCreateUser(getUsername());
		}
			
		if(this.currentPersonalViews.getCreateDate() == null) {
			this.currentPersonalViews.setCreateDate(Common.generateFieldreachDBDate());
		}
			
		this.currentPersonalViews.setModifyUser(getUsername());
		this.currentPersonalViews.setModifyDate(Common.generateFieldreachDBDate());
		
		this.currentPersonalViews.setViewName(this.personalViewSearchCriteria.getViewName());
		this.currentPersonalViews.setViewDesc(this.personalViewSearchCriteria.getViewDesc());
			
		getScriptResultsService().savePersonalViews(null, currentPersonalViews);
		
		this.loadPersonalViewList();
		
	}
	
	public ViewDefn createViewDefn(String fieldName, String fieldValue) {
		
		ViewDefnId viewDefId = new ViewDefnId();
		viewDefId.setFieldName(fieldName);
		viewDefId.setFieldValue(fieldValue);
		
		ViewDefn viewDefn = new ViewDefn();
		viewDefn.setId(viewDefId);
		
		return viewDefn;
	}
	
	public void deletePersonalView() {
		try {
			this.getScriptResultsService().deletePersonalView(null, selectedEditPersonalViews);
			this.loadPersonalViewList();
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
	}
	
	//PRB0050354 - MC - move this to its own function
	public String searchReference() throws FRInstanceException {
		
		_logger.debug(">>> searchReference");
		
		List<ReturnedScripts> returnedScripts = null;
		
		
		
		activeTab = 0;
		
		try {
			returnedScripts = getScriptResultsService().getScriptResultsByAltEquipRef(null, this.searchCriteria.getEquipmentReferenceNumber(), getUsername());
		} catch (MaxResultsExceededException e) {
			//if too many results are returned, tell the user to reduce search options
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_results_too_many_results_found_label"));
			return null;
		} catch (UserNotFoundException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
			return null;
		}

		if (returnedScripts == null || (returnedScripts != null && returnedScripts.size() == 0)){
			try {
				returnedScripts = getScriptResultsService().getScriptResultsByEquipNo(null, this.searchCriteria.getEquipmentReferenceNumber(), getUsername());
			} catch (MaxResultsExceededException e) {
				// if too many results are returned, tell the user to reduce search options
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_results_too_many_results_found_label"));
				return null;
			}catch (UserNotFoundException e) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
				return null;
			}
		}
		
		// if results are found, initialise the search results bean, and switch tabs
		if (returnedScripts != null && returnedScripts.size() > 0){
			searchResults.reset();
			searchResults.init(returnedScripts);
		}
		else{
			// if no results are found, display a warning message stating so
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_results_found"));
			return null;
		}
		
		_logger.debug("<<< searchReference");
		
		return "search";
	}
	
	//Used for Unique Search Reference
	
	public String referenceUniqueNumber() throws FRInstanceException
	{
		_logger.debug(">>> searchReference");
		
		ReturnedScripts returnedScripts = null;
		
		searchResults.reset();
		 
		activeTab = 0;
		
		returnedScripts = getScriptResultsService().getReturnedScript(null, this.searchCriteria.getReturnIdReferenceNumber());
		//searchResults.init(returnedScripts);
		if (returnedScripts != null && getScriptResultsService().isUserAuthorizedToViewReturnedScript(null,returnedScripts.getId(), getUsername())){
			searchResults.init(returnedScripts);
		}
		
		else{
			// if no results are found, display a warning message stating so
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_results_found"));
			return null;
		}
		
		_logger.debug("<<< searchReference");
		
		return "search";
	}

	//FDP1379 - MC - add direct search from query parameters
	public void searchDirect(){

		_logger.debug(">>> searchDirect");

		//Make sure this is not a post back, really only want to hit this function on initial post
		if(!FacesContext.getCurrentInstance().isPostback()) {

			try {
				//if direct params have been provided
				if(!this.emptyDirectSearchTerms()) {

					String equipNo = this.directequipNo;
					String workorderNo = this.directWorkOrderNo;
					Integer returnId = this.directReturnId;

					this.directequipNo = null;
					this.directWorkOrderNo = null;
					this.directReturnId = null;
					
					directMessage = null;
					directMessageSummary = null;

					this.reset();

					searchResults.reset();

					List<ReturnedScripts> returnedScripts = null;

					if(equipNo != null) {
						try {
							returnedScripts = getScriptResultsService().getScriptResultsByEquipNo(null, equipNo, getUsername());
						} catch (MaxResultsExceededException e) {
							// if too many results are returned, tell the user to reduce search options
							MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_results_too_many_results_found_label"));
							directMessage = Properties.get("fdm_search_results_too_many_results_found_label");
							activeTab = 1;
							return;
						}catch (UserNotFoundException e) {
							MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
							directMessage = Properties.get("fdm_login_username_not_found_exception_label");
							activeTab = 1;
							return;
						}
					}else if(workorderNo != null) {
						try {
							returnedScripts = getScriptResultsService().getScriptResultsByWorkOrderNo(null, getUsername(), workorderNo);
						} catch (MaxResultsExceededException e) {
							// if too many results are returned, tell the user to reduce search options
							MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_results_too_many_results_found_label"));
							directMessage = Properties.get("fdm_search_results_too_many_results_found_label");
							activeTab = 1;
							return;
						}catch (UserNotFoundException e) {
							MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
							directMessage = Properties.get("fdm_login_username_not_found_exception_label");
							activeTab = 1;
							return;
						}
					}else if(returnId !=null) {
						// 32370 - KN - getting results based on return id and adding in search results
						if(getScriptResultsService().isUserAuthorizedToViewReturnedScript(null, returnId, getUsername())) {
							returnedScripts = new ArrayList<ReturnedScripts>();
						    returnedScripts.add(getScriptResultsService().getReturnedScript(null, returnId));
						}
					}

					// if results are found, initialise the search results bean, and switch tabs
					if (returnedScripts != null && returnedScripts.size() > 0){
						//ManagedBeanHelper.findBean("searchResults", SearchResults.class).init(returnedScripts);
						searchResults.init(returnedScripts);
						activeTab = 1;
						if (returnId != null) {
							// 32370 - KN - redirecting directly to details screen
						      searchResults.setSelectedResult(searchResults.getResults().get(0));
						      searchResults.findScriptByRowDetails(searchResults.getResults().get(0));
						 }
					}
					else{
						// if no results are found, display a warning message stating so

						directMessage = Properties.get("fdm_search_no_results_found");
						directMessageSummary = (equipNo != null ? " for equipment number " + equipNo : "") + (workorderNo != null ? " for work order number " + workorderNo : "") + (returnId != null ? " for work order number " + returnId : "");

						//fdp1398 - MC - add more info to error message
						MessageHelper.setGlobalWarnMessage(directMessage + directMessageSummary);

						//activeTab = 0;
						searchResults.init(new ArrayList<ReturnedScripts>());
						//fdp1398 - MC - return to empty search tab
						activeTab = 1;


					}
				}

			}catch(Exception e) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
				_logger.error("searchDirect Unknown error occurred" + e.getMessage());
			}
		}else if(this.directMessage != null) {
			//Problem in JSF when using templates means prerenderview event gets called twice
			//this if statement should only be entered on second run of this function if an 
			//error message was added in the first run through.
			MessageHelper.setGlobalWarnMessage(directMessage + directMessageSummary);
			this.directMessage = null;
		}

		_logger.debug("<<< searchDirect");
		
	}
	
	public void findScriptByPersonalView(PersonalViews personalViews) throws FRInstanceException {
		
		PrimeFaces.current().multiViewState().clearAll();
		searchResults.reset();
		List<ReturnedScripts> returnedScripts = null;
		try {
			
			returnedScripts = this.getScriptResultsService().getScriptResultsByViewId(null, this.getUsername(), personalViews.getId());
		
		}  catch (MaxResultsExceededException e) {
			
			// if too many results are returned, tell the user to reduce search options
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_results_too_many_results_found_label"));
			directMessage = Properties.get("fdm_search_results_too_many_results_found_label");
			activeTab = 1;
			
		}catch (UserNotFoundException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
			directMessage = Properties.get("fdm_login_username_not_found_exception_label");
			activeTab = 1;
			
		}
		
		// if results are found, initialise the search results bean, and switch tabs
		if (returnedScripts != null && returnedScripts.size() > 0){
			searchResults.init(returnedScripts);
			activeTab = 1;
		}
		else{
			// if no results are found, display a warning message stating so
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_results_found"));
			activeTab = 0;
		}
		
		searchResults.setPersonalViewLabel(personalViews.getViewName());
		
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "search");
		
	}
	
	public void searchThroughPersonalView() throws FRInstanceException {
		 findScriptByPersonalView(this.selectedPersonalViews);
		 this.selectedPersonalViews = null;
	}
	
	
	
	/**
	 * helper method used to determine if the user has not specified any search criteria
	 */
	public boolean emptySearchTerms(){
		return searchCriteria.emptySearchTerms();
	}


	/**
	 * helper method used to determine if the user has not specified any search criteria
	 */
	public boolean emptyDirectSearchTerms(){
		return ((this.directequipNo == null || "".equals(this.directequipNo.trim())) &&
				(this.directWorkOrderNo == null || "".equals(this.directWorkOrderNo.trim())) &&
				(this.directReturnId == null));
	}

	/**
	 * method for resetting the search form and results
	 */
	public void reset(){
		
		_logger.debug(">>> reset");
		
		this.searchCriteria.reset();
		this.undoSearchCriteria.reset();

		scriptRoot = null;  
		workgroupRoot = null;  
		userRoot = null;  
		resultRoot = null;
		
		selectedScriptNodes = null; 
		selectedWorkgroupNodes = null; 
		selectedUserNodes = null;  
		selectedResultNodes = null; 
		
		activeTab = 0;

		//searchResults.setResults(null);
		//searchResults.setPage(1);
		
		_logger.debug("<<< reset");
	}

	/**
	 * method used to navigate users to the search screen when they click the search
	 * button on the left menu
	 */
	public void navigate(){

		this.reset();
			//FacesContext.getCurrentInstance().getExternalContext().redirect("/fdmwebportal/fdm/search.xhtml");
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "search");

	}

	public SearchResults getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(SearchResults searchResults) {
		this.searchResults = searchResults;
	}
	
	
	public String getTitle() {
		if(this.searchResults.isApprovalMode()) {
			return Properties.get("fdm_breadcrumb_search_approval_menu_heading");
		}
		String title= Properties.get("fdm_breadcrumb_search_menu_heading");
		if(searchResults.getPersonalViewLabel() != null ) {
			title = title + " - "+searchResults.getPersonalViewLabel();
		}
		return title;
	}

	public SearchCriteria getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(SearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
		this.undoSearchCriteria = new SearchCriteria(this.searchCriteria);
	}
	
	public TreeNode getScriptRoot() {
		return scriptRoot;
	}

	public void setScriptRoot(TreeNode scriptRoot) {
		this.scriptRoot = scriptRoot;
	}

	public TreeNode[] getSelectedScriptNodes() {
		return selectedScriptNodes;
	}

	public void setSelectedScriptNodes(TreeNode[] selectedScriptNodes) {
		this.selectedScriptNodes = selectedScriptNodes;
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

	public TreeNode getUserRoot() {
		return userRoot;
	}

	public void setUserRoot(TreeNode userRoot) {
		this.userRoot = userRoot;
	}

	public TreeNode[] getSelectedUserNodes() {
		return selectedUserNodes;
	}

	public void setSelectedUserNodes(TreeNode[] selectedUserNodes) {
		this.selectedUserNodes = selectedUserNodes;
	}

	public TreeNode getResultRoot() {
		return resultRoot;
	}

	public void setResultRoot(TreeNode resultRoot) {
		this.resultRoot = resultRoot;
	}

	public TreeNode[] getSelectedResultNodes() {
		return selectedResultNodes;
	}

	public void setSelectedResultNodes(TreeNode[] selectedResultNodes) {
		this.selectedResultNodes = selectedResultNodes;
	}

	public PersonalViews getSelectedPersonalViews() {
		return selectedPersonalViews;
	}

	public void setSelectedPersonalViews(PersonalViews selectedPersonalViews) {
		this.selectedPersonalViews = selectedPersonalViews;
	}

	public List<PersonalViews> getPersonalViewList() {
		return personalViewList;
	}

	public void setPersonalViewList(List<PersonalViews> personalViewList) {
		this.personalViewList = personalViewList;
	}

	public List<String> getResultApprovalStatuses() {
		return resultApprovalStatuses;
	}

	public void setResultApprovalStatuses(List<String> resultApprovalStatuses) {
		this.resultApprovalStatuses = resultApprovalStatuses;
	}


	public boolean isManageViewPage() {
		return manageViewPage;
	}

	public void setManageViewPage(boolean manageViewPage) {
		this.manageViewPage = manageViewPage;
	}

	public SearchCriteria getPersonalViewSearchCriteria() {
		return personalViewSearchCriteria;
	}

	public void setPersonalViewSearchCriteria(SearchCriteria personalViewSearchCriteria) {
		this.personalViewSearchCriteria = personalViewSearchCriteria;
	}

	public PersonalViews getCurrentPersonalViews() {
		return currentPersonalViews;
	}

	public void setCurrentPersonalViews(PersonalViews currentPersonalViews) {
		this.currentPersonalViews = currentPersonalViews;
	}

	public boolean isNewPersonalView() {
		return newPersonalView;
	}

	public void setNewPersonalView(boolean newPersonalView) {
		this.newPersonalView = newPersonalView;
	}

	public PersonalViews getSelectedEditPersonalViews() {
		return selectedEditPersonalViews;
	}

	public void setSelectedEditPersonalViews(PersonalViews selectedEditPersonalViews) {
		this.selectedEditPersonalViews = selectedEditPersonalViews;
	}
	
	public boolean isPersonalViewMenuSearch() {
		return personalViewMenuSearch;
	}

	public void setPersonalViewMenuSearch(boolean personalViewMenuSearch) {
		this.personalViewMenuSearch = personalViewMenuSearch;
	}
}
