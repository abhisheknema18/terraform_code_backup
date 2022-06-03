package com.amtsybex.fieldreach.fdm.equipment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.EquipmentAttrib;
import com.amtsybex.fieldreach.backend.model.EquipmentData;
import com.amtsybex.fieldreach.backend.service.EquipmentService;

@Named
@WindowScoped
public class EquipmentInformation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1897531762137318806L;

	@Inject
	transient private EquipmentService equipmentService;
	
	private EquipmentData equipmentData;
	
	private List<EquipmentAttrib> equipmentAttribList;
	
	private String location;

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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void populateEquipmentAssetInformation(String equipNo) throws FRInstanceException {
		this.setEquipmentData(this.equipmentService.getEquipmentData(null, equipNo));
		location=null;
		if(this.getEquipmentData()!=null) {
			if(this.getEquipmentData().getLatitude()!=null && this.getEquipmentData().getLongitude()!=null) {
				this.setLocation(this.getEquipmentData().getLatitude()+","+this.getEquipmentData().getLongitude());
			}			
		this.setEquipmentAttribList(this.equipmentService.getEquipmentAttribList(null, this.getEquipmentData().getEquipNo()));
		}
	}
	

	public List<EquipmentAttrib> filteredEquipAttribList() {
		List<EquipmentAttrib> filteredEquipAttribList = new ArrayList<EquipmentAttrib>();
		HashMap<String, String> nameLookUpMap = new HashMap<String, String>();
		HashMap<String, String> valueLookUpMap = new HashMap<String, String>();
		if (this.getEquipmentAttribList() != null) {
			for (EquipmentAttrib equipAttrib : this.getEquipmentAttribList()) {
				if (equipAttrib != null) {
					if (equipAttrib.getAttributeName().toUpperCase().endsWith("_NAMEDESC")) {
						int index = equipAttrib.getAttributeName().indexOf("_NAMEDESC");
						nameLookUpMap.put(equipAttrib.getAttributeName().substring(0,index), equipAttrib.getValue());
					}
					else if (equipAttrib.getAttributeName().toUpperCase().endsWith("_VALUEDESC")) {
						int index = equipAttrib.getAttributeName().indexOf("_VALUEDESC");
						valueLookUpMap.put(equipAttrib.getAttributeName().substring(0,index), equipAttrib.getValue());
					}
				}
			}
			for (EquipmentAttrib equipmentAttrib : this.getEquipmentAttribList()) {
					if (equipmentAttrib != null) {
						if (equipmentAttrib.getAttributeName().equals("EQUIPNO")
								|| equipmentAttrib.getAttributeName().equals("PARENTEQUIPNO")
								|| equipmentAttrib.getAttributeName().equals("ALTEQUIPREF")
								|| equipmentAttrib.getAttributeName().equals("EQUIPDESC")
								|| equipmentAttrib.getAttributeName().equals("EQUIPTYPE")
								|| equipmentAttrib.getAttributeName().equals("LATITUDE")
								|| equipmentAttrib.getAttributeName().equals("LONGITUDE")
								|| equipmentAttrib.getAttributeName().endsWith("_NAMEDESC")
								|| equipmentAttrib.getAttributeName().endsWith("_VALUEDESC")) {
							continue;
						}
						EquipmentAttrib equipAttribute = new EquipmentAttrib();
						//copy the existing object to a new one, to preserve the original object
						equipAttribute = equipmentAttrib;
						if(valueLookUpMap.containsKey(equipmentAttrib.getAttributeName())) {
							equipAttribute.setValue(valueLookUpMap.get(equipmentAttrib.getAttributeName()));
						}
						if(nameLookUpMap.containsKey(equipmentAttrib.getAttributeName())){
							equipAttribute.setAttributeName(nameLookUpMap.get(equipmentAttrib.getAttributeName()));
						}
						filteredEquipAttribList.add(equipAttribute);
					}
				}
		}
		return filteredEquipAttribList;
	}
}
