/**
 * Author:  C Moore
 * Date:    14/09/2015
 * Project: FDE034
 * 
 * Copyright AMT-Sybex 2015
 * Amendment 
 * Author C Moore
 * Date  23/09/2015
 * Project FDE034
 * 
 */

package com.amtsybex.fieldreach.client.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

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

import com.amtsybex.fieldreach.services.messages.response.ScriptQuestionDefinitionResponse;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class TestGetScriptQuestionDefinition extends CommonBase {
	
	static Logger log = LoggerFactory.getLogger(TestGetScriptQuestionDefinition.class.getName());
	
	@Test
	@TestDescription(desc="No date header and valid authorizaton token.")
	@TestLabel(label="Reference: 3801")
	public void test_1() {
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefValidSeqNo);
		
		HttpHeaders requestHeaders = new HttpHeaders();
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);		
		
	}
	
	
	@Test
	@TestDescription(desc="Invalid header and valid authorizaton token.")
	@TestLabel(label="Reference: 3802")
	public void test_2() 
	{	
		
		String dateHeader = "invalid date";
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefValidSeqNo);
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Non RFC 2616 date header and valid authorizaton token.")
	@TestLabel(label="Reference: 3803")
	public void test_3() 
	{
		String dateHeader = getNonRFC2616Date();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefValidSeqNo);
	
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		
		
		getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

	}

	
	@Test
	@TestDescription(desc="Date header more than 15 minutes in front of the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 3804")
	public void test_4() 
	{
		
		String dateHeader = getDateAfter(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefValidSeqNo);
	
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);	
	}
	
	
	@Test
	@TestDescription(desc="Date header more than 15 minutes behind the " +
						  "server time and valid authorizaton token.")
	@TestLabel(label="Reference: 3805")
	public void test_5() 
	{
		String dateHeader = getDateBefore(1);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefValidSeqNo);
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
				
		getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and no authorization token.")
	@TestLabel(label="Reference: 3806")
	public void test_6() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefValidSeqNo);
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS " + unrevokedUserCode + ":"
				+ null);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and malformed authorization token.")
	@TestLabel(label="Reference: 3807")
	public void test_7() 
	{
		
		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefValidSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-malformed" + unrevokedUserCode + ":"
				+ authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
		
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token.<br>" +
						  "Invalid user specified in the authorization header.")
	@TestLabel(label="Reference: 3808")
	public void test_8() 
	{
		
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefValidSeqNo);
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
			
		getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Valid date header and valid authorization token " +
						  "signed with incorrect password.")
	@TestLabel(label="Reference: 3809")
	public void test_9() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefValidSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	@TestDescription(desc="Attempt to retrieve a script result supplying an invalid scriptId. Check the " +
			"response is HTTP Status code 200 OK.  The success element is false and that a ScriptQuestionDefNotFoundException"
			+ " is returned")
	@TestLabel(label="Reference: 3810")
	public void test_10() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefInvalidScriptId);
		headersVariables.put("seqno", scriptQuestionDefValidSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.QUESTION_DEF_NOT_FOUND_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.QUESTION_DEF_NOT_FOUND_EXCEPTION));
	}
	
	
	@Test
	@TestDescription(desc="Attempt to get question definition using valid Date and Authorization headers. Specify a script id " +
			"that does exist in the database but supply a sequence number that does not exist in the script. Check HTTP status code " + 
			"is 200 OK, the success element is false and that a ScriptQuestionDefNotFoundException is returned")
	@TestLabel(label="Reference: 3811")
	public void test_11() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefInvalidSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.QUESTION_DEF_NOT_FOUND_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.QUESTION_DEF_NOT_FOUND_EXCEPTION));
		
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.12 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for an Attachment question type. Check HTTP status code is 200 OK, the success element is false and that + "
				+ "a ScriptQuestionDefNotSupportedException is returned")
	@TestLabel(label="Reference: 3812")
	public void test_12() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefAttachmentValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefAttachmentSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
		

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION));
	}
	
	
	@Test
	@TestDescription(desc="4.27.13 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for an Bitmap question type. Check HTTP status code is 200 OK, the success element is false and that + "
				+ "a ScriptQuestionDefNotSupportedException is returned")
	@TestLabel(label="Reference: 3813")
	public void test_13() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefBitmapSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
		

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION));
	}
	
	
	@Test
	@TestDescription(desc="4.27.14 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for an Calculation question type. Check HTTP status code is 200 OK, the success element is false and that + "
				+ "a ScriptQuestionDefNotSupportedException is returned")
	@TestLabel(label="Reference: 3814")
	public void test_14() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefCalculationSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
		

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION));
	}
	
	
	@Test
	@TestDescription(desc="4.27.15 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for an Embedded Spreadsheet question type. Check HTTP status code is 200 OK, the success element is false and that + "
				+ "a ScriptQuestionDefNotSupportedException is returned")
	@TestLabel(label="Reference: 3815")
	public void test_15() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefEmbeddedSpreadSheetSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
		

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION));
	}

	
	@Test
	@TestDescription(desc="4.27.16 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for an End of Block Marker question type. Check HTTP status code is 200 OK, the success element is false and that + "
				+ "a ScriptQuestionDefNotSupportedException is returned")
	@TestLabel(label="Reference: 3816")
	public void test_16() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefEBMSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
		

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION));
	}
	
	
	@Test
	@TestDescription(desc="4.27.17 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for an External Application question type. Check HTTP status code is 200 OK, the success element is false and that + "
				+ "a ScriptQuestionDefNotSupportedException is returned")
	@TestLabel(label="Reference: 3817")
	public void test_17() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefExtApplicationSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
		

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION));
	}
	
	
	@Test
	@TestDescription(desc="4.27.18 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for an Heading question type. Check HTTP status code is 200 OK, the success element is false and that + "
				+ "a ScriptQuestionDefNotSupportedException is returned")
	@TestLabel(label="Reference: 3818")
	public void test_18() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefHeadingSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
		

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION));
	}
	
	
	
	@Test
	@TestDescription(desc="4.27.19 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for an Heading question type. Check HTTP status code is 200 OK, the success element is false and that + "
				+ "a ScriptQuestionDefNotSupportedException is returned")
	@TestLabel(label="Reference: 3819")
	public void test_19() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefSignatureSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
		

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION));
	}
	
	
	@Test
	@TestDescription(desc="4.27.20 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for an Video question type. Check HTTP status code is 200 OK, the success element is false and that + "
				+ "a ScriptQuestionDefNotSupportedException is returned")
	@TestLabel(label="Reference: 3820")
	public void test_20() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefVideoSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);
		

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION));
	}
	
	
	@Test
	@TestDescription(desc="4.27.21 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for an Voice question type. Check HTTP status code is 200 OK, the success element is false and that + "
				+ "a ScriptQuestionDefNotSupportedException is returned")
	@TestLabel(label="Reference: 3821")
	public void test_21() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefVoiceSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was sucessful.", !response.isSuccess());
		
		assertTrue("No error codes returned.", response.getError().getErrorCode() != null);
		
		assertTrue("Unexpected Exception returned." +
				"<br/><br/><b>Expected:</b> " + Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION + 
				"<br><b>Found:</b> " + response.getError().getErrorCode(), 
				response.getError().getErrorCode().equals(Utils.QUESTION_DEF_NOT_SUPPORTED_EXCEPTION));
		
	}
	
	
	
	@Test
	@TestDescription(desc="4.27.22 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Photograph question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3822")
	public void test_22() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefPhotoSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.23 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Conditon question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3823")
	public void test_23() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefConditionSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	
	@Test
	@TestDescription(desc="4.27.24 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Numeric question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3824")
	public void test_24() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefNumericSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.25 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a String question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3825")
	public void test_25() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefStringSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.26 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Single Pick question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3826")
	public void test_26() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefSinglePickSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.27 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Single Pick Rule Based question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3827")
	public void test_27() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefSinglePickRBSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, scriptQuestionDefSinglePickRBReturnId, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.28 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Multi Pick question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3828")
	public void test_28() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefMultiPickSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.29 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Multi Pick Rule Based question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3829")
	public void test_29() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefMultiPickRBSciptID);
		headersVariables.put("seqno", scriptQuestionDefMultiPickRBSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, scriptQuestionDefMultiPickRBReturnId, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.30 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Multi Pick Rule Based question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3830")
	public void test_30() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefDecimalSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.31 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Level question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3831")
	public void test_31() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefLevelSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.32 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Date question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3832")
	public void test_32() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefDateSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.33 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Time question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3833")
	public void test_33() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefTimeSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.34 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Yes/No question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3834")
	public void test_34() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefYesNoSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.35 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Instruction question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3835")
	public void test_35() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefInstructionSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.36 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a GPS Location question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3836")
	public void test_36() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefGPSSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	@Test
	@TestDescription(desc="4.27.37 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Task List question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3837")
	public void test_37() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefTaskListSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.38 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a Formatted Input question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3838")
	public void test_38() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefFormattedInputSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	@Test
	@TestDescription(desc="4.27.39 Attempt to get question definition using valid Date and Authorization headers. Attempt to get a script " + 
				"question definition for a CSV question type. Check HTTP status code is 200 OK and the success element is true")
	@TestLabel(label="Reference: 3839")
	public void test_39() 
	{
		String dateHeader = getDateHeader();
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", scriptQuestionDefValidScriptId);
		headersVariables.put("seqno", scriptQuestionDefCSVSeqNo);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);
		
		ScriptQuestionDefinitionResponse response = null;
		
		response = getScriptQuestionDefinition(headersVariables, null, requestHeaders, HttpStatus.UNAUTHORIZED);

		assertTrue("No response was returned.", response != null);
		
		assertTrue("Request was a failure.", response.isSuccess());
		
	}
	
	
	public ScriptQuestionDefinitionResponse getScriptQuestionDefinition(Map<String, String> headersVariables, String returnId,
			HttpHeaders requestHeaders, HttpStatus expectedStatusCode) {
		

		final String wsURL = baseURL + "script/{id}/question/{seqno}";
		
		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();
		
		StringBuffer restCall;
		ScriptQuestionDefinitionResponse response = null;
		
		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try {

			// Build REST URL to call
			restCall = new StringBuffer(wsURL);
			
			if(returnId != null)
				restCall.append("?returnId=" + returnId);

			// Make REST call and retrieve the results
			String requestBody = null;

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.GET,requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();
			
			log.debug("getScriptQuestionDefinition : " + results);
			assertTrue("Result is null", results != null);
			
			
			// Parse XML received from the response into an 
			// getScriptQuestionDefinition object
			try {
				
                XStream xstream = new XStream(new StaxDriver());
                xstream.autodetectAnnotations(true);
                xstream.alias("ScriptQuestionDefinitionResponse", ScriptQuestionDefinitionResponse.class);				
				response = (ScriptQuestionDefinitionResponse) xstream.fromXML(results);

				log.debug("Success : " + response.isSuccess());
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
		
		return response;

	}

}
