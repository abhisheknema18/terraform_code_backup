/**
 * Author:  T Murray
 * Date:    28/02/2013
 * Project: FDE020
 * 
 * Copyright AMT-Sybex 2013
 */

package com.amtsybex.fieldreach.services.exception;

public class TransacationAlreadyProcessedException  extends Exception 
{

	private static final long serialVersionUID = -6709763757288060530L;

	
	public TransacationAlreadyProcessedException(String string) 
	{
		super(string);
	}

	public TransacationAlreadyProcessedException() 
	{
		super();
	}
	
}
