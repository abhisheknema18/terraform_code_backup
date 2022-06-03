package com.amtsybex.fieldreach.services.exception;

/**
 * FDE053 - MC - add dsystem monitor web service
 * @author CroninM
 *
 */
public class ServiceCodeNotFoundException  extends Exception {

	private static final long serialVersionUID = -736944604303806192L;

	public ServiceCodeNotFoundException(String string) {
		
		super(string);
	}

	public ServiceCodeNotFoundException() {
		
		super();
	}
}
