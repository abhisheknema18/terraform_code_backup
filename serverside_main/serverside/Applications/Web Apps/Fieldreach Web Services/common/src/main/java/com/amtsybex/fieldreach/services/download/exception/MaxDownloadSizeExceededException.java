/**
 * Author:  T Murray
 * Date:    10/04/2013
 * Project: FDE020
 *
 * Copyright AMT-Sybex 2013
 */
package com.amtsybex.fieldreach.services.download.exception;

public class MaxDownloadSizeExceededException extends Exception 
{

	private static final long serialVersionUID = 5084517346797283282L;

	
	public MaxDownloadSizeExceededException(String string)
	{
		super(string);
	}
	
	public MaxDownloadSizeExceededException()
	{
		super();
	}
	
}
