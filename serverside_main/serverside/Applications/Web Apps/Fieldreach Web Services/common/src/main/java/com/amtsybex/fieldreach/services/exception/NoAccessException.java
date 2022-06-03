package com.amtsybex.fieldreach.services.exception;

public class NoAccessException extends Exception {

	private static final long serialVersionUID = -1L;

	public NoAccessException()
	{
		super("No Access");
	}
	
	public NoAccessException(String message)
	{
		super(message);
	}


}
