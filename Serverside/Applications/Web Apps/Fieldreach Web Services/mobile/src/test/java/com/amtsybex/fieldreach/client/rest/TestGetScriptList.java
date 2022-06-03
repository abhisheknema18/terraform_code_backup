/**
 * Author:  T Murray
 * Date:    09/01/2014
 * Project: FDE022
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.client.rest;

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

import org.junit.* ;
import static org.junit.Assert.* ;


public class TestGetScriptList extends CommonBase
{

	static Logger log = LoggerFactory.getLogger(TestGetScriptList.class.getName());


	@Test
	@TestDescription(desc="No date header and valid authorizaton token.")
	@TestLabel(label="Reference: 3001")
	public void test_1() {
		
		HttpHeaders requestHeaders = new HttpHeaders();
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Invalid header and valid authorizaton token.")
	@TestLabel(label="Reference: 3002")
	public void test_2() {

		String dateHeader = "invalid date";

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label="Reference: 3003")
	public void test_3() {

		String dateHeader = getNonRFC2616Date();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes in front of the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 3004")
	public void test_4() {

		String dateHeader = getDateAfter(1);
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes behind the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 3005")
	public void test_5() {

		String dateHeader = getDateBefore(1);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and no authorization token.")
	@TestLabel(label="Reference: 3006")
	public void test_6() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ": : ");
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and malformed authorization token.")
	@TestLabel(label="Reference: 3007")
	public void test_7() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode + ":"
				+ authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token.<br>" +
						  "Invalid user specified in the authorization header.")
	@TestLabel(label="Reference: 3008")
	public void test_8() {

		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	

	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "signed with incorrect password.")
	@TestLabel(label="Reference: 3009")
	public void test_9() {

		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token. Request list of " +
			"scripts supplying a workgroupCode paramater associuated with script profile of type I. " +
			"Check HTTP response is 200 OK. Verify no exceptions have been returned and " +
			"list of scripts matches that expected.")
	@TestLabel(label="Reference: 3010")
	public void test_10() {

		String dateHeader = getDateHeader();
		
		ScriptListResponse response = null;
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getScriptList(scriptList_profileIWG, requestHeaders, HttpStatus.UNAUTHORIZED);
		
		assertTrue("Script list request was not a success.", response.isSuccess());
		
		assertTrue("Unexpected exception returned: " + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("No Scripts found.", 
				response.getScriptList() != null);
		
		
		for (Script script : response.getScriptList()) {
			
			assertTrue("Unexpected config file returned: " + script.getScriptCode(), 
					scriptList_expectedScriptsI.contains(script.getScriptCode()));
		}

	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token. Request list of " +
			"scripts supplying a workgroupCode paramater associuated with script profile of type C. " +
			"Check HTTP response is 200 OK. Verify no exceptions have been returned and " +
			"list of scripts matches that expected.")
	@TestLabel(label="Reference: 3011")
	public void test_11() {

		String dateHeader = getDateHeader();
		
		ScriptListResponse response = null;
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getScriptList(scriptList_profileCWG, requestHeaders, HttpStatus.UNAUTHORIZED);
		
		assertTrue("Script list request was not a success.", response.isSuccess());
		
		assertTrue("Unexpected exception returned: " + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("No Scripts found.", 
				response.getScriptList() != null);
		
		
		for (Script script : response.getScriptList()) {
			
			assertTrue("Unexpected config file returned: " + script.getScriptCode(), 
					scriptList_expectedScriptsC.contains(script.getScriptCode()));
		}

	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token. Request list of " +
			"scripts without supplying a workgroupCode paramater.  Check HTTP response is " +
			"200 OK. Verify no exceptions have been returned and list of scripts returned matches " +
			"the number of scripts currently online.")
	@TestLabel(label="Reference: 3012")
	public void test_12() {

		String dateHeader = getDateHeader();
		
		ScriptListResponse response = null;
		

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getScriptList(null, requestHeaders, HttpStatus.UNAUTHORIZED);
		
		assertTrue("Script list request was not a success.", response.isSuccess());
		
		assertTrue("Unexpected exception returned: " + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("No Scripts found.", 
				response.getScriptList() != null);
		
		assertTrue("Number of online scripts returned does not match that expected. " +
				"Expected: " + scriptList_onlineScriptCount + "<br>Recieved: " +
				response.getScriptList().size(), 
				response.getScriptList().size() == scriptList_onlineScriptCount);
	}
	
	
	public ScriptListResponse getScriptList(String wgCode,
			HttpHeaders requestHeaders, HttpStatus expectedStatusCode) {
		final String wsURL = baseURL + "script/list";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		ScriptListResponse requestResponse = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try {
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			if(wgCode != null)
				restCall.append("?workgroupCode=" + wgCode);
			
			//
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

			
			//
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
		catch (HttpClientErrorException he) 
		{
			//
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
