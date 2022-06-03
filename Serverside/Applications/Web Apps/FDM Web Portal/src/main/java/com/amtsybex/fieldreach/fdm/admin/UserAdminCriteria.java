package com.amtsybex.fieldreach.fdm.admin;

import java.util.ArrayList;
import java.util.List;

import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;

public class UserAdminCriteria {

	private List<HPCWorkGroups> workgroups;
	private String userCode;
	private String userName;
	
	private String workGroupCode;
	private String workGroupDesc;	
	
	private boolean allWorkgroupsSelected;
	
	public UserAdminCriteria() {
	
	}
	
	public UserAdminCriteria(UserAdminCriteria criteria) {
		super();
		
		this.userCode = criteria.userCode;
		this.userName = criteria.userName;
		if(criteria.workgroups != null) {
			this.workgroups = new ArrayList<HPCWorkGroups>(criteria.workgroups.size());
			this.workgroups.addAll(criteria.workgroups);
		}
		this.workGroupCode = criteria.workGroupCode;
		this.workGroupDesc = criteria.workGroupDesc;
		this.allWorkgroupsSelected = criteria.allWorkgroupsSelected;
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
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public boolean isAllWorkgroupsSelected() {
		return allWorkgroupsSelected;
	}
	public void setAllWorkgroupsSelected(boolean allWorkgroupsSelected) {
		this.allWorkgroupsSelected = allWorkgroupsSelected;
	}

	public String getWorkGroupCode() {
		return workGroupCode;
	}

	public void setWorkGroupCode(String workGroupCode) {
		this.workGroupCode = workGroupCode;
	}

	public String getWorkGroupDesc() {
		return workGroupDesc;
	}

	public void setWorkGroupDesc(String workGroupDesc) {
		this.workGroupDesc = workGroupDesc;
	}
	
	
	
}
