/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2015
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Code Re-factoring
 * 
 */
package com.amtsybex.fieldreach.services.endpoint.common.impl;


import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.InvalidUserCodeException;
import com.amtsybex.fieldreach.backend.instance.InstanceManager;
import com.amtsybex.fieldreach.backend.instance.Transaction;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCUsersAuth;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.Invitations;
import com.amtsybex.fieldreach.backend.service.SystemParametersService;
import com.amtsybex.fieldreach.backend.service.UserInvitationService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.exception.InvalidRequestTokenException;
import com.amtsybex.fieldreach.extract.utils.Utils;
import com.amtsybex.fieldreach.services.endpoint.common.UserController;
import com.amtsybex.fieldreach.services.endpoint.common.UserInviteIdentityToken;
import com.amtsybex.fieldreach.services.exception.ExpiredMobileInvitationException;
import com.amtsybex.fieldreach.services.exception.InvalidWorkgroupException;
import com.amtsybex.fieldreach.services.exception.RevokedMobileInvitationException;
import com.amtsybex.fieldreach.services.exception.UserNotFoundException;
import com.amtsybex.fieldreach.services.exception.UserRevokedException;
import com.amtsybex.fieldreach.services.messages.request.AuthenticationToken;

import com.amtsybex.fieldreach.user.utils.impl.InviteClaims;
import com.amtsybex.fieldreach.user.utils.impl.MobileInviteToken;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONObject;

public class UserControllerImpl implements UserController {

	static Logger log = LoggerFactory.getLogger(UserControllerImpl.class.getName());
	
  

	UserService userService;
	SystemParametersService systemParametersService;
	UserInviteIdentityToken userInviteIdentityToken;
	String userName;
	String userCode;
	UserInvitationService userInvitationService;
	
	@Autowired
    private ObjectMapper jacksonObjectMapper;

	@Autowired
	private InstanceManager instanceManager;

	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amtsybex.fieldreach.services.endpoint.common.UserController#
	 * getUsersByUserCode(java.lang.String, java.lang.String)
	 */
	@Override
	public List<HPCUsers> getUsersByUserCode(String frInstance, String userCode)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getUsersByUserCode userCode=" + userCode);

			log.debug("<<< getUsersByUserCode");
		}

		return getUserService().findHPCUsersByUserCode(frInstance, userCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.UserController#revokeUser
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public void revokeUser(String frInstance, String userCode)
			throws UserNotFoundException, FRInstanceException {

		if (log.isDebugEnabled())
			log.debug(">>> revokeUser userCode=" + userCode);

		HPCUsers objUser = null;

		Transaction trans = this.instanceManager.getTransaction(frInstance);

		try {

			// See if we can find user
			// PK on HPCUsers table indicates key on workgroup and usercode
			// So if list count of below is > 1 .. we cant use this login
			List<HPCUsers> lstUsers = getUserService().findHPCUsersByUserCode(
					frInstance, userCode);

			if (lstUsers == null || lstUsers.size() != 1)
				throw new UserNotFoundException();

			// 1 and only 1 user found
			objUser = lstUsers.get(0);

			// Set revoked flag on and save
			objUser.setRevoked(1);
			getUserService().saveHPCUser(frInstance, objUser);

			this.instanceManager.commitTransaction(trans);

		} catch (UserNotFoundException e) {

			this.instanceManager.rollbackTransaction(trans);
			throw e;

		} catch (FRInstanceException e) {

			this.instanceManager.rollbackTransaction(trans);
			throw e;

		}  catch (Throwable t) {
			
			log.error("UNEXPECTED EXCEPTION: " + t.getMessage());
			
			this.instanceManager.rollbackTransaction(trans);
			
			throw new RuntimeException(t);
		} 

		if (log.isDebugEnabled())
			log.debug("<<< revokeUser");
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.services.endpoint.common.UserController#getUser
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public HPCUsers getUser(String frInstance, String userCode, String workGroup)
			throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> getUser userCode=" + userCode + " workGroup="
					+ workGroup);

			log.debug("<<< getUser");
		}

		return getUserService().findHPCUser(frInstance, userCode, workGroup);
	}
	
	/**
	 *  Extract identity token
	 *  
	 * @param identityToken
	 * 
	 * @return
	 * 
	 * @throws InvalidRequestTokenException
	 */
    private JSONObject extractIdentityToken(String identityToken) 
            throws InvalidRequestTokenException {
        
       JSONObject identityPayload = null;
        try {
            
            identityPayload = userInviteIdentityToken.getIdentityTokenPayload(identityToken);
        } catch (ParseException e) {
            
            throw new InvalidRequestTokenException(IDENTITY_TOKEN_PARSE_ERROR);
        }
        return identityPayload;
    }

    @Override
    public HPCUsers addHPCUser(String applicationIdentifier, AuthenticationToken tokenDetails) 
            throws FRInstanceException, InvalidUserCodeException, InvalidWorkgroupException, InvalidRequestTokenException, RevokedMobileInvitationException, ExpiredMobileInvitationException {
        
        if (log.isDebugEnabled()) {


            log.debug("<<< addHPCUser");
        }
        
        String subject = getSubjectFromUserPrincipal();
        String issuer = getIssuerFromUserPrincipal();
        
        List<InviteClaims> inviteTokenDetails = getInviteTokenClaims(tokenDetails.getInviteToken());
        
        validateInvitationStatus(inviteTokenDetails);
        
        validateInvitationExpiry(inviteTokenDetails);
        
        String workGroupCode = getWorkGroupFromInviteToken(applicationIdentifier, inviteTokenDetails);
        JSONObject identityPayload = extractIdentityToken(tokenDetails.getIdentityToken());
        String hpcUserName = getHpcUserName(identityPayload);
        String hpcUserCode = getAvaiableHPCUserCode(applicationIdentifier, identityPayload);
        
        HPCUsersAuth newHpcUsersAuth = buildHpcUserAuth(hpcUserCode, issuer, subject);
        HPCUsers newMobieUser = buildHpcUsers(workGroupCode, hpcUserName, hpcUserCode, newHpcUsersAuth);
        
        userService.saveHPCUser(applicationIdentifier, newMobieUser);
        
        return newMobieUser;

    }

    /**
     * build HPCUserAuth object
     * 
     * @param hpcUserCode
     * 
     * @param issuer
     * 
     * @param subject
     * 
     * @return HPCUsersAuth object
     */
    private HPCUsersAuth buildHpcUserAuth(String hpcUserCode, String issuer, String subject) {
        
        HPCUsersAuth newHpcUsersAuth = new HPCUsersAuth(hpcUserCode);
        newHpcUsersAuth.setSubject(subject);
        newHpcUsersAuth.setIssuer(issuer);
        return newHpcUsersAuth;
    }

    /**
     * Build HpcUsers object 
     * 
     * @param workGroupCode
     * 
     * @param hpcUserName
     * 
     * @param hpcUserCode
     * 
     * @param newHpcUsersAuth
     * 
     * @return HPCUsers
     */
    private HPCUsers buildHpcUsers(String workGroupCode, String hpcUserName, String hpcUserCode, HPCUsersAuth newHpcUsersAuth) {
        
        HPCUsers newMobieUser = new HPCUsers(hpcUserCode, workGroupCode);
        newMobieUser.setUserName(hpcUserName);
        newMobieUser.setRevoked(0);
        newMobieUser.setLastModDate(Common.generateFieldreachDBDate(new Date()));
        newMobieUser.setLastModTime(Utils.normalizeFieldreachDBTime(Common.generateFieldreachDBTime(), true));
        
        newMobieUser.setHpcUsersAuth(newHpcUsersAuth);
        return newMobieUser;
    }

    /**
     * Prepare and get HPCUsercode using json identifier
     * 
     * @param applicationIdentifier
     * 
     * @param identityPayload
     * 
     * @return unique usercode
     * @throws FRInstanceException
     * @throws InvalidUserCodeException
     */
    private String getAvaiableHPCUserCode(String applicationIdentifier, JSONObject identityPayload)
            throws FRInstanceException, InvalidUserCodeException {
        
        if (null == identityPayload.get(userCode)) {
            
            log.error(EMPTY_USERCODE);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        
        String hpcUserCode = identityPayload.get(userCode).toString().replaceAll(USERCODE_MATCH_REGEX, "");
        
        if (hpcUserCode.isBlank()) {
            
            log.error(EMPTY_USERCODE);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        hpcUserCode = hpcUserCode.length() > USERCODE_MAX_LENGTH ? hpcUserCode.substring(0, USERCODE_MAX_LENGTH)
                : hpcUserCode;

        if (log.isDebugEnabled()) {

            log.debug(">>> getAvaiableHPCUserCode userCode=" + hpcUserCode);

            log.debug("<<< getAvaiableHPCUserCode");
        }
       return userService.generateUniqueUserCode(applicationIdentifier, hpcUserCode);
    }

    /**
     * prepare and get username from json identifier 
     * 
     * @param identityPayload
     * 
     * @return username
     */
    private String getHpcUserName(JSONObject identityPayload) {
        
        if (null == identityPayload.get(userName)) {
            
            log.error(EMPTY_USERNAME);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        
        String hpcUserName = identityPayload.get(userName).toString().replaceAll(USERNAME_MATCH_REGEX, "");
        
        if (hpcUserName.isBlank()) {
            
            log.error(EMPTY_USERNAME);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        hpcUserName = hpcUserName.length() > USERNAME_MAX_LENGTH ? hpcUserName.substring(0, USERNAME_MAX_LENGTH)
                : hpcUserName;
        return hpcUserName;
    }

    /**
     * Fetch workGroupCode from invite token 
     * @param applicationIdentifier
     * @param inviteTokenDetails
     * @return
     * @throws FRInstanceException
     * @throws InvalidWorkgroupException
     * @throws InvalidRequestTokenException
     */
    private String getWorkGroupFromInviteToken(String applicationIdentifier, List<InviteClaims> inviteTokenDetails)
            throws FRInstanceException, InvalidWorkgroupException, InvalidRequestTokenException {
        
        String workGroupCode = inviteTokenDetails.stream().findFirst().get().getWorkgroup();
        HPCWorkGroups workGroup = userService.findHPCWorkGroup(applicationIdentifier, workGroupCode);
        if (null == workGroup) {
            
            log.error(">>> Target Workgroup does not exist {} ", workGroupCode);
            throw new InvalidWorkgroupException(WORKGROUP_DOES_NOT_EXIST);
        } else if (workGroup.getActiveInt() == 0) {
            
            throw new InvalidWorkgroupException(INACTIVE_WORKGROUP);
        }
        
        return workGroupCode;
    }
    
    /**
     * Validate invitation status
     * @param inviteTokenDetails
     * @throws RevokedMobileInvitationException
     * @throws FRInstanceException
     */
    private void validateInvitationStatus(List<InviteClaims> inviteTokenDetails) throws RevokedMobileInvitationException, FRInstanceException {
      
      String inviteId = inviteTokenDetails.stream().findFirst().get().getInviteid();
      if (null == inviteId) {
  
        throw new RevokedMobileInvitationException("Invitation id not provided. Invalid claims");
      }
  
      Invitations invitation = userInvitationService.getInvitationById(null, inviteId);
      if (null == invitation || invitation.getInvitationStatus().equals("REVOKED")) {

        throw new RevokedMobileInvitationException(USER_INVITATION_REVOKED);
      }
     
    }
    
    /**
     * 
     * @param inviteTokenDetails
     * @throws ExpiredMobileInvitationException
     */
    private void validateInvitationExpiry(List<InviteClaims> inviteTokenDetails) throws ExpiredMobileInvitationException  {
      
      int tokenExpiry = inviteTokenDetails.stream().findFirst().get().getExpiry();
       
      if(Common.dateToExpiry(Common.convertFieldreachDate(tokenExpiry), 0) == -1) {
        throw new ExpiredMobileInvitationException(USER_INVITATION_EXPIRED);
      }
     
    }

    /**
     * Get Invite token claims 
     * @param string inviteToken
     * @return List<InviteClaims> list of claims 
     * @throws InvalidRequestTokenException
     */
    private List<InviteClaims> getInviteTokenClaims(String inviteToken) throws InvalidRequestTokenException {
      
      JSONObject inviteTokenPayload = this.getInvitePayload(inviteToken);
      MobileInviteToken inviteTokenDetails = null;
      
      try {
          
          inviteTokenDetails = jacksonObjectMapper.readValue(inviteTokenPayload.get("cfg").toString(), MobileInviteToken.class);
      } catch (JsonProcessingException e) {
          
          log.error(">>> JsonProcessingException");
          throw new InvalidRequestTokenException(INVITE_TOKEN_INCOMPLETE_PAYLOAD);
      }
      
      if (null == inviteTokenDetails || null == inviteTokenDetails.getClaims()
              || inviteTokenDetails.getClaims().size() < 1) {
          
          throw new InvalidRequestTokenException(INVITE_TOKEN_MISSING_CLAIMS);
      }
      return inviteTokenDetails.getClaims();
      
    }

    /**
     * Get Payload JSON from invitationToken 
     * @param inviteToken
     * @return JSONObject with payload json 
     * @throws InvalidRequestTokenException
     */
    private JSONObject getInvitePayload(String inviteToken) throws InvalidRequestTokenException {
      
      JSONObject inviteTokenPayload;
      try {
          
          inviteTokenPayload = userInviteIdentityToken.getInviteTokenPayload(inviteToken);
      } catch (ParseException e) {
          
          throw new InvalidRequestTokenException(INVITE_TOKEN_PARSE_ERROR);
      }
      return inviteTokenPayload;
      
    }
    
    public Optional<HPCUsers> getActiveUserDetails()  throws UserRevokedException {
      
        HPCUsers user = null;
        String subject = getSubjectFromUserPrincipal();
        String issuer = getIssuerFromUserPrincipal();
        
        try {
            
            user = userService.findHPCUserByOAuthPrincipal(null, issuer, subject);
            if(null != user && user.getRevokedBool()) {
                throw new UserRevokedException();
            }
        } catch (FRInstanceException e) {
            
            log.error("FRInstanceException Error while fetching user details {}", e.getMessage());
        }
        
        return Optional.ofNullable(user);
        
    }
    
    /**
     * get subject from user principal
     * 
     * @return String userPrincipal
     */
    private String getSubjectFromUserPrincipal() {
        
        if (log.isDebugEnabled())
            log.debug(">>> getSubjectFromUserPrincipal");
        
       return getAttributeFromAuthenticationToken(SUBJECT);
    }
    
    /**
     * Get issuer from userPrincipal
     * @return
     */
    private String getIssuerFromUserPrincipal() {
        
        if (log.isDebugEnabled())
            log.debug(">>> getIssuerFromUserPrincipal");
        
       return getAttributeFromAuthenticationToken(ISSUER);
    }
    
    /**
     * Get Attribute value from Auhtentication token 
     * @param attributeName
     * @return
     */
    private String getAttributeFromAuthenticationToken(String attributeName) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof BearerTokenAuthentication) {
            
            BearerTokenAuthentication bearer = (BearerTokenAuthentication) authentication;
            return attributeName.equals(SUBJECT) ?  (String) bearer.getTokenAttributes().get(attributeName) :((URL) bearer.getTokenAttributes().get(attributeName)).toString();
            
        } else if (authentication instanceof JwtAuthenticationToken) {
            
            JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
            return (String) jwt.getTokenAttributes().get(attributeName);

        }
        
        return StringUtils.EMPTY;
    }

	// FDE029 TM 19/01/2015
	// Removed resolveUserDatabaseClass method.
	// End FDE029

	/*-------------------------------------------
	 - Spring Injection Methods
	 --------------------------------------------*/


	public UserService getUserService() {

		return userService;
	}

	public void setUserService(UserService userService) {

		this.userService = userService;
	}

	public SystemParametersService getSystemParametersService() {

		return systemParametersService;
	}

	public void setSystemParametersService(
			SystemParametersService systemParametersService) {

		this.systemParametersService = systemParametersService;
	}

    public UserInviteIdentityToken getUserInviteIdentityToken() {
        
        return userInviteIdentityToken;
    }

    public void setUserInviteIdentityToken(UserInviteIdentityToken userInviteIdentityToken) {
        
        this.userInviteIdentityToken = userInviteIdentityToken;
    }

    public String getUserName() {
        
        return userName;
    }

    public void setUserName(String userName) {
        
        this.userName = userName;
    }

    public String getUserCode() {
        
        return userCode;
    }

    public void setUserCode(String userCode) {
        
        this.userCode = userCode;
    }

    public UserInvitationService getUserInvitationService() {
      return userInvitationService;
    }

    public void setUserInvitationService(UserInvitationService userInvitationService) {
      this.userInvitationService = userInvitationService;
    }
	
    
	

}
