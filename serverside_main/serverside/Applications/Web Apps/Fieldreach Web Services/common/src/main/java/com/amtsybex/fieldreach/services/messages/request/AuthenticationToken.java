package com.amtsybex.fieldreach.services.messages.request;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("AuthenticationToken")
public class AuthenticationToken implements Serializable {

	
	/**
     * 
     */
    private static final long serialVersionUID = 5639589415891548941L;
    
    public static final String APPLICATION_VND_FIELDSMART_AUTHENTICATIONTOKEN_1_JSON = "application/vnd.fieldsmart.authentication-token-1+json";
    public static final String APPLICATION_VND_FIELDSMART_AUTHENTICATIONTOKEN_1_XML = "application/vnd.fieldsmart.authentication-token-1+xml";
    
    private String inviteToken;
	private String identityToken;
    public String getInviteToken() {
        
        return inviteToken;
    }
    public void setInviteToken(String inviteToken) {
        
        this.inviteToken = inviteToken;
    }
    public String getIdentityToken() {
        
        return identityToken;
    }
    public void setIdentityToken(String identityToken) {
        
        this.identityToken = identityToken;
    }
	
}