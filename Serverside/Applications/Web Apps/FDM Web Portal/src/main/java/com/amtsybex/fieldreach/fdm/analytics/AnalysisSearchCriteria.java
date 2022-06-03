package com.amtsybex.fieldreach.fdm.analytics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.Script;

/**
 * FDE060 - script analytics/metrics
 * @author CroninM
 *
 */
public class AnalysisSearchCriteria {

	private Script script;
	private Date fromDate;
	private Date toDate;
	//Script analytics only
	private boolean scopeToRecentAsset;
	
	private List<HPCWorkGroups> workgroups;

	public AnalysisSearchCriteria() {
	}
	
	public AnalysisSearchCriteria(AnalysisSearchCriteria criteria) {
		super();
		this.script = criteria.script;
		this.fromDate = criteria.fromDate;
		this.toDate = criteria.toDate;
		this.workgroups = criteria.workgroups;
		this.scopeToRecentAsset = criteria.scopeToRecentAsset;
	}

	public void reset() {
		script = null;
		fromDate = null;
		toDate = null;
		workgroups = null;
		scopeToRecentAsset = false;
	}

	public Script getScript() {
		return script;
	}

	public void setScript(Script script) {
		this.script = script;
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

	public List<HPCWorkGroups> getWorkgroups() {
		if (workgroups == null){
			workgroups = new ArrayList<HPCWorkGroups>();
		}
		return workgroups;
	}

	public void setWorkgroups(List<HPCWorkGroups> workgroups) {
		this.workgroups = workgroups;
	}

	public List<String> getWorkgroupListAsString(){
		
		if (workgroups == null){
			return null;
		}
		
		return  workgroups.stream().map((workgroup) -> workgroup.getId().getWorkgroupCode()).collect(Collectors.toList());
		
	}

	public boolean isScopeToRecentAsset() {
		return scopeToRecentAsset;
	}

	public void setScopeToRecentAsset(boolean scopeToRecentAsset) {
		this.scopeToRecentAsset = scopeToRecentAsset;
	}
	
	
	
}
