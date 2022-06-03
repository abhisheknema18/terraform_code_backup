package com.amtsybex.fieldreach.fdm.equipment;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.MaxResultsExceededException;
import com.amtsybex.fieldreach.backend.model.EquipmentData;
import com.amtsybex.fieldreach.backend.service.EquipmentService;
import com.amtsybex.fieldreach.fdm.PageCodebase;

@Named
@WindowScoped
public class EquipmentSearchList extends PageCodebase implements Serializable {

	private static final long serialVersionUID = 4228092392489133447L;

	private List<EquipmentData> equipmentSearchList;
	
	private List<EquipmentData> filteredEquipmentSearchList;
	
	private EquipmentData selectedEquipment;
	
	private boolean next;
	private boolean previous;

	@Inject
	private transient EquipmentService equipmentService;
	@Inject
	transient EquipmentInformation equipmentInformation;
	@Inject
	transient EquipmentDetails equipmentDetails;
	
	public void initiateSearchList(List<EquipmentData> equipList) {
		this.setEquipmentSearchList(equipList);
		this.filteredEquipmentSearchList = equipList;
	}

	public void findEquipmentByRowDetails(EquipmentData selectedEquipmentData) throws FRInstanceException, IOException, MaxResultsExceededException {
		this.selectedEquipment = selectedEquipmentData;
		equipmentDetails.setReturnHierarchy(false);
		equipmentDetails.initialise(selectedEquipmentData);
		updateNavigation();
	}
	
	public void navigateNext() throws FRInstanceException, IOException, MaxResultsExceededException{
		nextResult();
		findEquipmentByRowDetails(selectedEquipment);
	}
	
	public void nextResult(){
		if ((filteredEquipmentSearchList.size()-1) > filteredEquipmentSearchList.indexOf(selectedEquipment)){
			selectedEquipment = filteredEquipmentSearchList.get(filteredEquipmentSearchList.indexOf(selectedEquipment)+1);
		}
		
		updateNavigation();
	}
	
	public void navigatePrevious() throws FRInstanceException, IOException, MaxResultsExceededException{
		previousResult();
		findEquipmentByRowDetails(selectedEquipment);
	}
	
	public void previousResult(){
		if (filteredEquipmentSearchList.indexOf(selectedEquipment) > 0){
			selectedEquipment = filteredEquipmentSearchList.get(filteredEquipmentSearchList.indexOf(selectedEquipment)-1);
		}
		
		updateNavigation();
	}
	public void updateNavigation(){
		setNext((filteredEquipmentSearchList.indexOf(selectedEquipment)+1) < equipmentSearchList.size());
		setPrevious(filteredEquipmentSearchList.indexOf(selectedEquipment) > 0);
	}
	
	public void loadAssetInformation(String equipNo) throws FRInstanceException {
		equipmentInformation.populateEquipmentAssetInformation(equipNo);
	}

	public String getTitle() {
		return "Asset Hierarchy Search Results";
	}

	public EquipmentService getEquipmentService() {
		return equipmentService;
	}

	public void setEquipmentService(EquipmentService equipmentService) {
		this.equipmentService = equipmentService;
	}

	public EquipmentInformation getEquipmentInformation() {
		return equipmentInformation;
	}

	public void setEquipmentInformation(EquipmentInformation equipmentInformation) {
		this.equipmentInformation = equipmentInformation;
	}

	public List<EquipmentData> getEquipmentSearchList() {
		return equipmentSearchList;
	}

	public void setEquipmentSearchList(List<EquipmentData> equipmentSearchList) {
		this.equipmentSearchList = equipmentSearchList;
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

	public EquipmentData getSelectedEquipment() {
		return selectedEquipment;
	}

	public void setSelectedEquipment(EquipmentData selectedEquipment) {
		this.selectedEquipment = selectedEquipment;
	}

	public List<EquipmentData> getFilteredEquipmentSearchList() {
		return filteredEquipmentSearchList;
	}

	public void setFilteredEquipmentSearchList(List<EquipmentData> filteredEquipmentSearchList) {
		this.filteredEquipmentSearchList = filteredEquipmentSearchList;
	}

	public String navigateReturn() {
		return "equipmentSearchList";
	}


}

