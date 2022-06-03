package com.amtsybex.fieldreach.fdm.user;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.MaxResultsExceededException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardServiceManager;
import com.amtsybex.fieldreach.fdm.search.Search;
import com.amtsybex.fieldreach.fdm.util.DateUtil;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.utils.impl.Common;

@Named
@WindowScoped
public class UserList extends PageCodebase implements Serializable{

	private static final long serialVersionUID = 964321042255183046L;

	@Inject
	transient UserService userService;
	
	@Inject
	transient DashboardServiceManager serviceManager;
	
	@Inject
	transient Search search;
	
	// maximum number of rows to display per page of the results screen
	private int numRows = 25;
	// specify what page the data scroller is initialised to
	private int page = 0;
	
	private List<HPCUsers> results;
	
	private HPCUsers selectedUserResult;
	private List<HPCUsers> selectedUserResults;
	
	private UserDetail userDetail;
	
	private boolean next;
	private boolean previous;
	
	private int activeTab = 0;
	
	private List<HPCWorkGroups> workGroupList;
	private Map<HPCWorkGroups, List<HPCUsers>> userMap;
	
	private List<HPCWorkGroups> workgroups;
	private HPCWorkGroups selectedWorkgroup;

	private List<HPCUsers> users;
	private HPCUsers selectedUser;
	
	private int fromDateRange;
	private int fromDateRangeUnit;
	
	private String directUsercode; 
	private String directWorkGroup; 
	private String directMessage; 
	private String directMessageSummary; 
	
	private TreeNode workgroupRoot;  
	private TreeNode[] selectedWorkgroupNodes; 

	private TreeNode userRoot;  
	private TreeNode[] selectedUserNodes; 
	
	public UserList() {
		
		super();
		reset();
	}
	
	public void reset() {

		next = false;
		previous = false;
		
		workgroups = null;
		users = null;
		fromDateRange = 0;
		fromDateRangeUnit = 0;

		selectedWorkgroup = null;
		selectedUser = null;

		workgroupRoot = null;  
		userRoot = null;  

		selectedWorkgroupNodes = null; 
		selectedUserNodes = null;  

		activeTab = 0;

		results = null;
		page = 0;

		this.selectedUserResult = null;
		this.selectedUserResults = null;
	}
	
	public void resetResults(){
		next = false;
		previous = false;
		results = null;
		page = 0;

		this.selectedUserResult = null;
		this.selectedUserResults = null;
	}
	

	public void searchDirect() throws FRInstanceException, UserNotFoundException {
		
		if(!FacesContext.getCurrentInstance().isPostback()) {
			
			if(!this.emptyDirectSearchTerms()) {
				
				this.reset();
				this.resetResults();
				
				String usercode = this.directUsercode;
				String workGroup = this.directWorkGroup;
				
				this.directUsercode = null;
				this.directWorkGroup = null;

				List<String> workgroupCodes = this.fetchWorkgroupStrings();
				
				if(workgroupCodes != null) {
					
					if(usercode != null && usercode.length() > 0) {
						
						List<HPCUsers> users = userService.findHPCUsersByUserCode(null, usercode);
						
						if(users != null) {
							
							for(HPCUsers user: users) {
								
								if(workgroupCodes.contains(user.getWorkgroupCode())) {
									
									if(results == null) {
										results = new ArrayList<HPCUsers>();
									}
									
									results.add(user);
								}
							}
						}
						
					}else {
						
						if(workgroupCodes.contains(workGroup)) {
							results = userService.findHPCUsersByWorkgroupCode(null, workGroup);
						}
						
					}
					
				}


				if (results != null && results.size() > 0){
					activeTab = 1;
				}
				else{
					// if no results are found, display a warning message stating so
					MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_results_found"));
					directMessage = Properties.get("fdm_search_no_results_found");
					activeTab = 0;
				}
				
			}
		}else if(this.directMessage != null) {
			//Problem in JSF when using templates means prerenderview event gets called twice
			//this if statement should only be entered on second run of this function if an 
			//error message was added in the first run through.
			MessageHelper.setGlobalWarnMessage(directMessage + directMessageSummary);
			this.directMessage = null;
		}
		
	}
	
	
	public boolean emptyDirectSearchTerms(){
		return ((this.directUsercode == null || "".equals(this.directUsercode.trim())) &&
				(this.directWorkGroup == null || "".equals(this.directWorkGroup.trim())));
	}

	public void search() throws FRInstanceException {
		
		this.resetResults();

		// if no search terms are entered, find all scripts
		if (emptySearchTerms()){
			
			try {
				results = fetchUsers();
			} catch (UserNotFoundException e) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
				activeTab = 0;
				return;
			}
			
		}else {
			
			
			List<String> groupCodes = null;
			List<String> userCodes = null;
			
			// build a list of workgroup codes from the list of workgroup objects
			if (getWorkgroups().size() > 0){
				groupCodes = new ArrayList<String>();
				for (HPCWorkGroups w : getWorkgroups()){
					groupCodes.add(w.getWorkgroupCode());
				}
			}

			// build a list of user codes from the list of user objects
			if (getUsers().size() > 0){
				userCodes = new ArrayList<String>();
				for (HPCUsers u : getUsers()){
					userCodes.add(u.getId().getUserCode());
				}
			}
			

			//if(this.getFromDateRange() > 0) {
				Calendar cal = Calendar.getInstance();
				
				if(this.getFromDateRangeUnit() == 0) {
					cal.add(Calendar.DAY_OF_YEAR, -this.getFromDateRange());
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
				}else if(this.getFromDateRangeUnit() == 1) {
					cal.add(Calendar.HOUR_OF_DAY, -this.getFromDateRange());
				}
				
				Date dateNow = cal.getTime();
				
				results = getUserService().findHPCUsers(null, groupCodes, userCodes, DateUtil.formatDateInt(dateNow), Common.FIELDREACH_TIME_FORMAT.format(dateNow), true);
			//}else {
			//	results = getUserService().findHPCUsers(null, groupCodes, userCodes, null, null, true);
			//}


		}
		
		if (results != null && results.size() > 0){
			activeTab = 1;
		}
		else{
			// if no results are found, display a warning message stating so
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_results_found"));
			activeTab = 0;
		}
	}
	
	public boolean emptySearchTerms(){
		return ((workgroups == null || workgroups.size() == 0) &&
				(users == null || users.size() == 0) &&
				fromDateRange == 0);
	}
	
	public List<String> fetchWorkgroupStrings() throws UserNotFoundException, FRInstanceException {
		List<String> fetchedWorkgroups = new ArrayList<String>();
		
		List<HPCWorkGroups> wgs = fetchWorkgroups();

		if (wgs != null){
			Iterator<HPCWorkGroups> workgroupIterator = wgs.iterator();
			while(workgroupIterator.hasNext()){
				fetchedWorkgroups.add(((HPCWorkGroups)workgroupIterator.next()).getWorkgroupCode());
			}
		}

		return fetchedWorkgroups;
	}
	
	
	public List<HPCWorkGroups> fetchWorkgroups() throws UserNotFoundException, FRInstanceException {
		
		if (workGroupList == null){
			workGroupList = this.serviceManager.getUserService().getAccessibleWorkgroups(null, getUsername());
		}
		return workGroupList;
	}
	
	/**
	 * method used to navigate users to the search screen when they click the search
	 * button on the left menu
	 * @throws FRInstanceException 
	 * @throws UserNotFoundException 
	 * @throws IOException 
	 */
	public void navigate() throws FRInstanceException, UserNotFoundException{
		
		this.reset();
		//FacesContext.getCurrentInstance().getExternalContext().redirect("/fdmwebportal/fdm/userlist.xhtml");
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "userlist");
	}
	
	public void findUserByRowDetails() throws FRInstanceException, MaxResultsExceededException{
		
		updateNavigation();
		
		//search.searchByWorkOrder(this.workDetail.getWork().getId().getWorkOrderNo());

		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "userdetail");

	}
	
	public void navigateNext() throws FRInstanceException, MaxResultsExceededException{

		nextResult();
	}
	
	public void nextResult() throws FRInstanceException, MaxResultsExceededException{
		//workDetail = new WorkDetail();
		
		HPCUsers us = null;
		int index = 0;
		for(int i=0; i<this.results.size(); i++) {
			
			us = results.get(i);
			if(this.getSelectedUserResult().getId().getUserCode().equals(us.getId().getUserCode())) {
				index = i;
				break;
			}
		}
		
		if ((results.size()-1) > index){
			this.setSelectedUserResult(results.get(index+1));
		}
		
		updateNavigation();
		
		//search.searchByWorkOrder(this.workDetail.getWork().getId().getWorkOrderNo());

	}
	
	/**
	 * method called when the user clicks the previous link on the details page
	 * @throws FRInstanceException 
	 * @throws MaxResultsExceededException 
	 */
	public void navigatePrevious() throws FRInstanceException, MaxResultsExceededException{

		previousResult();
	}
	
	public void previousResult() throws FRInstanceException, MaxResultsExceededException{
		//workDetail = new WorkDetail();
		HPCUsers us = null;
		int index = 0;
		for(int i=0; i<this.results.size(); i++) {
			
			us = results.get(i);
			if(this.getSelectedUserResult().getId().getUserCode().equals(us.getId().getUserCode())) {
				index = i;
				break;
			}
		}
		
		if (index > 0){
			this.setSelectedUserResult(results.get(index-1));
		}
		
		updateNavigation();
		
		//search.searchByWorkOrder(this.workDetail.getWork().getId().getWorkOrderNo());

	}
	
	public void navigateReturn(){
		
		this.selectedUserResult = null;
		this.selectedUserResults = null;

		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");
	}
	
	public void updateNavigation(){
		
		HPCUsers us = null;
		int index = 0;
		for(int i=0; i<this.results.size(); i++) {
			
			us = results.get(i);
			if(this.getSelectedUserResult().getId().getUserCode().equals(us.getId().getUserCode())) {
				index = i;
				break;
			}
		}
		
		setNext((index+1) < results.size());
		setPrevious(index > 0);
	}
	

	public int getNumRows() {
		return numRows;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}


	public boolean isNext() {
		return next;
	}

	public void setNext(boolean next) {
		this.next = next;
	}

	public boolean isPrevious() {
		return previous;
	}

	public void setPrevious(boolean previous) {
		this.previous = previous;
	}


	public DashboardServiceManager getServiceManager() {
		return serviceManager;
	}

	public void setServiceManager(DashboardServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	public int getActiveTab() {
		return activeTab;
	}

	public void setActiveTab(int activeTab) {
		this.activeTab = activeTab;
	}

	public void setWorkgroups(List<HPCWorkGroups> workgroups) {
		this.workgroups = workgroups;
	}

	public HPCWorkGroups getSelectedWorkgroup() {
		return selectedWorkgroup;
	}

	public void setSelectedWorkgroup(HPCWorkGroups selectedWorkgroup) {
		this.selectedWorkgroup = selectedWorkgroup;
	}

	public void setUsers(List<HPCUsers> users) {
		this.users = users;
	}

	public HPCUsers getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(HPCUsers selectedUser) {
		this.selectedUser = selectedUser;
	}

	public String getDirectMessage() {
		return directMessage;
	}

	public void setDirectMessage(String directMessage) {
		this.directMessage = directMessage;
	}

	public String getDirectMessageSummary() {
		return directMessageSummary;
	}

	public void setDirectMessageSummary(String directMessageSummary) {
		this.directMessageSummary = directMessageSummary;
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

	

	/**
	 * method for building the tree of workgroups
	 * 
	 * @throws UserNotFoundException
	 * @throws FRInstanceException 
	 */
	public void loadSelectedWorkgroups() throws UserNotFoundException, FRInstanceException{
		//if (workgroupRoot == null){
			workgroupRoot = new CheckboxTreeNode("Root", null);

			// make a map for the workgroup categories
			Map<Integer, CheckboxTreeNode> workgroupCats = new TreeMap<Integer, CheckboxTreeNode>();

			List<HPCWorkGroups> workgroupList = fetchWorkgroups();

			for (HPCWorkGroups w : workgroupList){

				// check if we have created a parent node for the current workgroup's category
				CheckboxTreeNode cat = workgroupCats.get(w.getWgCatId());

				// if not, create a new parent node for the current workgroup's category
				if (cat == null){
					cat = new CheckboxTreeNode(w.getHpcWgCat().getWgCatDesc(), workgroupRoot);
					cat.setExpanded(true);
					workgroupCats.put(w.getWgCatId(), cat);
				}

				CheckboxTreeNode node = new CheckboxTreeNode(w, cat); 
				if(this.getWorkgroups() != null && this.getWorkgroups().contains(w)) {
					node.setSelected(true,true);
				}
			}
		//}
		//tempWorkgroupNodes = selectedWorkgroupNodes;
	}
	
	/*public void cancelSelectedWorkgroups(){
	selectedWorkgroupNodes = tempWorkgroupNodes;
	}*/
	public void clearSelectedWorkgroups(){
	
		if(this.selectedWorkgroupNodes != null) {
			for (TreeNode tn : this.selectedWorkgroupNodes){
				tn.setSelected(false);
			}
		}

		this.selectedWorkgroupNodes = null;
	
	}
	



	/**
	 * method for building the tree of users
	 * 
	 * @throws UserNotFoundException
	 * @throws FRInstanceException 
	 */
	public void loadSelectedUsers() throws UserNotFoundException, FRInstanceException{
		if (userRoot == null){
			userRoot = new CheckboxTreeNode("Root", null);

			// make a map for the workgroup categories
			Map<String, CheckboxTreeNode> workgroupCats = new TreeMap<String, CheckboxTreeNode>();

			List<HPCUsers> userList = fetchUsers();

			for (HPCUsers u : userList){

				// check if we have created a parent node for the current workgroup's category
				CheckboxTreeNode cat = workgroupCats.get(u.getWorkgroupCode());

				// if not, create a new parent node for the current workgroup's category
				if (cat == null){
					HPCWorkGroups group = getUserService().findHPCWorkGroup(null, u.getWorkgroupCode());

					cat = new CheckboxTreeNode(group.getWorkgroupDesc(), userRoot);
					cat.setExpanded(true);
					workgroupCats.put(u.getWorkgroupCode(), cat);
				}

				CheckboxTreeNode node = new CheckboxTreeNode(u, cat); 
				if(this.getWorkgroups() != null && this.getWorkgroups().contains(u)) {
					node.setSelected(true,true);
				}
			}
		}

	}

	public void cancelSelectedUsers(){

	}


	/**
	 * parse the array of selected nodes into a list for the corresponding drop down menu
	 */
	public void updateSelectedWorkgroups(){
		List<TreeNode> selectedWorkgroupNodeList = Arrays.asList(selectedWorkgroupNodes);

		getWorkgroups().clear();

		for (TreeNode tn : selectedWorkgroupNodeList){
			// discard the parent nodes
			if (tn.getData() instanceof HPCWorkGroups)
				getWorkgroups().add((HPCWorkGroups)tn.getData());
		}
	}

	/**
	 * parse the array of selected nodes into a list for the corresponding drop down menu
	 */
	public void updateSelectedUsers(){
		List<TreeNode> selectedUserNodeList = Arrays.asList(selectedUserNodes);

		getUsers().clear();

		for (TreeNode tn : selectedUserNodeList){
			// discard the parent nodes
			if (tn.getData() instanceof HPCUsers)
				getUsers().add((HPCUsers)tn.getData());
		}
	}

	

	void setWorkGroupList(List<HPCWorkGroups> workGroupList) {
		this.workGroupList = workGroupList;
	}

	public Map<HPCWorkGroups, List<HPCUsers>> getUserMap() throws UserNotFoundException, FRInstanceException {
		if (userMap == null){
			userMap = getUserService().getFDMHPCWorkgroupToUserMap(null,getUsername(), false);
		}
		return userMap;
	}

	public void setUserMap(Map<HPCWorkGroups, List<HPCUsers>> userMap) {
		this.userMap = userMap;
	}

	public List<HPCWorkGroups> getWorkgroups() {
		if (workgroups == null){
			workgroups = new ArrayList<HPCWorkGroups>();
		}
		return workgroups;
	}
	
	/**
	 * get a list of users from the workgroup/user map
	 * @throws FRInstanceException 
	 */
	public List<HPCUsers> fetchUsers() throws UserNotFoundException, FRInstanceException {
		List<HPCUsers> fetchedUsers = new ArrayList<HPCUsers>();
		Map<HPCWorkGroups, List<HPCUsers>> map = getUserMap();

		Iterator<List<HPCUsers>> workgroupIterator = map.values().iterator();
		while(workgroupIterator.hasNext()){
			List<HPCUsers> userList = workgroupIterator.next();
			if (userList != null){
				for (HPCUsers u : userList)
					fetchedUsers.add(u);
			}
		}
		return fetchedUsers;
	}

	public List<HPCUsers> getUsers(){
		if (users == null){
			users = new ArrayList<HPCUsers>();
		}
		return users;
	}
	


	public String getDirectWorkGroup() {
		return directWorkGroup;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public List<HPCUsers> getResults() {
		return results;
	}

	public void setResults(List<HPCUsers> results) {
		this.results = results;
	}

	public HPCUsers getSelectedUserResult() {
		return selectedUserResult;
	}

	public void setSelectedUserResult(HPCUsers selectedUserResult) {
		this.selectedUserResult = selectedUserResult;
	}

	public List<HPCUsers> getSelectedUserResults() {
		return selectedUserResults;
	}

	public void setSelectedUserResults(List<HPCUsers> selectedUserResults) {
		this.selectedUserResults = selectedUserResults;
	}

	public UserDetail getUserDetail() {
		return userDetail;
	}

	public void setUserDetail(UserDetail userDetail) {
		this.userDetail = userDetail;
	}

	public String getDirectUsercode() {
		return directUsercode;
	}

	public void setDirectUsercode(String directUsercode) {
		this.directUsercode = directUsercode;
	}

	public List<HPCWorkGroups> getWorkGroupList() {
		return workGroupList;
	}

	public void setDirectWorkGroup(String directWorkGroup) {
		this.directWorkGroup = directWorkGroup;
	}

	public int getFromDateRange() {
		return fromDateRange;
	}

	public void setFromDateRange(int fromDateRange) {
		this.fromDateRange = fromDateRange;
	}

	public int getFromDateRangeUnit() {
		return fromDateRangeUnit;
	}

	public void setFromDateRangeUnit(int fromDateRangeUnit) {
		this.fromDateRangeUnit = fromDateRangeUnit;
	}

	public void setWorkGroupsFromStrings(List<String> woGroups) throws UserNotFoundException, FRInstanceException {
		
		List<HPCWorkGroups> wgs = fetchWorkgroups();
		List<HPCWorkGroups> groups = null;
		
		if (woGroups != null){
			
			groups = new ArrayList<HPCWorkGroups>();
			
			for(HPCWorkGroups wg: wgs) {
				if(woGroups.contains(wg.getId().getWorkgroupCode())) {
					groups.add(wg);
				}
			}
		}

		this.setWorkgroups(groups);
	}
	
	
	public String getTitle() {
		return "User Search List";
	}
}
