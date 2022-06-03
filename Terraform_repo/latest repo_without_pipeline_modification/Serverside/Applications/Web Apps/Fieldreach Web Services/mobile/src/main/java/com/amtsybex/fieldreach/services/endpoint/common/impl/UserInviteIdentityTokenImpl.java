package com.amtsybex.fieldreach.services.endpoint.common.impl;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;

import com.amtsybex.fieldreach.exception.InvalidRequestTokenException;
import com.amtsybex.fieldreach.services.endpoint.common.UserInviteIdentityToken;
import com.amtsybex.fieldreach.utils.AccessTokenAuthor;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.SignedJWT;

import net.minidev.json.JSONObject;

public class UserInviteIdentityTokenImpl implements UserInviteIdentityToken {
    
    private static final String EMPTY_INVITE_TOKEN = "Empty invite token";
    private static final String EMPTY_IDENTITY_TOKEN = "Empty identity token";
    
    @Autowired
    private AccessTokenAuthor AccessTokenAuthorImpl;

    @Override
    public JSONObject getInviteTokenPayload(String inviteToken) 
            throws ParseException, InvalidRequestTokenException {
        
        if(null == inviteToken) {
            
            throw new InvalidRequestTokenException(EMPTY_INVITE_TOKEN);
        }
        
        SignedJWT inviteTokenDetails = SignedJWT.parse(inviteToken);
        
        if(!AccessTokenAuthorImpl.validateInviteToken(inviteTokenDetails)) {
          
          throw new InvalidRequestTokenException(INVALID_INVITE_SIGNATURE);
        }
        
        if (null == inviteTokenDetails || null == inviteTokenDetails.getPayload() || null == inviteTokenDetails.getPayload().toJSONObject() ) {
            
            throw new InvalidRequestTokenException(EMPTY_INVITE_TOKEN);
        }
        
        return inviteTokenDetails.getPayload().toJSONObject();
    }

    @Override
    public JSONObject getIdentityTokenPayload(String identityToken)
            throws ParseException, InvalidRequestTokenException {
        
        if(null == identityToken) {
            
            throw new InvalidRequestTokenException(EMPTY_IDENTITY_TOKEN);
        }
        
        JWSObject identityTokenDetails = JWSObject.parse(identityToken);
        if (null == identityTokenDetails || null == identityTokenDetails.getPayload()  || null == identityTokenDetails.getPayload().toJSONObject() ) {
            
            throw new InvalidRequestTokenException(EMPTY_IDENTITY_TOKEN);
        }
        
        return identityTokenDetails.getPayload().toJSONObject();
    }

}
