/**
 * Author:  T Murray
 * Date:    25/09/2015
 * Project: FDE034
 * 
 * Copyright AMT-Sybex 2015
 * 
 */
package com.amtsybex.fieldreach.services.exception;

public class SystemUserRevokedException extends Exception {

	private static final long serialVersionUID = 1345392608886704643L;

	public SystemUserRevokedException() {
		
	}
	
	public SystemUserRevokedException(String message) {
		super(message);
	}

}
