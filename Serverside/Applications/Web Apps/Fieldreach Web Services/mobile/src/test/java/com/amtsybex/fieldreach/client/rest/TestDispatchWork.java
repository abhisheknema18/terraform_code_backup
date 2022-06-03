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
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestDispatchWork extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestDispatchWork.class.getName());

	@Test
	@TestDescription(desc = "Call dispatch work without the districtCode parameter. "
			+ "Specify a workorder that does not exist in the workissued table. " + "A "
			+ Utils.WORKORDER_NOT_FOUND_EXCEPTION + " message should be returned.")
	@TestLabel(label = "Reference: 7101")
	public void test_1() {

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set("x-fws-deviceid", deviceid);

		CallResponse resp = dispatchWork(WOCancelNotExist, null, requestHeaders, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Check for workorder not found exception
		assertTrue(
				"Unexepected exception returned. Expected: " + Utils.WORKORDER_NOT_FOUND_EXCEPTION + "<br>Returned: "
						+ resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_NOT_FOUND_EXCEPTION));

	}

	@Test
	@TestDescription(desc = "Call dispatch work with the districtCode parameter. "
			+ "Specify a workorder that does not exist in the workissued table. " + "A "
			+ Utils.WORKORDER_NOT_FOUND_EXCEPTION + " message should be returned.")
	@TestLabel(label = "Reference: 7102")
	public void test_2() {

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);

		CallResponse resp = dispatchWork(WOCancelNotExist, super.woDC, requestHeaders, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Check for workorder not found exception
		assertTrue(
				"Unexepected exception returned. Expected: " + Utils.WORKORDER_NOT_FOUND_EXCEPTION + "<br>Returned: "
						+ resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_NOT_FOUND_EXCEPTION));

	}

	@Test
	@TestDescription(desc = "Call dispatch work without the districtCode parameter. "
			+ "Specify a workorder that does exist in the workissued table but has a status "
			+ "that prevents dispatch. A " + Utils.WORKORDER_RECALL_EXCEPTION + " message should be returned.")
	@TestLabel(label = "Reference: 7103")
	public void test_3() {
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);

		CallResponse resp = dispatchWork(WOCancelInvalidStatus, null, requestHeaders, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Check for workorder not found exception
		assertTrue(
				"Unexepected exception returned. Expected: " + Utils.WORKORDER_DISPATCH_EXCEPTION + "<br>Returned: "
						+ resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_DISPATCH_EXCEPTION));

	}

	@Test
	@TestDescription(desc = "Call dispatch work with the districtCode parameter. "
			+ "Specify a workorder that does exist in the workissued table but has a status "
			+ "that prevents dispatch. A " + Utils.WORKORDER_RECALL_EXCEPTION + " message should be returned.")
	@TestLabel(label = "Reference: 7104")
	public void test_4() {
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);

		CallResponse resp = dispatchWork(WOCancelInvalidStatus, super.woDC, requestHeaders, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Check for workorder not found exception
		assertTrue(
				"Unexepected exception returned. Expected: " + Utils.WORKORDER_DISPATCH_EXCEPTION + "<br>Returned: "
						+ resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_DISPATCH_EXCEPTION));

	}

	@Test
	@TestDescription(desc = "Call dispatch work with the districtCode parameter. "
			+ "Specify a workorder that does exist in the workissued table. No exceptions should "
			+ "be returned as the database is updated.")
	@TestLabel(label = "Reference: 7105")
	public void test_5() {
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);

		File file = new File(StringUtils.cleanPath(super.uploadWorkOrderDir + "/" + 
				Utils.WORKORDER_FILE_PREFIX + super.validRELWO2 + Utils.WORKORDER_FILE_EXTENSION));
		
		String woData = null;
		
		if (file.exists() && file.isFile())
		{
			try 
			{
				byte[] bytes = Common.getBytesFromFile(file);
				woData = new String(bytes);
			} 
			catch (FileNotFoundException e) 
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			} 
			catch (IOException e)
			{
				log.error(e.getMessage());
				fail("Unable to extract work order content to build request body.");
			}
		}
		else
		{
			log.error("Error creating request body from work order file");
			fail("Unable to extract work order content to build request body.");
		}
		
		
		CallResponse resp = releaseWork(null, super.validRELWO2, woData, HttpStatus.OK);
		
		resp = dispatchWork(super.validRELWO2, "NA", requestHeaders, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was not a success.", resp.isSuccess());

		// No exceptions should be returned
		assertTrue("Unexepected exception returned: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);

	}
	
	@Test
	@TestDescription(desc = "Call dispatch work with the districtCode parameter. "
			+ "Specify a valid workorder. Supply a FWS authorisation type. "
			+ "An UnauthorizedException should be returned")
	@TestLabel(label = "Reference: 7106")
	public void test_6() {
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);

		dispatchWork(WOCancel, super.woDC, requestHeaders, HttpStatus.UNAUTHORIZED, false);


	}

	
	protected CallResponse dispatchWork(String woNo, String district, HttpHeaders requestHeaders, HttpStatus expectedStatusCode) {
		return this.dispatchWork(woNo, district, requestHeaders, expectedStatusCode, true);
	}
	
	private CallResponse dispatchWork(String woNo, String district, HttpHeaders requestHeaders,
			HttpStatus expectedStatusCode, boolean iwsType) {
		final String wsURL = baseURL + "dispatchwork/" + woNo;

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		CallResponse response = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);

		String dateHeader = getDateHeader();
		log.debug("Date header=" + dateHeader);
		requestHeaders.set("Date", dateHeader);
		
		if(iwsType) {
			requestHeaders.set("Authorization", getIWSAuthorisationHeaderValue(super.workissuedUserCode, super.workissuedUserPassword, dateHeader));
		}else {
			requestHeaders.set("Authorization", getAuthorisationHeaderValue(unrevokedUserCode, unrevokedPassword, dateHeader));
		}
		
		try {
			
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			if (district != null)
				restCall.append("?districtCode=" + district);

			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(restCall.toString(), HttpMethod.PUT,
					requestEntity, String.class);

			String results = resultsExchange.getBody();

			log.debug("process transaction results : " + results);
			assertTrue("Result is null", results != null);

			// Parse XML received from the response into an
			// CallResponse object
			try {
				
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("CallResponse", CallResponse.class);
				response = (CallResponse) xstream.fromXML(results);

				log.debug("Success : " + response.isSuccess());
				
			} catch (XmlMappingException e) {
				
				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
				
			}
			
		} catch (HttpClientErrorException he) {
			
			// Check to see if the HHTP status code is what we expect
			
			if (he.getStatusCode() == expectedStatusCode) {
				
				log.debug(expectedStatusCode + " status code returned as expected.");
				
			} else {
				
				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
		}

		return response;
	}
	
	
	protected CallResponse releaseWork(String districtCode, String woNo, String body,
			HttpStatus expectedStatusCode) {
		return this.releaseWork(districtCode, woNo, body, expectedStatusCode, true);
	}
	
	/**
	 * Method to call the release work web service
	 * 
	 * @param districtCode
	 * value of the option districtCode parameter. Null means it should not be used.
	 * 
	 * @param woNo
	 * workorderno of the work order being released.
	 * 
	 * @param body
	 * Body of the request
	 * 
	 * @param expectedStatusCode
	 * expected HTTP status code of the request being made
	 * 
	 */
	protected CallResponse releaseWork(String districtCode, String woNo, String body,
			HttpStatus expectedStatusCode, boolean iwsType)
	{
		
		log.debug(">>> releaseWork districtCode= " + districtCode +
				" body=xxxx");
		
		final String wsURL = baseURL + "releasework/" + woNo;

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		CallResponse resp = null;

		
		try 
		{
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);
			
			if (districtCode != null)
				restCall.append("?districtCode=" + districtCode);

			//
			// Request Headers
			HttpHeaders requestHeaders = new HttpHeaders();
			String dateHeader = getDateHeader();
			log.debug("Date header=" + dateHeader);
			requestHeaders.set("Date", dateHeader);
			requestHeaders.set("x-fws-deviceid", deviceid);

			requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
			
			if(iwsType) {
				requestHeaders.set("Authorization", getIWSAuthorisationHeaderValue(super.workissuedUserCode, super.workissuedUserPassword, dateHeader));
			}else {
				requestHeaders.set("Authorization", getAuthorisationHeaderValue(unrevokedUserCode, unrevokedPassword, dateHeader));
			}
			
			requestHeaders.set("x-fws-appCode", "FWOM");
			
			//
			// Build request Body
			String requestBody = body;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			//
			// Make REST call and retrieve the results
			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.PUT, requestEntity,
					String.class);


			String results = resultsExchange.getBody();

			log.debug("issuework results : " + results);
			log.debug("HTTP Status Code: " + resultsExchange.getStatusCode());

			assertTrue("Result is null", results != null);
			assertTrue("Unexpected HTTP Status Code. Expected: " + expectedStatusCode.value() + 
					"<br>Recieved: ", resultsExchange.getStatusCode().value() == expectedStatusCode.value());

			//
			// Parse XML received from the response into an
			// CallResponse object
			try {
				XStream xstream = new XStream(new StaxDriver());
				
				xstream.alias("CallResponse",
						CallResponse.class);

				resp = (CallResponse) xstream.fromXML(results);
				
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
			// Check to see if the HHTP status code is what we expect
			if (he.getStatusCode() == expectedStatusCode) 
			{
				log.debug(expectedStatusCode
						+ " status code returned as expected.");
			}
			else 
			{
				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
		}
		catch (Exception e) 
		{
			log.error("Client Exception :" + e.getMessage());
			fail("Client Exception :" + e.getMessage());
		}

		log.debug("<<< releaseWork");
		
		return resp;
	}

}
