/**
 * Author:  T Goodwin
 * Date:    01/01/2014
 * Project: FDE022
 * 
 * Copyright AMT-Sybex 2014
 * 
 */
package com.amtsybex.fieldreach.services.exception;

public class PasswordChangeException extends Exception {

	private static final long serialVersionUID = -490284500194405649L;

	public PasswordChangeException()
	{
		super("User Not Found");
	}
	
	public PasswordChangeException(String message)
	{
		super(message);
	}

}
