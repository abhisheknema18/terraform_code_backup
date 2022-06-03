/**
 * Author:  T Murray
 * Date:    30/09/2015
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

import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.* ;
import static org.junit.Assert.* ;

public class TestUpdateScriptResultStatus extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestUpdateScriptResultStatus.class.getName());
	 
	@Test
	@TestDescription(desc="No date header and valid authorizaton token.")
	@TestLabel(label="Reference: 4101")
	public void test_1() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(nextStatusReturnId));
		headersVariables.put("status", "ASTATUS");
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
				
		updateStatus(headersVariables, requestHeaders,  HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Invalid header and valid authorizaton token.")
	@TestLabel(label="Reference: 4102")
	public void test_2() {

		String dateHeader = "invalid date";
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(nextStatusReturnId));
		headersVariables.put("status", "ASTATUS");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
				
		updateStatus(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	@Test
	@TestDescription(desc="Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label="Reference: 4103")
	public void test_3() {

		String dateHeader = getNonRFC2616Date();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(nextStatusReturnId));
		headersVariables.put("status", "ASTATUS");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		updateStatus(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes in front of the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 4104")
	public void test_4() {

		String dateHeader = getDateAfter(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(nextStatusReturnId));
		headersVariables.put("status", "ASTATUS");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
				
		updateStatus(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes behind the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 4105")
	public void test_5() {

		String dateHeader = getDateBefore(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(nextStatusReturnId));
		headersVariables.put("status", "ASTATUS");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
				
		updateStatus(headersVariables, requestHeaders,  HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Valid date header and no authorization token.")
	@TestLabel(label="Reference: 4106")
	public void test_6() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(nextStatusReturnId));
		headersVariables.put("status", "ASTATUS");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ":"
				+ null);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
				
		updateStatus(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Valid date header and malformed authorization token.")
	@TestLabel(label="Reference: 4107")
	public void test_7() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(nextStatusReturnId));
		headersVariables.put("status", "ASTATUS");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode + ":"
				+ authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
				
		updateStatus(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token.<br>" +
						  "Invalid user specified in the authorization header.")
	@TestLabel(label="Reference: 4108")
	public void test_8() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(nextStatusReturnId));
		headersVariables.put("status", "ASTATUS");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
				
		updateStatus(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "signed with incorrect password.")
	@TestLabel(label="Reference: 4109")
	public void test_9() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(nextStatusReturnId));
		headersVariables.put("status", "ASTATUS");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
				
		updateStatus(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc="Attempt to update status status for a script result that does not exist."
			+ "Ensure that StatusUpdateException is returned.<br><br>"
			+ "returnid = 999999")
	@TestLabel(label="Reference: 4110")
	public void test_10() {
		
		CallResponse response = null;
		
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "999999");
		headersVariables.put("status", "ASTATUS");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		

		response = updateStatus(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("Request was a success.", !response.isSuccess());
		
		assertTrue("No exception returned:" + response.getError().getErrorCode(), 
				response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected exception returned. Expected:" + Utils.RESULT_STATUS_UPDATE_EXCEPTION +
				"<br>Received:" + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.RESULT_STATUS_UPDATE_EXCEPTION));
		
	}

	@Test
	@TestDescription(desc="Attempt to update status for a script result that does exist. Supply a current status"
			+ "that does not exist for the result"
			+ "Ensure that StatusUpdateException is returned.<br><br>"
			+ "returnid = test.nextstatus.returnid status=IDONTEXIST")
	@TestLabel(label="Reference: 4111")
	public void test_11() {
		
		CallResponse response = null;
		
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(nextStatusReturnId));
		headersVariables.put("status", "IDONTEXIST");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		

		response = updateStatus(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("Request was a success.", !response.isSuccess());
		
		assertTrue("No exception returned:" + response.getError().getErrorCode(), 
				response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected exception returned. Expected:" + Utils.RESULT_STATUS_UPDATE_EXCEPTION +
				"<br>Received:" + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.RESULT_STATUS_UPDATE_EXCEPTION));
		
	}
		
	@Test
	@TestDescription(desc="Attempt to update status for a script result that does exist. "
			+ "Supply a non system status Ensure that no errors are returned and the next status values"
			+ "returned are as expected.<br><br>"
			+ "returnid = test.nextstatus.returnid status = test.updatestatus.nonSystemStatus")
	@TestLabel(label="Reference: 4112")
	public void test_12() {
		
		CallResponse response = null;
		
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(nextStatusReturnId));
		headersVariables.put("status", updateNonSysStatus);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		

		response = updateStatus(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("Request was not a success.", response.isSuccess());
		
		assertTrue("Unexpected exception returned:" + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		

	}
	
	@Test
	@TestDescription(desc="Attempt to update status for a script result that does exist. "
			+ "Supply a system status Ensure that no errors are returned and the next status values"
			+ "returned are as expected.<br><br>"
			+ "returnid = test.nextstatus.returnid status = test.updatestatus.SystemStatus")
	@TestLabel(label="Reference: 4113")
	public void test_13() {
		
		CallResponse response = null;
		
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(nextStatusReturnId));
		headersVariables.put("status", updateSysStatus);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		response = updateStatus(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("Request was not a success.", response.isSuccess());
		
		assertTrue("Unexpected exception returned:" + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		

	}
	
	public CallResponse updateStatus(Map<String, String> headersVariables,
			HttpHeaders requestHeaders, HttpStatus expectedStatusCode) {
		
		final String wsURL = baseURL + "/script/result/{id}/status/{status}";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		CallResponse requestResponse = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try {

			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					null, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.PUT, requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();

			assertTrue("Result is null", results != null);

			// Parse XML received from the response into an 
			// CallResponse object
			try {
				
				XStream xstream = new XStream(new StaxDriver());
				xstream.autodetectAnnotations(true);
				xstream.alias("CallResponse", CallResponse.class);
				requestResponse = (CallResponse) xstream
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
