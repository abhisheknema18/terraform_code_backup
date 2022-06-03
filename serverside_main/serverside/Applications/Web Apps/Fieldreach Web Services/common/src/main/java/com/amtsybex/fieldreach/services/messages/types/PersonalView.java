/**
 * Author:  T Murray
 * Date:    14/08/2015
 * Project: FDE034
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.messages.types;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PersonalView")
public class PersonalView implements Serializable {

	private static final long serialVersionUID = 424469439714381879L;
	
	private String viewId;
    private String viewName;
    private String viewDesc;
    private boolean viewDefault;
    
	public String getViewId() {
		return viewId;
	}
	
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}
	
	public String getViewName() {
		return viewName;
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public String getViewDesc() {
		return viewDesc;
	}
	
	public void setViewDesc(String viewDesc) {
		this.viewDesc = viewDesc;
	}
	
	public boolean isViewDefault() {
		return viewDefault;
	}
	
	public void setViewDefault(boolean viewDefault) {
		this.viewDefault = viewDefault;
	}
	
}
