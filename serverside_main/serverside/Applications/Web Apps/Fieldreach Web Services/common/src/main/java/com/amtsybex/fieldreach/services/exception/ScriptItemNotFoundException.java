/**
 * Author:  T Murray
 * Date:    09/09/2015
 * Project: FDE034
 * 
 * Copyright AMT-Sybex 2015
 */

package com.amtsybex.fieldreach.services.exception;

public class ScriptItemNotFoundException extends Exception {
	
	private static final long serialVersionUID = -6619597687628851104L;

	public ScriptItemNotFoundException(String string) {
		
		super(string);
	}

	public ScriptItemNotFoundException() {
		
		super();
	}
	
}
