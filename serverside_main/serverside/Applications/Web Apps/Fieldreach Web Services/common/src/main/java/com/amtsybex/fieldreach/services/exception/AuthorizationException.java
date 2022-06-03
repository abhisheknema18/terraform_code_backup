/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.exception;

public class AuthorizationException extends Exception {

	public AuthorizationException(String string) {
		super(string);
	}

	public AuthorizationException() {
		super();
	}

	private static final long serialVersionUID = 7354459460769796539L;
}
