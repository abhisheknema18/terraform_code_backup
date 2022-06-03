package com.amtsybex.fieldreach.services.endpoint.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.SystemWorkStatus;
import com.amtsybex.fieldreach.backend.service.WorkOrderService;

public class WorkStatus {

	private static Logger log = LoggerFactory.getLogger(WorkStatus.class.getName());	
	
	public static enum WORKSTATUSCATEGORY {
		CLOSED, CANCELLED, PREDISPATCH, PRECLOSE, INFIELD, OPERATIONAL;
		public String toString() {
			return super.toString();
		}
	};
	
	public static enum WORKSTATUSDESIGNATION {
		ISSUED, REISSUED, CLOSED, CANCELLED, RELEASED, RECALLED, CANTDO, PRECLOSEAPPROVAL, CUSTOMINFIELD, OPERATIONAL;
		public String toString() {
			return super.toString();
		}
	};
	
	Map<String, List<String>> categoryWorkStatuses;
	Map<String, List<String>> designationWorkStatuses;
	
	private String[] resultCloseApprovalStatuses;
	
	@Autowired
	private WorkOrderService workOrderService;
	
	private Map<String, List<SystemWorkStatus>> systemWorkStatuses;
	
	public WorkStatus() {
		super();
		categoryWorkStatuses = new HashMap<String, List<String>>();
		designationWorkStatuses = new HashMap<String, List<String>>();
	}
	
	private List<SystemWorkStatus> getSystemWorkStatuses(String frInstance) throws FRInstanceException {
		
		log.debug(">>> getSystemWorkStatuses");
		
		if(systemWorkStatuses == null) {
			systemWorkStatuses = new HashMap<String, List<SystemWorkStatus>>();
		}
		
		if(!systemWorkStatuses.containsKey(frInstance)) {
			
			systemWorkStatuses.put(frInstance, this.workOrderService.findSystemWorkStatus(frInstance));
		}
		
		log.debug("<<< getSystemWorkStatuses status=");
		
		return systemWorkStatuses.get(frInstance);

	}
	
	public List<String> getWorkIssuedIgnoreStatusList(String frInstance, boolean ignoreReleased) throws FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> getWorkIssuedIgnoreStatusList");

		List<String> ignoreStatusList = new ArrayList<String>();

		ignoreStatusList.add(this.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CANCELLED));
		ignoreStatusList.add(this.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CANTDO));
		ignoreStatusList.add(this.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.CLOSED));
		ignoreStatusList.add(this.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.RECALLED));
		
		String preApproval = this.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.PRECLOSEAPPROVAL);
		if(preApproval != null) {
			ignoreStatusList.add(preApproval);
		}

		if(!ignoreReleased) {
			ignoreStatusList.add(this.getSystemWorkStatusByDesignation(frInstance, WORKSTATUSDESIGNATION.RELEASED));
		}

		if (log.isDebugEnabled())
			log.debug("<<< getWorkIssuedIgnoreStatusList");

		return ignoreStatusList;
	}


	public String[] getResultCloseApprovalStatuses() {
		return resultCloseApprovalStatuses;
	}

	public void setResultCloseApprovalStatuses(String[] resultCloseApprovalStatuses) {
		this.resultCloseApprovalStatuses = resultCloseApprovalStatuses;
	}
	
	
	public List<String> getPreDispatchStatusList(String frInstance) throws FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> gePreReleaseStatusList");

		List<String> predispatchList = this.getSystemWorkStatusesByCategory(frInstance, WORKSTATUSCATEGORY.PREDISPATCH);
		
		if(predispatchList != null && predispatchList.size() == 0) {
			
			predispatchList = null;
		}
		
		if (log.isDebugEnabled())
			log.debug("<<< gePreReleaseStatusList");

		return predispatchList;
	}
	

	/*public boolean isConfiguredStatus(String status) {
		if(status == null) {
			return false;
		}
		
		for(String cApp : resultCloseApprovalStatuses) {
			if(status.equals(cApp)) {
				return true;
			}
		}
		
		return status.equals(this.releasedWorkStatus) 
				|| status.equals(this.issuedWorkStatus)
				|| status.equals(reissuedWorkStatus)
				|| status.equals(preCloseApprovalWorkStatus)
				|| status.equals(closeWorkStatus)
				|| status.equals(cantDoWorkStatus)
				|| status.equals(cancelWorkStatus)
				|| status.equals(recallWorkStatus);
	}*/
	
	public boolean isStatusConfigured(String frInstance, String status) throws FRInstanceException {

		log.debug(">>> isStatusConfigured status=" + status);
		
		boolean result = false;
		
		List<SystemWorkStatus> statuses = this.getSystemWorkStatuses(frInstance);
		
		if(statuses != null) {
			for(SystemWorkStatus statusObj : statuses) {
				if(statusObj.getId().equalsIgnoreCase(status)) {
					result = true;
					break;
				}
			}
		}
		
		log.debug("<<< isStatusConfigured status/result" + status + "/" + result);
		return result;
	}
	
	
	public List<String> getSystemWorkStatusesByCategory(String frInstance, WORKSTATUSCATEGORY category) throws FRInstanceException {
		
		log.debug(">>> getSystemWorkStatuses category="+category.toString());
		
		//get status from cache
		List<String> sysStatuses = categoryWorkStatuses.get(frInstance + "-" + category.toString());
		
		if(sysStatuses == null) {
			//not found in cache so try to get from list of statuses
			List<SystemWorkStatus> statuses = this.getSystemWorkStatuses(frInstance);
			
			if(statuses != null) {
				for(SystemWorkStatus status : statuses) {
					if(status.getStatusCategory().equalsIgnoreCase(category.toString())) {
						if(sysStatuses == null) {
							sysStatuses = new ArrayList<String>();
							categoryWorkStatuses.put(frInstance + "-" + category.toString(), sysStatuses);
						}
						sysStatuses.add(status.getId());
					}
				}
			}
		}
		log.debug("<<< getSystemWorkStatuses");
		
		return sysStatuses;
	}
	
	public List<String> getSystemWorkStatusesByDesignation(String frInstance, WORKSTATUSDESIGNATION designation) throws FRInstanceException {

		log.debug(">>> getSystemWorkStatusesByDesignation designation="+designation.toString());
		
		//get status from cache
		List<String> sysStatuses = designationWorkStatuses.get(frInstance + "-" + designation.toString());
		
		if(sysStatuses == null) {
			//not found in cache so try to get from list of statuses
			List<SystemWorkStatus> statuses = this.getSystemWorkStatuses(frInstance);
			
			if(statuses != null) {
				for(SystemWorkStatus status : statuses) {
					if(status.getStatusDesignation().equalsIgnoreCase(designation.toString())) {
						if(sysStatuses == null) {
							sysStatuses = new ArrayList<String>();
							designationWorkStatuses.put(frInstance + "-" + designation.toString(), sysStatuses);
						}
						sysStatuses.add(status.getId());
					}
				}
			}
		}

		log.debug("<<< getSystemWorkStatusesByDesignation");
		
		return sysStatuses;
	}
	
	public String getSystemWorkStatusByDesignation(String frInstance, WORKSTATUSDESIGNATION designation) throws FRInstanceException {
		
		log.debug(">>> getSystemWorkStatusByDesignation designation="+designation.toString());
		
		String workStatus = null;
		
		List<String> workStatusList = this.getSystemWorkStatusesByDesignation(frInstance, designation);
		
		if(workStatusList != null && workStatusList.size() > 0) {
			workStatus = workStatusList.get(0);
		}
		
		if(workStatus == null && designation != WORKSTATUSDESIGNATION.PRECLOSEAPPROVAL) {
			throw new FRInstanceException("Work Status " + designation.toString() + "Not Defined");
		}
		
		log.debug("<<< getSystemWorkStatusByDesignation");
		
		return workStatus;
	}
	
	public boolean statusIsDesignated(String frInstance, String status, WORKSTATUSDESIGNATION designation) {
		
		log.debug(">>> statusIsDesignated designation="+designation.toString());
		
		boolean result = false;
		
		List<String> sysStatuses;
		try {
			sysStatuses = this.getSystemWorkStatusesByDesignation(frInstance, designation);
			
			if(sysStatuses != null) {
				for(String workStatus : sysStatuses) {
					if(workStatus.equalsIgnoreCase(status)) {
						result = true;
						break;
					}
				}
			}
			
		} catch (FRInstanceException e) {
			log.debug("statusIsDesignated returning false for status " + status + "in designation " + designation.toString());
		}
		
		
		
		log.debug("<<< statusIsDesignated " + result);
		
		return result;
	}
	
}
