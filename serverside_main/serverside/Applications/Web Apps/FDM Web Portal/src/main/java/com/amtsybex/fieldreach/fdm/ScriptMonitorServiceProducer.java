package com.amtsybex.fieldreach.fdm;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.amtsybex.fieldreach.backend.service.ScriptMonitorService;

public class ScriptMonitorServiceProducer {

	@Produces @Named @ApplicationScoped
	public ScriptMonitorService getScriptMonitorService(){

		return (ScriptMonitorService) WebApplicationContextUtils
				.getRequiredWebApplicationContext((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getBean("scriptMonitorService");

	}
}
