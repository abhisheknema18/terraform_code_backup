/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2015
 *  
 * Modified T Goodwin
 * Date 29/05/2012
 * Project FDE018
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Removal of /v1 namespace from web service end points 
 * and code re-factoring.
 */
package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.endpoint.common.UserController.AUTHENTICATING_USER_REVOKED;
import static com.amtsybex.fieldreach.services.messages.response.GetPersonalViewResponse.APPLICATION_VND_FIELDSMART_VIEW_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.GetPersonalViewResponse.APPLICATION_VND_FIELDSMART_VIEW_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.CallResponse.APPLICATION_VND_FIELDSMART_CALL_1_XML;
import static com.amtsybex.fieldreach.services.messages.response.UserInfoResponse.APPLICATION_VND_FIELDSMART_USERINFO_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.UserInfoResponse.APPLICATION_VND_FIELDSMART_USERINFO_1_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_JSON;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.APPLICATION_XML;
import static com.amtsybex.fieldreach.services.utils.DocumentationConstants.USER_CODE_DESCRIPTION;
import static com.amtsybex.fieldreach.services.messages.request.AuthenticationToken.APPLICATION_VND_FIELDSMART_AUTHENTICATIONTOKEN_1_JSON;
import static com.amtsybex.fieldreach.services.messages.request.AuthenticationToken.APPLICATION_VND_FIELDSMART_AUTHENTICATIONTOKEN_1_XML;
import static com.amtsybex.fieldreach.user.utils.UserInviteIdentityToken.INVALID_INVITE_SIGNATURE;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.InvalidUserCodeException;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.HPCWGCat;
import com.amtsybex.fieldreach.backend.model.HPCWorkGroups;
import com.amtsybex.fieldreach.backend.model.PersonalViews;
import com.amtsybex.fieldreach.backend.model.ScriptProfiles;
import com.amtsybex.fieldreach.backend.model.SystemParameters;
import com.amtsybex.fieldreach.backend.model.SystemUsers;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.backend.service.SystemParametersService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.exception.InvalidRequestTokenException;
import com.amtsybex.fieldreach.services.endpoint.common.ActivityLogController;
import com.amtsybex.fieldreach.services.endpoint.common.ActivityLogController.ACTIVITYTYPE;
import com.amtsybex.fieldreach.services.endpoint.common.UserController;
import com.amtsybex.fieldreach.services.endpoint.rest.RestUserController;
import com.amtsybex.fieldreach.services.exception.AuthenticationException;
import com.amtsybex.fieldreach.services.exception.ExpiredMobileInvitationException;
import com.amtsybex.fieldreach.services.exception.InvalidWorkgroupException;
import com.amtsybex.fieldreach.services.exception.RevokedMobileInvitationException;
import com.amtsybex.fieldreach.services.exception.SystemUserRevokedException;
import com.amtsybex.fieldreach.services.messages.request.AuthenticationToken;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.messages.response.GetPersonalViewResponse;
import com.amtsybex.fieldreach.services.messages.response.UserInfoResponse;
import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.PersonalView;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api(tags = "User & Config")
public class RestUserControllerImpl extends BaseControllerImpl implements RestUserController {

	private static final Logger log = LoggerFactory.getLogger(RestUserControllerImpl.class.getName());

	@Autowired
	private UserService userService;

	@Autowired
	protected SystemParametersService systemParametersService;

	@Autowired
	private ScriptService scriptService;

	@Autowired
	private ActivityLogController activityLogController;
	   
	 
	 @Autowired
	 private UserController userController;

	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	// FDE034 TM 14/08/2015

	@Deprecated
	@GetMapping(value = "/user/{id}/personalViews",
			produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_VIEW_1_JSON, APPLICATION_VND_FIELDSMART_VIEW_1_XML})
	@ApiOperation(value = "Get Personal Views", 
	notes = "Authorised users can create and maintain personalised views through the Fieldreach Field Data Manager application - FDM. " +
			"Personalised views allow users to save complex search definitions on the system which are then stored with a short description to identify them for later use. " +
			"These stored queries can then be then executed at any time from the FDM search screen by selecting the view from the Personalised View drop down and pressing the Search button. " + 
			"This web service end point allows clients to request a list of personal views for a specific user")
	@ResponseBody
	@Override
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include UserNotFoundException, FRInstanceException"),
			@ApiResponse(code = 400, message = "Bad Request - id is empty or no supplied.")
	})
	public ResponseEntity<GetPersonalViewResponse> getPersonalViews(@RequestHeader HttpHeaders httpHeaders,
			@ApiParam(value = USER_CODE_DESCRIPTION) @PathVariable("id") String userCode) {

		if (log.isDebugEnabled())
			log.debug(">>> getPersonalViews userCode=" + Common.CRLFEscapeString(userCode));

		// Initialise Response
		GetPersonalViewResponse pvResponse = new GetPersonalViewResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		pvResponse.setError(errorMessage);

		// Get application identifier
		String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);

		try {

			Utils.debugHeaders(log, httpHeaders);
			
			List<HPCUsers> hpcusrs = userService.findHPCUsersByUserCode(applicationIdentifier, userCode);
			if(hpcusrs == null) {
				throw new FRInstanceException("unknown user");
			}
			
			SystemUsers sysUser = null;
				
			// Find system user account that is associated with the mobile user.
			sysUser = userService.getSystemUser(applicationIdentifier, hpcusrs.get(0).getHpcUsersAuth().getSubject(), hpcusrs.get(0).getHpcUsersAuth().getIssuer());
						

			if(sysUser == null) {
				throw new FRInstanceException("system user was not linked with mobile user");
			}

			// Now search for personal view associated with this user.
			List<PersonalViews> personalViews = this.userService.getPersonalViews(applicationIdentifier, sysUser.getId());

			List<PersonalView> respViews = new ArrayList<PersonalView>();

			for(PersonalViews pv : personalViews) {

				PersonalView respView = new PersonalView();

				respView.setViewId(Integer.toString(pv.getId()));
				respView.setViewName(pv.getViewName());
				respView.setViewDesc(pv.getViewDesc());

				if(pv.getViewDefault() != null)
					respView.setViewDefault(true);

				respViews.add(respView);
			}

			pvResponse.setPersonalViewList(respViews);

	
		} catch (FRInstanceException e) {

			log.error(e.getMessage());

			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());
		} finally {

			pvResponse.setSuccess(errorMessage.getErrorCode() == null);

		}

		log.debug("<<< getPersonalViews");

		return ResponseEntity.ok(pvResponse);
	}

	// End FDE034
	@GetMapping(value = "/user/info", produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_USERINFO_1_JSON, APPLICATION_VND_FIELDSMART_USERINFO_1_XML})
	@ApiOperation(value = "Get User Info", notes = "This web service end point allows clients to request authorised users, FieldSmart user information")
	@ResponseBody
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK - Possible error codes include UserRevokedException, AuthenticationException, FRInstanceException, EXCEPTION(General Exception)"),
	})
	public ResponseEntity<UserInfoResponse> userInfo(HttpServletRequest request,			   
			@RequestHeader(value=Utils.APPCODE_HEADER, required = false) String appCode,
			@RequestHeader(value=Utils.APPVERSION_HEADER, required = false) String appVersion) throws AuthenticationException{

		UserInfoResponse userInfoResponse = new UserInfoResponse();
		ErrorMessage errorMessage = new ErrorMessage();
		userInfoResponse.setError(errorMessage);
		HPCUsers objUser = null;

		String applicationIdentifier = Utils.extractApplicationIdentifier(request);


		Utils.debugHeaders(log, request);

		try {

			objUser = getUserDetailsFromUserPrincipal(applicationIdentifier);

			// Authentication complete, setup response.
			userInfoResponse.setAltRef(objUser.getAltRef());
			userInfoResponse.setDeviceId(objUser.getDeviceid());

			userInfoResponse.setUserClass(objUser.getUserClass());
			userInfoResponse.setUserCode(objUser.getId().getUserCode());
			userInfoResponse.setUserName(objUser.getUserName());

			SystemParameters sysParams = systemParametersService.getSystemParams(applicationIdentifier);
			//FDE044 - MC
			userInfoResponse.setInstance(sysParams.getChrl());

			//29885 - Update Authenticate WebService to return dataSec parameter
			String dataSec = sysParams.getDataSec();
			//null check to ensure that datasec is sent as a empty response even if null in DB
			userInfoResponse.setDataSec(dataSec!=null?dataSec:"");

			//FDE058 - 39772 - MC - add google map key to system parameters
			userInfoResponse.setMapkey(sysParams.getMapKey()!=null?sysParams.getMapKey():"");
			userInfoResponse.setMapkeyBing(sysParams.getMapKeyBing()!=null?sysParams.getMapKeyBing():"");
			

			if (objUser.getId().getWorkgroupCode() != null) 
				setWorkgroupDetails(applicationIdentifier, userInfoResponse, objUser.getId().getWorkgroupCode());

			try {

				SystemUsers sysUser = userService.getSystemUser(applicationIdentifier, objUser.getHpcUsersAuth().getSubject(), objUser.getHpcUsersAuth().getIssuer());

				if (sysUser == null) {

					userInfoResponse.setSystemUser(false);
				}
				else {

					// only check if system user is revoked for the mobile approvals app.
					if (sysUser.getRevoked() != null && sysUser.getRevoked() == 1) {

						throw new SystemUserRevokedException("System user '" + sysUser.getId() +
								"' linked with mobile user '" + objUser.getId().getUserCode() +
								"' is revoked"); }

					userInfoResponse.setSystemUser(true);
				}

			} catch (Exception e) {

				userInfoResponse.setSystemUser(false);
			}

			// Get list of appcodes users workgroup has access to.
			List<String> appCodes = this.userService.getMobileAppCodes(applicationIdentifier,
					objUser.getId().getWorkgroupCode());

			String codes = "";

			for (String code : appCodes)
				codes += code + ",";

			codes = StringUtils.chop(codes);

			userInfoResponse.setAppCodes(codes);

			// End FDE034
			
			activityLogController.recordActivityLog(applicationIdentifier, ACTIVITYTYPE.USERINFO, userInfoResponse.getUserCode(), userInfoResponse.getDeviceId(), appCode, appVersion, null);


		} 
        catch(AuthenticationException ex) {
            log.warn(ex.getMessage());
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), ex);
        } 
		catch (FRInstanceException e) {

			log.error("User " + Common.CRLFEscapeString(objUser.getId().getUserCode()) + " Exception in authenticateUser: " + Common.CRLFEscapeString(e.getMessage()));
			errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
			errorMessage.setErrorDescription(e.getMessage());

		} catch (Exception ex) {

			log.error("User " + Common.CRLFEscapeString(objUser.getId().getUserCode()) + " Exception in authenticateUser " + Common.CRLFEscapeString(ex.getMessage()));
			errorMessage.setErrorCode(Utils.AUTHENTICATION_EXCEPTION);
			errorMessage.setErrorDescription(Utils.AUTHENTICATION_EXCEPTION_DESCRIPTION);

		} finally {

			userInfoResponse.setSuccess(errorMessage.getErrorCode() == null);
		}

		log.debug("<<< userInfo");

		return ResponseEntity.ok(userInfoResponse);
	}	


	private void setWorkgroupDetails(String applicationidentifier, UserInfoResponse userInfoResponse,
			String workgroupCode) throws FRInstanceException {

		if (log.isDebugEnabled()) {

			log.debug(">>> setWorkgroupDetails applicationidentifier=" + Common.CRLFEscapeString(applicationidentifier) + " authresponse=XXX");
		}

		HPCWorkGroups objWorkGroup = userService.findHPCWorkGroup(applicationidentifier, workgroupCode);

		if (objWorkGroup != null) {

			// Workgroup information
			userInfoResponse.setWorkGroupCode(workgroupCode);
			userInfoResponse.setWorkGroupDesc(objWorkGroup.getWorkgroupDesc());
			userInfoResponse.setWgClassA(objWorkGroup.getWgClassA());
			userInfoResponse.setWgClassB(objWorkGroup.getWgClassB());

			if (objWorkGroup.getWgCatId() != null) {

				// Work Category information
				HPCWGCat objWorkCategory = userService.findHPCWGCat(applicationidentifier,
						objWorkGroup.getWgCatIdInt());

				if (objWorkCategory != null)
					userInfoResponse.setWgCatDesc(objWorkCategory.getWgCatDesc());
			}

			if (objWorkGroup.getProfileId() != null) {

				// Profile details
				ScriptProfiles objProfile = scriptService.findScriptProfile(applicationidentifier,
						objWorkGroup.getProfileId());

				if (objProfile != null)
					userInfoResponse.setProfileName(objProfile.getProfileName());
			}
		}

		log.debug("<<< setWorkgroupDetails");
	}
	
    @PutMapping(value = "/user/authenticated", 
                                               produces = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_VND_FIELDSMART_CALL_1_JSON, APPLICATION_VND_FIELDSMART_CALL_1_XML },
                                               consumes = { APPLICATION_JSON, APPLICATION_XML,  APPLICATION_VND_FIELDSMART_AUTHENTICATIONTOKEN_1_JSON, APPLICATION_VND_FIELDSMART_AUTHENTICATIONTOKEN_1_XML})
    @Override
    @ApiOperation(value = "Authenticate/create user", notes = "This web service end point allow mobile users to register themselves as a FieldSmart mobile user or authenticate if user is already exists using invite and identity tokens.")
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK - Possible error codes include InvalidWorkgroupException, UserRevokedException, InvalidUserCode, FRInstanceException, RevokedMobileInvitationException, ExpiredMobileInvitationException"),
            @ApiResponse(code = 401, message = "Unauthorized - invalid invite or identity token."),
            @ApiResponse(code = 400, message = "Bad Request - Invalid invite token signature.")})
    public ResponseEntity<CallResponse> authenticateUser(HttpServletRequest request,
            @RequestHeader HttpHeaders httpHeaders,
            @RequestHeader(value=Utils.APPCODE_HEADER, required = false) String appCode,
            @RequestBody AuthenticationToken tokenDetails)
    {

        CallResponse resp = new CallResponse();
        ErrorMessage errorMessage = new ErrorMessage();
        resp.setError(errorMessage);

        String applicationIdentifier = Utils.extractApplicationIdentifier(httpHeaders);
        try {
            
            boolean isUserExists = true;
            HPCUsers user = null;
            try {
                
                user = this.getUserDetailsFromUserPrincipal(applicationIdentifier);
            } catch (AuthenticationException e1) {
                
                isUserExists = false;
            }
            
            if (!isUserExists) {
                
                try {
                 
                    HPCUsers hpcUsers = userController.addHPCUser(applicationIdentifier, tokenDetails );
                
                    activityLogController.recordActivityLog(applicationIdentifier, ACTIVITYTYPE.MOBILEUSERREG,
                            hpcUsers.getId().getUserCode(), null, appCode,
                            "New Mobile User " + hpcUsers.getId().getUserCode() + " Registered", null);

                } catch (InvalidWorkgroupException iwgex) {
                    
                    log.error(iwgex.getMessage());
                    errorMessage.setErrorCode(Utils.INVALID_WORKGROUP_EXCEPTION);
                    errorMessage.setErrorDescription(iwgex.getMessage());
                } catch (InvalidUserCodeException e) {
                    
                    log.error(e.getMessage());
                    errorMessage.setErrorCode(Utils.INVALID_USER_CODE);
                    errorMessage.setErrorDescription(e.getMessage());
                } catch (InvalidRequestTokenException e1) {
                    
                    log.error("Error in processing invite token. message {}", e1.getMessage());
                    if(e1.getMessage().equals(INVALID_INVITE_SIGNATURE)) {
                      
                      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e1.getLocalizedMessage(), e1);
                    }
                    
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
                } catch (RevokedMobileInvitationException e) {
                  
                  log.error(e.getMessage());
                  errorMessage.setErrorCode(Utils.REVOKED_MOBILE_INVITATION_EXCEPTION);
                  errorMessage.setErrorDescription(e.getMessage());
                } catch (ExpiredMobileInvitationException e) {
                  
                  log.error(e.getMessage());
                  errorMessage.setErrorCode(Utils.EXPIRED_MOBILE_INVITATION_EXCEPTION);
                  errorMessage.setErrorDescription(e.getMessage());
                } 

            } else {
                
                if (log.isDebugEnabled()) {
                    
                    log.debug(">>> authenticateUser user already exists userCode  =" + user.getId().getUserCode());
                }
                if (user.getRevokedBool()) {
                    
                    if (log.isDebugEnabled()) {
                        
                        log.debug(
                                ">>> authenticateUser user already exists but revoked  =" + user.getId().getUserCode());
                    }
                    errorMessage.setErrorCode(Utils.USER_REVOKED);
                    errorMessage.setErrorDescription(AUTHENTICATING_USER_REVOKED);
                } else {
                    
                    activityLogController.recordActivityLog(applicationIdentifier, ACTIVITYTYPE.MOBILELOGIN,
                            user.getId().getUserCode(), null, appCode,
                            "Mobile User " + user.getId().getUserCode() + " Login", null);
                }
            }

        } catch (FRInstanceException e) {
            
            log.error(e.getMessage());
            errorMessage.setErrorCode(Utils.FR_INSTANCE_EXCEPTION);
            errorMessage.setErrorDescription(e.getMessage());
        }
        
        resp.setSuccess(errorMessage.getErrorCode() == null);

        log.debug("<<< authenticateUser");
        return ResponseEntity.ok(resp);

    }
}
