/**
 * Author:  T Goodwin
 * Date:    31/01/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.resource.exception;

public class ResourceTypeNotFoundException extends Exception {

	private static final long serialVersionUID = 4868800937361118391L;

	
	public ResourceTypeNotFoundException(String string)
	{
		super(string);
	}
	
	public ResourceTypeNotFoundException()
	{
		super();
	}
}
