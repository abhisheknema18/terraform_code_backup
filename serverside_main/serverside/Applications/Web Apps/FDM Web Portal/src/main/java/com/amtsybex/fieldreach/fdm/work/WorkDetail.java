package com.amtsybex.fieldreach.fdm.work;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.WorkIssued;
import com.amtsybex.fieldreach.backend.model.WorkStatusHistory;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.search.SearchResult;
import com.amtsybex.fieldreach.fdm.util.DateUtil;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.utils.impl.Common;

@Named
@WindowScoped
public class WorkDetail extends PageCodebase implements Serializable{

	private static final long serialVersionUID = -7755963526372460488L;
	
	private WorkIssued work;
	private HPCUsers user;
	private HPCWorkGroups workGroup;
	private List<WorkStatusHistory> workStatusHistory;
	private MapModel mapModel;
	
	private List<SearchResult> resultHistory;
	
	
	private Date tempStartDate;
	private Date tempFinsihDate;
	
	private Date tempStartTime;
	private Date tempFinsihTime;
	
	private int page = 1;
	
	public WorkDetail() {
		super();
	}

	public WorkIssued getWork() {
		return work;
	}

	public void setWork(WorkIssued work) {
		this.work = work;
		if(work.getPlanStartDate() != null) {
			this.tempStartDate = Common.convertFieldreachDate(work.getPlanStartDateInt());
		}else {
			this.tempStartDate = null;
		}
		if(work.getReqFinishDate() != null) {
			this.tempFinsihDate = Common.convertFieldreachDate(work.getReqFinishDateInt());
		}else {
			this.tempFinsihDate = null;
		}
		
		if(work.getPlanStartTime() != null) {
			this.tempStartTime = Common.convertFieldreachTime(Integer.parseInt(work.getPlanStartTime()), true);
		}else {
			this.tempStartTime = null;
		}
		
		if(work.getReqFinishTime() != null) {
			this.tempFinsihTime = Common.convertFieldreachTime(Integer.parseInt(work.getReqFinishTime()), true);
		}else {
			this.tempFinsihTime = null;
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
		/*if(tempStartDate != null) {
			this.getWork().setPlanStartDate(Common.generateFieldreachDBDate(tempStartDate));
		}else {
			this.getWork().setPlanStartDate(null);
		}*/
	}

	public Date getTempFinsihDate() {
		return tempFinsihDate;
	}

	public void setTempFinsihDate(Date tempFinsihDate) {
		this.tempFinsihDate = tempFinsihDate;
		/*if(tempFinsihDate != null) {
			this.getWork().setReqFinishDate(Common.generateFieldreachDBDate(tempFinsihDate));
		}else {
			this.getWork().setReqFinishDate(null);
		}*/
	}
		
	public void createDatesFromTempDates() {
		if(tempStartDate != null) {
			this.getWork().setPlanStartDate(Common.generateFieldreachDBDate(tempStartDate));
		}else {
			this.getWork().setPlanStartDate(null);
		}
		if(tempFinsihDate != null) {
			this.getWork().setReqFinishDate(Common.generateFieldreachDBDate(tempFinsihDate));
		}else {
			this.getWork().setReqFinishDate(null);
		}
		if(this.tempStartTime != null) {
			this.getWork().setPlanStartTime(DateUtil.formatTime(tempStartTime));
		}else {
			this.getWork().setPlanStartTime(null);
		}
		
		if(this.tempFinsihTime != null) {
			this.getWork().setReqFinishTime(DateUtil.formatTime(tempFinsihTime));
		}else {
			this.getWork().setReqFinishTime(null);
		}
		
		if(this.tempFinsihTime != null) {
			this.tempFinsihTime = Common.convertFieldreachTime(Integer.parseInt(work.getReqFinishTime()), true);
		}else {
			this.tempFinsihTime = null;
		}
	}
	
	public String getTitle() {
		return Properties.get("fdm_breadcrumb_work_menu_heading");
	}

	public MapModel getMapModel() {
		return mapModel;
	}

	public void setMapModel(MapModel mapModel) {
		this.mapModel = mapModel;
	}

	public void addMarker() {
		if(this.getLocation()!=null && !this.getLocation().isEmpty()) {
			mapModel = new DefaultMapModel();
			
			LatLng coOrds = new LatLng(Double.valueOf(this.getWork().getLatitude()),Double.valueOf(this.getWork().getLongitude()));
			
			mapModel.addOverlay(new Marker(coOrds));
		}
	}
	
	public String getLocation() {
		if(this.getWork().getLatitude()!=null && this.getWork().getLongitude()!=null) {
			
			return this.getWork().getLatitude()+","+this.getWork().getLongitude();
		}
		else {
			return null;
		}
	}

	public Date getTempStartTime() {
		return tempStartTime;
	}

	public void setTempStartTime(Date tempStartTime) {
		this.tempStartTime = tempStartTime;
	}

	public Date getTempFinsihTime() {
		return tempFinsihTime;
	}

	public void setTempFinsihTime(Date tempFinsihTime) {
		this.tempFinsihTime = tempFinsihTime;
	}

	public List<SearchResult> getResultHistory() {
		return resultHistory;
	}

	public void setResultHistory(List<SearchResult> resultHistory) {
		this.resultHistory = resultHistory;
	}

}
