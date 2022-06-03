/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2012
 * 
 * Amended:
 * FDE026 TM 26/06/2014
 * Multiple Fieldreach Instance Support
 * 
 */
package com.amtsybex.fieldreach.services.endpoint.common;

import java.util.List;
import java.util.Optional;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.InvalidUserCodeException;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.exception.InvalidRequestTokenException;
import com.amtsybex.fieldreach.services.exception.ExpiredMobileInvitationException;
import com.amtsybex.fieldreach.services.exception.InvalidWorkgroupException;
import com.amtsybex.fieldreach.services.exception.RevokedMobileInvitationException;
import com.amtsybex.fieldreach.services.exception.UserNotFoundException;
import com.amtsybex.fieldreach.services.exception.UserRevokedException;
import com.amtsybex.fieldreach.services.messages.request.AuthenticationToken;


public interface UserController {

    public static final int USERNAME_MAX_LENGTH = 40;
    public static final int USERCODE_MAX_LENGTH = 32;
    public static final int TRUNCATED_USERCODE_MAX_LENGTH = 30;
    public static final String USERCODE_MATCH_REGEX = "[^A-Za-z0-9_-]";
    public static final String USERNAME_MATCH_REGEX = "[^ A-Za-z0-9_()-]";
    public static final String AUTHENTICATING_USER_REVOKED =  "Authenticating User has been revoked";
    public static final String IDENTITY_TOKEN_PARSE_ERROR = "Identity token could not be parsed";
    public static final String INVITE_TOKEN_PARSE_ERROR = "Invite token could not be parsed";
    public static final String EMPTY_USERCODE = "Empty userCode";
    public static final String EMPTY_USERNAME = "Empty username";
    public static final String INVITE_TOKEN_INCOMPLETE_PAYLOAD = "Configuration could not be extracted from invite token";
    public static final String INVITE_TOKEN_MISSING_CLAIMS =  "Invite token does not contain claims ";
    public static final String WORKGROUP_DOES_NOT_EXIST = "Target Workgroup does not exist";
    public static final String INACTIVE_WORKGROUP = "Target Workgroup is inactive";
    public static final String USER_CODE_COULD_NOT_GENERATED = "Unable to generate a unique user Code";
    public static final String ISSUER = "iss";
    public static final String SUBJECT = "sub";
    public static final String USER_INVITATION_REVOKED  = "Provided Mobile User Invitation has been revoked";
    public static final String USER_INVITATION_EXPIRED  = "Provided Mobile User Invitation has been expired";

	/**
	 * Get a list of fieldreach users with the specified userCode
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param userCode
	 *            usercode to search for.
	 * 
	 * @return list of user with given userCode
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public List<HPCUsers> getUsersByUserCode(String frInstance, String userCode)
			throws FRInstanceException;

	/**
	 * Revoke a fieldreach user
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param userCode
	 *            usercode of the user to revoke.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public void revokeUser(String frInstance, String userCode)
			throws UserNotFoundException, FRInstanceException;

	/**
	 * Retrieve fieldreach user for given userCode and workgroup
	 * 
	 * @param frInstance
	 *            Fieldreach instance to be used. Supplying null means the
	 *            default instance will be used.
	 * 
	 * @param userCode
	 *            usercode of the user to search for.
	 * 
	 * @param workGroup
	 *            workgroup to search for the user in.
	 * 
	 * @return HPCUsers record for the user if a macth is found. null is
	 *         returned if no match is found.
	 * 
	 * @throws FRInstanceException
	 *             Fieldreach instance name supplied has not been configured. An
	 *             exception occurred accessing the Fieldreach instance.
	 */
	public HPCUsers getUser(String frInstance, String userCode, String workGroup)
			throws FRInstanceException;

	// FDE029 TM 19/01/2015
	// Removed resolveUserDatabaseClass method.
	// End FDE029
	
	/**
	 * Add HpcUser along with hpcUserAuth
	 * 
	 * @param applicationIdentifier
	 * 
	 * @param tokenDetails
	 * 
	 * @return HPCUsers created HPCUsersdetails
	 * 
	 * @throws FRInstanceException
	 * 
	 * @throws InvalidUserCodeException
	 * 
	 * @throws InvalidWorkgroupException
	 * 
	 * @throws InvalidRequestTokenException
	 * @throws InvalidUserCodeException 
	 * @throws RevokedMobileInvitationException 
	 * @throws ExpiredMobileInvitationException 
	 */
    public HPCUsers addHPCUser(String applicationIdentifier, AuthenticationToken tokenDetails)
            throws FRInstanceException, InvalidUserCodeException, InvalidWorkgroupException,
            InvalidRequestTokenException, RevokedMobileInvitationException, ExpiredMobileInvitationException;
    
    
    /**
     * Method to get active user details 
     * 
     * @return Optional<HPCUsers>
     * 
     * @throws UserRevokedException if user is revoked will be thrown  UserRevokedException
     */
    public Optional<HPCUsers> getActiveUserDetails()  throws UserRevokedException;

}
