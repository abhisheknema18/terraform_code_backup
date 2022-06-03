/**
 * Author:  T Murray
 * Date:    13/01/2014
 * Project: FDE022
 * 
 * Copyright AMT-Sybex 2014
 */
package com.amtsybex.fieldreach.client.rest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory; 
import org.slf4j.Logger;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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

import com.amtsybex.fieldreach.services.messages.response.UploadInitiateResponse;
import com.amtsybex.fieldreach.services.messages.response.UploadPartResponse;
import com.amtsybex.fieldreach.services.resource.exception.ResourceTypeNotFoundException;
import com.amtsybex.fieldreach.services.test.annotations.TestDescription;
import com.amtsybex.fieldreach.services.test.annotations.TestLabel;
import com.amtsybex.fieldreach.services.upload.UploadController;
import com.amtsybex.fieldreach.services.utils.Utils;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.junit.*;

import static org.junit.Assert.*;

public class TestResultsUpload extends CommonBase {

	static Logger log = LoggerFactory.getLogger(TestResultsUpload.class.getName());

	@Autowired
	UploadController fileUploadController = (UploadController)ctx.getBean("fileUploadController");

	private Resource sourceFile = new FileSystemResource(
			StringUtils.cleanPath(uploadScriptsSourceDirectory + "/"
					+ legalUploadFileName));

	private Resource uploadFile = null;

	private long maxChunkSize;
	private String identifier;
	private int nextPart;
	private String initialChecksum;
	private String uploadChecksum;

	private int expectedParts;

	private boolean xml1 = false;
	private boolean xml2 = false;
	private boolean xml3 = false;
	private boolean xml4 = false;
	private boolean xml5 = false;
	private boolean xml6 = false;
	private boolean xml7 = false;
	private boolean xml8 = false;
	private boolean xml9 = false;
	private boolean xml10 = false;
	private boolean xml11 = false;
	private boolean xml12 = false;
	private boolean xml13 = false;
	private boolean xml14 = false;
	private boolean xml15 = false;
	private boolean xml16 = false;
	private boolean xml17 = false;
	private boolean xml18 = false;
	private boolean xml19 = false;
	private boolean xml20 = false;
	private boolean xml21 = false;
	private boolean xml22 = false;
	private boolean xml23 = false;
	private boolean xml24 = false;
	private boolean xml25 = false;
	private boolean xml26 = false;
	private boolean xml27 = false;
	private boolean xml28 = false;
	private boolean xml29 = false;
	private boolean xml30 = false;
	private boolean xml31 = false;
	private boolean xml32 = false;
	private boolean xml50 = false;

	@Test
	@TestDescription(desc = "Initiate an upload request no Date header and valid "
			+ "Authorization header.")
	@TestLabel(label = "Reference: 1401")
	public void test_1() {
		
		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));

		initiateUpload(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request invalid Date header and"
			+ " valid Authorization header")
	@TestLabel(label = "Reference: 1402")
	public void test_2() {

		String dateHeader = "hhfdss";

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request date header not in RFC 2616 "
			+ "format and valid Authorization header.")
	@TestLabel(label = "Reference: 1403")
	public void test_3() {

		String dateHeader = getNonRFC2616Date();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request date header more than 15 mins ahead "
			+ "of server time and valid Authorization header.")
	@TestLabel(label = "Reference: 1404")
	public void test_4() {

		String dateHeader = getDateAfter(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request date header more than "
			+ "15 minutes behind server time and valid Authorization header.")
	@TestLabel(label = "Reference: 1405")
	public void test_5() {

		String dateHeader = getDateBefore(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request valid date header and no "
			+ "Authorization header.")
	@TestLabel(label = "Reference: 1406")
	public void test_6() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request valid date header and "
			+ "malformed Authorization header.")
	@TestLabel(label = "Reference: 1407")
	public void test_7() {

		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-I_AM_MALFORMED"
				+ unrevokedUserCode + ":" + authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request valid date header valid "
			+ "Authorization header using an invalid user in "
			+ "the authorisation header.")
	@TestLabel(label = "Reference: 1408")
	public void test_8() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request with valid date header valid "
			+ "Authorization header signed with incorrect password.")
	@TestLabel(label = "Reference: 1409")
	public void test_9() {

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders,
				HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request with invalid XML in the request body.<br>"
			+ "400 bad request error should be returned.")
	@TestLabel(label = "Reference: 1410")
	public void test_10() {
		
		xml1 = true;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);

		xml1 = false;
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request with valid XML that is not in UploadInitiateRequest format"
			+ " in the request body.<br><br>400 bad request error should be returned.")
	@TestLabel(label = "Reference: 1411")
	public void test_11() {
		
		xml2 = true;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);

		xml2 = false;
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request omitting the fileName element."
			+ "<br><br>400 bad request error should be returned.")
	@TestLabel(label = "Reference: 1412")
	public void test_12() {
		
		xml3 = true;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);

		xml3 = false;
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request with an empty fileName element."
			+ "<br><br>400 bad request error should be returned.")
	@TestLabel(label = "Reference: 1413")
	public void test_13() {
		
		xml4 = true;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);

		xml4 = false;
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request omitting the totalSizeBytes element."
			+ "<br><br>400 bad request error should be returned.")
	@TestLabel(label = "Reference: 1414")
	public void test_14() {
		
		xml5 = true;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);

		xml5 = false;
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request with an empty totalSizeBytes element."
			+ "<br><br>400 bad request error should be returned.")
	@TestLabel(label = "Reference: 1415")
	public void test_15() {
		
		xml6 = true;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		initiateUpload(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);

		xml6 = false;
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request without a MimeType element."
			+ "<br><br>The request should return a 200 status code and be flagged a a success.")
	@TestLabel(label = "Reference: 1416")
	public void test_16() {
		
		UploadInitiateResponse response = null;

		xml7 = true;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		response = initiateUpload(headersVariables, requestHeaders,
				HttpStatus.OK);
		xml7 = false;

		assertTrue("No UploadInitiateResponse returned", response != null);
		assertTrue("Upload initiate.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request with an empty MimeType element."
			+ "<br><br>The request should return a 200 status code and be flagged a a success.")
	@TestLabel(label = "Reference: 1417")
	public void test_17() {
		
		UploadInitiateResponse response = null;

		xml8 = true;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		response = initiateUpload(headersVariables, requestHeaders,
				HttpStatus.OK);
		xml8 = false;

		assertTrue("No UploadInitiateResponse returned", response != null);
		assertTrue("Upload initiate.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request omitting the identifier element."
			+ "<br><br>The request should return a 200 status code and be flagged a a success.")
	@TestLabel(label = "Reference: 1418")
	public void test_18() {
		
		UploadInitiateResponse response = null;

		xml9 = true;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		response = initiateUpload(headersVariables, requestHeaders,
				HttpStatus.OK);
		xml9 = false;

		assertTrue("No UploadInitiateResponse returned", response != null);
		assertTrue("Upload initiate.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request with an empty identifier element."
			+ "<br><br>The request should return a 200 status code and be flagged a a success.")
	@TestLabel(label = "Reference: 1419")
	public void test_19() {
		
		UploadInitiateResponse response = null;

		xml10 = true;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		response = initiateUpload(headersVariables, requestHeaders,
				HttpStatus.OK);
		xml10 = false;

		assertTrue("No UploadInitiateResponse returned", response != null);
		assertTrue("Upload initiate.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request speicfying the total size as 1001 bytes. "
			+ "This is 1 byte over the amount allowed for these tests."
			+ "<br><br>The request should return a 200 status code and be flagged as failure.")
	@TestLabel(label = "Reference: 1420")
	public void test_20() {
		
		UploadInitiateResponse response = null;

		xml11 = true;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		response = initiateUpload(headersVariables, requestHeaders,
				HttpStatus.OK);
		xml11 = false;

		assertTrue("No UploadInitiateResponse returned.", response != null);
		assertTrue("Upload initiate was a success.", !response.isSuccess());

		assertTrue(
				"Unexpected exception returned.<br><br><b>ErrorCode:</b><br>"
						+ response.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ response.getError().getErrorDescription(), response
						.getError().getErrorCode().equals(Utils.MAX_FILESIZE_EXCEEDED));
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request speicfying the total size as <=1000 bytes. "
			+ "The request should return a 200 status code and be flagged as a success."
			+ "<br><br>The message should return a non-blank identifier and the MaxChunkSizeBytes"
			+ "should be 100. StartFromPart should be 1.")
	@TestLabel(label = "Reference: 1421")
	public void test_21() {
		
		UploadInitiateResponse response = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		response = initiateUpload(headersVariables, requestHeaders,
				HttpStatus.OK);

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		assertTrue("Identifier is missing.", response.getIdentifier() != null
				|| !response.getIdentifier().trim().equals(""));

		assertTrue("MaxChunkSizeBytes is not 100.",
				response.getMaxChunkSizeBytes() == 100);

		assertTrue("StartFromPart is not 1.", response.getStartFromPart()
				.intValue() == 1);
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request speicfying the total size as <=1000 bytes. "
			+ "The request should return a 200 status code and be flagged as a success."
			+ "<br><br>The message should return a non-blank identifier and the MaxChunkSizeBytes "
			+ "should be 100. StartFromPart should be 1."
			+ "<br><br>Make the initate call again. Ensure that the StartFromPart is 1 and that the identifier "
			+ "returned is the same as the first call."
			+ "<br><br>Make a final initiate call passing the ID returned this time. Verify that the call"
			+ "succeeds and that the ID returtned is the same as that passed in. StartFromPart should also be 1.")
	@TestLabel(label = "Reference: 1422")
	public void test_22() {
		
		UploadInitiateResponse response = null;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		// Make first initiate call
		response = initiateUpload(headersVariables, requestHeaders,
				HttpStatus.OK);

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		assertTrue("Identifier is missing.", response.getIdentifier() != null
				|| !response.getIdentifier().trim().equals(""));

		assertTrue("MaxChunkSizeBytes is not 100.",
				response.getMaxChunkSizeBytes() == 100);

		assertTrue("StartFromPart is not 1.", response.getStartFromPart()
				.intValue() == 1);

		// Make second initiate call
		response = initiateUpload(headersVariables, requestHeaders,
				HttpStatus.OK);

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		assertTrue("Identifier is missing.", response.getIdentifier() != null
				|| !response.getIdentifier().trim().equals(""));

		assertTrue("StartFromPart is not 1.", response.getStartFromPart()
				.intValue() == 1);

		assertTrue("identifiers do not match.",
				identifier.equals(response.getIdentifier()));

		// Make third initiate call
		response = initiateUpload(headersVariables, requestHeaders,
				HttpStatus.OK);

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		assertTrue("Identifier is missing.", response.getIdentifier() != null
				|| !response.getIdentifier().trim().equals(""));

		assertTrue("StartFromPart is not 1.", response.getStartFromPart()
				.intValue() == 1);

		assertTrue("identifiers do not match.",
				identifier.equals(response.getIdentifier()));
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part with no Date header and "
			+ "valid Authorization header.")
	@TestLabel(label = "Reference: 1423")
	public void test_23() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		// Build headers for web service call

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("x-fws-deviceid", deviceid);
		String dateHeader = getDateHeader();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));

		// Now process the chunks
		uploadChunk(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part with invalid Date header"
			+ " and valid Authorization header")
	@TestLabel(label = "Reference: 1424")
	public void test_24() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		//
		// Build headers for web service call
		String dateHeader = "hhfdss";

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part with date header not in RFC 2616 "
			+ "format and valid Authorization header.")
	@TestLabel(label = "Reference: 1425")
	public void test_25() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		//
		// Build headers for web service call
		String dateHeader = getNonRFC2616Date();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part with date header more than 15 mins"
			+ " ahead of server time and valid Authorization header.")
	@TestLabel(label = "Reference: 1426")
	public void test_26() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		//
		// Build headers for web service call
		String dateHeader = getDateAfter(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part with date header more than 15 "
			+ "minutes behind server time and valid Authorization header.")
	@TestLabel(label = "Reference: 1427")
	public void test_27() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		//
		// Build headers for web service call
		String dateHeader = getDateBefore(1);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part with valid date header "
			+ "and no Authorization header.")
	@TestLabel(label = "Reference: 1428")
	public void test_28() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part with valid date header and "
			+ "malformed Authorization header.")
	@TestLabel(label = "Reference: 1429")
	public void test_29() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();
		String authToken = getAuthToken(unrevokedPassword, dateHeader);

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "FWS-I_AM_MALFORMED"
				+ unrevokedUserCode + ":" + authToken);
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part with valid date header valid "
			+ "Authorization header using an invalid user in "
			+ "the authorisation header.")
	@TestLabel(label = "Reference: 1430")
	public void test_30() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(invalidUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}
	

	@Test
	@TestDescription(desc = "Attempt to upload part with valid date header valid "
			+ "Authorization header signed with incorrect password.")
	@TestLabel(label = "Reference: 1431")
	public void test_31() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,invalidPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.UNAUTHORIZED);
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part using an invalid identifier."
			+ "<br><br>Check that the message returned is marked as unsuccessful "
			+ "and the error code is set to " + Utils.INVALID_ID_EXCEPTION)
	@TestLabel(label = "Reference: 1432")
	public void test_32() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		UploadPartResponse partResponse = null;

		xml12 = true;

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", "invalid");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		partResponse = uploadChunk(headersVariables, requestHeaders,
				HttpStatus.OK);

		xml12 = false;

		assertTrue("No UploadPartResponse returned.", partResponse != null);

		assertTrue("upload of part was successful.", !partResponse.isSuccess());

		assertTrue(
				"Unexpected exception returned.<br><br><b>ErrorCode:</b><br>"
						+ partResponse.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ partResponse.getError().getErrorDescription(),
				partResponse.getError().getErrorCode().equals(Utils.INVALID_ID_EXCEPTION));
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part using an invalid XML message body."
			+ "<br><br>A 400 Bad request http status should be returned.")
	@TestLabel(label = "Reference: 1433")
	public void test_33() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		xml13 = true;

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
		xml13 = false;
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part using with no Identifier element "
			+ "in the request body."
			+ "<br><br>A 400 Bad request http status should be returned.")
	@TestLabel(label = "Reference: 1434")
	public void test_34() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		xml14 = true;

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
		xml14 = false;
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part using with a blank Identifier element "
			+ "in the request body."
			+ "<br><br>A 400 Bad request http status should be returned.")
	@TestLabel(label = "Reference: 1435")
	public void test_35() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		xml15 = true;

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
		xml15 = false;
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part using with no PartNumber element "
			+ "in the request body."
			+ "<br><br>A 400 Bad request http status should be returned.")
	@TestLabel(label = "Reference: 1436")
	public void test_36() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		xml16 = true;

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
		xml16 = false;
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part using with a blank PartNumber element "
			+ "in the request body."
			+ "<br><br>A 400 Bad request http status should be returned.")
	@TestLabel(label = "Reference: 1437")
	public void test_37() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		xml17 = true;

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
		xml17 = false;
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part using with no Checksum element "
			+ "in the request body."
			+ "<br><br>A 400 Bad request http status should be returned.")
	@TestLabel(label = "Reference: 1438")
	public void test_38() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		xml18 = true;

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
		xml18 = false;
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part using with a blank Checksum element "
			+ "in the request body."
			+ "<br><br>A 400 Bad request http status should be returned.")
	@TestLabel(label = "Reference: 1439")
	public void test_39() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		xml19 = true;

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
		xml19 = false;
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part using with no DataLength element "
			+ "in the request body."
			+ "<br><br>A 400 Bad request http status should be returned.")
	@TestLabel(label = "Reference: 1440")
	public void test_40() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		xml20 = true;

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
		xml20 = false;
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part using with an empty DataLength element "
			+ "in the request body."
			+ "<br><br>A 400 Bad request http status should be returned.")
	@TestLabel(label = "Reference: 1441")
	public void test_41() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		xml21 = true;

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
		xml21 = false;
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part using with no Data element "
			+ "in the request body."
			+ "<br><br>A 400 Bad request http status should be returned.")
	@TestLabel(label = "Reference: 1442")
	public void test_42() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		xml22 = true;

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
		xml22 = false;
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload part using with an empty Data element "
			+ "in the request body."
			+ "<br><br>A 400 Bad request http status should be returned.")
	@TestLabel(label = "Reference: 1443")
	public void test_43() {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		xml23 = true;

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
		xml23 = false;
	}
	
	
	@Test
	@TestDescription(desc = "Initiate an upload request for a file with total size < 1000 bytes."
			+ "<br>The upload initiate response should have the success flag set to true. "
			+ "The MaxChunkSizeBytes should be 100 and StartFromPart 1. "
			+ "<br><br>Read the data from the file and encode to Base64. Send and upload part "
			+ "request specifying PartNumber as 1 and set the Data and DataLength elements accordingly."
			+ "<br><br>Response returned should be 200 OK. The Success and IsComplete elements in the message"
			+ "returned should be true. The file should be present in the specified upload directory. "
			+ "Generate a checksum for this file and confirm it matches the orignal checksum.")
	@TestLabel(label = "Reference: 1444")
	public void test_44() throws IOException {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		assertTrue("Identifier is missing.", response.getIdentifier() != null
				|| !response.getIdentifier().trim().equals(""));

		assertTrue("MaxChunkSizeBytes is not 100.",
				response.getMaxChunkSizeBytes() == 100);

		assertTrue("StartFromPart is not 1.", response.getStartFromPart()
				.intValue() == 1);

		//
		// Build headers for web service call

		
		initialChecksum = Common.generateMD5Checksum(getFileContents(sourceFile
				.getFile().getAbsolutePath()).getBytes());

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		UploadPartResponse uploadResponse = null;

		uploadResponse = uploadChunk(headersVariables, requestHeaders,
				HttpStatus.OK);

		assertTrue("No UploadPartResponse returned.", uploadResponse != null);

		assertTrue(
				"Upload of part was not successful.<br><br><b>ErrorCode:</b><br>"
						+ uploadResponse.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ uploadResponse.getError().getErrorDescription(),
				uploadResponse.isSuccess());

		assertTrue(
				"Upload of part was not compelete.<br><br><b>ErrorCode:</b><br>"
						+ uploadResponse.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ uploadResponse.getError().getErrorDescription(),
				uploadResponse.isComplete());

		// FDE034 TM 25/02/2016

		String uploadDir = "";
		
		try {
			
			uploadDir = fileUploadController.getUploadDir(legalUploadFileName);
			
		} catch (ResourceTypeNotFoundException e) {
			
			fail("Upload Direcotyr could not be found for file: " + legalUploadFileName);
		}
				
		uploadFile = new FileSystemResource(StringUtils.cleanPath(uploadDir + "/" + legalUploadFileName));

		// End FDE037
		
		assertTrue("Uploaded file not created correctly.", uploadFile.exists());

		uploadChecksum = Common.generateMD5Checksum(
				getFileContents(uploadFile.getFile().getAbsolutePath()).getBytes());

		assertTrue("Checksums do not match.",
				uploadChecksum.equals(initialChecksum));
	}


	@Test
	@TestDescription(desc = "Initiate an upload request for a file with total size < 1000 bytes."
			+ "<br>The upload initiate response should have the success flag set to true. "
			+ "The MaxChunkSizeBytes should be 100 and StartFromPart 1. "
			+ "<br><br>Read the data from the file and encode to Base64. Set the checksum to be an invalid "
			+ "checksum and send and upload part request specifying PartNumber as 1 and set the Data "
			+ "and DataLength elements accordingly."
			+ "<br><br>Response returned should be 200 OK. The Success and IsComplete elements in the message "
			+ "returned should be false. a "
			+ Utils.CHECKSUM_EXCEPTION
			+ "should be returned.")
	@TestLabel(label = "Reference: 1445")
	public void test_45() throws IOException {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		assertTrue("Identifier is missing.", response.getIdentifier() != null
				|| !response.getIdentifier().trim().equals(""));

		assertTrue("MaxChunkSizeBytes is not 100.",
				response.getMaxChunkSizeBytes() == 100);

		assertTrue("StartFromPart is not 1.", response.getStartFromPart()
				.intValue() == 1);

		//
		// Build headers for web service call

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		UploadPartResponse uploadResponse = null;
		xml24 = true;

		uploadResponse = uploadChunk(headersVariables, requestHeaders,
				HttpStatus.OK);

		xml24 = false;

		assertTrue("No UploadPartResponse returned.", uploadResponse != null);

		assertTrue("Upload of part was successful.",
				!uploadResponse.isSuccess());

		assertTrue("Upload of part was complete.", !uploadResponse.isComplete());

		assertTrue(
				"unexpected exception returned.<br><br><b>ErrorCode:</b><br>"
						+ uploadResponse.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ uploadResponse.getError().getErrorDescription(),
				uploadResponse.getError().getErrorCode()
						.equals(Utils.CHECKSUM_EXCEPTION));
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request for a file with total size < 1000 bytes."
			+ "<br>The upload initiate response should have the success flag set to true. "
			+ "The MaxChunkSizeBytes should be 100 and StartFromPart 1. "
			+ "<br><br>Read the data from the file and encode to Base64. Send an upload part request "
			+ "specifying PartNumber as 2 and set the Data and DataLength elements accordingly."
			+ "<br><br>Response returned should be 200 OK. The Success and Complete elements in the message "
			+ "returned should be false. The nextPart element should be one and a "
			+ Utils.PART_NO_SEQ_EXCEPTION + "should be returned.")
	@TestLabel(label = "Reference: 1446")
	public void test_46() throws IOException {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		assertTrue("Identifier is missing.", response.getIdentifier() != null
				|| !response.getIdentifier().trim().equals(""));

		assertTrue("MaxChunkSizeBytes is not 100.",
				response.getMaxChunkSizeBytes() == 100);

		assertTrue("StartFromPart is not 1.", response.getStartFromPart()
				.intValue() == 1);

		//
		// Build headers for web service call

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		UploadPartResponse uploadResponse = null;
		xml25 = true;

		uploadResponse = uploadChunk(headersVariables, requestHeaders,
				HttpStatus.OK);

		xml25 = false;

		assertTrue("No UploadPartResponse returned.", uploadResponse != null);

		assertTrue("Upload of part was successful.",
				!uploadResponse.isSuccess());

		assertTrue("Upload of part was complete.", !uploadResponse.isComplete());

		assertTrue("NextPart is not 1 as expected.", uploadResponse
				.getNextPart().intValue() == 1);

		assertTrue(
				"Unexpected exception returned.<br><br><b>ErrorCode:</b><br>"
						+ uploadResponse.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ uploadResponse.getError().getErrorDescription(),
				uploadResponse.getError().getErrorCode()
						.equals(Utils.PART_NO_SEQ_EXCEPTION));
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request for a file with total size < 1000 bytes."
			+ "<br>The upload initiate response should have the success flag set to true. "
			+ "The MaxChunkSizeBytes should be 100 and StartFromPart 1. "
			+ "<br><br>Read the data from the file and encode to Base64. "
			+ "Send an upload part request specifying PartNumber as 1, set the "
			+ "data element accordingly and specify DataLength as 101 bytes."
			+ "<br><br>Response returned should be 200 OK. The Success and "
			+ "IsComplete elements in the message returned should be false. "
			+ "The nextPart element should be 1 and a "
			+ Utils.MAX_CHUNKSIZE_EXCEEDED
			+ "should be returned.")
	@TestLabel(label = "Reference: 1447")
	public void test_47() throws IOException {
		
		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		assertTrue("Identifier is missing.", response.getIdentifier() != null
				|| !response.getIdentifier().trim().equals(""));

		assertTrue("MaxChunkSizeBytes is not 100.",
				response.getMaxChunkSizeBytes() == 100);

		assertTrue("StartFromPart is not 1.", response.getStartFromPart()
				.intValue() == 1);

		//
		// Build headers for web service call

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		UploadPartResponse uploadResponse = null;
		xml26 = true;

		uploadResponse = uploadChunk(headersVariables, requestHeaders,
				HttpStatus.OK);

		xml26 = false;

		assertTrue("No UploadPartResponse returned.", uploadResponse != null);

		assertTrue("Upload of part was successful.",
				!uploadResponse.isSuccess());

		assertTrue("Upload of part was complete.", !uploadResponse.isComplete());

		assertTrue("NextPart is not 1 as expected.", uploadResponse
				.getNextPart().intValue() == 1);

		assertTrue(
				"Unexpected exception returned.<br><br><b>ErrorCode:</b><br>"
						+ uploadResponse.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ uploadResponse.getError().getErrorDescription(),
				uploadResponse.getError().getErrorCode()
						.equals(Utils.MAX_CHUNKSIZE_EXCEEDED));
	}

	
	@Test
	@TestDescription(desc = "Initiate an upload request for a file with actual total size > 500 bytes. "
			+ "Specify the total size as being < 1000 bytes for the initiate upload call."
			+ "<br><br>The upload initiate response should have the success flag set to true. "
			+ "The MaxChunkSizeBytes should be 100 and StartFromPart 1. "
			+ "A checksum should be generated for the whole file prior to upload."
			+ "<br><br>Start the upload of the file in chunks of 100 bytes, "
			+ "and encoding the data in Base64. Valid checksums should be generated and "
			+ "each upload part number correctly."
			+ "<br><br>Response returned should be 200 OK. The Success element should be true for "
			+ "each upload part request, and the Complete elemen should be false for all uploads "
			+ "except the last one."
			+ "<br><br>The file should be present in the specified upload directory. "
			+ "Generate a checksum for this file and confirm it matches the orignal checksum.")
	@TestLabel(label = "Reference: 1448")
	public void test_48() throws IOException {
		
		xml27 = true;
		initialChecksum = Common.generateMD5Checksum(
				getFileContents(sourceFile.getFile().getAbsolutePath()).getBytes());

		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
				+ response.getError().getErrorCode() + "<br><b>Detail:</b><br>"
				+ response.getError().getErrorDescription(),
				response.isSuccess());

		assertTrue("Identifier is missing.", response.getIdentifier() != null
				|| !response.getIdentifier().trim().equals(""));

		assertTrue("MaxChunkSizeBytes is not 100.",
				response.getMaxChunkSizeBytes() == 100);

		assertTrue("StartFromPart is not 1.", response.getStartFromPart()
				.intValue() == 1);

		//
		// Build headers for web service call

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		UploadPartResponse uploadResponse = null;

		uploadResponse = uploadChunk(headersVariables, requestHeaders,
				HttpStatus.OK);

		xml27 = false;

		assertTrue("No UploadPartResponse returned.", uploadResponse != null);

		assertTrue("Upload of part was not successful.",
				uploadResponse.isSuccess());

		assertTrue("Upload of part was not complete.",
				uploadResponse.isComplete());

		assertTrue(
				"Unexpected exception returned.<br><br><b>ErrorCode:</b><br>"
						+ uploadResponse.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ uploadResponse.getError().getErrorDescription(),
				uploadResponse.getError().getErrorCode() == null);

		// FDE034 TM 25/02/2016

		String uploadDir = "";
		
		try {
			
			uploadDir = fileUploadController.getUploadDir(legalUploadFileName);
			
		} catch (ResourceTypeNotFoundException e) {
			
			fail("Upload directory could not be found for file: " + legalUploadFileName);
		}
				
		uploadFile = new FileSystemResource(StringUtils.cleanPath(uploadDir + "/" + legalUploadFileName));

		// End FDE037
		
		assertTrue("Uploaded file not created correctly.", uploadFile.exists());

		uploadChecksum = Common.generateMD5Checksum(
				getFileContents(uploadFile.getFile().getAbsolutePath()).getBytes());

		assertTrue("Checksums do not match.",
				uploadChecksum.equals(initialChecksum));
	}

	@Test
	@TestDescription(desc = "Initiate an upload request for a file with actual total size > 500 bytes. "
			+ "Specify the total size as being < 1000 bytes for the initiate upload call."
			+ "<br><br>The upload initiate response should have the success flag set to true. "
			+ "The MaxChunkSizeBytes should be 100 and StartFromPart 1. "
			+ "A checksum should be generated for the whole file prior to upload."
			+ "<br><br>Start the upload of the file in chunks of 100 bytes, "
			+ "and encoding the data in Base64. Valid checksums should be generated and "
			+ "each upload part number correctly. Upload all chunks except the last one. "
			+ "<br><br>Response returned should be 200 OK. The Success element should be true for "
			+ "each upload part request, and the Complete element should be false. "
			+ "<br><br>Send a valid initiate request and verify the response is flagged a success. "
			+ "The StartFromPart should also be the last part."
			+ "<br><br>Upload the last part, verify success is true and that the complete "
			+ "flag is also true."
			+ "<br><br>The file should be present in the specified upload directory. "
			+ "Generate a checksum for this file and confirm it matches the orignal checksum.")
	@TestLabel(label = "Reference: 1449")
	public void test_49() throws IOException {
		
		xml28 = true;
		initialChecksum = Common.generateMD5Checksum(
				getFileContents(sourceFile.getFile().getAbsolutePath()).getBytes());

		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("1st Initiate: No UploadInitiateResponse returned.",
				response != null);

		assertTrue(
				"1st Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
						+ response.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ response.getError().getErrorDescription(),
				response.isSuccess());

		assertTrue("1st Initiate: Identifier is missing.",
				response.getIdentifier() != null
						|| !response.getIdentifier().trim().equals(""));

		assertTrue("1st Initiate: MaxChunkSizeBytes is not 100.",
				response.getMaxChunkSizeBytes() == 100);

		assertTrue("1st Initiate: StartFromPart is not 1.", response
				.getStartFromPart().intValue() == 1);

		//
		// Build headers for web service call

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		UploadPartResponse uploadResponse = null;

		uploadResponse = uploadChunk(headersVariables, requestHeaders,
				HttpStatus.OK);

		xml28 = false;

		assertTrue("No UploadPartResponse returned.", uploadResponse != null);

		assertTrue("Upload of part was not successful.",
				uploadResponse.isSuccess());

		assertTrue("Upload of part was completed.",
				!uploadResponse.isComplete());

		assertTrue(
				"Unexpected exception returned.<br><br><b>ErrorCode:</b><br>"
						+ uploadResponse.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ uploadResponse.getError().getErrorDescription(),
				uploadResponse.getError().getErrorCode() == null);

		// Make a second initiate call

		xml29 = true;
		response = performInitiate();

		assertTrue("2nd Initiate: No UploadInitiateResponse returned.",
				response != null);

		assertTrue(
				"2nd Initiation was not a success.<br><br><b>ErrorCode:</b><br>"
						+ response.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ response.getError().getErrorDescription(),
				response.isSuccess());

		assertTrue("2nd Initiate: Identifier is missing.",
				response.getIdentifier() != null
						|| !response.getIdentifier().trim().equals(""));

		assertTrue("2nd Initiate: StartFromPart is not the last part.",
				response.getStartFromPart().intValue() == expectedParts);

		uploadResponse = uploadChunk(headersVariables, requestHeaders,
				HttpStatus.OK);
		xml29 = false;

		assertTrue("File upload (resumption) was not a success.",
				uploadResponse.isSuccess());

		assertTrue("File upload (resumption) was not a completed.",
				uploadResponse.isComplete());

		// FDE034 TM 25/02/2016

		String uploadDir = "";
		
		try {
			
			uploadDir = fileUploadController.getUploadDir(legalUploadFileName);
			
		} catch (ResourceTypeNotFoundException e) {
			
			fail("Upload directory could not be found for file: " + legalUploadFileName);
		}
				
		uploadFile = new FileSystemResource(StringUtils.cleanPath(uploadDir + "/" + legalUploadFileName));

		// End FDE037

		assertTrue("Uploaded file not created correctly.", uploadFile.exists());

		uploadChecksum = Common.generateMD5Checksum(
				getFileContents(uploadFile.getFile().getAbsolutePath()).getBytes());

		assertTrue("Checksums do not match.",
				uploadChecksum.equals(initialChecksum));
	}
	
	@Test
	@TestDescription(desc = "Initiate request specifying total size < 1000 bytes for file of same size. "
			+ "Call should return with status code 200 Ok. The UploadInitiateResponse should have success "
			+ "element set to true. The message should return a non blank identifier. "
			+ "The maxchunkSizeBytes should be set to 100. The startFromPart should be set to 1. "
			+ "Read data from file, calculate checksum, and encode in base64. Send upload part request with "
			+ "checksum, partNumber set to 1 and data set to encoded data and datalen set correctly. "
			+ "Check response code is 200 Ok. Check success element to true, check isComplete is false. "
			+ "Check nextPart is set to 2. Now to test restarting, send a new InitiateUploadRequest with "
			+ "identifier set to blank using original request details. "
			+ "Check result code is 200 OK, success=true and iscomplete=false and next part is set to 2.")
	@TestLabel(label = "Reference: 1450")
	public void test_50() throws IOException {
		
		log.debug(">>> test_50");
		
		xml10 = true;
		initialChecksum = Common.generateMD5Checksum(
				getFileContents(sourceFile.getFile().getAbsolutePath()).getBytes());

		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("1st Initiate: No UploadInitiateResponse returned.",
				response != null);

		assertTrue(
				"1st Initiate: The UploadInitiateResponse was not a success.<br><br><b>ErrorCode:</b><br>"
						+ response.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ response.getError().getErrorDescription(),
				response.isSuccess());

		assertTrue(
				"1st Initiate: The UploadInitiateResponse identifier returned was blank.",
				!response.getIdentifier().trim().equals("") || response != null);

		assertTrue(
				"1st Initiate: The MaxChunkSizeBytes is not 100.<br><br><b>MaxChunkSizeBytes:</b><br>"
						+ response.getMaxChunkSizeBytes(),
				response.getMaxChunkSizeBytes() == 100);

		assertTrue(
				"1st Initiate: The StartFromPart is not 1.<br><br><b>StartFromPart:</b><br>"
						+ response.getStartFromPart().intValue(), response
						.getStartFromPart().intValue() == 1);

		//
		// Build headers for web service call
		xml10 = false;
		xml30 = true;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		UploadPartResponse uploadResponse = null;

		uploadResponse = uploadChunk(headersVariables, requestHeaders,
				HttpStatus.OK);

		assertTrue("The part upload was not a success.",
				uploadResponse.isSuccess());

		assertTrue("The upload was completed.", !uploadResponse.isComplete());

		assertTrue(
				"The next part is not 2 as expected.<br><br><b>NextPart:</b><br>"
						+ uploadResponse.getNextPart().intValue(),
				uploadResponse.getNextPart().intValue() == 2);

		// Now attempt to initiate the upload again

		
		xml10 = false;
		initialChecksum = Common.generateMD5Checksum(
				getFileContents(sourceFile.getFile().getAbsolutePath()).getBytes());

		// initiate an upload Again
		response = performInitiate();

		assertTrue("2nd Initiate: No UploadInitiateResponse returned.",
				response != null);

		assertTrue(
				"2nd Initiate: The UploadInitiateResponse was not a success.<br><br><b>ErrorCode:</b><br>"
						+ response.getError().getErrorCode()
						+ "<br><b>Detail:</b><br>"
						+ response.getError().getErrorDescription(),
				response.isSuccess());

		assertTrue(
				"2nd Initiate: The UploadInitiateResponse identifier returned was blank.",
				!response.getIdentifier().trim().equals("") || response != null);

		assertTrue(
				"2nd Initiate: The StartFromPart is not 2.<br><br><b>StartFromPart:</b><br>"
						+ response.getStartFromPart().intValue(), response
						.getStartFromPart().intValue() == 2);

		//
		// For completeness we want to finish the upload
		// so that we don't have to reset file system to run test again
		xml10 = false;

		xml50 = true;
		uploadResponse = uploadChunk(headersVariables, requestHeaders,
				HttpStatus.OK);
		xml50 = false;
		
		assertTrue("Upload not successful", uploadResponse.isSuccess());
		assertTrue("Upload not complete", uploadResponse.isComplete());
		
		log.debug("<<< test_50");
	}


	@Test
	@TestDescription(desc = "Attempt to upload initiate request part file using an empty Body request.<br><br>"
			+ "Check response code is 400 Bad Request.")
	@TestLabel(label = "Reference: 1451")
	public void test_51() throws IOException {
		
		xml31 = true;

		performInitiate();
	}

	
	@Test
	@TestDescription(desc = "Attempt to upload initiate request part file using a valid UploadInitiateRequest. "
			+ "Check response code is 200 OK. Check success value is true.<br><br>"
			+ "Initiate a upload part request with the identifier and an empty body request.<br> "
			+ "Check response code is 400 Bad Request.")
	@TestLabel(label = "Reference: 1452")
	public void test_52() throws IOException {
		
		xml10 = true;
		initialChecksum = Common.generateMD5Checksum(
				getFileContents(sourceFile.getFile().getAbsolutePath()).getBytes());

		// First initiate an upload
		UploadInitiateResponse response = performInitiate();

		assertTrue("No UploadInitiateResponse returned.", response != null);

		assertTrue("UploadInitiateResponse was not a success.",
				response.isSuccess());

		xml10 = false;
		xml32 = true;

		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");
		headersVariables.put("identifier", identifier);

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		uploadChunk(headersVariables, requestHeaders, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * Method to initiate a file upload.
	 */
	private UploadInitiateResponse initiateUpload(
			Map<String, String> headersVariables, HttpHeaders requestHeaders,
			HttpStatus expectedStatusCode) {
		
		final String wsURL = baseURL + "script/{id}/result/multipart";

		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		UploadInitiateResponse requestResponse = null;
		String requestBody = "";

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try {
			restCall = new StringBuffer(wsURL);

			//
			// Create an initiate call
			if (xml1) {
				requestBody = "sgsdggw";
			} else if (xml2) {
				requestBody = "<valid>Valid XML but invalid UploadInitiate</valid>";
			} else if (xml3) {
				requestBody = "<UploadInitiate>" + "<totalSizeBytes>"
						+ sourceFile.getFile().length() + "</totalSizeBytes>"
						+ "<mimeType>text/xml</mimeType>"
						+ "<identifier></identifier>" + "</UploadInitiate>";
			} else if (xml4) {
				requestBody = "<UploadInitiate>" + "<totalSizeBytes>"
						+ sourceFile.getFile().length() + "</totalSizeBytes>"
						+ "<fileName></fileName>"
						+ "<mimeType>text/xml</mimeType>"
						+ "<identifier></identifier>" + "</UploadInitiate>";
			} else if (xml5) {
				requestBody = "<UploadInitiate>" + "<fileName>"
						+ legalUploadFileName + "</fileName>"
						+ "<mimeType>text/xml</mimeType>"
						+ "<identifier></identifier>" + "</UploadInitiate>";
			} else if (xml6) {
				requestBody = "<UploadInitiate>" + "<fileName>"
						+ legalUploadFileName + "</fileName>"
						+ "<totalSizeBytes></totalSizeBytes>"
						+ "<mimeType>text/xml</mimeType>"
						+ "<identifier></identifier>" + "</UploadInitiate>";
			} else if (xml7) {
				assertTrue("Upload source file does not exist",
						sourceFile.exists());

				requestBody = "<UploadInitiate>" + "<fileName>"
						+ legalUploadFileName + "</fileName>"
						+ "<totalSizeBytes>" + sourceFile.getFile().length()
						+ "</totalSizeBytes>" + "<identifier></identifier>"
						+ "</UploadInitiate>";
			} else if (xml8) {
				assertTrue("Upload source file does not exist",
						sourceFile.exists());

				requestBody = "<UploadInitiate>" + "<fileName>"
						+ legalUploadFileName + "</fileName>"
						+ "<totalSizeBytes>" + sourceFile.getFile().length()
						+ "</totalSizeBytes>" + "<mimeType></mimeType>"
						+ "<identifier></identifier>" + "</UploadInitiate>";
			} else if (xml9) {
				assertTrue("Upload source file does not exist",
						sourceFile.exists());

				requestBody = "<UploadInitiate>" + "<fileName>"
						+ legalUploadFileName + "</fileName>"
						+ "<totalSizeBytes>" + sourceFile.getFile().length()
						+ "</totalSizeBytes>" + "<mimeType>text/xml</mimeType>"
						+ "</UploadInitiate>";
			} else if (xml10) {
				assertTrue("Upload source file does not exist",
						sourceFile.exists());

				requestBody = "<UploadInitiate>" + "<fileName>"
						+ legalUploadFileName + "</fileName>"
						+ "<totalSizeBytes>" + sourceFile.getFile().length()
						+ "</totalSizeBytes>" + "<mimeType>text/xml</mimeType>"
						+ "<identifier></identifier>" + "</UploadInitiate>";
			} else if (xml11) {
				assertTrue("Upload source file does not exist",
						sourceFile.exists());

				requestBody = "<UploadInitiate>" + "<fileName>"
						+ legalUploadFileName + "</fileName>"
						+ "<totalSizeBytes>1001</totalSizeBytes>"
						+ "<mimeType>text/xml</mimeType>"
						+ "<identifier></identifier>" + "</UploadInitiate>";
			} else if (xml31) {
				requestBody = "";
			} else {
				assertTrue("Upload source file does not exist",
						sourceFile.exists());

				requestBody = "<UploadInitiate>" + "<fileName>"
						+ legalUploadFileName + "</fileName>"
						+ "<totalSizeBytes>" + sourceFile.getFile().length()
						+ "</totalSizeBytes>" + "<mimeType>text/xml</mimeType>"
						+ "<identifier>" + identifier + "</identifier>"
						+ "</UploadInitiate>";
			}

			log.debug("Request body :" + requestBody);

			HttpEntity<String> requestEntity = new HttpEntity<String>(
					requestBody, requestHeaders);

			ResponseEntity<String> resultsExchange = restTemplate.exchange(
					restCall.toString(), HttpMethod.POST, requestEntity,
					String.class, headersVariables);

			String results = resultsExchange.getBody();

			log.debug("upload Initiate results : " + results);
			assertTrue("Result is null", results != null);

			if (xml28) {
				expectedParts = (int) sourceFile.getFile().length() / 100;

				if ((int) sourceFile.getFile().length() % 100 != 0)
					expectedParts++;
			}

			try {
				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("UploadInitiateResponse",
						UploadInitiateResponse.class);
				requestResponse = (UploadInitiateResponse) xstream
						.fromXML(results);

				log.debug("Success : " + requestResponse.isSuccess());

				if (requestResponse.isSuccess()) {
					log.debug("Initiate results :"
							+ requestResponse.getIdentifier());

					log.debug("Max chunk size :"
							+ requestResponse.getMaxChunkSizeBytes()
							+ "Start From Part: "
							+ requestResponse.getStartFromPart() + "identifier"
							+ requestResponse.getIdentifier());

					identifier = requestResponse.getIdentifier();
					nextPart = requestResponse.getStartFromPart().intValue();
					maxChunkSize = requestResponse.getMaxChunkSizeBytes();
				}

			} catch (XmlMappingException e) {
				log.error("Client Mapping Exception :" + e.getMessage());
				fail("Client Mapping Exception :" + e.getMessage());
			}
		} catch (IOException ioe) {
			log.error("IO Exception :" + ioe.getMessage());
			fail("IO Exception :" + ioe.getMessage());

		} catch (HttpClientErrorException he) {
			//
			// Check to see if the HHTP status code is what we expect
			if (he.getStatusCode() == expectedStatusCode) {
				log.debug(expectedStatusCode
						+ " status code returned as expected.");
			} else {
				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
		}

		return requestResponse;
	}

	public UploadPartResponse uploadChunk(Map<String, String> headersVariables,
			HttpHeaders requestHeaders, HttpStatus expectedStatusCode) {
		
		RestOperations restTemplate = null;
		restTemplate = new RestTemplate();

		StringBuffer restCall;
		UploadPartResponse partrequestResponse = null;

		requestHeaders.set("x-fws-applicationidentifier", applicationIdentifier);
		
		try {
			//
			// Upload the chunks in turn
			String wsPartURL = baseURL
					+ "script/{id}/result/multipart/{identifier}";
			restCall = new StringBuffer(wsPartURL);

			//
			// Split the data into chunks that are the maximum chunk size
			List<String> chunks = this
					.getFileChunks(uploadScriptsSourceDirectory + "/"
							+ legalUploadFileName);

			for (String chunk : chunks) {

				String requestBody = "";

				// get the last chunk for xml29 flag
				if (xml29) {
					chunk = chunks.get(chunks.size() - 1);

					requestBody = "<UploadPart>"
							+ "<identifier>"
							+ identifier
							+ "</identifier>"
							+ "<partNumber>"
							+ new BigInteger(Integer.toString(nextPart))
							+ "</partNumber>"
							+ "<checksum>"
							+ Common.generateMD5Checksum(chunk.getBytes())
							+ "</checksum>"
							+ "<dataLength>"
							+ chunk.getBytes().length
							+ "</dataLength>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				} else if (xml12) {
					requestBody = "<UploadPart>"
							+ "<identifier>invalid</identifier>"
							+ "<partNumber>"
							+ new BigInteger(Integer.toString(nextPart))
							+ "</partNumber>"
							+ "<checksum>"
							+ Common.generateMD5Checksum(chunk.getBytes())
							+ "</checksum>"
							+ "<dataLength>"
							+ chunk.getBytes().length
							+ "</dataLength>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				} else if (xml13) {
					requestBody = "invalid xml";
				} else if (xml14) {
					requestBody = "<UploadPart>"
							+ "<partNumber>"
							+ new BigInteger(Integer.toString(nextPart))
							+ "</partNumber>"
							+ "<checksum>"
							+ Common.generateMD5Checksum(chunk.getBytes())
							+ "</checksum>"
							+ "<dataLength>"
							+ chunk.getBytes().length
							+ "</dataLength>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				} else if (xml15) {
					requestBody = "<UploadPart>"
							+ "<identifier></identifier>"
							+ "<partNumber>"
							+ new BigInteger(Integer.toString(nextPart))
							+ "</partNumber>"
							+ "<checksum>"
							+ Common.generateMD5Checksum(chunk.getBytes())
							+ "</checksum>"
							+ "<dataLength>"
							+ chunk.getBytes().length
							+ "</dataLength>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				} else if (xml16) {
					requestBody = "<UploadPart>"
							+ "<identifier></identifier>"
							+ "<checksum>"
							+ Common.generateMD5Checksum(chunk.getBytes())
							+ "</checksum>"
							+ "<dataLength>"
							+ chunk.getBytes().length
							+ "</dataLength>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				} else if (xml17) {
					requestBody = "<UploadPart>"
							+ "<identifier></identifier>"
							+ "<partNumber></partNumber>"
							+ "<checksum>"
							+ Common.generateMD5Checksum(chunk.getBytes())
							+ "</checksum>"
							+ "<dataLength>"
							+ chunk.getBytes().length
							+ "</dataLength>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				} else if (xml18) {
					requestBody = "<UploadPart>"
							+ "<identifier>"
							+ identifier
							+ "</identifier>"
							+ "<partNumber>"
							+ new BigInteger(Integer.toString(nextPart))
							+ "</partNumber>"
							+ "<dataLength>"
							+ chunk.getBytes().length
							+ "</dataLength>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				} else if (xml19) {
					requestBody = "<UploadPart>"
							+ "<identifier>"
							+ identifier
							+ "</identifier>"
							+ "<partNumber>"
							+ new BigInteger(Integer.toString(nextPart))
							+ "</partNumber>"
							+ "<checksum></checksum>"
							+ "<dataLength>"
							+ chunk.getBytes().length
							+ "</dataLength>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				} else if (xml20) {
					requestBody = "<UploadPart>"
							+ "<identifier>"
							+ identifier
							+ "</identifier>"
							+ "<partNumber>"
							+ new BigInteger(Integer.toString(nextPart))
							+ "</partNumber>"
							+ "<checksum>"
							+ Common.generateMD5Checksum(chunk.getBytes())
							+ "</checksum>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				} else if (xml21) {
					requestBody = "<UploadPart>"
							+ "<identifier>"
							+ identifier
							+ "</identifier>"
							+ "<partNumber>"
							+ new BigInteger(Integer.toString(nextPart))
							+ "</partNumber>"
							+ "<checksum>"
							+ Common.generateMD5Checksum(chunk.getBytes())
							+ "</checksum>"
							+ "<dataLength></dataLength>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				} else if (xml22) {
					requestBody = "<UploadPart>" + "<identifier>" + identifier
							+ "</identifier>" + "<partNumber>"
							+ new BigInteger(Integer.toString(nextPart))
							+ "</partNumber>" + "<checksum>"
							+ Common.generateMD5Checksum(chunk.getBytes()) + "</checksum>"
							+ "<dataLength>" + chunk.getBytes().length
							+ "</dataLength>" + "</UploadPart>";
				} else if (xml23) {
					requestBody = "<UploadPart>" + "<identifier>" + identifier
							+ "</identifier>" + "<partNumber>"
							+ new BigInteger(Integer.toString(nextPart))
							+ "</partNumber>" + "<checksum>"
							+ Common.generateMD5Checksum(chunk.getBytes()) + "</checksum>"
							+ "<dataLength>" + chunk.getBytes().length
							+ "</dataLength>" + "<data></data>"
							+ "</UploadPart>";
				} else if (xml24) {
					requestBody = "<UploadPart>"
							+ "<identifier>"
							+ identifier
							+ "</identifier>"
							+ "<partNumber>"
							+ new BigInteger(Integer.toString(nextPart))
							+ "</partNumber>"
							+ "<checksum>invalidchecksum</checksum>"
							+ "<dataLength>"
							+ chunk.getBytes().length
							+ "</dataLength>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				} else if (xml25) {
					requestBody = "<UploadPart>"
							+ "<identifier>"
							+ identifier
							+ "</identifier>"
							+ "<partNumber>2</partNumber>"
							+ "<checksum>"
							+ Common.generateMD5Checksum(chunk.getBytes())
							+ "</checksum>"
							+ "<dataLength>"
							+ chunk.getBytes().length
							+ "</dataLength>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				} else if (xml26) {
					requestBody = "<UploadPart>"
							+ "<identifier>"
							+ identifier
							+ "</identifier>"
							+ "<partNumber>"
							+ new BigInteger(Integer.toString(nextPart))
							+ "</partNumber>"
							+ "<checksum>"
							+ Common.generateMD5Checksum(chunk.getBytes())
							+ "</checksum>"
							+ "<dataLength>101</dataLength>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				} else if (xml32) {
					requestBody = "";
				} else {
					requestBody = "<UploadPart>"
							+ "<identifier>"
							+ identifier
							+ "</identifier>"
							+ "<partNumber>"
							+ new BigInteger(Integer.toString(nextPart))
							+ "</partNumber>"
							+ "<checksum>"
							+ Common.generateMD5Checksum(chunk.getBytes())
							+ "</checksum>"
							+ "<dataLength>"
							+ chunk.getBytes().length
							+ "</dataLength>"
							+ "<data>"
							+ Common.encodeBase64(chunk
									.getBytes(Common.UTF8_ENCODING)) + "</data>"
							+ "</UploadPart>";
				}

				log.debug("Part Request body :" + requestBody);

				HttpEntity<String> requestEntity = new HttpEntity<String>(
						requestBody, requestHeaders);

				ResponseEntity<String> resultsExchange = restTemplate.exchange(
						restCall.toString(), HttpMethod.PUT, requestEntity,
						String.class, headersVariables);

				String results = resultsExchange.getBody();

				log.debug("Upload Part results :" + results);

				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("UploadPartResponse", UploadPartResponse.class);
				partrequestResponse = (UploadPartResponse) xstream
						.fromXML(results);

				log.debug("Part uploaded Success : "
						+ partrequestResponse.isSuccess());

				// If upload part failed exit the loop and dont try to upload
				// more parts.
				if (!partrequestResponse.isSuccess())
				{
					log.debug("break ..");
					break;
				}

				// get the next upload part if the upload is not complete
				if (!partrequestResponse.isComplete())
				{
					nextPart = partrequestResponse.getNextPart().intValue();
					log.debug("next part :" + nextPart);
				}

				if (xml27) {
					if (partrequestResponse.getNextPart() != null) {
						assertTrue("Success = true and Complete = false "
								+ "should only occur for the last part.",
								partrequestResponse.isSuccess()
										&& !partrequestResponse.isComplete());
					}
				}

				// Return the response before uploading the last chunk
				if (xml28) {
					if (partrequestResponse.getNextPart().intValue() == expectedParts)
						return partrequestResponse;
				}

				// Processing for the last chunk only for xml29
				if (xml29)
					return partrequestResponse;

				
				if (xml50 && partrequestResponse.isComplete())
				{
					break;
				}
				
				// Return the response after uploading the first chunk
				if (xml30 && !xml50) {
					if (partrequestResponse.getNextPart().intValue() == 2)
						return partrequestResponse;
				}
				
			}

			log.debug("File Completed : " + partrequestResponse.isComplete());

		} catch (XmlMappingException e) {
			log.error("Client Mapping Exception :" + e.getMessage());
			fail("Client Mapping Exception :" + e.getMessage());
		} catch (IOException ioe) {
			log.error("IO Exception :" + ioe.getMessage());
			fail("IO Exception :" + ioe.getMessage());

		} catch (HttpClientErrorException he) {
			//
			// Check to see if the HHTP status code is what we expect
			if (he.getStatusCode() == expectedStatusCode) {
				log.debug(expectedStatusCode
						+ " status code returned as expected.");
			} else {
				log.error("Client Exception :" + he.getMessage());
				fail("Client Exception :" + he.getMessage());
			}
		}

		return partrequestResponse;

	}

	private List<String> getFileChunks(String fileName) {
		
		List<String> chunks = new ArrayList<String>();

		BufferedInputStream bufferedInput = null;
		byte[] buffer = new byte[(int) maxChunkSize];

		try {
			bufferedInput = new BufferedInputStream(new FileInputStream(
					fileName));

			int bytesRead = 0;

			// Keep reading from the file while there is any content
			// when the end of the stream has been reached, -1 is returned
			while ((bytesRead = bufferedInput.read(buffer)) != -1) {
				// Process the chunk of bytes read
				// in this case we just construct a String and print it out
				String chunk = new String(buffer, 0, bytesRead);
				log.debug("chunk = '" + chunk + "'");

				// Add to array list of chunks
				chunks.add(chunk);
			}
		} 
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			// Close the BufferedInputStream
			try {
				if (bufferedInput != null)
					bufferedInput.close();
			} 
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return chunks;
	}

	
	private String getFileContents(String file) {
		
		BufferedReader reader = null;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			reader = new BufferedReader(new FileReader(file));

			String line = null;

			String ls = System.getProperty("line.separator");

			try {
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
					stringBuilder.append(ls);
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}

		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		return stringBuilder.toString();
	}

	
	private UploadInitiateResponse performInitiate() {
		
		UploadInitiateResponse resp = null;

		//
		// Build headers for web service call
		String dateHeader = getDateHeader();

		Map<String, String> headersVariables = new HashMap<String, String>();
		headersVariables.put("id", "82");

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set(
				"Authorization",
				getAuthorisationHeaderValue(unrevokedUserCode,unrevokedPassword,
						dateHeader));
		requestHeaders.set("Date", dateHeader);
		requestHeaders.set("x-fws-deviceid", deviceid);

		//
		// Make first initiate call
		if (xml31)
			resp = initiateUpload(headersVariables, requestHeaders,
					HttpStatus.BAD_REQUEST);
		else
			resp = initiateUpload(headersVariables, requestHeaders,
					HttpStatus.OK);

		return resp;
	}
	
}
