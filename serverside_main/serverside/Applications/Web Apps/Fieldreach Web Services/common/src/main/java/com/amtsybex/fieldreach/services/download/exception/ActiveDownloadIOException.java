/**
 * Author:  T Murray
 * Date:    11/04/2013
 * Project: FDE020
 *
 * Copyright AMT-Sybex 2013
 */
package com.amtsybex.fieldreach.services.download.exception;

public class ActiveDownloadIOException extends Exception 
{
	private static final long serialVersionUID = -8617074401509897283L;

	public ActiveDownloadIOException(String string)
	{
		super(string);
	}
	
	public ActiveDownloadIOException()
	{
		super();
	}
	
}
