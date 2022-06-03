/**
 * Author:  T Goodwin
 * Date:    05/12/2012
 * Project: FDE022
 * 
 * Copyright AMT-Sybex 2013
 */
package com.amtsybex.fieldreach.services.exception;

public class AuthenticationException extends Exception {

	private static final long serialVersionUID = -4263710730525102865L;

	public AuthenticationException(Exception cause) {
		super(cause);
	}
	
	public AuthenticationException(String string) {
		super(string);
	}

	public AuthenticationException() {
		super();
	}


}
