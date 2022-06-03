package com.amtsybex.fieldreach.fdm.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.Script;

/**
 * PRB0050354 - moving the criteria out to its own class. Tidies up code and makes copying for undo on cancel easier
 * @author CroninM
 *
 */
public class SearchCriteria {

	
	private List<Script> scripts;
	private List<HPCWorkGroups> workgroups;
	private List<HPCUsers> users;
	private List<String> resultStatuses;
	private Date fromDate;
	private Date toDate;
	//private String referenceNumber;
	private String equipmentReferenceNumber;
	//private Integer referenceUniqueNumber;
	private Integer returnIdReferenceNumber;
	
	private String personalViewFromDate;
	private String personalViewToDate;
	private boolean searchOnMenu;
	
	private boolean allScriptsSelected;
	private boolean allWorkgroupsSelected;
	private boolean allUsersSelected;
	private boolean allResultsSelected;
	
	private String viewName;
	private String viewDesc;
	
	//private List<Date> dateRange;

	public SearchCriteria() {
		
	}
	
	public SearchCriteria(SearchCriteria criteria) {
		super();
		if(criteria.scripts != null) {
			this.scripts = new ArrayList<Script>(criteria.scripts.size());
			this.scripts.addAll(criteria.scripts);
		}
		if(criteria.workgroups != null) {
			this.workgroups = new ArrayList<HPCWorkGroups>(criteria.workgroups.size());
			this.workgroups.addAll(criteria.workgroups);
		}
		if(criteria.users != null) {
			this.users = new ArrayList<HPCUsers>(criteria.users.size());
			this.users.addAll(criteria.users);
		}
		if(criteria.resultStatuses != null) {
			this.resultStatuses = new ArrayList<String>(criteria.resultStatuses.size());
			this.resultStatuses.addAll(criteria.resultStatuses);
		}
		/*if(criteria.dateRange != null) {
			this.dateRange = new ArrayList<Date>(criteria.dateRange.size());
			this.dateRange.addAll(criteria.dateRange);
		}*/
		this.fromDate = criteria.fromDate;
		this.toDate = criteria.toDate;
		this.equipmentReferenceNumber = criteria.equipmentReferenceNumber;

	}
	
	public boolean emptySearchTerms(){
		return ((scripts == null || scripts.size() == 0) &&
				(workgroups == null || workgroups.size() == 0) &&
				(users == null || users.size() == 0) &&
				(resultStatuses == null || resultStatuses.size() == 0) &&
				//(dateRange == null || dateRange.size() == 0) &&
				fromDate == null &&
				toDate == null);
				//(referenceNumber == null || "".equals(referenceNumber.trim())));
	}
	
	public void reset() {
		scripts = null;
		workgroups = null;
		users = null;
		resultStatuses = null;
		fromDate = null;
		toDate = null;
		//dateRange = null;
		equipmentReferenceNumber = null;
		returnIdReferenceNumber=null;
		
		personalViewFromDate = null;
		personalViewToDate = null;
		searchOnMenu = false;
		
	}

	public List<Script> getScripts() {
		if (scripts == null){
			scripts = new ArrayList<Script>();
		}
		return scripts;
	}

	public void setScripts(List<Script> scripts) {
		this.scripts = scripts;
	}

	public List<HPCWorkGroups> getWorkgroups() {
		if (workgroups == null){
			workgroups = new ArrayList<HPCWorkGroups>();
		}
		return workgroups;
	}

	public void setWorkgroups(List<HPCWorkGroups> workgroups) {
		this.workgroups = workgroups;
	}

	public List<HPCUsers> getUsers(){
		if (users == null){
			users = new ArrayList<HPCUsers>();
		}
		return users;
	}

	public void setUsers(List<HPCUsers> users) {
		this.users = users;
	}

	public List<String> getResultStatuses() {
		if (resultStatuses == null){
			resultStatuses = new ArrayList<String>();
		}
		return resultStatuses;
	}

	public void setResultStatuses(List<String> resultStatuses) {
		this.resultStatuses = resultStatuses;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getEquipmentReferenceNumber() {
		return equipmentReferenceNumber;
	}

	public void setEquipmentReferenceNumber(String equipmentReferenceNumber) {
		this.equipmentReferenceNumber = equipmentReferenceNumber;
	}

	public Integer getReturnIdReferenceNumber() {
		return returnIdReferenceNumber;
	}

	public void setReturnIdReferenceNumber(Integer returnIdReferenceNumber) {
		this.returnIdReferenceNumber = returnIdReferenceNumber;
	}

	public String getPersonalViewFromDate() {
		return personalViewFromDate;
	}

	public void setPersonalViewFromDate(String personalViewFromDate) {
		this.personalViewFromDate = personalViewFromDate;
	}

	public String getPersonalViewToDate() {
		return personalViewToDate;
	}

	public void setPersonalViewToDate(String personalViewToDate) {
		this.personalViewToDate = personalViewToDate;
	}

	public boolean isSearchOnMenu() {
		return searchOnMenu;
	}

	public void setSearchOnMenu(boolean searchOnMenu) {
		this.searchOnMenu = searchOnMenu;
	}
	
	public boolean isAllScriptsSelected() {
		if (this.scripts != null && !this.scripts.isEmpty())
			return false;
		else
			return true;
	}

	public void setAllScriptsSelected(boolean allScriptsSelected) {
		this.allScriptsSelected = allScriptsSelected;
	}

	public boolean isAllWorkgroupsSelected() {
		if (this.workgroups != null && !this.workgroups.isEmpty())
			return false;
		else
			return true;		
	}

	public void setAllWorkgroupsSelected(boolean allWorkgroupsSelected) {
		this.allWorkgroupsSelected = allWorkgroupsSelected;
	}

	public boolean isAllUsersSelected() {
		if (this.users != null && !this.users.isEmpty())
			return false;
		else
			return true;
	}

	public void setAllUsersSelected(boolean allUsersSelected) {
		this.allUsersSelected = allUsersSelected;
	}

	public boolean isAllResultsSelected() {
		if (this.resultStatuses != null && !this.resultStatuses.isEmpty())
			return false;
		else
			return true;
	}

	public void setAllResultsSelected(boolean allResultsSelected) {
		this.allResultsSelected = allResultsSelected;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getViewDesc() {
		return viewDesc;
	}

	public void setViewDesc(String viewDesc) {
		this.viewDesc = viewDesc;
	}

	
	/*public Date getFromDate() {
		return this.dateRange != null && this.dateRange.size() == 2 ? this.dateRange.get(0) : null;
	}
	
	public Date getToDate() {
		return this.dateRange != null && this.dateRange.size() == 2 ? this.dateRange.get(1) : null;
	}*/

	

	

	/*public List<Date> getDateRange() {
		if (dateRange == null){
			dateRange = new ArrayList<Date>();
		}
		return dateRange;
	}

	public void setDateRange(List<Date> dateRange) {
		this.dateRange = dateRange;
	}*/

	
}
