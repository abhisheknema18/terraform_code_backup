package com.amtsybex.fieldreach.fdm.search;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.StreamedContent;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.NextStatusDef;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.ScriptStatusDef;
import com.amtsybex.fieldreach.backend.model.ScriptVersions;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.details.Details;
import com.amtsybex.fieldreach.fdm.details.DetailsCSVDownloadController;
import com.amtsybex.fieldreach.fdm.details.DetailsMultiView;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;


@Named
@WindowScoped
public class SearchResults extends PageCodebase implements Serializable {
	private static final long serialVersionUID = 102006412198749886L;

	private List<SearchResult> results;
	private List<SearchResult> resultsFiltered;

	private SearchResult selectedResult;
	private List<SearchResult> selectedResults;

	private String personalViewLabel;
	
	private boolean next;
	private boolean previous;
	private boolean workOrderNo;

	private boolean approvalMode;

	@Inject
	transient Details details;

	@Inject
	transient DetailsMultiView detailsMultiView;	
	
	@Inject
	DetailsCSVDownloadController detailsCSVDownloadController;

	// maximum number of rows to display per page of the results screen
	private int numRows = 25;

	// specify what page the data scroller is initialised to
	private int page = 0;

	//FDE049 - MC
	private boolean returnToWork;
	
	private boolean returnToMultiView;
	
	private Map<String, Object> nextStatuses;
	
	private String nextStatus;

	public boolean isReturnToWork() {
		return returnToWork;
	}

	public void setReturnToWork(boolean returnToWork) {
		this.returnToWork = returnToWork;
	}


	public void onPageChange(PageEvent event) {

		this.setPage(((DataTable) event.getSource()).getFirst());
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getNumRows() {
		return numRows;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	public boolean isWorkOrderNo() {
		return workOrderNo;
	}

	public void setWorkOrderNo(boolean workOrderNo) {
		this.workOrderNo = workOrderNo;
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


	public List<SearchResult> getResultsFiltered() {
		return resultsFiltered;
	}

	public void setResultsFiltered(List<SearchResult> resultsFiltered) {
		this.resultsFiltered = resultsFiltered;
	}

	public boolean isApprovalMode() {
		return approvalMode;
	}

	public void setApprovalMode(boolean approvalMode) {
		this.approvalMode = approvalMode;
	}

	public List<SearchResult> getSelectedResults() {
		return selectedResults;
	}

	public void setSelectedResults(List<SearchResult> selectedResults) {
		this.selectedResults = selectedResults;
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

	/**
	 * method called when the user clicks the next link on the details page
	 * @throws FRInstanceException 
	 */
	public void navigateNext() throws FRInstanceException{
		nextResult();

		//ManagedBeanHelper.findBean("details", Details.class).initialise(selectedResult.getReturnedScript());
		details.setResultNotesRowNo(0);
		details.initialise(selectedResult.getReturnedScript());
	}

	public void nextResult(){
		if ((resultsFiltered.size()-1) > resultsFiltered.indexOf(selectedResult)){
			selectedResult = resultsFiltered.get(resultsFiltered.indexOf(selectedResult)+1);
		}

		updateNavigation();
	}

	/**
	 * method called when the user clicks the previous link on the details page
	 * @throws FRInstanceException 
	 */
	public void navigatePrevious() throws FRInstanceException{
		previousResult();

		//ManagedBeanHelper.findBean("details", Details.class).initialise(selectedResult.getReturnedScript());
		details.setResultNotesRowNo(0);
		details.initialise(selectedResult.getReturnedScript());
	}

	public void previousResult(){
		if (resultsFiltered.indexOf(selectedResult) > 0){
			selectedResult = resultsFiltered.get(resultsFiltered.indexOf(selectedResult)-1);
		}

		updateNavigation();
	}

	/**
	 * method called when user clicks the "return" link in the details page
	 */
	public String navigateReturn(){

		if(this.returnToMultiView) {
			try {
				//TODO this is a bit inefficient, we need to reload the multiview because an item may have been edited
				//this will reload each script result and rebuild the question table. it needs to do this because with
				//repeating groups its not safe i think to just take the edited detail and replace it in the list as the
				//number of questions may differ. we would at least need to recall build responses on multiview.
				List<ReturnedScripts> scripts = new ArrayList<ReturnedScripts>();
				for(SearchResult result : selectedResults ) {
					scripts.add(result.getReturnedScript());
				}

				detailsMultiView.initialise(scripts);
			} catch (Exception e) {
				MessageHelper.setGlobalWarnMessage(Properties.get("fdm_error_unknown"));
			}
			
			return "detailsMultiView";
		}else if(returnToWork) {
			return "workdetail";
		}
		return "search";
	}

	public List<SearchResult> getResults() {
		if (results == null)
			results = new ArrayList<SearchResult>();
		return results;
	}

	public void setResults(List<SearchResult> results) {
		this.results = results;
	}

	public SearchResult getSelectedResult() {
		if (selectedResult == null)
			selectedResult = new SearchResult();

		return selectedResult;
	}

	public void setSelectedResult(SearchResult selectedResult) {
		this.selectedResult = selectedResult;
	}

	/**
	 * this method is called when a user clicks on a row in the results table, and it initialises the details page
	 * @throws FRInstanceException 
	 * @throws IOException 
	 */
	public void findScriptByRowDetails(SearchResult selectedSearchResult) throws FRInstanceException, IOException{

		this.selectedResult = selectedSearchResult;
		//ManagedBeanHelper.findBean("details", Details.class).initialise(selectedResult.getReturnedScript());
		details.setResultNotesRowNo(0);
		details.initialise(selectedSearchResult.getReturnedScript());
		// determine if next/previous navigation buttons on the details scren are enabled
		updateNavigation();
		
		returnToMultiView = false;

		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "details");

	}
	
	public void loadDetailsMultiView() throws FRInstanceException{
		//ManagedBeanHelper.findBean("details", Details.class).initialise(selectedResult.getReturnedScript());
		if(this.selectedResults == null || selectedResults.size() == 0) {
			return;
		}
		
		if(this.selectedResults != null && this.selectedResults.size() > 50) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_result_multi_view_max_results"));
			return;
		}
		
		List<ReturnedScripts> scripts = new ArrayList<ReturnedScripts>();
		for(SearchResult result : selectedResults ) {
			scripts.add(result.getReturnedScript());
		}
		detailsMultiView.initialise(scripts);
		// determine if next/previous navigation buttons on the details scren are enabled
		updateNavigation();

		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "detailsMultiView");
		
	}
	
	public StreamedContent downloadCSV() throws FRInstanceException{
		
		//ManagedBeanHelper.findBean("details", Details.class).initialise(selectedResult.getReturnedScript());
		if(this.selectedResults == null || selectedResults.size() == 0) {
			return null;
		}
		
		List<ReturnedScripts> scripts = new ArrayList<ReturnedScripts>();
		for(SearchResult result : selectedResults ) {
			scripts.add(result.getReturnedScript());
		}
		return detailsCSVDownloadController.downloadCSV(scripts);
	}
	
	public void loadSingleViewFromMutiView(Details selectedDetail) throws FRInstanceException, IOException{
		
		//ManagedBeanHelper.findBean("details", Details.class).initialise(selectedResult.getReturnedScript());
		details.setResultNotesRowNo(0);
        details.initialise(selectedDetail.getScript());
		// determine if next/previous navigation buttons on the details scren are enabled
		updateNavigation();
		
		returnToMultiView = true;

		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "details");

	}


	public void init(List<ReturnedScripts> origResults) throws FRInstanceException{
		//this.results = results;
		this.page = 0;

		// custom comparator to sort results by date and time 
		Collections.sort(origResults, new Comparator<ReturnedScripts>(){

			@Override
			public int compare(ReturnedScripts s1, ReturnedScripts s2) {
				// we first check which of the two scripts has an earlier
				int dateOrder = s1.getCompleteDateInt().compareTo(s2.getCompleteDateInt());

				// if two dates aren't the same, we return either -1 or 1, depending on what date comes first
				if (dateOrder != 0)
					return dateOrder;

				// if dates are the same, we need to check the time to see which one comes first
				return s1.getCompleteTimeInt().compareTo(s2.getCompleteTimeInt());
			}

		});

		List<SearchResult> res = new ArrayList<SearchResult>();

		// we need to loop over the result list to determine if any results contain a work order number
		for (ReturnedScripts r : origResults){
			if (!workOrderNo && r.getWorkOrderNo() != null && !r.getWorkOrderNo().isEmpty()){
				workOrderNo = true; // this flag is used to display the work order no/description columns on the results table
				//break;
			}

			SearchResult sw = new SearchResult();

			sw.setAltEquipRef(r.getAltEquipRef());
			sw.setWorkOrderDesc(r.getWorkOrderDesc());
			sw.setCompleteDate(r.getCompleteDate());
			sw.setCompleteTime(r.getCompleteTime());
			sw.setCompleteUser(r.getHpcUser() == null ? r.getCompleteUser() : r.getHpcUser().getUserName());
			sw.setEquipDesc(r.getEquipDesc());
			sw.setResultStatus(r.getResultStatus());
			sw.setReturnedScript(r);
			sw.setSummaryDesc(r.getScriptVersions().getScript().getScriptDesc());
			sw.setSummaryDesc(r.getScriptVersions() == null || r.getScriptVersions().getScript() == null ? "" : r.getScriptVersions().getScript().getScriptDesc());
			sw.setWorkOrderNo(r.getWorkOrderNo());
			sw.setId(r.getId());

			res.add(sw);

		}

		this.results = res;
		this.resultsFiltered = this.results;

		this.personalViewLabel = null;

		this.clearFilters();
	}


	public void init(ReturnedScripts origResults) throws FRInstanceException {
		List<SearchResult> res = new ArrayList<SearchResult>(); SearchResult sw = new
				SearchResult();

		sw.setAltEquipRef(origResults.getAltEquipRef());
		sw.setWorkOrderDesc(origResults.getWorkOrderDesc());
		sw.setCompleteDate(origResults.getCompleteDate());
		sw.setCompleteTime(origResults.getCompleteTime());
		sw.setCompleteUser(origResults.getHpcUser() == null ?
				origResults.getCompleteUser() : origResults.getHpcUser().getUserName());
		sw.setEquipDesc(origResults.getEquipDesc());
		sw.setResultStatus(origResults.getResultStatus());
		sw.setReturnedScript(origResults);
		sw.setSummaryDesc(origResults.getScriptVersions().getScript().getScriptDesc()
				); sw.setSummaryDesc(origResults.getScriptVersions() == null ||
				origResults.getScriptVersions().getScript() == null ? "" :
					origResults.getScriptVersions().getScript().getScriptDesc());
				sw.setWorkOrderNo(origResults.getWorkOrderNo());
				sw.setId(origResults.getId());

				res.add(sw); 
				this.results = res; 
				this.resultsFiltered = this.results;
				this.clearFilters(); 
	}

	public void resetSelectionAfterFilter() {

		if(this.selectedResults != null) {
			Iterator<SearchResult> resultIterator = this.selectedResults.iterator();
			while (resultIterator.hasNext()) {
				if(!this.resultsFiltered.contains(resultIterator.next())) {
					resultIterator.remove();
				}
			}
		}
	}

	public boolean isResultStatusEditable() throws FRInstanceException {

		if (this.selectedResults != null && this.selectedResults.size() > 0) {

			String[] closeStatuses = this.getWorkOrderController().getWorkStatuses().getResultCloseApprovalStatuses();

			if (closeStatuses != null) {
				List<String> cStatusList = new ArrayList<String>(Arrays.asList(closeStatuses));

				for (SearchResult res : this.selectedResults) {

					if (cStatusList.contains(res)) {
						return false;
					}
				}
			}

			SystemUsers sysUser = getUserService().getSystemUser(null, getUsername());

			String fdmCode = sysUser.getFdmGroupCode();

			// check if has update status

			if (fdmCode != null && fdmCode.length() > 0
					&& this.getUserService().hasResultStatusUpdateGroupFunction(null, fdmCode)) {

				for (SearchResult res : this.selectedResults) {
					loadNextStatuses(res.getReturnedScript());
					if (this.getNextStatuses() != null && this.getNextStatuses().size() > 1
							&& !this.isCurrentStatusSystemStatus(res)) {
						return true;
					}
				}
			}

		}

		return false;
	}

	protected boolean isCurrentStatusSystemStatus(SearchResult selectedResult) throws FRInstanceException {

		// if not error status then check if status is system status
		// get available statuses to show in dropdown
		ScriptVersions version = this.getScriptService().findScriptVersion(null,
				selectedResult.getReturnedScript().getScriptId());

		List<ScriptStatusDef> scriptStatusDef = this.getScriptResultsService().getScriptStatusDefByScriptCodeID(null,
				version.getScriptCodeId());

		if (scriptStatusDef == null || scriptStatusDef.isEmpty())
			return false;

		// Check to see if status is a system status
		for (ScriptStatusDef ssd : scriptStatusDef) {

			if (ssd.getStatusValue().equals(selectedResult.getResultStatus())) {

				if (Integer.toString(ssd.getSysStatusFlag()).equals("1")) {
					return true;
				}

				break;
			}
		}

		return false;
	}

	public void loadNextStatuses(ReturnedScripts returnedScript) throws FRInstanceException {
		// get available statuses to show in dropdown

		ScriptVersions version = this.getScriptService().findScriptVersion(null, returnedScript.getScriptId());

		List<NextStatusDef> nextDef = this.getScriptResultsService().getNextStatusDefByScriptCodeID(null,
				version.getScriptCodeId());

		if (nextDef == null || nextDef.isEmpty())
			return;

		nextStatuses = new LinkedHashMap<String, Object>();

		nextStatuses.put(returnedScript.getResultStatus(), returnedScript.getResultStatus());

		nextStatus = returnedScript.getResultStatus();

		for (NextStatusDef def : nextDef) {

			if (def.getId().getStatusValue().equalsIgnoreCase(returnedScript.getResultStatus())) {

				nextStatuses.put(def.getNextStatusValue(), def.getNextStatusValue());
			}
		}

	}

	public boolean isUniqueScriptIdSelected(boolean includeStatus){

		if(this.selectedResults != null && this.selectedResults.size() > 0) {
			
			if(this.selectedResults.size() > 1) {
				
				Integer scriptid = null;
				String scriptStatus = null;
				
				for(SearchResult res : this.selectedResults) {
					
					if(scriptid == null) {
						scriptid = res.getReturnedScript().getScriptId();
						scriptStatus = res.getReturnedScript().getResultStatus();
					}else if(scriptid.intValue() != res.getReturnedScript().getScriptId().intValue() || (includeStatus && !scriptStatus.equals(res.getReturnedScript().getResultStatus())) ) {
						return false;
					}
				}
			}

			return true;
			
		}

		return false;
	}

	/** 
	 * simple method that enables/disables the next/previous navigation buttons based on the currently selected result
	 */
	public void updateNavigation(){
		setNext((resultsFiltered.indexOf(selectedResult)+1) < resultsFiltered.size());
		setPrevious(resultsFiltered.indexOf(selectedResult) > 0);
	}

	public void reset(){
		next = false;
		previous = false;
		workOrderNo = false;
		results = null;
		resultsFiltered = null;
		selectedResult = null;
		selectedResults = null;
		returnToWork = false;
		returnToMultiView = false;
		approvalMode = false;
		this.clearFilters();
	}

	public String getTitle() {
		return Properties.get("fdm_breadcrumb_details_menu_heading");
	}

	public void clearFilters() {
		PrimeFaces.current().executeScript("PF('resultsTable').clearFilters()");
		PrimeFaces.current().executeScript("PF('resultsTable').unselectAllRows()");
		PrimeFaces.current().executeScript("PF('resultsTable').filter()");
	}

	public void filter() {
		PrimeFaces.current().executeScript("PF('resultsTable').filter()");
	}

	public String getPersonalViewLabel() {
		return personalViewLabel;
	}

	public void setPersonalViewLabel(String personalViewLabel) {
		this.personalViewLabel = personalViewLabel;
	}

	public boolean isReturnToMultiView() {
		return returnToMultiView;
	}

	public void setReturnToMultiView(boolean returnToMultiView) {
		this.returnToMultiView = returnToMultiView;
	}

}