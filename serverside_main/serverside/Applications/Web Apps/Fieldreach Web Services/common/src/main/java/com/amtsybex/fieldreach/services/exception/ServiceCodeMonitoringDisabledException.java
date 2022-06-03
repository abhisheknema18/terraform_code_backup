package com.amtsybex.fieldreach.services.exception;

/**
 * FDE053 - MC - add dsystem monitor web service
 * @author CroninM
 *
 */
public class ServiceCodeMonitoringDisabledException extends Exception {

	private static final long serialVersionUID = -5449841628555192053L;

	public ServiceCodeMonitoringDisabledException(String string) {
		
		super(string);
	}

	public ServiceCodeMonitoringDisabledException() {
		
		super();
	}
}
