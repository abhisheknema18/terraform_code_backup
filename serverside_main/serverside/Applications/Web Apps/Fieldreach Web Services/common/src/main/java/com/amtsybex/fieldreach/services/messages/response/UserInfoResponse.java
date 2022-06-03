package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;

public class UserInfoResponse implements Serializable {

	private static final long serialVersionUID = -196308016419051812L;

    public static final String APPLICATION_VND_FIELDSMART_USERINFO_1_JSON = "application/vnd.fieldsmart.user-info-1+json";
    public static final String APPLICATION_VND_FIELDSMART_USERINFO_1_XML = "application/vnd.fieldsmart.user-info-1+xml";
    
	private ErrorMessage error;

	private String userCode;
	private String userName;
	private String workGroupCode;
	private String workGroupDesc;
	private String wgCatDesc;
	private boolean systemUser;
	private String altRef;
	private String deviceId;
	private String userClass;
	private String wgClassA;
	private String wgClassB;
	private String profileName;
	private String appCodes;
	private String instance;
	private String mapkey;
	private String mapkeyBing;
	private String dataSec;
	private boolean success;

	public ErrorMessage getError() {
		return error;
	}

	public void setError(ErrorMessage error) {
		this.error = error;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getWorkGroupCode() {
		return workGroupCode;
	}

	public void setWorkGroupCode(String workGroupCode) {
		this.workGroupCode = workGroupCode;
	}

	public String getWorkGroupDesc() {
		return workGroupDesc;
	}

	public void setWorkGroupDesc(String workGroupDesc) {
		this.workGroupDesc = workGroupDesc;
	}

	public String getWgCatDesc() {
		return wgCatDesc;
	}

	public void setWgCatDesc(String wgCatDesc) {
		this.wgCatDesc = wgCatDesc;
	}

	public boolean isSystemUser() {
		return systemUser;
	}

	public void setSystemUser(boolean systemUser) {
		this.systemUser = systemUser;
	}

	public String getAltRef() {
		return altRef;
	}

	public void setAltRef(String altRef) {
		this.altRef = altRef;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUserClass() {
		return userClass;
	}

	public void setUserClass(String userClass) {
		this.userClass = userClass;
	}

	public String getWgClassA() {
		return wgClassA;
	}

	public void setWgClassA(String wgClassA) {
		this.wgClassA = wgClassA;
	}

	public String getWgClassB() {
		return wgClassB;
	}

	public void setWgClassB(String wgClassB) {
		this.wgClassB = wgClassB;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getAppCodes() {
		return appCodes;
	}

	public void setAppCodes(String appCodes) {
		this.appCodes = appCodes;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getMapkey() {
		return mapkey;
	}

	public void setMapkey(String mapkey) {
		this.mapkey = mapkey;
	}

	public String getDataSec() {
		return dataSec;
	}

	public void setDataSec(String dataSec) {
		this.dataSec = dataSec;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMapkeyBing() {
		return mapkeyBing;
	}

	public void setMapkeyBing(String mapkeyBing) {
		this.mapkeyBing = mapkeyBing;
	}
	

}
