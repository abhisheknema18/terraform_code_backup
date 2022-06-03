/**
 * Author:  T Murray
 * Date:    13/01/2014
 * Project: FDE022
 * 
 * Copyright AMT-Sybex 2014
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

import com.amtsybex.fieldreach.services.messages.response.GetScriptResponse;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestGetScriptSource extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestGetScriptSource.class.getName());
	
	@Test
	@TestDescription(desc="No date header and valid authorizaton token.")
	@TestLabel(label="Reference: 1201")
	public void test_1() {

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", validScriptId);

		HttpHeaders requestHeaders = new HttpHeaders();
		
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptSource(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Invalid header and valid authorizaton token.")
	@TestLabel(label="Reference: 1202")
	public void test_2() {

		String dateHeader = "invalid date";
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", validScriptId);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode, unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptSource(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label="Reference: 1203")
	public void test_3() {

		String dateHeader = getNonRFC2616Date();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", validScriptId);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode, unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptSource(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes in front of the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 1204")
	public void test_4() {

		String dateHeader = getDateAfter(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", validScriptId);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptSource(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes behind the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 1205")
	public void test_5() {
		
		String dateHeader = getDateBefore(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", validScriptId);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptSource(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and no authorization token.")
	@TestLabel(label="Reference: 1206")
	public void test_6() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", validScriptId);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ":"
				+ null);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptSource(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and malformed authorization token.")
	@TestLabel(label="Reference: 1207")
	public void test_7() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", validScriptId);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode + ":"
				+ authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptSource(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token.<br>" +
						  "Invalid user specified in the authorization header.")
	@TestLabel(label="Reference: 1208")
	public void test_8() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", validScriptId);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptSource(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	

	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "signed with incorrect password.")
	@TestLabel(label="Reference: 1209")
	public void test_9() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", validScriptId);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptSource(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Attempt to get script source for a script that does not exists.<br>" +
			Utils.FILE_NOT_FOUND +" should be returned.")
	@TestLabel(label="Reference: 1210")
	public void test_10() {
		
		GetScriptResponse response = null;

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", invalidScriptId);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		response = getScriptSource(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("Script was succesfully retrieved.", 
				!response.isSuccess());
		assertTrue("No error returned.", 
				response.getError().getErrorCode() != null);
		assertTrue("Unexpected exception returned.<br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.getError().getErrorCode().equals(Utils.FILE_NOT_FOUND));
	}
	

	@Test
	@TestDescription(desc="retrieve a script and generate an MD5 checksum for the script.<br>" +
						  "Compare this generated checksum with the known checksum for the " +
						  "script and ensure they match.")
	@TestLabel(label="Reference: 1211")
	public void test_11() {
		
		GetScriptResponse response = null;

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", validScriptId);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		response = getScriptSource(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("Script was not succesfully retrieved.<br><br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.isSuccess());
		assertTrue("Script source not populated.", 
				response.getScriptFileSource() != null);
		assertTrue("Checksums do not match.",
				verifyChecksum(validScriptChecksum, response.getScriptFileSource()));
	}
	
	
	@Ignore("Test is ignored as a demonstration")
	@Test
	@TestDescription(desc="Login as authenticated user.<br>" +
						  "Retrieve online script list and parse to retrieve all scripts. ")
	@TestLabel(label="Reference: 1212")
	public void test_12() {

	}
	
	
	@Test
	@TestDescription(desc="retrieve a script and decode. Generate a checksum and compare to the  " +
						  "checksum returned and ensure they match.")
	@TestLabel(label="Reference: 1213")
	public void test_13() {
		
		GetScriptResponse response = null;

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", validScriptId);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		response = getScriptSource(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("Script was not succesfully retrieved.<br><br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.isSuccess());
		
		assertTrue("Script source not populated.", 
				response.getScriptFileSource() != null);
		
		assertTrue("Checksums do not match.",
				verifyChecksum(response.getChecksum(), response.getScriptFileSource()));
	}
	
	
	private GetScriptResponse getScriptSource(Map<String, String> headersVariables,
			HttpHeaders requestHeaders, HttpStatus expectedStatusCode) {
		
		final String wsURL = baseURL + "script/{id}";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();
		
		StringBuffer restCall;
		GetScriptResponse response = null;
		
		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try {

			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET,requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();
			
			log.debug("get script results : " + results);
			assertTrue("Result is null", results != null);
			
			
			// Parse XML received from the response into an 
			// GetScriptResponse object
			try {
				
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("GetScriptResponse", GetScriptResponse.class);
				response = (GetScriptResponse) xstream
						.fromXML(results);

				log.debug("Success : " + response.isSuccess());
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
		
		return response;
	}

}
