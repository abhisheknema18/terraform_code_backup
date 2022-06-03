package com.amtsybex.fieldreach.services.endpoint.common;

import java.text.ParseException;

import com.amtsybex.fieldreach.exception.InvalidRequestTokenException;

import net.minidev.json.JSONObject;


public interface UserInviteIdentityToken {
  
    public static final String INVALID_INVITE_SIGNATURE = "Invalid invite token signature";
    public JSONObject getInviteTokenPayload(String inviteToken) throws ParseException, InvalidRequestTokenException; 
    public JSONObject getIdentityTokenPayload(String identityToken) throws ParseException, InvalidRequestTokenException;
}
