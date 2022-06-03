package com.amtsybex.fieldreach.fdm.web.jsf.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * helper class for setting messages on the front end
 */
public class MessageHelper {

	public static void setInfoMessage(String id, String str){
		FacesContext.getCurrentInstance().addMessage(	id, 
														new FacesMessage(FacesMessage.SEVERITY_INFO, 
														str, str));
	}
	
	public static void setErrorMessage(String id, String str){
		FacesContext.getCurrentInstance().addMessage(	id, 
														new FacesMessage(FacesMessage.SEVERITY_ERROR, 
														str, str));
	}
	
	public static void setWarnMessage(String id, String str){
		FacesContext.getCurrentInstance().addMessage(	id, 
														new FacesMessage(FacesMessage.SEVERITY_WARN, 
														str, str));
	}
	
	public static void setGlobalInfoMessage(String str){
		FacesContext.getCurrentInstance().addMessage(	null, 
														new FacesMessage(FacesMessage.SEVERITY_INFO, 
														str, str));
	}
	
	public static void setGlobalErrorMessage(String str){
		FacesContext.getCurrentInstance().addMessage(	null, 
														new FacesMessage(FacesMessage.SEVERITY_ERROR, 
														str, str));
	}
	
	public static void setGlobalWarnMessage(String str){
		FacesContext.getCurrentInstance().addMessage(	null, 
														new FacesMessage(FacesMessage.SEVERITY_WARN, 
														str, str));
	}
}
