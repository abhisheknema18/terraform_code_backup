/**
 * Author:  T Murray
 * Date:    13/01/2014
 * Project: FDE022
 * 
 * Copyright AMT-Sybex 2014
 */
package com.amtsybex.fieldreach.client.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;

import org.slf4j.LoggerFactory; 
import org.slf4j.Logger;  

import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.amtsybex.fieldreach.services.messages.request.VALIDATIONTYPEREQUEST;
import com.amtsybex.fieldreach.services.messages.response.ValidationTypeResponse;
import com.amtsybex.fieldreach.services.messages.types.Validation;
import com.amtsybex.fieldreach.services.messages.types.ValidationProperty;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class TestValidationList extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestValidationList.class.getName());

	private boolean corruptRequest = false;
	private boolean blankRequest = false;

	private List<String> lstValidationTypes = null;
	
	@Test
	@TestDescription(desc = "No Date header and valid Authorization header.")
	@TestLabel(label = "Reference: 1701")
	public void test_1() {

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));

		buildValidationTypes();

		this.callValidationTypeRequest(lstValidationTypes, "",
				headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Invalid Date header and valid Authorization header")
	@TestLabel(label = "Reference: 1702")
	public void test_2() {

		String dateHeader = "hhfdss";

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		buildValidationTypes();

		this.callValidationTypeRequest(lstValidationTypes, "",
				headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Date header not in RFC 2616 format and valid Authorization header.")
	@TestLabel(label = "Reference: 1703")
	public void test_3() {

		String dateHeader = getNonRFC2616Date();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		buildValidationTypes();

		this.callValidationTypeRequest(lstValidationTypes, "",
				headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Date header more than 15 mins ahead of server time and valid Authorization header.")
	@TestLabel(label = "Reference: 1704")
	public void test_4() {

		String dateHeader = getDateAfter(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		buildValidationTypes();

		this.callValidationTypeRequest(lstValidationTypes, "",
				headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Date header more than 15 minutes behind server time and valid Authorization header.")
	@TestLabel(label = "Reference: 1705")
	public void test_5() {

		String dateHeader = getDateBefore(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		buildValidationTypes();

		this.callValidationTypeRequest(lstValidationTypes, "",
				headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Valid date header and no Authorization header.")
	@TestLabel(label = "Reference: 1706")
	public void test_6() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		buildValidationTypes();

		this.callValidationTypeRequest(lstValidationTypes, "",
				headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Valid date header and malformed Authorization header.")
	@TestLabel(label = "Reference: 1707")
	public void test_7() {
		
		String dateHeader = getDateHeader();
		String authToken = getAuthToken(workissuedUserPassword, dateHeader);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-I_AM_MALFORMED"
				+ workissuedUserCode + ":" + authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		buildValidationTypes();

		this.callValidationTypeRequest(lstValidationTypes, "",
				headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Valid date header valid Authorization header using an invalid user in "
			+ "the authorisation header.")
	@TestLabel(label = "Reference: 1708")
	public void test_8() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		buildValidationTypes();

		this.callValidationTypeRequest(lstValidationTypes, "",
				headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Valid date header valid Authorization header signed with incorrect password.")
	@TestLabel(label = "Reference: 1709")
	public void test_9() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		buildValidationTypes();

		this.callValidationTypeRequest(lstValidationTypes, "",
				headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Send Blank Request.Check status code is 400 Bad request")
	@TestLabel(label = "Reference: 1710")
	public void test_10() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		buildValidationTypes();

		blankRequest = true;
		this.callValidationTypeRequest(lstValidationTypes, "",
				headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
	}

	
	@Test
	@TestDescription(desc = "Send Invalid XML Request.Check status code is 400 Bad request")
	@TestLabel(label = "Reference: 1711")
	public void test_11() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		buildValidationTypes();

		corruptRequest = true;
		this.callValidationTypeRequest(lstValidationTypes, "",
				headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
	}

	
	@Test
	@TestDescription(desc = "Send Request with non Existing Types."
			+ "<br/>Check status code is 200 OK."
			+ "<br/>Verify no validation list items returned.")
	@TestLabel(label = "Reference: 1712")
	public void test_12() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		buildNoExistingValidationTypes();

		ValidationTypeResponse response = this.callValidationTypeRequest(
				lstValidationTypes, "", headersVariables, requestHeaders,
				HttpStatus.OK);

		assertTrue("Success for missing validation types is false",
				response.isSuccess());

		assertTrue("Validations returned", response.getValidations() == null);
	}

	
	@Test
	@TestDescription(desc = "Send Request with single validation type."
			+ "<br/>Check status code is 200 OK."
			+ "<br/>Verify only that item returned."
			+ "<br/>Verify properties are in order.")
	@TestLabel(label = "Reference: 1713")
	public void test_13() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		buildSingleValidationTypes();

		ValidationTypeResponse response = this.callValidationTypeRequest(
				lstValidationTypes, "", headersVariables, requestHeaders,
				HttpStatus.OK);

		assertTrue("Success for missing validation types is false",
				response.isSuccess());

		assertTrue("Validations not returned",
				response.getValidations() != null);

		assertTrue("Validations returned too many", response.getValidations()
				.size() == 1);

		String validationType = lstValidationTypes.get(0);

		for (Validation objVal : response.getValidations()) {
			assertTrue("Validation type returned invalid",
					validationType.equals(objVal.getValidationType()));

			int lastWeightScore = Integer.MAX_VALUE;
			String lastName = null;
			
			for (ValidationProperty objProperty : objVal
					.getValidationProperties()) {
				assertTrue("WeightScore order not correct ",
						objProperty.getWeightScore() <= lastWeightScore);
				
				if (objProperty.getWeightScore() == lastWeightScore)
				{
					assertTrue("Name order not correct", lastName == null ||
							objProperty.getName().compareTo(lastName) >= 0);
				}

				lastName = objProperty.getName();
				lastWeightScore = objProperty.getWeightScore();
			}

		}
	}

	
	@Test
	@TestDescription(desc = "Send Request with multiple validation types."
			+ "<br/>Check status code is 200 OK."
			+ "<br/>Verify all items returned."
			+ "<br/>Verify properties are in order.")
	@TestLabel(label = "Reference: 1714")
	public void test_14() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		//
		// Call web service

		buildValidationTypes();

		ValidationTypeResponse response = this.callValidationTypeRequest(
				lstValidationTypes, "", headersVariables, requestHeaders,
				HttpStatus.OK);

		assertTrue("Success for missing validation types is false",
				response.isSuccess());

		assertTrue("Validations not returned",
				response.getValidations() != null);

		int i = 0;
		for (Validation objVal : response.getValidations()) {

			assertTrue("Validations not in order ", objVal.getValidationType()
					.equals(lstValidationTypes.get(i)));


			int lastWeightScore = Integer.MAX_VALUE;
			String lastName = null;
			for (ValidationProperty objProperty : objVal
					.getValidationProperties()) {
				assertTrue("WeightScore order not correct ",
						objProperty.getWeightScore() <= lastWeightScore);
				
				if (objProperty.getWeightScore() == lastWeightScore)
				{
					assertTrue("Name order not correct", lastName == null ||
							objProperty.getName().compareTo(lastName) >= 0);
				}

				lastName = objProperty.getName();
				lastWeightScore = objProperty.getWeightScore();
			}

			i++;
		}
	}

	
	@Test
	@TestDescription(desc = "Send Request with multiple validation types."
			+ "<br/>Check status code is 200 OK."
			+ "<br/>Verify items returned returned."
			+ "<br/>Verify properties are in order."
			+ "<br/>Calculate checksum and resend request."
			+ "<br/>Verify checksum match is set on.")
	@TestLabel(label = "Reference: 1715")
	public void test_15() {
		
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		//
		// Call web service

		buildValidationTypes();

		ValidationTypeResponse response = this.callValidationTypeRequest(
				lstValidationTypes, "", headersVariables, requestHeaders,
				HttpStatus.OK);

		assertTrue("Success for missing validation types is false",
				response.isSuccess());

		//
		//
		assertTrue("Validations not returned",
				response.getValidations() != null);

		XStreamMarshaller restApiMarshaller = new XStreamMarshaller();
		Class<?>[] classes = {VALIDATIONTYPEREQUEST.class, Validation.class, ValidationProperty.class}; 

		restApiMarshaller.setAnnotatedClasses(classes);

		StringBuilder strBuilder = new StringBuilder();
		int i = 0;
		for (Validation objVal : response.getValidations()) {
			//
			// Check returned in order..
			assertTrue("Validations not in order ", objVal.getValidationType()
					.equals(lstValidationTypes.get(i)));

			StringWriter ioStream = new StringWriter();

			try {
				restApiMarshaller.marshal(objVal, new StreamResult(ioStream));
				strBuilder.append(ioStream.getBuffer().toString());

			} catch (XmlMappingException e) {
				log.error("XmlMappingException in /validationType/list "
						+ e.getMessage());
				fail("XMLMappingException");

			} catch (IOException e) {
				log.error("IOException in /validationType/list "
						+ e.getMessage());
				fail("IOException");
			}

			//
			//
			int lastWeightScore = Integer.MAX_VALUE;
			String lastName = null;
			for (ValidationProperty objProperty : objVal
					.getValidationProperties()) {
				assertTrue("WeightScore order not correct ",
						objProperty.getWeightScore() <= lastWeightScore);
				
				if (objProperty.getWeightScore() == lastWeightScore)
				{
					assertTrue("Name order not correct", lastName == null ||
							objProperty.getName().compareTo(lastName) >= 0);
				}

				lastName = objProperty.getName();
				lastWeightScore = objProperty.getWeightScore();
			}

			i++;
		}

		//
		// Validation entries in string.. now calc checksum
		String checksum = Common.generateMD5Checksum(
				strBuilder.toString().getBytes());

		//
		// Now resend with checksum..
		response = this.callValidationTypeRequest(lstValidationTypes, checksum,
				headersVariables, requestHeaders, HttpStatus.OK);

		assertTrue("Success for missing validation types is false",
				response.isSuccess());

		assertTrue("Checksum match is off", response.getChecksumMatch() == 1);

		//
		//
		assertTrue("Validations returned", response.getValidations() == null);

	}
	
	@Test
	@TestDescription(desc = "Send Request with multiple validation types."
			+ "<br/>Check status code is 200 OK."
			+ "<br/>Verify items returned returned."
			+ "<br/>Verify properties are in order."
			+ "<br/>Calculate checksum and resend request with incorrect checksum."
			+ "<br/>Verify checksum match is set off.")
	@TestLabel(label = "Reference: 1716")
	public void test_16() {

		try {
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		//
		// Call web service

		buildValidationTypes();

		ValidationTypeResponse response = this.callValidationTypeRequest(
				lstValidationTypes, "", headersVariables, requestHeaders,
				HttpStatus.OK);

		assertTrue("Success for missing validation types is false",
				response.isSuccess());
		
		//
		//
		assertTrue("Validations not returned",
				response.getValidations() != null);

		XStreamMarshaller restApiMarshaller = new XStreamMarshaller();
		
		Class<?>[] classes = {VALIDATIONTYPEREQUEST.class, Validation.class, ValidationProperty.class}; 

		restApiMarshaller.setAnnotatedClasses(classes);

		StringBuilder strBuilder = new StringBuilder();
		int i = 0;
		for (Validation objVal : response.getValidations()) {
			log.debug("Validation " + objVal.getValidationType());

			//
			// Check returned in order..
			assertTrue("Validations not in order ", objVal.getValidationType()
					.equals(lstValidationTypes.get(i)));

			StringWriter ioStream = new StringWriter();

			try {
				restApiMarshaller.marshal(objVal, new StreamResult(ioStream));
				strBuilder.append(ioStream.getBuffer().toString());

			} catch (XmlMappingException e) {
				log.error("XmlMappingException in /validationType/list "
						+ e.getMessage());
				fail("XMLMappingException");

			} catch (IOException e) {
				log.error("IOException in /validationType/list "
						+ e.getMessage());
				fail("IOException");
			} catch (Exception ie)
			{
				log.error("Exception in /validationType/list "
						+ ie.getMessage());
				fail("Exception");
			}

			//
			//
			int lastWeightScore = Integer.MAX_VALUE;
			String lastName = null;
			for (ValidationProperty objProperty : objVal
					.getValidationProperties()) {
				
				log.debug("Weight " + objProperty.getWeightScore() + " Name:" +
						objProperty.getName() + " last weight:" + lastWeightScore +
						" name:" + lastName);
				assertTrue("WeightScore order not correct ",
						objProperty.getWeightScore() <= lastWeightScore);
				
				if (objProperty.getWeightScore() == lastWeightScore)
				{
					assertTrue("Name order not correct", lastName == null ||
							objProperty.getName().compareTo(lastName) >= 0);
				}
				
				lastName = objProperty.getName();
				lastWeightScore = objProperty.getWeightScore();
			}

			i++;
		}

		//
		// Validation entries in string.. now calc checksum
		String checksum = Common.generateMD5Checksum(
				strBuilder.toString().getBytes());

		//
		// Now resend with checksum..
		response = this.callValidationTypeRequest(lstValidationTypes, checksum
				+ "XXX", headersVariables, requestHeaders, HttpStatus.OK);

		assertTrue("Success for missing validation types is false",
				response.isSuccess());

		assertTrue("Checksum match is on", response.getChecksumMatch() == 0);

		//
		//
		assertTrue("Validations returned", response.getValidations() != null);
		} catch (Exception ex)
		{
			log.error("Error in test case" + Utils.getStackTrace(ex) );
			fail("Exception in test case " + ex.getMessage());
		}

	}
	
	
	@Test
	@TestDescription(desc = "Valid date header valid Authorization header for Integration Webservice")
	@TestLabel(label = "Reference: 1717")
	public void test_17() {
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", unrevokedUserCode);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", this.getIWSAuthorisationHeaderValue(workissuedUserCode,workissuedUserPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		buildValidationTypes();

		ValidationTypeResponse response = this.callValidationTypeRequest(lstValidationTypes, "",
				headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("Success for missing validation types is false",
				response.isSuccess());
	}

	
	public ValidationTypeResponse callValidationTypeRequest(
			List<String> validationTypes, String checksum,
			Map<String, String> headersVariables, HttpHeaders requestHeaders,
			HttpStatus expectedStatusCode) {
		
		log.debug(">>> callValidationTypeRequest");

		final String wsURL = baseURL + "validationType/list";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		ValidationTypeResponse requestResponse = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		//
		//
		try {
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			//
			// Build Request body
			String requestBody = "";

			if (!corruptRequest && !blankRequest) {
				requestBody = "<VALIDATIONTYPEREQUEST>";

				requestBody += "<VALIDATIONTYPELIST>";
				for (String valType : validationTypes) {
					requestBody += "<VALIDATIONTYPE>" + valType
							+ "</VALIDATIONTYPE>";
				}
				requestBody += "</VALIDATIONTYPELIST>";
				
				requestBody += "<CHECKSUM>" + checksum + "</CHECKSUM>";

				requestBody += "</VALIDATIONTYPEREQUEST>";
			} else if (corruptRequest) {
				requestBody = "<pd>";
			} else if (blankRequest) {
				requestBody = "";
			}

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			//
			// Make REST call and retrieve the results
			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.POST, requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();

			log.debug("Validation Type List Results : " + results);

			//
			// Parse XML received from the response into a
			// CallResponse object
			try {

                XStream xstream = new XStream(new StaxDriver());
                xstream.autodetectAnnotations(true);
                xstream.alias("VALIDATIONTYPERESPONSE", ValidationTypeResponse.class);	
                requestResponse = (ValidationTypeResponse) xstream.fromXML(results);
				
				log.debug("Validation List Success : "
						+ requestResponse.isSuccess());
			} catch (XmlMappingException e) {
				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
			}
		} catch (HttpClientErrorException he) {
			//
			// Check to see if the HHTP status code is what we expect
			if (he.getStatusCode() == expectedStatusCode) {
				log.debug(expectedStatusCode
						+ " status code returned as expected.");
			} else {
				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
		} finally {
			//
			// Reset flags
			corruptRequest = false;
			blankRequest = false;
		}

		log.debug("<<< callValidationTypeRequest");

		return requestResponse;
	}

	
	private void buildValidationTypes() {
		
		log.debug(">>> buildValidationTypes");
		
		try {
			if (lstValidationTypes != null) {
				lstValidationTypes.clear();
			}
			lstValidationTypes = null;

			if (lstValidationTypes == null) {

				lstValidationTypes = new ArrayList<String>();

				log.debug("Validation types :" + validationTypeList);

				String[] validationTypes = validationTypeList.split(",");

				assertTrue("Validation Type List is empty",
						validationTypes.length > 0);

				for (int idx = 0; idx < validationTypes.length; idx++) {
					lstValidationTypes.add(validationTypes[idx]);
				}
			}
		} catch (Exception ex) {
			log.error("Error " + ex.getMessage());
			fail(ex.getMessage());
		}
		log.debug("<<< buildValidationTypes");
	}

	
	private void buildSingleValidationTypes() {
		if (lstValidationTypes != null) {
			lstValidationTypes.clear();
		}
		lstValidationTypes = null;

		if (lstValidationTypes == null) {

			lstValidationTypes = new ArrayList<String>();

			String[] validationTypes = singlevalidationTypeList.split(",");

			assertTrue("Single Validation Type List is empty",
					validationTypes.length > 0);

			assertTrue("Single Validation Type List is too large",
					validationTypes.length == 1);

			for (int idx = 0; idx < validationTypes.length; idx++) {
				lstValidationTypes.add(validationTypes[idx]);
			}
		}
	}

	
	private void buildNoExistingValidationTypes() {

		if (lstValidationTypes != null) {
			lstValidationTypes.clear();
		}
		lstValidationTypes = null;

		if (lstValidationTypes == null) {

			lstValidationTypes = new ArrayList<String>();

			String[] validationTypes = validationTypeNonExistingList.split(",");

			assertTrue("Validation Type Non Existing List is empty",
					validationTypes.length > 0);

			for (int idx = 0; idx < validationTypes.length; idx++) {
				lstValidationTypes.add(validationTypes[idx]);
			}
		}
	}

}
