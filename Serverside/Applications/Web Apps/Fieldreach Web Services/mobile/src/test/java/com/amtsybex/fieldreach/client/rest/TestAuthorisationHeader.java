/**
 * Author:  T Goodwin
 * Date:    28/01/2014
 * Project: FDE022
 * 
 * Copyright AMT-Sybex 2014
 */
package com.amtsybex.fieldreach.client.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

import com.amtsybex.fieldreach.services.messages.response.ScriptListResponse;
import com.amtsybex.fieldreach.services.messages.types.File;
import com.amtsybex.fieldreach.services.messages.types.Script;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.*;

public class TestAuthorisationHeader extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestAuthorisationHeader.class
			.getName());


	@Test
	@TestDescription(desc = "No date header and valid authorizaton token.")
	@TestLabel(label = "Reference: 1101")
	public void test_1() {

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, null));
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Invalid header and valid authorizaton token.")
	@TestLabel(label = "Reference: 1102")
	public void test_2() {

		String dateHeader = "invalid date";

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label = "Reference: 1103")
	public void test_3() {

		String dateHeader = getNonRFC2616Date();
		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Date header more than 15 minutes in front of the "
			+ "server time and valid authorizaton token.")
	@TestLabel(label = "Reference: 1104")
	public void test_4() {

		String dateHeader = getDateAfter(1);

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Date header more than 15 minutes behind the "
			+ "server time and valid authorizaton token.")
	@TestLabel(label = "Reference: 1105")
	public void test_5() {

		String dateHeader = getDateBefore(1);

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						unrevokedPassword, dateHeader));
		
		
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Valid date header and no authorization token.")
	@TestLabel(label = "Reference: 1106")
	public void test_6() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		
		String aesPassword =encodeAESPassword(unrevokedPassword, false);

		String authHeader = "FWS " + unrevokedUserCode + ":" + null + ":"
				+ aesPassword;
		
		requestHeaders.set("Authorization", authHeader);
		
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Valid date header and malformed authorization token.")
	@TestLabel(label = "Reference: 1107")
	public void test_7() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		
		String authToken = getAuthToken(unrevokedPassword, dateHeader);
		String aesPassword = encodeAESPassword(unrevokedPassword, false);

		String authHeader = "FWS-malformed " + unrevokedUserCode + ":" + authToken + ":"
				+ aesPassword;
		
		requestHeaders.set("Authorization", authHeader);
		
		
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}


	@Test
	@TestDescription(desc = "Valid date header and valid authorization token.<br>"
			+ "Invalid user specified in the authorization header.")
	@TestLabel(label = "Reference: 1108")
	public void test_8() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,
						unrevokedPassword, dateHeader));
		
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Valid date header and valid authorization token "
			+ "signed with incorrect password.")
	@TestLabel(label = "Reference: 1109")
	public void test_9() {

		String dateHeader = getDateHeader();
		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,
						invalidPassword, dateHeader));
		
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	/* TODO SPRINGBOOT - invalid test now - commenting to avoid compilation error.
	@Test
	@TestDescription(desc = "Valid date header authorization token signed using FR algorithm.")
	@TestLabel(label = "Reference: 1110")
	public void test_10() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		
		String authToken = getAuthToken(unrevokedPassword, dateHeader, "Fieldreach");
		String aesPassword = userPasswordService
				.encodeAES(unrevokedPassword, false);

		String authHeader = "FWS " + unrevokedUserCode + ":" + authToken + ":"
				+ aesPassword;
		
		requestHeaders.set("Authorization", authHeader);
		
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}*/
	

	@Test
	@TestDescription(desc = "Valid authorisation header for primary when primary and secondary configured")
	@TestLabel(label = "Reference: 1111")
	public void test_11() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,
						 unrevokedPassword, dateHeader));
		
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptList(null, requestHeaders, HttpStatus.OK);
	}
	

	@Test
	@TestDescription(desc = "Valid authorisation header for secondary, where user exists with diff pass in primary")
	@TestLabel(label = "Reference: 1112")
	public void test_12() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(userInPrimaryAndSecondaryCode,
						userInPrimaryAndSecondaryPassword, dateHeader));
		
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc = "Valid authorisation header for secondary, where user does NOT exist in primary")
	@TestLabel(label = "Reference: 1113")
	public void test_13() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(userNotInPrimaryCode,
						userNotInPrimaryPassword, dateHeader));
		
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		getScriptList(null, requestHeaders, HttpStatus.OK);
	}


	public ScriptListResponse getScriptList(String wgCode,
			HttpHeaders requestHeaders, HttpStatus expectedStatusCode) {
		
		final String wsURL = baseURL + "script/list";
		log.debug(" authorisation URL " + wsURL);

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		ScriptListResponse requestResponse = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try {

			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			if(wgCode != null)
				restCall.append("?workgroupCode=" + wgCode);
			
			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET, requestEntity,
					String.class);

			String results = resultsExchange.getBody();


			log.debug("getConfigList results : " + results);
			assertTrue("Result is null", results != null);


			// Parse XML received from the response into an 
			// GetConfigResponse object
			try {
				
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("ScriptListResponse", ScriptListResponse.class);
				xstream.alias("Script", Script.class);
				xstream.alias("File", File.class);
				requestResponse = (ScriptListResponse) xstream
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
