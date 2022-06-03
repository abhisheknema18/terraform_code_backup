package com.amtsybex.fieldreach.fdm;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.amtsybex.fieldreach.backend.service.SystemParametersService;

public class SystemParametersServiceProducer {

	
	@Produces @Named @ApplicationScoped
	public SystemParametersService getSystemParametersService(){

		return (SystemParametersService) WebApplicationContextUtils
				.getRequiredWebApplicationContext((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getBean("systemParametersService");

	}
	
}
