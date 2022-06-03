/**
 * Author:  T Murray
 * Date:    21/11/2012
 * Project: FDE019
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.services.utils.xml.exception;

public class XMLValidationException extends Exception {

	private static final long serialVersionUID = -9197142399466176570L;
	
	
	public XMLValidationException(String string) 
	{
		super(string);
	}

	
	public XMLValidationException() 
	{
		super();
	}

}
