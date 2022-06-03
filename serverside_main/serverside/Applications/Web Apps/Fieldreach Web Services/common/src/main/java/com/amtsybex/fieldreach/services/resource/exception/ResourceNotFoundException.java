/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.resource.exception;

public class ResourceNotFoundException extends Exception {

	private static final long serialVersionUID = -396978774000140893L;

	
	public ResourceNotFoundException(String string)
	{
		super(string);
	}
	
	public ResourceNotFoundException()
	{
		super();
	}
	
}
