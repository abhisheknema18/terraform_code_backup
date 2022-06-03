package com.amtsybex.fieldreach.fdm;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.amtsybex.fieldreach.fdm.property.PortalPropertyUtil;

public class PortalPropertyServiceProvider {
	

	@Produces @Named @ApplicationScoped
	public PortalPropertyUtil getPortalPropertyUtil(){

		return (PortalPropertyUtil) WebApplicationContextUtils
				.getRequiredWebApplicationContext((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getBean("portalPropertyUtil");

	}

	
}
