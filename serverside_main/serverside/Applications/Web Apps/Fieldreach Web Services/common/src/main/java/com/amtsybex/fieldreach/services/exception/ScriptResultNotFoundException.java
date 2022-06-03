/**
 * Author:  T Murray
 * Date:    28/02/2013
 * Project: FDE020
 * 
 * Copyright AMT-Sybex 2013
 */

package com.amtsybex.fieldreach.services.exception;

public class ScriptResultNotFoundException  extends Exception 
{
	
	private static final long serialVersionUID = -6619597687628851104L;

	
	public ScriptResultNotFoundException(String string) 
	{
		super(string);
	}

	public ScriptResultNotFoundException() 
	{
		super();
	}
	
}
