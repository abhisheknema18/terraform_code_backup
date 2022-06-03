package com.amtsybex.fieldreach.fdm.admin;

import java.util.List;

import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;

public class SystemUserAdminCriteria {

	private String userCode;
	private String userName;
	private List<HPCWorkGroups> workgroups;
	private List<HPCUsers> users;
	
	public SystemUserAdminCriteria() {
	
	}
	
	public SystemUserAdminCriteria(SystemUserAdminCriteria criteria) {
		super();
		
		this.userCode = criteria.userCode;
		this.userName = criteria.userName;
		this.workgroups = criteria.workgroups;
		this.users = criteria.users;
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

	public List<HPCWorkGroups> getWorkgroups() {
		return workgroups;
	}

	public void setWorkgroups(List<HPCWorkGroups> workgroups) {
		this.workgroups = workgroups;
	}

	public List<HPCUsers> getUsers() {
		return users;
	}

	public void setUsers(List<HPCUsers> users) {
		this.users = users;
	}
	
}
