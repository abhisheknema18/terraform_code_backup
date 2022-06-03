package com.amtsybex.fieldreach.swagger.model;

public class ValidationTypeRequest { 
	private ValidationTypeList validationTypeList;
	private String checksum;
	public ValidationTypeList getValidationTypeList() {
		return validationTypeList;
	}
	public void setValidationTypeList(ValidationTypeList validationTypeList) {
		this.validationTypeList = validationTypeList;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}


}
