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

import com.amtsybex.fieldreach.services.messages.response.GetConfigResponse;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.* ;
import static org.junit.Assert.* ;


public class TestConfigDownload extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestConfigDownload.class.getName());


	@Test
	@TestDescription(desc="No date header and valid authorizaton token.")
	@TestLabel(label="Reference: 1301")
	public void test_1() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptConfigFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getConfig(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Invalid header and valid authorizaton token.")
	@TestLabel(label="Reference: 1302")
	public void test_2() {

		String dateHeader = "invalid date";
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptConfigFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getConfig(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label="Reference: 1303")
	public void test_3() {

		String dateHeader = getNonRFC2616Date();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptConfigFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getConfig(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes in front of the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 1304")
	public void test_4() {

		String dateHeader = getDateAfter(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptConfigFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getConfig(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes behind the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 1305")
	public void test_5() {

		String dateHeader = getDateBefore(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptConfigFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getConfig(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and no authorization token.")
	@TestLabel(label="Reference: 1306")
	public void test_6() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptConfigFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ":"
				+ null);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getConfig(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and malformed authorization token.")
	@TestLabel(label="Reference: 1307")
	public void test_7() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptConfigFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode + ":"
				+ authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getConfig(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token.<br>" +
						  "Invalid user specified in the authorization header.")
	@TestLabel(label="Reference: 1308")
	public void test_8() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptConfigFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getConfig(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	

	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "signed with incorrect password.")
	@TestLabel(label="Reference: 1309")
	public void test_9() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptConfigFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getConfig(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Attempt to get a configuration file that does not exist.<br>" +
						  "Verify that a "+Utils.FILE_NOT_FOUND+" is returned.")
	@TestLabel(label="Reference: 1310")
	public void test_10() {
		
		GetConfigResponse response = null;

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "sdczbdre.xml");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getConfig(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("No response recieved from the server.",
				response != null);
		assertTrue("Config request was successful.",
				!response.isSuccess());
		assertTrue("No error returned.", 
				response.getError().getErrorCode() != null);
		assertTrue("Unexpected exception returned.<br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.getError().getErrorCode().equals(Utils.FILE_NOT_FOUND));	
	}
	
	
	@Test
	@TestDescription(desc="Retrieve an existing WMconfig file.<br>" +
						  "Check the response is successful. Generate an MD5 checksum for the config" +
						  "returned and compare it to a known checksum for the config file.<br>" +
						  "The checksums should match.")
	@TestLabel(label="Reference: 1311")
	public void test_11() {
		
		GetConfigResponse response = null;

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", wmConfigFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getConfig(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("No response received from the server.",
				response != null);
		assertTrue("Config request was not successful.",
				response.isSuccess());
		assertTrue("Unexpected exception returned.<br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.getError().getErrorCode() == null);
		assertTrue("Config not retrieved.",
				response.getConfigFileSource() != null);
		assertTrue("Checksums do not match.",
				verifyChecksum(wmChecksum, response.getConfigFileSource()));
	}

	
	@Test
	@TestDescription(desc="Retrieve an existing Scriptconfig file.<br>" +
						  "Check the response is successful. Generate an MD5 checksum for the config" +
						  "returned and compare it to a known checksum for the config file.<br>" +
						  "The checksums should match.")
	@TestLabel(label="Reference: 1312")
	public void test_12() {
	
		GetConfigResponse response = null;
		
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptConfigFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getConfig(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("No response recieved from the server.",
				response != null);
		assertTrue("Config request was not successful.",
				response.isSuccess());
		assertTrue("Unexpected exception returned.<br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.getError().getErrorCode() == null);
		assertTrue("Config not retrieved.",
				response.getConfigFileSource() != null);
		assertTrue("Checksums do not match.",
				verifyChecksum(scriptChecksum, response.getConfigFileSource()));
	}

	
	@Test
	@TestDescription(desc="Retrieve an existing workgroup specific WMconfig file.<br>" +
						  "Check the response is successful. Generate an MD5 checksum for the config" +
						  "returned and compare it to a known checksum for the config file.<br>" +
						  "The checksums should match.")
	@TestLabel(label="Reference: 1313")
	public void test_13() {
		
		GetConfigResponse response = null;
	
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", workGroupWMConfigFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getConfig(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("No response recieved from the server.",
				response != null);
		assertTrue("Config request was not successful.",
				response.isSuccess());
		assertTrue("Unexpected exception returned.<br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.getError().getErrorCode() == null);
		assertTrue("Config not retrieved.",
				response.getConfigFileSource() != null);
		assertTrue("Checksums do not match.",
				verifyChecksum(workGroupWMChecksum, response.getConfigFileSource()));
	}

	
	@Test
	@TestDescription(desc="Retrieve an existing workgroup specific ScriptConfig file.<br>" +
						  "Check the response is successful. Generate an MD5 checksum for the config" +
						  "returned and compare it to a known checksum for the config file.<br>" +
						  "The checksums should match.")
	@TestLabel(label="Reference: 1314")
	public void test_14() {
		
		GetConfigResponse response = null;

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", workGroupScriptConfigFile);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getConfig(headersVariables, requestHeaders, HttpStatus.OK);
		
		assertTrue("No response recieved from the server.",
				response != null);
		assertTrue("Config request was not successful.",
				response.isSuccess());
		assertTrue("Unexpected exception returned.<br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.getError().getErrorCode() == null);
		assertTrue("Config not retrieved.",
				response.getConfigFileSource() != null);
		assertTrue("Checksums do not match.",
				verifyChecksum(workGroupScriptChecksum, response.getConfigFileSource()));
	}
	
	
	
	public GetConfigResponse getConfig(Map<String, String> headersVariables,
			HttpHeaders requestHeaders, HttpStatus expectedStatusCode) {
		
		final String wsURL = baseURL + "config/{id}";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		GetConfigResponse requestResponse = null;

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


			log.debug("getConfig results : " + results);
			assertTrue("Result is null", results != null);

			
			// Parse XML received from the response into an 
			// GetConfigResponse object
			try {
				
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("GetConfigResponse", GetConfigResponse.class);
				requestResponse = (GetConfigResponse) xstream
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
