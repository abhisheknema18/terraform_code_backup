/**
 * Author:  T Murray
 * Date:    09/01/2014
 * Project: FDE022
 * 
 * Copyright AMT-Sybex 2012
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

import com.amtsybex.fieldreach.services.messages.response.RetrieveWorkOrderResponse;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.Test;
import static org.junit.Assert.*;


public class TestGetWorkOrderSource extends CommonBase 
{

	static Logger log = LoggerFactory.getLogger(TestGetWorkOrderSource.class.getName());
	
	
	@Test
	@TestDescription(desc="No date header and valid authorizaton token.")
	@TestLabel(label="Reference: 2301")
	public void test_1() 
	{

		//
		// Headers
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WONotWorkIssued);

		HttpHeaders requestHeaders = new HttpHeaders();
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		getWorkOrderSource(null, headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
		
	@Test
	@TestDescription(desc="Invalid header and valid authorizaton token.")
	@TestLabel(label="Reference: 2302")
	public void test_2() 
	{
		//
		// Headers
		String dateHeader = "invalid date";
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WONotWorkIssued);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		getWorkOrderSource(null, headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
	@Test
	@TestDescription(desc="Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label="Reference: 2303")
	public void test_3() 
	{

		//
		// Headers
		String dateHeader = getNonRFC2616Date();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WONotWorkIssued);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		getWorkOrderSource(null, headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes in front of the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 2304")
	public void test_4() 
	{

		//
		// Headers
		String dateHeader = getDateAfter(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WONotWorkIssued);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		getWorkOrderSource(null, headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes behind the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 2305")
	public void test_5() 
	{

		//
		// Headers
		String dateHeader = getDateBefore(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WONotWorkIssued);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		getWorkOrderSource(null, headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and no authorization token.")
	@TestLabel(label="Reference: 2306")
	public void test_6() 
	{
		//
		// Headers
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WONotWorkIssued);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ":"
				+ null);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		getWorkOrderSource(null, headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and malformed authorization token.")
	@TestLabel(label="Reference: 2307")
	public void test_7() 
	{
		//
		// Headers
		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WONotWorkIssued);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode + ":"
				+ authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		getWorkOrderSource(null, headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token.<br>" +
						  "Invalid user specified in the authorization header.")
	@TestLabel(label="Reference: 2308")
	public void test_8() 
	{
		//
		// Headers
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WONotWorkIssued);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		getWorkOrderSource(null, headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	

	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "signed with incorrect password.")
	@TestLabel(label="Reference: 2309")
	public void test_9() 
	{
		//
		// Headers
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WONotWorkIssued);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		getWorkOrderSource(null, headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
			
	}
	
	


	@Test
	@TestDescription(desc="Attempt to get work order source for a work order that does not exist in the " +
			"workissued table. Do not specify the districtCode parameter. " + Utils.FILE_NOT_FOUND +" should be returned.")
	@TestLabel(label="Reference: 2310")
	public void test_10() 
	{
		RetrieveWorkOrderResponse response = null;
		
	
		//
		// Headers
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WONotWorkIssued);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getWorkOrderSource(null, headersVariables, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Work order was succesfully retrieved.", 
				!response.isSuccess());
		
		assertTrue("No error returned.", 
				response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected exception returned.<br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.getError().getErrorCode().equals(Utils.FILE_NOT_FOUND));
			
	}
	

	@Test
	@TestDescription(desc="Attempt to get work order source for a work order that does not exist in the " +
			"workissued table. Specify the districtCode parameter. " + Utils.FILE_NOT_FOUND +" should be returned.")
	@TestLabel(label="Reference: 2311")
	public void test_11() 
	{
		RetrieveWorkOrderResponse response = null;
		
	
		//
		// Headers
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WONotWorkIssued);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getWorkOrderSource(super.woDC, headersVariables, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Work order was succesfully retrieved.", 
				!response.isSuccess());
		
		assertTrue("No error returned.", 
				response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected exception returned.<br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.getError().getErrorCode().equals(Utils.FILE_NOT_FOUND));
			
	}
	
	
	@Test
	@TestDescription(desc="Attempt to get work order source for a work order that does exist in the " +
			"workissued table but not on the file system. Do not specify the districtCode parameter. " + 
			Utils.FILE_NOT_FOUND +" should be returned.")
	@TestLabel(label="Reference: 2312")
	public void test_12() 
	{
		RetrieveWorkOrderResponse response = null;
		
	
		//
		// Headers
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WOWorkIssuedAndNotFileSystem);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getWorkOrderSource(null, headersVariables, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Work order was succesfully retrieved.", 
				!response.isSuccess());
		
		assertTrue("No error returned.", 
				response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected exception returned.<br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.getError().getErrorCode().equals(Utils.FILE_NOT_FOUND));
			
	}
	

	@Test
	@TestDescription(desc="Attempt to get work order source for a work order that does exist in the " +
			"workissued table but not on the file system. Specify the districtCode parameter. " + 
			Utils.FILE_NOT_FOUND +" should be returned.")
	@TestLabel(label="Reference: 2313")
	public void test_13() 
	{
		RetrieveWorkOrderResponse response = null;
		
	
		//
		// Headers
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WOWorkIssuedAndNotFileSystem);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getWorkOrderSource(super.woDC, headersVariables, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Work order was succesfully retrieved.", 
				!response.isSuccess());
		
		assertTrue("No error returned.", 
				response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected exception returned.<br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.getError().getErrorCode().equals(Utils.FILE_NOT_FOUND));
			
	}
	
	
	@Test
	@TestDescription(desc="Attempt to get work order source for a work order that does exist in the " +
			"workissued table. Do not specify the districtCode parameter. Verify that a " + 
			Utils.WORKORDER_DOWNLOAD_EXCEPTION + " exception is returned.")
	@TestLabel(label="Reference: 2314")
	public void test_14() 
	{
		RetrieveWorkOrderResponse response = null;
		
		//
		// Headers
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WODownloadInvalidStatus);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getWorkOrderSource(null, headersVariables, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Work order download was a success.", !response.isSuccess());
		
		assertTrue("Unexpected exception returned. Expected: " + Utils.WORKORDER_DOWNLOAD_EXCEPTION + 
				"<br>Recieved: " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.WORKORDER_DOWNLOAD_EXCEPTION));
		
	}
	
	
	@Test
	@TestDescription(desc="Attempt to get work order source for a work order that does exist in the " +
			"workissued table. Specify the districtCode parameter. Verify that a " + 
			Utils.WORKORDER_DOWNLOAD_EXCEPTION + " exception is returned.")
	@TestLabel(label="Reference: 2315")
	public void test_15() 
	{
		RetrieveWorkOrderResponse response = null;
		
		//
		// Headers
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WODownloadInvalidStatus);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getWorkOrderSource(super.woDC, headersVariables, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Work order download was a success.", !response.isSuccess());
		
		assertTrue("Unexpected exception returned. Expected: " + Utils.WORKORDER_DOWNLOAD_EXCEPTION + 
				"<br>Recieved: " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.WORKORDER_DOWNLOAD_EXCEPTION));
		
	}
	
	@Test
	@TestDescription(desc="Attempt to get work order source for a work order that does exist in the " +
			"workissued table and the file system. Do not specify the districtCode parameter. No exceptions should be " +
			"returned. extract work order data and generate a checksum, verifying that it matches the checksum returned " +
			"by the web service.")
	@TestLabel(label="Reference: 2316")
	public void test_16() 
	{
		RetrieveWorkOrderResponse response = null;
		
	
		//
		// Headers
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WOWorkIssuedAndFileSystem);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getWorkOrderSource(null, headersVariables, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Work order was not succesfully retrieved. Error Code: " +
				response.getError().getErrorCode(), response.isSuccess());
		
		
		assertTrue("Unexpected exception returned.<br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("work order data  not populated.", 
				response.getWorkOrderData() != null);
		
		assertTrue("Checksums do not match.",
				verifyChecksum(response.getChecksum(), response.getWorkOrderData()));
			
	}
	
	
	@Test
	@TestDescription(desc="Attempt to get work order source for a work order that does exist in the " +
			"workissued table and the file system. Specify the districtCode parameter. No exceptions should be " +
			"returned. Extract work order data and generate a checksum, verifying that it matches the checksum returned " +
			"by the web service.")
	@TestLabel(label="Reference: 2317")
	public void test_17() 
	{
		RetrieveWorkOrderResponse response = null;
		
	
		//
		// Headers
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", WOWorkIssuedAndFileSystem);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		response = getWorkOrderSource(super.woDC, headersVariables, requestHeaders, HttpStatus.OK);
		
		
		assertTrue("Work order was not succesfully retrieved. Error Code: " +
				response.getError().getErrorCode(), response.isSuccess());
		
		
		assertTrue("Unexpected exception returned.<br><b>ErrorCode:</b><br>" 
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(), 
				response.getError().getErrorCode() == null);
		
		assertTrue("work order data  not populated.", 
				response.getWorkOrderData() != null);
		
		assertTrue("Checksums do not match.",
				verifyChecksum(response.getChecksum(), response.getWorkOrderData()));
			
	}
	
		
	
	private RetrieveWorkOrderResponse getWorkOrderSource(String districtCode, Map<String, String> headersVariables,
			   				    HttpHeaders requestHeaders,
			   				    HttpStatus expectedStatusCode)
	{
		final String wsURL = baseURL + "retrieveWorkOrder/{id}";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();
		
		StringBuffer restCall;
		RetrieveWorkOrderResponse response = null;
		
		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try 
		{
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);
			
			
			//
			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET,requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();
			
			log.debug("get worko order source results : " + results);
			assertTrue("Result is null", results != null);
			
			
			//
			// Parse XML received from the response into an 
			// RetrieveWorkOrderResponse object
			try 
			{
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("RetrieveWorkOrderResponse", RetrieveWorkOrderResponse.class);
				response = (RetrieveWorkOrderResponse) xstream
						.fromXML(results);

				log.debug("Success : " + response.isSuccess());
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
			//Check to see if the HHTP status code is what we expect
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
		
		return response;
	}

}
