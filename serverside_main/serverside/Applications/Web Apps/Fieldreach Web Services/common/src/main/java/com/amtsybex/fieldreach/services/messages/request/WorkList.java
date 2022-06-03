package com.amtsybex.fieldreach.services.messages.request;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("WorkList")
public class WorkList implements Serializable {

	private static final long serialVersionUID = -788590515452432499L;
	
	private String userCode;
	private String workgroupCode;
	
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getWorkgroupCode() {
		return workgroupCode;
	}
	public void setWorkgroupCode(String workgroupCode) {
		this.workgroupCode = workgroupCode;
	}


	
}