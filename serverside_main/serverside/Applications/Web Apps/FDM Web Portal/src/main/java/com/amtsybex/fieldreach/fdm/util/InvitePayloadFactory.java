package com.amtsybex.fieldreach.fdm.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.user.utils.impl.InviteAuth;
import com.amtsybex.fieldreach.user.utils.impl.InviteClaims;
import com.amtsybex.fieldreach.user.utils.impl.InviteResources;
import com.amtsybex.fieldreach.user.utils.impl.MobileInviteToken;
import com.amtsybex.fieldreach.user.utils.impl.SystemInviteToken;

@Component
public class InvitePayloadFactory {

   private static String authorityUri;
   private static String tenantUri;
   private static String clientId;
   private static String portalUri;
   
   private static String  RESOURCES_TYPE = "tenant";
   private static List<String> RESOURCES_AUTH = Arrays.asList("default");
   private static String  CLAIMS_TYPE = "user/info";
   private static String  AUTH_TYPE = "oidc";
   private static String  AUTH_ID = "default";
   
   
   @Value("${spring.security.oauth2.client.provider.auth0.issuer-uri}")
   public void setAthorityUri(String authUri) {
       
       authorityUri = authUri;
   }  
   
   @Value("${tenant-uri.mws:#{null}}")
   public void setTenantUri(String mws) {
       
       tenantUri = mws;
   }
   
   @Value("${tenant-uri.portal:#{null}}")
   public void setPortalUri(String uri) {
       
	   portalUri = uri;
   }
   
   @Value("${spring.security.oauth2.client.registration.auth0.client-id:#{null}}")
   public void setClientId(String clientid) {
       
       clientId = clientid;
   }  
    
   /**
    * Get Invite token Claims section object 
    * @param workGroupCode
    * @param invitationId
    * @param expiryDate
    * @return InviteClaims 
    */
    public static InviteClaims getInviteClaimsObj(String workGroupCode, String invitationId, int expiryDate) {
        
        InviteClaims inviteClaims = new InviteClaims();
        inviteClaims.setWorkgroup(workGroupCode);
        inviteClaims.setExpiry(expiryDate);
        inviteClaims.setInviteid(invitationId);
        inviteClaims.setType(CLAIMS_TYPE);
        return inviteClaims;
    }
    
    /**
     * Get Invite token Auth section object 
     * @return InviteAuth
     */
    public static InviteAuth getInviteAuthObj() {
        
        InviteAuth inviteAuth = new InviteAuth();
        inviteAuth.setType(AUTH_TYPE); 
        inviteAuth.setId(AUTH_ID);
        inviteAuth.setAuthority_uri(authorityUri);
        inviteAuth.setClient_id(clientId);
        return inviteAuth;
        
    }
    
    /**
     * 
     * Get Invite token Resources section object    
     * @return
     * 
     */
    public static InviteResources getInviteResourcesObj() {
        
        InviteResources inviteResources = new InviteResources();
        inviteResources.setUri(tenantUri);
        inviteResources.setType(RESOURCES_TYPE);
        inviteResources.setAuth(RESOURCES_AUTH);
        return inviteResources;
        
    }
    
    /**
     * Prepare final invite token claims object using single entity objects 
     * @param claims
     * @param auth
     * @param resource
     * @return InviteToken
     */
    public static MobileInviteToken getInviteTokenObj(InviteClaims claims, InviteAuth auth, InviteResources resource ) {
        
        return getInviteTokenObj(Arrays.asList(claims), Arrays.asList(auth), Arrays.asList(resource));
    }
    
    /**
     * Prepare final invite token claims object using list of entity objects 
     * @param claims
     * @param auth
     * @param resources
     * @return InviteToken
     */
    public static MobileInviteToken getInviteTokenObj(List<InviteClaims> claims, List<InviteAuth> auth, List<InviteResources> resources ) {
        
        MobileInviteToken inviteToken = new MobileInviteToken();
        inviteToken.setClaims(claims);
        inviteToken.setAuth(auth);
        inviteToken.setResources(resources);
        return inviteToken;
        
    }
    
    public static SystemInviteToken getSystemInviteTokenObj(SystemUsers systemUser, String invitationId, int expiryDate) {
    	
    	SystemInviteToken sysInviteToken = new SystemInviteToken();
    	sysInviteToken.setUri(portalUri);
    	sysInviteToken.setInviteid(invitationId);
    	sysInviteToken.setUsercode(systemUser.getId());
    	sysInviteToken.setUsername(systemUser.getUserName());
    	sysInviteToken.setPortalaccessgroup(systemUser.getFdmGroupCode());
    	sysInviteToken.setSbaccessgroup(systemUser.getSbGroupCode());
    	sysInviteToken.setWindowslogin(systemUser.getWinLogin());
    	sysInviteToken.setUserclass(systemUser.getUserClass());
    	sysInviteToken.setAdmin(systemUser.getAdminUser());
		sysInviteToken.setExpiry(expiryDate);
    	
    	return sysInviteToken;
    	
    }

}
