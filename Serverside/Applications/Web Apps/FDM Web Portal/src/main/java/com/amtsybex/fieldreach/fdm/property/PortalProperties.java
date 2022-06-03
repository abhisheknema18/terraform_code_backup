package com.amtsybex.fieldreach.fdm.property;

import java.util.List;

public class PortalProperties {

    private Boolean enableuserworkassignment;
    
    private String workOrderLocation;
    
    private Result result = new Result();

    private Refresh refresh = new Refresh();
    
    private Approval approval = new Approval();
	
	public Boolean getEnableuserworkassignment() {
		return enableuserworkassignment;
	}

	public void setEnableuserworkassignment(Boolean enableuserworkassignment) {
		this.enableuserworkassignment = enableuserworkassignment;
	}

	public String getWorkOrderLocation() {
		return workOrderLocation;
	}

	public void setWorkOrderLocation(String workOrderLocation) {
		this.workOrderLocation = workOrderLocation;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Refresh getRefresh() {
		return refresh;
	}

	public void setRefresh(Refresh refresh) {
		this.refresh = refresh;
	}

	public static class Result {

		private String templatelocation;

		public String getTemplatelocation() {
			return templatelocation;
		}

		public void setTemplatelocation(String templatelocation) {
			this.templatelocation = templatelocation;
		}

	}
	
	public static class Approval{
		
		private List<String> result;
		
		private List<String> closeWork;

		public List<String> getResult() {
			return result;
		}

		public void setResult(List<String> result) {
			this.result = result;
		}

		public List<String> getCloseWork() {
			return closeWork;
		}

		public void setCloseWork(List<String> closeWork) {
			this.closeWork = closeWork;
		}
		
	}
	
	public static class Refresh {
		private Integer interval;
		
		public Integer getInterval() {
			return interval;
		}
		
		public void setInterval(Integer interval) {
			this.interval = interval;
		}

	}

	public Approval getApproval() {
		return approval;
	}

	public void setApproval(Approval approval) {
		this.approval = approval;
	}

}

