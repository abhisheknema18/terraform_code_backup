/**
 * Author:  T Murray
 * Date:    23/09/2015
 * Project: FDE034
 * 
 * Copyright AMT-Sybex 2015
 */
package com.amtsybex.fieldreach.client.rest;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory; 
import org.slf4j.Logger;  
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.XmlMappingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.amtsybex.fieldreach.services.messages.response.GetPersonalViewResponse;
import com.amtsybex.fieldreach.services.messages.types.PersonalView;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.* ;
import static org.junit.Assert.* ;

public class TestGetPersonalViews extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestGetPersonalViews.class.getName());

	@Test
	@TestDescription(desc="No date header and valid authorizaton token.")
	@TestLabel(label="Reference: 3701")
	public void test_1() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", personalViewValidUser);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getPersonalViews(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Invalid header and valid authorizaton token.")
	@TestLabel(label="Reference: 3702")
	public void test_2() {

		String dateHeader = "invalid date";
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", personalViewValidUser);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getPersonalViews(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	@Test
	@TestDescription(desc="Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label="Reference: 3703")
	public void test_3() {

		String dateHeader = getNonRFC2616Date();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", personalViewValidUser);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getPersonalViews(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes in front of the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 3704")
	public void test_4() {

		String dateHeader = getDateAfter(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", personalViewValidUser);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getPersonalViews(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes behind the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 3705")
	public void test_5() {

		String dateHeader = getDateBefore(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", personalViewValidUser);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getPersonalViews(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Valid date header and no authorization token.")
	@TestLabel(label="Reference: 3706")
	public void test_6() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", personalViewValidUser);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ":"
				+ null);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getPersonalViews(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Valid date header and malformed authorization token.")
	@TestLabel(label="Reference: 3707")
	public void test_7() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", personalViewValidUser);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode + ":"
				+ authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getPersonalViews(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token.<br>" +
						  "Invalid user specified in the authorization header.")
	@TestLabel(label="Reference: 3708")
	public void test_8() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", personalViewValidUser);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getPersonalViews(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "signed with incorrect password.")
	@TestLabel(label="Reference: 3709")
	public void test_9() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", personalViewValidUser);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getPersonalViews(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Request personal views for mobile user not linked to a system user account."
			+ "Verify request is successful and no personal views are returned.")
	@TestLabel(label="Reference: 3710")
	public void test_10() {
		
		GetPersonalViewResponse response = null;
		
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", personalViewNonSysUser);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getPersonalViews(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("Request failed.", 
				response.isSuccess());
		
		assertTrue("Unexpected exception returned:" + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("Personal views were returned: " + response.getPersonalViewList().size() , 
				response.getPersonalViewList().isEmpty());
	}
	
	@Test
	@TestDescription(desc="Request personal views for mobile user linked to a system user account. User "
			+ "has no personal views defined. Verify request is successful and no personal views are "
			+ "returned.")
	@TestLabel(label="Reference: 3711")
	public void test_11() {
		
		GetPersonalViewResponse response = null;
		
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", personalViewValidUserNoViews);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getPersonalViews(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("Request failed.", 
				response.isSuccess());
		
		assertTrue("Unexpected exception returned:" + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("Personal views were returned: " + response.getPersonalViewList().size() , 
				response.getPersonalViewList().isEmpty());
	}
	
	@Test
	@TestDescription(desc="Request personal views for mobile user linked to a system user account. User "
			+ "has personal views defined. Verify request is successful and expected number of personal "
			+ "views are returned.")
	@TestLabel(label="Reference: 3712")
	public void test_12() {
		
		GetPersonalViewResponse response = null;
		
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", personalViewValidUser);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getPersonalViews(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("Request failed.", 
				response.isSuccess());
		
		assertTrue("Unexpected exception returned:" + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("Unexpected number of personal views returned. Expected:" + personalViewValidUserViewCount +
				"<br>Received:" + response.getPersonalViewList().size(), 
				response.getPersonalViewList().size() == personalViewValidUserViewCount);
	}
	
	
	public GetPersonalViewResponse getPersonalViews(Map<String, String> headersVariables,
			HttpHeaders requestHeaders, HttpStatus expectedStatusCode) {
		
		final String wsURL = baseURL + "/user/{id}/personalViews";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		GetPersonalViewResponse requestResponse = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try {

			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET, requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();

			assertTrue("Result is null", results != null);

			// Parse XML received from the response into an 
			// GetPersonalViewResponse object
			try {
				
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("GetPersonalViewResponse", GetPersonalViewResponse.class);
				xstream.alias("PersonalView", PersonalView.class);
				requestResponse = (GetPersonalViewResponse) xstream
						.fromXML(results);

				log.debug("Success : " + requestResponse.isSuccess());
			} 
			catch (XmlMappingException e) {
				
				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
			}
		} 
		catch (HttpClientErrorException he) {
			
			//Check to see if the HHTP status code is what we expect
			if (he.getStatusCode() == expectedStatusCode)
				log.debug(expectedStatusCode + " status code returned as expected.");

			else {
				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
		}
		
		return requestResponse;
	}

}
