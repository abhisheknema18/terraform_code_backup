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

import com.amtsybex.fieldreach.services.messages.response.GetScriptResultResponse;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.* ;
import static org.junit.Assert.* ;


public class TestScriptResultRetrieval extends CommonBase 
{

	static Logger log = LoggerFactory.getLogger(TestScriptResultRetrieval.class.getName());

	@Test
	@TestDescription(desc="No date header and valid authorizaton token.")
	@TestLabel(label="Reference: 2501")
	public void test_1() 
	{
		
		HttpHeaders requestHeaders = new HttpHeaders();
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "0");
		
		
		retrieveScriptResult(requestHeaders, headersVariables, null, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Invalid header and valid authorizaton token.")
	@TestLabel(label="Reference: 2502")
	public void test_2() 
	{	
		
		String dateHeader = "invalid date";
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "0");
		
		
		retrieveScriptResult(requestHeaders, headersVariables, null,  HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label="Reference: 2503")
	public void test_3() 
	{
		String dateHeader = getNonRFC2616Date();
	
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "0");
		
		retrieveScriptResult(requestHeaders, headersVariables, null, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes in front of the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 2504")
	public void test_4() 
	{
		
		String dateHeader = getDateAfter(1);
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "0");
		
		retrieveScriptResult(requestHeaders, headersVariables, null, HttpStatus.UNAUTHORIZED);	
	}
	
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes behind the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 2505")
	public void test_5() 
	{
		String dateHeader = getDateBefore(1);
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "0");
		
		retrieveScriptResult(requestHeaders, headersVariables, null, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and no authorization token.")
	@TestLabel(label="Reference: 2506")
	public void test_6() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ":"
				+ null);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "0");
		
		retrieveScriptResult(requestHeaders, headersVariables, null, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and malformed authorization token.")
	@TestLabel(label="Reference: 2507")
	public void test_7() 
	{
		
		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode + ":"
				+ authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "0");
		
		retrieveScriptResult(requestHeaders, headersVariables, null, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token.<br>" +
						  "Invalid user specified in the authorization header.")
	@TestLabel(label="Reference: 2508")
	public void test_8() 
	{
		
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "0");
			
		retrieveScriptResult(requestHeaders, headersVariables, null, HttpStatus.UNAUTHORIZED);
	}
	

	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "signed with incorrect password.")
	@TestLabel(label="Reference: 3109")
	public void test_9() 
	{
		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "0");
		
		retrieveScriptResult(requestHeaders, headersVariables, null, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Attempt to retrieve a script result supplying a returnid of 'NAN'. Check the " +
			"response is HTTP Status code 400 Bad Rquest.")
	@TestLabel(label="Reference: 2510")
	public void test_10() 
	{
		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "NAN");
		
		retrieveScriptResult(requestHeaders, headersVariables, null, HttpStatus.BAD_REQUEST);
	}
	
	
	@Test
	@TestDescription(desc="Attempt to retrieve a script result supplying a returnid that does not exist" +
			"in the returnscripts table. Verify that the response if HTTP 200 OK. Verify that the request" +
			"success element is false. Check that ScriptResultNotFound exception is returned.")
	@TestLabel(label="Reference: 2511")
	public void test_11() 
	{
		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "-1");
		
		GetScriptResultResponse response = null;
		
		response = retrieveScriptResult(requestHeaders, headersVariables, null, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.SCRIPT_RESULT_NOTFOUND_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.SCRIPT_RESULT_NOTFOUND_EXCEPTION));
	}
	
	
	@Test
	@TestDescription(desc="Attempt to retrieve a script result supplying a returnid that exists" +
			"in the returnscripts table. Verify that the response if HTTP 200 OK. Verify that the request" +
			"success element is false. Check that ScriptResultExtractException is returned.")
	@TestLabel(label="Reference: 2512")
	public void test_12() 
	{
		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", retrieveScriptResultExtractionError);
		
		GetScriptResultResponse response = null;
		
		response = retrieveScriptResult(requestHeaders, headersVariables, null, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.SCRIPT_RESULT_EXTRACT_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.SCRIPT_RESULT_EXTRACT_EXCEPTION));
	}
	
	
	@Test
	@TestDescription(desc="Attempt to retrieve a script result supplying a returnid that exists" +
			"in the returnscripts table and can be extracted. Do not specify the extractBinary parameter. " +
			"Verify that the response if HTTP 200 OK. Verify that the request success element is true. " +
			"Extract the script result data and generate a checksum. verify the checksums match.")
	@TestLabel(label="Reference: 2513")
	public void test_13() 
	{
		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", retrieveScriptResultValidResult);
		
		
		GetScriptResultResponse response = null;
		response = retrieveScriptResult(requestHeaders, headersVariables, null,  HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
				
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> None" + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("Script result data not found.", 
				response.getScriptResultSource() != null);
		

		byte[] decoded = Common.decodeBase64(response.getScriptResultSource());
		String generatedMD5 = Common.generateMD5Checksum(decoded);
		
		assertTrue("Checksums do not match." +
				"<br/><br/><b>Expected:</b> " + response.getChecksum() +
				"<br><b>Generated:</b> " + generatedMD5, 
				response.getChecksum().equals(generatedMD5));
		
		
		Common.writeBytesToFile(decoded, "2513.xml", retrieveScriptResultDir);
	}
	
	
	@Test
	@TestDescription(desc="Attempt to retrieve a script result supplying a returnid that exists" +
			"in the returnscripts table and can be extracted. Specify the extractBinary parameter with a " +
			"value of false. Verify that the response if HTTP 200 OK. Verify that the request success " +
			"element is true.Extract the script result data and generate a checksum. verify the " +
			"checksums match.")
	@TestLabel(label="Reference: 2514")
	public void test_14() 
	{
		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", retrieveScriptResultValidResult);
		
		
		GetScriptResultResponse response = null;
		response = retrieveScriptResult(requestHeaders, headersVariables, false, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
				
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> None" + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("Script result data not found.", 
				response.getScriptResultSource() != null);
		

		byte[] decoded = Common.decodeBase64(response.getScriptResultSource());
		String generatedMD5 = Common.generateMD5Checksum(decoded);
		
		assertTrue("Checksums do not match." +
				"<br/><br/><b>Expected:</b> " + response.getChecksum() +
				"<br><b>Generated:</b> " + generatedMD5, 
				response.getChecksum().equals(generatedMD5));
		
		
		Common.writeBytesToFile(decoded, "2514.xml", retrieveScriptResultDir);
	}
	
	
	@Test
	@TestDescription(desc="Attempt to retrieve a script result supplying a returnid that exists" +
			"in the returnscripts table and can be extracted. Specify the extractBinary parameter with a " +
			"value of true. Verify that the response if HTTP 200 OK. Verify that the request success " +
			"element is true.Extract the script result data and generate a checksum. verify the " +
			"checksums match.")
	@TestLabel(label="Reference: 2515")
	public void test_15() 
	{
		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", retrieveScriptResultValidResult);
		
		
		GetScriptResultResponse response = null;
		response = retrieveScriptResult(requestHeaders, headersVariables, true, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
				
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> None" + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("Script result data not found.", 
				response.getScriptResultSource() != null);
		
		byte[] decoded = Common.decodeBase64(response.getScriptResultSource());
		String generatedMD5 = Common.generateMD5Checksum(decoded);
		
		assertTrue("Checksums do not match." +
				"<br/><br/><b>Expected:</b> " + response.getChecksum() +
				"<br><b>Generated:</b> " + generatedMD5, 
				response.getChecksum().equals(generatedMD5));
		
		
		Common.writeBytesToFile(decoded, "2515.xml", retrieveScriptResultDir);
	}
	
	
	@Test
	@TestDescription(desc="Attempt to retrieve a script result supplying a returnid that exists" +
			"in the returnscripts table and can be extracted and contains unanswered questions. "
			+ "Specify the fullScript parameter with a value of true. Verify that the response "
			+ "if HTTP 200 OK. Verify that the request success element is true.Extract the script "
			+ "result data and generate a checksum. verify the checksums match.")
	@TestLabel(label="Reference: 2516")
	public void test_16() 
	{
		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", retrieveScriptResultUnanswered);
		
		
		GetScriptResultResponse response = null;
		response = retrieveScriptResult(requestHeaders, headersVariables, true, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
				
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> None" + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("Script result data not found.", 
				response.getScriptResultSource() != null);
		

		byte[] decoded = Common.decodeBase64(response.getScriptResultSource());
		String generatedMD5 = Common.generateMD5Checksum(decoded);
		
		assertTrue("Checksums do not match." +
				"<br/><br/><b>Expected:</b> " + response.getChecksum() +
				"<br><b>Generated:</b> " + generatedMD5, 
				response.getChecksum().equals(generatedMD5));
		
		
		Common.writeBytesToFile(decoded, "2516.xml", retrieveScriptResultDir);
	}
	
	@Test
	@TestDescription(desc="Attempt to retrieve a script result supplying a returnid that exists" +
			"in the returnscripts table and can not be edited by user test.unrevokedUserCode1. "
			+ "Specify the extractEditPermission parameter with a value of true. Verify that the "
			+ "response is HTTP 200 OK. Verify that the request success element is true. "
			+ "Extract the script result data and generate a checksum. verify the checksums match."
			+ "Ensure the edit flag is present and set to false.")
	@TestLabel(label="Reference: 2517")
	public void test_17() 
	{
		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", retrieveScriptResultCannotEdit);
		
		
		GetScriptResultResponse response = null;
		response = retrieveScriptResult(requestHeaders, headersVariables, true, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
				
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> None" + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("Script result data not found.", 
				response.getScriptResultSource() != null);
		
		assertTrue("Edit flag set to true.", 
				response.getEdit() == false);
		
		byte[] decoded = Common.decodeBase64(response.getScriptResultSource());
		String generatedMD5 = Common.generateMD5Checksum(decoded);
		
		assertTrue("Checksums do not match." +
				"<br/><br/><b>Expected:</b> " + response.getChecksum() +
				"<br><b>Generated:</b> " + generatedMD5, 
				response.getChecksum().equals(generatedMD5));
		
		
		Common.writeBytesToFile(decoded, "2517.xml", retrieveScriptResultDir);
	}

	@Test
	@TestDescription(desc="Attempt to retrieve a script result supplying a returnid that exists" +
			"in the returnscripts table and can be edited by user test.canEditUserCode. "
			+ "Specify the extractEditPermission parameter with a value of true. Verify that the "
			+ "response is HTTP 200 OK. Verify that the request success element is true. "
			+ "Extract the script result data and generate a checksum. verify the checksums match."
			+ "Ensure the edit flag is present and set to true.")
	@TestLabel(label="Reference: 2518")
	public void test_18() 
	{
		String dateHeader = getDateHeader();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(canEditUserCode,canEditPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", retrieveScriptResultCanEdit);
		
		
		GetScriptResultResponse response = null;
		response = retrieveScriptResult(requestHeaders, headersVariables, true, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
				
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> None" + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("Script result data not found.", 
				response.getScriptResultSource() != null);
		
		assertTrue("Edit flag set to false.", 
				response.getEdit() == true);
		
		byte[] decoded = Common.decodeBase64(response.getScriptResultSource());
		String generatedMD5 = Common.generateMD5Checksum(decoded);
		
		assertTrue("Checksums do not match." +
				"<br/><br/><b>Expected:</b> " + response.getChecksum() +
				"<br><b>Generated:</b> " + generatedMD5, 
				response.getChecksum().equals(generatedMD5));
		
		Common.writeBytesToFile(decoded, "2518.xml", retrieveScriptResultDir);
	}
	

	public GetScriptResultResponse retrieveScriptResult(HttpHeaders requestHeaders, 
			Map<String, String> headersVariables, Boolean extractBinary, HttpStatus expectedStatusCode)
	{
		final String wsURL = baseURL + "script/result/{id}?extractEditPermission=true";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		GetScriptResultResponse requestResponse = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try 
		{
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			if (extractBinary != null)
				restCall.append("&extractBinary=" + extractBinary);
				
			
			//
			// Make REST call and retrieve the results
			String requestBody = null;

			
			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);


			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET, requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();


			log.debug("retrieveScriptResult results : " + results);
			assertTrue("Result is null", results != null);

			
			//
			// Parse XML received from the response into an 
			// GetConfigResponse object
			try 
			{
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("GetScriptResultResponse", GetScriptResultResponse.class);
				requestResponse = (GetScriptResultResponse) xstream.fromXML(results);

				log.debug("Success : " + requestResponse.isSuccess());
			} 
			catch (XmlMappingException e) 
			{
				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
			}
		} 
		catch (HttpClientErrorException he) 
		{
			//
			//Check to see if the HTTP status code is what we expect
			if (he.getStatusCode() == expectedStatusCode)
			{
				log.debug(expectedStatusCode + " status code returned as expected.");
			}
			else
			{
				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
		}
		
		return requestResponse;
		
	}
	
}