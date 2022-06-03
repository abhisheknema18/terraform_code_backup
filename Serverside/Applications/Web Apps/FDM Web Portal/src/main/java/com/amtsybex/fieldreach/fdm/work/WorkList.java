package com.amtsybex.fieldreach.fdm.work;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.component.EditableValueHolder;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.MaxResultsExceededException;
import com.amtsybex.fieldreach.backend.exception.UserNotFoundException;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.WorkIssued;
import com.amtsybex.fieldreach.backend.model.WorkStatusHistory;
import com.amtsybex.fieldreach.backend.service.WorkOrderService;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardServiceManager;
import com.amtsybex.fieldreach.fdm.equipment.EquipmentInformation;
import com.amtsybex.fieldreach.fdm.property.PortalPropertyUtil;
import com.amtsybex.fieldreach.fdm.search.Search;
import com.amtsybex.fieldreach.fdm.search.SearchResult;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.services.endpoint.common.WorkStatus.WORKSTATUSDESIGNATION;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

@Named
@WindowScoped
public class WorkList extends PageCodebase implements Serializable{

	private static final long serialVersionUID = 964321042255183046L;

	private static Logger _logger = LoggerFactory.getLogger(WorkList.class.getName());
	
	public enum WorkViewMode {
		LIST, ALLOCATION, CLOSE_APPROVE, DASHBOARD;
		
		public String getPrettyString() {
			switch(this) {
			case LIST:
				return "Work Order Search";
			case ALLOCATION:
				return "Work Assignment and Dispatch";
			case CLOSE_APPROVE:
				return "Work Closure Approval";			
			case DASHBOARD:
					return "Work Order Search List";
			default: return "Work Order Search";
			}
		}
	}
		
	@Inject
	transient WorkOrderService workOrderService;
	
	@Inject
	transient DashboardServiceManager serviceManager;
	
	@Inject
	transient Search search;
	
	@Inject
	transient PortalPropertyUtil portalPropertyUtil;
	
	@Inject
	private WorkDetail workDetail;
	
	@Inject
	private EquipmentInformation equipmentInformation;
	
	private String woType;
	private List<String> ignoreStatusList;
	
	// maximum number of rows to display per page of the results screen
	private int numRows = 25;
	// specify what page the data scroller is initialised to
	private int page = 0;
	
	private WorkViewMode workViewMode;
	
	private List<WorkIssued> issued;
	private List<WorkIssued> issuedFiltered;
	
	private WorkIssued selectedWork;
	private List<WorkIssued> selectedWorks;
	
	private boolean next;
	private boolean previous;
	
	private int activeTab = 0;
	private int workDetailActiveTab = 0;
	
	private String recallReason;
	
	private HPCWorkGroups selectedWorkgroup;

	
	private HPCUsers selectedUser;
	
	
	private List<String> overrideWorkStatusOptions;
	
	private List<String> workStatusOptions;
	
	private String directWorkOrderNo; 
	private String directWorkGroup; 
	private String directStatus; 
	private String directMessage; 
	private String directMessageSummary; 
	

	private boolean allWorkgroupsSelected;
	private TreeNode workgroupRoot;  
	private TreeNode[] selectedWorkgroupNodes; 

	private boolean allUsersSelected;
	private TreeNode userRoot;  
	private TreeNode[] selectedUserNodes; 

	private TreeNode statusRoot;  
	private TreeNode[] selectedStatusNodes; 
	
	private TreeNode workgroupAssignedRoot; 
	private TreeNode userAssignedRoot; 
	private TreeNode selectedAssignedWorkgroupNode; 
	private TreeNode selectedAssigneduserNode; 
	
	private String selectedAssignedWorkGroupDisplay;
	private String selectedAssignedUserDisplay;
	
	
	private List<SearchResult> fieldClosedResults;
	private SearchResult selectedFieldClosedResult;
	
	private boolean resetFilters;
	
	//PRB0050354 - MC - undo on cancel of edit criteria
	private WorkCriteria searchCriteria;
	private WorkCriteria undoSearchCriteria;

	public WorkList() {
		
		super();
		
		reset();

	}
	
	public void cancel() {
		this.searchCriteria = new WorkCriteria(this.undoSearchCriteria);
		allWorkgroupsSelected = (searchCriteria.getWorkgroups()).isEmpty() ? true : false;
		allUsersSelected=(searchCriteria.getUsers()).isEmpty() ? true : false;
	}
	
	public void reset() {
		
		_logger.debug(">>> reset");
		
		this.searchCriteria = new WorkCriteria();
		this.undoSearchCriteria = new WorkCriteria();
		
		ignoreStatusList = null;
		woType = null;
		next = false;
		previous = false;
		
		workStatusOptions = new ArrayList<String>();
		workStatusOptions.add(Properties.get("fdm_work_status_option_wip"));
		workStatusOptions.add(Properties.get("fdm_work_status_option_closed_cancelled"));
		
		overrideWorkStatusOptions = null;

		selectedWorkgroup = null;
		selectedUser = null;

		workgroupRoot = null;  
		userRoot = null;  
		statusRoot = null;

		selectedWorkgroupNodes = null; 
		selectedUserNodes = null;  
		selectedStatusNodes = null; 
		
		allWorkgroupsSelected = true;
		allUsersSelected = true;
		
		//activeTab = 0;

		//issued = null;
		//issuedFiltered = null;
		//page = 0;
		
		//this.setWorkViewMode(WorkViewMode.LIST);
		
		//this.selectedWork = null;
		//this.selectedWorks = null;

		//this.clearFilters();
		
		_logger.debug("<<< reset");
	}

	
	public void resetResults(){
		
		_logger.debug(">>> resetResults");

		next = false;
		previous = false;
		issued = null;
		issuedFiltered = null;
		page = 0;

		this.setWorkViewMode(WorkViewMode.LIST);
		
		this.selectedWork = null;
		this.selectedWorks = null;

		this.clearFilters();
		
		_logger.debug("<<< resetResults");
	}
	
	public void resetSelectionAfterFilter() {

		if(this.selectedWorks != null) {
			Iterator<WorkIssued> workIterator = this.selectedWorks.iterator();
			while (workIterator.hasNext()) {
				if(!this.issuedFiltered.contains(workIterator.next())) {
					workIterator.remove();
				}
			}
		}
	}
	
	public void clearFilters() {
		//PrimeFaces.current().clearTableState("fdm/worklist.xhtml_searchTabs:workListResultsForm:singleWorkListTable");
		//PrimeFaces.current().clearTableState("fdm/worklist.xhtml_searchTabs:workListResultsForm:multiWorkListTable");
		PrimeFaces.current().multiViewState().clearAll();
		PrimeFaces.current().executeScript("PF('workorderTable').filter()");
	}
	
	
	/* - being taken out but just commenting as might be put back in at some stage
	public void cancelWork() throws FRInstanceException {
		
        List<WorkIssued> failedWos = new ArrayList<WorkIssued>();
		List<WorkIssued> successWos = new ArrayList<WorkIssued>();
		
		for(int i=0; i< this.selectedWorks.size(); i++) {
			
			WorkIssued work = selectedWorks.get(i);

			try {
				this.serviceManager.getWorkOrderController().cancelWorkOrder(null, work);
			}catch (Exception e) {
				failedWos.add(work);
			}

		}
		
		if(failedWos.size() > 0) {
			this.selectedWorks.removeAll(successWos);
			this.setSelectedWorks(successWos);
			MessageHelper.setGlobalWarnMessage("Cancelled "+successWos.size()+" work orders. Failed to cancel " +failedWos.size()+ " work orders");
		}else {
			MessageHelper.setGlobalWarnMessage("Cancelled "+successWos.size()+" work orders");
			this.setSelectedWorks(null);
		}

	}*/
	
	//FROM LIST SCREEN
	public void recallWorks() {
		
		_logger.debug(">>> recallWorks");
		
		if(this.selectedWorks == null) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_no_selection"));
			return;
		}
		
        List<WorkIssued> failedWos = new ArrayList<WorkIssued>();
		List<WorkIssued> successWos = new ArrayList<WorkIssued>();
		
		for(int i=0; i< this.selectedWorks.size(); i++) {
			
			WorkIssued work = selectedWorks.get(i);

			try {
				this.serviceManager.getWorkOrderController().recallWorkOrder(null, this.getUsername(), work.getWorkOrderNo(), work.getDistrictCode(), null);
				_logger.info("recalled work order " + work.getWorkOrderNo() + "," + work.getDistrictCode());
			}catch (Exception e) {
				_logger.warn("failed to recal work order " + work.getWorkOrderNo() + "," + work.getDistrictCode());
				failedWos.add(work);
				_logger.error("recallWorks failed wo " + e.getMessage());
			}

		}
		
		try {
			if(failedWos.size() > 0) {
				this.selectedWorks.removeAll(successWos);
				this.setSelectedWorks(successWos);

				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_list_recalled_failure", new Object[] {successWos.size(), failedWos.size()}));
			}else {
				MessageHelper.setGlobalInfoMessage(Properties.get("fdm_work_list_recalled_success", new Object[] {successWos.size()}));
				this.setSelectedWorks(null);
			}
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("recallWorks Unknown error occurred" + e.getMessage());
		}
		
		_logger.debug("<<< recallWorks");

	}
	
	//FROM DETAIL SCREEN
	public void recallWork() {
		
		_logger.debug(">>> recallWork");
		
		try {
			
			this.serviceManager.getWorkOrderController().recallWorkOrder(null, this.getUsername(), this.selectedWork.getWorkOrderNo(), this.selectedWork.getDistrictCode(), this.recallReason);
			
			this.updateWorkOrderFromDB(this.getSelectedWork());
			
			MessageHelper.setGlobalInfoMessage(Properties.get("fdm_work_recalled_success"));
			
			_logger.info("recalled work order " + this.selectedWork.getWorkOrderNo() + "," + this.selectedWork.getDistrictCode());
			
		}catch (Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_recalled_failure"));
			_logger.error("Failed to recall work order " + e.getMessage());
		}
		
		_logger.debug("<<< recallWork");
		
	}
	
	
	public void closeWork() {

		_logger.debug(">>> closeWork");
		
		SearchResult fieldClosedResult = null;
		//check for results in field complete
		
		String[] closeStatuses = this.serviceManager.getWorkOrderController().getWorkStatuses().getResultCloseApprovalStatuses();
		
		if(closeStatuses != null) {
			List<String> cStatusList = new ArrayList<String>(Arrays.asList(closeStatuses));
			for(SearchResult result : this.search.getSearchResults().getResults()) {
				
				if(cStatusList.contains(result.getResultStatus())) {
					
					if(fieldClosedResult != null) {
						
						long dateTime = Long.parseLong(String.valueOf(result.getCompleteDate()) + Utils.correctFieldreachDBTime(result.getCompleteTime()));
						
						if(dateTime > Long.parseLong(String.valueOf(fieldClosedResult.getCompleteDate()) + Utils.correctFieldreachDBTime(fieldClosedResult.getCompleteTime()))) {
							fieldClosedResult = result;
						}
						
					}else {
						fieldClosedResult = result;
					}
				}
			}
		}
		
		try {
			
			if(fieldClosedResult != null) {
				//update script status to closed (next success status)
				this.serviceManager.getScriptResultsService().updateResultStatus(null, fieldClosedResult.getId());
				this.search.searchByWorkOrder(this.getSelectedWork().getId().getWorkOrderNo());
				this.workDetail.setResultHistory(this.search.getSearchResults().getResults());
			}
			
			this.serviceManager.getWorkOrderController().closeWorkOrder(null, this.getUsername(), this.selectedWork.getWorkOrderNo(), this.selectedWork.getDistrictCode());
			
			this.updateWorkOrderFromDB(this.getSelectedWork());
			
			MessageHelper.setGlobalInfoMessage(Properties.get("fdm_work_closed_success"));
			
			_logger.info("closed work order " + this.selectedWork.getWorkOrderNo() + "," + this.selectedWork.getDistrictCode());
			
		}catch (Exception e) {
			_logger.warn("failed to close work order " + this.selectedWork.getWorkOrderNo() + "," + this.selectedWork.getDistrictCode());
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_closed_failure"));
			_logger.error("Failed to close work order " + e.getMessage());
		}
		
		_logger.debug("<<< closeWork");
	}
	

	public void dispatchWorks(boolean fromAssign) {
		
		_logger.debug(">>> dispatchWorks");
		
		if(this.selectedWorks == null) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_no_selection"));
			return;
		}
		
        List<WorkIssued> failedWos = new ArrayList<WorkIssued>();
		List<WorkIssued> successWos = new ArrayList<WorkIssued>();
		
		for(int i=0; i< this.selectedWorks.size(); i++) {
			
			WorkIssued work = selectedWorks.get(i);

			try {
				this.serviceManager.getWorkOrderController().dispatchWorkOrder(null, work.getWorkOrderNo(), work.getDistrictCode());
				
				//remove from search
				WorkIssued wo;
				for(int j=0; j<this.issued.size(); j++) {
					
					wo = issued.get(j);
					if(work.getWorkOrderNo().equals(wo.getWorkOrderNo())) {
						issued.remove(wo);
						break;
					}
				}
				if(issuedFiltered != null) {

					PrimeFaces.current().executeScript("PF('workorderTable').filter()");
				}
				
				
				successWos.add(work);
				
				_logger.info("dispatched work order " + work.getWorkOrderNo() + "," + work.getDistrictCode());
				
			}catch (Exception e) {
				_logger.warn("failed to dispatch work order " + work.getWorkOrderNo() + "," + work.getDistrictCode());
				failedWos.add(work);
				_logger.error("failed to dispatch work order " + e.getMessage());
			}

		}
		
		try {
			if(failedWos.size() > 0) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_list_dispatch_failure", new Object[] {successWos.size(), failedWos.size()}));
				this.selectedWorks.removeAll(successWos);
				this.setSelectedWorks(failedWos);
				
			}else {
				this.setSelectedWorks(null);
				if(fromAssign) {
					MessageHelper.setGlobalInfoMessage(Properties.get("fdm_work_list_assign_dispatch_success", new Object[] {successWos.size()}));
				}else {
					MessageHelper.setGlobalInfoMessage(Properties.get("fdm_work_list_dispatch_success", new Object[] {successWos.size()}));
				}
				
			}
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("dispatchWorks Unknown error occurred" + e.getMessage());
		}
		
		_logger.debug("<<< dispatchWorks");
		
	}
	
	public void dispatchWork() {

		_logger.debug(">>> dispatchWork");
		
		try {
			
			if(this.workDetail.getWork() != null) {
				//This is being done here due to a bug in primefaces, there is no way to trap the close event on the popup sidebar
				//we need to revert any changes that might have been made and cancelled in the edit/reissue dialogs
				this.cancelEditDetailMode();
			}
			
			this.serviceManager.getWorkOrderController().dispatchWorkOrder(null, this.getSelectedWork().getWorkOrderNo(),  this.getSelectedWork().getDistrictCode());
			
			this.updateWorkOrderFromDB(this.getSelectedWork());
			 
			MessageHelper.setGlobalInfoMessage(Properties.get("fdm_work_dispatched_success"));
			 
			 _logger.info("dispatched work order " + this.getSelectedWork().getWorkOrderNo() + "," + this.getSelectedWork().getDistrictCode());
			 
		}catch (Exception e) {
			 _logger.warn("failed to dispatch work order " + this.getSelectedWork().getWorkOrderNo() + "," + this.getSelectedWork().getDistrictCode());
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_dispatched_failure"));
			_logger.error("Failed to dispatch work order " + e.getMessage());
		}

		_logger.debug("<<< dispatchWork");
		
	}
	
	
	public void reIssueWork() {

		_logger.debug(">>> reIssueWork");
		this.assignSaveWork(true);
		_logger.info("reissued work order " + this.getSelectedWork().getWorkOrderNo() + "," + this.getSelectedWork().getDistrictCode());
		 
		_logger.debug("<<< reIssueWork");
	}
	
	
	public void assignSaveWorks(boolean dispatch) {        
        
		_logger.debug(">>> assignSaveWorks");
		
		if(this.selectedWorks == null) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_no_selection"));
			return;
		}

		List<WorkIssued> failedWos = new ArrayList<WorkIssued>();
		List<WorkIssued> successWos = new ArrayList<WorkIssued>();
		
		//save and update from work order list
		for(int i=0; i< this.selectedWorks.size(); i++) {
			
			WorkIssued work = selectedWorks.get(i);
			
			try {
				work.setWorkgroupCode(this.selectedAssignedWorkGroupDisplay);
				work.setUserCode(this.selectedAssignedUserDisplay);
				if(this.selectedAssigneduserNode != null) {
					work.setUser((HPCUsers)this.selectedAssigneduserNode.getData());
				}else {
					work.setUser(null);
				}
				
				if(this.selectedAssignedWorkgroupNode != null) {
					work.setWorkGroup((HPCWorkGroups)this.selectedAssignedWorkgroupNode.getData());
				}else {
					work.setWorkGroup(null);
				}

				
				
				this.saveWorkOrder(work);

		        work = this.serviceManager.getWorkService().findWorkIssued(null, work.getWorkOrderNo(), work.getDistrictCode());
				
		        //remove from search or update work insearch
				WorkIssued wo;
				for(int j=0; j<this.issued.size(); j++) {
					
					wo = issued.get(j);
					if(work.getWorkOrderNo().equals(wo.getWorkOrderNo())) {
						issued.set(j, work);
						break;
					}
				}
				
				successWos.add(work);
				
				_logger.info("saved work order " + work.getWorkOrderNo() + "," + work.getDistrictCode());
				 
			
			} catch (Exception e) {
				
				_logger.warn("failed to saved work order " + work.getWorkOrderNo() + "," + work.getDistrictCode());
				_logger.error(e.getMessage());
				
				try {
					//revert work order to before edits were made
					work  = this.serviceManager.getWorkService().findWorkIssued(null, work.getWorkOrderNo(), work.getDistrictCode());
				}catch(Exception ex) {
					MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
					_logger.error("assignSaveWorks Unknown error occurred" + e.getMessage());
					return;
				}
				
				
				WorkIssued wo;
				for(int j=0; j<this.issued.size(); j++) {
					
					wo = issued.get(j);
					if(work.getWorkOrderNo().equals(wo.getWorkOrderNo())) {
						issued.set(j, work);
						break;
					}
				}
				
				failedWos.add(work);
			}
			
			
		}

		try {

			if(failedWos.size() > 0) {

				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_list_assign_failure", new Object[] {successWos.size(),failedWos.size()}));

				this.selectedWorks.clear();
				//this.setSelectedWorks(failedWos);
				
			}else {
				
				if(dispatch) {
					this.dispatchWorks(true);
				}
				else {
					
					MessageHelper.setGlobalInfoMessage(Properties.get("fdm_work_list_assign_success", new Object[] {successWos.size()}));
					
					this.selectedWorks.clear();
					//this.setSelectedWorks(failedWos);
					
				}
			}
			
			
			if(issuedFiltered != null) {

				PrimeFaces.current().executeScript("PF('workorderTable').filter()");
			}
			
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("assignSaveWorks Unknown error occurred" + e.getMessage());
		}

		_logger.debug("<<< assignSaveWorks");

    }
	
	public void assignSaveWork(boolean dispatch) {
		
		_logger.debug(">>> assignSaveWork");
		
		try {

			if(this.workDetail.getTempStartDate() != null && this.workDetail.getTempFinsihDate() != null && this.workDetail.getTempFinsihDate().before(this.getWorkDetail().getTempStartDate())) {
				
				MessageHelper.setErrorMessage("validationMessages", Properties.get("fdm_work_edit_dates"));
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				
				//this.cancelEditDetailMode();
				
				_logger.debug("<<< assignSaveWork Planned Start Date after Required Finish Date");
				
				return;
				//throw new Exception("Planned Start Date after Required Finish Date");
			}
			
			if(this.workDetail.getTempStartDate() == null && this.workDetail.getTempStartTime() != null) {
				
				MessageHelper.setErrorMessage("validationMessages", "Planned Start Time must have a Planned Start Date");
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				
				return;
			}
			
			if(this.workDetail.getTempFinsihDate() == null && this.workDetail.getTempFinsihTime() != null) {
				
				MessageHelper.setErrorMessage("validationMessages", "Required Finish Time must have a Required Finish Date");
				FacesContext.getCurrentInstance().validationFailed();
				PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
				
				return;
			}
			
			this.workDetail.createDatesFromTempDates();
			
			//save and update from work order detail
			WorkIssued work = this.workDetail.getWork();

			this.saveWorkOrder(work);
			
	        if(dispatch) {
		        //call dispatch ws for work order
				this.serviceManager.getWorkOrderController().dispatchWorkOrder(null, work.getWorkOrderNo(), work.getDistrictCode());
	        }
			
	        this.updateWorkOrderFromDB(work);

			MessageHelper.setGlobalInfoMessage(Properties.get("fdm_work_edit_success"));
			
			_logger.info("saved work order " + work.getWorkOrderNo() + "," + work.getDistrictCode());
			
		} catch (Exception e) {
			
			_logger.warn("Failed to update work order " + this.workDetail.getWork().getWorkOrderNo() + "," + this.workDetail.getWork().getDistrictCode());
			_logger.error(e.getMessage());
			
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_edit_failure"));
			
			this.cancelEditDetailMode();
			
			MessageHelper.setGlobalInfoMessage(Properties.get("fdm_work_edit_revert"));

		}
		
		_logger.debug("<<< assignSaveWork");
	}
	
	private void updateWorkOrderFromDB(WorkIssued work) throws FRInstanceException {
		
		_logger.debug(">>> updateWorkOrderFromDB");
		
		//retrieve updated work order details from db
		work = this.serviceManager.getWorkService().findWorkIssued(null, work.getWorkOrderNo(), work.getDistrictCode());
		
		WorkIssued wo;
		for(int i=0; i<this.issued.size(); i++) {

			wo = issued.get(i);
			if(work.getWorkOrderNo().equals(wo.getWorkOrderNo())) {
				issued.set(i, work);
				break;
			}
		}
		if(issuedFiltered != null) {

			for(int i=0; i<this.issuedFiltered.size(); i++) {
	
				wo = issuedFiltered.get(i);
				if(work.getWorkOrderNo().equals(wo.getWorkOrderNo())) {
					issuedFiltered.set(i, work);
					break;
				}
			}
		}
		
		this.setSelectedWork(work);
		
		_logger.debug("<<< updateWorkOrderFromDB");
	}
	
	private void saveWorkOrder(WorkIssued work) throws ResourceTypeNotFoundException, ResourceNotFoundException, FRInstanceException, ParserConfigurationException, SAXException, IOException, TransformerException {
        
		_logger.debug(">>> saveWorkOrder");
		
		this.serviceManager.getWorkOrderController().modifyWorkOrder(null, this.getUsername(), work);
		
		_logger.debug("<<< saveWorkOrder");
        
	}
	

	public void searchDirect() {
		
		_logger.debug(">>> searchDirect");
		
		if(!FacesContext.getCurrentInstance().isPostback()) {

			if(this.workViewMode == null || resetFilters) {
				//reset any filters that may be on the result table
				PrimeFaces.current().executeScript("PF('workorderTable').clearFilters()");

				resetFilters = false;
			}
			
			PrimeFaces.current().executeScript("PF('workorderTable').unselectAllRows()");
			

			if(!this.emptyDirectSearchTerms()) {

				this.reset();
				this.resetResults();
				
				String workOrderNo = this.directWorkOrderNo;
				String status = this.directStatus;
				String workGroup = this.directWorkGroup;
				
				this.directWorkOrderNo = null;
				this.directStatus = null;
				this.directWorkGroup = null;

				try {
					if(workOrderNo != null && workOrderNo.length() > 0) {
						WorkIssued work = workOrderService.findWorkIssuedByWorkOrderNo(null, workOrderNo);
						
						if(work != null) {
							
							if(this.fetchWorkgroupStrings().contains(work.getWorkgroupCode())) {
								issued = new ArrayList<WorkIssued>();
								issued.add(work);
							}

						}
					}else {
						List<String> statusList = null;
						if(status != null && status.length() > 0) {
							statusList = new ArrayList<String>();
							statusList.add(status);
						}

						List<String> workGroupList = null;
						
						if(workGroup != null && workGroup.length() > 0) {
							workGroupList = new ArrayList<String>();
							workGroupList.add(workGroup);
						}

						try {
						issued = workOrderService.findWorkIssuedInDateRangeForWorkGroups(null, this.getUsername(), null, null, null, workGroupList, null, null, statusList, null, true, false);
						}catch (MaxResultsExceededException e) {
							// if too many results are returned, tell the user to reduce search options
							_logger.warn("Search returned too many results " + e.getMessage());
							MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_results_too_many_results_found_label"));
							return;
						} catch (UserNotFoundException e) {
							_logger.warn("user not found for search " + e.getMessage());
							MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
							return;
						}
					}

					if (issued == null || issued.size() == 0){
						// if no results are found, display a warning message stating so
						MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_results_found"));
						directMessage = Properties.get("fdm_search_no_results_found");
						activeTab = 0;
					}
				}catch (Exception e) {
					directMessage = Properties.get("fdm_error_unknown");
					_logger.error("searchDirect Unknown error occurred" + e.getMessage());
					return;
				}
				
			}
		}else if(this.directMessage != null) {
			//Problem in JSF when using templates means prerenderview event gets called twice
			//this if statement should only be entered on second run of this function if an 
			//error message was added in the first run through.
			MessageHelper.setGlobalWarnMessage(directMessage + directMessageSummary);
			this.directMessage = null;
		}
		
		_logger.debug("<<< searchDirect");
		
	}
	
	
	public boolean emptyDirectSearchTerms(){
		return ((this.directStatus == null || "".equals(this.directStatus.trim())) &&
				(this.directWorkGroup == null || "".equals(this.directWorkGroup.trim())) &&
				(this.directWorkOrderNo == null || "".equals(this.directWorkOrderNo.trim())));
	}
	
	
	public boolean showFilter() {
		
		_logger.debug(">>> showFilter");
		
		if(this.workViewMode == WorkViewMode.LIST) {
			_logger.debug("<<< showFilter true");
			return true;
		}
		_logger.debug("<<< showFilter false");
		return false;
	}
	
	public void loadWorkClosureApproval()  {
		
		_logger.debug(">>> loadWorkClosureApproval");
		
		this.reset();
		this.resetResults();

		activeTab = 0;
		
		List<String> status = new ArrayList<String>();
		
		try {
			String sysStatus = this.serviceManager.getWorkOrderController().getWorkStatuses().getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.PRECLOSEAPPROVAL);
			
			if(sysStatus == null) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
				return;
			}
			
			status.add(sysStatus);
			
		} catch (FRInstanceException e1) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			return;
		}

		
		try {
			issued = workOrderService.findWorkIssuedInDateRangeForWorkGroups(null, this.getUsername(), null, null, null, this.fetchWorkgroupStrings(), null, null, status, null, true, false);
		} catch (MaxResultsExceededException e) {
			_logger.warn("Search returned too many results " + e.getMessage());
			// if too many results are returned, tell the user to reduce search options
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_results_too_many_results_found_label"));
			return;
		} catch (UserNotFoundException e) {
			_logger.warn("Search user not found " + e.getMessage());
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
			return;
		} catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadWorkClosureApproval Unknown error occurred" + e.getMessage());
			return;
		}
		
		this.setWorkViewMode(WorkViewMode.CLOSE_APPROVE);
		
		if (issued == null || issued.size() == 0){
			// if no results are found, display a warning message stating so
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_results_found"));
		}
		
		resetFilters = true;

		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");

		_logger.debug("<<< loadWorkClosureApproval");
		
	}
	
	public void loadWorkAllocation() {
		
		_logger.debug(">>> loadWorkAllocation");
		
		this.reset();
		this.resetResults();

		activeTab = 0;
		
		try {
			issued = workOrderService.findWorkIssuedInDateRangeForWorkGroups(null, this.getUsername(), null, null, null, this.fetchWorkgroupStrings(), null, null, this.serviceManager.getWorkOrderController().getWorkStatuses().getPreDispatchStatusList(null), null, true, false);
		} catch (MaxResultsExceededException e) {
			_logger.warn("Search returned too many results " + e.getMessage());
			// if too many results are returned, tell the user to reduce search options
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_results_too_many_results_found_label"));
			return;
		} catch (UserNotFoundException e) {
			_logger.warn("Search user not found " + e.getMessage());
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
			return;
		} catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadWorkAllocation Unknown error occurred" + e.getMessage());
			return;
		}
		
		this.setWorkViewMode(WorkViewMode.ALLOCATION);
		
		if (issued == null || issued.size() == 0){
			// if no results are found, display a warning message stating so
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_results_found"));
		}
		
		resetFilters = true;
		
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");

		_logger.debug("<<< loadWorkAllocation");
		
	}
	
	public void searchAndHideFilterOnDisplay(boolean isFinishDate) {
		
		this.setWorkViewMode(WorkViewMode.DASHBOARD);
		this.search(isFinishDate);
		this.reset();
		this.activeTab = 0;
		this.resetFilters = true;
	}
	
	public void search(boolean isFinishDate) {
		
		_logger.debug(">>> search");

		this.undoSearchCriteria = new WorkCriteria(this.searchCriteria);
		
		activeTab = 0;
		
		// if no search terms are entered, find all scripts
		if (emptySearchTerms()){
			
			try {
				
				resetFilters = true;
				
				List<String> releasedStatus = new ArrayList<String>();
				
				try {
					releasedStatus.add(this.serviceManager.getWorkOrderController().getWorkStatuses().getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.RELEASED));
				} catch (FRInstanceException e1) {
					MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
					if(this.getWorkViewMode() != WorkViewMode.DASHBOARD) {
						return ;
						
					}else {
						this.resetResults();
						FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");
					}
				}
				issued = workOrderService.findWorkIssuedInDateRangeForWorkGroups(null, this.getUsername(), null, null, null, this.fetchWorkgroupStrings(), null, releasedStatus, null, null, false, isFinishDate);
			
				this.issuedFiltered = issued;
				
			} catch (MaxResultsExceededException e) {
				_logger.warn("Search returned too many results " + e.getMessage());
				// if too many results are returned, tell the user to reduce search options
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_results_too_many_results_found_label"));
				if(this.getWorkViewMode() != WorkViewMode.DASHBOARD) {
					return ;
					
				}else {
					this.resetResults();
					FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");
				}
			} catch (UserNotFoundException e) {
				_logger.warn("Search user not found " + e.getMessage());
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
				if(this.getWorkViewMode() != WorkViewMode.DASHBOARD) {
					return ;
				}else {
					this.resetResults();
					FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");
				}
			} catch(Exception e) {
				MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
				_logger.error("search Unknown error occurred" + e.getMessage());
				if(this.getWorkViewMode() != WorkViewMode.DASHBOARD) {
					return ;
					
				}else {
					this.resetResults();
					FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");
				}
			}
			
		}else {
			
			if(this.searchCriteria.getFromDate() == null ^ this.searchCriteria.getToDate() == null) {
				if (this.getWorkViewMode() != WorkViewMode.DASHBOARD) {
					_logger.debug("<<< Work order search only one date set");
					MessageHelper.setErrorMessage(null, Properties.get("fdm_search_both_dates_need_set"));
					FacesContext.getCurrentInstance().validationFailed();
					PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
					
					try {
						EditableValueHolder component = (EditableValueHolder) FacesContext.getCurrentInstance().getViewRoot().findComponent("workSearchTabs:searchForm:fromDate");
						component.setValid(false);
						component = (EditableValueHolder) FacesContext.getCurrentInstance().getViewRoot().findComponent("workSearchTabs:searchForm:toDate");
						component.setValid(false);
					}catch(Exception e) {}

					return;
				}
			}
			if (this.searchCriteria.getFromDate() != null && this.searchCriteria.getToDate() != null) {
				if (this.searchCriteria.getFromDate().after(this.searchCriteria.getToDate())) {
					if (this.getWorkViewMode() != WorkViewMode.DASHBOARD) {
						_logger.debug("<<< Work order search from date greater than to date");
						MessageHelper.setErrorMessage(null, Properties.get("fdm_search_from_before_to"));
						FacesContext.getCurrentInstance().validationFailed();
						PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);

						try {
							EditableValueHolder component = (EditableValueHolder) FacesContext.getCurrentInstance().getViewRoot().findComponent("workSearchTabs:searchForm:fromDate");
							component.setValid(false);
							component = (EditableValueHolder) FacesContext.getCurrentInstance().getViewRoot().findComponent("workSearchTabs:searchForm:toDate");
							component.setValid(false);
						}catch(Exception e) {}
						// throw new Exception("Planned Start Date after Required Finish Date");
						return;
					}
				}
			}
			
			//TODO FROM DATE
			Integer fromD = null;
			String fromT = null;
			if(this.searchCriteria.getFromDate() != null) {
				fromD = Common.generateFieldreachDBDate(this.searchCriteria.getFromDate());
				if(!isFinishDate) {
					fromT = Common.FIELDREACH_TIME_FORMAT.format(this.searchCriteria.getFromDate());
				}
			}
			
			Integer toD = null;
			if(this.searchCriteria.getToDate() != null) {
				toD = Common.generateFieldreachDBDate(this.searchCriteria.getToDate());
			}

			resetFilters = true;
			this.resetResults();
			
			List<String> groupCodes = null;
			List<String> userCodes = null;
			List<String> statuses = null;
			
			// build a list of workgroup codes from the list of workgroup objects
			if (searchCriteria.getWorkgroups().size() > 0){
				groupCodes = new ArrayList<String>();
				for (HPCWorkGroups w : searchCriteria.getWorkgroups()){
					groupCodes.add(w.getWorkgroupCode());
				}
			}

			// build a list of user codes from the list of user objects
			if (searchCriteria.getUsers().size() > 0){
				userCodes = new ArrayList<String>();
				for (HPCUsers u : searchCriteria.getUsers()){
					userCodes.add(u.getId().getUserCode());
				}
			}

			// copy the list of statuses
			if(this.getOverrideWorkStatusOptions() != null) {
				statuses = this.getOverrideWorkStatusOptions();
			}else if (this.searchCriteria.getSelectedWorkStatusOption().length == 1){
				if(this.searchCriteria.getSelectedWorkStatusOption()[0].equals((Properties.get("fdm_work_status_option_closed_cancelled")))) {

					ignoreStatusList = null;
					statuses = new ArrayList<String>();
					
					try {
						statuses.add(this.serviceManager.getWorkOrderController().getWorkStatuses().getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.CANCELLED));
						statuses.add(this.serviceManager.getWorkOrderController().getWorkStatuses().getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.CLOSED));
					} catch (FRInstanceException e1) {
						MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
						if(this.getWorkViewMode() != WorkViewMode.DASHBOARD) {
							return ;
							
						}else {
							this.resetResults();
							FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");
						}
					}
					
				}else if(this.searchCriteria.getSelectedWorkStatusOption()[0].equals((Properties.get("fdm_work_status_option_wip")))) {
					
					statuses = null;
					ignoreStatusList = new ArrayList<String>();

					try {
						ignoreStatusList.add(this.serviceManager.getWorkOrderController().getWorkStatuses().getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.CANCELLED));
						ignoreStatusList.add(this.serviceManager.getWorkOrderController().getWorkStatuses().getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.CLOSED));
					} catch (FRInstanceException e1) {
						MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
						if(this.getWorkViewMode() != WorkViewMode.DASHBOARD) {
							return ;
							
						}else {
							this.resetResults();
							FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");
						}
					}
					
				}

			}else if(this.searchCriteria.getSelectedWorkStatusOption().length == 2) {
				
				//search for everything
				statuses = null;
				ignoreStatusList = null;
			}
			
			/*if(ignoreStatusList == null) {
				ignoreStatusList = new ArrayList<String>();
			}
			if(!ignoreStatusList.contains(this.serviceManager.getWorkOrderController().getWorkStatuses().getReleasedWorkStatus())) {
				ignoreStatusList.add(this.serviceManager.getWorkOrderController().getWorkStatuses().getReleasedWorkStatus());
			}*/
			
			try {

				issued = workOrderService.findWorkIssuedInDateRangeForWorkGroups(null, this.getUsername(), fromD, fromT, toD, groupCodes, userCodes, ignoreStatusList, statuses, woType, false, isFinishDate);
			
				this.issuedFiltered = issued;
				
			}catch (MaxResultsExceededException e) {
				_logger.warn("Search returned too many results " + e.getMessage());
				// if too many results are returned, tell the user to reduce search options
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_results_too_many_results_found_label"));
				if(this.getWorkViewMode() != WorkViewMode.DASHBOARD) {
					return ;
					
				}else {
					this.resetResults();
					FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");
				}
			} catch (UserNotFoundException e) {
				_logger.warn("Search user not found " + e.getMessage());
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_login_username_not_found_exception_label"));
				if(this.getWorkViewMode() != WorkViewMode.DASHBOARD) {
					return ;
					
				}else {
					this.resetResults();
					FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");
				}
			}	catch(Exception e) {
				MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
				_logger.error("search Unknown error occurred" + e.getMessage());
				if(this.getWorkViewMode() != WorkViewMode.DASHBOARD) {
					return ;
					
				}else {
					this.resetResults();
					FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");
				}
			}
		}
		
		if (issued == null || issued.size() == 0){
			// if no results are found, display a warning message stating so
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_results_found"));
			if(this.getWorkViewMode() != WorkViewMode.DASHBOARD) {
				return ;
			}else {
				this.resetResults();
				
			}
		}
		
		_logger.debug("<<< search");
		
		
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");
	}
	
	
	public void searchWONumber() {
		
		_logger.debug(">>> searchWONumber");

		activeTab = 1;
		try {
			
			this.undoSearchCriteria = new WorkCriteria(this.searchCriteria);
			
			issued = workOrderService.findWorkIssuedByWorkOrderNoLike(null, getUsername(), searchCriteria.getWoNumber());
			this.issuedFiltered = issued;
			
			if (issued == null || issued.size() == 0){
				// if no results are found, display a warning message stating so
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_search_no_results_found"));
				if(this.getWorkViewMode() != WorkViewMode.DASHBOARD) {

					return ;
				}else {

					this.resetFilters = true;
					this.resetResults();
					
				}
			}
		}catch(Exception e) {
			MessageHelper.setGlobalErrorMessage(Properties.get("fdm_error_unknown"));
			if(this.getWorkViewMode() != WorkViewMode.DASHBOARD) {
				return ;
			}else {

				this.resetFilters = true;
				this.resetResults();
				
			}
			//MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("searchWONumber Unknown error occurred" + e.getMessage());
		}
		this.setWorkViewMode(WorkViewMode.LIST);
		_logger.debug("<<< searchWONumber");
		
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");

	}
	
	public boolean emptySearchTerms(){
		return searchCriteria.emptySearchTerms();
	}
	
	public List<String> fetchWorkgroupStrings() throws UserNotFoundException, FRInstanceException {
		
		_logger.debug(">>> fetchWorkgroupStrings");
		
		List<String> fetchedWorkgroups = null;
		
		if(!this.serviceManager.getUserService().hasUnlimitedAccessibleWorkgroups(null, getUsername())) {
			fetchedWorkgroups = new ArrayList<String>();
			
			List<HPCWorkGroups> wgs = fetchWorkgroups();

			if (wgs != null){
				Iterator<HPCWorkGroups> workgroupIterator = wgs.iterator();
				while(workgroupIterator.hasNext()){
					fetchedWorkgroups.add(((HPCWorkGroups)workgroupIterator.next()).getWorkgroupCode());
				}
			}
		}

		_logger.debug("<<< fetchWorkgroupStrings");
		
		return fetchedWorkgroups;
	}
	
	
	public List<HPCWorkGroups> fetchWorkgroups() throws UserNotFoundException, FRInstanceException {
		
		_logger.debug(">>> fetchWorkgroups");
		
		List<HPCWorkGroups> workGroupList = new ArrayList<HPCWorkGroups>();
		
			workGroupList = this.serviceManager.getUserService().getAccessibleWorkgroups(null, getUsername());
			
		
		_logger.debug("<<< fetchWorkgroups");
		return workGroupList;
	}
	
	/**
	 * method used to navigate users to the search screen when they click the search
	 * button on the left menu
	 * @throws FRInstanceException 
	 * @throws UserNotFoundException 
	 * @throws IOException 
	 */
	public void navigate() {
		
		_logger.debug(">>> navigate");
		
		this.reset();
		//FacesContext.getCurrentInstance().getExternalContext().redirect("/fdmwebportal/fdm/worklist.xhtml");
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "worklist");
		
		_logger.debug("<<< navigate");
	}
	
	
	public void findWorkByRowDetails(WorkIssued selectedWork){
		
		_logger.debug(">>> findWorkByRowDetails");
		
		try {
			
			this.setSelectedWork(selectedWork);
			
			updateNavigation();
			
			this.search.searchByWorkOrder(selectedWork.getId().getWorkOrderNo());
			
			this.workDetail.setResultHistory(this.search.getSearchResults().getResults());
			
			this.equipmentInformation.populateEquipmentAssetInformation(selectedWork.getEquipNo());
			
			this.workDetail.addMarker();
			
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "workdetail");
				
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("findWorkByRowDetails Unknown error occurred" + e.getMessage());
			return;
		}

		_logger.debug("<<< findWorkByRowDetails");

	}
	
	public void navigateNext() throws FRInstanceException, MaxResultsExceededException{
		_logger.debug(">>> navigateNext");
		nextResult();
		_logger.debug("<<< navigateNext");
	}
	
	public void nextResult() throws MaxResultsExceededException, FRInstanceException {
		//workDetail = new WorkDetail();
		
		WorkIssued wo = null;
		int index = 0;
		for(int i=0; i<this.issuedFiltered.size(); i++) {
			
			wo = issuedFiltered.get(i);
			if(this.getSelectedWork().getWorkOrderNo().equals(wo.getWorkOrderNo())) {
				index = i;
				break;
			}
		}
		
		if ((issuedFiltered.size()-1) > index){
			this.setSelectedWork(issuedFiltered.get(index+1));
		}
		
		this.equipmentInformation.populateEquipmentAssetInformation(this.workDetail.getWork().getEquipNo());		
		
		updateNavigation();
		
		this.workDetail.addMarker();
		
		this.search.searchByWorkOrder(this.workDetail.getWork().getId().getWorkOrderNo());
		
		this.workDetail.setResultHistory(this.search.getSearchResults().getResults());

	}
	
	/**
	 * method called when the user clicks the previous link on the details page
	 * @throws FRInstanceException 
	 * @throws MaxResultsExceededException 
	 */
	public void navigatePrevious() throws FRInstanceException, MaxResultsExceededException{
		_logger.debug(">>> navigatePrevious");
		previousResult();
		_logger.debug("<<< navigatePrevious");
	}
	
	public void previousResult() throws FRInstanceException, MaxResultsExceededException{
		//workDetail = new WorkDetail();
		WorkIssued wo = null;
		int index = 0;
		for(int i=0; i<this.issuedFiltered.size(); i++) {
			
			wo = issuedFiltered.get(i);
			if(this.getSelectedWork().getWorkOrderNo().equals(wo.getWorkOrderNo())) {
				index = i;
				break;
			}
		}
		
		if (index > 0){
			this.setSelectedWork(issuedFiltered.get(index-1));
		}
		
		this.equipmentInformation.populateEquipmentAssetInformation(this.workDetail.getWork().getEquipNo());
		
		updateNavigation();
		
		this.workDetail.addMarker();
		
		this.search.searchByWorkOrder(this.workDetail.getWork().getId().getWorkOrderNo());
		
		this.workDetail.setResultHistory(this.search.getSearchResults().getResults());

	}
	
	public String navigateReturn(){
		
		_logger.debug(">>> navigateReturn");
		
		this.selectedWork = null;
		this.selectedWorks = null;

		//FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), "worklist", "worklist");
		
		_logger.debug("<<< navigateReturn");
		
		return "worklist";
	}
	
	public void updateNavigation(){
		
		workDetailActiveTab = 0;
				
		WorkIssued wo = null;
		int index = 0;
		for(int i=0; i<this.issuedFiltered.size(); i++) {
			
			wo = issuedFiltered.get(i);
			if(this.getSelectedWork().getWorkOrderNo().equals(wo.getWorkOrderNo())) {
				index = i;
				break;
			}
		}
		
		setNext((index+1) < issuedFiltered.size());
		setPrevious(index > 0);
	}
	

	public WorkOrderService getWorkOrderService() {
		return workOrderService;
	}

	public void setWorkOrderService(WorkOrderService workOrderService) {
		this.workOrderService = workOrderService;
	}

	public List<String> getIgnoreStatusList() {
		return ignoreStatusList;
	}

	public void setIgnoreStatusList(List<String> ignoreStatusList) {
		this.ignoreStatusList = ignoreStatusList;
	}

	public List<WorkIssued> getIssued() {
		return issued;
	}

	public void setIssued(List<WorkIssued> issued) {
		this.issued = issued;
	}

	public WorkIssued getSelectedWork() {
		return selectedWork;
	}

	public void setSelectedWork(WorkIssued selectedWork) {
		
		_logger.debug(">>> setSelectedWork");
		
		this.selectedWork = selectedWork;
		if(selectedWork != null) {
			this.workDetail = new WorkDetail();
			this.workDetail.setWork(this.selectedWork);
			if(this.selectedWork.getUser() != null && !StringUtils.isEmpty(this.selectedWork.getUser().getId().getUserCode())) {
				this.workDetail.setUser(this.selectedWork.getUser());
			}
			if(this.selectedWork.getWorkGroup() != null && !StringUtils.isEmpty(this.selectedWork.getWorkGroup().getId().getWorkgroupCode())) {
				this.workDetail.setWorkGroup(this.selectedWork.getWorkGroup());
			}
			
			try {
				List<WorkStatusHistory> workStatusHistory = this.serviceManager.getWorkService().findLastWorkStatusHistoryByWorkOrder(null, this.selectedWork.getWorkOrderNo());
				this.workDetail.setWorkStatusHistory(workStatusHistory !=null? workStatusHistory : new ArrayList<>());
			}catch(Exception e) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
				_logger.error("setSelectedWork Unknown error occurred" + e.getMessage());
			}
		}

		_logger.debug("<<< setSelectedWork");
		
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

	public String getWoType() {
		return woType;
	}

	public void setWoType(String woType) {
		this.woType = woType;
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

	public WorkDetail getWorkDetail() {
		return workDetail;
	}

	public void setWorkDetail(WorkDetail workDetail) {
		this.workDetail = workDetail;
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

	public HPCWorkGroups getSelectedWorkgroup() {
		return selectedWorkgroup;
	}

	public void setSelectedWorkgroup(HPCWorkGroups selectedWorkgroup) {
		this.selectedWorkgroup = selectedWorkgroup;
	}

	public HPCUsers getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(HPCUsers selectedUser) {
		this.selectedUser = selectedUser;
	}

	public String getDirectWorkOrderNo() {
		return directWorkOrderNo;
	}

	public void setDirectWorkOrderNo(String directWorkOrderNo) {
		this.directWorkOrderNo = directWorkOrderNo;
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

	/*public TreeNode[] getTempWorkgroupNodes() {
		return tempWorkgroupNodes;
	}

	public void setTempWorkgroupNodes(TreeNode[] tempWorkgroupNodes) {
		this.tempWorkgroupNodes = tempWorkgroupNodes;
	}*/

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

	public TreeNode getStatusRoot() {
		return statusRoot;
	}

	public void setStatusRoot(TreeNode statusRoot) {
		this.statusRoot = statusRoot;
	}

	public TreeNode[] getSelectedStatusNodes() {
		return selectedStatusNodes;
	}

	public void setSelectedStatusNodes(TreeNode[] selectedStatusNodes) {
		this.selectedStatusNodes = selectedStatusNodes;
	}
	public EquipmentInformation getEquipmentInformation() {
		return equipmentInformation;
	}

	public void setEquipmentInformation(EquipmentInformation equipmentInformation) {
		this.equipmentInformation = equipmentInformation;
	}

	/**
	 * method for building the tree of workgroups
	 * 
	 * @throws UserNotFoundException
	 * @throws FRInstanceException 
	 */
	public void loadSelectedWorkgroups() {
		
		_logger.debug(">>> loadSelectedWorkgroups");
		
		try {
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
				if(containsWorkGroup(this.searchCriteria.getWorkgroups(), w.getWorkgroupCode())) {
					node.setSelected(true,true);
				}
			}
		//}
		//tempWorkgroupNodes = selectedWorkgroupNodes;
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedWorkgroups Unknown error occurred" + e.getMessage());
		}
		
		_logger.debug("<<< loadSelectedWorkgroups");

	}
	
	/*public void cancelSelectedWorkgroups(){
	selectedWorkgroupNodes = tempWorkgroupNodes;
	}*/
	public void clearSelectedWorkgroups(){
	
		_logger.debug(">>> clearSelectedWorkgroups");
		
		if(this.selectedWorkgroupNodes != null) {
			for (TreeNode tn : this.selectedWorkgroupNodes){
				tn.setSelected(false);
			}
		}

		this.selectedWorkgroupNodes = null;
		allWorkgroupsSelected = (searchCriteria.getWorkgroups()).isEmpty() ? true : false;
		
		_logger.debug("<<< clearSelectedWorkgroups");
	
	}
	
	public void loadSelectedWorkgroupsAssigned(){
		
		_logger.debug(">>> loadSelectedWorkgroupsAssigned");
		
		if(this.workDetail.getWork() != null) {
			//This is being done here due to a bug in primefaces, there is no way to trap the close event on the popup sidebar
			//we need to revert any changes that might have been made and cancelled in the edit/reissue dialogs
			this.cancelEditDetailMode();
		}
	
		this.selectedAssigneduserNode = null;
		this.selectedAssignedWorkgroupNode = null;
		
		selectedAssignedWorkGroupDisplay = null;
		selectedAssignedUserDisplay = null;
		
		TreeNode tempSelected = null;
		WorkIssued work = null;
		
		if(this.selectedWork != null) {
			work = this.workDetail.getWork();
		}
		
		try {
			//if (workgroupAssignedRoot == null){
			workgroupAssignedRoot = new DefaultTreeNode("Root", null);
			this.userAssignedRoot = new DefaultTreeNode("Root", null);

			// make a map for the workgroup categories
			Map<Integer, DefaultTreeNode> workgroupCats = new TreeMap<Integer, DefaultTreeNode>();

			List<HPCWorkGroups> workgroupList = fetchWorkgroups();

			for (HPCWorkGroups w : workgroupList){

				// check if we have created a parent node for the current workgroup's category
				DefaultTreeNode cat = workgroupCats.get(w.getWgCatId());
				

				// if not, create a new parent node for the current workgroup's category
				if (cat == null){
					cat = new DefaultTreeNode(w.getHpcWgCat().getWgCatDesc(), workgroupAssignedRoot);
					cat.setExpanded(true);
					workgroupCats.put(w.getWgCatId(), cat);
				}
				
				cat.setSelectable(false);

				DefaultTreeNode tn = new DefaultTreeNode(w, cat); 
				if(this.searchCriteria.getWorkgroups() != null && this.searchCriteria.getWorkgroups().contains(w)) {
					tn.setSelected(true);
				}
				
				if(work != null && work.getWorkgroupCode() != null) {
					
					if(this.selectedWorks != null && this.selectedWorks.size() > 1) {
						//leave everything blank
					}else if(work.getWorkgroupCode().equals(w.getWorkgroupCode())) {
						tempSelected = tn;
						tempSelected.setSelected(true);
						this.setSelectedAssignedWorkgroupNode(tempSelected);
						this.setSelectedAssignedWorkGroupDisplay(work.getWorkgroupCode());
						this.setSelectedAssignedUserDisplay(work.getUser() == null ? null : work.getUser().getId().getUserCode());
					}
				}
				
			}
		//}
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedWorkgroupsAssigned Unknown error occurred" + e.getMessage());
		}

		_logger.debug("<<< loadSelectedWorkgroupsAssigned");
		
	}



	/**
	 * method for building the tree of users
	 * 
	 * @throws UserNotFoundException
	 * @throws FRInstanceException 
	 */
	public void loadSelectedUsers(){
		_logger.debug(">>> loadSelectedUsers");
		try {
			//if (userRoot == null){
			userRoot = new CheckboxTreeNode("Root", null);

			// make a map for the workgroup categories
			Map<String, DefaultTreeNode> workgroupCats = new TreeMap<String, DefaultTreeNode>();

			List<HPCUsers> userList = fetchUsers();

			for (HPCUsers u : userList){

				// check if we have created a parent node for the current workgroup's category
				DefaultTreeNode cat = workgroupCats.get(u.getWorkgroupCode());

				// if not, create a new parent node for the current workgroup's category
				if (cat == null){
					HPCWorkGroups group = getUserService().findHPCWorkGroup(null, u.getWorkgroupCode());

					cat = new DefaultTreeNode(group.getWorkgroupDesc(), userRoot);
					cat.setExpanded(true);
					workgroupCats.put(u.getWorkgroupCode(), cat);
				}
				
				cat.setSelectable(false);

				DefaultTreeNode tn = new DefaultTreeNode(u, cat); 
				if(containsUsers(this.searchCriteria.getUsers() , u.getId())) {
					tn.setSelected(true);
				}
			}
		//}
		}catch(Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			_logger.error("loadSelectedUsers Unknown error occurred" + e.getMessage());
		}
		
		_logger.debug("<<< loadSelectedUsers");

	}


	public void clearSelectedUsers(){
		
		_logger.debug(">>> clearSelectedUsers");
		
		if(this.selectedUserNodes != null) {
			for (TreeNode tn : this.selectedUserNodes){
				tn.setSelected(false);
			}
		}

		this.selectedUserNodes = null;
		allUsersSelected=(searchCriteria.getUsers()).isEmpty() ? true : false;
		_logger.debug("<<< clearSelectedUsers");
	}

	/**
	 * parse the array of selected nodes into a list for the corresponding drop down menu
	 */
	public void updateSelectedWorkgroups(){
		
		_logger.debug(">>> updateSelectedWorkgroups");
		
		List<TreeNode> selectedWorkgroupNodeList = Arrays.asList(selectedWorkgroupNodes);

		searchCriteria.getWorkgroups().clear();

		for (TreeNode tn : selectedWorkgroupNodeList){
			// discard the parent nodes
			if (tn.getData() instanceof HPCWorkGroups)
				searchCriteria.getWorkgroups().add((HPCWorkGroups)tn.getData());
		}
		allWorkgroupsSelected = (searchCriteria.getWorkgroups()).isEmpty() ? true : false;
		
		_logger.debug("<<< updateSelectedWorkgroups");
	}

	/**
	 * parse the array of selected nodes into a list for the corresponding drop down menu
	 */
	public void updateSelectedUsers(){
		
		_logger.debug(">>> updateSelectedUsers");
		
		List<TreeNode> selectedUserNodeList = Arrays.asList(selectedUserNodes);

		searchCriteria.getUsers().clear();

		for (TreeNode tn : selectedUserNodeList){
			// discard the parent nodes
			if (tn.getData() instanceof HPCUsers)
				searchCriteria.getUsers().add((HPCUsers)tn.getData());
		}
		allUsersSelected=(searchCriteria.getUsers()).isEmpty() ? true : false;
				
		_logger.debug("<<< updateSelectedUsers");
	}
	

	public Map<HPCWorkGroups, List<HPCUsers>> getUserMap() throws UserNotFoundException, FRInstanceException {
		Map<HPCWorkGroups, List<HPCUsers>> userMap;
			userMap = getUserService().getFDMHPCWorkgroupToUserMap(null,getUsername(), false);
		return userMap;
	}

	
	/**
	 * get a list of users from the workgroup/user map
	 * @throws FRInstanceException 
	 */
	public List<HPCUsers> fetchUsers() throws UserNotFoundException, FRInstanceException {
		
		_logger.debug(">>> fetchUsers");
		
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
		
		_logger.debug("<<< fetchUsers");
		
		return fetchedUsers;

	}

	public List<String> getWorkStatusOptions() {
		return workStatusOptions;
	}

	public void setWorkStatusOptions(List<String> workStatusOptions) {
		this.workStatusOptions = workStatusOptions;
	}

	public String getDirectWorkGroup() {
		return directWorkGroup;
	}

	public void setDirectWorkGroup(String directWorkGroup) {
		this.directWorkGroup = directWorkGroup;
	}

	public String getDirectStatus() {
		return directStatus;
	}

	public void setDirectStatus(String directStatus) {
		this.directStatus = directStatus;
	}

	
	/*public void switchEditListMode() {
		this.editListMode = !this.editListMode;
		if(this.editListMode) {
			try {
				this.loadSelectedWorkgroupsAssigned();
			} catch (Exception e) {
				MessageHelper.setGlobalWarnMessage("Error loading Work groups, Can't enter edit mode");
			}
		}else {
			try {
				this.setSelectedWorks(null);
			} catch (FRInstanceException e) {
			}
		}
	}*/

	
	public boolean isUserWorkAssignmentEnabled() {

		_logger.debug("<<< isUserWorkAssignmentEnabled");
		return portalPropertyUtil.props().getEnableuserworkassignment();
	}
	
	
	public boolean isWorkorderEditable() {
		
		return this.isWorkorderEditable(this.workDetail.getWork());
	}
	
	public boolean isWorkorderEditable(WorkIssued workOrder) {
		
		_logger.debug(">>> isWorkorderEditable");
		
		if(workOrder == null) {
			_logger.debug("<<< isWorkorderEditable null false");
			return false;
		}
		
		try {
			String closedStatus = this.getServiceManager().getWorkOrderController().getWorkStatuses().getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.CLOSED);
			
			if(workOrder.getWorkStatus().equalsIgnoreCase(closedStatus)) {
				_logger.debug("<<< isWorkorderEditable close false");
				return false;
			}
			
			String status = this.getServiceManager().getWorkOrderController().getWorkStatuses().getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.CANCELLED);
			
			if(workOrder.getWorkStatus().equalsIgnoreCase(status)) {
				_logger.debug("<<< isWorkorderEditable cancel false");
				return false;
			}
		}catch(FRInstanceException e) {
			_logger.error("FRInstance Exception while retrieving CLOSE/CANCEL status ", e);
			_logger.debug("<<< isWorkorderEditable cancel false");
			return false;
		}

		_logger.debug("<<< isWorkorderEditable true");
		return true;
	}
	
	public boolean isWorkorderDispatchable() {
		
		_logger.debug(">>> isWorkorderDispatchable");
		
		WorkIssued workOrder = this.workDetail.getWork();
		
		if(workOrder == null) {
			_logger.debug("<<< isWorkorderDispatchable null false");
			return false;
		}
		
		try {
			List<String> preDispatchStatuses = this.getServiceManager().getWorkOrderController().getWorkStatuses().getPreDispatchStatusList(null);

			if(preDispatchStatuses.contains(workOrder.getWorkStatus())) {
				_logger.debug(">>> isWorkorderDispatchable true");
				return true;
			}
		}catch(FRInstanceException e) {
			_logger.error("FRInstance Exception while retrieving predispatch status list ", e);
			_logger.debug("<<< isWorkorderDispatchable false");
			return false;
		}
		
		
		_logger.debug(">>> isWorkorderDispatchable false");
		return false;
	}
	
	public boolean isWorkorderRecallable() {
		
		_logger.debug(">>> isWorkorderRecallable");
		
		WorkIssued workOrder = this.workDetail.getWork();
		
		if(workOrder == null) {
			_logger.debug("<<< isWorkorderRecallable null false");
			return false;
		}
		
		List<String> offFieldStatuses;

		try {
			offFieldStatuses = this.getServiceManager().getWorkOrderController().getWorkStatuses().getWorkIssuedIgnoreStatusList(null, false);
			
			if(offFieldStatuses.contains(workOrder.getWorkStatus())) {
				_logger.debug("<<< isWorkorderRecallable offField false");
				return false;
			}
		}catch(FRInstanceException e) {
			_logger.error("FRInstance Exception while retrieving recallable status list ", e);
			return false;
		}
		

		
		_logger.debug("<<< isWorkorderRecallable true");
		return true;
	}
	
	public boolean isWorkorderCloseApproveable() {
		
		_logger.debug(">>> isWorkorderCloseApproveable");
		WorkIssued workOrder = this.workDetail.getWork();
		
		if(workOrder == null) {
			_logger.debug("<<< isWorkorderCloseApproveable null false");
			return false;
		}

		try {
			String status = this.getServiceManager().getWorkOrderController().getWorkStatuses().getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.PRECLOSEAPPROVAL);
		
			if(workOrder.getWorkStatus().equals(status)) {
				_logger.debug("<<< isWorkorderCloseApproveable true");
				return true;
			}
			
		} catch (FRInstanceException e) {
			_logger.info("FRInstance Exception while retrieving PRECLOSEAPPROVAL status ", e);
		}

		
		_logger.debug("<<< isWorkorderCloseApproveable false");
		return false;
	}
	
	public boolean isWorkorderReissueable() {
		
		_logger.debug(">>> isWorkorderReissueable");
		
		WorkIssued workOrder = this.workDetail.getWork();
		
		if(workOrder == null) {
			_logger.debug("<<< isWorkorderReissueable null false");
			return false;
		}
		
		try {
			String status = this.getServiceManager().getWorkOrderController().getWorkStatuses().getSystemWorkStatusByDesignation(null, WORKSTATUSDESIGNATION.PRECLOSEAPPROVAL);
			
			if(workOrder.getWorkStatus().equals(status)) {
				_logger.debug("<<< isWorkorderReissueable true");
				return true;
			}
			
		} catch (FRInstanceException e) {
			_logger.info("FRInstance Exception while retrieving PRECLOSEAPPROVAL status ", e);
		}
		
		
		_logger.debug("<<< isWorkorderReissueable false");
		return false;
	}
	
	
	public void cancelEditDetailMode() {
		
		_logger.debug(">>> cancelEditDetailMode");
		
		//restore work order to previous state
		 //retrieve updated work order details from db
		WorkIssued work = this.workDetail.getWork();
		
		try {
			work  = this.serviceManager.getWorkService().findWorkIssued(null, work.getId().getWorkOrderNo(), work.getId().getDistrictCode());
			
			WorkIssued wo;
			for(int i=0; i<this.issued.size(); i++) {
				
				wo = issued.get(i);
				if(work.getWorkOrderNo().equals(wo.getWorkOrderNo())) {
					issued.set(i, work);
					selectedWork = work;
					break;
				}
			}
			workDetail.setWork(work);
			
			if(work.getWorkgroupCode() != null) {
				workDetail.setWorkGroup(this.serviceManager.getUserService().findHPCWorkGroup(null, work.getWorkgroupCode()));
				workDetail.setUser(this.serviceManager.getUserService().findHPCUser(null, work.getUserCode(), work.getWorkgroupCode()));
			}else {
				workDetail.setWorkGroup(null);
				workDetail.setUser(null);
			}
			
			
			
		} catch (FRInstanceException e) {
			_logger.error("cancelEditDetailMode " + e.getMessage());
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_work_edit_refresh_error"));
		}
		
		_logger.debug("<<< cancelEditDetailMode");

	}

	public List<WorkIssued> getSelectedWorks() {
		return selectedWorks;
	}

	public void setSelectedWorks(List<WorkIssued> selectedWorks) {
		this.selectedWorks = selectedWorks;
		if(this.selectedWorks != null && this.selectedWorks.size() > 0) {
			this.setSelectedWork(this.selectedWorks.get(0));
		}
	}

	public TreeNode getSelectedAssignedWorkgroupNode() {
		return selectedAssignedWorkgroupNode;
	}

	public void setSelectedAssignedWorkgroupNode(TreeNode selectedAssignedWorkgroupNode) throws FRInstanceException {
		
		_logger.debug(">>> setSelectedAssignedWorkgroupNode");
		
		this.selectedAssignedWorkgroupNode = selectedAssignedWorkgroupNode;

		if(selectedAssignedWorkgroupNode == null || !(selectedAssignedWorkgroupNode.getData() instanceof HPCWorkGroups) ) {
			if(this.selectedWork != null) {
				this.workDetail.getWork().setWorkgroupCode(null);
				this.workDetail.getWork().setWorkGroup(null);
			}
			this.selectedAssignedWorkgroupNode = null;
			this.selectedAssignedWorkGroupDisplay = null;
		} else {
			
			this.selectedAssignedWorkGroupDisplay = ((HPCWorkGroups)selectedAssignedWorkgroupNode.getData()).getWorkgroupCode();
			
			WorkIssued work = null;
			
			if(this.selectedWork != null) {
				this.workDetail.getWork().setWorkgroupCode(((HPCWorkGroups)selectedAssignedWorkgroupNode.getData()).getWorkgroupCode());
				this.workDetail.getWork().setWorkGroup((HPCWorkGroups)selectedAssignedWorkgroupNode.getData());
				this.workDetail.setWorkGroup((HPCWorkGroups)selectedAssignedWorkgroupNode.getData());
				
				work = this.workDetail.getWork();
			}
			
			
			String tempUserCode = null;
			if(work != null) {
				tempUserCode = work.getUser() == null ? null : work.getUser().getId().getUserCode();
			}
					
			
			//update assign user list
			List<HPCUsers>assignUsers = this.serviceManager.getUserService().findHPCUsersByWorkgroupCode(null, ((HPCWorkGroups)selectedAssignedWorkgroupNode.getData()).getWorkgroupCode());
			
			this.userAssignedRoot = new CheckboxTreeNode("Root", null);

			boolean userFound = false;
			
			if(assignUsers != null) {
				for (HPCUsers u : assignUsers){

					TreeNode tn = new CheckboxTreeNode(u, this.userAssignedRoot); 

					if(work != null && tempUserCode != null && u.getId().getUserCode().equals(tempUserCode)) {
						tn.setSelected(true);
						userFound = true;
						this.setSelectedAssigneduserNode(tn);
					}
				}
			}
			if(!userFound) {
				this.setSelectedAssigneduserNode(null);
				this.selectedAssignedUserDisplay = null;
			}
			
		}
		
		_logger.debug("<<< setSelectedAssignedWorkgroupNode");

	}

	public TreeNode getUserAssignedRoot() {
		return userAssignedRoot;
	}

	public void setUserAssignedRoot(TreeNode userAssignedRoot) {
		this.userAssignedRoot = userAssignedRoot;
	}

	public TreeNode getSelectedAssigneduserNode() {
		return selectedAssigneduserNode;
	}

	public void setSelectedAssigneduserNode(TreeNode selectedAssigneduserNode) {
		
		_logger.debug(">>> setSelectedAssigneduserNode");
		
		this.selectedAssigneduserNode = selectedAssigneduserNode;
		
		if(selectedAssigneduserNode != null) {
			
			this.selectedAssignedUserDisplay = ((HPCUsers)selectedAssigneduserNode.getData()).getId().getUserCode();
			
			if(this.selectedWork != null) {
				this.getWorkDetail().getWork().setUser(((HPCUsers)selectedAssigneduserNode.getData()));
				this.getWorkDetail().getWork().setUserCode(((HPCUsers)selectedAssigneduserNode.getData()).getId().getUserCode());
				this.getWorkDetail().setUser((HPCUsers)selectedAssigneduserNode.getData());
			}
			
		}else {
			this.selectedAssignedUserDisplay = null;
			
			if(this.selectedWork != null) {
				this.getWorkDetail().getWork().setUser(null);
				this.getWorkDetail().getWork().setUserCode(null);
				this.getWorkDetail().setUser(null);
			}
		}
		
		_logger.debug("<<< setSelectedAssigneduserNode");

	}

	public TreeNode getWorkgroupAssignedRoot() {
		return workgroupAssignedRoot;
	}

	public void setWorkgroupAssignedRoot(TreeNode workgroupAssignedRoot) {
		this.workgroupAssignedRoot = workgroupAssignedRoot;
	}

	public WorkViewMode getWorkViewMode() {
		return workViewMode;
	}

	public void setWorkViewMode(WorkViewMode workViewMode) {
		this.workViewMode = workViewMode;
	}

	public String getSelectedAssignedWorkGroupDisplay() {
		return selectedAssignedWorkGroupDisplay;
	}

	public void setSelectedAssignedWorkGroupDisplay(String selectedAssignedWorkGroupDisplay) {
		this.selectedAssignedWorkGroupDisplay = selectedAssignedWorkGroupDisplay;
	}

	public String getSelectedAssignedUserDisplay() {
		return selectedAssignedUserDisplay;
	}

	public void setSelectedAssignedUserDisplay(String selectedAssignedUserDisplay) {
		this.selectedAssignedUserDisplay = selectedAssignedUserDisplay;
	}

	/*public boolean isEditListMode() {
		return editListMode;
	}

	public void setEditListMode(boolean editListMode) {
		this.editListMode = editListMode;
	}*/


	public void setWorkGroupsFromStrings(List<String> woGroups) throws UserNotFoundException, FRInstanceException {
		
		_logger.debug(">>> setWorkGroupsFromStrings");
		
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

		this.searchCriteria.setWorkgroups(groups);
		
		_logger.debug("<<< setWorkGroupsFromStrings");
	}

	
	public List<WorkIssued> getIssuedFiltered() {
		return issuedFiltered;
	}

	public void setIssuedFiltered(List<WorkIssued> issuedFiltered) {
		this.issuedFiltered = issuedFiltered;
	}

	public List<SearchResult> getFieldClosedResults() {
		return fieldClosedResults;
	}

	public void setFieldClosedResults(List<SearchResult> fieldClosedResults) {
		this.fieldClosedResults = fieldClosedResults;
	}

	public SearchResult getSelectedFieldClosedResult() {
		return selectedFieldClosedResult;
	}

	public void setSelectedFieldClosedResult(SearchResult selectedFieldClosedResult) {
		this.selectedFieldClosedResult = selectedFieldClosedResult;
	}

	public int getWorkDetailActiveTab() {
		return workDetailActiveTab;
	}

	public void setWorkDetailActiveTab(int workDetailActiveTab) {
		this.workDetailActiveTab = workDetailActiveTab;
	}

	public String getRecallReason() {
		return recallReason;
	}

	public void setRecallReason(String recallReason) {
		this.recallReason = recallReason;
	}

	public String getTitle() {
		return workViewMode.getPrettyString();
	}

	public List<String> getOverrideWorkStatusOptions() {
		return overrideWorkStatusOptions;
	}

	public void setOverrideWorkStatusOptions(List<String> overrideWorkStatusOptions) {
		this.overrideWorkStatusOptions = overrideWorkStatusOptions;
	}

	public WorkCriteria getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(WorkCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public boolean isAllWorkgroupsSelected() {
		return allWorkgroupsSelected;
	}

	public void setAllWorkgroupsSelected(boolean allWorkgroupsSelected) {
		this.allWorkgroupsSelected = allWorkgroupsSelected;
	}

	public boolean isAllUsersSelected() {
		return allUsersSelected;
	}

	public void setAllUsersSelected(boolean allUsersSelected) {
		this.allUsersSelected = allUsersSelected;
	}

	public WorkCriteria getUndoSearchCriteria() {
		return undoSearchCriteria;
	}

	public void setUndoSearchCriteria(WorkCriteria undoSearchCriteria) {
		this.undoSearchCriteria = undoSearchCriteria;
	}

}
