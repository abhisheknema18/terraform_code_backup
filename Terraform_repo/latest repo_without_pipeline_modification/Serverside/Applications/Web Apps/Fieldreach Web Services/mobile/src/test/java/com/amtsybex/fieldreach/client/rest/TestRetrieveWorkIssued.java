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
import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.XmlMappingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.amtsybex.fieldreach.services.messages.response.WorkIssuedResponse;
import com.amtsybex.fieldreach.services.messages.types.Attachment;
import com.amtsybex.fieldreach.services.messages.types.WorkIssued;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;


public class TestRetrieveWorkIssued extends CommonBase 
{
	
	static Logger log = LoggerFactory.getLogger(TestRetrieveWorkIssued.class.getName());
	

	@Test
	@TestDescription(desc="No date header and valid authorizaton token.")
	@TestLabel(label="Reference: 2801")
	public void test_1() {

		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		retrieveWorkIssued("", "", "", HttpStatus.UNAUTHORIZED, 
				headersVariables, requestHeaders);
	}
	
	
	@Test
	@TestDescription(desc="Invalid header and valid authorizaton token.")
	@TestLabel(label="Reference: 2802")
	public void test_2() {

		String dateHeader = "invalid date";
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		retrieveWorkIssued("", "", "", HttpStatus.UNAUTHORIZED, 
				headersVariables, requestHeaders);
	}
	
	
	@Test
	@TestDescription(desc="Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label="Reference: 2803")
	public void test_3() {

		String dateHeader = getNonRFC2616Date();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		retrieveWorkIssued("", "", "", HttpStatus.UNAUTHORIZED, 
				headersVariables, requestHeaders);
	}
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes in front of the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 2804")
	public void test_4() {

		String dateHeader = getDateAfter(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		retrieveWorkIssued("", "", "", HttpStatus.UNAUTHORIZED, 
				headersVariables, requestHeaders);
			
	}
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes behind the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 2805")
	public void test_5() {

		String dateHeader = getDateBefore(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		retrieveWorkIssued("", "", "", HttpStatus.UNAUTHORIZED, 
				headersVariables, requestHeaders);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and no authorization token.")
	@TestLabel(label="Reference: 2806")
	public void test_6() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ": : ");
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		retrieveWorkIssued("", "", "", HttpStatus.UNAUTHORIZED, 
				headersVariables, requestHeaders);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and malformed authorization token.")
	@TestLabel(label="Reference: 2807")
	public void test_7() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode + ":"
				+ authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		retrieveWorkIssued("", "", "", HttpStatus.UNAUTHORIZED, 
				headersVariables, requestHeaders);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token.<br>" +
						  "Invalid user specified in the authorization header.")
	@TestLabel(label="Reference: 2808")
	public void test_8() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		retrieveWorkIssued("", "", "", HttpStatus.UNAUTHORIZED, 
				headersVariables, requestHeaders);
	}
	

	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "signed with incorrect password.")
	@TestLabel(label="Reference: 2809")
	public void test_9() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		retrieveWorkIssued("", "", "", HttpStatus.UNAUTHORIZED, 
				headersVariables, requestHeaders);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "with no parameters passed. HTTP 400 Bad Request Returned.")
	@TestLabel(label="Reference: 2810")
	public void test_10() {
		
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		retrieveWorkIssued(null, null, null, HttpStatus.BAD_REQUEST, 
				headersVariables, requestHeaders);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "with userCode parameter only passed. HTTP 400 Bad Request Returned.")
	@TestLabel(label="Reference: 2811")
	public void test_11() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		retrieveWorkIssued(super.workissuedUserCode, null, null, HttpStatus.BAD_REQUEST, 
				headersVariables, requestHeaders);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "with workGroupCode parameter only passed. HTTP 400 Bad Request Returned.")
	@TestLabel(label="Reference: 2812")
	public void test_12() {
		
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		retrieveWorkIssued(null, super.workIssuedWorkGroupCode, null, HttpStatus.BAD_REQUEST, 
				headersVariables, requestHeaders);
			
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "with workAllocationMode parameter only passed. HTTP 400 Bad Request Returned.")
	@TestLabel(label="Reference: 2813")
	public void test_13() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		retrieveWorkIssued(null, null, "U", HttpStatus.BAD_REQUEST, 
				headersVariables, requestHeaders);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "with userCode and workAllocationMode parameters passed." +
						  " HTTP 400 Bad Request Returned.")
	@TestLabel(label="Reference: 2814")
	public void test_14() {
		
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		retrieveWorkIssued(super.workissuedUserCode, null, "U", HttpStatus.BAD_REQUEST, 
				headersVariables, requestHeaders);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
			  "with workgroupCode and workAllocationMode parameters passed." +
			  " HTTP 400 Bad Request Returned.")
	@TestLabel(label="Reference: 2815")
	public void test_15() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		retrieveWorkIssued(null, super.workIssuedWorkGroupCode, "U", HttpStatus.BAD_REQUEST, 
				headersVariables, requestHeaders);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
			  "with all parameters passed, workAllocationMode U. Ensure request is a success, no " +
			  " exceptons thrown and number of records returned is as expected.")
	@TestLabel(label="Reference: 2816")
	public void test_16() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(super.workissuedUserCode,super.workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		WorkIssuedResponse resp = retrieveWorkIssued(super.workissuedUserCode, super.workIssuedWorkGroupCode, "U", HttpStatus.OK, 
				headersVariables, requestHeaders);
		
		
		//Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), 
				resp.isSuccess());
		
		//Check no exception message returned
		assertTrue("Unexpected exception returned: " + resp.getError().getErrorCode(), 
				resp.getError().getErrorCode() == null);
		
		//Check expected number of records returned.
		assertTrue("unexpected number of records returned from the WorkIssued table. Expected: " + super.recordsExpectedU +
				"<br> Recieved: " + resp.getWorkIssuedList().size(),
				resp.getWorkIssuedList().size() == super.recordsExpectedU);

	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
			  "with all parameters passed, workAllocationMode W. Ensure request is a success, no " +
			  " exceptons thrown and number of records returned is as expected.")
	@TestLabel(label="Reference: 2817")
	public void test_17() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(super.workissuedUserCode,super.workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		WorkIssuedResponse resp = retrieveWorkIssued(super.workissuedUserCode, super.workIssuedWorkGroupCode, "W", HttpStatus.OK, 
				headersVariables, requestHeaders);
		
		
		//Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), 
				resp.isSuccess());
		
		//Check no exception message returned
		assertTrue("Unexpected exception returned: " + resp.getError().getErrorCode(), 
				resp.getError().getErrorCode() == null);
		
		//Check expected number of records returned.
		assertTrue("unexpected number of records returned from the WorkIssued table. Expected: " + super.recordsExpectedW +
				"<br> Recieved: " + resp.getWorkIssuedList().size(),
				resp.getWorkIssuedList().size() == super.recordsExpectedW);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
			  "with all parameters passed, workAllocationMode MW. Ensure request is a success, no " +
			  " exceptons thrown and number of records returned is as expected.")
	@TestLabel(label="Reference: 2818")
	public void test_18() {
		
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(super.workissuedUserCode,super.workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		WorkIssuedResponse resp = retrieveWorkIssued(super.workissuedUserCode, super.workIssuedWorkGroupCode, "MW", HttpStatus.OK, 
				headersVariables, requestHeaders);
		
		
		//Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), 
				resp.isSuccess());
		
		//Check no exception message returned
		assertTrue("Unexpected exception returned: " + resp.getError().getErrorCode(), 
				resp.getError().getErrorCode() == null);
		
		//Check expected number of records returned.
		assertTrue("unexpected number of records returned from the WorkIssued table. Expected: " + super.recordsExpectedMW +
				"<br> Recieved: " + resp.getWorkIssuedList().size(),
				resp.getWorkIssuedList().size() == super.recordsExpectedMW);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
			"with all parameters passed except optional workAllocationMode. " +
			"Ensure request is a success, no exceptons thrown and number of records returned " +
			"is as expected.")
	@TestLabel(label="Reference: 2819")
	public void test_19() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(super.workissuedUserCode,super.workissuedUserPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		WorkIssuedResponse resp = retrieveWorkIssued(super.workissuedUserCode, super.workIssuedWorkGroupCode, null, HttpStatus.OK, 
				headersVariables, requestHeaders);
		
		
		//Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), 
				resp.isSuccess());
		
		//Check no exception message returned
		assertTrue("Unexpected exception returned: " + resp.getError().getErrorCode(), 
				resp.getError().getErrorCode() == null);
		
		//Check expected number of records returned.
		assertTrue("unexpected number of records returned from the WorkIssued table. Expected: " + super.recordsExpectedU +
				"<br> Recieved: " + resp.getWorkIssuedList().size(),
				resp.getWorkIssuedList().size() == super.recordsExpectedU);

	}
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token but got a user other than the one requesting " +
			"BadRequestException should be returned")
	@TestLabel(label="Reference: 2820")
	public void test_20() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(super.unrevokedUserCode,super.unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		WorkIssuedResponse resp = retrieveWorkIssued(super.workissuedUserCode, super.workIssuedWorkGroupCode, null, HttpStatus.BAD_REQUEST, 
				headersVariables, requestHeaders);

	}
	
	
	@Test
	@TestDescription(desc="Integration call to getWorkList. Valid date header and valid authorization token " +
			"with all parameters. search on user with same workgroup with available access. " +
			"Ensure request is a success, no exceptons thrown and number of records returned " +
			"is as expected.")
	@TestLabel(label="Reference: 2821")
	public void test_21() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(userInPrimaryAndSecondaryCode, "sybex1", dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		WorkIssuedResponse resp = this.retrieveWorkIssuedIWS(super.workissuedUserCode, null, null, HttpStatus.OK, 
				headersVariables, requestHeaders);
		
		
		//Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), 
				resp.isSuccess());
		
		//Check no exception message returned
		assertTrue("Unexpected exception returned: " + resp.getError().getErrorCode(), 
				resp.getError().getErrorCode() == null);
		
		//Check expected number of records returned.
		assertTrue("unexpected number of records returned from the WorkIssued table. Expected: " + super.recordsExpectedU +
				"<br> Recieved: " + resp.getWorkIssuedList().size(),
				resp.getWorkIssuedList().size() == super.recordsExpectedU);

	}
	
	
	@Test
	@TestDescription(desc="Integration call to getWorkList. Valid date header and valid authorization token " +
			"with all parameters. search on workgroup with available access. " +
			"Ensure request is a success, no exceptons thrown and number of records returned " +
			"is as expected.")
	@TestLabel(label="Reference: 2822")
	public void test_22() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(userInPrimaryAndSecondaryCode, "sybex1", dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		WorkIssuedResponse resp = this.retrieveWorkIssuedIWS(null, super.workIssuedWorkGroupCode, null, HttpStatus.OK, 
				headersVariables, requestHeaders);
		
		
		//Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), 
				resp.isSuccess());
		
		//Check no exception message returned
		assertTrue("Unexpected exception returned: " + resp.getError().getErrorCode(), 
				resp.getError().getErrorCode() == null);
		
		//Check expected number of records returned.
		assertTrue("unexpected number of records returned from the WorkIssued table. Expected: " + super.recordsExpectedW +
				"<br> Recieved: " + resp.getWorkIssuedList().size(),
				resp.getWorkIssuedList().size() == super.recordsExpectedW);

	}
	
	@Test
	@TestDescription(desc="Integration call to getWorkList. Valid date header and valid authorization token " +
			"with all parameters. search on user and workgroup with available access. should return user results not workgroup. " +
			"Ensure request is a success, no exceptons thrown and number of records returned " +
			"is as expected.")
	@TestLabel(label="Reference: 2823")
	public void test_23() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(userInPrimaryAndSecondaryCode, "sybex1", dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		WorkIssuedResponse resp = this.retrieveWorkIssuedIWS(null, super.workIssuedWorkGroupCode, null, HttpStatus.OK, 
				headersVariables, requestHeaders);
		
		
		//Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), 
				resp.isSuccess());
		
		//Check no exception message returned
		assertTrue("Unexpected exception returned: " + resp.getError().getErrorCode(), 
				resp.getError().getErrorCode() == null);
		
		//Check expected number of records returned.
		assertTrue("unexpected number of records returned from the WorkIssued table. Expected: " + super.recordsExpectedW +
				"<br> Recieved: " + resp.getWorkIssuedList().size(),
				resp.getWorkIssuedList().size() == super.recordsExpectedW);

	}
	
	
	
	@Test
	@TestDescription(desc="Integration call to getWorkList. Valid date header and valid authorization token " +
			"with all parameters. search on unknown workgroup, sys user access to all workgroups includes unknown" +
			" Ensure request is a success." +
			"is as expected.")
	@TestLabel(label="Reference: 2824")
	public void test_25() {

		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getIWSAuthorisationHeaderValue(userInPrimaryAndSecondaryCode, "sybex1", dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		WorkIssuedResponse resp = this.retrieveWorkIssuedIWS(null, "WGCODENOTINLIST", null, HttpStatus.OK, 
				headersVariables, requestHeaders);
		
		//Ensure response was a success
		assertTrue("Response was not a success. Error Code: " + resp.getError().getErrorCode(), 
						resp.isSuccess());

	}
	
	/**
	 * Method to call the workissued work web service

	 */
	protected WorkIssuedResponse retrieveWorkIssuedIWS(String usercode, String wgCode, String workAllocationMode,
			HttpStatus expectedStatusCode, Map<String, String> headersVariables,
			HttpHeaders requestHeaders) {
		
		log.debug(">>> retrieveWorkIssued usercode= " + usercode +
				" wgCode= " + wgCode + " workAllocationMode= " + workAllocationMode);
		
		
		final String wsURL = baseURL + "getworklist";
		
			
		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		WorkIssuedResponse resp = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try 
		{
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);
			
			if (usercode!= null && wgCode!= null)
			{
				restCall.append("?userCode=" + usercode +
						"&workgroupCode=" + wgCode);
			}
			else
			{
				if(usercode!= null)
					restCall.append("?userCode=" + usercode);
				if (wgCode != null)
					restCall.append("?workgroupCode=" + wgCode);
			}
			
			if(workAllocationMode!= null && (usercode== null && wgCode == null))
				restCall.append("?workAllocationMode=" + workAllocationMode);
			else if(workAllocationMode!= null)
				restCall.append("&workAllocationMode=" + workAllocationMode);
			
						
			
			//
			// Request Headers
			//
			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET,requestEntity,
					String.class, headersVariables);

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
				
				xstream.alias("WorkIssuedResponse",
						WorkIssuedResponse.class);
				xstream.alias("WorkIssued", WorkIssued.class);
				xstream.alias("Attachment", Attachment.class);
				resp = (WorkIssuedResponse) xstream.fromXML(results);
				
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

		log.debug("<<< retrieveWorkIssued");
		
		return resp;
	}
	/**
	 * Method to call the workissued work web service

	 */
	protected WorkIssuedResponse retrieveWorkIssued(String usercode, String wgCode, String workAllocationMode,
			HttpStatus expectedStatusCode, Map<String, String> headersVariables,
			HttpHeaders requestHeaders) {
		
		log.debug(">>> retrieveWorkIssued usercode= " + usercode +
				" wgCode= " + wgCode + " workAllocationMode= " + workAllocationMode);
		
		
		final String wsURL = baseURL + "workissued";
		
			
		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		WorkIssuedResponse resp = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try 
		{
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);
			
			if (usercode!= null && wgCode!= null)
			{
				restCall.append("?userCode=" + usercode +
						"&workgroupCode=" + wgCode);
			}
			else
			{
				if(usercode!= null)
					restCall.append("?userCode=" + usercode);
				if (wgCode != null)
					restCall.append("?workgroupCode=" + wgCode);
			}
			
			if(workAllocationMode!= null && (usercode== null && wgCode == null))
				restCall.append("?workAllocationMode=" + workAllocationMode);
			else if(workAllocationMode!= null)
				restCall.append("&workAllocationMode=" + workAllocationMode);
			
						
			
			//
			// Request Headers
			//
			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET,requestEntity,
					String.class, headersVariables);

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
				
				xstream.alias("WorkIssuedResponse",
						WorkIssuedResponse.class);
				xstream.alias("WorkIssued", WorkIssued.class);
				xstream.alias("Attachment", Attachment.class);
				resp = (WorkIssuedResponse) xstream.fromXML(results);
				
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

		log.debug("<<< retrieveWorkIssued");
		
		return resp;
	}
}
