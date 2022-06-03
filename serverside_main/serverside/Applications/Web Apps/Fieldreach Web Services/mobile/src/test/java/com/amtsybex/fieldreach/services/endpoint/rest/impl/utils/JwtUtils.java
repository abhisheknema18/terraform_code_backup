package com.amtsybex.fieldreach.services.endpoint.rest.impl.utils;


import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.amtsybex.fieldreach.services.messages.request.AuthenticationToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.SignedJWT;

import net.minidev.json.JSONObject;

public class JwtUtils {
    
   public static String IDENTITY_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjR4dEotLTVCMzU3aW1zNlRoQTF5SiJ9.ewogICJuaWNrbmFtZSI6ICJjcm9uaW4iLAogICJuYW1lIjogIkNST05JTiIsCiAgInBpY3R1cmUiOiAiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvZWExYzFkOWE0Y2EwMzIyZTBjNDBlZjIwZGRhYzJkNGU/cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZjci5wbmciLAogICJ1cGRhdGVkX2F0IjogIjIwMjEtMTAtMjdUMTI6NTQ6MTIuNTE4WiIsCiAgImlzcyI6ICJodHRwczovL21jYW10LmV1LmF1dGgwLmNvbS8iLAogICJzdWIiOiAiYXV0aDB8NjA2MWI2N2YyM2UxYzcwMDZhY2Q3YTczIiwKICAiYXVkIjogInRuNDdtTk80SjRneVNYREczYmh1YzExbWlZSTVKZ25oIiwKICAiaWF0IjogMTYzNTM0MDEwOCwKICAiZXhwIjogMTYzNTcwMDEwOAp9.BlGra0vRqZ9F_4aiAxEhUX2ewIM5qqzmF1TdJnjPrlqhRItbzh7Z33biawd-7wRNw8hNsJbVRoFspNKyQQlRYPJ2MntWEWeOoyzObV5me5H26kjnWYS1XPyMylYLcP3Pns8a2zv83XXOO70XkXtVlczgzMxCXJPGbzuxdpJ4qzGpg56Lti5JsiNnGP86qK8V2Lw3SXOgMI3sOc3RGNRsdlUyAfrIwvE-kop1kWvWwXqkrcJiwOdl_QNhzORdJ9GFzkYY6iX0VS8JOempAnBafnXTDJzLmADAs0hM16ReQYK-3itA_-JrWDYmxeAUW9ShXFkgZCy-g8O9LQo3yeo_wQ";
   public static String INVITE_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.ewogICJjZmciOiB7CiAgICAiYXV0aCI6IFsKICAgICAgewogICAgICAgICJhdXRob3JpdHlfdXJpIjogImh0dHBzOi8vbWNhbXQuZXUuYXV0aDAuY29tLyIsCiAgICAgICAgImlkIjogImRlZmF1bHQiLAogICAgICAgICJ0eXBlIjogIm9pZGMiLAogICAgICAgICJjbGllbnRfaWQiOiAidG40N21OTzRKNGd5U1hERzNiaHVjMTFtaVlJNUpnbmgiCiAgICAgIH0KICAgIF0sCiAgICAiY2xhaW1zIjogWwogICAgICB7CiAgICAgICAgIndvcmtncm91cCI6ICJDUk9OSU4iLAogICAgICAgICJpbnZpdGVpZCI6ICJmNWJjZWVkNi1lYTc5LTRkNTctOTQ3YS1hNjEyNjI2MjU1OTAiLAogICAgICAgICJleHBpcnkiOiA0MDIyMDEwNywKICAgICAgICAidHlwZSI6ICJ1c2VyL2luZm8iCiAgICAgIH0KICAgIF0sCiAgICAicmVzb3VyY2VzIjogWwogICAgICB7CiAgICAgICAgImF1dGgiOiBbCiAgICAgICAgICAiZGVmYXVsdCIKICAgICAgICBdLAogICAgICAgICJ0eXBlIjogInRlbmFudCIsCiAgICAgICAgInVyaSI6ICJodHRwOi8vbG9jYWxob3N0OjgwNzAvbW9iaWxlLyIKICAgICAgfQogICAgXQogIH0KfQ==.a_hu_iws-uQ0wYEu7Uy8OlrOPNjm3-6yEVEYB2GccWkJoamdSvexl3Nxt5ssfN3IQZzpCw_fkFNJnoxATs_SetmLRTYbYoFojZAUXrGomKcmBRjV_42PxXOwK39uq3wIVB-Ad_hVOzPjbXB08-Rmg_XIQPc-SzhK0fNqZhsA1Jh_cMGQ-x91go5mklsSMouS8uO7PUBvtbq8Mdcdr6rm1WGFdc2EYISGxdWw4N3dKrDoJpTm4e4s1frKA5pk5zi8aflerHldJAybEmFKXzjsur_hywix2N6kUZycg0WUn2U4N-uh2smTHC9YqCS0TYiuJLCNUcVAdMna_vOfoVpcAQ";
   public static String ISSUER = "issuer";
   public static String SUBJECT = "subject";
   
    
    public static Map<String, Object> setMockSecurityContext() {
        
        JwtAuthenticationToken authentication = Mockito.mock(JwtAuthenticationToken.class);
        authentication.setAuthenticated(true);
        Map<String, Object> attrts = new HashMap<>();
        attrts.put("sub", "subject");
        attrts.put("iss", "issuer");

        // Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        return attrts;
    }
    
    /**
     * FEtch Identity token payload 
     * 
     * @param identityToken
     * 
     * @return JSONObject
     */
    public static JSONObject getPayload(String identityToken) {
        
        JWSObject identityTokenDetails;
        try {
            
            identityTokenDetails = JWSObject.parse(identityToken);
        } catch (ParseException e) {
            
           return null;
        }
        
        return identityTokenDetails.getPayload().toJSONObject();
    }
    
    /**
     * Get payload from invite token 
     * 
     * @param InviteToken
     * 
     * @return JSONObject
     */
    public static JSONObject getInvitePayload(String InviteToken) {
        
        SignedJWT inviteTokenDetails;
        try {
            
            inviteTokenDetails = SignedJWT.parse(InviteToken);
        } catch (ParseException e) {
            
           return null;
        }
        
        return inviteTokenDetails.getPayload().toJSONObject();
    }
    
    /**
     * Prepare authentication request XML using identity token 
     * 
     * @param identityToken
     * 
     * @return XML string 
     * 
     * @throws JsonProcessingException
     */
    public static String getAuthenticatedRequestXml(String identityToken) 
            throws JsonProcessingException {
        
        XmlMapper xmlMapper = new XmlMapper();
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setIdentityToken(identityToken);
        return xmlMapper.writeValueAsString(authenticationToken);
    }
    
    /**
     *  Prepare authentication request XML using identity and invite token 
     *  
     * @param identityToken
     * 
     * @param inviteToken
     * 
     * @return
     * 
     * @throws JsonProcessingException
     */
    public static String getAuthenticatedRequestXml(String identityToken, String inviteToken) 
            throws JsonProcessingException {
        
        XmlMapper xmlMapper = new XmlMapper();
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setIdentityToken(identityToken);
        authenticationToken.setInviteToken(inviteToken);
        return xmlMapper.writeValueAsString(authenticationToken);
    }
    
    /**
     * Prepare AuthenticationToken object
     * 
     * @param inviteToken
     * 
     * @param identityToken
     * 
     * @return AuthenticationToken
     */
    public static AuthenticationToken getAuthenticationTokenDetails(String inviteToken, String identityToken) {
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setIdentityToken(identityToken);
        authenticationToken.setInviteToken(inviteToken);
        return authenticationToken;
    }
    
    public static String getAuthenticatedRequestJson(String identityToken) 
            throws JsonProcessingException {
        
        ObjectMapper json = new ObjectMapper();
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setIdentityToken(identityToken);

        return json.writeValueAsString(authenticationToken);
    }

}
