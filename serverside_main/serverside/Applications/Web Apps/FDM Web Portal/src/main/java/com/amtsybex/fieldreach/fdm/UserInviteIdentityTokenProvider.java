package com.amtsybex.fieldreach.fdm;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.amtsybex.fieldreach.user.utils.UserInviteIdentityToken;

public class UserInviteIdentityTokenProvider {
	
	@Produces @Named @ApplicationScoped
    public UserInviteIdentityToken getUserInviteIdentityToken(){

        return (UserInviteIdentityToken) WebApplicationContextUtils
                .getRequiredWebApplicationContext((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getBean("userInviteIdentityToken");

    }

}
