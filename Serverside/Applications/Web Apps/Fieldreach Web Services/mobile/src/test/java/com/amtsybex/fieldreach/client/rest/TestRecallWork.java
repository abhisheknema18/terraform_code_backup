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

import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestRecallWork extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestRecallWork.class.getName());

	@Test
	@TestDescription(desc = "Call recall work without the districtCode parameter. "
			+ "Specify a workorder that does not exist in the workissued table. " + "A "
			+ Utils.WORKORDER_NOT_FOUND_EXCEPTION + " message should be returned.")
	@TestLabel(label = "Reference: 4101")
	public void test_1() {

		HttpHeaders requestHeaders = new HttpHeaders();

		requestHeaders.set("x-fws-deviceid", deviceid);

		CallResponse resp = recallWork(WOCancelNotExist, null, requestHeaders, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Check for workorder not found exception
		assertTrue(
				"Unexepected exception returned. Expected: " + Utils.WORKORDER_NOT_FOUND_EXCEPTION + "<br>Returned: "
						+ resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_NOT_FOUND_EXCEPTION));

	}

	@Test
	@TestDescription(desc = "Call recall work with the districtCode parameter. "
			+ "Specify a workorder that does not exist in the workissued table. " + "A "
			+ Utils.WORKORDER_NOT_FOUND_EXCEPTION + " message should be returned.")
	@TestLabel(label = "Reference: 6102")
	public void test_2() {

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);

		CallResponse resp = recallWork(WOCancelNotExist, super.woDC, requestHeaders, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Check for workorder not found exception
		assertTrue(
				"Unexepected exception returned. Expected: " + Utils.WORKORDER_NOT_FOUND_EXCEPTION + "<br>Returned: "
						+ resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_NOT_FOUND_EXCEPTION));

	}

	@Test
	@TestDescription(desc = "Call recall work without the districtCode parameter. "
			+ "Specify a workorder that does exist in the workissued table but has a status "
			+ "that prevents recall. A " + Utils.WORKORDER_RECALL_EXCEPTION + " message should be returned.")
	@TestLabel(label = "Reference: 6103")
	public void test_3() {
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);

		CallResponse resp = recallWork(WOCancelInvalidStatus, null, requestHeaders, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Check for workorder not found exception
		assertTrue(
				"Unexepected exception returned. Expected: " + Utils.WORKORDER_RECALL_EXCEPTION + "<br>Returned: "
						+ resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_RECALL_EXCEPTION));

	}

	@Test
	@TestDescription(desc = "Call recall work with the districtCode parameter. "
			+ "Specify a workorder that does exist in the workissued table but has a status "
			+ "that prevents recall. A " + Utils.WORKORDER_RECALL_EXCEPTION + " message should be returned.")
	@TestLabel(label = "Reference: 6104")
	public void test_4() {
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);

		CallResponse resp = recallWork(WOCancelInvalidStatus, super.woDC, requestHeaders, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was a success.", !resp.isSuccess());

		// Check for workorder not found exception
		assertTrue(
				"Unexepected exception returned. Expected: " + Utils.WORKORDER_RECALL_EXCEPTION + "<br>Returned: "
						+ resp.getError().getErrorCode(),
				resp.getError().getErrorCode().equals(Utils.WORKORDER_RECALL_EXCEPTION));

	}

	@Test
	@TestDescription(desc = "Call recall work without the districtCode parameter. "
			+ "Specify a workorder that does exist in the workissued table. No exceptions should "
			+ "be returned as the database is updated.")
	@TestLabel(label = "Reference: 6105")
	public void test_5() {
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);

		CallResponse resp = recallWork(WOCancel, null, requestHeaders, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was not a success.", resp.isSuccess());

		// No exceptions should be returned
		assertTrue("Unexepected exception returned: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);

	}

	@Test
	@TestDescription(desc = "Call recall work with the districtCode parameter. "
			+ "Specify a workorder that does exist in the workissued table. No exceptions should "
			+ "be returned as the database is updated.")
	@TestLabel(label = "Reference: 6106")
	public void test_6() {
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);

		CallResponse resp = recallWork(WOCancel, super.woDC, requestHeaders, HttpStatus.OK);

		// Ensure response was not a success
		assertTrue("Response was not a success.", resp.isSuccess());

		// No exceptions should be returned
		assertTrue("Unexepected exception returned: " + resp.getError().getErrorCode(),
				resp.getError().getErrorCode() == null);

	}
	
	@Test
	@TestDescription(desc = "Call recall work with the districtCode parameter. "
			+ "Specify a valid workorder. Supply a FWS authorisation type. "
			+ "An UnauthorizedException should be returned")
	@TestLabel(label = "Reference: 6107")
	public void test_7() {
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);

		recallWork(WOCancel, super.woDC, requestHeaders, HttpStatus.UNAUTHORIZED, false);


	}

	
	protected CallResponse recallWork(String woNo, String district, HttpHeaders requestHeaders, HttpStatus expectedStatusCode) {
		return this.recallWork(woNo, district, requestHeaders, expectedStatusCode, true);
	}
	
	private CallResponse recallWork(String woNo, String district, HttpHeaders requestHeaders,
			HttpStatus expectedStatusCode, boolean iwsType) {
		final String wsURL = baseURL + "recallwork/" + woNo;

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

}
