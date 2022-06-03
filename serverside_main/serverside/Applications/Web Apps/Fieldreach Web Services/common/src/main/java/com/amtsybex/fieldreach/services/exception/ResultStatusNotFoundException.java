/**
 * Author:  T Murray
 * Date:    04/09/2015
 * Project: FDE034
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.services.exception;

public class ResultStatusNotFoundException extends Exception {
	
	private static final long serialVersionUID = -7452561236599139634L;

	public ResultStatusNotFoundException(String string) {
		
		super(string);
	}

	public ResultStatusNotFoundException() {
		
		super();
	}
}
