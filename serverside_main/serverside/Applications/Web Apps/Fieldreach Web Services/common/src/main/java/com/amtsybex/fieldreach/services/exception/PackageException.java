package com.amtsybex.fieldreach.services.exception;

public class PackageException extends Exception{

	private static final long serialVersionUID = 9082926373697518746L;

	public PackageException() {
		super("Package Exception");
	}
	
	public PackageException(String message)
	{
		super(message);
	}
}
