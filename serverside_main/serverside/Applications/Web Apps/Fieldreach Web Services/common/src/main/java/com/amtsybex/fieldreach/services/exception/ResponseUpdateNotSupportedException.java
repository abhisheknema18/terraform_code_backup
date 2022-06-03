/**
 * Author:  T Murray
 * Date:    08/09/2015
 * Project: FDE034
 * 
 * Copyright AMT-Sybex 2015
 */

package com.amtsybex.fieldreach.services.exception;

public class ResponseUpdateNotSupportedException extends Exception {

	private static final long serialVersionUID = -2876622617211974903L;

	public ResponseUpdateNotSupportedException(String string) {
		
		super(string);
	}

	public ResponseUpdateNotSupportedException() {
		
		super();
	}
	
}
