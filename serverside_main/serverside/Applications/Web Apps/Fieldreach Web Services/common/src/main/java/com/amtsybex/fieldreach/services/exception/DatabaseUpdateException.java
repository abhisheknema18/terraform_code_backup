/**
 * Author:  T Murray
 * Date:    28/02/2013
 * Project: FDE020
 * 
 * Copyright AMT-Sybex 2013
 */

package com.amtsybex.fieldreach.services.exception;

public class DatabaseUpdateException  extends Exception 
{
	
	private static final long serialVersionUID = -7734325226632762387L;

	
	public DatabaseUpdateException(String string) 
	{
		super(string);
	}

	public DatabaseUpdateException() 
	{
		super();
	}
	
}
