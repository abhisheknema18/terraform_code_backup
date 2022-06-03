package com.amtsybex.fieldreach.fdm.user;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.WorkIssued;
import com.amtsybex.fieldreach.backend.model.WorkStatusHistory;
import com.amtsybex.fieldreach.utils.impl.Common;

public class UserDetail implements Serializable{

	private static final long serialVersionUID = -7755963526372460488L;
	
	private WorkIssued work;
	private HPCUsers user;
	private HPCWorkGroups workGroup;
	private List<WorkStatusHistory> workStatusHistory;
	
	private Date tempStartDate;
	private Date tempFinsihDate;
	
	private int page = 1;
	
	public UserDetail() {
		super();
	}

	public WorkIssued getWork() {
		return work;
	}

	public void setWork(WorkIssued work) {
		this.work = work;
		if(work.getPlanStartDate() != null) {
			this.tempStartDate = Common.convertFieldreachDate(work.getPlanStartDateInt());
		}
		if(work.getReqFinishDate() != null) {
			this.tempFinsihDate = Common.convertFieldreachDate(work.getReqFinishDateInt());
		}

	}

	public HPCUsers getUser() {
		return user;
	}

	public void setUser(HPCUsers user) {
		this.user = user;
	}

	public HPCWorkGroups getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(HPCWorkGroups workGroup) {
		this.workGroup = workGroup;
	}

	public List<WorkStatusHistory> getWorkStatusHistory() {
		return workStatusHistory;
	}

	public void setWorkStatusHistory(List<WorkStatusHistory> workStatusHistory) {
		this.workStatusHistory = workStatusHistory;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Date getTempStartDate() {
		return tempStartDate;
	}

	public void setTempStartDate(Date tempStartDate) {
		this.tempStartDate = tempStartDate;
		if(tempStartDate != null) {
			this.getWork().setPlanStartDate(Common.generateFieldreachDBDate(tempStartDate));
		}else {
			this.getWork().setPlanStartDate(null);
		}
	}

	public Date getTempFinsihDate() {
		return tempFinsihDate;
	}

	public void setTempFinsihDate(Date tempFinsihDate) {
		this.tempFinsihDate = tempFinsihDate;
		if(tempFinsihDate != null) {
			this.getWork().setReqFinishDate(Common.generateFieldreachDBDate(tempFinsihDate));
		}else {
			this.getWork().setReqFinishDate(null);
		}
	}
	
	

}
