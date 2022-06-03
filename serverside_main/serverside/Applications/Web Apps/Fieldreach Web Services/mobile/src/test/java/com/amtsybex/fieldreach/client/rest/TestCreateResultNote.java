/**
 * Author:  T Murray
 * Date:    19/05/2016
 * Project: FDP1218
 * 
 * Copyright AMT-Sybex 2016
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

import com.amtsybex.fieldreach.services.messages.response.CallResponse;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.junit.*;
import static org.junit.Assert.*;

public class TestCreateResultNote extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestCreateResultNote.class.getName());

	@Test
	@TestDescription(desc = "Attempt to create a result note. Specify a return is that exists in the returnscripts table, but do"
			+ "not supply a request body. Check HTTP status code 400 Bad Request is returned.")
	@TestLabel(label = "Reference: 5101")
	public void test_1() {

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(createNoteReturnId));
		
		createResultNote(headersVariables, "", null, HttpStatus.BAD_REQUEST);
	}
	
	@Test
	@TestDescription(desc = "Attempt to create a result note. Specify a return is that exists in the returnscripts table. Supply"
			+ "an invalid a request body. Check HTTP status code 400 Bad Request is returned.")
	@TestLabel(label = "Reference: 5102")
	public void test_2() {

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(createNoteReturnId));
		
		createResultNote(headersVariables, "INVALID REQUEST BODY", null, HttpStatus.BAD_REQUEST);
	}
	
	@Test
	@TestDescription(desc = "Attempt to create a result note. Specify a return is that exists in the returnscripts table. Supply"
			+ "a requesty body with a SequenceNumber but no ResOrderNo element. Check HTTP status code 400 Bad Request is returned.")
	@TestLabel(label = "Reference: 5103")
	public void test_3() {

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(createNoteReturnId));
		
		StringBuilder requestBodyBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		requestBodyBuilder.append("<ScriptResultNote>");
		requestBodyBuilder.append("<noteText>Test Note</noteText>");
		requestBodyBuilder.append("<sequenceNumber>1</sequenceNumber>");
		requestBodyBuilder.append("</ScriptResultNote>");
		
		createResultNote(headersVariables, requestBodyBuilder.toString(), null, HttpStatus.BAD_REQUEST);
	}

	@Test
	@TestDescription(desc = "Attempt to create a result note. Specify a return is that exists in the returnscripts table. Supply"
			+ "a requesty body with a ResOrderNo but no SequenceNumber element. Check HTTP status code 400 Bad Request is returned.")
	@TestLabel(label = "Reference: 5104")
	public void test_4() {

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(createNoteReturnId));
		
		StringBuilder requestBodyBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		requestBodyBuilder.append("<ScriptResultNote>");
		requestBodyBuilder.append("<noteText>Test Note</noteText>");
		requestBodyBuilder.append("<resOrderNo>1</resOrderNo>");
		requestBodyBuilder.append("</ScriptResultNote>");
		
		createResultNote(headersVariables, requestBodyBuilder.toString(), null, HttpStatus.BAD_REQUEST);
	}
	
	@Test
	@TestDescription(desc = "Attempt to create a result note. Specify a return is that exists in the returnscripts table. Supply"
			+ "a requesty body with a ResOrderNo and SequenceNumber element, ensuring the SequenceNumber is 0 and"
			+ "the ResOrderNo is > 0. Check HTTP status code 400 Bad Request is returned.")
	@TestLabel(label = "Reference: 5105")
	public void test_5() {

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(createNoteReturnId));
		
		StringBuilder requestBodyBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		requestBodyBuilder.append("<ScriptResultNote>");
		requestBodyBuilder.append("<noteText>Test Note</noteText>");
		requestBodyBuilder.append("<sequenceNumber>0</sequenceNumber>");
		requestBodyBuilder.append("<resOrderNo>1</resOrderNo>");
		requestBodyBuilder.append("</ScriptResultNote>");
		
		createResultNote(headersVariables, requestBodyBuilder.toString(), null, HttpStatus.BAD_REQUEST);
	}
	
	@Test
	@TestDescription(desc = "Attempt to create a result note. Specify a return is that exists in the returnscripts table. Supply"
			+ "a requesty body with a ResOrderNo and SequenceNumber element, ensuring the ResOrderNo is 0 and"
			+ "the sequenceNumber is > 0. Check HTTP status code 400 Bad Request is returned.")
	@TestLabel(label = "Reference: 5106")
	public void test_6() {

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(createNoteReturnId));
		
		StringBuilder requestBodyBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		requestBodyBuilder.append("<ScriptResultNote>");
		requestBodyBuilder.append("<noteText>Test Note</noteText>");
		requestBodyBuilder.append("<sequenceNumber>1</sequenceNumber>");
		requestBodyBuilder.append("<resOrderNo>0</resOrderNo>");
		requestBodyBuilder.append("</ScriptResultNote>");
		
		createResultNote(headersVariables, requestBodyBuilder.toString(), null, HttpStatus.BAD_REQUEST);
	}
	
	@Test
	@TestDescription(desc = "Attempt to create a result note. Specify a return is that exists in the returnscripts table. Supply"
			+ "a requesty body with no noteText element. Check HTTP status code 400 Bad Request is returned.")
	@TestLabel(label = "Reference: 5107")
	public void test_7() {

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(createNoteReturnId));
		
		StringBuilder requestBodyBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		requestBodyBuilder.append("<ScriptResultNote>");
		requestBodyBuilder.append("<sequenceNumber>1</sequenceNumber>");
		requestBodyBuilder.append("<resOrderNo>1</resOrderNo>");
		requestBodyBuilder.append("</ScriptResultNote>");
		
		createResultNote(headersVariables, requestBodyBuilder.toString(), null, HttpStatus.BAD_REQUEST);
	}
	
	@Test
	@TestDescription(desc = "Attempt to create a result note specifying a returnid that does not exist "
			+ "in the returnscripts table.  Check HTTP status code 200 OK. Response success element should "
			+ "be false and a " + Utils.SCRIPT_RESULT_NOTFOUND_EXCEPTION + " returned")
	@TestLabel(label = "Reference: 5108")
	public void test_8() {

		CallResponse resp;
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "99999999");
		
		StringBuilder requestBodyBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		requestBodyBuilder.append("<ScriptResultNote>");
		requestBodyBuilder.append("<noteText>Test Note</noteText>");
		requestBodyBuilder.append("<sequenceNumber>1</sequenceNumber>");
		requestBodyBuilder.append("<resOrderNo>1</resOrderNo>");
		requestBodyBuilder.append("</ScriptResultNote>");
		
		resp = createResultNote(headersVariables, requestBodyBuilder.toString(), null, HttpStatus.OK);
		
		assertTrue("Request was a success.", !resp.isSuccess());
		
		assertTrue("No exception returned:" + resp.getError().getErrorCode(), 
				resp.getError().getErrorCode() != null);
		
		assertTrue("Unexpected exception returned. Expected:" + Utils.SCRIPT_RESULT_NOTFOUND_EXCEPTION +
				"<br>Received:" + resp.getError().getErrorCode(), 
				resp.getError().getErrorCode().equals(Utils.SCRIPT_RESULT_NOTFOUND_EXCEPTION));
	}
	
	@Test
	@TestDescription(desc = "Attempt to create a result note specifying a returnid that exists "
			+ "in the returnscripts table. Ensure the sequenceNumber element references a sequence number that does not"
			+ "exist in the script associated with the script result (99999999). Check HTTP status code 200 OK. "
			+ "Response success element should be false and a " + Utils.SCRIPT_RESULT_NOTFOUND_EXCEPTION + " returned")
	@TestLabel(label = "Reference: 5109")
	public void test_9() {

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(createNoteReturnId));
		
		StringBuilder requestBodyBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		requestBodyBuilder.append("<ScriptResultNote>");
		requestBodyBuilder.append("<noteText>Test Note</noteText>");
		requestBodyBuilder.append("<sequenceNumber>99999999</sequenceNumber>");
		requestBodyBuilder.append("<resOrderNo>1</resOrderNo>");
		requestBodyBuilder.append("</ScriptResultNote>");
		
		createResultNote(headersVariables, requestBodyBuilder.toString(), null, HttpStatus.BAD_REQUEST);
	}
	
	@Test
	@TestDescription(desc = "Attempt to create a result note specifying a returnid that exists "
			+ "in the returnscripts table. Ensure the a sequencenumber and resorderno > 0 are is supplied. "
			+ "The optional userCOde paramater should also be supplied (TestUser). Check HTTP status code 200 OK. "
			+ "Response success element should be true. Manually inspect the database to ensure it has been updated correctly.")
	@TestLabel(label = "Reference: 5110")
	public void test_10() {

		CallResponse resp;
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(createNoteReturnId));
		
		StringBuilder requestBodyBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		requestBodyBuilder.append("<ScriptResultNote>");
		requestBodyBuilder.append("<noteText>Test Note</noteText>");
		requestBodyBuilder.append("<sequenceNumber>" + createNoteSeqNo + "</sequenceNumber>");
		requestBodyBuilder.append("<resOrderNo>" + createNoteResOrderNo + "</resOrderNo>");
		requestBodyBuilder.append("</ScriptResultNote>");
		
		resp = createResultNote(headersVariables, requestBodyBuilder.toString(), "TestUser", HttpStatus.OK);
		
		assertTrue("Request failure.", resp.isSuccess());
				
		assertTrue("Unexpected exception returned:" + resp.getError().getErrorCode(), resp.getError().getErrorCode() == null);
	}
	
	@Test
	@TestDescription(desc = "Attempt to create a result note specifying a returnid that exists "
			+ "in the returnscripts table. Ensure the sequencenumber and resorderno are not supplied. Check HTTP status code 200 OK. "
			+ "Response success element should be true. Manually inspect the database to ensure it has been updated correctly.")
	@TestLabel(label = "Reference: 5111")
	public void test_11() {

		CallResponse resp;
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", Integer.toString(createNoteReturnId));
		
		StringBuilder requestBodyBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		requestBodyBuilder.append("<ScriptResultNote>");
		requestBodyBuilder.append("<noteText>Test Note</noteText>");
		requestBodyBuilder.append("</ScriptResultNote>");
		
		resp = createResultNote(headersVariables, requestBodyBuilder.toString(), null, HttpStatus.OK);
		
		assertTrue("Request failure.", resp.isSuccess());
				
		assertTrue("Unexpected exception returned:" + resp.getError().getErrorCode(), resp.getError().getErrorCode() == null);
	}
	
	public CallResponse createResultNote(Map<String, String> headersVariables, String requestBody, String userCode,
			HttpStatus expectedStatusCode) {

		String wsURL = baseURL + "/script/result/{id}/note";

		if (userCode != null)
			wsURL = wsURL + "?userCode=" + userCode;
		
		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		CallResponse requestResponse = null;

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		String dateHeader = getDateHeader();
		log.debug("Date header=" + dateHeader);
		requestHeaders.set("Date", dateHeader);
		
		requestHeaders.set("Authorization", getIWSAuthorisationHeaderValue(super.workissuedUserCode, super.workissuedUserPassword, dateHeader));
		
		try {

			// Build REST URL to call
			restCall = new StringBuffer(wsURL);

			HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(restCall.toString(), HttpMethod.POST,
					requestEntity, String.class, headersVariables);

			String results = resultsExchange.getBody();

			assertTrue("Result is null", results != null);

			// Parse XML received from the response into an
			// CallResponse object
			try {

				XStream xstream = new XStream(new StaxDriver());
				xstream.autodetectAnnotations(true);
				xstream.alias("CallResponse", CallResponse.class);
				requestResponse = (CallResponse) xstream.fromXML(results);

				log.debug("Success : " + requestResponse.isSuccess());
			} catch (XmlMappingException e) {

				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
			}
		} catch (HttpClientErrorException he) {

			// Check to see if the HHTP status code is what we expect
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
