/**
 * Author:  T Murray
 * Date:    22/03/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.resource.exception;


public class NotFoundException extends Exception 
{
	
	private static final long serialVersionUID = 5175331370940377412L;

	public NotFoundException(String string) 
	{
		super(string);
	}

	public NotFoundException() 
	{
		super();
	}
}