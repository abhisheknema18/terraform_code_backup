package com.amtsybex.fieldreach.fdm.work;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;

/**
 * PRB0050354 - moving the criteria out to its own class. Tidies up code and makes copying for undo on cancel easier
 * @author CroninM
 *
 */
public class WorkCriteria {

	private List<HPCWorkGroups> workgroups;
	private List<HPCUsers> users;
	private String[] selectedWorkStatusOption;
	private Date fromDate;
	private Date toDate;
	private String woNumber;
	
	public WorkCriteria() {
		selectedWorkStatusOption = new String[] {Properties.get("fdm_work_status_option_wip")};
	}

	public WorkCriteria(WorkCriteria criteria) {
		
		super();
		
		if(criteria.workgroups != null) {
			this.workgroups = new ArrayList<HPCWorkGroups>(criteria.workgroups.size());
			this.workgroups.addAll(criteria.workgroups);
		}
		if(criteria.users != null) {
			this.users = new ArrayList<HPCUsers>(criteria.users.size());
			this.users.addAll(criteria.users);
		}
		if(criteria.selectedWorkStatusOption != null) {
			this.selectedWorkStatusOption = new String[criteria.selectedWorkStatusOption.length];
			System.arraycopy( criteria.selectedWorkStatusOption, 0, this.selectedWorkStatusOption, 0, criteria.selectedWorkStatusOption.length );
		}
		
		this.fromDate = criteria.fromDate;
		this.toDate = criteria.toDate;
		this.woNumber = criteria.woNumber;

	}
	
	public boolean emptySearchTerms(){
		return ((workgroups == null || workgroups.size() == 0) &&
				(users == null || users.size() == 0) &&
				selectedWorkStatusOption == null &&
				fromDate == null &&
				toDate == null);
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

	public String[] getSelectedWorkStatusOption() {
		return selectedWorkStatusOption;
	}

	public void setSelectedWorkStatusOption(String[] selectedWorkStatusOption) {
		this.selectedWorkStatusOption = selectedWorkStatusOption;
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

	public String getWoNumber() {
		return woNumber;
	}

	public void setWoNumber(String woNumber) {
		this.woNumber = woNumber;
	}
	
	
}
