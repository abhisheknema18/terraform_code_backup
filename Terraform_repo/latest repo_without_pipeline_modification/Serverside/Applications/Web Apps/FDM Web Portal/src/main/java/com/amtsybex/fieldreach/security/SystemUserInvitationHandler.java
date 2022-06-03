package com.amtsybex.fieldreach.security;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.model.AccessGroups;
import com.amtsybex.fieldreach.backend.model.Invitations;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.model.SystemUsersAuth;
import com.amtsybex.fieldreach.backend.model.pk.SystemUsersAuthId;
import com.amtsybex.fieldreach.backend.service.AccessGroupService;
import com.amtsybex.fieldreach.backend.service.UserInvitationService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.exception.InvalidRequestTokenException;
import com.amtsybex.fieldreach.fdm.SystemActivityLogger;
import com.amtsybex.fieldreach.fdm.web.jsf.util.Properties;
import com.amtsybex.fieldreach.user.utils.UserInviteIdentityToken;
import com.amtsybex.fieldreach.user.utils.impl.SystemInviteToken;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import static com.amtsybex.fieldreach.fdm.admin.UserInvitation.INVITATIONSTATUS;

@Named
@WindowScoped
public class SystemUserInvitationHandler implements Serializable {

	private static final long serialVersionUID = 7668619072569506050L;

	private static Logger logger = LoggerFactory.getLogger(SystemUserInvitationHandler.class.getName());

	private String token;

	private String errorMessage;

	@Inject
	transient UserService userService;

	@Inject
	UserInviteIdentityToken userInviteIdentityToken;

	@Inject
	transient SystemActivityLogger systemActivityLogger;

	@Inject
	transient UserInvitationService userInvitationService;

	@Inject
	transient AccessGroupService accessGroupService;

	public void processInvitation() {

		Map<String, Object> inviteTokenPayload = null;
		SystemInviteToken systemInviteToken = null;
		try {

			try {

				inviteTokenPayload = userInviteIdentityToken.getInviteTokenPayload(token);
				systemInviteToken = getSystemInviteClaims(inviteTokenPayload);
			} catch (Exception e) {

				logger.error("Error while processing System Invitation. {}", e.getMessage());
				throw new Exception(Properties.get("fdm_invalid_system_user_invitation"));
			}

			OAuth2User oAuth2User = getAuthenticatedUserDetails();
			String subject = oAuth2User.getAttribute("sub");
			URL issuer = oAuth2User.getAttribute("iss");
			String userCode = systemInviteToken.getUsercode();
			String userName = systemInviteToken.getUsername();
			String portalAccessGroup = systemInviteToken.getPortalaccessgroup();

			validateUserDetails(issuer, subject, userCode, userName, portalAccessGroup);

			if (Common.dateToExpiry(Common.convertFieldreachDate(systemInviteToken.getExpiry()), 0) == -1) {

				logger.error("System user invitation Expired {}",systemInviteToken.getInviteid() );
				throw new Exception(Properties.get("fdm_system_user_invitation_expired"));
			}

			Invitations invitationDetails = validateAndGetSystemUserInvitation(systemInviteToken);

			if (isSystemUserExists(subject, issuer, userCode)) {

				throw new Exception(Properties.get("fdm_system_user_invitation_no_longer_valid"));
			}

			if (!isValidAccessGroup(systemInviteToken)) {

				throw new Exception(Properties.get("fdm_system_user_invitation_is_invalid"));
			}

			CreateSystemUserAndUpdateInvitationStatus(systemInviteToken, subject, issuer, userCode, userName,
					invitationDetails);

			systemActivityLogger.recordActivityLog(null, SystemActivityLogger.ACTIVITYTYPE.SYSTEM_USER_REG, userCode,
					"New System User " + userCode + " Registered", null);

			FacesContext.getCurrentInstance().getApplication().getNavigationHandler()
			.handleNavigation(FacesContext.getCurrentInstance(), null, "home");
		} catch (FRInstanceException fre) {

			logFailedSystemActivity(Properties.get("fdm_system_user_invitation_process_error"), systemInviteToken);
		} catch (Exception e) {

			logFailedSystemActivity(e.getMessage(), systemInviteToken);
		}

	}

	/**
	 * Log failed system user creation activity and set error message
	 * 
	 * @param errorDetails
	 */
	private void logFailedSystemActivity(String errorDetails, SystemInviteToken systemInviteToken) {

		String invitationId = null != systemInviteToken ? systemInviteToken.getInviteid() : StringUtils.EMPTY;
		this.errorMessage = errorDetails;
		systemActivityLogger.recordActivityLog(null, SystemActivityLogger.ACTIVITYTYPE.SYSTEM_USER_REG_FAIL, "SYSTEM",
				errorDetails+ " [invitationId] = "+ invitationId, null);
	}

	/**
	 * Create System User alsong with system user auth and update Invitation status
	 * as COMPLETE
	 * 
	 * @param systemInviteToken
	 * @param subject
	 * @param issuer
	 * @param userCode
	 * @param userName
	 * @param invitation
	 * @throws FRInstanceException
	 */
	private void CreateSystemUserAndUpdateInvitationStatus(SystemInviteToken systemInviteToken, String subject,
			URL issuer, String userCode, String userName, Invitations invitation) throws FRInstanceException {

		SystemUsersAuthId systemUsersAuthId = new SystemUsersAuthId(userCode);
		SystemUsersAuth systemUsersAuth = new SystemUsersAuth();
		systemUsersAuth.setId(systemUsersAuthId);
		systemUsersAuth.setIssuer(issuer.toString());
		systemUsersAuth.setSubject(subject);

		SystemUsers systemUsers = new SystemUsers(userCode, userName, Common.generateFieldreachDBDate(),
				systemInviteToken.getSbaccessgroup(), systemInviteToken.getPortalaccessgroup(), 0, systemInviteToken.getAdmin(),
				systemInviteToken.getWindowslogin(), null, systemInviteToken.getUserclass(), Common.generateFieldreachDBDate(),
				StringUtils.leftPad(String.valueOf(Common.generateFieldreachDBTime()), 6, '0'));

		systemUsers.setSystemUsersAuth(systemUsersAuth);

		invitation.setInvitationStatus(INVITATIONSTATUS.COMPLETE.toString());

		userService.saveSystemUser(null, systemUsers, invitation);
	}

	/**
	 * Validate Sytem user Mandatory fields values
	 * 
	 * @param issuer
	 * @param subject
	 * @param userCode
	 * @param userName
	 * @param portalAccessGroup
	 * @throws Exception
	 */
	private void validateUserDetails(URL issuer, String subject, String userCode, String userName,
			String portalAccessGroup) throws Exception {

		if (null == issuer || StringUtils.isEmpty(subject) || StringUtils.isEmpty(userCode) || StringUtils.isEmpty(userName)
				|| StringUtils.isEmpty(portalAccessGroup)) {

			logger.error(
					"Missing required mandatory field values :: userCode = {}, userName= {}, portalAccessGroup={}",
					 userCode, userName, portalAccessGroup);
			throw new Exception(Properties.get("fdm_invalid_system_user_invitation"));
		}
	}

	/**
	 * Validate Portal and SB access Groups 
	 * @param systemInviteToken
	 * @return boolean true on valid access group else false 
	 * @throws FRInstanceException
	 * @throws Exception
	 */
	private boolean isValidAccessGroup(SystemInviteToken systemInviteToken) throws FRInstanceException, Exception {

		if (!StringUtils.isEmpty(systemInviteToken.getPortalaccessgroup())) {

			AccessGroups portalAccessGroupDetails = accessGroupService.getAccessGroup(null,
					systemInviteToken.getPortalaccessgroup());
			if (null == portalAccessGroupDetails || !portalAccessGroupDetails.getProgCode().equals("FDM")) {

				logger.error("Provided Portal access group is not part of FDM progCode. portalAccessGroup = {}",
						systemInviteToken.getPortalaccessgroup());
				return false;
			}
		}

		if (!StringUtils.isEmpty(systemInviteToken.getSbaccessgroup())) {

			AccessGroups sbAccessGroupDetails = accessGroupService.getAccessGroup(null, systemInviteToken.getSbaccessgroup());
			if (null == sbAccessGroupDetails || !sbAccessGroupDetails.getProgCode().equals("SB")) {

				logger.error("Provided SB access group is not part of SB progCode. portalAccessGroup = {}",
						systemInviteToken.getSbaccessgroup());
				return false;
			}
		}
		return true;
	}

	/**
	 * Check If System user and System user auth entry already exists based on subject, issuer, userCode
	 * @param subject
	 * @param issuer
	 * @param userCode
	 * @return boolean true if entry exists elase false
	 * @throws FRInstanceException
	 */
	private boolean isSystemUserExists(String subject, URL issuer, String userCode) throws FRInstanceException {

		SystemUsers systemUserDetails = userService.getSystemUser(null, userCode);

		if (null != systemUserDetails) {

			logger.error("System user already exists. UserCode = {}", userCode);
			return true;

		}
		SystemUsersAuth systemUserAuthDetails = userService.getSystemUserAuth(null, subject, issuer.toString());
		if (null != systemUserAuthDetails) {

			logger.error("System user subject/issuer already exists for usercode = {}", userCode);
			return true;
		}
		return false;
	}

	/**
	 * Validate UserInvitation against its status 
	 * @param systemInviteToken
	 * @return boolean true on valid invitation else false 
	 * @throws FRInstanceException
	 * @throws Exception
	 */
	private Invitations validateAndGetSystemUserInvitation(SystemInviteToken systemInviteToken)
			throws FRInstanceException, Exception {

		Invitations invitationDetails = userInvitationService.getInvitationById(null, systemInviteToken.getInviteid());

		if (null == invitationDetails) {

			logger.error("System user Invitation does not exists {}", systemInviteToken.getInviteid());
			throw new Exception(Properties.get("fdm_system_user_invitation_no_longer_valid"));
		}

		if (invitationDetails.getInvitationStatus().equals("REVOKED")) {

			logger.error("System user Invitation revoked {}", systemInviteToken.getInviteid());
			throw new Exception(Properties.get("fdm_system_user_invitation_is_revoked"));
		}

		if (invitationDetails.getInvitationStatus().equals(INVITATIONSTATUS.COMPLETE.toString())) {

			logger.error("System user Invitation already processed {}", systemInviteToken.getInviteid());
			throw new Exception(Properties.get("fdm_system_user_invitation_already_processed"));
		}

		return invitationDetails;
	}

	/**
	 * Get logged in user Oauth token 
	 * @return
	 */
	private OAuth2User getAuthenticatedUserDetails() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		return oAuth2User;
	}

	/**
	 * Get Claims from InviteToken
	 * @param inviteTokenPayload
	 * @return
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 * @throws InvalidRequestTokenException
	 */
	private SystemInviteToken getSystemInviteClaims(Map<String, Object> inviteTokenPayload)
			throws JsonProcessingException, JsonMappingException, InvalidRequestTokenException {

		ObjectMapper obm = new ObjectMapper();
		if (null != inviteTokenPayload.get("claims")) {

			return obm.readValue(inviteTokenPayload.get("claims").toString(), SystemInviteToken.class);
		} else {

			throw new InvalidRequestTokenException("Missing claims under system invitation token.");
		}

	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
