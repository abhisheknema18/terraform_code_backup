/**
 * Author:  T Goodwin
 * Date:    09/02/2012
 * Project: FDE016
 * 
 * Copyright AMT-Sybex 2015
 * 
 * Amended:
 * FDE029 TM 27/01/2015
 * Removal of /v1 namespace from web service end points 
 * and code re-factoring.
 */
package com.amtsybex.fieldreach.services.endpoint.rest;

public interface RestTestController {
	
	/**
	 * Run all internal tests to perform system testing
	 * 
	 * @return HTML based results for viewing test results.
	 */
	public String executeAll();

	/**
	 * Run specific internal test
	 * 
	 * @return HTML based results for viewing test results.
	 */
	public String execute(String className);

	/**
	 * Run all internal tests to perform system testing of authentication.
	 * 
	 * @return HTML based results for viewing test results.
	 */
	public String executeAuth();

	/**
	 * Run specific internal test for authentication.
	 * 
	 * @return HTML based results for viewing test results.
	 */
	public String executeAuthTest(String className);
	
}
