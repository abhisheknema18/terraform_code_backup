package com.amtsybex.fieldreach.fdm;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.backend.service.ValidationTypeService;
import com.amtsybex.fieldreach.services.endpoint.common.ScriptController;

public class ScriptServiceProducer {

	@Produces @Named @ApplicationScoped
	public ScriptService getScriptService(){

		return (ScriptService) WebApplicationContextUtils
				.getRequiredWebApplicationContext((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getBean("scriptService");

	}

	@Produces @Named @ApplicationScoped
	public ValidationTypeService getvalidationTypeService(){

		return (ValidationTypeService) WebApplicationContextUtils
				.getRequiredWebApplicationContext((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getBean("validationTypeService");

	}
	
	@Produces @Named @ApplicationScoped
	public ScriptController getScriptController(){

		return (ScriptController) WebApplicationContextUtils
				.getRequiredWebApplicationContext((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getBean("scriptController");

	}
}
