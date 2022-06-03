/**
 * Author:  T Murray
 * Date:    11/04/2013
 * Project: FDE020
 *
 * Copyright AMT-Sybex 2013
 */
package com.amtsybex.fieldreach.services.download.exception;

public class InvalidDownloadIdentifierException extends Exception 
{
	
	private static final long serialVersionUID = 7105023033769176651L;

	public InvalidDownloadIdentifierException(String string)
	{
		super(string);
	}
	
	public InvalidDownloadIdentifierException()
	{
		super();
	}
	
}
