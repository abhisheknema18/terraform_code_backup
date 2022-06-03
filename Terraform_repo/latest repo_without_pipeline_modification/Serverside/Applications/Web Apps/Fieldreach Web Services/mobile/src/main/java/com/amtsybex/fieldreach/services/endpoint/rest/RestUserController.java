/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Amended:
 * FDE029 TM 16/01/2015
 * Removal of /v1 namespace from web service end points 
 * and code re-factoring.
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import com.amtsybex.fieldreach.services.exception.AuthenticationException;
import com.amtsybex.fieldreach.services.messages.request.AuthenticationToken;
import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.messages.response.GetPersonalViewResponse;
import com.amtsybex.fieldreach.services.messages.response.UserInfoResponse;


public interface RestUserController {

	// FDE034 TM 14/08/2015

	/**
	 * 
	 * @param httpHeaders
	 *            Http headers
	 * 
	 * @param userCode
	 *            Mobile user code of user to get personal views for. User must
	 *            be linked to a system user account.
	 * 
	 * @return GetPersonalViewResponse message
	 * 
	 * @
	 */
	ResponseEntity<GetPersonalViewResponse> getPersonalViews(HttpHeaders httpHeaders, String userCode) ;

	// End FDE034
	
	
	/**
	 * 
	 * @param httpHeaders
	 *            Http headers
	 *            
	 * @param userCode
	 *            Mobile UserCode
	 *            
	 * @param appVersion
	 *            Mobile app version
	 *            
	 * @return String userCode
	 * 
	 * @
	 */
	public ResponseEntity<UserInfoResponse> userInfo(HttpServletRequest request, String userCode, String appVersion) throws AuthenticationException;
	
	/**
	 *  Method to authenticate/create new mobile user
	 *  
	 * @param request
	 * 
	 * @param httpHeaders
	 * 
	 * @param appCode
	 * 
	 * @param tokenDetails
	 * 
	 * @return CallerResponse with success or error details
	 */
	public ResponseEntity<CallResponse> authenticateUser(HttpServletRequest request, HttpHeaders httpHeaders, String appCode, AuthenticationToken tokenDetails);
}
