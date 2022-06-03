/**
 * Author:  T Murray
 * Date:    04/05/2016
 * Project: FDP1170
 * 
 * Copyright AMT-Sybex 2016
 */
package com.amtsybex.fieldreach.services.test;

public interface BaseTestController {

	/**
	 * Generate a list of all the Unit Test Classes.
	 * 
	 * @param title
	 * Tite to display on test page
	 * 
	 * @return HTML that shows a list of all the test classes that can be
	 *         executed.
	 */
	public String displayTestClasses(String title);

	/**
	 * Method to execute the tests contained by the test class specified.
	 * 
	 * @param className
	 *            Name of the Test Class to execute the tests of.
	 * 
	 * @return HTML summarising the results of the test run.
	 */
	public String execute(String className);

	/**
	 * Check test values are as expected so that when the tests run they are
	 * being carried out against the expected data.
	 * 
	 * @param results
	 *            A String buffer containing any HTML output generated before
	 *            running the bootstrap, and that any output produced by the
	 *            bootstrap will be written to.
	 * 
	 * @param attemptFix
	 *            If set to true, the bootstrap will attempt to fix any
	 *            incorrect test data where possible.
	 * 
	 * @return True if the test data is as expected. False if test data is not
	 *         as expected.
	 * 
	 *         HTML detailing reasons for failure will be written to the
	 *         StringBuffer passed in to the results parameter.
	 */
	public boolean bootstrapOK(StringBuffer results, boolean attemptFix);

	/**
	 * Method to get the name of the properties file being used to Execute the
	 * JUnit tests
	 * 
	 * @return Name of the properties file being used to Execute the JUnit
	 *         tests.
	 */
	public String getTestPropertiesFile();
	
}
