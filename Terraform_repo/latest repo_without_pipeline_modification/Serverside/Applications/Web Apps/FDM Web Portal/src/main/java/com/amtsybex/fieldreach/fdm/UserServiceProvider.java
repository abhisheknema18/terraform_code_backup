package com.amtsybex.fieldreach.fdm;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.amtsybex.fieldreach.backend.service.UserService;

public class UserServiceProvider {

	@Produces @Named @ApplicationScoped
	public UserService getUserService(){

		return (UserService) WebApplicationContextUtils
				.getRequiredWebApplicationContext((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getBean("userService");

	}

}
