package com.amtsybex.fieldreach.fdm.equipment;

import java.io.Serializable;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.PrimeFaces;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.MaxResultsExceededException;
import com.amtsybex.fieldreach.backend.model.EquipmentData;
import com.amtsybex.fieldreach.backend.service.EquipmentService;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;

@Named
@WindowScoped
public class EquipmentSearch extends PageCodebase implements Serializable {

	private static final long serialVersionUID = -3697783279978091055L;	
	
	//FDE060 - KN - reference no for equipment search
	private String equipmentReferenceNo;
	private String equipmentNo;
	private String equipmentType;
	@Inject
	transient EquipmentService equipmentService;
	@Inject
	transient EquipmentHierarchy equipmentHierarchy;
	@Inject
	transient EquipmentSearchList equipmentSearchList;
	
	
	public void resetSearch(){
		
		this.equipmentNo = "";
		this.equipmentType = "";
		this.equipmentReferenceNo = "";
	}
	
	//FDE060 - KN - equipment Search
	public String searchReference() throws FRInstanceException {

		EquipmentData equipmentData = null;

		if (equipmentData == null) {
			equipmentData = getEquipmentService().getEquipmentData(null, this.equipmentNo);
		}
		
		if(equipmentData != null) {
			equipmentHierarchy.init(equipmentData);
			this.resetSearch();
			
		} else {
			MessageHelper.setGlobalWarnMessage("No Asset Found");
			return null;
		}

		return "equipmentHierarchy";
	}
	
	public String equipmentSearch()throws FRInstanceException {
		
		if(this.equipmentNo.isEmpty() && this.equipmentReferenceNo.isEmpty() && this.equipmentType.isEmpty()) {
			MessageHelper.setErrorMessage("messages", "Please define search criteria.");
			FacesContext.getCurrentInstance().validationFailed();
			PrimeFaces.current().ajax().addCallbackParam("validationErrorOccured", true);
			return null;
		}
		
		try {
			List<EquipmentData> equipList = getEquipmentService().getEquipmentDataList(null, this.equipmentNo, this.equipmentReferenceNo, this.equipmentType);
			equipmentSearchList.initiateSearchList(equipList);
			if(equipList.size() < 1) {
				MessageHelper.setGlobalWarnMessage("No Results Found");
				return null;
			}
		} catch (MaxResultsExceededException e) {
			// if too many results are returned, tell the user to reduce search options
			MessageHelper.setGlobalWarnMessage("Search returned too many rows Try using more search criteria");
			return null;
		}
		PrimeFaces.current().multiViewState().clearAll();
		this.resetSearch();
		return "equipmentSearchList";
		
	}

	public String getEquipmentReferenceNo() {
		return equipmentReferenceNo;
	}

	public void setEquipmentReferenceNo(String equipmentReferenceNo) {
		this.equipmentReferenceNo = equipmentReferenceNo;
	}

	public EquipmentService getEquipmentService() {
		return equipmentService;
	}

	public void setEquipmentService(EquipmentService equipmentService) {
		this.equipmentService = equipmentService;
	}

	public EquipmentHierarchy getEquipmentHierarchy() {
		return equipmentHierarchy;
	}

	public void setEquipmentHierarchy(EquipmentHierarchy equipmentHierarchy) {
		this.equipmentHierarchy = equipmentHierarchy;
	}

	public String getEquipmentNo() {
		return equipmentNo;
	}

	public void setEquipmentNo(String equipmentNo) {
		this.equipmentNo = equipmentNo;
	}

	public String getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}
	public Integer getSize() {
		return 3;
	}

}
