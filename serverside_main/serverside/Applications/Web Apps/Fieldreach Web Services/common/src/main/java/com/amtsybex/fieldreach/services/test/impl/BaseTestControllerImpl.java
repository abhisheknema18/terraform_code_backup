/**
 * Author:  T Murray
 * Date:    04/05/2016
 * Project: FDP1170
 * 
 * Copyright AMT-Sybex 2016
 */
package com.amtsybex.fieldreach.services.test.impl;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.LoggerFactory; 
import org.slf4j.Logger;  
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.amtsybex.fieldreach.services.test.BaseTestController;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.utils.impl.Common;

public abstract class BaseTestControllerImpl implements BaseTestController {
	
	static Logger log = LoggerFactory.getLogger(BaseTestControllerImpl.class.getName());
	
	protected ArrayList<String> testClasses;
	protected String testPropsFile;
	protected StringBuffer resultsSB;
	
	protected String testLink;
	
	/*-------------------------------------------
	 - Interface Implementations
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * @see com.amtsybex.fieldreach.services.test.BaseTestController#displayTestClasses(java.lang.String)
	 */
	@Override
	public String displayTestClasses(String title) {

		log.debug(">>> displayTestClasses");
		
		log.debug("<<< displayTestClasses");
		
		return this.displayClasses(title);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.amtsybex.fieldreach.services.test.TestController#execute(java.lang.String)
	 */
	@Override
	public String execute(String className) {

		if (log.isDebugEnabled())
			log.debug(">>> execute class=" + Common.CRLFEscapeString(className));

		StringBuffer results = new StringBuffer();

		results.append(getHTMLHeader());

		results.append("<h2>Fieldreach Web Service Unit Tests</h2>");

		if (bootstrapOK(results, true)) {

			if (testClasses != null) {

				JUnitCore runCore = new JUnitCore();

				resultsSB = new StringBuffer();
				runCore.addListener(new TestListener(resultsSB));

				Class<?>[] classes = getClasses();

				for (int i = 0; i < classes.length; i++) {

					Class<?> currentClass = classes[i];

					log.debug("... " + currentClass.getCanonicalName());

					if (currentClass.getCanonicalName().endsWith(className)) {
						
						runTestClass(runCore, currentClass, results);
						break;
					}
				}
			}
		}

		results.append(getHTMLFooter());

		log.debug("<<< execute");

		return results.toString();
	}

	/*-------------------------------------------
	 - Abstract Methods
	 --------------------------------------------*/
	
	/*
	 * (non-Javadoc)
	 * @see com.amtsybex.fieldreach.services.test.TestController#bootstrapOK(java.lang.StringBuffer, boolean)
	 */
	@Override
	public abstract boolean bootstrapOK(StringBuffer results, boolean attemptFix);

	/*-------------------------------------------
	 - Private/Protected Methods
	 --------------------------------------------*/
	
	protected String displayClasses(String title) {
		
		log.debug(">>> displayClasses");

		StringBuffer results = new StringBuffer();

		results.append(getHTMLHeader());

		results.append("<h2>" + title + "</h2>");

		if (bootstrapOK(results, true)) {

			if (testClasses != null)
				generateTestList(results);
		}

		results.append(getHTMLFooter());

		log.debug("<<< displayClasses");

		return results.toString();
	}
	
	/*-------------------------------------------
	 - Protected Methods
	 --------------------------------------------*/

	/**
	 * Get the 'header' of a page that will display test results.
	 * 
	 * @return HTML generated for displaying the 'header' of a page that will
	 *         display test results.
	 */
	protected String getHTMLHeader() {

		return "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>"
				+ "<html>"
				+ "	<head>"
				+ "		<title>FieldReach Web Services Unit Tests</title>"
				+ "		<style type='text/css'>"
				+ "			body {background-color:#ffffff; "
				+ "				  font-family:Verdana, Arial, sans-serif;"
				+ "				  font-size:12px;"
				+ "				  background-color:#FBFBFB;}"
				+ "			table {border-collapse:collapse;}"
				+ "			td {font-family:Verdana, Arial, sans-serif;"
				+ "				font-size:12px;"
				+ "				line-height:18px;"
				+ "				padding:5px;}"
				+ "			p {color:#000000; line-height:18px;}"
				+ "		</style>"
				+ "	</head>" + "	<body>";
	}

	/**
	 * Get the 'footer' of a page that will display test results.
	 * 
	 * @return HTML generated for displaying the 'footer' of a page that will
	 *         display test results.
	 */
	protected String getHTMLFooter() {

		return "	</body>" + "</html>";
	}

	/**
	 * Generate a table containing a list of all the test classes that can be
	 * executed.
	 * 
	 * @param results
	 *            StringBuffer where HTML is appended to. This may already
	 *            contain HTML content.
	 */
	protected void generateTestList(StringBuffer results) {

		try {

			Resource resource = new ClassPathResource(testPropsFile);
			Properties props = PropertiesLoaderUtils.loadProperties(resource);

			String link = props.getProperty("test.baseURL") + this.testLink;

			results.append("<table>");

			Class<?>[] classes = getClasses();

			for (int i = 0; i < classes.length; i++) {

				Class<?> currentClass = classes[i];
				log.debug("Class :" + currentClass.getSimpleName());

				addTestClassLink(results, currentClass.getSimpleName(), link
						+ currentClass.getSimpleName());
			}

			results.append("</table>");

		} catch (IOException ie) {

			addBootStrapError(results,
					"Properties file not found - test.properties");
		}

	}

	/**
	 * Generate a table row containing a reason for an error generated by a
	 * bootstrap method.
	 * 
	 * @param results
	 *            StringBuffer where HTML is appended to. This may already
	 *            contain HTML content.
	 * 
	 * @param failReason
	 *            Failure reason to be written to the row
	 */
	protected void addBootStrapError(StringBuffer results, String failReason) {

		results.append("<tr>" + "		<td style='width:100px; "
				+ "				   font-weight:bold;"
				+ "				   border-top:1px solid #000000;"
				+ "				   border-left: 1px solid #000000;"
				+ "         	   border-right:1px solid #000000;"
				+ "				   background-color:#F5F5F5;"
				+ "				   border-bottom:1px solid #000000;'>"
				+ "			Fail Reason: " + "		</td>"
				+ "		<td style='border-top:1px solid #000000;"
				+ "         	   border-right:1px solid #000000;"
				+ "				   background-color:#FFFFFF;"
				+ "				   border-bottom:1px solid #000000;'>" + failReason
				+ "		</td>" + "</tr>");
	}

	/**
	 * Generate a table row containing a link to execute a the tests in the
	 * specified test class.
	 * 
	 * @param results
	 *            StringBuffer where HTML is appended to. This may already
	 *            contain HTML content.
	 * 
	 * @param className
	 *            Name of the test class to generate a link for.
	 * 
	 * @param link
	 *            URL that will run the tests.
	 */
	protected void addTestClassLink(StringBuffer results, String className,
			String link) {

		results.append("<tr>" + "		<td style='border-top:1px solid #000000;"
				+ "         	   border-right:1px solid #000000;"
				+ "				   background-color:#FFFFFF;"
				+ "				   border-bottom:1px solid #000000;'>" + className
				+ "		</td>" + "		<td style='width:100px; "
				+ "				   font-weight:bold;"
				+ "				   border-top:1px solid #000000;"
				+ "				   border-left: 1px solid #000000;"
				+ "         	   border-right:1px solid #000000;"
				+ "				   background-color:#F5F5F5;"
				+ "				   border-bottom:1px solid #000000;'><a href=\"" + link
				+ "\">Go</a>" + "		</td>" + "</tr>");
	}

	/**
	 * Run the JUnit tests cases contained in the class supplied.
	 * 
	 * @param runCore
	 *            JUnit engine for executing test cases.
	 * 
	 * @param currentClass
	 *            Class that contains JUnit tests cases to execute.
	 * 
	 * @param results
	 *            StringBuffer where HTML is appended to. This may already
	 *            contain HTML content.
	 * 
	 * @return HTML table that contains the results of executing the test cases
	 *         in the supplied class.
	 */
	protected String runTestClass(JUnitCore runCore, Class<?> currentClass,
			StringBuffer results) {

		log.debug(">>> runTestClass class=" + currentClass.getCanonicalName());

		results.append("<div style='border:1px dotted #ABABAB;"
				+ "			padding:20px;" + "			margin-bottom:40px;"
				+ "			width:550px;" + "		    background-color:#FFFFFF;"
				+ "			text-align:left;'");

		results.append("<p style='margin-left:10px;'> <b>Test cases for class:</b><br>"
				+ currentClass.getCanonicalName() + "</p>");

		results.append("<hr style='margin-top:20px;'>");

		results.append("<div style='padding:10px;'>");

		// Run tests
		Result runResult = runCore.run(currentClass);

		results.append("<div style='margin-top:10px;"
				+ "			margin-bottom:10px;'>");
		results.append("	<p> <b>Test Summary</b><br>" + "		Completed Tests: "
				+ runResult.getRunCount() + "		<br>" + "		Failures: "
				+ runResult.getFailureCount() + "	</p>");

		if (runResult.getFailureCount() > 0) {

			results.append("<p>" + "<b>Tests Failed:</b><br>");

			for (Failure runFailure : runResult.getFailures()) {

				String name = runFailure.getDescription()
						.getAnnotation(TestLabel.class).label();

				results.append(name + "<br>");
			}

			results.append("</p>");
		}

		results.append("</div>");
		
		results.append("<hr>");
		results.append("<table>");
		results.append(resultsSB.toString());
		results.append("</table>");
		
		results.append("</div>");
		results.append("</div>");

		log.debug("<<< runTestClass");

		return results.toString();
	}

	protected Class<?>[] getClasses() {

		List<Class<?>> classes = convertToClasses(testClasses);
		return classes.toArray(new Class[classes.size()]);
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	private static List<Class<?>> convertToClasses(final List<String> classFiles) {

		List<Class<?>> classes = new ArrayList<Class<?>>();

		for (String name : classFiles) {

			Class<?> c = null;

			try {

				c = Class.forName(name.trim());
				
			} catch (ClassNotFoundException e) {

				log.error("Class not found >" + name + "<");
				c = null;
			}

			if (c != null) {

				if (!Modifier.isAbstract(c.getModifiers()))
					classes.add(c);
			}
		}

		return classes;
	}

	/*-------------------------------------------
	 - Spring Injection Methods
	 --------------------------------------------*/

	public ArrayList<String> getTestClasses() {

		return testClasses;
	}

	public void setTestClasses(ArrayList<String> testClasses) {

		this.testClasses = testClasses;
	}

	public String getTestPropertiesFile() {

		return testPropsFile;
	}

	public void setTestPropertiesFile(String testPropsFile) {

		this.testPropsFile = testPropsFile;
	}	
	
	public void setTestLink(String testLink) {
		
		this.testLink = testLink;
	}
	
}