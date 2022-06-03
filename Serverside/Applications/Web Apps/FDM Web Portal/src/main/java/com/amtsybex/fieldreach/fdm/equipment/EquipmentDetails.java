package com.amtsybex.fieldreach.fdm.equipment;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.MaxResultsExceededException;
import com.amtsybex.fieldreach.backend.model.EquipmentAttrib;
import com.amtsybex.fieldreach.backend.model.EquipmentData;
import com.amtsybex.fieldreach.backend.model.ReturnedScripts;
import com.amtsybex.fieldreach.backend.model.WorkIssued;
import com.amtsybex.fieldreach.backend.service.WorkOrderService;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.details.DetailsMultiView;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;

@Named
@WindowScoped
public class EquipmentDetails extends PageCodebase implements Serializable {

	private static final long serialVersionUID = -6500334344876373598L;

	@Inject
	transient EquipmentInformation equipmentInformation;
	
	@Inject
	transient WorkOrderService workOrderService;
	
	@Inject
	transient DetailsMultiView detailsMultiView;	
	
	private String selectedEquipNo;
	private String location;
	private EquipmentData equipmentData;
	private List<EquipmentAttrib> equipmentAttribList;
	private List<ReturnedScripts> scriptResultsList;
	private List<WorkIssued> workIssuedHistoryList;
	private WorkIssued selectedWorks;
	private List<ReturnedScripts> selectedResults;
	private List<ReturnedScripts> resultsFiltered;
	
	private boolean returnHierarchy;
	
	private String accordionIndex;
	
	private MapModel mapModel;
	
	public void initialise(EquipmentData equipmentData) throws FRInstanceException, IOException, MaxResultsExceededException{
		
		List<ReturnedScripts> returnedScriptResults = null;
		selectedResults = null;
		accordionIndex = null;
		
		this.setEquipmentData(equipmentData);
		
		addMarker();
		
		this.equipmentInformation.populateEquipmentAssetInformation(this.getEquipmentData().getEquipNo());
		
		this.setEquipmentAttribList(this.equipmentInformation.filteredEquipAttribList());
		
		returnedScriptResults = this.getScriptResultsService().getScriptResults(null, this.getEquipmentData().getEquipNo(), null, null, null, null, null, null, null, null);
		resultsFiltered = returnedScriptResults;
		
		this.setScriptResultsList(returnedScriptResults != null ? returnedScriptResults : new ArrayList<ReturnedScripts>());
		
		this.setWorkIssuedHistoryList(this.getWorkOrderService().findWorkIssuedByequipNo(null, this.getEquipmentData().getEquipNo()));
		
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "equipmentDetails");

	}	
	
	public void loadDetailsMultiView() throws FRInstanceException{

		if(this.selectedResults == null || selectedResults.size() == 0) {
			return;
		}
		
		if(this.selectedResults != null && this.selectedResults.size() > 100) {
			MessageHelper.setGlobalWarnMessage(Properties.get("fdm_result_multi_view_max_results"));
			return;
		}
		
		detailsMultiView.initialise(this.selectedResults);
		
		detailsMultiView.setPageFrom("equipmentDetails");

		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "detailsMultiView");
		
	}
	
	public void resetSelectionAfterFilter() {

		if(this.selectedResults != null) {
			Iterator<ReturnedScripts> resultIterator = this.selectedResults.iterator();
			while (resultIterator.hasNext()) {
				if(!this.resultsFiltered.contains(resultIterator.next())) {
					resultIterator.remove();
				}
			}
		}
	}

	public boolean isUniqueScriptIdSelected(){

		if(this.selectedResults != null && this.selectedResults.size() > 0) {
			
			if(this.selectedResults.size() > 1) {
				
				Integer scriptid = null;

				for(ReturnedScripts res : this.selectedResults) {
					
					if(scriptid == null) {
						scriptid = res.getScriptId();
					}else if(scriptid.intValue() != res.getScriptId().intValue() ) {
						return false;
					}
				}
			}

			return true;
			
		}

		return false;
	}
	
	private void addMarker() {
		if(this.getLocation()!=null && !this.getLocation().isEmpty()) {
			mapModel = new DefaultMapModel();
			
			LatLng coOrds = new LatLng(Double.valueOf(this.equipmentData.getLatitude()),Double.valueOf(this.equipmentData.getLongitude()));
			
			mapModel.addOverlay(new Marker(coOrds));
		}
	}
	
	/**
	 * method called when user clicks the "return" link in the details page
	 */
	public String navigateReturn(){
		if(this.returnHierarchy)
			return "equipmentHierarchy";
		return "equipmentSearchList";
	}
	
	public void loadAssetInformation(String equipNo) throws FRInstanceException {
		equipmentInformation.populateEquipmentAssetInformation(equipNo);
	}

	public String getTitle() {
		return  Properties.get("fdm_asset_details_title");
	}

	public EquipmentInformation getEquipmentInformation() {
		return equipmentInformation;
	}

	public void setEquipmentInformation(EquipmentInformation equipmentInformation) {
		this.equipmentInformation = equipmentInformation;
	}

	public EquipmentData getEquipmentData() {
		return equipmentData;
	}

	public void setEquipmentData(EquipmentData equipmentData) {
		this.equipmentData = equipmentData;
	}

	public List<EquipmentAttrib> getEquipmentAttribList() {
		return equipmentAttribList;
	}

	public void setEquipmentAttribList(List<EquipmentAttrib> equipmentAttribList) {
		this.equipmentAttribList = equipmentAttribList;
	}

	public String getSelectedEquipNo() {
		return selectedEquipNo;
	}

	public void setSelectedEquipNo(String selectedEquipNo) {
		this.selectedEquipNo = selectedEquipNo;
	}

	public List<ReturnedScripts> getScriptResultsList() {
		return scriptResultsList;
	}

	public void setScriptResultsList(List<ReturnedScripts> scriptResultsList) {
		this.scriptResultsList = scriptResultsList;
	}

	public List<WorkIssued> getWorkIssuedHistoryList() {
		return workIssuedHistoryList;
	}

	public void setWorkIssuedHistoryList(List<WorkIssued> workIssuedHistoryList) {
		this.workIssuedHistoryList = workIssuedHistoryList;
	}

	public String getLocation() {
		if(this.getEquipmentData().getLatitude()!=null && this.getEquipmentData().getLongitude()!=null) {
			
			return this.getEquipmentData().getLatitude()+","+this.getEquipmentData().getLongitude();
		}
		else {
			return null;
		}
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public WorkIssued getSelectedWorks() {
		return selectedWorks;
	}

	public void setSelectedWorks(WorkIssued selectedWorks) {
		this.selectedWorks = selectedWorks;
	}


	public MapModel getMapModel() {
		return mapModel;
	}

	public void setMapModel(MapModel mapModel) {
		this.mapModel = mapModel;
	}

	public WorkOrderService getWorkOrderService() {
		return workOrderService;
	}

	public void setWorkOrderService(WorkOrderService workOrderService) {
		this.workOrderService = workOrderService;
	}

	public boolean isReturnHierarchy() {
		return returnHierarchy;
	}

	public void setReturnHierarchy(boolean returnHierarchy) {
		this.returnHierarchy = returnHierarchy;
	}

	public List<ReturnedScripts> getSelectedResults() {
		return selectedResults;
	}

	public void setSelectedResults(List<ReturnedScripts> selectedResults) {
		this.selectedResults = selectedResults;
	}

	public List<ReturnedScripts> getResultsFiltered() {
		return resultsFiltered;
	}

	public void setResultsFiltered(List<ReturnedScripts> resultsFiltered) {
		this.resultsFiltered = resultsFiltered;
	}

	public String getAccordionIndex() {
		return accordionIndex;
	}

	public void setAccordionIndex(String accordionIndex) {
		this.accordionIndex = accordionIndex;
	}


}

