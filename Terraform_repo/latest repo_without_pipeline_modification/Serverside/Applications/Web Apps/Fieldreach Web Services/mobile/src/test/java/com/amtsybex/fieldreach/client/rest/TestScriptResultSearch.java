/**
 * Author:  T Murray
 * Date:    26/03/2013
 * Project: FDE020
 * 
 * Copyright AMT-Sybex 2012
 */
package com.amtsybex.fieldreach.client.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

import com.amtsybex.fieldreach.services.messages.response.ScriptResultsSearchResponse;
import com.amtsybex.fieldreach.services.messages.types.ScriptResult;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.* ;
import static org.junit.Assert.* ;


public class TestScriptResultSearch extends CommonBase 
{

	static Logger log = LoggerFactory.getLogger(TestScriptResultSearch.class.getName());

	@Test
	@TestDescription(desc="No date header and valid authorizaton token.")
	@TestLabel(label="Reference: 3101")
	public void test_1() 
	{
		
		HttpHeaders requestHeaders = new HttpHeaders();
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", "IDONTEXIST");
		params.put("fromDate",  null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		scriptResultSearch(requestHeaders, params, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Invalid header and valid authorizaton token.")
	@TestLabel(label="Reference: 3102")
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
		
		
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", "IDONTEXIST");
		params.put("fromDate",  null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		scriptResultSearch(requestHeaders, params, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label="Reference: 3103")
	public void test_3() 
	{
		String dateHeader = getNonRFC2616Date();
	
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		
		
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", "IDONTEXIST");
		params.put("fromDate",  null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		scriptResultSearch(requestHeaders, params, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes in front of the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 3104")
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
		
		
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", "IDONTEXIST");
		params.put("fromDate",  null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		scriptResultSearch(requestHeaders, params, HttpStatus.UNAUTHORIZED);	
	}
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes behind the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 3105")
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
		
		
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", "IDONTEXIST");
		params.put("fromDate",  null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		scriptResultSearch(requestHeaders, params, HttpStatus.UNAUTHORIZED);	
	}
	
	@Test
	@TestDescription(desc="Valid date header and no authorization token.")
	@TestLabel(label="Reference: 3106")
	public void test_6() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ":"
				+ null);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", "IDONTEXIST");
		params.put("fromDate",  null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		scriptResultSearch(requestHeaders, params, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Valid date header and malformed authorization token.")
	@TestLabel(label="Reference: 3107")
	public void test_7() 
	{
		
		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode + ":"
				+ authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", "IDONTEXIST");
		params.put("fromDate",  null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		scriptResultSearch(requestHeaders, params, HttpStatus.UNAUTHORIZED);
	}
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token.<br>" +
						  "Invalid user specified in the authorization header.")
	@TestLabel(label="Reference: 3108")
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
		
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", "IDONTEXIST");
		params.put("fromDate",  null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
			
		scriptResultSearch(requestHeaders, params, HttpStatus.UNAUTHORIZED);
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
		
		Map<String, String> params = new HashMap<String,String>();
				
		params.put("equipNo", "IDONTEXIST");
		params.put("fromDate",  null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		scriptResultSearch(requestHeaders, params, HttpStatus.UNAUTHORIZED);
	}

	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Do not supply any search parameters." +
			"Ensure response returned is HTTP 400 Bad Request.")
	@TestLabel(label="Reference: 3110")
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
		
	
		Map<String, String> params = new HashMap<String,String>();
				
		params.put("equipNo", null);
		params.put("fromDate",  null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		
		scriptResultSearch(requestHeaders, params, HttpStatus.BAD_REQUEST);
				
	}
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply fromDate parameter of XXX." +
			" Ensure response returned is HTTP 400 Bad Request.")
	@TestLabel(label="Reference: 3111")
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
		
	
		Map<String, String> params = new HashMap<String,String>();
				
		params.put("equipNo", null);
		params.put("fromDate",  "XXX");
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		
		scriptResultSearch(requestHeaders, params, HttpStatus.BAD_REQUEST);
		
	}
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply toDate paramter of XXX." +
			" Ensure response returned is HTTP 400 Bad Request.")
	@TestLabel(label="Reference: 3112")
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
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", "XXX");
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		scriptResultSearch(requestHeaders, params, HttpStatus.BAD_REQUEST);
		
	}
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply an equipNo parameter "+
			" that exists in the returnedscripts table. Verify response is HTTP 200 OK and no results " +
			"are returned in the response.")
	@TestLabel(label="Reference: 3113")
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
		
	
		Map<String, String> params = new HashMap<String,String>();	
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3113) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3113));
		
	}
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply only a "+
			"fromDate parameter. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3114")
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
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3114) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3114));
		
	}
	

	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply only a "+
			"toDate parameter. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3115")
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
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3115) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3115));
	}


	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply only a "+
			"scriptCode parameter with a single value. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3116")
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
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3116) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3116));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply only a "+
			"scriptCode parameter with multiple values. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3117")
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
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3117) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3117));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply only a "+
			"scriptCode parameter with a single value. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3118")
	public void test_18() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3118) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3118));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply only a "+
			"scriptCode parameter with multiple values. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3119")
	public void test_19() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3119) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3119));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply only a "+
			"userCode parameter with a single value. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3120")
	public void test_20() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3120) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3120));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply only a "+
			"userCode parameter with multiple values. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3121")
	public void test_21() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3121) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3121));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply only a "+
			"workgroupCode parameter with a single value. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3122")
	public void test_22() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3122) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3122));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply only a "+
			"workgroupCode parameter with multiple values. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3123")
	public void test_23() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3123) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3123));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo and fromDate parameters. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3124")
	public void test_24() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3124) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3124));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo and fromDate parameters. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3125")
	public void test_25() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3125) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3125));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo and scriptCode parameters. Ensure the scriptCode parameter contains a single value. " +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3126")
	public void test_26() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3126) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3126));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo and scriptCode parameters. Ensure the scriptCode parameter contains multiple values. " +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3127")
	public void test_27() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3127) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3127));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo and resultStatus parameters. Ensure the resultStatus parameter contains a single value. " +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3128")
	public void test_28() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3128) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3128));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo and resultStatus parameters. Ensure the resultStatus parameter contains multiple values. " +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3129")
	public void test_29() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3129) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3129));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo and userCode parameters. Ensure the userCode parameter contains a single value. " +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3130")
	public void test_30() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3130) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3130));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo and userCode parameters. Ensure the userCode parameter contains multiple values. " +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3131")
	public void test_31() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3131) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3131));
	}
	
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo and workgroupCode parameters. Ensure the workgroupCode parameter contains a single value. " +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3132")
	public void test_32() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3132) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3132));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo and workgroupCode parameters. Ensure the workgroupCode parameter contains multiple values. " +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3133")
	public void test_33() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3133) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3133));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate and toDate parameters. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3134")
	public void test_34() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3134) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3134));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate and scriptCode parameters. Ensure scriptCode is a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3135")
	public void test_35() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3135) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3135));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate and scriptCode parameters. Ensure scriptCode contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3136")
	public void test_36() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3136) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3136));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate and resultStatus parameters. Ensure resultStatus is a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3137")
	public void test_37() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3137) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3137));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate and resultStatus parameters. Ensure resultStatus contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3138")
	public void test_38() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3138) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3138));
	}
	
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate and resultStatus parameters. Ensure resultStatus is a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3139")
	public void test_39() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3139) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3139));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate and resultStatus parameters. Ensure resultStatus contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3140")
	public void test_40() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3140) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3140));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate and scriptCode parameters. Ensure scriptCode is a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3141")
	public void test_41() 
	{
		String dateHeader = getDateHeader();;
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3141) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3141));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate and scriptCode parameters. Ensure scriptCode contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3142")
	public void test_42() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3142) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3142));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate and resultStatus parameters. Ensure resultStatus is a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3143")
	public void test_43() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3143) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3143));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate and resultStatus parameters. Ensure resultStatus contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3144")
	public void test_44() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3144) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3144));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate and userCode parameters. Ensure userCode is a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3145")
	public void test_45() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3145) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3145));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate and userCode parameters. Ensure userCode contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3146")
	public void test_46() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3146) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3146));
	}
	

	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate and workgroupCode parameters. Ensure workgroupCode is a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3147")
	public void test_47() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3147) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3147));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate and workgroupCode parameters. Ensure workgroupCode contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3148")
	public void test_48() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3148) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3148));
	}
	
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"scriptCode and resultStatus parameters. Ensure each parameter is a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3149")
	public void test_49() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3149) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3149));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"scriptCode and resultStatus parameters. Ensure each parameter contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3150")
	public void test_50() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3150) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3150));
	}
	

	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"scriptCode and workgroupCode parameters. Ensure each parameter is a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3151")
	public void test_51() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3151) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3151));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"scriptCode and workgroupCode parameters. Ensure each parameter contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3152")
	public void test_52() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3152) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3152));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"resultStatus and userCode parameters. Ensure each parameter is a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3153")
	public void test_53() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3153) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3153));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"resultStatus and userCode parameters. Ensure each parameter contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3154")
	public void test_54() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3154) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3154));
	}
	
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"resultStatus and workgroupCode parameters. Ensure each parameter is a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3155")
	public void test_55() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3155) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3155));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"resultStatus and workgroupCode parameters. Ensure each parameter contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3156")
	public void test_56() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3156) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3156));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate and toDate parameters. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3157")
	public void test_57() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3157) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3157));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate and scriptCode parameters. " +
			"Ensure scriptCode contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3158")
	public void test_58() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3158) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3158));
	}
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate and scriptCode parameters. " +
			"Ensure scriptCode contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3159")
	public void test_59() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3159) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3159));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate and resultStatus parameters. " +
			"Ensure resultStatus contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3160")
	public void test_60() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3160) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3160));
	}
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate and resultStatus parameters. " +
			"Ensure resultStatus contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3161")
	public void test_61() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3161) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3161));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate and userCode parameters. " +
			"Ensure userCode contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3162")
	public void test_62() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3162) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3162));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate and userCode parameters. " +
			"Ensure userCode contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3163")
	public void test_63() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3163) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3163));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate and workgroupCode parameters. " +
			"Ensure workgroupCode contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3164")
	public void test_64() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3164) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3164));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate and workgroupCode parameters. " +
			"Ensure workgroupCode contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3165")
	public void test_65() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3165) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3165));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate and scriptCode parameters. " +
			"Ensure scriptCode contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3166")
	public void test_66() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3166) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3166));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate and scriptCode parameters. " +
			"Ensure scriptCode contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3167")
	public void test_67() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3167) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3167));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate and resultStatus parameters. " +
			"Ensure resultStatus contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3168")
	public void test_68() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3168) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3168));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate and resultStatus parameters. " +
			"Ensure resultStatus contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3169")
	public void test_69() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3169) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3169));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate and userCode parameters. " +
			"Ensure userCode contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3170")
	public void test_70() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3170) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3170));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate and userCode parameters. " +
			"Ensure userCode contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3171")
	public void test_71() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3171) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3171));
	}
	
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate and workgroupCode parameters. " +
			"Ensure workgroupCode contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3172")
	public void test_72() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3172) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3172));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate and workgroupCode parameters. " +
			"Ensure workgroupCode contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3173")
	public void test_73() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3173) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3173));
	}
	

	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"scriptCode, resultStatus and userCode parameters. " +
			"Ensure each parameter contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3174")
	public void test_74() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3174) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3174));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"scriptCode, resultStatus and userCode parameters. " +
			"Ensure each parameter contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3175")
	public void test_75() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3175) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3175));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"scriptCode, resultStatus and workgroupCode parameters. " +
			"Ensure each parameter contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3176")
	public void test_76() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3176) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3176));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"scriptCode, resultStatus and workgroupCode parameters. " +
			"Ensure each parameter contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3177")
	public void test_77() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3177) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3177));
	}
	
	
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"userCode, resultStatus and workgroupCode parameters. " +
			"Ensure each parameter contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3178")
	public void test_78() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3178) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3178));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"userCode, resultStatus and workgroupCode parameters. " +
			"Ensure each parameter contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3179")
	public void test_79() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", null);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3179) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3179));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate and scriptCode parameters. " +
			"Ensure scriptCode contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3180")
	public void test_80() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3180) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3180));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate and scriptCode parameters. " +
			"Ensure scriptCode contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3181")
	public void test_81() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3181) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3181));
	}
	
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate and resultStatus parameters. " +
			"Ensure resultStatus contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3182")
	public void test_82() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3182) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3182));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate and resultStatus parameters. " +
			"Ensure resultStatus contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3183")
	public void test_83() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3183) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3183));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate and userCode parameters. " +
			"Ensure userCode contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3184")
	public void test_84() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3184) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3184));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate and userCode parameters. " +
			"Ensure userCode contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3185")
	public void test_85() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3185) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3185));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate and workgroupCode parameters. " +
			"Ensure workgroupCode contains a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3186")
	public void test_86() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3186) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3186));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate and workgroupCode parameters. " +
			"Ensure workgroupCode contains multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3187")
	public void test_87() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3187) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3187));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate, scriptCode, resultStatus and workgroupCode parameters. " +
			"Ensure scriptCode, resultStatus and workgroupCode contain a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3188")
	public void test_88() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3188) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3188));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate, scriptCode, resultStatus and workgroupCode parameters. " +
			"Ensure scriptCode, resultStatus and workgroupCode contain multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3189")
	public void test_89() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3189) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3189));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate, userCode, resultStatus and workgroupCode parameters. " +
			"Ensure userCode, resultStatus and workgroupCode contain a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3190")
	public void test_90() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3190) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3190));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate, userCode, resultStatus and workgroupCode parameters. " +
			"Ensure userCode, resultStatus and workgroupCode contain multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3191")
	public void test_91() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", null);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3191) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3191));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate, scriptCode and resultStatus parameters. " +
			"Ensure equipNo, fromDate, toDate, scriptCode and resultStatus contain a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3192")
	public void test_92() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3192) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3192));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate, scriptCode and resultStatus parameters. " +
			"Ensure equipNo, fromDate, toDate, scriptCode and resultStatus contain multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3193")
	public void test_93() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3193) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3193));
	}
	

	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate, scriptCode and userCode parameters. " +
			"Ensure equipNo, fromDate, toDate, scriptCode and userCode contain a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3194")
	public void test_94() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3194) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3194));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate, scriptCode and userCode parameters. " +
			"Ensure equipNo, fromDate, toDate, scriptCode and userCode contain multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3195")
	public void test_95() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", null);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3195) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3195));
	}
	
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate, scriptCode and workgroupCode parameters. " +
			"Ensure equipNo, fromDate, toDate, scriptCode and workgroupCode contain a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3196")
	public void test_96() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3196) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3196));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate, scriptCode and workgroupCode parameters. " +
			"Ensure equipNo, fromDate, toDate, scriptCode and workgroupCode contain multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3197")
	public void test_97() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", null);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3197) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3197));
	}

	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate, scriptCode, resultStatus and userCode parameters. " +
			"Ensure fromDate, toDate, scriptCode, resultStatus and userCode contain a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3198")
	public void test_98() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3198) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3198));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate, scriptCode, resultStatus and userCode parameters. " +
			"Ensure fromDate, toDate, scriptCode, resultStatus and userCode contain multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 3199")
	public void test_99() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(3199) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(3199));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate, scriptCode, resultStatus and workgroupCode parameters. " +
			"Ensure fromDate, toDate, scriptCode, resultStatus and workgroupCode contain a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31100")
	public void test_100() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31100) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31100));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate, scriptCode, resultStatus and workgroupCode parameters. " +
			"Ensure fromDate, toDate, scriptCode, resultStatus and workgroupCode contain multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31101")
	public void test_101() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31101) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31101));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate, scriptCode, resultStatus, userCode and workgroupCode parameters. " +
			"Ensure toDate, scriptCode, resultStatus, userCode and workgroupCode contain a single value." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31102")
	public void test_102() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31102) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31102));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"toDate, scriptCode, resultStatus, userCode and workgroupCode parameters. " +
			"Ensure toDate, scriptCode, resultStatus, userCode and workgroupCode contain multiple values." +
			"Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31103")
	public void test_103() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", null);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31103) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31103));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate, scriptCode, resultStatus, userCode and workgroupCode parameters. " +
			"Ensure fromDate, toDate, scriptCode, resultStatus, userCode and " +
			"workgroupCode contain a single value. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31104")
	public void test_104() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31104) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31104));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"fromDate, toDate, scriptCode, resultStatus, userCode and workgroupCode parameters. " +
			"Ensure fromDate, toDate, scriptCode, resultStatus, userCode and " +
			"workgroupCode contain multiple values. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31105")
	public void test_105() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", null);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31105) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31105));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate, scriptCode, resultStatus and userCode parameters. " +
			"Ensure equipNo, fromDate, toDate, scriptCode, resultStatus and userCode" +
			"contain a single value. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31106")
	public void test_106() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31106) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31106));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate, scriptCode, resultStatus and userCode parameters. " +
			"Ensure equipNo, fromDate, toDate, scriptCode, resultStatus and userCode" +
			"contain multiple values. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31107")
	public void test_107() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", null);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31107) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31107));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate, scriptCode, resultStatus and workgroupCode parameters. " +
			"Ensure equipNo, fromDate, toDate, scriptCode, resultStatus and workgroupCode" +
			"contain a single value. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31108")
	public void test_108() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31108) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31108));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate, scriptCode, resultStatus and workgroupCode parameters. " +
			"Ensure equipNo, fromDate, toDate, scriptCode, resultStatus and workgroupCode" +
			"contain multiple values. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31109")
	public void test_109() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", null);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31109) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31109));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate, scriptCode, resultStatus, userCode and workgroupCode parameters. " +
			"Ensure equipNo, fromDate, toDate, scriptCode, resultStatus, userCode and workgroupCode" +
			"contain a single value. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31110")
	public void test_110() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeSingle);
		params.put("resultStatus", scriptResultSearch_resultStatusSingle);
		params.put("userCode", scriptResultSearch_userCodeSingle);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeSingle);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31110) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31110));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"equipNo, fromDate, toDate, scriptCode, resultStatus, userCode and workgroupCode parameters. " +
			"Ensure equipNo, fromDate, toDate, scriptCode, resultStatus, userCode and workgroupCode" +
			"contain multiple values. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31111")
	public void test_111() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("equipNo", scriptResultSearch_equipNo);
		params.put("fromDate", scriptResultSearch_fromDate);
		params.put("toDate", scriptResultSearch_toDate);
		params.put("scriptCode", scriptResultSearch_scriptCodeMulti);
		params.put("resultStatus", scriptResultSearch_resultStatusMulti);
		params.put("userCode", scriptResultSearch_userCodeMulti);
		params.put("workgroupCode", scriptResultSearch_workgroupCodeMulti);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31111) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31111));
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply the viewId parameter"
			+ "alongside another parameter. Ensure response returned is HTTP 400 Bad Request.")
	@TestLabel(label="Reference: 31112")
	public void test_112() 
	{
		
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("viewId", scriptResultSearch_viewId);
		params.put("equipNo", scriptResultSearch_equipNo);
		
		scriptResultSearch(requestHeaders, params, HttpStatus.BAD_REQUEST);
		
	}
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply the workOrderNo parameter"
			+ "alongside another parameter. Ensure response returned is HTTP 400 Bad Request.")
	@TestLabel(label="Reference: 31113")
	public void test_113() 
	{
		
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("workOrderNo", scriptResultSearch_workOrderNo);
		params.put("equipNo", scriptResultSearch_equipNo);
		
		scriptResultSearch(requestHeaders, params, HttpStatus.BAD_REQUEST);
		
	}
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply the resAssocCodeReturnId parameter"
			+ "alongside another parameter. Ensure response returned is HTTP 400 Bad Request.")
	@TestLabel(label="Reference: 31114")
	public void test_114() 
	{
		
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("resAssocCodeReturnId", scriptResultSearch_resAssocCodeReturnId);
		params.put("equipNo", scriptResultSearch_equipNo);
		
		scriptResultSearch(requestHeaders, params, HttpStatus.BAD_REQUEST);
		
	}
	
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"viewId parameter. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31115")
	public void test_115() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("viewId", scriptResultSearch_viewId);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31115) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31115));
	}
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"workOrderNo parameter. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31116")
	public void test_116() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("workOrderNo", scriptResultSearch_workOrderNo);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31116) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31116));
	}
	
	@Test
	@TestDescription(desc="Supply valid date and authorisation headers. Supply "+
			"resAssocCodeReturnId parameter. Verify response is HTTP 200 OK. " +
			"Check the respsonse returns the expected number of script results.")
	@TestLabel(label="Reference: 31117")
	public void test_117() 
	{
		String dateHeader = getDateHeader();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
	
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("resAssocCodeReturnId", scriptResultSearch_resAssocCodeReturnId);
		
		ScriptResultsSearchResponse response = null;
				
		response = scriptResultSearch(requestHeaders, params, HttpStatus.OK);
		
		
		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
		assertTrue("Unexpected number of script results returned." +
				"<br/><br/><b>Expected:</b> " + scriptResultSearchExpectedResults.get(31117) + 
				"<br><b>Found:</b> " + response.getScriptResultList().size(), 
				response.getScriptResultList().size() == scriptResultSearchExpectedResults.get(31117));
	}
	
	
	public ScriptResultsSearchResponse scriptResultSearch(HttpHeaders requestHeaders, 
			Map<String,String> params, HttpStatus expectedStatusCode)
	{
		final String wsURL = baseURL + "script/result/search";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		ScriptResultsSearchResponse requestResponse = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try 
		{
			//
			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

				
			//
			// Make REST call and retrieve the results
			String requestBody = null;

			
			// build query string
			if (params != null)
			{
				boolean added = false;
				
				for (Entry<String, String> param : params.entrySet())
				{
					if (!added)
					{
						if (param.getValue() != null) 
						{
							restCall.append("?" + param.getKey() + "=" + param.getValue());
							added = true;
						}
					}
					else
					{
						if (param.getValue() != null) 
							restCall.append("&" + param.getKey() + "=" + param.getValue());
					}
				}
			}
			
			Map<String, String> headersVariables = new HashMap<String, String>();
						
			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);


			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET, requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();


			log.debug("scriptResultSearch results : " + results);
			assertTrue("Result is null", results != null);

			
			//
			// Parse XML received from the response into an 
			// GetConfigResponse object
			try 
			{
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("ScriptResultsSearchResponse", ScriptResultsSearchResponse.class);
				xstream.alias("ScriptResult", ScriptResult.class);
				requestResponse = (ScriptResultsSearchResponse) xstream
						.fromXML(results);

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
