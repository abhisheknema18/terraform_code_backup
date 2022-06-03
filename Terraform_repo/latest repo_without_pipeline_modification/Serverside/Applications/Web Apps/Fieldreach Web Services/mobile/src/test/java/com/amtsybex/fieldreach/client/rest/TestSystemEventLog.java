package com.amtsybex.fieldreach.client.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.slf4j.LoggerFactory; 
import org.slf4j.Logger;  
import org.junit.Test;
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

public class TestSystemEventLog extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestSystemEventLog.class.getName());
	

	@Test
	@TestDescription(desc = "Call SystemEventLog without a body. a BadRequestException should be thrown")
	@TestLabel(label = "Reference: 4301")
	public void test_1() {

		systemEventLog(null, HttpStatus.BAD_REQUEST, true);

	}
	
	@Test
	@TestDescription(desc = "Call SystemEventLog with a body, but with an FWS authorization header. an UnauthorizedException should be thrown")
	@TestLabel(label = "Reference: 4302")
	public void test_2() {

		String body = "<SystemEvent>" + 
				"  <eventDate>20180101</eventDate>" + 
				"  <eventTime>010101</eventTime >" + 
				"  <eventCategory>EXCEPTION</eventCategory >" + 
				"  <eventType>ERROR</eventType >" + 
				"  <eventSummary>TEST EXCEPTION</eventSummary >" + 
				"  <eventDesc>TEST EXCEPTION DETAIL</eventDesc >" + 
				"  <sourceSystem>FIELDREACH</sourceSystem >" + 
				"  <application>FWOM</application >" + 
				"  <severity>3</severity >" + 
				"  <userCode>FRADM</userCode >" + 
				"  <errorCode>1</errorCode >" + 
				"  <workOrderNo>20180101</workOrderNo>" + 
				"  <districtCode>NA</districtCode>" + 
				"</SystemEvent>";
		
		systemEventLog(body, HttpStatus.UNAUTHORIZED, false);

	}
	
	@Test
	@TestDescription(desc = "Call SystemEventLog with a body, with an IWS authorization header. The request should return successful without errors")
	@TestLabel(label = "Reference: 4303")
	public void test_3() {

		String body = "<SystemEvent>" + 
				"  <eventDate>20180101</eventDate>" + 
				"  <eventTime>010101</eventTime >" + 
				"  <eventCategory>EXCEPTION</eventCategory >" + 
				"  <eventType>ERROR</eventType >" + 
				"  <eventSummary>TEST EXCEPTION</eventSummary >" + 
				"  <eventDesc>TEST EXCEPTION DETAIL</eventDesc >" + 
				"  <sourceSystem>FIELDREACH</sourceSystem >" + 
				"  <application>FWOM</application >" + 
				"  <severity>3</severity >" + 
				"  <userCode>FRADM</userCode >" + 
				"  <errorCode>1</errorCode >" + 
				"  <workOrderNo>20180101</workOrderNo>" + 
				"  <districtCode>NA</districtCode>" + 
				"</SystemEvent>";
		
		CallResponse resp = systemEventLog(body, HttpStatus.OK, true);
		
		assertTrue("Request was not sucessful.", resp.isSuccess());

	}
	
	
	@Test
	@TestDescription(desc = "Call updateServiceStatus with an appCode that is not configured, with an IWS authorization header. The request should return an AppCodeNotConfiguredException")
	@TestLabel(label = "Reference: 4304")
	public void test_4() {
		
		CallResponse resp = this.callUpdateServiceCheckpoint("ASDF",  HttpStatus.OK, true);
		
		assertEquals("Request was not sucessful. Expected app code not found exception", resp.getError().getErrorCode(), Utils.SERVICE_CODE_NOT_FOUND_EXCEPTION);

	}
	
	@Test
	@TestDescription(desc = "Call updateServiceStatus with an appCode that is configured but monitoring disabled, with an IWS authorization header. The request should return an AppCodeMonitorDisabledException")
	@TestLabel(label = "Reference: 4305")
	public void test_5() {
		
		CallResponse resp = this.callUpdateServiceCheckpoint("FWOM",  HttpStatus.OK, true);
		
		assertEquals("Request was not sucessful. Expected app code monitoring disabled exception", resp.getError().getErrorCode(), Utils.SERVICE_CODE_MONITOR_DISABLED_EXCEPTION);

	}
	
	@Test
	@TestDescription(desc = "Call updateServiceStatus with an appCode that is configured and monitoring enabled, with an IWS authorization header. The request should be a success")
	@TestLabel(label = "Reference: 4306")
	public void test_6() {
		
		CallResponse resp = this.callUpdateServiceCheckpoint("FWS",  HttpStatus.OK, true);
		
		assertTrue("Request was not sucessful.", resp.isSuccess());

	}
	
	
	protected CallResponse systemEventLog(String body, HttpStatus expectedStatusCode, boolean iwsType)
	{
		
		log.debug(">>> systemEventLog body=xxxx");
		
		final String wsURL = baseURL + "addSystemEvent/";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		CallResponse resp = null;

		
		try 
		{
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);
			
			//
			// Request Headers
			HttpHeaders requestHeaders = new HttpHeaders();
			String dateHeader = getDateHeader();
			log.debug("Date header=" + dateHeader);
			requestHeaders.set("Date", dateHeader);
			requestHeaders.set("x-fws-deviceid", deviceid);

			requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
			
			if(iwsType) {
				requestHeaders.set("Authorization", getIWSAuthorisationHeaderValue(userInPrimaryAndSecondaryCode, "sybex1", dateHeader));
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
					restCall.toString(), HttpMethod.POST, requestEntity,
					String.class);


			String results = resultsExchange.getBody();

			log.debug("systemEventLog results : " + results);
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

		log.debug("<<< systemEventLog");
		
		return resp;
	}
	
	protected CallResponse callUpdateServiceCheckpoint(String appCode, HttpStatus expectedStatusCode, boolean iwsType)
	{
		
		log.debug(">>> systemEventLog body=xxxx");
		
		final String wsURL = baseURL + "updateServiceCheckpoint/" + appCode;

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		CallResponse resp = null;

		
		try 
		{
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);
			
			//
			// Request Headers
			HttpHeaders requestHeaders = new HttpHeaders();
			String dateHeader = getDateHeader();
			log.debug("Date header=" + dateHeader);
			requestHeaders.set("Date", dateHeader);
			requestHeaders.set("x-fws-deviceid", deviceid);

			if(applicationIdentifier != null) {
				requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
			}

			if(iwsType) {
				requestHeaders.set("Authorization", getIWSAuthorisationHeaderValue(userInPrimaryAndSecondaryCode, "sybex1", dateHeader));
			}else {
				requestHeaders.set("Authorization", getAuthorisationHeaderValue(unrevokedUserCode, unrevokedPassword, dateHeader));
			}
			
			requestHeaders.set("x-fws-appCode", "FWOM");
			

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					null, requestHeaders);

			//
			// Make REST call and retrieve the results
			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.PUT, requestEntity,
					String.class);


			String results = resultsExchange.getBody();

			log.debug("updateServiceStatus results : " + results);
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

		log.debug("<<< systemEventLog");
		
		return resp;
	}
	
	
}
