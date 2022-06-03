/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2012
 * 
 * Author T Goodwin
 * Date : 31/12/2013
 * Project FDE022
 * Add in constructors so specific message failure can be returned.
 * 
 */
package com.amtsybex.fieldreach.services.exception;

public class UserNotFoundException extends AuthenticationException {

	private static final long serialVersionUID = 7965734366742656563L;
	
	//
	// FDE022 Start
	// 31/12/2013
	// 
	// Add in constructor so specific message can be passed.
	public UserNotFoundException()
	{
		super("User Not Found");
	}
	
	public UserNotFoundException(String message)
	{
		super(message);
	}
	
	//
	// FDE022 End
	//
}
