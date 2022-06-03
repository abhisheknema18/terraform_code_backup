/**
 * Author:  T Murray
 * Date:    28/02/2013
 * Project: FDE020
 * 
 * Copyright AMT-Sybex 2013
 */

package com.amtsybex.fieldreach.services.exception;

public class WorkOrderNotFoundException  extends Exception 
{

	private static final long serialVersionUID = -336617557064708744L;

	
	public WorkOrderNotFoundException(String string) 
	{
		super(string);
	}

	public WorkOrderNotFoundException() 
	{
		super();
	}
	
}
