/**
 * Author:  T Murray
 * Date:    28/02/2013
 * Project: FDE020
 * 
 * Copyright AMT-Sybex 2013
 */

package com.amtsybex.fieldreach.services.exception;

public class UnknownTransactionTypeException  extends Exception 
{

	private static final long serialVersionUID = -2876622617211974903L;

	
	public UnknownTransactionTypeException(String string) 
	{
		super(string);
	}

	public UnknownTransactionTypeException() 
	{
		super();
	}
	
}
