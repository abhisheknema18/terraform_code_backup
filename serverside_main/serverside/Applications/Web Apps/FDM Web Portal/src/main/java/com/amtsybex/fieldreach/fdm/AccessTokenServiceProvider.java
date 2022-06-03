package com.amtsybex.fieldreach.fdm;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.amtsybex.fieldreach.backend.service.AccessTokenService;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor;

public class AccessTokenServiceProvider {
	
	@Produces @Named @ApplicationScoped
	public AccessTokenService getAccesTokenService(){

		return (AccessTokenService) WebApplicationContextUtils
				.getRequiredWebApplicationContext((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getBean("accessTokenService");

	}
	
	@Produces @Named @ApplicationScoped
	public AccessTokenAuthor getAccesTokenAuthor(){

		return (AccessTokenAuthor) WebApplicationContextUtils
				.getRequiredWebApplicationContext((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getBean("accessTokenAuthorImpl");

	}

}
