package com.amtsybex.fieldreach.fdm.details;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.NextStatusDef;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.ScriptStatusDef;
import com.amtsybex.fieldreach.backend.model.ScriptVersions;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.service.EquipmentService;
import com.amtsybex.fieldreach.fdm.dashboard.DashboardServiceManager;
import com.amtsybex.fieldreach.fdm.equipment.EquipmentInformation;
import com.amtsybex.fieldreach.fdm.search.Search;
import com.amtsybex.fieldreach.fdm.search.SearchResult;
import com.amtsybex.fieldreach.fdm.search.SearchResults;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;

@Named
@WindowScoped
public class DetailsMultiView extends MultiViewBase implements Serializable {

	private static final long serialVersionUID = 3229785571926793176L;
	
	private List<DetailsBase> selectedDetails;
	
	private ResultSet selectedAnswer;
	
	private Details selectedEquipmentDetails;
	
	@Inject
	protected transient EquipmentInformation equipmentInformation;
	
	@Inject
	transient SearchResults searchResults;
	
	@Inject
	transient Search search;
	
	@Inject
	transient DashboardServiceManager serviceManager;
	
	@Inject
	transient EquipmentService equipmentService;
	
	private boolean selectAll;

	//FDP1406
	private Map<String, Object> nextStatuses;
	//FDP1406
	private String nextStatus;
	
	private String pageFrom;
	

	/**
	 * init method for building up the details screen based on the selected result
	 * 
	 * @param script	- script the user has selected on the search results screen
	 * @throws FRInstanceException 
	 */
	public void initialise(List<ReturnedScripts> scripts) throws FRInstanceException{
		
		super.initialise(scripts, false);
		
		this.selectedDetails = null;
		this.selectAll = false;
		this.pageFrom = null;

		
		//40317 - toggle answered/unanswered
		DetailsBase firstRes = details.get(0);
		
		int i=0;
		for(ResultSet response : firstRes.getAnswers()) {
			
			if(response.isHeading()) {
			
				for(DetailsBase dt : details) {
					
					response.setHasAnsweredQuestions(false);
					if(this.shouldShowHeading(i, response.getiLevel(), dt.getAnswers())) {
						response.setHasAnsweredQuestions(true);
						break;
					}
				}

			}
			i++;
		}
		
	}
	
	public boolean isUniqueResultStatusSelected(){

		if(this.selectedDetails != null && this.selectedDetails.size() > 0) {
			
			if(this.selectedDetails.size() > 1) {

				String scriptStatus = null;
				
				for(DetailsBase res : this.selectedDetails) {
					
					if(scriptStatus == null) {
						scriptStatus = res.getScript().getResultStatus();
					}else if(!scriptStatus.equals(res.getScript().getResultStatus()) ) {
						return false;
					}
				}
			}

			return true;
			
		}

		return false;
	}
	
	//40317 - toggle answered/unanswered
	private boolean shouldShowHeading(int index, int level,  List<ResultSet> responses) {
		if(index >= 0) {
			for(int i=index+1; i<responses.size(); i++) {
				ResultSet answer = responses.get(i);
				if(answer != null) {
					if(answer.isAnswered()) {
						return true;
					}else if(answer.isHeading() && answer.getiLevel() <= level) {
						break;
					} else if(answer.getiLevel() < level) {
						break;
					}
				}
			}
		}
		return false;
	}
	
	public String navigateReturn(){
		if(this.pageFrom != null && pageFrom.length() > 0) {
			return pageFrom;
		}
		return "search";
	}
	
	public void loadNextStatuses() throws FRInstanceException {
		this.loadNextStatuses(this.details.get(0).getScript());

	}
	
	public void loadNextStatuses(ReturnedScripts returnedScript) throws FRInstanceException {
		//get available statuses to show in dropdown
		
		ScriptVersions version = this.getScriptService().findScriptVersion(null, returnedScript.getScriptId());

		List<NextStatusDef> nextDef = this.getScriptResultsService().getNextStatusDefByScriptCodeID(null, version.getScriptCodeId());
		
		if (nextDef == null || nextDef.isEmpty())
			return;
		
		nextStatuses = new LinkedHashMap<String, Object>();

		nextStatuses.put(returnedScript.getResultStatus(), returnedScript.getResultStatus());
		
		nextStatus = returnedScript.getResultStatus();
		
		for (NextStatusDef def : nextDef) {

			if (def.getId().getStatusValue().equalsIgnoreCase(returnedScript.getResultStatus())) {
				
				nextStatuses.put(def.getNextStatusValue(),def.getNextStatusValue());
			}
		}
		
	}
	
	//FDP1406
	public String updateNextStatus() {
		
		if (nextStatus == null || nextStatus.equals("")) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_details_failed_to_update_status"));
			return null;
		}
		
		try {
			
			int updated = 0;
			int failed = 0;
			
			List<DetailsBase> detailsToRemove = new ArrayList<DetailsBase>();
			for(DetailsBase detail : this.selectedDetails) {
				
				try {
					this.getScriptResultsService().updateResultStatus(null, detail.returnId, nextStatus, this.getUsername());
					
					this.details.remove(detail);
					detailsToRemove.add(detail);
					
					if(pageFrom != null && pageFrom.equals("equipmentDetails")) {
						ReturnedScripts ret = this.getScriptResultsService().getReturnedScript(null, detail.returnId);
						detail.getScript().setResultStatus(ret.getResultStatus());
					}else {
						
						SearchResult resToRemove = null;
						for(SearchResult res : searchResults.getSelectedResults()) {
							if(detail.getReturnId() == res.getId()) {
								resToRemove = res;
								break;
							}
						}
						if(resToRemove != null) {
							
							ReturnedScripts ret = this.getScriptResultsService().getReturnedScript(null, detail.returnId);
							
							if(searchResults.isApprovalMode() && !search.getResultApprovalStatuses().contains(ret.getResultStatus())) {
								searchResults.getSelectedResults().remove(resToRemove);
								searchResults.getResults().remove(resToRemove);
							}else {
								
								if(ret != null) {
									resToRemove.setReturnedScript(ret);
									resToRemove.setResultStatus(ret.getResultStatus());
								}
							}
						}
						
					}

					updated++;
				}catch (Exception e) {
					failed++;
				}
			}
			
			this.selectedDetails.removeAll(detailsToRemove);
			
			
			if((this.pageFrom != null && pageFrom.length() > 0) || !this.searchResults.isApprovalMode()) {
				if(failed > 0) {
					if(updated == 0) {
						MessageHelper.setGlobalErrorMessage("Updated " + updated + " result sets status, failed to update " + failed + " result sets.");
					}else {
						MessageHelper.setGlobalWarnMessage("Updated " + updated + " result sets status, failed to approve " + failed + " result sets.");
					}
				}else {
					MessageHelper.setGlobalInfoMessage("Updated " + updated + " result sets status.");
				}
			}else {
				if(failed > 0) {
					if(updated == 0) {
						MessageHelper.setGlobalErrorMessage("Approved " + updated + " result sets, failed to approve " + failed + " result sets.");
					}else {
						MessageHelper.setGlobalWarnMessage("Approved " + updated + " result sets, failed to approve " + failed + " result sets.");
					}
				}else {
					MessageHelper.setGlobalInfoMessage("Approved " + updated + " result sets.");
				}
			}
			
			if(this.details.size() == 0) {
				//everything approved so return to list and pop a message
				return navigateReturn();
			}
			
		} catch (Exception e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_details_failed_to_update_status"));
		}
		
		return null;
	}
	
	public void updateNextStatusFromSearch() {
		
		if (nextStatus == null || nextStatus.equals("")) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_details_failed_to_update_status"));
			return;
		}
			
		int updated = 0;
		int failed = 0;
		
		List<SearchResult> resultsToRemove = new ArrayList<SearchResult>();
		for(SearchResult res : searchResults.getSelectedResults()) {
			
			try {
				
				this.getScriptResultsService().updateResultStatus(null, res.getId(), nextStatus, this.getUsername());
				
				ReturnedScripts ret = this.getScriptResultsService().getReturnedScript(null, res.getId());
				
				if(searchResults.isApprovalMode() && !search.getResultApprovalStatuses().contains(ret.getResultStatus())) {
					resultsToRemove.add(res);
				}else {
					
					if(ret != null) {
						res.setResultStatus(ret.getResultStatus());
						res.setReturnedScript(ret);
					}
				}

				updated++;
				
			}catch (Exception e) {
				failed++;
			}
			
		}
		
		this.searchResults.getSelectedResults().removeAll(resultsToRemove);
		this.searchResults.getResults().removeAll(resultsToRemove);
		
		
		if(this.searchResults.isApprovalMode()) {
			if(failed > 0) {
				if(updated == 0) {
					MessageHelper.setGlobalErrorMessage("Approved " + updated + " result sets, failed to approve " + failed + " result sets.");
				}else {
					MessageHelper.setGlobalWarnMessage("Approved " + updated + " result sets, failed to approve " + failed + " result sets.");
				}
			}else {
				MessageHelper.setGlobalInfoMessage("Approved " + updated + " result sets.");
			}
		}else {
			if(failed > 0) {
				if(updated == 0) {
					MessageHelper.setGlobalErrorMessage("Updated " + updated + " result sets status, failed to update " + failed + " result sets.");
				}else {
					MessageHelper.setGlobalWarnMessage("Updated " + updated + " result sets status, failed to approve " + failed + " result sets.");
				}
			}else {
				MessageHelper.setGlobalInfoMessage("Updated " + updated + " result sets status.");
			}
		}
		


	}
	
	private boolean isCurrentStatusSystemStatus() throws FRInstanceException {
		
		DetailsBase firstDetail = this.details.get(0);
		
		//if not error status then check if status is system status
		//get available statuses to show in dropdown
		ScriptVersions version = this.getScriptService().findScriptVersion(null, firstDetail.getScript().getScriptId());
	
		List<ScriptStatusDef> scriptStatusDef =  this.getScriptResultsService().getScriptStatusDefByScriptCodeID(null, version.getScriptCodeId());
		
		if (scriptStatusDef == null || scriptStatusDef.isEmpty())
			return false;
		
		// Check to see if status is a system status
		for (ScriptStatusDef ssd : scriptStatusDef) {

			if (ssd.getStatusValue().equals(firstDetail.getScript().getResultStatus())) {

				if (Integer.toString(ssd.getSysStatusFlag()).equals("1")) {
					return true;
				}

				break;
			}
		}
		
		return false;
	}

	public boolean isResultStatusEditable() throws FRInstanceException {
		
		DetailsBase firstDetail = this.details.get(0);
		
		String[] closeStatuses = this.serviceManager.getWorkOrderController().getWorkStatuses().getResultCloseApprovalStatuses();
		
		if(closeStatuses != null) {
			List<String> cStatusList = new ArrayList<String>(Arrays.asList(closeStatuses));
			
			if(cStatusList.contains(firstDetail.getScript().getResultStatus())) {
				return false;
			}
		}
		
		SystemUsers sysUser = getUserService().getSystemUser(null, getUsername());
		
		String fdmCode = sysUser.getFdmGroupCode();
		
		//check if has update status 
		
		if(fdmCode != null && fdmCode.length() > 0 && this.getUserService().hasResultStatusUpdateGroupFunction(null, fdmCode)) {
			
			this.loadNextStatuses();
			
			if(this.getNextStatuses() != null && this.getNextStatuses().size() > 1 && !this.isCurrentStatusSystemStatus()) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public void toggleSelection(DetailsBase selectedDetail){
		
		if(this.selectedDetails == null) {
			this.selectedDetails = new ArrayList<DetailsBase>();
		}
		if(this.selectedDetails.contains(selectedDetail)) {
			this.selectedDetails.remove(selectedDetail);
			this.selectAll = false;
		}else {
			this.selectedDetails.add(selectedDetail);
		}
	}
	
	public boolean isAllQuestionsUnAnswered(ResultSet answer) {
		
		int index = this.details.get(0).getAnswers().indexOf(answer);
		
		boolean unanswered = true;
		
		for(DetailsBase detail : details) {
			if(detail.getAnswers().get(index).isAnswered()) {
				unanswered = false;
				break;
			}
		}
		
		return unanswered;
	}
	
	public boolean isSelectAll() {
		return selectAll;
	}
	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}
	
	public void toggleSelectAll() {
		if(this.selectAll) {
			this.selectedDetails = new ArrayList<DetailsBase>();
			for(DetailsBase detail: this.details) {
				this.selectedDetails.add(detail);
				detail.setSelectedOnApprovalDetail(true);
			}
		}else {
			for(DetailsBase detail: this.details) {
				detail.setSelectedOnApprovalDetail(false);
			}
			this.selectedDetails = new ArrayList<DetailsBase>();
		}
	}
	
	public List<DetailsBase> getSelectedDetails() {
		return selectedDetails;
	}
	public void setSelectedDetails(List<DetailsBase> selectedDetails) {
		this.selectedDetails = selectedDetails;
	}
	
	public boolean isSelectedDetail(Details detail){
		if(this.getSelectedDetails().contains(detail)) {
			return true;
		}
		return false;
	}

	public List<DetailsBase> getDetails() {
		return details;
	}
	public void setDetails(List<DetailsBase> details) {
		this.details = details;
	}

	public ResultSet getSelectedAnswer() {
		return selectedAnswer;
	}
	public void setSelectedAnswer(ResultSet selectedAnswer) {
		this.selectedAnswer = selectedAnswer;
	}
	public Details getSelectedEquipmentDetails() {
		return selectedEquipmentDetails;
	}
	public void setSelectedEquipmentDetails(Details selectedEquipmentDetails) {
		try {
			equipmentInformation.populateEquipmentAssetInformation(selectedEquipmentDetails.script.getEquipNo());
			selectedEquipmentDetails.setEquipmentInformation(equipmentInformation);
		} catch (FRInstanceException e) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
		}
		this.selectedEquipmentDetails = selectedEquipmentDetails;
	}
	public SearchResults getSearchResults() {
		return searchResults;
	}
	public void setSearchResults(SearchResults searchResults) {
		this.searchResults = searchResults;
	}
	public DashboardServiceManager getServiceManager() {
		return serviceManager;
	}
	public void setServiceManager(DashboardServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	public Map<String, Object> getNextStatuses() {
		return nextStatuses;
	}
	public void setNextStatuses(Map<String, Object> nextStatuses) {
		this.nextStatuses = nextStatuses;
	}
	public String getNextStatus() {
		return nextStatus;
	}
	public void setNextStatus(String nextStatus) {
		this.nextStatus = nextStatus;
	}
	
	public String getTitle() {
		return Properties.get("fdm_breadcrumb_details_approval_menu_heading");
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

}
